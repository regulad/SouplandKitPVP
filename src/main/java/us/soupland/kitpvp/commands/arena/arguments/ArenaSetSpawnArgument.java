package us.soupland.kitpvp.commands.arena.arguments;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.soupland.kitpvp.practice.arena.Arena;
import us.soupland.kitpvp.practice.arena.ArenaHandler;
import us.soupland.kitpvp.utilities.chat.ColorText;
import us.soupland.kitpvp.utilities.command.KitPvPArgument;

public class ArenaSetSpawnArgument extends KitPvPArgument {

    public ArenaSetSpawnArgument() {
        super("setspawn");
        onlyplayers = true;
    }

    @Override
    public String getUsage(String s) {
        return '/' + s + ' ' + name + " <arenaName> <first|second>";
    }

    @Override
    public void onExecute(CommandSender sender, String label, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(ColorText.translate("&cUsage: " + getUsage(label)));
        } else {
            Arena arena = ArenaHandler.getByName(args[1]);
            if (arena == null) {
                sender.sendMessage(ColorText.translate("&cAn arena with that name doesn't exists."));
                return;
            }
            Location location = ((Player) sender).getLocation();
            if (args[2].equalsIgnoreCase("first")) {
                arena.setFirstPosition(location);
            } else if (args[2].equalsIgnoreCase("second")) {
                arena.setSecondPosition(location);
            } else {
                sender.sendMessage(ColorText.translate("&cUsage: " + getUsage(label)));
                return;
            }
            sender.sendMessage(ColorText.translate("&ePlease perform /" + label + " save " + arena.getName()));
        }
    }
}