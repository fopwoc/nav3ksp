package io.github.fopwoc.nav3ksp

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey

/**
 * [NavTreeBuilder] builds typed entries for "entryProvider" in [NavDisplay] for current tree and related subtrees.
 * @param [branchesEntry] - function where all relations from [NavKey] to [Composable] views have to linked via [entry]\
 */
abstract class NavTreeBuilder(private val branches: Array<NavTreeBuilder> = emptyArray()) {
    abstract fun EntryProviderScope<NavKey>.branchesEntry()

    fun EntryProviderScope<NavKey>.buildTree() {
        for (branch in branches) {
            with(branch) {
                branchesEntry()
            }
        }

        branchesEntry()
    }
}