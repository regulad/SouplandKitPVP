package us.soupland.kitpvp.commands.kitpvp;

import org.bukkit.command.CommandSender;
import us.soupland.kitpvp.levelrank.LevelRank;
import us.soupland.kitpvp.utilities.chat.ColorText;
import us.soupland.kitpvp.utilities.command.KitPvPArgument;

public class KitReloadArgument extends KitPvPArgument {
    public KitReloadArgument() {
        super("reload");
    }

    @Override
    public String getUsage(String s) {
        return '/' + s + ' ' + name;
    }

    @Override
    public void onExecute(CommandSender sender, String label, String[] args) {
        /*KitPvP.getInstance().getConfig().reload();
        KitPvP.getInstance().getKitConfig().reload();
        KitPvP.getInstance().getRankConfig().reload();*/
        LevelRank.getAllRanks().clear();
        LevelRank.loadAllRanks();

        sender.sendMessage(ColorText.translateAmpersand("&aKitpvp reloaded"));
    }
}
