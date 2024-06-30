package com.lielamar.auth.bukkit.handlers;

import net.byteflux.libby.BukkitLibraryManager;
import net.byteflux.libby.Library;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

public class DependencyHandler {

    public DependencyHandler(Plugin plugin) {
        try {
            this.loadDependencies(plugin);
        } catch (Exception exception) {
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "[2FA] 2FA detected that you are using Java 16 without the --add-opens java.base/java.lang=ALL-UNNAMED or the --add-opens java.base/java.net=ALL-UNNAMED flags!");
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "[2FA] If you want the plugin to support all features, most significantly Remote Databases, please add this flag to your startup script");
        }
    }

    private void loadDependencies(Plugin plugin) {
        BukkitLibraryManager loader = new BukkitLibraryManager(plugin);

        loader.addMavenCentral();


        String commonsCodecVersion = "1.17.0"; // Updated to match Gradle build file
        String hikariCpVersion = "5.1.0"; // Updated to match Gradle build file
        String h2Version = "2.2.224"; // Updated to match Gradle build file
        String mysqlVersion = "8.4.0"; // Updated to match Gradle build file
        String mariaDBVersion = "3.4.0"; // Updated to match Gradle build file
        String postgresVersion = "42.7.3"; // Updated to match Gradle build file
        String mongoDBVersion = "5.1.1"; // Updated to match Gradle build file
        String slf4jVersion = "2.0.13"; // Updated to match Gradle build file
        String log4jVersion = "2.23.1"; // Updated to match Gradle build file
        String atlassianVersion = "2.1.1"; // Added Atlassian library

        Bukkit.getServer().getLogger().info("Loading library Atlassian v" + atlassianVersion);
        Library library = Library.builder()
                .groupId("com.atlassian")
                .artifactId("onetime")
                .version(atlassianVersion)
                .build();
        loader.loadLibrary(library);

        Bukkit.getServer().getLogger().info("Loading library Commons-Codec v" + commonsCodecVersion);
        library = Library.builder()
                .groupId("commons-codec")
                .artifactId("commons-codec")
                .version(commonsCodecVersion)
                .build();
        loader.loadLibrary(library);

        Bukkit.getServer().getLogger().info("Loading library HikariCP v" + hikariCpVersion);
        library = Library.builder()
                .groupId("com.zaxxer")
                .artifactId("HikariCP")
                .version(hikariCpVersion)
                .build();
        loader.loadLibrary(library);

        Bukkit.getServer().getLogger().info("Loading library H2 v" + h2Version);
        library = Library.builder()
                .groupId("com.h2database")
                .artifactId("h2")
                .version(h2Version)
                .build();
        loader.loadLibrary(library);

        Bukkit.getServer().getLogger().info("Loading library MySQL v" + mysqlVersion);
        library = Library.builder()
                .groupId("mysql")
                .artifactId("mysql-connector-j")
                .version(mysqlVersion)
                .build();
        loader.loadLibrary(library);

        Bukkit.getServer().getLogger().info("Loading library MariaDB v" + mariaDBVersion);
        library = Library.builder()
                .groupId("org.mariadb.jdbc")
                .artifactId("mariadb-java-client")
                .version(mariaDBVersion)
                .build();
        loader.loadLibrary(library);

        Bukkit.getServer().getLogger().info("Loading library PostgreSQL v" + postgresVersion);
        library = Library.builder()
                .groupId("org.postgresql")
                .artifactId("postgresql")
                .version(postgresVersion)
                .build();
        loader.loadLibrary(library);

        Bukkit.getServer().getLogger().info("Loading library MongoDB v" + mongoDBVersion);
        library = Library.builder()
                .groupId("org.mongodb")
                .artifactId("mongodb-driver-sync")
                .version(mongoDBVersion)
                .build();
        loader.loadLibrary(library);

        Bukkit.getServer().getLogger().info("Loading library Slf4j v" + slf4jVersion);
        library = Library.builder()
                .groupId("org.slf4j")
                .artifactId("slf4j-api")
                .version(slf4jVersion)
                .build();
        loader.loadLibrary(library);

        Bukkit.getServer().getLogger().info("Loading library Log4j v" + log4jVersion);
        library = Library.builder()
                .groupId("org.apache.logging.log4j")
                .artifactId("log4j-core")
                .version(log4jVersion)
                .build();
        loader.loadLibrary(library);
    }
}