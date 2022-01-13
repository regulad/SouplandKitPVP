package us.soupland.kitpvp.commands;

import us.soupland.kitpvp.profile.Profile;
import us.soupland.kitpvp.profile.ProfileManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import us.soupland.kitpvp.utilities.chat.ColorText;
import us.soupland.kitpvp.utilities.command.KitPvPCommand;

public class SetReclaimCommand extends KitPvPCommand {

    public SetReclaimCommand() {
        super("setreclaim");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(ColorText.translate("&cUsage: /" + label + " <playerName>"));
        } else {
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
            if ((!target.hasPlayedBefore()) && (!target.isOnline())) {
                sender.sendMessage(ColorText.translate("&c" + args[0] + " has never played before."));
                return false;
            }
            Profile profile = ProfileManager.getProfile(target);
            profile.setReclaimed(!profile.isReclaimed());
            if (!target.isOnline()) {
                ProfileManager.saveProfile(profile, false);
            }
            sender.sendMessage(ColorText.translate("&a" + target.getName() + "&7's reclaimed has been set to &d" + profile.isReclaimed() + "&7."));
        }
        return true;
    }
}