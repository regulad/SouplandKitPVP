package us.soupland.kitpvp.practice.listener;

import us.soupland.kitpvp.enums.PlayerState;
import us.soupland.kitpvp.profile.Profile;
import us.soupland.kitpvp.profile.ProfileManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

public class SpawnListener implements Listener {

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (event.getRightClicked() instanceof Player) {
            Player player = event.getPlayer();
            Profile profile = ProfileManager.getProfile(player);
            if (profile.getPlayerState() != PlayerState.SPAWNPRACTICE) {
                return;
            }
            ItemStack itemInHand = player.getItemInHand();
            if (itemInHand == null) {
                return;
            }
            if (itemInHand.getType() == Material.BLAZE_ROD) {
                player.performCommand("duel " + event.getRightClicked().getName());
            }
        }
    }
}