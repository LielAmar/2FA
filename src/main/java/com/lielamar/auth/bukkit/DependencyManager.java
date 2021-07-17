package com.lielamar.auth.bukkit;

import net.byteflux.libby.BukkitLibraryManager;
import net.byteflux.libby.Library;
import org.bukkit.plugin.Plugin;

public class DependencyManager {

    public DependencyManager(Plugin plugin) {
        this.loadDependencies(plugin);
    }

    private void loadDependencies(Plugin plugin) {
        BukkitLibraryManager loader = new BukkitLibraryManager(plugin);

        loader.addMavenCentral();
        loader.addRepository("https://repo.alessiodp.com/releases/");

        System.out.println("[+] Downloading library: Google Auth v1.4.0");
        Library library = Library.builder()
                .groupId("com.warrenstrange")
                .artifactId("googleauth")
                .version("1.4.0")
                .build();
        loader.loadLibrary(library);

        System.out.println("[+] Downloading library: Commons-Codec v1.6");
        library = Library.builder()
                .groupId("commons-codec")
                .artifactId("commons-codec")
                .version("1.6")
                .build();
        loader.loadLibrary(library);

        System.out.println("[+] Downloading library: HikariCP v4.0.3");
        library = Library.builder()
                .groupId("com.zaxxer")
                .artifactId("HikariCP")
                .version("4.0.3")
                .build();
        loader.loadLibrary(library);

        System.out.println("[+] Downloading library: H2 v1.4.200");
        library = Library.builder()
                .groupId("com.h2database")
                .artifactId("h2")
                .version("1.4.200")
                .build();
        loader.loadLibrary(library);

        System.out.println("[+] Downloading library: MariaDB v3.0.0-alpha");
        library = Library.builder()
                .groupId("org.mariadb.jdbc")
                .artifactId("mariadb-java-client")
                .version("3.0.0-alpha")
                .build();
        loader.loadLibrary(library);

        System.out.println("[+] Downloading library: PostgreSQL v42.2.23");
        library = Library.builder()
                .groupId("org.postgresql")
                .artifactId("postgresql")
                .version("42.2.23")
                .build();
        loader.loadLibrary(library);

        System.out.println("[+] Downloading library: MongoDB v3.12.7");
        library = Library.builder()
                .groupId("org.mongodb")
                .artifactId("mongo-java-driver")
                .version("3.12.7")
                .build();
        loader.loadLibrary(library);
    }
}
