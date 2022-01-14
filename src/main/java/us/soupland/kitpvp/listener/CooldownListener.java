package us.soupland.kitpvp.listener;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import us.soupland.kitpvp.KitPvP;
import us.soupland.kitpvp.enums.PlayerState;
import us.soupland.kitpvp.profile.Profile;
import us.soupland.kitpvp.profile.ProfileManager;
import us.soupland.kitpvp.utilities.cooldown.CooldownExpiredEvent;
import us.soupland.kitpvp.utilities.task.TaskUtil;

public class CooldownListener implements Listener {

    @EventHandler
    public void onCooldownExpired(CooldownExpiredEvent event) {
        if (event.isForced()) {
            event.getPlayer().setLevel(0);
            return;
        }
        Player player = event.getPlayer();
        Profile profile = ProfileManager.getProfile(player);
        if (event.getCooldown() == KitPvP.getCooldown("SpawnTimer")) {
            Location location = KitPvP.getInstance().getServerData().getSpawnLocation();
            if (location != null) {
                player.teleport(location);
            }
            profile.setFell(false);
            player.setAllowFlight(false);
            player.setFlying(false);
            TaskUtil.runTask(() -> profile.setPlayerState(PlayerState.SPAWN));
        }
        if (event.getCooldown() == KitPvP.getCooldown("FlyExpire")) {
            player.setAllowFlight(false);
            player.setFlying(false);
        }
    }
}