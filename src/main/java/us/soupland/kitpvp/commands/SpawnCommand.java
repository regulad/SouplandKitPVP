package us.soupland.kitpvp.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.soupland.kitpvp.KitPvP;
import us.soupland.kitpvp.enums.PlayerState;
import us.soupland.kitpvp.profile.Profile;
import us.soupland.kitpvp.profile.ProfileManager;
import us.soupland.kitpvp.utilities.KitPvPUtils;
import us.soupland.kitpvp.utilities.chat.ColorText;
import us.soupland.kitpvp.utilities.command.KitPvPCommand;
import us.soupland.kitpvp.utilities.cooldown.Cooldown;
import us.soupland.kitpvp.utilities.player.PlayerUtils;

public class SpawnCommand extends KitPvPCommand {

    public SpawnCommand() {
        super("spawn", null, "resetkit", "clearkit");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(KitPvPUtils.ONLY_PLAYERS);
            return false;
        }
        Player player = (Player) sender;
        Profile profile = ProfileManager.getProfile(player);
        if (profile.getPlayerState() == PlayerState.SPAWN) {
            PlayerUtils.resetPlayer(player, false, true);
            return false;
        }
        if (profile.getPlayerCombat() > 0L) {
            player.sendMessage(ColorText.translate("&cYou cannot teleport while you are combat tagged."));
            return false;
        }
        if (profile.getPlayerState() != PlayerState.PLAYING) {
            player.sendMessage(ColorText.translate("&cYou cannot be teleported."));
            return false;
        }
        Cooldown cooldown = KitPvP.getCooldown("SpawnTimer");
        if (cooldown.isOnCooldown(player)) {
            player.sendMessage(ColorText.translate("&cPlease be patient!"));
            return false;
        }
        player.sendMessage(ColorText.translate("&eYou will be &ateleported &ein &98 seconds&e."));
        player.sendMessage(ColorText.translate("&eDon't move or your teleport will be cancelled."));
        cooldown.setCooldown(player, true);
        return true;
    }
}