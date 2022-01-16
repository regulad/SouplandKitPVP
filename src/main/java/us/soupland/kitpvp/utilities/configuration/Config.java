package us.soupland.kitpvp.utilities.configuration;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class Config extends YamlConfiguration {
    private final String fileName;
    private final Plugin plugin;
    private final File file;

    public Config(Plugin plugin, String fileName) {
        this.plugin = plugin;
        this.fileName = fileName;
        this.file = new File(plugin.getDataFolder(), fileName);
        saveAndLoad();
    }

    private void saveAndLoad() {
        try (final InputStream inputStream = plugin.getResource(fileName)) {
            if (!file.exists() && inputStream != null && inputStream.available() != 0) {
                plugin.saveResource(fileName, false);
            }
            load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        try {
            save(this.file);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public void load() {
        try {
            load(file);
        } catch (IOException | InvalidConfigurationException exception) {
            exception.printStackTrace();
        }
    }
}