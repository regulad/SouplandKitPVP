package us.soupland.kitpvp.koth.commands.args;

import us.soupland.kitpvp.koth.selection.KothSelection;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.soupland.kitpvp.utilities.command.KitPvPArgument;

public class KothWandArgument extends KitPvPArgument {

    public KothWandArgument() {
        super("wand");
        setOnlyplayers(true);
    }

    @Override
    public String getUsage(String s) {
        return '/' + s + ' ' + getName();
    }

    @Override
    public void onExecute(CommandSender sender, String label, String[] args) {
        ((Player) sender).getInventory().addItem(KothSelection.getWand());

    }
}
