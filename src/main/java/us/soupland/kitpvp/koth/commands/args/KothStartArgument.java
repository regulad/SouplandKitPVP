package us.soupland.kitpvp.koth.commands.args;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import us.soupland.kitpvp.koth.Koth;
import us.soupland.kitpvp.utilities.chat.ColorText;
import us.soupland.kitpvp.utilities.command.KitPvPArgument;

public class KothStartArgument extends KitPvPArgument {

    public KothStartArgument() {
        super("start");
    }

    @Override
    public String getUsage(String s) {
        return '/' + s + ' ' + getName() + " <kothName> <seconds>";
    }


    @Override
    public void onExecute(CommandSender sender, String label, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(ColorText.translateAmpersand("&cUsage: " + getUsage(label)));
            return;
        }
        String kothName = args[1];
        Koth koth = Koth.getByName(kothName);
        if (koth == null) {
            sender.sendMessage(ColorText.translateAmpersand("&cA koth with that name doesn't exists. &7(/koth create " + kothName + ')'));
            return;
        }

        if (koth.isActive()) {
            sender.sendMessage(ColorText.translateAmpersand("&c" + kothName + " KoTH is already started. &7(/koth stop " + kothName + ')'));
            return;
        }

        int seconds = Integer.parseInt(args[2]);

        if (seconds <= 0) {
            sender.sendMessage(ColorText.translateAmpersand("&cInvalid seconds format."));
            return;
        }

        koth.setSeconds(seconds);
        koth.setRemaining(seconds);
        koth.setActive(true);
        Bukkit.broadcastMessage(ColorText.translateAmpersand("&4&l[KoTH] &9" + kothName + " &ehas started. &6(" + DurationFormatUtils.formatDuration((seconds * 1000L), "mm:ss") + ')'));

    }
}
