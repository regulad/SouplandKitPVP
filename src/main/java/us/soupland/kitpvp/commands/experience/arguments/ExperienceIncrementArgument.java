package us.soupland.kitpvp.commands.experience.arguments;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import us.soupland.kitpvp.events.PlayerGainExpEvent;
import us.soupland.kitpvp.profile.Profile;
import us.soupland.kitpvp.profile.ProfileManager;
import us.soupland.kitpvp.utilities.chat.ColorText;
import us.soupland.kitpvp.utilities.command.KitPvPArgument;
import us.soupland.kitpvp.utilities.task.TaskUtil;

public class ExperienceIncrementArgument extends KitPvPArgument {

    public ExperienceIncrementArgument() {
        super("increment");
    }

    @Override
    public String getUsage(String s) {
        return '/' + s + ' ' + name + " <playerName> <type>";
    }

    @Override
    public void onExecute(CommandSender sender, String label, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(ColorText.translate("&cUsage: " + getUsage(label)));
        } else {
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
            if ((!target.hasPlayedBefore()) && (!target.isOnline())) {
                sender.sendMessage(ColorText.translate("&c" + args[1] + " has never played before."));
                return;
            }

            Profile profile = ProfileManager.getProfile(target);

            PlayerGainExpEvent.Type type;
            try {
                type = PlayerGainExpEvent.Type.valueOf(args[2].toUpperCase());
            } catch (Exception ignored) {
                ignored.printStackTrace();
                sender.sendMessage(ColorText.translate("&cInvalid type."));
                return;
            }

            profile.upgradeExperience(type);

            if (!target.isOnline()) {
                TaskUtil.runTask(() -> ProfileManager.saveProfile(profile, true));
            }

            sender.sendMessage(ColorText.translate("&a" + target.getName() + '\'' + (target.getName().endsWith("s") ? "" : "s") + " experience incremented."));
        }
    }
}