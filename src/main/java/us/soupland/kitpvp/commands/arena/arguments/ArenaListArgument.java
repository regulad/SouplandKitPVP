package us.soupland.kitpvp.commands.arena.arguments;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.CommandSender;
import us.soupland.kitpvp.practice.arena.Arena;
import us.soupland.kitpvp.practice.arena.ArenaHandler;
import us.soupland.kitpvp.utilities.chat.ColorText;
import us.soupland.kitpvp.utilities.command.KitPvPArgument;

public class ArenaListArgument extends KitPvPArgument {

    public ArenaListArgument() {
        super("list");
    }

    @Override
    public String getUsage(String s) {
        return '/' + s + ' ' + name;
    }

    @Override
    public void onExecute(CommandSender sender, String label, String[] args) {
        if (ArenaHandler.getArenaMap().isEmpty()) {
            sender.sendMessage(ColorText.translate("&cThere are no arenas created."));
            return;
        }
        sender.sendMessage(ColorText.translate("&7&m" + StringUtils.repeat("-", 40)));
        for (Arena arena : ArenaHandler.getArenaMap()) {
            sender.sendMessage(ColorText.translate(" &7- &e&l" + arena.getName()));
        }
        sender.sendMessage(ColorText.translate("&7&m" + StringUtils.repeat("-", 40)));
    }
}