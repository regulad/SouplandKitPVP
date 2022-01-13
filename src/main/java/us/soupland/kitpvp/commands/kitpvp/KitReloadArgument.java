package us.soupland.kitpvp.commands.kitpvp;

import us.soupland.kitpvp.levelrank.LevelRank;
import org.bukkit.command.CommandSender;
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
        LevelRank.getLevelRanks().clear();
        LevelRank.loadRanks();

        sender.sendMessage(ColorText.translate("&aKitpvp reloaded"));
    }
}
