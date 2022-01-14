package us.soupland.kitpvp.commands;

import com.google.common.primitives.Ints;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import us.soupland.kitpvp.enums.PlayerStat;
import us.soupland.kitpvp.profile.Profile;
import us.soupland.kitpvp.profile.ProfileManager;
import us.soupland.kitpvp.utilities.chat.ColorText;
import us.soupland.kitpvp.utilities.command.KitPvPCommand;

public class CreditsCommand extends KitPvPCommand {

    public CreditsCommand() {
        super("credits");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(ColorText.translate("&cUsage: /" + label + " <add/take> <playerName> <amount>"));
        } else {
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
            if ((!target.hasPlayedBefore()) && (!target.isOnline())) {
                sender.sendMessage(ColorText.translate("&c" + args[1] + " has never played before."));
                return false;
            }
            Profile profile = ProfileManager.getProfile(target);
            Integer integer = Ints.tryParse(args[2]);
            if (integer == null || integer <= 0) {
                sender.sendMessage(ColorText.translate("&cInvalid amount!"));
                return false;
            }
            if (args[0].equalsIgnoreCase("add")) {
                profile.setStat(PlayerStat.CREDITS, (profile.getStat(PlayerStat.CREDITS) + integer));
                if (!target.isOnline()) {
                    ProfileManager.saveProfile(profile, false);
                }
                sender.sendMessage(ColorText.translate("&7You have added &a" + integer + " &7credits to " + target.getName() + "&7!"));
            } else if (args[0].equalsIgnoreCase("take")) {
                if (integer > profile.getStat(PlayerStat.CREDITS)) {
                    integer = profile.getStat(PlayerStat.CREDITS);
                }
                if (integer <= 0) {
                    sender.sendMessage(ColorText.translate("&c" + target.getName() + " don't have credits enough."));
                    return false;
                }
                profile.setStat(PlayerStat.CREDITS, (profile.getStat(PlayerStat.CREDITS) - integer));
                if (!target.isOnline()) {
                    ProfileManager.saveProfile(profile, false);
                }
                sender.sendMessage(ColorText.translate("&7You have remove &a" + integer + " &7credits from " + target.getName() + "&7!"));
            } else {
                sender.sendMessage(ColorText.translate("&cUsage: /" + label + " <add/remove> <playerName> <amount>"));
            }
        }
        return true;
    }
}