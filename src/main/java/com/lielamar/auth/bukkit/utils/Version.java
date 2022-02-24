package com.lielamar.auth.bukkit.utils;

import org.bukkit.Bukkit;

public class Version {

    private static final Version instance = new Version();
    public static Version getInstance() { return instance; }

    private Version() {
        try {
            this.serverVersion = ServerVersion.valueOf("v" + Bukkit.getServer().getBukkitVersion().split("-")[0].replaceAll("\\.", "_"));
            this.nmsVersion = NMSVersion.valueOf(Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3]);
        } catch (Exception e) { e.printStackTrace(); }
    }


    private ServerVersion serverVersion;
    private NMSVersion nmsVersion;

    public ServerVersion getServerVersion() { return this.serverVersion; }
    public NMSVersion getNMSVersion() { return this.nmsVersion; }


    public enum ServerVersion {
        v1_7_2("v1_7_2", 10702, NMSVersion.v1_7_R1),
        v1_7_5("v1_7_5", 10705, NMSVersion.v1_7_R2),
        v1_7_8("v1_7_8", 10708, NMSVersion.v1_7_R3),
        v1_7_9("v1_7_9", 10709, NMSVersion.v1_7_R3),
        v1_7_10("v1_7_10", 10710, NMSVersion.v1_7_R4),

        v1_8_0("v1_8_0", 10800, NMSVersion.v1_8_R1),
        v1_8_3("v1_8_3", 10803, NMSVersion.v1_8_R2),
        v1_8_4("v1_8_4", 10804, NMSVersion.v1_8_R3),
        v1_8_5("v1_8_5", 10805, NMSVersion.v1_8_R3),
        v1_8_6("v1_8_6", 10806, NMSVersion.v1_8_R3),
        v1_8_7("v1_8_7", 10807, NMSVersion.v1_8_R3),
        v1_8_8("v1_8_8", 10808, NMSVersion.v1_8_R3),

        v1_9_0("v1_9_0", 10900, NMSVersion.v1_9_R1),
        v1_9_2("v1_9_2", 10902, NMSVersion.v1_9_R1),
        v1_9_4("v1_9_4", 10904, NMSVersion.v1_9_R2),

        v1_10_0("v1_10_0", 11000, NMSVersion.v1_10_R1),
        v1_10_2("v1_10_2", 11002, NMSVersion.v1_10_R1),

        v1_11_0("v1_11_0", 11100, NMSVersion.v1_11_R1),
        v1_11_1("v1_11_1", 11101, NMSVersion.v1_11_R1),
        v1_11_2("v1_11_2", 11102, NMSVersion.v1_11_R1),

        v1_12_0("v1_12_0", 11200, NMSVersion.v1_12_R1),
        v1_12_1("v1_12_1", 11201, NMSVersion.v1_12_R1),
        v1_12_2("v1_12_2", 11202, NMSVersion.v1_12_R1),

        v1_13_0("v1_13_0", 11300, NMSVersion.v1_13_R1),
        v1_13_1("v1_13_1", 11301, NMSVersion.v1_13_R2),
        v1_13_2("v1_13_2", 11302, NMSVersion.v1_13_R2),

        v1_14_0("v1_14_0", 11400, NMSVersion.v1_14_R1),
        v1_14_1("v1_14_1", 11401, NMSVersion.v1_14_R1),
        v1_14_2("v1_14_2", 11402, NMSVersion.v1_14_R1),
        v1_14_3("v1_14_3", 11403, NMSVersion.v1_14_R1),
        v1_14_4("v1_14_4", 11404, NMSVersion.v1_14_R1),

        v1_15_0("v1_15_0", 11500, NMSVersion.v1_15_R1),
        v1_15_1("v1_15_1", 11501, NMSVersion.v1_15_R1),
        v1_15_2("v1_15_2", 11502, NMSVersion.v1_15_R1),

        v1_16_1("v1_16_1", 11601, NMSVersion.v1_16_R1),
        v1_16_2("v1_16_2", 11602, NMSVersion.v1_16_R2),
        v1_16_3("v1_16_3", 11603, NMSVersion.v1_16_R2),
        v1_16_4("v1_16_4", 11604, NMSVersion.v1_16_R3),
        v1_16_5("v1_16_5", 11605, NMSVersion.v1_16_R3),

        v1_17_0("v1_17_0", 11700, NMSVersion.v1_17_R1),
        v1_17_1("v1_17_1", 11701, NMSVersion.v1_17_R1),
        v1_17_2("v1_17_2", 11702, NMSVersion.v1_17_R1),
        v1_17_3("v1_17_3", 11703, NMSVersion.v1_17_R1),
        v1_17_4("v1_17_4", 11704, NMSVersion.v1_17_R1),
        v1_17_5("v1_17_5", 11705, NMSVersion.v1_17_R1),
        v1_17_6("v1_17_6", 11706, NMSVersion.v1_17_R1),
        v1_17_7("v1_17_7", 11707, NMSVersion.v1_17_R1),
        v1_17_8("v1_17_8", 11708, NMSVersion.v1_17_R1),
        v1_17_9("v1_17_9", 11709, NMSVersion.v1_17_R1),

        v1_18_0("v1_18_0", 11800, NMSVersion.v1_18_R1),
        v1_18_1("v1_18_1", 11801, NMSVersion.v1_18_R1),
        v1_18_2("v1_18_2", 11802, NMSVersion.v1_18_R1),
        v1_18_3("v1_18_3", 11803, NMSVersion.v1_18_R1),
        v1_18_4("v1_18_4", 11804, NMSVersion.v1_18_R1),
        v1_18_5("v1_18_5", 11805, NMSVersion.v1_18_R1),
        v1_18_6("v1_18_6", 11806, NMSVersion.v1_18_R1),
        v1_18_7("v1_18_7", 11807, NMSVersion.v1_18_R1),
        v1_18_8("v1_18_8", 11808, NMSVersion.v1_18_R1),
        v1_18_9("v1_18_9", 11809, NMSVersion.v1_18_R1),

        v1_19_0("v1_19_0", 11900, NMSVersion.v1_19_R1),
        v1_19_1("v1_19_1", 11901, NMSVersion.v1_19_R1),
        v1_19_2("v1_19_2", 11902, NMSVersion.v1_19_R1),
        v1_19_3("v1_19_3", 11903, NMSVersion.v1_19_R1),
        v1_19_4("v1_19_4", 11904, NMSVersion.v1_19_R1),
        v1_19_5("v1_19_5", 11905, NMSVersion.v1_19_R1),
        v1_19_6("v1_19_6", 11906, NMSVersion.v1_19_R1),
        v1_19_7("v1_19_7", 11907, NMSVersion.v1_19_R1),
        v1_19_8("v1_19_8", 11908, NMSVersion.v1_19_R1),
        v1_19_9("v1_19_9", 11909, NMSVersion.v1_19_R1),

        v1_20_0("v1_20_0", 12000, NMSVersion.v1_20_R1),
        v1_20_1("v1_20_1", 12001, NMSVersion.v1_20_R1),
        v1_20_2("v1_20_2", 12002, NMSVersion.v1_20_R1),
        v1_20_3("v1_20_3", 12003, NMSVersion.v1_20_R1),
        v1_20_4("v1_20_4", 12004, NMSVersion.v1_20_R1),
        v1_20_5("v1_20_5", 12005, NMSVersion.v1_20_R1),
        v1_20_6("v1_20_6", 12006, NMSVersion.v1_20_R1),
        v1_20_7("v1_20_7", 12007, NMSVersion.v1_20_R1),
        v1_20_8("v1_20_8", 12008, NMSVersion.v1_20_R1),
        v1_20_9("v1_20_9", 12009, NMSVersion.v1_20_R1);

        private final String versionName;
        private final int versionId;
        private final NMSVersion nmsVersion;

        ServerVersion(String versionName, int versionId, NMSVersion nmsVersion) {
            this.versionName = versionName;
            this.versionId = versionId;
            this.nmsVersion = nmsVersion;
        }

        public String getVersionName() { return this.versionName; }
        public String getStrippedName() { return this.versionName.replaceFirst("v", "").replaceAll("_", "."); }

        int getVersionId() { return this.versionId; }

        NMSVersion getNmsVersion() { return this.nmsVersion; }

        public boolean above(ServerVersion otherVersion) { return this.versionId >= otherVersion.getVersionId(); }
    }

    public enum NMSVersion {
        v1_7_R1("v1_7_R1", 10701),
        v1_7_R2("v1_7_R2", 10702),
        v1_7_R3("v1_7_R3", 10703),
        v1_7_R4("v1_7_R4", 10704),

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

        private final String versionName;
        private final int versionId;

        NMSVersion(String versionName, int versionId) {
            this.versionName = versionName;
            this.versionId = versionId;
        }

        public String getVersionName() { return this.versionName; }
        public String getStrippedName() { return this.versionName.replaceFirst("v", "").replaceAll("_", "."); }

        int getVersionId() { return this.versionId; }

        public boolean above(NMSVersion otherVersion) { return this.versionId >= otherVersion.getVersionId(); }
    }
}