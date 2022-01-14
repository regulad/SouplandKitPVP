package us.soupland.kitpvp.commands.kitpvp;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import us.soupland.kitpvp.profile.Profile;
import us.soupland.kitpvp.profile.ProfileManager;
import us.soupland.kitpvp.utilities.chat.ColorText;
import us.soupland.kitpvp.utilities.command.KitPvPArgument;

public class KitResetArgument extends KitPvPArgument {

    public KitResetArgument() {
        super("reset");
    }

    @Override
    public String getUsage(String s) {
        return '/' + s + ' ' + name + " <playerName>";
    }

    @Override
    public void onExecute(CommandSender sender, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ColorText.translate("&cUsage: " + getUsage(label)));
        } else {
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
            if ((!target.hasPlayedBefore()) && (!target.isOnline())) {
                sender.sendMessage(ColorText.translate("&c" + args[1] + " has never played before."));
                return;
            }
            Profile profile = ProfileManager.getProfile(target);

            profile.reset();
            sender.sendMessage(ColorText.translate("&a" + target.getName() + '\'' + (target.getName().endsWith("s") ? "" : "s") + " stats were successfully reset."));

            if (!target.isOnline()) {
                ProfileManager.saveProfile(profile, true);
            }
        }
    }
}