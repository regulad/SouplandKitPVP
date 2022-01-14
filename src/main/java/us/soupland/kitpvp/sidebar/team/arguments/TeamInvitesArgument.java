package us.soupland.kitpvp.sidebar.team.arguments;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.soupland.kitpvp.profile.ProfileManager;
import us.soupland.kitpvp.sidebar.team.Team;
import us.soupland.kitpvp.utilities.chat.ColorText;
import us.soupland.kitpvp.utilities.command.KitPvPArgument;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TeamInvitesArgument extends KitPvPArgument {

    public TeamInvitesArgument() {
        super("invites", "View team invitations");
        onlyplayers = true;
    }

    @Override
    public String getUsage(String s) {
        return '/' + s + ' ' + name;
    }

    @Override
    public void onExecute(CommandSender sender, String label, String[] args) {
        Player player = (Player) sender;

        List<String> receivedInvites = new ArrayList<>();

        for (Team team : Team.getTeams()) {
            if (team.getInvitedPlayers().containsKey(player.getUniqueId())) {
                receivedInvites.add(team.getDisplayName());
            }
        }

        Team team = ProfileManager.getProfile(player).getTeam();

        if (team != null) {
            List<String> strings = new ArrayList<>();
            for (Map.Entry<UUID, UUID> entry : team.getInvitedPlayers().entrySet()) {
                strings.add(Bukkit.getOfflinePlayer(entry.getKey()).getName());
            }
            sender.sendMessage(ColorText.translateAmpersand("&bSent by &e" + team.getDisplayName() + " &7(" + strings.size() + ")&3: &7" + (strings.isEmpty() ? "Your team has not invited anyone" : StringUtils.join(strings, "&7, ")) + "&7."));
        }

        sender.sendMessage(ColorText.translateAmpersand("&3Requested (" + receivedInvites.size() + ")&3: &7" + (receivedInvites.isEmpty() ? "No teams have invited you" : StringUtils.join(receivedInvites, "&7, ")) + "&7."));
    }
}