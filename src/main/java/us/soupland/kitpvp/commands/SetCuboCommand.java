package us.soupland.kitpvp.commands;

import us.soupland.kitpvp.KitPvP;
import us.soupland.kitpvp.server.ServerData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.soupland.kitpvp.utilities.KitPvPUtils;
import us.soupland.kitpvp.utilities.chat.ColorText;
import us.soupland.kitpvp.utilities.command.KitPvPCommand;
import us.soupland.kitpvp.utilities.location.LocationUtils;

public class SetCuboCommand extends KitPvPCommand {

    public SetCuboCommand() {
        super("setcubo", "", "setcuboid");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(KitPvPUtils.ONLY_PLAYERS);
            return false;
        }
        Player player = (Player) sender;
        if (args.length < 1) {
            player.sendMessage(ColorText.translate("&cUsage: /" + label + " <first|second|firstKoth|secondKoth>"));
        } else {
            ServerData serverData = KitPvP.getInstance().getServerData();
            if (args[0].equalsIgnoreCase("first")) {
                serverData.setFirstCI(LocationUtils.getString(player.getLocation()));
                player.sendMessage(ColorText.translate("&6&l>&e&l> &aFirst position has been updated. &e&l<&6&l<"));
            } else if (args[0].equalsIgnoreCase("second")) {
                serverData.setSecondCI(LocationUtils.getString(player.getLocation()));
                player.sendMessage(ColorText.translate("&6&l>&e&l> &aSecond position has been updated. &e&l<&6&l<"));
            } else if (args[0].equalsIgnoreCase("firstKoth")) {
                serverData.setFirstKoth(LocationUtils.getString(player.getLocation()));
                player.sendMessage(ColorText.translate("&6&l>&e&l> &aFirst position has been updated. &e&l<&6&l<"));
            } else if (args[0].equalsIgnoreCase("secondKoth")) {
                serverData.setSecondKoth(LocationUtils.getString(player.getLocation()));
                player.sendMessage(ColorText.translate("&6&l>&e&l> &aSecond position has been updated. &e&l<&6&l<"));
            } else {
                player.sendMessage(ColorText.translate("&cUsage: /" + label + " <first|second|firstKoth|secondKoth>"));
                return false;
            }
            serverData.saveServer();
        }
        return true;
    }
}