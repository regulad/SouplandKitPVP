package us.soupland.kitpvp.games.arguments;

import org.bukkit.command.CommandSender;
import us.soupland.kitpvp.KitPvP;
import us.soupland.kitpvp.games.Game;
import us.soupland.kitpvp.games.GameHandler;
import us.soupland.kitpvp.utilities.KitPvPUtils;
import us.soupland.kitpvp.utilities.chat.ColorText;
import us.soupland.kitpvp.utilities.command.KitPvPArgument;

public class GameForceStartArgument extends KitPvPArgument {

    public GameForceStartArgument() {
        super("forcestart", null, KitPvPUtils.PERMISSION + "games.forcestart", "force");
    }

    @Override
    public String getUsage(String s) {
        return '/' + s + ' ' + name;
    }

    @Override
    public void onExecute(CommandSender sender, String label, String[] args) {
        GameHandler gameHandler = KitPvP.getInstance().getGameHandler();
        Game game = gameHandler.getUpcomingGame();
        if (game == null) {
            sender.sendMessage(ColorText.translateAmpersand("&cThere is currently no ongoing events!"));
            return;
        }
        gameHandler.setup();
        sender.sendMessage(ColorText.translateAmpersand("&eForcing game..."));
    }
}