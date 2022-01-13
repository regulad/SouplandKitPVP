package us.soupland.kitpvp.koth.commands.args;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import us.soupland.kitpvp.koth.Koth;
import us.soupland.kitpvp.utilities.chat.ColorText;
import us.soupland.kitpvp.utilities.command.KitPvPArgument;

public class KothStopArgument extends KitPvPArgument {

    public KothStopArgument() {
        super("stop");
    }

    @Override
    public String getUsage(String s) {
        return '/' + s + ' ' + getName() + " <kothName>";
    }

    @Override
    public void onExecute(CommandSender sender, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ColorText.translate("&cUsage: " + getUsage(label)));
            return;
        }
        String kothName = args[1];
        Koth koth = Koth.getByName(kothName);
        if (koth == null) {
            sender.sendMessage(ColorText.translate("&cA koth with that name doesn't exists. &7(/koth create " + kothName + ')'));
            return;
        }

        if (!koth.isActive()) {
            sender.sendMessage(ColorText.translate("&c" + kothName + " KoTH is not started. &7(/koth start " + kothName + ')'));
            return;
        }

        koth.setActive(false);
        Bukkit.broadcastMessage(ColorText.translate("&4&l[KoTH] &9" + kothName + " &chas been stopped."));
    }
}