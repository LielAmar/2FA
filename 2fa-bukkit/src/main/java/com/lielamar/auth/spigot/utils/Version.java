package com.lielamar.auth.spigot.utils;

import org.bukkit.Bukkit;

public class Version {

    private static final Version INSTANCE = new Version();
    public static Version getInstance() { return INSTANCE; }

    private Version() {
        try {
            this.serverVersion = ServerVersion.valueOf("v" + Bukkit.getServer().getBukkitVersion().split("-")[0].replaceAll("\\.", "_"));
        } catch (Exception ignored) {}
    }


    private ServerVersion serverVersion;

    public ServerVersion getServerVersion() { return this.serverVersion; }

    public enum ServerVersion {
        v1_8_0("v1_8_0", 1),
        v1_8_3("v1_8_3", 2),
        v1_8_4("v1_8_4", 3),
        v1_8_5("v1_8_5", 4),
        v1_8_6("v1_8_6", 5),
        v1_8_7("v1_8_7", 6),
        v1_8_8("v1_8_8", 7),

        v1_9_0("v1_9_0", 8),
        v1_9_2("v1_9_2", 9),
        v1_9_4("v1_9_4", 10),

        v1_10_0("v1_10_0", 11),
        v1_10_2("v1_10_2", 12),

        v1_11_0("v1_11_0", 13),
        v1_11_1("v1_11_1", 14),
        v1_11_2("v1_11_2", 15),

        v1_12_0("v1_12_0", 16),
        v1_12_1("v1_12_1", 17),
        v1_12_2("v1_12_2", 18),

        v1_13_0("v1_13_0", 19),
        v1_13_1("v1_13_1", 20),
        v1_13_2("v1_13_2", 21),

        v1_14_0("v1_14_0", 22),
        v1_14_1("v1_14_1", 23),
        v1_14_2("v1_14_2", 24),
        v1_14_3("v1_14_3", 25),
        v1_14_4("v1_14_4", 26),

        v1_15_0("v1_15_0", 27),
        v1_15_1("v1_15_1", 28),
        v1_15_2("v1_15_2", 29),

        v1_16_0("v1_16_0", 30),
        v1_16_1("v1_16_1", 31),
        v1_16_2("v1_16_2", 32),
        v1_16_3("v1_16_3", 33),
        v1_16_4("v1_16_4", 34),
        v1_16_5("v1_16_5", 35),

        v1_17_0("v1_17_0", 36),
        v1_17_1("v1_17_1", 37),

        v1_18_0("v1_18_0", 38),
        v1_18_1("v1_18_1", 39),
        v1_18_2("v1_18_2", 40),

        v1_19_0("v1_19_0", 41),
        v1_19_1("v1_19_1", 42),
        v1_19_2("v1_19_2", 43),
        v1_19_3("v1_19_3", 44),
        v1_19_4("v1_19_4", 45),

        v1_20_0("v1_20_0", 46),
        v1_20_1("v1_20_1", 47),
        v1_20_2("v1_20_2", 48),
        v1_20_3("v1_20_3", 49),
        v1_20_4("v1_20_4", 50),
        v1_20_5("v1_20_5", 51),
        v1_20_6("v1_20_6", 52),

        v1_21("v1_21_0", 53);

        private final String versionName;
        private final int versionId;

        ServerVersion(String versionName, int versionId) {
            this.versionName = versionName;
            this.versionId = versionId;
        }

        public String getVersionName() { return this.versionName; }
        public String getStrippedName() { return this.versionName.replaceFirst("v", "").replaceAll("_", "."); }

        int getVersionId() { return this.versionId; }

        public boolean above(ServerVersion otherVersion) { return this.versionId >= otherVersion.getVersionId(); }
    }
}