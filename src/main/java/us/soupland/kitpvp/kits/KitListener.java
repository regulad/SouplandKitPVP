package us.soupland.kitpvp.kits;

import us.soupland.kitpvp.profile.Profile;
import us.soupland.kitpvp.profile.ProfileManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import us.soupland.kitpvp.utilities.chat.ColorText;

public class KitListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Profile profile = ProfileManager.getProfile(player);
        Kit kit = profile.getCurrentKit();
        if (kit != null) {
            if (profile.isFrozenToUseAbility()) {
                player.sendMessage(ColorText.translate("&cYou are currently jammed, so you can not use your ability."));
                return;
            }
            kit.execute(event);
        }
    }
}