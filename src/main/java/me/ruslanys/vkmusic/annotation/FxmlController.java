package me.ruslanys.vkmusic.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
public @interface FxmlController {

    String view();

}
