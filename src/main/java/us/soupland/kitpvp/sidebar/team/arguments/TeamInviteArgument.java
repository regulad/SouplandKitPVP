package us.soupland.kitpvp.sidebar.team.arguments;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.soupland.kitpvp.profile.Profile;
import us.soupland.kitpvp.profile.ProfileManager;
import us.soupland.kitpvp.sidebar.team.Team;
import us.soupland.kitpvp.utilities.KitPvPUtils;
import us.soupland.kitpvp.utilities.chat.ChatUtil;
import us.soupland.kitpvp.utilities.chat.ColorText;
import us.soupland.kitpvp.utilities.command.KitPvPArgument;
import us.soupland.kitpvp.utilities.task.TaskUtil;

import java.util.UUID;

public class TeamInviteArgument extends KitPvPArgument {

    public TeamInviteArgument() {
        super("invite", null, null, "inv");
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
            sender.sendMessage(ColorText.translate("&cYou are not in a team."));
            return;
        }
        UUID uuid = ((Player) sender).getUniqueId();
        if (!team.getOfficers().contains(uuid)) {
            sender.sendMessage(ColorText.translate("&cYou must be an officer to invite players."));
        } else {
            if (team.getMembers().size() >= 10) {
                sender.sendMessage(ColorText.translate("&cYour team is full. (10/10)"));
                return;
            }
            if (args.length < 2) {
                sender.sendMessage(ColorText.translate("&cUsage: " + getUsage(label)));
            } else {
                OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
                if ((!target.hasPlayedBefore()) && (!target.isOnline())) {
                    sender.sendMessage(ColorText.translate("&c" + args[1] + " has never played before."));
                    return;
                }
                if (target.isOnline()) {
                    if (!((Player) sender).canSee(target.getPlayer())) {
                        sender.sendMessage(KitPvPUtils.getPlayerNotFoundMessage(args[1]));
                        return;
                    }
                }
                Profile targetProfile = ProfileManager.getProfile(target);
                if (targetProfile.getTeam() != null) {
                    if (targetProfile.getTeam().equals(team)) {
                        sender.sendMessage(ColorText.translate("&c" + target.getName() + " is already in your team."));
                    } else {
                        sender.sendMessage(ColorText.translate("&c" + target.getName() + " is already in a team."));
                    }
                } else {
                    if (team.getInvitedPlayers().containsKey(target.getUniqueId())) {
                        sender.sendMessage(ColorText.translate("&c" + target.getName() + " is already invited to your. &7(/team uninvite " + target.getName() + ')'));
                    } else {
                        team.getInvitedPlayers().put(target.getUniqueId(), ((Player) sender).getUniqueId());
                        team.sendMessage(ColorText.translate("&3[*] &c" + target.getName() + " &ehas been invited to your team by &a" + sender.getName() + "&e."));

                        if (target.isOnline()) {
                            String senderName = sender.getName();
                            new ChatUtil("&3[*] &a" + sender.getName() + " &ehas invited you to join " + team.getDisplayName() + "&e. &7[Click to accept]", "&7Click here to accept", "/team join " + team.getName()).send(target.getPlayer());
                            target.getPlayer().sendMessage(ColorText.translate("&3[*] &eIt will expire in &a1 minute&e."));

                            TaskUtil.runTaskLater(() -> {
                                if (profile.getTeam() != null && targetProfile.getTeam() == null) {
                                    profile.getTeam().getInvitedPlayers().remove(target.getUniqueId());
                                    if (target.isOnline()) {
                                        target.getPlayer().sendMessage("");
                                        target.getPlayer().sendMessage(ColorText.translate("&3[*] &a" + senderName + "&e's invitation has expired."));
                                        target.getPlayer().sendMessage("");
                                    }
                                    profile.getTeam().sendMessage(ColorText.translate("&3[*] &eThe invitation of &c" + target.getName() + " &ehas expired!"));
                                }
                            }, 60 * 20L);
                        }
                    }
                }
            }
        }
    }
}