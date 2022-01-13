package us.soupland.kitpvp.games.arguments;

import us.soupland.kitpvp.KitPvP;
import us.soupland.kitpvp.enums.PlayerState;
import us.soupland.kitpvp.enums.Theme;
import us.soupland.kitpvp.games.GameHandler;
import us.soupland.kitpvp.profile.ProfileManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.soupland.kitpvp.utilities.KitPvPUtils;
import us.soupland.kitpvp.utilities.chat.ColorText;
import us.soupland.kitpvp.utilities.command.KitPvPArgument;

public class GameStopArgument extends KitPvPArgument {

    public GameStopArgument() {
        super("stop", null, KitPvPUtils.PERMISSION + "games.stop");
    }

    @Override
    public String getUsage(String s) {
        return '/' + s + ' ' + name;
    }

    @Override
    public void onExecute(CommandSender sender, String label, String[] args) {
        GameHandler gameHandler = KitPvP.getInstance().getGameHandler();
        if (gameHandler.getUpcomingGame() != null) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (ProfileManager.getProfile(player).getPlayerState() == PlayerState.INGAME) {
                    player.performCommand("game leave");
                }
            }
            gameHandler.setUpcomingGame(null);
            gameHandler.getPlayers().clear();
            gameHandler.destroy();
        } else if (gameHandler.getActiveGame() != null) {
            gameHandler.getActiveGame().finish(null);
        } else {
            sender.sendMessage(ColorText.translate("&cThere are no viewable events!"));
            return;
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            Theme theme = ProfileManager.getProfile(player).getTheme();
            player.sendMessage(ColorText.translate(theme.getPrimaryColor() + "&l[Event] &cThe events has forcefully ended!"));
        }
    }
}