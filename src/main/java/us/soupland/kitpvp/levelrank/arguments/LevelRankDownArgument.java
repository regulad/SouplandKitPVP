package us.soupland.kitpvp.levelrank.arguments;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import us.soupland.kitpvp.profile.Profile;
import us.soupland.kitpvp.profile.ProfileManager;
import us.soupland.kitpvp.utilities.chat.ColorText;
import us.soupland.kitpvp.utilities.command.KitPvPArgument;
import us.soupland.kitpvp.utilities.task.TaskUtil;

public class LevelRankDownArgument extends KitPvPArgument {

    public LevelRankDownArgument() {
        super("rankdown");
    }

    @Override
    public String getUsage(String s) {
        return '/' + s + ' ' + name + " <playerName>";
    }

    @Override
    public void onExecute(CommandSender sender, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ColorText.translate("&cUsage: " + getUsage(label)));
            return;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        if ((!target.hasPlayedBefore()) && (!target.isOnline())) {
            sender.sendMessage(ColorText.translate("&c" + args[1] + " has never played before."));
            return;
        }

        Profile profile = ProfileManager.getProfile(target);

        sender.sendMessage(ColorText.translate("&cYou've ranked down " + target.getName() + '\'' + (target.getName().endsWith("s") ? "" : "s") + " level from " + profile.getLevelRank().getName() + " to " + profile.getRankDown().getName() + '.'));

        profile.setExperience(profile.getRankDown().getRequiredExp());
        profile.rankDown();

        if (!target.isOnline()) {
            TaskUtil.runTask(() -> ProfileManager.saveProfile(profile, true));
        }

    }
}