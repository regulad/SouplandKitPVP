package us.soupland.kitpvp.levelrank.arguments;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import us.soupland.kitpvp.levelrank.LevelRank;
import us.soupland.kitpvp.profile.Profile;
import us.soupland.kitpvp.profile.ProfileManager;
import us.soupland.kitpvp.utilities.chat.ColorText;
import us.soupland.kitpvp.utilities.command.KitPvPArgument;
import us.soupland.kitpvp.utilities.task.TaskUtil;

public class LevelRankUpArgument extends KitPvPArgument {

    public LevelRankUpArgument() {
        super("rankup");
    }

    @Override
    public String getUsage(String s) {
        return '/' + s + ' ' + name + " <playerName>";
    }

    @Override
    public void onExecute(CommandSender sender, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ColorText.translateAmpersand("&cUsage: " + getUsage(label)));
            return;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        if ((!target.hasPlayedBefore()) && (!target.isOnline())) {
            sender.sendMessage(ColorText.translateAmpersand("&c" + args[1] + " has never played before."));
            return;
        }

        Profile profile = ProfileManager.getProfile(target);

        final @NotNull LevelRank currentRank = profile.getLevelRank();

        if (profile.rankUp()) {
            sender.sendMessage(ColorText.translateAmpersand("&aYou've ranked up " + target.getName() + '\'' + (target.getName().endsWith("s") ? "" : "s") + " level from " + currentRank.getName() + " to " + profile.getLevelRank().getName() + '.'));

            profile.setExperience(profile.getRankUp().getRequiredExp());
            profile.rankUp();

            if (!target.isOnline()) {
                TaskUtil.runTask(() -> ProfileManager.saveProfile(profile, true));
            }
        } else {
            sender.sendMessage("Cannot rank up at this time.");
        }
    }
}
