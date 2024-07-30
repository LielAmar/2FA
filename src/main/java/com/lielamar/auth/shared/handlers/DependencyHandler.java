package com.lielamar.auth.shared.handlers;

import com.lielamar.auth.shared.TwoFactorAuthenticationPlugin;
import net.byteflux.libby.*;
import org.slf4j.Logger;

public class DependencyHandler {

    public <T extends TwoFactorAuthenticationPlugin> DependencyHandler(T plugin) {
        try {
            this.loadDependencies(plugin);
        } catch (Exception exception) {
            plugin.getDependencyLogger().error("[2FA] 2FA detected that you are using Java 16 without the --add-opens java.base/java.lang=ALL-UNNAMED or the --add-opens java.base/java.net=ALL-UNNAMED flags!");
            plugin.getDependencyLogger().error("[2FA] If you want the plugin to support all features, most significantly Remote Databases, please add this flag to your startup script");
        }
    }

    private <T extends TwoFactorAuthenticationPlugin> void loadDependencies(T plugin) {
        LibraryManager loader;
        boolean loadAllDependencies = false;
        if (plugin instanceof com.lielamar.auth.bukkit.TwoFactorAuthentication bukkitPlugin) {
            loader = new BukkitLibraryManager(bukkitPlugin);
            loadAllDependencies = true; // We load all the dependencies since we need them all.
        } else if (plugin instanceof com.lielamar.auth.bungee.TwoFactorAuthentication bungeePlugin) {
            loader = new BungeeLibraryManager(bungeePlugin);
        } else if (plugin instanceof com.lielamar.auth.velocity.TwoFactorAuthentication velocityPlugin) {
            loader = new VelocityLibraryManager<>(
                    velocityPlugin.getLogger(),
                    velocityPlugin.getDataDirectory(),
                    velocityPlugin.getProxy().getPluginManager(),
                    velocityPlugin
            );
        } else {
            throw new IllegalArgumentException("An instance that was not related to the plugin was provided.");
        }

        loader.addMavenCentral();


        Library library;
        Logger dependencyLogger = plugin.getDependencyLogger();
        if (loadAllDependencies) {
            String commonsCodecVersion = "1.17.1"; // Updated to match Gradle build file
            String hikariCpVersion = "5.1.0"; // Updated to match Gradle build file
            String h2Version = "2.3.230"; // Updated to match Gradle build file
            String mysqlVersion = "9.0.0"; // Updated to match Gradle build file
            String mariaDBVersion = "3.4.1"; // Updated to match Gradle build file
            String postgresVersion = "42.7.3"; // Updated to match Gradle build file
            String mongoDBVersion = "5.1.2"; // Updated to match Gradle build file
            String slf4jVersion = "2.0.13"; // Updated to match Gradle build file
            String log4jVersion = "2.23.1"; // Updated to match Gradle build file

            dependencyLogger.info("Loading library Commons-Codec v{}", commonsCodecVersion);
            library = Library.builder()
                    .groupId("commons-codec")
                    .artifactId("commons-codec")
                    .version(commonsCodecVersion)
                    .build();
            loader.loadLibrary(library);

            dependencyLogger.info("Loading library HikariCP v{}", hikariCpVersion);
            library = Library.builder()
                    .groupId("com.zaxxer")
                    .artifactId("HikariCP")
                    .version(hikariCpVersion)
                    .build();
            loader.loadLibrary(library);

            dependencyLogger.info("Loading library H2 v{}", h2Version);
            library = Library.builder()
                    .groupId("com.h2database")
                    .artifactId("h2")
                    .version(h2Version)
                    .build();
            loader.loadLibrary(library);

            dependencyLogger.info("Loading library MySQL v{}", mysqlVersion);
            library = Library.builder()
                    .groupId("com.mysql")
                    .artifactId("mysql-connector-j")
                    .version(mysqlVersion)
                    .build();
            loader.loadLibrary(library);

            dependencyLogger.info("Loading library MariaDB v{}", mariaDBVersion);
            library = Library.builder()
                    .groupId("org.mariadb.jdbc")
                    .artifactId("mariadb-java-client")
                    .version(mariaDBVersion)
                    .build();
            loader.loadLibrary(library);

            dependencyLogger.info("Loading library PostgreSQL v{}", postgresVersion);
            library = Library.builder()
                    .groupId("org.postgresql")
                    .artifactId("postgresql")
                    .version(postgresVersion)
                    .build();
            loader.loadLibrary(library);

            dependencyLogger.info("Loading library MongoDB v{}", mongoDBVersion);
            library = Library.builder()
                    .groupId("org.mongodb")
                    .artifactId("mongodb-driver-sync")
                    .version(mongoDBVersion)
                    .build();
            loader.loadLibrary(library);

            dependencyLogger.info("Loading library Slf4j v{}", slf4jVersion);
            library = Library.builder()
                    .groupId("org.slf4j")
                    .artifactId("slf4j-api")
                    .version(slf4jVersion)
                    .build();
            loader.loadLibrary(library);

            dependencyLogger.info("Loading library Log4j v{}", log4jVersion);
            library = Library.builder()
                    .groupId("org.apache.logging.log4j")
                    .artifactId("log4j-core")
                    .version(log4jVersion)
                    .build();
            loader.loadLibrary(library);
        }

        String kotlinStdLibVersion = "2.0.0"; // Added STDLib to allow atlassian library to run properly.
        String atlassianVersion = "2.1.1"; // Added Atlassian library

        dependencyLogger.info("Loading library Kotlin-StandardLib v{}", kotlinStdLibVersion);
        library = Library.builder()
                .groupId("org.jetbrains.kotlin")
                .artifactId("kotlin-stdlib")
                .version(kotlinStdLibVersion)
                .build();
        loader.loadLibrary(library);

        dependencyLogger.info("Loading library OneTime v{}", atlassianVersion);
        library = Library.builder()
                .groupId("com.atlassian")
                .artifactId("onetime")
                .version(atlassianVersion)
                .build();
        loader.loadLibrary(library);
    }
}