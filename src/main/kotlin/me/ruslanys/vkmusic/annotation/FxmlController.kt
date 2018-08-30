package me.ruslanys.vkmusic.annotation

import org.springframework.stereotype.Controller

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
@MustBeDocumented
@Controller
annotation class FxmlController(val view: String)
