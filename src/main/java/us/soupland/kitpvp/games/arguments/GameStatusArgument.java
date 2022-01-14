package us.soupland.kitpvp.games.arguments;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.CommandSender;
import us.soupland.kitpvp.KitPvP;
import us.soupland.kitpvp.games.Game;
import us.soupland.kitpvp.games.GameHandler;
import us.soupland.kitpvp.games.GamePlayerState;
import us.soupland.kitpvp.utilities.KitPvPUtils;
import us.soupland.kitpvp.utilities.chat.ColorText;
import us.soupland.kitpvp.utilities.command.KitPvPArgument;

public class GameStatusArgument extends KitPvPArgument {

    public GameStatusArgument() {
        super("status", null, KitPvPUtils.PERMISSION + "games.status");
    }

    @Override
    public String getUsage(String s) {
        return '/' + s + ' ' + name;
    }

    @Override
    public void onExecute(CommandSender sender, String label, String[] args) {
        GameHandler gameHandler = KitPvP.getInstance().getGameHandler();

        if (gameHandler.getUpcomingGame() == null && gameHandler.getActiveGame() == null) {
            sender.sendMessage(ColorText.translateAmpersand("&cThere are no viewable events!"));
            return;
        }
        Game game = gameHandler.getUpcomingGame();
        if (game != null) {
            sender.sendMessage(ColorText.translateAmpersand("&7&m" + StringUtils.repeat("-", 12) + "&r &4&lEvent &7&m" + StringUtils.repeat("-", 12)));
            sender.sendMessage(ColorText.translateAmpersand("&eUpcoming Event&7: &f" + game.getName()));
            sender.sendMessage(" ");
            sender.sendMessage(ColorText.translateAmpersand("&4&l" + game.getName() + " &eis joinable. &7(/join)"));
            sender.sendMessage(ColorText.translateAmpersand("&eParticipants: &f" + gameHandler.getPlayers().size() + '/' + game.getMaxPlayers()));
            sender.sendMessage(ColorText.translateAmpersand("&7&m" + StringUtils.repeat("-", 30)));
            return;
        }
        if ((game = gameHandler.getActiveGame()) != null) {
            sender.sendMessage(ColorText.translateAmpersand("&7&m" + StringUtils.repeat("-", 12) + "&r &4&lEvent &7&m" + StringUtils.repeat("-", 12)));
            sender.sendMessage(ColorText.translateAmpersand("&eActive Event&7: &f" + game.getName()));
            sender.sendMessage(" ");
            sender.sendMessage(ColorText.translateAmpersand("&4&l" + game.getName() + " &cis not joinable. &7(/game spectate)"));
            sender.sendMessage(ColorText.translateAmpersand("&eParticipants: &f" + game.getPlayers(GamePlayerState.ALIVE).size() + '/' + (game.getPlayers(GamePlayerState.ALIVE).size() + game.getPlayers(GamePlayerState.DEAD).size())));
            sender.sendMessage(ColorText.translateAmpersand("&7&m" + StringUtils.repeat("-", 30)));
            return;
        }
        sender.sendMessage(ColorText.translateAmpersand("&cThere are no viewable events!"));
    }
}