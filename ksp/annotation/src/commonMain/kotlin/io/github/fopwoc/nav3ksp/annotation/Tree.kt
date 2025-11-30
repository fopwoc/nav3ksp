package io.github.fopwoc.nav3ksp.annotation

import kotlin.reflect.KClass

@Target(AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class Tree(val subTree: Array<KClass<out Annotation>> = [])
