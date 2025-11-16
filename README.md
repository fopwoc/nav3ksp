# Nav3ksp

---

b

Multiplatform library for codegen of typed navigation in Navigation 3.

Library is designed to keep the same level of control that Navigation 3 gives to developer.
But it solves the problem of having to write very complex code that will link your Composable View, data classes for typed navigation, and Entry<T> to combine all of the above into NavDisplay.

It introduces several concepts:  
**Branch** - Basically NavKey, a Composable component representation with all its arguments. These are used to build the BackStack for Typed navigation.  
**Tree** - A collection of related Branches and other Trees that it includes. In conjunction with NavDisplay, it provides access to all related Branches and subTrees via the BackStack.  

Actually, this lib can be used without KSP if you really like writing boilerplate by hand.

Somewhat inspired by awesome android library [compose-destinations](https://github.com/raamcosta/compose-destinations)

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

For more examples you can see 'example' module with multiplatform app that shows few usages of this library.

