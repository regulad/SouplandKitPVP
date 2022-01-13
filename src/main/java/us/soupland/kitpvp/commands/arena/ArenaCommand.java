package us.soupland.kitpvp.commands.arena;

import us.soupland.kitpvp.commands.arena.arguments.*;
import us.soupland.kitpvp.utilities.command.KitPvPCommand;

public class ArenaCommand extends KitPvPCommand {

    public ArenaCommand() {
        super("arena");
        registerArgument(new ArenaCreateArgument());
        registerArgument(new ArenaDeleteArgument());
        registerArgument(new ArenaListArgument());
        registerArgument(new ArenaSaveArgument());
        registerArgument(new ArenaSetSpawnArgument());
        registerArgument(new ArenaTeleportArgument());
        registerArgument(new ArenaLadderArgument());
        registerArgument(new ArenaGenerateArgument());
        registerArgument(new ArenaGenHelpArgument());
    }
}