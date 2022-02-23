package com.lielamar.auth.bukkit.handlers;

import com.lielamar.auth.bukkit.TwoFactorAuthentication;
import net.byteflux.libby.BukkitLibraryManager;
import net.byteflux.libby.Library;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.util.Properties;

public class DependencyHandler {

    public DependencyHandler(Plugin plugin) {
        try {
            Properties properties = new Properties();
            properties.load(TwoFactorAuthentication.class.getClassLoader().getResourceAsStream("project.properties"));

            this.loadDependencies(plugin, properties);
        } catch(IOException exception) {
            exception.printStackTrace();

            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "[2FA] 2FA failed to load dependencies. The plugin might not support all features!");
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "[2FA] This has most likely happened because your server doesn't have access to the internet!");
        } catch(Exception exception) {
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "[2FA] 2FA detected that you are using Java 16 without the --add-opens java.base/java.lang=ALL-UNNAMED or the --add-opens java.base/java.net=ALL-UNNAMED flags!");
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "[2FA] If you want the plugin to support all features, most significantly Remote Databases, please add this flag to your startup script");
        }
    }

    private void loadDependencies(Plugin plugin, Properties properties) {
        BukkitLibraryManager loader = new BukkitLibraryManager(plugin);

        if(Boolean.parseBoolean(properties.getProperty("load_maven_central", "true")))
            loader.addMavenCentral();

        String googleAuthGroupId = properties.getProperty("group_id_google_auth", "com.warrenstrange");
        String commonsCodecGroupId = properties.getProperty("group_id_commons_codec", "commons-codec");
        String hikariCpGroupId = properties.getProperty("group_id_hikari_cp", "com.zaxxer");
        String h2GroupId = properties.getProperty("group_id_h2", "com.h2database");
        String mysqlGroupId = properties.getProperty("group_id_mysql", "mysql");
        String mariaDBGroupId = properties.getProperty("group_id_maria_db", "org.mariadb.jdbc");
        String postgresGroupId = properties.getProperty("group_id_postgres", "org.postgresql");
        String mongoDBGroupId = properties.getProperty("group_id_mongo_db", "org.mongodb");
        String slf4jGroupId = properties.getProperty("group_id_slf4j", "org.slf4j");

        String googleAuthArtifactId = properties.getProperty("artifact_id_google_auth", "googleauth");
        String commonsCodecArtifactId = properties.getProperty("artifact_id_commons_codec", "commons-codec");
        String hikariCpArtifactId = properties.getProperty("artifact_id_hikari_cp", "HikariCP");
        String h2ArtifactId = properties.getProperty("artifact_id_h2", "h2");
        String mysqlArtifactId = properties.getProperty("artifact_id_mysql", "mysql-connector-java");
        String mariaDBArtifactId = properties.getProperty("artifact_id_maria_db", "mariadb-java-client");
        String postgresArtifactId = properties.getProperty("artifact_id_postgres", "postgresql");
        String mongoDBArtifactId = properties.getProperty("artifact_id_mongo_db", "mongo-java-driver");
        String slf4jArtifactId = properties.getProperty("artifact_id_slf4j", "slf4j-api");

        String googleAuthVersion = properties.getProperty("version_google_auth", "1.5.0");
        String commonsCodecVersion = properties.getProperty("version_commons_codec", "1.15");
        String hikariCpVersion = properties.getProperty("version_hikari_cp", "4.0.3");
        String h2Version = properties.getProperty("version_h2", "2.1.210");
        String mysqlVersion = properties.getProperty("version_mysql", "8.0.26");
        String mariaDBVersion = properties.getProperty("version_maria_db", "2.7.3");
        String postgresVersion = properties.getProperty("version_postgres", "42.3.3");
        String mongoDBVersion = properties.getProperty("version_mongo_db", "3.12.10");
        String slf4jVersion = properties.getProperty("version_slf4j", "2.0.0-alpha5");

        System.out.println("[2FA] Loading library Google Auth v" + googleAuthVersion);
        Library library = Library.builder()
                .groupId(googleAuthGroupId)
                .artifactId(googleAuthArtifactId)
                .version(googleAuthVersion)
                .build();
        loader.loadLibrary(library);

        System.out.println("[2FA] Loading library Commons-Codec v" + commonsCodecVersion);
        library = Library.builder()
                .groupId(commonsCodecGroupId)
                .artifactId(commonsCodecArtifactId)
                .version(commonsCodecVersion)
                .build();
        loader.loadLibrary(library);

        System.out.println("[2FA] Loading library HikariCP v" + hikariCpVersion);
        library = Library.builder()
                .groupId(hikariCpGroupId)
                .artifactId(hikariCpArtifactId)
                .version(hikariCpVersion)
                .build();
        loader.loadLibrary(library);

        System.out.println("[2FA] Loading library H2 v" + h2Version);
        library = Library.builder()
                .groupId(h2GroupId)
                .artifactId(h2ArtifactId)
                .version(h2Version)
                .build();
        loader.loadLibrary(library);

        System.out.println("[2FA] Loading library MySQL v" + mysqlVersion);
        library = Library.builder()
                .groupId(mysqlGroupId)
                .artifactId(mysqlArtifactId)
                .version(mysqlVersion)
                .build();
        loader.loadLibrary(library);

        System.out.println("[2FA] Loading library MariaDB v" + mariaDBVersion);
        library = Library.builder()
                .groupId(mariaDBGroupId)
                .artifactId(mariaDBArtifactId)
                .version(mariaDBVersion)
                .build();
        loader.loadLibrary(library);

        System.out.println("[2FA] Loading library PostgreSQL v" + postgresVersion);
        library = Library.builder()
                .groupId(postgresGroupId)
                .artifactId(postgresArtifactId)
                .version(postgresVersion)
                .build();
        loader.loadLibrary(library);

        System.out.println("[2FA] Loading library MongoDB v" + mongoDBVersion);
        library = Library.builder()
                .groupId(mongoDBGroupId)
                .artifactId(mongoDBArtifactId)
                .version(mongoDBVersion)
                .build();
        loader.loadLibrary(library);

        System.out.println("[2FA] Loading library Slf4j v" + slf4jVersion);
        library = Library.builder()
                .groupId(slf4jGroupId)
                .artifactId(slf4jArtifactId)
                .version(slf4jVersion)
                .build();
        loader.loadLibrary(library);
    }
}
