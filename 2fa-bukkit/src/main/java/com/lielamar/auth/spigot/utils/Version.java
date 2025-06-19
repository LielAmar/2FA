package com.lielamar.auth.spigot.utils;

import org.bukkit.Bukkit;

import org.bukkit.Bukkit;

public class Version {

    private static final Version INSTANCE = new Version();
    public static Version getInstance() { return INSTANCE; }

    private final String versionString;
    private final ComparableVersion serverVersion;

    private Version() {
        String bukkitVersion = Bukkit.getServer().getBukkitVersion().split("-")[0]; // e.g., "1.20.6"
        this.versionString = bukkitVersion;
        this.serverVersion = new ComparableVersion(bukkitVersion);
    }

    public String getVersionString() {
        return this.versionString;
    }

    public boolean isAtLeast(String version) {
        return serverVersion.compareTo(new ComparableVersion(version)) >= 0;
    }

    public boolean isLowerThan(String version) {
        return serverVersion.compareTo(new ComparableVersion(version)) < 0;
    }

    public boolean isHigherThan(String version) {
        return serverVersion.compareTo(new ComparableVersion(version)) > 0;
    }

    static class ComparableVersion implements Comparable<ComparableVersion> {
        private final int[] parts;

        public ComparableVersion(String version) {
            String[] tokens = version.split("\\.");
            this.parts = new int[Math.max(tokens.length, 3)];
            for (int i = 0; i < tokens.length; i++) {
                try {
                    this.parts[i] = Integer.parseInt(tokens[i]);
                } catch (NumberFormatException e) {
                    this.parts[i] = 0;
                }
            }
        }

        @Override
        public int compareTo(ComparableVersion other) {
            for (int i = 0; i < Math.max(this.parts.length, other.parts.length); i++) {
                int a = i < this.parts.length ? this.parts[i] : 0;
                int b = i < other.parts.length ? other.parts[i] : 0;
                if (a != b) return Integer.compare(a, b);
            }
            return 0;
        }

        @Override
        public String toString() {
            return String.join(".", String.valueOf(parts[0]), String.valueOf(parts[1]), String.valueOf(parts[2]));
        }
    }
}
