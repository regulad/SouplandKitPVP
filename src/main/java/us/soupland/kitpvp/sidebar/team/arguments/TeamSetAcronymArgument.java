package us.soupland.kitpvp.sidebar.team.arguments;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.soupland.kitpvp.profile.Profile;
import us.soupland.kitpvp.profile.ProfileManager;
import us.soupland.kitpvp.sidebar.team.Team;
import us.soupland.kitpvp.utilities.chat.ColorText;
import us.soupland.kitpvp.utilities.command.KitPvPArgument;

public class TeamSetAcronymArgument extends KitPvPArgument {

    public TeamSetAcronymArgument() {
        super("acronym");
    }

    @Override
    public String getUsage(String s) {
        return '/' + s + ' ' + name + " <acronym>";
    }

    @Override
    public void onExecute(CommandSender sender, String label, String[] args) {
        Profile profile = ProfileManager.getProfile((Player) sender);
        Team team = profile.getTeam();
        if (team == null) {
            sender.sendMessage(ColorText.translateAmpersand("&cYou are not in a team."));
            return;
        }
        if (!team.getOfficers().contains(((Player) sender).getUniqueId())) {
            sender.sendMessage(ColorText.translateAmpersand("&cYou must be an officer to change acronym."));
            return;
        }
        if (args.length < 2) {
            sender.sendMessage(ColorText.translateAmpersand("&cUsage: " + getUsage(label)));
        } else {
            String acronym = args[1];
            if (ChatColor.stripColor(ColorText.translateAmpersand(args[1])).length() < 2) {
                sender.sendMessage(ColorText.translateAmpersand("&cMinimum acronym name size is 3 characters!"));
                return;
            }

            if (ChatColor.stripColor(ColorText.translateAmpersand(args[1])).length() > 4) {
                sender.sendMessage(ColorText.translateAmpersand("&cMaximum acronym name size is 3 characters!"));
                return;
            }

            if (!StringUtils.isAlphanumeric(ChatColor.stripColor(ColorText.translateAmpersand(args[1])))) {
                sender.sendMessage(ColorText.translateAmpersand("&cTeam acronym must be alphanumeric!"));
                return;
            }

            if (!sender.hasPermission("team.acronym.color")) {
                args[1] = ChatColor.stripColor(args[1]);
            }

            acronym = acronym.replace("&k", "");
            acronym = acronym.replace("&l", "");
            acronym = acronym.replace("&o", "");
            acronym = acronym.replace("&m", "");

            team.setDisplayName(acronym);
            sender.sendMessage(ColorText.translateAmpersand("&aTeam acronym has been successfully changed!"));
        }
    }
}
