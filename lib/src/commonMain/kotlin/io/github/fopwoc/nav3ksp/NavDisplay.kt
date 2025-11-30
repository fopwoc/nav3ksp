package io.github.fopwoc.nav3ksp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.scene.SceneStrategy
import androidx.navigation3.scene.SinglePaneSceneStrategy
import androidx.navigation3.ui.NavDisplay

/**
 * [NavDisplay] proxy function that does bare minimum to show something useful.
 * @property navTreeBuilder - [NavTreeBuilder] of very first Tree with all branches and subTrees you want to be able
 * to navigate in context of this NavDisplay.
 * @property backStackLocalComposition - composition provider that is containing BackStack related to your Tree.
 * By design, this is CompositionLocal to provide 'context-right' backstack getter in every component you need.
 * @property sceneStrategy - scene strategy provider.
 */
@Composable
fun NavDisplay(
    modifier: Modifier = Modifier,
    sceneStrategy: SceneStrategy<NavKey> = SinglePaneSceneStrategy(),
    navTreeBuilder: NavTreeBuilder,
    backStackLocalComposition: ProvidableCompositionLocal<NavBackStack<NavKey>>,
) {
    NavDisplay(
        modifier = modifier,
        sceneStrategy = sceneStrategy,
        backStack = backStackLocalComposition.current,
        entryProvider = entryProvider {
            with(navTreeBuilder) {
                buildTree()
            }
        }
    )
}
