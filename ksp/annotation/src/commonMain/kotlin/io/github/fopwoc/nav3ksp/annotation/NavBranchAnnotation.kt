package io.github.fopwoc.nav3ksp.annotation

import kotlin.reflect.KClass

@Repeatable
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class Branch(val tree: KClass<out Annotation>)

@Target(AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class Tree(val subTree: Array<KClass<out Annotation>> = [])

