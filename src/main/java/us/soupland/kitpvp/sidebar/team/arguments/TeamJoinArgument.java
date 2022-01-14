package us.soupland.kitpvp.sidebar.team.arguments;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.soupland.kitpvp.profile.Profile;
import us.soupland.kitpvp.profile.ProfileManager;
import us.soupland.kitpvp.sidebar.team.Team;
import us.soupland.kitpvp.utilities.KitPvPUtils;
import us.soupland.kitpvp.utilities.chat.ColorText;
import us.soupland.kitpvp.utilities.command.KitPvPArgument;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TeamJoinArgument extends KitPvPArgument {

    public TeamJoinArgument() {
        super("join", null, null, "accept");
        onlyplayers = true;
    }

    @Override
    public String getUsage(String s) {
        return '/' + s + ' ' + name;
    }

    @Override
    public void onExecute(CommandSender sender, String label, String[] args) {
        Profile profile = ProfileManager.getProfile((Player) sender);
        Team playerTeam = profile.getTeam();
        if (playerTeam != null) {
            sender.sendMessage(ColorText.translateAmpersand("&cYou are already in a team."));
            return;
        }
        if (args.length < 2) {
            sender.sendMessage(ColorText.translateAmpersand("&cUsage: " + getUsage(label)));
        } else {
            Team found = Team.getByName(args[1]);
            if (found == null) {
                for (Team team : Team.getTeams()) {
                    for (UUID uuid : team.getAllUuids()) {
                        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
                        if (player.getName() != null) {
                            if (player.getName().equalsIgnoreCase(args[1])) {
                                Profile foundProfile = ProfileManager.getProfile(player);
                                if (foundProfile.getTeam() != null) {
                                    found = foundProfile.getTeam();
                                }
                            }
                        }
                    }
                }
            }
            if (found == null) {
                sender.sendMessage(ColorText.translateAmpersand("&cNo player teams found with player or name '" + args[1] + "'."));
                return;
            }
            if (!sender.isOp()) {
                if (!found.getInvitedPlayers().containsKey(((Player) sender).getUniqueId())) {
                    sender.sendMessage(ColorText.translateAmpersand("&cThat team hasn't invited you."));
                    return;
                }
                if (found.getAllUuids().size() >= 10) {
                    sender.sendMessage(ColorText.translateAmpersand("&cThat team is full."));
                    return;
                }
            }
            found.getInvitedPlayers().remove(((Player) sender).getUniqueId());
            found.getMembers().add(((Player) sender).getUniqueId());
            found.getPlayerJoined().put(((Player) sender).getUniqueId(), System.currentTimeMillis());
            profile.setTeam(found);
            found.sendMessage(ColorText.translateAmpersand("&3[*] &a" + sender.getName() + " &ehas joined to your team."));
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String label, String[] args) {
        List<String> toReturn = new ArrayList<>();
        if (sender instanceof Player) {
            if (args.length == 2) {
                for (Team team : Team.getTeams()) {
                    if (team.getInvitedPlayers().containsKey(((Player) sender).getUniqueId())) {
                        toReturn.add(team.getName());
                    }
                }
            }
        }
        return KitPvPUtils.getCompletions(args, toReturn);
    }
}