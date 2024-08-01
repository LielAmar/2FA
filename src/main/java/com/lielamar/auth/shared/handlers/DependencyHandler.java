package com.lielamar.auth.shared.handlers;

import net.byteflux.libby.Library;
import net.byteflux.libby.LibraryManager;

import java.lang.reflect.InaccessibleObjectException;
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
        } catch (InaccessibleObjectException exception) {
            logger.severe("2FA detected that you might be using Java 16 without the --add-opens java.base/java.lang=ALL-UNNAMED or the --add-opens java.base/java.net=ALL-UNNAMED flags!");
            logger.severe("If you want the plugin to support all features, most significantly Remote Databases, please add this flag to your startup script");
            logger.severe("In case that the issue persists please contact the developers via Discord or Github Issues using '/2fa report' file.");
        } catch (Exception exception) {
            logger.severe("An error has occurred while loading the dependencies.");
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