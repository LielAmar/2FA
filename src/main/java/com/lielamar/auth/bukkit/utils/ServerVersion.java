package com.lielamar.auth.bukkit.utils;

import org.bukkit.Bukkit;

public class ServerVersion {

    private static final ServerVersion instance = new ServerVersion();
    public static ServerVersion getInstance() { return instance; }

    private ServerVersion() {
        try { this.serverVersion = Version.valueOf(Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3]); }
        catch (Exception e) { e.printStackTrace(); }
    }


    private Version serverVersion;

    public String getVersion() {
        return this.serverVersion.getNMSName();
    }

    public String getStrippedVersion() {
        return this.serverVersion.getStrippedName();
    }

    public boolean above(Version version) {
        return serverVersion.getId() >= version.getId();
    }


    public enum Version {
        v1_8_R1("v1_8_R1", 10801),
        v1_8_R2("v1_8_R2", 10802),
        v1_8_R3("v1_8_R3", 10803),
        v1_9_R1("v1_9_R1", 10901),
        v1_9_R2("v1_9_R2", 10902),
        v1_10_R1("v1_10_R1", 11001),
        v1_11_R1("v1_11_R1", 11101),
        v1_12_R1("v1_12_R1", 11201),
        v1_13_R1("v1_13_R1", 11301),
        v1_13_R2("v1_13_R2", 11302),
        v1_14_R1("v1_14_R1", 11401),
        v1_15_R1("v1_15_R1", 11501),
        v1_16_R1("v1_16_R1", 11601),
        v1_16_R2("v1_16_R2", 11602),
        v1_16_R3("v1_16_R3", 11603),
        v1_17_R1("v1_17_R1", 11701),
        v1_17_R2("v1_17_R2", 11702),
        v1_17_R3("v1_17_R3", 11703),
        v1_18_R1("v1_18_R1", 11801),
        v1_18_R2("v1_18_R2", 11802),
        v1_18_R3("v1_18_R3", 11803),
        v1_19_R1("v1_19_R1", 11901),
        v1_19_R2("v1_19_R2", 11902),
        v1_19_R3("v1_19_R3", 11903),
        v1_20_R1("v1_20_R1", 12001),
        v1_20_R2("v1_20_R2", 12002),
        v1_20_R3("v1_20_R3", 12003);

        private final String name;
        private final int id;

        Version(String name, int id) {
            this.name = name;
            this.id = id;
        }

        public String getNMSName() { return this.name; }
        public String getStrippedName() { return this.name.substring(0, this.name.length()-1).replaceAll("v", "").replaceAll("R", "").replaceAll("_", "."); }

        int getId() { return id; }
    }
}