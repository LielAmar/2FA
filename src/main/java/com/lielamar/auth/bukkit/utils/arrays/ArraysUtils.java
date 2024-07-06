package com.lielamar.auth.bukkit.utils.arrays;

import org.jetbrains.annotations.NotNull;

public class ArraysUtils {

    public static String[] removeFirstElement(@NotNull String[] arguments) {
        if(arguments.length <= 1)
            return new String[0];

        String[] placeholder = new String[arguments.length - 1];
        for(int i = 0; i < placeholder.length; i++)
            placeholder[i] = arguments[i+1];

        return placeholder;
    }
}