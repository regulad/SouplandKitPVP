package us.soupland.kitpvp.games.arguments;

import us.soupland.kitpvp.KitPvP;
import us.soupland.kitpvp.games.Game;
import us.soupland.kitpvp.games.GameHandler;
import us.soupland.kitpvp.games.GamePlayerState;
import us.soupland.kitpvp.games.events.PlayerLeaveGameEvent;
import us.soupland.kitpvp.utilities.chat.ColorText;
import us.soupland.kitpvp.utilities.command.KitPvPArgument;
import us.soupland.kitpvp.utilities.player.PlayerUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GameLeaveArgument extends KitPvPArgument {

    public GameLeaveArgument() {
        super("leave");
        onlyplayers = true;
    }

    @Override
    public String getUsage(String s) {
        return '/' + s + ' ' + name;
    }

    @Override
    public void onExecute(CommandSender sender, String label, String[] args) {
        GameHandler gameHandler = KitPvP.getInstance().getGameHandler();
        Game game;
        Player player = (Player) sender;

        if (gameHandler.getSpectators().contains(player)) {
            gameHandler.removeSpectator(player);
            player.sendMessage(ColorText.translate("&eYou're no longer in the event."));
            return;
        }
        if (gameHandler.getPlayers().contains(player)) {
            gameHandler.leave(player);
            player.sendMessage(ColorText.translate("&eYou're no longer in the event."));
            return;
        }
        if ((game = gameHandler.getUpcomingGame()) != null && game.getPlayers().containsKey(player)) {
            if (game.getPlayers(GamePlayerState.ALIVE).contains(player)) {
                game.eliminate(player);
            }
            game.getPlayers().put(player, GamePlayerState.REMOVED);
            player.sendMessage(ColorText.translate("&eYou're no longer in the event."));
            PlayerUtils.resetPlayer(player, false, true);
            new PlayerLeaveGameEvent(player, game).call();
            return;
        }
        if ((game = gameHandler.getActiveGame()) != null && game.getPlayers().containsKey(player)) {
            game.eliminate(player);
            game.getPlayers().put(player, GamePlayerState.REMOVED);
            /*if (game instanceof TvTSumoGame) {
                TvTSumoGame duoSumo = (TvTSumoGame) game;
                Team team = duoSumo.getByPlayer(player);
                if (team != null) {
                    team.getPlayers().put(player, GamePlayerState.REMOVED);
                }
            }*/
            player.sendMessage(ColorText.translate("&eYou're no longer in the event."));
            PlayerUtils.resetPlayer(player, false, true);
            new PlayerLeaveGameEvent(player, game).call();
            return;
        }
        player.sendMessage(ColorText.translate("&cYou're not in an events."));
    }
}