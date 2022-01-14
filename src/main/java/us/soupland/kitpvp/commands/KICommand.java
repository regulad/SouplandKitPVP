package us.soupland.kitpvp.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import us.soupland.kitpvp.KitPvP;
import us.soupland.kitpvp.koth.KothManager;
import us.soupland.kitpvp.server.ServerData;
import us.soupland.kitpvp.utilities.command.KitPvPCommand;

public class KICommand extends KitPvPCommand {

    private KothManager manager = KitPvP.getInstance().getKothManager();
    private ServerData serverData = KitPvP.getInstance().getServerData();

    public KICommand() {
        super("ki", null, "kothinfo");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        /*if (!manage.isActive()) {
            sender.sendMessage(ColorText.translate("&cKoth is not active!"));
        } else {
            if (serverData.getKothCuboID() == null) {
                sender.sendMessage(ColorText.translate("&cAn error occurred, try later again!"));
                return false;
            }
            Location location = serverData.getKothCuboID().getCenter();
            sender.sendMessage(ColorText.translate("&c" + StringUtils.repeat("-", 20)));
            sender.sendMessage(ColorText.translate("&4&lKoTH Information&7:"));
            sender.sendMessage("");
            sender.sendMessage(ColorText.translate("&7X: &f" + location.getBlockX()));
            sender.sendMessage(ColorText.translate("&7Y: &f" + location.getBlockY()));
            sender.sendMessage(ColorText.translate("&7Z: &f" + location.getBlockZ()));
            sender.sendMessage("");
            if (manager.getCapper() != null) {
                sender.sendMessage(ColorText.translate("&cCurrent Capper:"));
                sender.sendMessage(ColorText.translate(manager.getCapper().getDisplayName()));
                sender.sendMessage("");
                sender.sendMessage(ColorText.translate(" &4* &c" + DurationFormatUtils.formatDurationWords(manager.getRemaining() * 1000, true, true)));
            }
            sender.sendMessage(ColorText.translate("&c" + StringUtils.repeat("-", 20)));
        }*/
        return true;
    }
}