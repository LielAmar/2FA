package com.lielamar.auth.bukkit;

import net.byteflux.libby.BukkitLibraryManager;
import net.byteflux.libby.Library;
import org.bukkit.plugin.Plugin;

import java.util.Enumeration;
import java.util.Properties;

public class DependencyManager {

    public DependencyManager(Plugin plugin, Properties properties) {
        this.loadDependencies(plugin, properties);
    }

    private void loadDependencies(Plugin plugin, Properties properties) {
        BukkitLibraryManager loader = new BukkitLibraryManager(plugin);

        if(Boolean.parseBoolean(properties.getProperty("load_maven_central", "true")))
            loader.addMavenCentral();

        String googleAuthGroupId = properties.getProperty("group_id_google_auth", "com.warrenstrange");
        String commonsCodecGroupId = properties.getProperty("group_id_commons_codec", "commons-codec");
        String hikariCpGroupId = properties.getProperty("group_id_hikari_cp", "com.zaxxer");
        String h2GroupId = properties.getProperty("group_id_h2", "com.h2database");
        String mariaDBGroupId = properties.getProperty("group_id_maria_db", "org.mariadb.jdbc");
        String postgresGroupId = properties.getProperty("group_id_postgres", "org.postgresql");
        String mongoDBGroupId = properties.getProperty("group_id_mongo_db", "org.mongodb");

        String googleAuthArtifactId = properties.getProperty("artifact_id_google_auth", "googleauth");
        String commonsCodecArtifactId = properties.getProperty("artifact_id_commons_codec", "commons-codec");
        String hikariCpArtifactId = properties.getProperty("artifact_id_hikari_cp", "HikariCP");
        String h2ArtifactId = properties.getProperty("artifact_id_h2", "h2");
        String mariaDBArtifactId = properties.getProperty("artifact_id_maria_db", "mariadb-java-client");
        String postgresArtifactId = properties.getProperty("artifact_id_postgres", "postgresql");
        String mongoDBArtifactId = properties.getProperty("artifact_id_mongo_db", "mongo-java-driver");

        String googleAuthVersion = properties.getProperty("version_google_auth", "1.4.0");
        String commonsCodecVersion = properties.getProperty("version_commons_codec", "1.6");
        String hikariCpVersion = properties.getProperty("version_hikari_cp", "4.0.3");
        String h2Version = properties.getProperty("version_h2", "1.4.200");
        String mariaDBVersion = properties.getProperty("version_maria_db", "3.0.0-alpha");
        String postgresVersion = properties.getProperty("version_postgres", "42.2.23");
        String mongoDBVersion = properties.getProperty("version_mongo_db", "3.12.7");


        Enumeration<?> iter = properties.propertyNames();
        while(iter.hasMoreElements()) {
            Object val = iter.nextElement();
            System.out.println(val + ": " + properties.getProperty((String)val));
        }


        System.out.println("[+] Loading dependency: Google Auth v" + googleAuthVersion);
        Library library = Library.builder()
                .groupId(googleAuthGroupId)
                .artifactId(googleAuthArtifactId)
                .version(googleAuthVersion)
                .build();
        loader.loadLibrary(library);

        System.out.println("[+] Loading dependency: Commons-Codec v" + commonsCodecVersion);
        library = Library.builder()
                .groupId(commonsCodecGroupId)
                .artifactId(commonsCodecArtifactId)
                .version(commonsCodecVersion)
                .build();
        loader.loadLibrary(library);

        System.out.println("[+] Loading dependency: HikariCP v" + hikariCpVersion);
        library = Library.builder()
                .groupId(hikariCpGroupId)
                .artifactId(hikariCpArtifactId)
                .version(hikariCpVersion)
                .build();
        loader.loadLibrary(library);

        System.out.println("[+] Loading dependency: H2 v" + h2Version);
        library = Library.builder()
                .groupId(h2GroupId)
                .artifactId(h2ArtifactId)
                .version(h2Version)
                .build();
        loader.loadLibrary(library);

        System.out.println("[+] Loading dependency: MariaDB v" + mariaDBVersion);
        library = Library.builder()
                .groupId(mariaDBGroupId)
                .artifactId(mariaDBArtifactId)
                .version(mariaDBVersion)
                .build();
        loader.loadLibrary(library);

        System.out.println("[+] Loading dependency: PostgreSQL v" + postgresVersion);
        library = Library.builder()
                .groupId(postgresGroupId)
                .artifactId(postgresArtifactId)
                .version(postgresVersion)
                .build();
        loader.loadLibrary(library);

        System.out.println("[+] Loading dependency: MongoDB v" + mongoDBVersion);
        library = Library.builder()
                .groupId(mongoDBGroupId)
                .artifactId(mongoDBArtifactId)
                .version(mongoDBVersion)
                .build();
        loader.loadLibrary(library);
    }
}
