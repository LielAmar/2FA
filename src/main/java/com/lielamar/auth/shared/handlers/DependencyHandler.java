package com.lielamar.auth.shared.handlers;

import net.byteflux.libby.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DependencyHandler {
    protected final Logger logger;
    protected final LibraryManager loader;

    public DependencyHandler(LibraryManager loader) {
        this.logger = LoggerFactory.getLogger("2FA");
        this.loader = loader;
        try {
            this.loadDependencies(loader);
        } catch (Exception exception) {
            logger.error("[2FA] 2FA detected that you are using Java 16 without the --add-opens java.base/java.lang=ALL-UNNAMED or the --add-opens java.base/java.net=ALL-UNNAMED flags!");
            logger.error("[2FA] If you want the plugin to support all features, most significantly Remote Databases, please add this flag to your startup script");
        }
    }

    private void loadDependencies(LibraryManager loader) {
        loader.addMavenCentral();

        Library library;

        String kotlinStdLibVersion = "2.0.0"; // Added STDLib to allow atlassian library to run properly.
        String atlassianVersion = "2.1.1"; // Added Atlassian library

        logger.info("Loading library Kotlin-StandardLib v{}", kotlinStdLibVersion);
        library = Library.builder()
                .groupId("org.jetbrains.kotlin")
                .artifactId("kotlin-stdlib")
                .version(kotlinStdLibVersion)
                .build();
        loader.loadLibrary(library);

        logger.info("Loading library OneTime v{}", atlassianVersion);
        library = Library.builder()
                .groupId("com.atlassian")
                .artifactId("onetime")
                .version(atlassianVersion)
                .build();
        loader.loadLibrary(library);
    }
}