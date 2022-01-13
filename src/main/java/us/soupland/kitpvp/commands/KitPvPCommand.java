package us.soupland.kitpvp.commands;

import us.soupland.kitpvp.commands.kitpvp.KitProfileArgument;
import us.soupland.kitpvp.commands.kitpvp.KitReloadArgument;
import us.soupland.kitpvp.commands.kitpvp.KitResetArgument;

public class KitPvPCommand extends us.soupland.kitpvp.utilities.command.KitPvPCommand {

    public KitPvPCommand() {
        super("kitpvp");
        registerArgument(new KitProfileArgument());
        registerArgument(new KitReloadArgument());
        registerArgument(new KitResetArgument());
    }
}