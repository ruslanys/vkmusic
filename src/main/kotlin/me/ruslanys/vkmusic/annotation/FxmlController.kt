package me.ruslanys.vkmusic.annotation

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
@MustBeDocumented
annotation class FxmlController(val view: String)
