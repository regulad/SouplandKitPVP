package us.soupland.kitpvp.practice;

import lombok.Getter;
import us.soupland.kitpvp.listener.handler.ListenerHandler;
import us.soupland.kitpvp.practice.kit.Kit;
import us.soupland.kitpvp.practice.listener.ArenaListener;
import us.soupland.kitpvp.practice.listener.EntityListener;
import us.soupland.kitpvp.practice.listener.PlayerListener;
import us.soupland.kitpvp.practice.listener.SpawnListener;

import java.util.HashMap;
import java.util.Map;

public class PracticeHandler {

    @Getter
    private static Map<String, Kit> kitMap = new HashMap<>();

    public PracticeHandler() {
        ListenerHandler.registerListeners(new SpawnListener(), new ArenaListener(), new EntityListener(), new PlayerListener());
    }

    public void registerKit(Kit... kits) {
        for (Kit kit : kits) {
            if (!kitMap.containsKey(kit.getName())) {
                kitMap.put(kit.getName(), kit);
            }
        }
    }

}