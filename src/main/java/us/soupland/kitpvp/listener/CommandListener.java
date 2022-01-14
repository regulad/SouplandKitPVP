package us.soupland.kitpvp.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import us.soupland.kitpvp.enums.ServerTime;
import us.soupland.kitpvp.profile.Profile;
import us.soupland.kitpvp.profile.ProfileManager;
import us.soupland.kitpvp.utilities.chat.ChatUtil;
import us.soupland.kitpvp.utilities.chat.ColorText;
import us.soupland.kitpvp.utilities.location.LocationUtils;

public class CommandListener implements Listener {

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        String command = event.getMessage().split(" ")[0];

        Player player = event.getPlayer();
        Profile profile = ProfileManager.getProfile(player);
        if (command.equalsIgnoreCase("/leave")) {
            event.setCancelled(true);
            player.performCommand("game leave");
        } else if (command.equalsIgnoreCase("/day")) {
            event.setCancelled(true);
            profile.setServerTime(ServerTime.DAY);
            player.setPlayerTime(15000L, true);
            player.sendMessage(ColorText.translate("&7You have changed your server time to &fDay&7."));
        } else if (command.equalsIgnoreCase("/night")) {
            event.setCancelled(true);
            profile.setServerTime(ServerTime.NIGHT);
            player.setPlayerTime(6000L, true);
            player.sendMessage(ColorText.translate("&7You have changed your server time to &fNight&7."));
        } else if (command.equalsIgnoreCase("/mylocation")) {
            if (player.isOp()) {
                event.setCancelled(true);
                new ChatUtil("").copy("&a&lYour location has been encrypted. &d[Click here to copy]", "&7Click here to copy your location encrypted.", LocationUtils.getString(player.getLocation())).send(player);
            }
        }
    }
}