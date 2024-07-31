package com.lielamar.auth.bukkit.handlers;

import com.lielamar.auth.bukkit.TwoFactorAuthentication;
import com.lielamar.auth.shared.handlers.DependencyHandler;
import net.byteflux.libby.BukkitLibraryManager;
import net.byteflux.libby.Library;

public class BukkitDependencyHandler extends DependencyHandler {

    public BukkitDependencyHandler(TwoFactorAuthentication plugin) {
        super(new BukkitLibraryManager(plugin));
    }

    protected void loadDependencies() {
        super.loadDependencies();
        Library library;

        String hikariCpVersion = "5.1.0"; // Updated to match Gradle build file
        String h2Version = "2.3.230"; // Updated to match Gradle build file
        String mysqlVersion = "9.0.0"; // Updated to match Gradle build file
        String mariaDBVersion = "3.4.1"; // Updated to match Gradle build file
        String postgresVersion = "42.7.3"; // Updated to match Gradle build file
        String mongoDBVersion = "5.1.2"; // Updated to match Gradle build file
        String slf4jVersion = "2.0.13"; // Updated to match Gradle build file
        String log4jVersion = "2.23.1"; // Updated to match Gradle build file

        logger.info("Loading library HikariCP v" + hikariCpVersion);
        library = Library.builder()
                .groupId("com.zaxxer")
                .artifactId("HikariCP")
                .version(hikariCpVersion)
                .build();
        loader.loadLibrary(library);

        logger.info("Loading library H2 v" + h2Version);
        library = Library.builder()
                .groupId("com.h2database")
                .artifactId("h2")
                .version(h2Version)
                .build();
        loader.loadLibrary(library);

        logger.info("Loading library MySQL v" + mysqlVersion);
        library = Library.builder()
                .groupId("com.mysql")
                .artifactId("mysql-connector-j")
                .version(mysqlVersion)
                .build();
        loader.loadLibrary(library);

        logger.info("Loading library MariaDB v" + mariaDBVersion);
        library = Library.builder()
                .groupId("org.mariadb.jdbc")
                .artifactId("mariadb-java-client")
                .version(mariaDBVersion)
                .build();
        loader.loadLibrary(library);

        logger.info("Loading library PostgreSQL v" + postgresVersion);
        library = Library.builder()
                .groupId("org.postgresql")
                .artifactId("postgresql")
                .version(postgresVersion)
                .build();
        loader.loadLibrary(library);

        logger.info("Loading library MongoDB v" + mongoDBVersion);
        library = Library.builder()
                .groupId("org.mongodb")
                .artifactId("mongodb-driver-sync")
                .version(mongoDBVersion)
                .build();
        loader.loadLibrary(library);

        logger.info("Loading library Slf4j v" + slf4jVersion);
        library = Library.builder()
                .groupId("org.slf4j")
                .artifactId("slf4j-api")
                .version(slf4jVersion)
                .build();
        loader.loadLibrary(library);

        logger.info("Loading library Log4j v" + log4jVersion);
        library = Library.builder()
                .groupId("org.apache.logging.log4j")
                .artifactId("log4j-core")
                .version(log4jVersion)
                .build();
        loader.loadLibrary(library);
    }
}
