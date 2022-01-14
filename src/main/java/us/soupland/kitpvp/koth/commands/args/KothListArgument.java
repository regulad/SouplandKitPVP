package us.soupland.kitpvp.koth.commands.args;

import org.bukkit.command.CommandSender;
import us.soupland.kitpvp.koth.Koth;
import us.soupland.kitpvp.utilities.chat.ColorText;
import us.soupland.kitpvp.utilities.command.KitPvPArgument;

public class KothListArgument extends KitPvPArgument {

    public KothListArgument() {
        super("list");
    }

    @Override
    public String getUsage(String s) {
        return '/' + s + ' ' + getName();
    }

    @Override
    public void onExecute(CommandSender sender, String label, String[] args) {
        if (Koth.getKoths().isEmpty()) {
            sender.sendMessage(ColorText.translateAmpersand("&cThere are no koths created."));
            return;
        }
        sender.sendMessage(ColorText.translateAmpersand("&6KOTHS (" + Koth.getKoths().size() + ')'));
        Koth.getKoths().forEach((s, koth) -> sender.sendMessage(ColorText.translateAmpersand("&6" + koth.getName() + ": &7" + koth.getRemaining() + (koth.isActive() ? " &a[ACTIVE]" : ""))));
    }
}