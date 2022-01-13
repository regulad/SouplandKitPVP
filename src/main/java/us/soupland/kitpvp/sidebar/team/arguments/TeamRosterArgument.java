package us.soupland.kitpvp.sidebar.team.arguments;

import us.soupland.kitpvp.profile.Profile;
import us.soupland.kitpvp.profile.ProfileManager;
import us.soupland.kitpvp.sidebar.team.Team;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.soupland.kitpvp.utilities.chat.ColorText;
import us.soupland.kitpvp.utilities.command.KitPvPArgument;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TeamRosterArgument extends KitPvPArgument {

    public TeamRosterArgument() {
        super("roster", "Get details about a team", null, "show", "who", "info", "i");
    }

    @Override
    public String getUsage(String s) {
        return '/' + s + ' ' + name + " <playerName|teamName>";
    }

    @Override
    public void onExecute(CommandSender sender, String label, String[] args) {
        if (args.length < 2) {
            if (sender instanceof Player) {
                Profile profile = ProfileManager.getProfile((Player) sender);
                if (profile.getTeam() == null) {
                    sender.sendMessage(ColorText.translate("&cYou are not in a team."));
                    return;
                }
                sendTeamInformation(sender, profile.getTeam());
            } else {
                sender.sendMessage(ColorText.translate("&cUsage: " + getUsage(label)));
            }
        } else {
            Team found = Team.getByName(args[1]);
            if (found == null) {
                for (Team team : Team.getTeams()) {
                    if (team.getDisplayName().equalsIgnoreCase(args[1])) {
                        found = team;
                    }
                    for (UUID uuid : team.getAllUuids()) {
                        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
                        if (player.getName() != null) {
                            if (player.getName().equalsIgnoreCase(args[1])) {
                                Profile profile = ProfileManager.getProfile(player);
                                if (profile.getTeam() != null) {
                                    if (found == null) {
                                        found = profile.getTeam();
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (found == null) {
                sender.sendMessage(ColorText.translate("&cNo player teams found with player or name '" + args[1] + "'."));
                return;
            }
            sendTeamInformation(sender, found);
        }
    }

    private void sendTeamInformation(CommandSender sender, Team team) {

        String leaderName;
        UUID leader = team.getLeader();
        OfflinePlayer leaderPlayer = Bukkit.getOfflinePlayer(leader);

        if (leaderPlayer == null) {
            leaderName = null;
        } else if (!leaderPlayer.isOnline() || (sender instanceof Player && !((Player) sender).canSee(leaderPlayer.getPlayer()))) {
            leaderName = "&c";
        } else {
            leaderName = "&a";
        }

        if (leaderName != null) {
            leaderName += leaderPlayer.getName();
        }

        List<String> officerString = new ArrayList<>();

        if (!team.getOfficers().isEmpty()) {
            for (UUID uuid : team.getOfficers()) {
                OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);

                if (player == null) {
                    continue;
                }

                if (team.getLeader() == uuid) {
                    continue;
                }

                if (player.isOnline() && sender instanceof Player && !((Player) sender).canSee(player.getPlayer())) {
                    continue;
                }

                officerString.add((player.isOnline() ? "&a" : "&c") + player.getName());
            }
        }

        List<String> membersStrings = new ArrayList<>();

        if (!team.getMembers().isEmpty()) {
            for (UUID uuid : team.getMembers()) {
                OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);

                if (player == null) {
                    continue;
                }

                if (player.isOnline() && sender instanceof Player && !((Player) sender).canSee(player.getPlayer())) {
                    continue;
                }

                membersStrings.add((player.isOnline() ? "&a" : "&c") + player.getName());
            }
        }

        sender.sendMessage(ColorText.translate("&7&m" + StringUtils.repeat("-", 40)));
        sender.sendMessage(ColorText.translate(" &9&l" + team.getName() + " &7[" + team.getOnlineMembers(sender) + '/' + team.getAllUuids().size() + ']'));
        if (leaderName != null) {
            sender.sendMessage(ColorText.translate(" &eLeader: " + leaderName));
        }
        if (!officerString.isEmpty()) {
            sender.sendMessage(ColorText.translate(" &eCaptains: " + StringUtils.join(officerString, "&7, ") + "&7."));
        }
        if (!membersStrings.isEmpty()) {
            sender.sendMessage(ColorText.translate(" &eMembers: " + StringUtils.join(membersStrings, "&7, ") + "&7."));
        }
        if (team.getDescription() != null) {
            sender.sendMessage(ColorText.translate(" &eDescription: &r") + team.getDescription());
        }
        sender.sendMessage(ColorText.translate("&7&m" + StringUtils.repeat("-", 40)));
    }
}