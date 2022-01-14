package us.soupland.kitpvp.commands.arena.arguments;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.soupland.kitpvp.practice.arena.Arena;
import us.soupland.kitpvp.practice.arena.ArenaHandler;
import us.soupland.kitpvp.utilities.chat.ColorText;
import us.soupland.kitpvp.utilities.command.KitPvPArgument;

public class ArenaTeleportArgument extends KitPvPArgument {

    public ArenaTeleportArgument() {
        super("teleport", null, null, "tp");
        onlyplayers = true;
    }

    @Override
    public String getUsage(String s) {
        return '/' + s + ' ' + name + " <arenaName>";
    }

    @Override
    public void onExecute(CommandSender sender, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ColorText.translate("&cUsage: " + getUsage(label)));
        } else {
            Arena arena = ArenaHandler.getByName(args[1]);
            if (arena == null) {
                sender.sendMessage(ColorText.translate("&cAn arena with that name doesn't exists."));
                return;
            }
            Location location = arena.getFirstPosition();
            if (location == null) {
                location = arena.getSecondPosition();
            }

            if (location == null) {
                sender.sendMessage(ColorText.translate("&cTeleport failed!"));
                return;
            }

            if (((Player) sender).teleport(location)) {
                sender.sendMessage(ColorText.translate("&eTeleported..."));
            } else {
                sender.sendMessage(ColorText.translate("&cTeleport failed!"));
            }
        }
    }
}