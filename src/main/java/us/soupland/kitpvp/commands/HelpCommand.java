package us.soupland.kitpvp.commands;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.soupland.kitpvp.enums.KitMenuType;
import us.soupland.kitpvp.enums.Theme;
import us.soupland.kitpvp.profile.ProfileManager;
import us.soupland.kitpvp.utilities.KitPvPUtils;
import us.soupland.kitpvp.utilities.chat.ColorText;
import us.soupland.kitpvp.utilities.command.KitPvPCommand;

public class HelpCommand extends KitPvPCommand {

    public HelpCommand() {
        super("help", null, "?");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(KitPvPUtils.ONLY_PLAYERS);
            return false;
        }
        Player player = (Player) sender;
        Theme theme = ProfileManager.getProfile(player).getTheme();
        sender.sendMessage(ColorText.translateAmpersand(theme.getPrimaryColor() + StringUtils.repeat("=", 3) + " Commands " + StringUtils.repeat("=", 3)));
        sender.sendMessage(ColorText.translateAmpersand(theme.getPrimaryColor() + "/hub - Go back to the lobby"));
        sender.sendMessage(ColorText.translateAmpersand(theme.getPrimaryColor() + "/spawn - Go back to the main world spawn."));
        if (ProfileManager.getProfile(player).getKitMenuType() == KitMenuType.GUI) {
            sender.sendMessage(ColorText.translateAmpersand(theme.getPrimaryColor() + "/kit - Open the Kit Selector. "));
        } else {
            sender.sendMessage(ColorText.translateAmpersand(theme.getPrimaryColor() + "/kit <kitName> - Legacy Kit Selection."));
        }
        sender.sendMessage(ColorText.translateAmpersand(theme.getPrimaryColor() + "/stats <playerName> - See a player's stats."));
        sender.sendMessage(ColorText.translateAmpersand(theme.getPrimaryColor() + "/shop - Buy events, kits, and more..."));
        return true;
    }
}
