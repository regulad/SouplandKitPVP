package us.soupland.kitpvp.koth.commands;

import us.soupland.kitpvp.koth.commands.args.*;
import us.soupland.kitpvp.utilities.command.KitPvPCommand;

import java.util.Arrays;

public class KothCommands extends KitPvPCommand {

    public KothCommands() {
        super("koth");

        Arrays.asList(new KothCreateArgument(), new KothDeleteArgument(), new KothStartArgument(),
                new KothWandArgument(), new KothStopArgument(), new KothListArgument()).forEach(this::registerArgument);
    }
}
