package us.soupland.kitpvp.koth.commands.args;

import org.bukkit.command.CommandSender;
import us.soupland.kitpvp.koth.Koth;
import us.soupland.kitpvp.utilities.chat.ColorText;
import us.soupland.kitpvp.utilities.command.KitPvPArgument;

public class KothDeleteArgument extends KitPvPArgument {

    public KothDeleteArgument() {
        super("delete");
    }

    @Override
    public String getUsage(String s) {
        return '/' + s + ' ' + getName() + " <kothName>";
    }

    @Override
    public void onExecute(CommandSender sender, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ColorText.translate("&cUsage: " + getUsage(label)));
        } else {
            String kothName = args[1];

            Koth koth = Koth.getByName(kothName);
            if (koth == null) {
                sender.sendMessage(ColorText.translate("&cA koth with that name doesn't exists."));
                return;
            }
            koth.delete();
            sender.sendMessage(ColorText.translate("&c" + kothName + " KoTH has been deleted."));
        }
    }
}