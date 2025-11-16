package io.github.fopwoc.nav3ksp

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.savedstate.serialization.SavedStateConfiguration
import kotlinx.serialization.modules.PolymorphicModuleBuilder
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

/**
 * [NavTreeLayout] - builds module of serialization binding of [NavKey] classes for backStack internal needs
 */
abstract class NavTreeLayout(private val branches: Array<NavTreeLayout> = emptyArray()) {
    internal val stateConfiguration = SavedStateConfiguration {
        serializersModule = SerializersModule {
            polymorphic(baseClass = NavKey::class) {
                for (branch in branches) {
                    with(branch) {
                        polymorphicSerializationSubClasses()
                    }
                }

                polymorphicSerializationSubClasses()
            }
        }
    }

    abstract fun PolymorphicModuleBuilder<NavKey>.polymorphicSerializationSubClasses()

    @Composable
    fun rememberTreeBackStack(vararg initialBranches: NavKey) = rememberNavBackStack(
        configuration = stateConfiguration,
        *initialBranches
    )
}