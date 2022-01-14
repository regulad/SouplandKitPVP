package us.soupland.kitpvp.koth.commands.args;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.soupland.kitpvp.koth.Koth;
import us.soupland.kitpvp.koth.selection.KothListener;
import us.soupland.kitpvp.koth.selection.KothSelection;
import us.soupland.kitpvp.utilities.chat.ColorText;
import us.soupland.kitpvp.utilities.command.KitPvPArgument;
import us.soupland.kitpvp.utilities.cuboid.Cuboid;

public class KothCreateArgument extends KitPvPArgument {

    public KothCreateArgument() {
        super("create");
        setOnlyplayers(true);
    }

    @Override
    public String getUsage(String s) {
        return '/' + s + ' ' + getName() + " <kothName>";
    }

    @Override
    public void onExecute(CommandSender sender, String label, String[] args) {
        Player player = (Player) sender;
        if (args.length < 2) {
            player.sendMessage(getUsage(label));
            return;
        }
        String kothName = args[1];
        if (Koth.getByName(kothName) != null) {
            player.sendMessage(ColorText.translate("&cA koth with that name already exists."));
            return;
        }

        KothSelection kothSelection = KothListener.getByPlayer(player);

        if (kothSelection == null) {
            player.sendMessage(ColorText.translate("&cFirst you need to define the cap zone"));
            return;
        }

        if (kothSelection.getFirst() == null) {
            player.sendMessage(ColorText.translate("&cFirst point no selected"));
            return;
        }
        if (kothSelection.getSecond() == null) {
            player.sendMessage(ColorText.translate("&cSecond point no selected"));
            return;
        }

        Koth koth = new Koth(kothName);

        koth.setCuboid(new Cuboid(kothSelection.getFirst(), kothSelection.getSecond()));

        player.sendMessage(ColorText.translate("&aKoth created successfully"));
    }
}
