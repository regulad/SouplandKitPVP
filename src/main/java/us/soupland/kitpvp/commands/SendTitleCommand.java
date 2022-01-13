package us.soupland.kitpvp.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.soupland.kitpvp.utilities.KitPvPUtils;
import us.soupland.kitpvp.utilities.chat.ColorText;
import us.soupland.kitpvp.utilities.command.KitPvPCommand;

public class SendTitleCommand extends KitPvPCommand {

    public SendTitleCommand() {
        super("sendpacket", null, "sendtitle");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(ColorText.translate("&cUsage: /" + label + " <playerName/all> <title> <subTitle>"));
        } else {
            if (args[0].equalsIgnoreCase("all") || args[0].equalsIgnoreCase("*")) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.sendTitle(ColorText.translate(args[1].replace("\n", " ")), ColorText.translate(args[2].replace("\n", " ")));
                }
                sender.sendMessage(ColorText.translate("&aPackets sent to everybody."));
            } else {
                Player player = Bukkit.getPlayer(args[0]);
                if (player == null || !player.isOnline()) {
                    sender.sendMessage(KitPvPUtils.getPlayerNotFoundMessage(args[0]));
                    return false;
                }
                player.sendTitle(ColorText.translate(args[1].replace("\n", " ")), ColorText.translate(args[2].replace("\n", " ")));
                sender.sendMessage(ColorText.translate("&aPackets sent to " + player.getName() + '.'));
            }
        }
        return true;
    }
}