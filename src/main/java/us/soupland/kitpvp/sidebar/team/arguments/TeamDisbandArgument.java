package us.soupland.kitpvp.sidebar.team.arguments;

import com.mongodb.client.model.Filters;
import us.soupland.kitpvp.sidebar.team.Team;
import us.soupland.kitpvp.utilities.chat.ColorText;
import us.soupland.kitpvp.utilities.task.TaskUtil;
import us.soupland.kitpvp.KitPvP;
import us.soupland.kitpvp.profile.Profile;
import us.soupland.kitpvp.profile.ProfileManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.soupland.kitpvp.utilities.command.KitPvPArgument;

import java.util.UUID;

public class TeamDisbandArgument extends KitPvPArgument {

    public TeamDisbandArgument() {
        super("disband");
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
        if (team.getLeader() != null && !team.getLeader().equals(((Player) sender).getUniqueId()) && !sender.isOp()) {
            sender.sendMessage(ColorText.translate("&cYou must be leader to disband your team."));
            return;
        }
        Bukkit.broadcastMessage(ColorText.translate("&eTeam &9" + team.getName() + " &ehas been &cdisbanded &eby " + sender.getName() + "&e."));
        for (UUID uuid : team.getAllUuids()) {
            OfflinePlayer target = Bukkit.getOfflinePlayer(uuid);
            Profile targetProfile = ProfileManager.getProfile(target);

            targetProfile.setTeam(null);

            /*
            if (!target.isOnline()) {
                ProfileManager.saveProfile(targetProfile, false);
            }
             */
        }
        TaskUtil.runTask(() -> KitPvP.getInstance().getPvPDatabase().getTeams().deleteOne(Filters.eq("uuid", team.getUuid().toString())));
        Team.getTeams().remove(team);
    }
}
