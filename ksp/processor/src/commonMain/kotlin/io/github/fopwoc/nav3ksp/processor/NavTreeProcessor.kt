package io.github.fopwoc.nav3ksp.processor

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.google.devtools.ksp.closestClassDeclaration
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSValueParameter
import com.google.devtools.ksp.validate
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.asTypeName
import com.squareup.kotlinpoet.joinToCode
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.writeTo
import io.github.fopwoc.nav3ksp.NavTree
import io.github.fopwoc.nav3ksp.NavTreeBuilder
import io.github.fopwoc.nav3ksp.NavTreeLayout
import io.github.fopwoc.nav3ksp.annotation.Branch
import io.github.fopwoc.nav3ksp.annotation.Tree
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.PolymorphicModuleBuilder
import java.util.Locale

class NavTreeProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
) : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val treesSymbols = resolver.getSymbolsWithAnnotation(Tree::class.qualifiedName!!)
            .filterIsInstance<KSClassDeclaration>()
            .toSet()

        val branchesSymbols = resolver.getSymbolsWithAnnotation(Branch::class.qualifiedName!!)
            .filterIsInstance<KSFunctionDeclaration>()
            .toSet()

        val branchTrees = branchesSymbols.filter { it.validate() }.associateWith { branch ->
            require(branch.annotations.toList().isNotEmpty()) {
                logger.error("Branch $branch has no any annotation. How???", branch)
            }

            require(branch.annotations.any { it.shortName.getShortName() == Composable::class.simpleName }) {
                logger.error("Branch $branch have to be @Composable function", branch)
            }

            val branchAnnotation = branch.annotations.filter { annotation ->
                annotation.annotationType.resolve().toClassName() == Branch::class.asClassName()
            }

            branchAnnotation.map { annotation ->
                require(annotation.arguments.size == 1) {
                    logger.error(
                        "annotation $branchAnnotation of branch $branch have to be with single argument.",
                        branch
                    )
                    logger.error("Actually how did you even end up like this???", branch)
                }

                val treeType = annotation.arguments.first().value as KSType

                require(
                    treeType.declaration.annotations.map { it.shortName.getShortName() }.toList()
                        .any { it == Tree::class.java.simpleName }
                ) {
                    logger.error("Tree \"$treeType\" in branch annotation of $branch have to implement @Tree", branch)
                }

                treeType
            }
        }


        val treesWithBranches = treesSymbols.filter { it.validate() }.associateWith { tree ->
            branchTrees.filter { (_, annotations) ->
                tree.toClassName() in annotations.map { it.toClassName() }
            }.keys.toSet()
        }

        logger.info("treesWithBranches $treesWithBranches")

        if (treesWithBranches.isEmpty()) {
            return emptyList()
        }

        treesWithBranches.forEach { (tree, branches) ->
            require(branches.isNotEmpty()) {
                logger.error("No branches found for tree $tree", tree)
            }

            require(tree.annotations.toList().size == 1) {
                logger.error("tree $tree have to be with single annotation", tree)
            }

            val subTree = (tree.annotations.first().arguments.firstOrNull()?.value as? ArrayList<*> ?: ArrayList<Any>())
                .filterIsInstance<KSType>().toSet()

            require(tree.asStarProjectedType() !in subTree) {
                logger.error("tree $tree has dependency on itself", tree)
            }

            require(tree.toClassName().simpleName.all { it.isLetter() }) {
                logger.error("tree $tree contains some wierd chars", tree)
            }

            logger.info("processing tree $tree (subtree $subTree) with branches $branches")

            generateNavTree(resolver, codeGenerator, tree, branches)
            generateNavTreeLayout(codeGenerator, tree, subTree, branches)
            generateNavTreeBuilder(resolver, codeGenerator, tree, subTree, branches)
        }

        return (treesSymbols + branchesSymbols).filterNot { it.validate() }
    }

    companion object {

        const val OBJECT_TREE_POSTFIX = "NavTree"
        const val OBJECT_TREE_LAYOUT_POSTFIX = "NavTreeLayout"
        const val OBJECT_TREE_BUILDER_POSTFIX = "NavTreeBuilder"

        const val BRANCH_VIEW_SUFFIX = "View"
        const val BRANCH_TREE_SUFFIX = "Tree"

        fun generateNavTree(
            resolver: Resolver,
            codeGenerator: CodeGenerator,
            tree: KSClassDeclaration,
            branches: Set<KSFunctionDeclaration>
        ) {
            val className = tree.asStarProjectedType().toTreeClassName(OBJECT_TREE_POSTFIX)

            val typedBranchesBuilder = branches.map { branch ->

                val branchName = branch.simpleName.getShortName().removeSuffix(BRANCH_VIEW_SUFFIX)
                    .replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                    }

                val branchParameters = branch.parameters.filterNot { it.implementsViewModel(resolver) }

                if (branchParameters.isNotEmpty()) {
                    val properties = branchParameters.map {
                        PropertySpec.builder(it.name!!.getShortName(), it.type.toTypeName())
                            .initializer(it.name!!.getShortName())
                            .build()
                    }

                    val parameters = branchParameters.map {
                        ParameterSpec.builder(it.name!!.getShortName(), it.type.toTypeName())
                            .build()
                    }

                    val constructor = FunSpec.constructorBuilder().addParameters(parameters).build()

                    TypeSpec
                        .classBuilder(branchName)
                        .primaryConstructor(constructor)
                        .addProperties(properties)
                } else {
                    TypeSpec.objectBuilder(branchName)
                }
                    .addModifiers(KModifier.DATA)
                    .addAnnotation(Serializable::class.asTypeName())
                    .addSuperinterface(NavKey::class.asTypeName())
                    .build()
            }

            FileSpec
                .builder(className)
                .addType(
                    TypeSpec
                        .objectBuilder(className)
                        .addSuperinterface(NavTree::class.asTypeName())
                        .addTypes(typedBranchesBuilder)
                        .build()
                )
                .build()
                .writeTo(codeGenerator, false, listOfNotNull(tree.containingFile) + branches.mapNotNull { it.containingFile })
        }

        fun generateNavTreeLayout(
            codeGenerator: CodeGenerator,
            tree: KSClassDeclaration,
            subTree: Set<KSType>,
            branches: Set<KSFunctionDeclaration>
        ) {
            val className = tree.asStarProjectedType().toTreeClassName(OBJECT_TREE_LAYOUT_POSTFIX)

            val branchesPolymorphicSubclasses = branches.map { branch ->
                val branchName = branch.simpleName.getShortName().removeSuffix(BRANCH_VIEW_SUFFIX)

                CodeBlock.builder()
                    .add(
                        "subclass(serializer = %T.serializer())\n",
                        tree.asStarProjectedType().toTreeClassName(OBJECT_TREE_POSTFIX).nestedClass(branchName)
                    )
                    .build()
            }

            val function = FunSpec
                .builder("polymorphicSerializationSubClasses")
                .receiver(
                    PolymorphicModuleBuilder::class.asTypeName().parameterizedBy(
                        NavKey::class.asTypeName()
                    )
                )
                .addModifiers(KModifier.OVERRIDE)
                .also { builder ->
                    branchesPolymorphicSubclasses.forEach {
                        builder.addCode(it)
                    }
                }
                .build()

            FileSpec
                .builder(className)
                .addType(
                    TypeSpec
                        .objectBuilder(className)
                        .superclass(NavTreeLayout::class.asTypeName())
                        .also { builder ->
                            if (subTree.isNotEmpty()) {
                                val constructor = CodeBlock.builder()
                                    .indent()
                                    .add("\nbranches = arrayOf(\n")
                                    .indent()
                                    .also { constructorBuilder ->
                                        subTree.forEach { sub ->
                                            constructorBuilder
                                                .add("%T", sub.toTreeClassName(OBJECT_TREE_LAYOUT_POSTFIX))
                                                .add(",")
                                                .add("\n")
                                        }
                                    }
                                    .unindent()
                                    .add(")\n")
                                    .unindent()
                                    .build()
                                builder.addSuperclassConstructorParameter(constructor)
                            }
                        }
                        .addFunction(function)
                        .build()
                )
                .addAliasedImport(ClassName("kotlinx.serialization.modules", "subclass"), "subclass")
                .build()
                .writeTo(codeGenerator, false, listOfNotNull(tree.containingFile) + branches.mapNotNull { it.containingFile } + subTree.mapNotNull { it.declaration.containingFile })
        }

        fun generateNavTreeBuilder(
            resolver: Resolver,
            codeGenerator: CodeGenerator,
            tree: KSClassDeclaration,
            subTree: Set<KSType>,
            branches: Set<KSFunctionDeclaration>
        ) {
            val className = tree.asStarProjectedType().toTreeClassName(OBJECT_TREE_BUILDER_POSTFIX)
            val classNameTree = tree.asStarProjectedType().toTreeClassName(OBJECT_TREE_POSTFIX)

            val branchesEntry = branches.map { branch ->
                val branchName = branch.simpleName.getShortName().removeSuffix(BRANCH_VIEW_SUFFIX)
                val viewName = branch.simpleName.getShortName()

                val params = branch.parameters
                    .filterNot { it.implementsViewModel(resolver) }
                    .map { param ->
                        val p = param.name!!.getShortName()
                        CodeBlock.of("%L = it.%L", p, p)
                    }
                    .joinToCode(separator = ", ")

                CodeBlock.builder()
                    .add("entry<%T> {\n", classNameTree.nestedClass(branchName))
                    .indent()
                    .addStatement(
                        format = "%M(%L)",
                        MemberName(
                            branch.packageName.asString(),
                            viewName
                        ),
                        params
                    )
                    .unindent()
                    .add("}\n")
                    .build()
            }

            val function = FunSpec
                .builder("branchesEntry")
                .receiver(
                    EntryProviderScope::class.asTypeName().parameterizedBy(
                        NavKey::class.asTypeName()
                    )
                )
                .addModifiers(KModifier.OVERRIDE)
                .also { builder ->
                    branchesEntry.forEach {
                        builder.addCode(it)
                    }
                }
                .build()

            FileSpec
                .builder(className)
                .addType(
                    TypeSpec
                        .objectBuilder(className)
                        .superclass(NavTreeBuilder::class.asTypeName())
                        .also { builder ->
                            if (subTree.isNotEmpty()) {
                                val constructor = CodeBlock.builder()
                                    .indent()
                                    .add("\nbranches = arrayOf(\n")
                                    .indent()
                                    .also { constructorBuilder ->
                                        subTree.forEach { sub ->
                                            constructorBuilder
                                                .add("%T", sub.toTreeClassName(OBJECT_TREE_BUILDER_POSTFIX))
                                                .add(",")
                                                .add("\n")
                                        }
                                    }
                                    .unindent()
                                    .add(")\n")
                                    .unindent()
                                    .build()
                                builder.addSuperclassConstructorParameter(constructor)
                            }
                        }
                        .addFunction(function)
                        .build()
                )
                .also { builder ->
                    branches.forEach { branch ->
                        val branchClassName = ClassName(
                            branch.qualifiedName!!.getQualifier(),
                            branch.simpleName.getShortName()
                        )

                        builder.addAliasedImport(branchClassName, branch.simpleName.getShortName())
                    }
                }
                .build()
                .writeTo(codeGenerator, false, listOfNotNull(tree.containingFile) + branches.mapNotNull { it.containingFile } + subTree.mapNotNull { it.declaration.containingFile })
        }

        fun KSValueParameter.implementsViewModel(resolver: Resolver): Boolean {
            val viewModelDecl = resolver.getClassDeclarationByName(
                resolver.getKSNameFromString(ViewModel::class.qualifiedName!!)
            )!!

            val candidate: KSDeclaration = this.type.resolve().declaration

            return candidate.closestClassDeclaration()?.implementsViewModel(viewModelDecl) ?: false
        }

        fun KSClassDeclaration.implementsViewModel(viewModelDecl: KSClassDeclaration): Boolean {
            return this.closestClassDeclaration()!!.superTypes
                .map { it.resolve().declaration }
                .filterIsInstance<KSClassDeclaration>()
                .any { superDecl ->
                    superDecl == viewModelDecl || superDecl.implementsViewModel(viewModelDecl)
                }
        }

        fun KSType.toTreeClassName(postfix: String): ClassName {
            return ClassName(
                packageName = this.toTreePackageName(),
                this.toClassName().simpleName.removeSuffix(BRANCH_TREE_SUFFIX)
                    .replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                    } + postfix,
            )
        }

        fun KSType.toTreePackageName(): String {
            return this.toClassName().packageName + "." + this.toClassName().simpleName.replaceFirstChar {
                it.lowercase(
                    Locale.getDefault()
                )
            }
        }
    }
}
