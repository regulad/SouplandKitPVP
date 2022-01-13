package us.soupland.kitpvp.games;

import us.soupland.kitpvp.games.arguments.*;
import us.soupland.kitpvp.utilities.command.KitPvPCommand;

public class GameCommand extends KitPvPCommand {

    public GameCommand() {
        super("game", null, "events", "event");
        registerArgument(new GameJoinArgument());
        registerArgument(new GameLeaveArgument());
        registerArgument(new GameHostArgument());
        registerArgument(new GameLocationArgument());
        registerArgument(new GameForceStartArgument());
        registerArgument(new GameStatusArgument());
        registerArgument(new GameStopArgument());
        registerArgument(new GameSpectateArgument());
    }
}