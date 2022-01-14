package us.soupland.kitpvp.listener;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import us.soupland.kitpvp.enums.PlayerState;
import us.soupland.kitpvp.profile.Profile;
import us.soupland.kitpvp.profile.ProfileManager;

public class ParticleListener implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();
        if (from.getBlockX() == to.getBlockX() && from.getBlockY() == to.getBlockY() && from.getBlockZ() == to.getBlockZ()) {
            return;
        }
        Player player = event.getPlayer();
        Profile profile = ProfileManager.getProfile(player);
        if (profile.getPlayerState() == PlayerState.SPAWN || profile.getPlayerState() == PlayerState.PLAYING || profile.getPlayerState() == PlayerState.SPAWNPRACTICE) {
            if (profile.getCurrentParticle() != null) {
                Location location = event.getTo();
                try {
                    for (Player online : Bukkit.getOnlinePlayers()) {
                        profile.getCurrentParticle().sendToPlayer(online, location, 0, 0, 0, 10, 20);
                    }
                } catch (Exception ignored) {
                }
            }
        }
    }
}