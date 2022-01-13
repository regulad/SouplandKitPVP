package us.soupland.kitpvp.games.arenas;

import lombok.Getter;
import us.soupland.kitpvp.KitPvP;
import org.bukkit.Location;
import us.soupland.kitpvp.utilities.configuration.Config;
import us.soupland.kitpvp.utilities.location.LocationUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameMapHandler {

    @Getter
    private Map<String, GameMap> gameMap;

    private Config config;

    public GameMapHandler(KitPvP plugin) {
        this.gameMap = new HashMap<>();
        this.config = new Config(plugin, "gamemap.yml");
        this.loadGameMaps();
    }

    public GameMap getGameMap(String gameName) {
        return this.getGameMap().get(gameName);
    }

    public GameMap createGameMap(String gameName) {
        GameMap arena = new GameMap(gameName, new ArrayList<>());
        this.gameMap.put(gameName, arena);
        return arena;
    }

    public void destroyGameMap(String gameName) {
        this.gameMap.remove(gameName);
    }

    private void loadGameMaps() {
        if (config.getConfigurationSection("games") != null) {
            for (String gameName : config.getConfigurationSection("games").getKeys(false)) {
                List<String> serializedLocations = config.getStringList("games." + gameName + ".locations");

                List<Location> locations = new ArrayList<>();

                try {
                    for (String serializedLocation : serializedLocations) {
                        locations.add(LocationUtils.getLocation(serializedLocation));
                        System.out.println(serializedLocation);
                    }
                } catch (Exception ignored) {
                    System.out.println("[GAMES] ARENA " + gameName + " COULD NOT BE LOADED.");
                    continue;
                }
                GameMap arena = new GameMap(gameName, locations);

                this.gameMap.put(gameName, arena);
            }
        }
    }

    public void saveGameMaps() {
        config.set("games", null);
        for (Map.Entry<String, GameMap> arenaEntry : this.gameMap.entrySet()) {
            String gameName = arenaEntry.getKey();
            GameMap arena = arenaEntry.getValue();

            List<String> serializedLocations = new ArrayList<>();

            for (Location location : arena.getLocations()) {
                if (location == null) continue;
                serializedLocations.add(LocationUtils.getString(location));
            }
            config.set("games." + gameName + ".locations", serializedLocations);
        }
        config.save();
        System.out.println("saved " + getGameMap().size());
    }


}
