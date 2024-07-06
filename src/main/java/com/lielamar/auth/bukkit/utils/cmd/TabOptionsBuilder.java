package com.lielamar.auth.bukkit.utils.cmd;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

public class TabOptionsBuilder {

    private final List<Callable<List<String>>> argsF = new ArrayList<>();


    public TabOptionsBuilder range(int min, int max, int jump) {
        argsF.add(() -> {
            List<String> r = new ArrayList<>();

            for(int i = min; i < max; i += jump)
                r.add(i + "");

            return r;
        });

        return this;
    }

    public TabOptionsBuilder players() {
        argsF.add(() -> Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).collect(Collectors.toList()));
        return this;
    }

    public TabOptionsBuilder playerAnd(String... option) {
        argsF.add(() -> {
            List<String> r = Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).collect(Collectors.toList());
            r.addAll(Arrays.asList(option));

            return r;
        });

        return this;
    }

    public TabOptionsBuilder list(String... option) {
        argsF.add(() -> Arrays.asList(option));
        return this;
    }

    public List<String> build(String[] args) {
        String lastArg = "";

        int id = args.length;

        if(id > 0) {
            id -= 1;
            lastArg = args[id];
        }

        if(id < argsF.size()) {
            try {
                String finalLastArg = lastArg;
                return argsF.get(id).call().stream().filter(s -> s.toLowerCase().startsWith(finalLastArg.toLowerCase())).collect(Collectors.toList());
            } catch(Exception e) {
                e.printStackTrace();
            }
        }

        return Collections.emptyList();
    }
}