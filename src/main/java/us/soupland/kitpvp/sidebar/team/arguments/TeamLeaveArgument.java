package us.soupland.kitpvp.sidebar.team.arguments;

import us.soupland.kitpvp.profile.Profile;
import us.soupland.kitpvp.profile.ProfileManager;
import us.soupland.kitpvp.sidebar.team.Team;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.soupland.kitpvp.utilities.chat.ColorText;
import us.soupland.kitpvp.utilities.command.KitPvPArgument;

import java.util.UUID;

public class TeamLeaveArgument extends KitPvPArgument {

    public TeamLeaveArgument() {
        super("leave");
        onlyplayers = true;
    }

    @Override
    public String getUsage(String s) {
        return '/' + s + ' ' + name;
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
        if (team.getLeader() != null && team.getLeader().equals(uuid)) {
            ((Player) sender).performCommand("team disband");
            return;
        }
        profile.setTeam(null);
        team.getOfficers().remove(uuid);
        team.getMembers().remove(uuid);
        team.getPlayerJoined().remove(uuid);
        team.sendMessage(ColorText.translate("&3[*] &c" + sender.getName() + " has left the team."));
    }
}