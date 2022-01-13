package us.soupland.kitpvp.listener.handler;

import us.soupland.kitpvp.KitPvP;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

public class ListenerHandler {

    public static void registerListeners(Listener... listeners) {
        for (Listener listener : listeners) {
            Bukkit.getPluginManager().registerEvents(listener, KitPvP.getInstance());
        }
    }
}