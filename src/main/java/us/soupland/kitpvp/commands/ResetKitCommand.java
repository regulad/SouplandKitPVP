package us.soupland.kitpvp.commands;

import us.soupland.kitpvp.enums.PlayerState;
import us.soupland.kitpvp.profile.Profile;
import us.soupland.kitpvp.profile.ProfileManager;
import us.soupland.kitpvp.utilities.KitPvPUtils;
import us.soupland.kitpvp.utilities.chat.ColorText;
import us.soupland.kitpvp.utilities.command.KitPvPCommand;
import us.soupland.kitpvp.utilities.player.PlayerUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ResetKitCommand extends KitPvPCommand {

    public ResetKitCommand() {
        super("resetkit", null, "clearkit");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(KitPvPUtils.ONLY_PLAYERS);
            return false;
        }

        if (args.length < 1) {
            sender.sendMessage(ChatColor.YELLOW + "Use /resetkit {name}");
            return false;
        }

        Player player = (Player) sender;
        Profile profile = ProfileManager.getProfile(player);
        if (profile.getPlayerState() == PlayerState.SPAWN) {
            PlayerUtils.resetPlayer(player, false, false);
            player.sendMessage(ColorText.translate("&eYour kit has been reset."));
        } else {
            player.sendMessage(ColorText.translate("&cYou can only execute this command in spawn."));
        }

        return true;
    }
}