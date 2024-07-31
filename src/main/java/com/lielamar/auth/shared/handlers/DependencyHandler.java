package com.lielamar.auth.shared.handlers;

import net.byteflux.libby.Library;
import net.byteflux.libby.LibraryManager;

import java.util.logging.Level;
import java.util.logging.Logger;

public class DependencyHandler {
    protected final Logger logger;
    protected final LibraryManager loader;

    public DependencyHandler(LibraryManager loader) {
        this.logger = Logger.getLogger("2FADebug");
        this.loader = loader;
        try {
            this.loadDependencies();
        } catch (Exception exception) {
            logger.severe("An error has occurred while attempting to load the dependencies");
            logger.log(Level.SEVERE, "Error caused by: ", exception);
        }
    }

    protected void loadDependencies() {
        loader.addMavenCentral();

        Library library;

        String kotlinStdLibVersion = "2.0.0"; // Added STDLib to allow atlassian library to run properly.
        String atlassianVersion = "2.1.1"; // Added Atlassian library
        String commonsCodecVersion = "1.17.1"; // Updated to match Gradle build file

        logger.info("Loading library Commons-Codec v" + commonsCodecVersion);
        library = Library.builder()
                .groupId("commons-codec")
                .artifactId("commons-codec")
                .version(commonsCodecVersion)
                .build();
        loader.loadLibrary(library);

        logger.info("Loading library Kotlin-StandardLib v" + kotlinStdLibVersion);
        library = Library.builder()
                .groupId("org.jetbrains.kotlin")
                .artifactId("kotlin-stdlib")
                .version(kotlinStdLibVersion)
                .build();
        loader.loadLibrary(library);

        logger.info("Loading library OneTime v" + atlassianVersion);
        library = Library.builder()
                .groupId("com.atlassian")
                .artifactId("onetime")
                .version(atlassianVersion)
                .build();
        loader.loadLibrary(library);
    }
}