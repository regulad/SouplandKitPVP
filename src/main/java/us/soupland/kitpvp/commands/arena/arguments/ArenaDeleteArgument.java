package us.soupland.kitpvp.commands.arena.arguments;

import org.bukkit.command.CommandSender;
import us.soupland.kitpvp.practice.arena.Arena;
import us.soupland.kitpvp.practice.arena.ArenaHandler;
import us.soupland.kitpvp.utilities.chat.ColorText;
import us.soupland.kitpvp.utilities.command.KitPvPArgument;

public class ArenaDeleteArgument extends KitPvPArgument {

    public ArenaDeleteArgument() {
        super("delete");
    }

    @Override
    public String getUsage(String s) {
        return '/' + s + ' ' + name + " <arenaName>";
    }

    @Override
    public void onExecute(CommandSender sender, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ColorText.translateAmpersand("&cUsage: " + getUsage(label)));
        } else {
            Arena arena = ArenaHandler.getByName(args[1]);
            if (arena == null) {
                sender.sendMessage(ColorText.translateAmpersand("&cAn arena with that name doesn't exists."));
                return;
            }
            ArenaHandler.delete(arena);
            sender.sendMessage(ColorText.translateAmpersand("&cAn arena named &c" + arena.getName() + " &chas been deleted."));
        }
    }
}