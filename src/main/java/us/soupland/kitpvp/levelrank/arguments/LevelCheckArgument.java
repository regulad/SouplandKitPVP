package us.soupland.kitpvp.levelrank.arguments;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import us.soupland.kitpvp.profile.Profile;
import us.soupland.kitpvp.profile.ProfileManager;
import us.soupland.kitpvp.utilities.chat.ColorText;
import us.soupland.kitpvp.utilities.command.KitPvPArgument;

public class LevelCheckArgument extends KitPvPArgument {

    public LevelCheckArgument() {
        super("check");
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

        sender.sendMessage(ColorText.translateAmpersand("&eLevel rank of " + target.getName() + " is " + profile.getLevelRank().getName()));
        sender.sendMessage(ColorText.translateAmpersand("&aNext rankup &e" + profile.getRankUp().getName() + " &axp&7:&f " + profile.getExperience() + "&7/&c" + profile.getRankUp().getRequiredExp()));

    }
}
