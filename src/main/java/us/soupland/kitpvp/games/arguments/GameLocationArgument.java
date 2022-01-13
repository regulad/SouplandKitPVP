package us.soupland.kitpvp.games.arguments;

import com.google.common.primitives.Ints;
import us.soupland.kitpvp.KitPvP;
import us.soupland.kitpvp.games.arenas.GameMap;
import us.soupland.kitpvp.games.arenas.GameMapHandler;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.soupland.kitpvp.utilities.KitPvPUtils;
import us.soupland.kitpvp.utilities.chat.ChatUtil;
import us.soupland.kitpvp.utilities.chat.ColorText;
import us.soupland.kitpvp.utilities.command.KitPvPArgument;

import java.util.HashMap;

public class GameLocationArgument extends KitPvPArgument {

    public GameLocationArgument() {
        super("location", null, KitPvPUtils.PERMISSION + "games.location");
        onlyplayers = true;
    }

    @Override
    public String getUsage(String s) {
        return '/' + s + ' ' + name + " <add|list|clear|save> <eventName>";
    }

    @Override
    public void onExecute(CommandSender sender, String label, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(ColorText.translate("&cUsage: " + getUsage(label)));
        } else {
            Location location = ((Player) sender).getLocation();

            GameMapHandler gameMapHandler = KitPvP.getInstance().getGameMapHandler();

            GameMap gameMap = gameMapHandler.getGameMap(args[2].toLowerCase());
            if (args[1].equalsIgnoreCase("add")) {
                if (gameMap == null) {
                    gameMapHandler.createGameMap(args[2].toLowerCase());
                    sender.sendMessage(ColorText.translate("&cMap could not be found. &a&lCreating..."));
                    return;
                }
                gameMap.getLocations().add(location);
                sender.sendMessage(ColorText.translate("&aYour location has been successfully &lADDED&a."));
                gameMapHandler.saveGameMaps();
                sender.sendMessage(ColorText.translate("&c> GameMapHander:saveGameMaps successfully saved..."));
            } else if (args[1].equalsIgnoreCase("list")) {
                if (gameMapHandler.getGameMap(args[2].toLowerCase()) == null || gameMap.getLocations().isEmpty()) {
                    sender.sendMessage(ColorText.translate("&cNo locations provided for this events."));
                } else {
                    sender.sendMessage(ColorText.translate("&aLocations for &f" + gameMap.getGame() + " &a- SIZE: " + gameMap.getLocations().size()));
                    int i = 1;
                    for (Location faggot : gameMap.getLocations()) {
                        new ChatUtil("&4&l#" + i + " &f- &c'" + faggot.getWorld().getName() + "' &d" + faggot.getBlockX() + "&c, &d" + faggot.getBlockY() + "&c, &d" + faggot.getBlockZ() + "&c. &a&lCLICK HERE TO BE TELEPORTED", "&7CLICK HERE", "/tpcoords " + faggot.getBlockX() + ' ' + faggot.getBlockY() + ' ' + faggot.getBlockZ()).send((Player) sender);
                        i++;
                    }
                }
            } else if (args[1].equalsIgnoreCase("save")) {
                sender.sendMessage(ColorText.translate("&aGameMapHandler:saveGameMaps we are saving."));
                gameMapHandler.saveGameMaps();
                sender.sendMessage(ColorText.translate("&a&lSAVED " + gameMapHandler.getGameMap().size() + " MAPS."));
            } else if (args[1].equalsIgnoreCase("clear")) {
                if (args.length < 4) {
                    sender.sendMessage(ColorText.translate("&cUsage: /" + label + " <clear> <eventName> <id>"));
                    return;
                }
                int id;
                try {
                    id = Ints.tryParse(args[3]);
                } catch (NumberFormatException ignored) {
                    sender.sendMessage(ColorText.translate("&cInvalid ID."));
                    return;
                }
                if (gameMapHandler.getGameMap(args[2].toLowerCase()) == null || gameMap.getLocations().isEmpty()) {
                    sender.sendMessage(ColorText.translate("&cNo locations provided for this events."));
                } else {
                    HashMap<Integer, Location> formattedList = new HashMap<>();
                    int count = 1;
                    for (Location faggot : gameMap.getLocations()) {
                        formattedList.put(count, faggot);
                        count++;
                    }
                    if (!formattedList.containsKey(id)) {
                        sender.sendMessage(ColorText.translate("&cID not found."));
                        return;
                    }
                    gameMap.getLocations().remove(formattedList.get(id));
                    ((Player) sender).performCommand("game location save FAGGOT");
                }
            } else {
                sender.sendMessage(ColorText.translate("&cUsage: " + getUsage(label)));
            }
        }
    }
}