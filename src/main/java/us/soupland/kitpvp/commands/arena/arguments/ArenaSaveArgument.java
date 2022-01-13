package us.soupland.kitpvp.commands.arena.arguments;

import us.soupland.kitpvp.practice.arena.Arena;
import us.soupland.kitpvp.practice.arena.ArenaHandler;
import org.bukkit.command.CommandSender;
import us.soupland.kitpvp.utilities.chat.ColorText;
import us.soupland.kitpvp.utilities.command.KitPvPArgument;

public class ArenaSaveArgument extends KitPvPArgument {

    public ArenaSaveArgument() {
        super("save");
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
            arena.saveArena();
            sender.sendMessage(ColorText.translate("&aArena named " + arena.getName() + " saved."));
        }
    }
}