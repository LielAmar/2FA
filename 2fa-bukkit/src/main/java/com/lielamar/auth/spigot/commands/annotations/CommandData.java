package com.lielamar.auth.spigot.commands.annotations;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CommandData {
    String name();

    String[] aliases();
}
