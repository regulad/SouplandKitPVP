package us.soupland.kitpvp.sidebar.team;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.soupland.kitpvp.sidebar.team.arguments.*;
import us.soupland.kitpvp.utilities.chat.ColorText;
import us.soupland.kitpvp.utilities.command.KitPvPArgument;
import us.soupland.kitpvp.utilities.command.KitPvPCommand;

public class TeamCommand extends KitPvPCommand {

    private TeamHelpArgument helpArgument;

    public TeamCommand() {
        super("team", null, "t", "clan");
        registerArgument(new TeamCreateArgument());
        registerArgument(new TeamRosterArgument());
        registerArgument(helpArgument = new TeamHelpArgument());
        registerArgument(new TeamInvitesArgument());
        registerArgument(new TeamOfficerCommand());
        registerArgument(new TeamInviteArgument());
        registerArgument(new TeamDisbandArgument());
        registerArgument(new TeamDescriptionArgument());
        registerArgument(new TeamJoinArgument());
        registerArgument(new TeamLeaveArgument());
        registerArgument(new TeamListArgument());
        registerArgument(new TeamUninviteArgument());
        registerArgument(new TeamKickArgument());
        registerArgument(new TeamSetAcronymArgument());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            helpArgument.onExecute(sender, label, args);
        } else {
            KitPvPArgument argument = getArgument(args[0]);
            if (argument == null || (argument.onlyplayers && !(sender instanceof Player))) {
                helpArgument.onExecute(sender, label, args);
            } else {
                if (argument.permission == null || sender.hasPermission(argument.permission)) {
                    argument.onExecute(sender, label, args);
                } else {
                    sender.hasPermission(ColorText.translate("&cTeam sub-command '" + argument.name + "' not found."));
                }
            }
        }
        return true;
    }
}