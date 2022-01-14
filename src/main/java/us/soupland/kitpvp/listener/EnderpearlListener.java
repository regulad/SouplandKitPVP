package us.soupland.kitpvp.listener;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import us.soupland.kitpvp.KitPvP;
import us.soupland.kitpvp.enums.PlayerState;
import us.soupland.kitpvp.profile.Profile;
import us.soupland.kitpvp.profile.ProfileManager;
import us.soupland.kitpvp.utilities.chat.ColorText;
import us.soupland.kitpvp.utilities.cooldown.Cooldown;
import us.soupland.kitpvp.utilities.player.DurationFormatter;
import us.soupland.kitpvp.utilities.time.TimeUtils;

public class EnderpearlListener implements Listener {

    public EnderpearlListener() {
        new Cooldown("Enderpearl", TimeUtils.parse("16s"), "&eEnderpearl", "&aYour &e&lEnderpearl &acooldown has expired.\n&aYou may now use an &e&lEnderpearl&a!");
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction().name().startsWith("RIGHT")) {
            Player player = event.getPlayer();
            if (player.getGameMode() == GameMode.CREATIVE) {
                return;
            }
            if (player.getItemInHand() != null && player.getItemInHand().getType() == Material.ENDER_PEARL) {
                Profile profile = ProfileManager.getProfile(player);
                if (profile.getPlayerState() == PlayerState.PLAYING || profile.getPlayerState() == PlayerState.FIGHTINGPRACTICE || profile.getPlayerState() == PlayerState.INGAME) {
                    Cooldown cooldown = KitPvP.getCooldown("Enderpearl");
                    if (cooldown.getDuration(player) > 0L) {
                        event.setUseItemInHand(Event.Result.DENY);
                        player.updateInventory();
                        player.sendMessage(ColorText.translate("&eEnderpearl Cooldown: &c" + DurationFormatter.getRemaining(cooldown.getDuration(player), true)));
                        return;
                    }
                    cooldown.setCooldown(player, true);
                }
            }
        }
    }

}