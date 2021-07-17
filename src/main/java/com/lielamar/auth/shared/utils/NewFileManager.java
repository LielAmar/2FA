package com.lielamar.auth.shared.utils;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class NewFileManager {

    private static final String HEADER_SEPARATOR = "# ------------------------------------------------ #";
    private static final String COMMENT_LINE = "CONFIG_COMMENT_";

    private final JavaPlugin plugin;
    private final Map<String, Config> configs;

    public NewFileManager(@NotNull JavaPlugin plugin) {
        this.plugin = plugin;
        this.configs = new HashMap<>();
    }


    public @Nullable Config getConfig(@NotNull String fileName) {
        return this.getConfig(this.plugin.getDataFolder().getPath(), fileName, null);
    }

    /**
     * Returns a Config object representing a single configuration
     *
     * @param path       Path to the config file
     * @param fileName   Name of the config file
     * @param header     Header to set to the file if it didn't exist
     * @return           Config object representing the given config
     */
    public @Nullable Config getConfig(@NotNull String path, @NotNull String fileName, @Nullable String[] header) {
        fileName = this.fixConfigName(fileName);

        Config config = this.configs.get(fileName);
        if(config != null)
            return config;

        File configFile = this.getConfigFile(path, fileName);

        if(configFile == null)
            return null;

        // If the config doesn't already exist, we want to create it, copy the resource and set it's header
        if(!configFile.exists())
            this.createFile(configFile, this.plugin.getResource(fileName));

//            config = new Config(configFile);
//            this.setHeader(config, header);
//        } else
        config = new Config(configFile);

        this.configs.put(fileName, config);
        return config;
    }


    /**
     * Returns the File object of a config file
     *
     * @param path       Path to the config file
     * @param fileName   Name of the config file
     * @return           File object of the given config
     */
    private @Nullable File getConfigFile(@NotNull String path, @NotNull String fileName) {
        if(fileName.isEmpty())
            return null;

        if(path.isEmpty())
            return new File(this.plugin.getDataFolder() + File.separator + fileName);

        if(!path.endsWith("/"))
            path += File.separator;

        return new File(path + fileName);
    }

    /**
     * Creates a File object for a config file
     *
     * @param configFile   File to create
     * @param source       Optional source to apply to the config
     */
    private void createFile(@NotNull File configFile, @Nullable InputStream source) {
        if(configFile.exists())
            return;

        try {
            boolean createdDir  = configFile.getParentFile().mkdir();
            boolean createdFile = configFile.createNewFile();

            if(source != null)
                this.loadSource(configFile, source);

        } catch(IOException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Loads a source stream to a file
     *
     * @param configFile   File to load the source to
     * @param source       Source to load
     */
    private void loadSource(@NotNull File configFile, @NotNull InputStream source) {
        if(!configFile.exists())
            return;

        try {
            OutputStream out = new FileOutputStream(configFile);

            int length;
            byte[] buffer = new byte[1024];

            while((length = source.read(buffer)) > 0)
                out.write(buffer, 0, length);

            out.close();
            source.close();
        } catch(IOException exception) {
            exception.printStackTrace();
        }
    }


    /**
     * Fixes a config name -> turns to lowercase & adds .yml in the end
     *
     * @param fileName   Name to fix
     * @return           Fixed name
     */
    private @NotNull String fixConfigName(@NotNull String fileName) {
        if(!fileName.toLowerCase().endsWith(".yml"))
            fileName += ".yml";

        return fileName.toLowerCase();
    }


    public static class Config {

        private final File configFile;

        private YamlConfiguration configuration;
        private int comments;

        public Config(@NotNull File configFile) {
            this.configFile = configFile;

            this.loadConfig();
        }


        private void loadConfig() {
            if(!this.configFile.exists())
                return;

            try {
                this.comments = 0;

                String addLine;
                String currentLine;

                StringBuilder builder = new StringBuilder();
                BufferedReader reader = new BufferedReader(new FileReader(this.configFile));

                // Looping through all lines
                // If the current line is a comment we want to replace the "#" with a path, to make it a normal value
                // so bukkit loads the configuration successfully
                while((currentLine = reader.readLine()) != null) {
                    if(currentLine.startsWith("#")) {
                        addLine = currentLine.replaceAll("\"", "'").replaceFirst("#", COMMENT_LINE + this.comments++ + ": \"") + "\"";
                        System.out.println("addline: " + addLine);
                        builder.append(addLine).append("\n");
                    } else
                        builder.append(currentLine).append("\n");
                }

                reader.close();

                Reader inputString = new StringReader(builder.toString());
                reader = new BufferedReader(inputString);

                System.out.println("builer: " + builder.toString());

                this.configuration = YamlConfiguration.loadConfiguration(reader);
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        public @Nullable Object get(@NotNull String path)                                    { return this.configuration.get(path); }
        public @Nullable Object get(@NotNull String path, @NotNull Object defaultValue)       { return this.configuration.get(path, defaultValue); }

        public @Nullable String getString(@NotNull String path)                              { return this.configuration.getString(path); }
        public @Nullable String getString(@NotNull String path, @NotNull String defaultValue) { return this.configuration.getString(path, defaultValue); }

        public int getInt(@NotNull String path)                                              { return this.configuration.getInt(path); }
        public int getInt(@NotNull String path, int defaultValue)                            { return this.configuration.getInt(path, defaultValue); }

        public double getDouble(@NotNull String path)                                        { return this.configuration.getDouble(path); }
        public double getDouble(@NotNull String path, double defaultValue)                   { return this.configuration.getDouble(path, defaultValue); }

        public boolean getBoolean(@NotNull String path)                                      { return this.configuration.getBoolean(path); }
        public boolean getBoolean(@NotNull String path, boolean defaultValue)                { return this.configuration.getBoolean(path, defaultValue); }

        public @Nullable List<?> getList(@NotNull String path)                               { return this.configuration.getList(path); }
        public @Nullable List<?> getList(@NotNull String path, @NotNull List<?> defaultValue) { return this.configuration.getList(path, defaultValue); }

        public @NotNull List<String> getStringList(@NotNull String path)                     { return this.configuration.getStringList(path); }
        public @NotNull List<Integer> getIntegerList(@NotNull String path)                   { return this.configuration.getIntegerList(path); }

        public @Nullable Set<String> getKeys()                                               { return this.configuration.getKeys(false); }

        public @NotNull ConfigurationSection createSection(@NotNull String path)             { return this.configuration.createSection(path); }
        public @Nullable ConfigurationSection getConfigurationSection(@NotNull String path)  { return this.configuration.getConfigurationSection(path); }

        public boolean contains(@NotNull String path)                                        { return this.configuration.contains(path); }
        public void removeKey(@NotNull String path)                                          { this.configuration.set(path, null); }
        public void set(@NotNull String path, @Nullable Object value)                        { this.configuration.set(path, value); }


        /**
         * Sets a value in the config with a comment above it
         *
         * @param path      Path of the value
         * @param value     Value to set
         * @param comment   Comment to add
         */
        public void set(@NotNull String path, @NotNull Object value, String comment) {
            if(!this.configuration.contains(path))
                this.configuration.set(COMMENT_LINE + this.comments++, comment);

            this.configuration.set(path, value);
        }

        /**
         * Sets a value in the config with comments above it
         *
         * @param path       Path of the value
         * @param value      Value to set
         * @param comments   Comments to add
         */
        public void set(@NotNull String path, @NotNull Object value, String[] comments) {
            for(String comment : comments) {
                if(!this.configuration.contains(path))
                    this.configuration.set(COMMENT_LINE + this.comments++, comment);
            }

            this.configuration.set(path, value);
        }


        /**
         * Reloads the config from the saved file on the disk
         */
        public void reloadConfig() {
            this.loadConfig();
        }


        public void saveConfig() {
            this.saveConfig(this.getConfigAsString());
        }

        /**
         * Saves the config:
         *   - Gets the config string from the YamlConfiguration object
         *   - Applies comments to the config string
         *   - saves it to the config file
         *
         * @param configString string to save to the config
         */
        private void saveConfig(String configString) {
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(this.configFile));
                writer.write(configString);
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * Returns the config as string
         *
         * @return   String value of the config, including comments
         */
        private String getConfigAsString() {
            String configString = this.configuration.saveToString();
            return this.applyComments(configString);
        }

        /**
         * Gets the config string and edits it to apply comments
         *
         * @param configString   Original configuration string with comments set as normal values
         * @return               Edited configuration string with comments set as normal comments
         */
        private String applyComments(String configString) {
//            int lastLine = 0;
            int headerLine = 0;

            String[] lines = configString.split("\n");
            StringBuilder config = new StringBuilder("");

            for(String line : lines) {
                if(line.startsWith(COMMENT_LINE)) {
                    String comment = "#" + line.trim().substring(line.indexOf(":") + 1);

                    if(comment.equals(HEADER_SEPARATOR)) {
                        if(headerLine == 0) { // Beginning of the header
                            config.append(comment).append("\n");

//                            lastLine = 0;
                            headerLine = 1;
                        } else { // Ending of the header
                            config.append(comment).append("\n\n");

//                            lastLine = 0;
                            headerLine = 0;
                        }
                    } else {
                        System.out.println("checking comment: " + comment);
                        if(comment.startsWith("# '")) {
                            System.out.println("replacing1");
                            comment = comment.substring(0, comment.length() - 1).replaceFirst(" '", "");
                        }

                        config.append(comment).append("\n");
//                        lastLine = 0;
                    }
                } else {
                    System.out.println("not a comment line: " + line);
                    config.append(line).append("\n").append("\n");
//                    lastLine = 1;
                }
            }

            return config.toString();
        }
    }
}
