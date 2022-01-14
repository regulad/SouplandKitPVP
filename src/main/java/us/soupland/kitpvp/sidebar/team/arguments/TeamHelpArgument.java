package us.soupland.kitpvp.sidebar.team.arguments;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.CommandSender;
import us.soupland.kitpvp.utilities.chat.ColorText;
import us.soupland.kitpvp.utilities.command.KitPvPArgument;

public class TeamHelpArgument extends KitPvPArgument {

    public TeamHelpArgument() {
        super("help", "View help on how to use teams", null, "h");
    }

    @Override
    public String getUsage(String s) {
        return '/' + s + ' ' + name;
    }

    @Override
    public void onExecute(CommandSender sender, String label, String[] args) {
        sender.sendMessage(ColorText.translateAmpersand("&7&m" + StringUtils.repeat("-", 36)));
        sender.sendMessage(ColorText.translateAmpersand("&4&lTeam Information &7\u2503 &f&lCommands"));
        sender.sendMessage(ColorText.translateAmpersand("&7&m" + StringUtils.repeat("-", 36)));
        sender.sendMessage(ColorText.translateAmpersand("&4General Commands:"));
        sender.sendMessage(ColorText.translateAmpersand("&f/" + label + " create <teamName> <abbreviated name> &7(Create a new team)"));
        sender.sendMessage(ColorText.translateAmpersand("&f/" + label + " accept <teamName> &7(Accept a pending invitation)"));
        sender.sendMessage(ColorText.translateAmpersand("&f/" + label + " leave &7(Leave your current team)"));
        sender.sendMessage("");
        sender.sendMessage(ColorText.translateAmpersand("&4Information Commands:"));
        sender.sendMessage(ColorText.translateAmpersand("&f/" + label + " info [playerName|teamName] &7(Display team information)"));
        sender.sendMessage(ColorText.translateAmpersand("&f/" + label + " list &7(Show list of teams online)"));
        sender.sendMessage("");
        sender.sendMessage(ColorText.translateAmpersand("&4Captain Commands:"));
        sender.sendMessage(ColorText.translateAmpersand("&f/" + label + " invite <playerName> &7(Invite a player to your team)"));
        sender.sendMessage(ColorText.translateAmpersand("&f/" + label + " uninvite <playerName> &7(Revoke an invitation)"));
        sender.sendMessage(ColorText.translateAmpersand("&f/" + label + " acronym <abbreviated name> &7(Set abbreviated name)"));
        sender.sendMessage(ColorText.translateAmpersand("&f/" + label + " kick <playerName> &7(Kick a player from your team)"));
        sender.sendMessage(ColorText.translateAmpersand("&f/" + label + " description [message] &7(Set your team's description)"));
        sender.sendMessage("");
        sender.sendMessage(ColorText.translateAmpersand("&4Leader Commands:"));
        sender.sendMessage(ColorText.translateAmpersand("&f/" + label + " officer <add|remove> <playerName> &7(Promote/Demote a player to/from captain)"));
        sender.sendMessage(ColorText.translateAmpersand("&f/" + label + " disband &7(Disband your team)"));
        sender.sendMessage(ColorText.translateAmpersand("&7&m" + StringUtils.repeat("-", 36)));
    }
}