package us.soupland.kitpvp.commands.arena.arguments;

import org.bukkit.command.CommandSender;
import us.soupland.kitpvp.practice.arena.Arena;
import us.soupland.kitpvp.practice.arena.ArenaHandler;
import us.soupland.kitpvp.utilities.chat.ColorText;
import us.soupland.kitpvp.utilities.command.KitPvPArgument;

public class ArenaCreateArgument extends KitPvPArgument {

    public ArenaCreateArgument() {
        super("create");
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
            if (arena != null) {
                sender.sendMessage(ColorText.translateAmpersand("&cAn arena with that name already exists."));
                return;
            }
            arena = new Arena(args[1]);
            sender.sendMessage(ColorText.translateAmpersand("&eAn arena named &c" + arena.getName() + " &ehas been created."));
        }
    }

}