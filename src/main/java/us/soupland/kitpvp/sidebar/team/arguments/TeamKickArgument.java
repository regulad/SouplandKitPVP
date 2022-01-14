package us.soupland.kitpvp.sidebar.team.arguments;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.soupland.kitpvp.profile.Profile;
import us.soupland.kitpvp.profile.ProfileManager;
import us.soupland.kitpvp.sidebar.team.Team;
import us.soupland.kitpvp.utilities.chat.ColorText;
import us.soupland.kitpvp.utilities.command.KitPvPArgument;

public class TeamKickArgument extends KitPvPArgument {

    public TeamKickArgument() {
        super("kick");
        onlyplayers = true;
    }

    @Override
    public String getUsage(String s) {
        return '/' + s + ' ' + name + " <playerName>";
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
            sender.sendMessage(ColorText.translateAmpersand("&cYou must be an officer to kick players."));
            return;
        }
        if (args.length < 2) {
            sender.sendMessage(ColorText.translateAmpersand("&cUsage: " + getUsage(label)));
        } else {
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
            if ((!target.hasPlayedBefore()) && (!target.isOnline())) {
                sender.sendMessage(ColorText.translateAmpersand("&c" + args[1] + " has never played before."));
                return;
            }
            if (sender == target) {
                sender.sendMessage(ColorText.translateAmpersand("&cYou can't kick yourself."));
                return;
            }
            Profile targetProfile = ProfileManager.getProfile(target);
            if (!team.equals(targetProfile.getTeam())) {
                sender.sendMessage(ColorText.translateAmpersand("&c" + target.getName() + " is not in your team."));
                return;
            }
            if (team.getLeader().equals(target.getUniqueId())) {
                sender.sendMessage(ColorText.translateAmpersand("&cYou can't kick the leader."));
                return;
            }
            if (team.getOfficers().contains(target.getUniqueId()) && team.getLeader() != ((Player) sender).getUniqueId()) {
                sender.sendMessage(ColorText.translateAmpersand("&cYou can't kick another officer."));
                return;
            }
            team.getOfficers().remove(target.getUniqueId());
            team.getMembers().remove(target.getUniqueId());
            team.getPlayerJoined().remove(target.getUniqueId());
            team.sendMessage(ColorText.translateAmpersand("&ePlayer " + target.getName() + " &ehas been kicked by &c" + sender.getName() + " &efrom the team."));
            targetProfile.setTeam(null);
        }
    }
}