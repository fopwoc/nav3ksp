# Nav3ksp

[![Build](https://github.com/fopwoc/nav3ksp/actions/workflows/build.yml/badge.svg)](https://github.com/fopwoc/nav3ksp/actions/workflows/build.yml)

Multiplatform codegen library for typed navigation in Navigation 3.

Library is designed to keep the same level of control that Navigation 3 gives to developer.
It solves the problem of writing a lot of boring and repetitive code that will link your Composable Views, typed navigation data classes, and entries to combine all of the above into NavDisplay.

It introduces several concepts:
* **Branch** - Basically NavKey, a Composable component representation with all its arguments. These are used to build the BackStack for Typed navigation.
* **Tree** - A collection of related Branches and other Trees that it includes. In conjunction with NavDisplay, it provides access to all related Branches and subTrees via the BackStack.

By the way, this library can be used without KSP if you really like writing boilerplate by hand.

Somewhat inspired by awesome android library [compose-destinations](https://github.com/raamcosta/compose-destinations)

---

## Showcase

Android | iOS
:-: | :-:
<video src="https://github.com/user-attachments/assets/0bf67a03-5b98-4cfa-86a3-cb16b2f94873"/> | <video src="https://github.com/user-attachments/assets/343ed10b-4be5-4f0b-bedb-2b46ca5a06ae"/>

---

## Examples

First, we need to declare an annotation that will represent our Tree

```kotlin
@Tree
annotation class RootTree
```

Then, for each View that will be part of this Tree, we must specify the Branch annotation and indicate the connection to the Tree

```kotlin
@Branch(RootTree::class)
@Composable
fun ExampleView() { 
    // ...
}
```

Finally, call NavDisplay, provide it with the generated NavTreeLayout and NavTreeBuilder of your Tree, and manually specify the first screen in BackStack. Or not just one, it's vararg.

```kotlin
val LocalBackStack = compositionLocalOf<NavBackStack<NavKey>> {
    error("No LocalBackStack provided")
}

@Composable
fun App() {
    CompositionLocalProvider(
        LocalBackStack provides RootNavTreeLayout.rememberTreeBackStack(RootNavTree.Example)
    ) {
        NavDisplay(
            navTreeBuilder = RootNavTreeBuilder,
            backStackLocalComposition = LocalBackStack
        )
    } 
}
```

That's it! 

---

For more examples, you can see 'example' module with multiplatform app that shows few usages of this library.

1) [Simple navigation](example/composeApp/src/commonMain/kotlin/io/github/fopwoc/nav3ksp/example/01-simple.kt) - Just plain navigation
2) [Back Handled](example/composeApp/src/commonMain/kotlin/io/github/fopwoc/nav3ksp/example/02-backhandled.kt) - Like simple navigation, but back gesture is handled in view
3) [ViewModel](example/composeApp/src/commonMain/kotlin/io/github/fopwoc/nav3ksp/example/03-viewmodel.kt) - Example how this lib handles view models
4) [Arguments](example/composeApp/src/commonMain/kotlin/io/github/fopwoc/nav3ksp/example/04-arguments.kt) - Navigation with typed arguments 
5) [Nested](example/composeApp/src/commonMain/kotlin/io/github/fopwoc/nav3ksp/example/05-nested.kt) - Nested navigation with bottom bar and 3 views. Also example of handing backstack from another scope.
6) [Result](example/composeApp/src/commonMain/kotlin/io/github/fopwoc/nav3ksp/example/06-result.kt) - Navigation to form with result handling by another View
7) [Manual](example/composeApp/src/commonMain/kotlin/io/github/fopwoc/nav3ksp/example/07-manual.kt) - Boilerplate by hand
