package us.soupland.kitpvp.commands;

import us.soupland.kitpvp.KitPvP;
import us.soupland.kitpvp.enums.PlayerItem;
import us.soupland.kitpvp.enums.PlayerState;
import us.soupland.kitpvp.profile.Profile;
import us.soupland.kitpvp.profile.ProfileManager;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import us.soupland.kitpvp.utilities.KitPvPUtils;
import us.soupland.kitpvp.utilities.command.KitPvPCommand;
import us.soupland.kitpvp.utilities.location.LocationUtils;

public class OneVsOneCommand extends KitPvPCommand {

    public OneVsOneCommand() {
        super("onevsone");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(KitPvPUtils.ONLY_PLAYERS);
            return false;
        }
        Player player = (Player) sender;
        Profile profile = ProfileManager.getProfile(player);

        if (profile.getPlayerState() != PlayerState.SPAWN) {
            return false;
        }

        profile.setPlayerState(PlayerState.SPAWNPRACTICE);

        PlayerInventory inventory = player.getInventory();

        inventory.clear();
        inventory.setArmorContents(null);

        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }

        Location location = LocationUtils.getLocation(KitPvP.getInstance().getServerData().getSpawnPractice());

        if (location != null) {
            player.teleport(location);
        }

        inventory.setItem(0, PlayerItem.SPAWN_PRACTICE_CUSTOM_DUEL.getItem());
        inventory.setItem(8, PlayerItem.SPAWN_PRACTICE_RETURN_SPAWN.getItem());

        player.updateInventory();
        return true;
    }
}