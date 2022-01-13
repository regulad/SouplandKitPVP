package us.soupland.kitpvp.sidebar.team.arguments;

import us.soupland.kitpvp.profile.Profile;
import us.soupland.kitpvp.profile.ProfileManager;
import us.soupland.kitpvp.sidebar.team.Team;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.soupland.kitpvp.utilities.chat.ColorText;
import us.soupland.kitpvp.utilities.command.KitPvPArgument;

public class TeamDescriptionArgument extends KitPvPArgument {

    public TeamDescriptionArgument() {
        super("description", null, null, "desc", "ann", "announcement");
    }

    @Override
    public String getUsage(String s) {
        return '/' + s + ' ' + name + " <description>";
    }

    @Override
    public void onExecute(CommandSender sender, String label, String[] args) {
        Profile profile = ProfileManager.getProfile((Player) sender);
        Team team = profile.getTeam();
        if (team == null) {
            sender.sendMessage(ColorText.translate("&cYou are not in a team."));
            return;
        }
        if (team.getLeader() == ((Player) sender).getUniqueId() || team.getOfficers().contains(((Player) sender).getUniqueId()) || sender.isOp()) {
            if (args.length < 2) {
                sender.sendMessage(ColorText.translate("&cUsage: " + getUsage(label)));
            } else {
                StringBuilder description = new StringBuilder();
                for (int i = 1; i < args.length; i++) {
                    description.append(args[i]).append(' ');
                }
                team.setDescription(description.toString());
                team.sendMessage(ColorText.translate(sender.getName() + " &echanged the team announcement."));
            }
        } else {
            sender.sendMessage(ColorText.translate("&cYou must be an officer to set an announcement."));
        }
    }
}