package us.soupland.kitpvp.listener.handler;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import us.soupland.kitpvp.KitPvP;

public class ListenerHandler {

    public static void registerListeners(Listener... listeners) {
        for (Listener listener : listeners) {
            Bukkit.getPluginManager().registerEvents(listener, KitPvP.getInstance());
        }
    }
}