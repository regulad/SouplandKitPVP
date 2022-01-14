package us.soupland.kitpvp.sidebar.team.arguments;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.soupland.kitpvp.enums.PlayerStat;
import us.soupland.kitpvp.profile.Profile;
import us.soupland.kitpvp.profile.ProfileManager;
import us.soupland.kitpvp.sidebar.team.Team;
import us.soupland.kitpvp.utilities.chat.ChatUtil;
import us.soupland.kitpvp.utilities.chat.ColorText;
import us.soupland.kitpvp.utilities.command.KitPvPArgument;

public class TeamCreateArgument extends KitPvPArgument {

    public TeamCreateArgument() {
        super("create", "Create a team");
        onlyplayers = true;
    }

    @Override
    public String getUsage(String s) {
        return '/' + s + ' ' + name + " <teamName> <abbreviated name>";
    }

    @Override
    public void onExecute(CommandSender sender, String label, String[] args) {
        Profile profile = ProfileManager.getProfile((Player) sender);
        if (profile.getTeam() != null) {
            sender.sendMessage(ColorText.translate("&cYou are already in a team"));
            return;
        }
        if (profile.getStat(PlayerStat.CREDITS) < 5000) {
            sender.sendMessage(ColorText.translate("&cYou don't have credits enough to create a Team. You need 5000 credits."));
            return;
        }
        if (args.length < 3) {
            sender.sendMessage(ColorText.translate("&cUsage: " + getUsage(label)));
        } else {
            if (args[1].length() < 3) {
                sender.sendMessage(ColorText.translate("&cMinimum team name size is 3 characters!"));
                return;
            }
            if (args[1].length() > 10) {
                sender.sendMessage(ColorText.translate("&cMaximum team name size is 10 characters!"));
                return;
            }

            if (ChatColor.stripColor(ColorText.translate(args[2])).length() < 2) {
                sender.sendMessage(ColorText.translate("&cMinimum acronym name size is 2 characters!"));
                return;
            }

            if (ChatColor.stripColor(ColorText.translate(args[2])).length() > 4) {
                sender.sendMessage(ColorText.translate("&cMaximum acronym name size is 4 characters!"));
                return;
            }

            Team team = Team.getByName(args[1]);
            if (team != null) {
                sender.sendMessage(ColorText.translate("&cThat team already exists!"));
                return;
            }
            if (!StringUtils.isAlphanumeric(args[1])) {
                sender.sendMessage(ColorText.translate("&cTeam tag must be alphanumeric!"));
                return;
            }

            if (!StringUtils.isAlphanumeric(ChatColor.stripColor(ColorText.translate(args[2])))) {
                sender.sendMessage(ColorText.translate("&cTeam acronym must be alphanumeric!"));
                return;
            }

            if (!sender.hasPermission("team.acronym.color")) {
                args[2] = ChatColor.stripColor(args[2]);
            }

            String acronym = args[2];

            acronym = acronym.replace("&k", "");
            acronym = acronym.replace("&l", "");
            acronym = acronym.replace("&o", "");
            acronym = acronym.replace("&m", "");

            team = new Team(args[1], ((Player) sender).getUniqueId(), null);

            team.setDisplayName(acronym);

            team.getPlayerJoined().put(((Player) sender).getUniqueId(), System.currentTimeMillis());

            profile.setTeam(team);
            profile.setStat(PlayerStat.CREDITS, (profile.getStat(PlayerStat.CREDITS) - 5000));

            Bukkit.broadcastMessage(ColorText.translate("&eTeam &9" + team.getName() + " &ehas been &acreated &eby " + sender.getName() + "&e."));
            for (Player online : Bukkit.getOnlinePlayers()) {
                if (online.equals(sender)) {
                    continue;
                }
                new ChatUtil("&7[Click here for more Information]", null, "/team show " + team.getName()).send(online);
            }
        }
    }
}