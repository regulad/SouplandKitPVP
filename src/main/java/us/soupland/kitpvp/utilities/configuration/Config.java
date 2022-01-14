package us.soupland.kitpvp.utilities.configuration;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class Config extends YamlConfiguration {
    private final String fileName;
    private final JavaPlugin javaPlugin;
    private final File file;

    public Config(JavaPlugin javaPlugin, String fileName) {
        this.javaPlugin = javaPlugin;
        this.fileName = fileName;
        File folder = javaPlugin.getDataFolder();
        this.file = new File(folder, fileName);
        createNewFile();
    }

    private void createNewFile() {
        try (final InputStream inputStream = javaPlugin.getResource(fileName)) {
            if (!file.exists() && inputStream != null && inputStream.available() != 0) {
                javaPlugin.saveResource(fileName, false);
            }
            load(file);
        } catch (IOException | InvalidConfigurationException exception) {
            exception.printStackTrace();
        }
    }

    public void save() {
        try {
            save(this.file);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}