package us.soupland.kitpvp.sidebar.team.arguments;

import org.apache.commons.lang3.StringUtils;
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

public class TeamOfficerCommand extends KitPvPArgument {

    public TeamOfficerCommand() {
        super("officer");
        onlyplayers = true;
    }

    @Override
    public String getUsage(String s) {
        return '/' + s + ' ' + name + " <add|remove> <playerName>";
    }

    @Override
    public void onExecute(CommandSender sender, String label, String[] args) {
        Profile profile = ProfileManager.getProfile((Player) sender);
        Team team = profile.getTeam();
        if (team == null) {
            sender.sendMessage(ColorText.translate("&cYou are not in a team."));
            return;
        }
        if (!team.getOfficers().contains(((Player) sender).getUniqueId())) {
            sender.sendMessage(ColorText.translate("&cYou must be leader to edit the team roster."));
            return;
        }
        if (args.length < 3) {
            sender.sendMessage(ColorText.translate("&6&m" + StringUtils.repeat("-", 25)));
            sender.sendMessage(ColorText.translate("&c/" + label + ' ' + name + " <add> <playerName>"));
            sender.sendMessage(ColorText.translate("&c/" + label + ' ' + name + " <remove> <playerName>"));
            sender.sendMessage(ColorText.translate("&6&m" + StringUtils.repeat("-", 25)));
        } else {
            boolean isAddArgument;
            if (args[1].equalsIgnoreCase("add")) {
                isAddArgument = true;
            } else if (args[1].equalsIgnoreCase("remove")) {
                isAddArgument = false;
            } else {
                sender.sendMessage(ColorText.translate("&6&m" + StringUtils.repeat("-", 25)));
                sender.sendMessage(ColorText.translate("&c/" + label + ' ' + name + " <add> <playerName>"));
                sender.sendMessage(ColorText.translate("&c/" + label + ' ' + name + " <remove> <playerName>"));
                sender.sendMessage(ColorText.translate("&6&m" + StringUtils.repeat("-", 25)));
                return;
            }
            OfflinePlayer target = Bukkit.getPlayer(args[2]);
            if ((!target.hasPlayedBefore()) && (!target.isOnline())) {
                sender.sendMessage(ColorText.translate("&c" + args[2] + " has never played before."));
                return;
            }
            if (target.isOnline()) {
                if (!((Player) sender).canSee(target.getPlayer())) {
                    sender.sendMessage(KitPvPUtils.getPlayerNotFoundMessage(args[1]));
                    return;
                }
            }
            Profile targetProfile = ProfileManager.getProfile(target);
            Team targetTeam = targetProfile.getTeam();
            if (targetTeam == null || team != targetTeam) {
                sender.sendMessage(ColorText.translate("&c" + target.getName() + " is not in your team."));
                return;
            }
            if (isAddArgument) {
                if (team.getLeader() == target.getUniqueId() || team.getOfficers().contains(target.getUniqueId())) {
                    sender.sendMessage(ColorText.translate("&c" + target.getName() + " is already an officer."));
                } else {
                    team.getOfficers().add(target.getUniqueId());
                    team.sendMessage(ColorText.translate("&3[*] &c" + target.getName() + " &ehas been promoted to an &aOFFICER&e."));
                }
            } else {
                if (team.getLeader() == target.getUniqueId() || !team.getOfficers().contains(target.getUniqueId())) {
                    sender.sendMessage(ColorText.translate("&c" + target.getName() + " is not an officer."));
                } else {
                    team.getOfficers().remove(target.getUniqueId());
                    team.sendMessage(ColorText.translate("&3[*] &c" + target.getName() + " &ehas been demoted to &cMEMBER&e."));
                }
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String label, String[] args) {
        List<String> toReturn = new ArrayList<>();
        if (sender instanceof Player) {
            if (args.length == 3) {
                Team team = ProfileManager.getProfile((Player) sender).getTeam();
                if (team != null) {
                    for (UUID uuid : team.getAllUuids()) {
                        if (team.getLeader().equals(uuid)) {
                            continue;
                        }
                        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
                        if (player != null) {
                            toReturn.add(player.getName());
                        }
                    }
                }
            }
        }
        return KitPvPUtils.getCompletions(args, toReturn);
    }
}