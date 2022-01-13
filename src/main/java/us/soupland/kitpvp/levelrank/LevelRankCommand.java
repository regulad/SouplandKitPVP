package us.soupland.kitpvp.levelrank;

import us.soupland.kitpvp.levelrank.arguments.*;
import us.soupland.kitpvp.utilities.command.KitPvPCommand;

public class LevelRankCommand extends KitPvPCommand {

    public LevelRankCommand() {
        super("levelrank");
        registerArgument(new LevelCheckArgument());
        registerArgument(new LevelSetRankArgument());
        registerArgument(new LevelResetCommand());
        registerArgument(new LevelRankUpArgument());
        registerArgument(new LevelRankDownArgument());
    }
}
