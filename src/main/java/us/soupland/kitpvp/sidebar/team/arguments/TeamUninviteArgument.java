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

public class TeamUninviteArgument extends KitPvPArgument {

    public TeamUninviteArgument() {
        super("uninvite");
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
        if (team.getLeader() == ((Player) sender).getUniqueId() || team.getOfficers().contains(((Player) sender).getUniqueId())) {
            if (args.length < 2) {
                sender.sendMessage(ColorText.translate("&cUsage: " + getUsage(label)));
            } else {
                OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
                if ((!target.hasPlayedBefore()) && (!target.isOnline())) {
                    sender.sendMessage(ColorText.translate("&c" + args[1] + " has never played before."));
                    return;
                }
                if (!team.getInvitedPlayers().containsKey(target.getUniqueId())) {
                    sender.sendMessage(ColorText.translate("&cNo pending invite for " + target.getName() + '.'));
                    return;
                }
                team.getInvitedPlayers().remove(target.getUniqueId());
                team.sendMessage(ColorText.translate("&ePlayer " + sender.getName() + " &ehas cancelled " + target.getName() + "&e's invitation to the team."));
            }
        } else {
            sender.sendMessage(ColorText.translate("&cYou must be an officer to uninvite players."));
        }
    }
}
