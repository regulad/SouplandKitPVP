package us.soupland.kitpvp.levelrank.arguments;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import us.soupland.kitpvp.levelrank.LevelRank;
import us.soupland.kitpvp.profile.Profile;
import us.soupland.kitpvp.profile.ProfileManager;
import us.soupland.kitpvp.utilities.chat.ColorText;
import us.soupland.kitpvp.utilities.command.KitPvPArgument;

public class LevelSetRankArgument extends KitPvPArgument {

    public LevelSetRankArgument() {
        super("setrank");
    }

    @Override
    public String getUsage(String s) {
        return '/' + s + ' ' + name + " <playerName> <rankName>";
    }

    @Override
    public void onExecute(CommandSender sender, String label, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(ColorText.translate("&cUsage: " + getUsage(label)));
            return;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        if ((!target.hasPlayedBefore()) && (!target.isOnline())) {
            sender.sendMessage(ColorText.translate("&c" + args[1] + " has never played before."));
            return;
        }
        String rankName = args[2];

        LevelRank levelRank = LevelRank.getByName(rankName);

        if (levelRank == null) {
            sender.sendMessage(ColorText.translate("&cA rank named '" + rankName + "' could not be found."));
            return;
        }

        Profile profile = ProfileManager.getProfile(target);

        profile.setLevelRank(levelRank);
        profile.setExperience(levelRank.getRequiredExp());

        sender.sendMessage(ColorText.translate("&aYou've updated " + target.getName() + '\'' + (target.getName().endsWith("s") ? "" : "s") + " rank to " + levelRank.getName() + '.'));

    }
}
