package us.soupland.kitpvp.sidebar.scoreboard.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import us.soupland.kitpvp.KitPvP;
import us.soupland.kitpvp.sidebar.scoreboard.Aridi;
import us.soupland.kitpvp.sidebar.scoreboard.AridiManager;
import us.soupland.kitpvp.sidebar.scoreboard.events.PlayerLoadScoreboardEvent;
import us.soupland.kitpvp.sidebar.scoreboard.events.PlayerUnloadScoreboardEvent;

public class AridiListener implements Listener {

    private KitPvP plugin = KitPvP.getInstance();
    private AridiManager aridiManager = plugin.getAridiManager();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (aridiManager != null) {
            Aridi aridi = new Aridi(player, aridiManager);
            aridiManager.getAridiMap().put(player.getUniqueId(), aridi);
            new PlayerLoadScoreboardEvent(player, aridi).call();
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (aridiManager != null && aridiManager.getAridiMap().containsKey(player.getUniqueId())) {
            new PlayerUnloadScoreboardEvent(player, aridiManager.getAridiMap().get(player.getUniqueId())).call();
            aridiManager.getAridiMap().remove(player.getUniqueId());
        }
    }
}