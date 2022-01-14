package us.soupland.kitpvp.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.soupland.kitpvp.KitPvP;
import us.soupland.kitpvp.server.ServerData;
import us.soupland.kitpvp.utilities.KitPvPUtils;
import us.soupland.kitpvp.utilities.chat.ColorText;
import us.soupland.kitpvp.utilities.command.KitPvPCommand;
import us.soupland.kitpvp.utilities.location.LocationUtils;

public class SetSpawnCommand extends KitPvPCommand {

    public SetSpawnCommand() {
        super("setspawn");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(KitPvPUtils.ONLY_PLAYERS);
            return false;
        }
        Player player = (Player) sender;
        ServerData serverData = KitPvP.getInstance().getServerData();
        if (args.length < 1) {
            player.sendMessage(ColorText.translate("&cUsage: /" + label + " <main|practice|events>"));
        } else {
            String stringLocation = LocationUtils.getString(player.getLocation());
            if (args[0].equalsIgnoreCase("main")) {
                serverData.setSpawn(stringLocation);
            } else if (args[0].equalsIgnoreCase("practice")) {
                serverData.setSpawnPractice(stringLocation);
            } else if (args[0].equalsIgnoreCase("events")) {
                serverData.setSpawnEvents(stringLocation);
            } else {
                player.sendMessage(ColorText.translate("&cUsage: /" + label + " <main|practice|events>"));
                return false;
            }
            player.sendMessage(ColorText.translate("&a&l>&b&l>&c&l>&d&l>&e&l>&f&l> &aLocation modified."));
        }
        return true;
    }
}