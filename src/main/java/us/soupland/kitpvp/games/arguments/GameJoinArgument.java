package us.soupland.kitpvp.games.arguments;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.soupland.kitpvp.KitPvP;
import us.soupland.kitpvp.enums.PlayerState;
import us.soupland.kitpvp.games.GameHandler;
import us.soupland.kitpvp.profile.Profile;
import us.soupland.kitpvp.profile.ProfileManager;
import us.soupland.kitpvp.utilities.chat.ColorText;
import us.soupland.kitpvp.utilities.command.KitPvPArgument;

public class GameJoinArgument extends KitPvPArgument {

    public GameJoinArgument() {
        super("join");
        onlyplayers = true;
    }

    @Override
    public String getUsage(String s) {
        return '/' + s + ' ' + name;
    }

    @Override
    public void onExecute(CommandSender sender, String label, String[] args) {
        Profile profile = ProfileManager.getProfile((Player) sender);
        if (profile.getPlayerState().name().contains("PRACTICE")) {
            sender.sendMessage(ColorText.translate("&cYou're currently in 1v1, you must be in spawn to join events."));
            return;
        }
        if (profile.getPlayerState() == PlayerState.SPAWN || profile.getPlayerState() == PlayerState.PLAYING) {
            /*if (profile.getPlayerCombat() > 0L) {
                sender.sendMessage(ColorText.translate("&cYou must not be Spawn-Tagged."));
                return;
            }*/
            GameHandler gameHandler = KitPvP.getInstance().getGameHandler();
            /*if (gameHandler.getActiveGame() != null) {
                sender.sendMessage(ColorText.translate("&cThe events has already began!"));
                return;
            }
            if (gameHandler.getUpcomingGame() == null) {
                sender.sendMessage(ColorText.translate("&cThere is currently no ongoing events!"));
                return;
            }
            if (gameHandler.getPlayers().contains(sender)) {
                sender.sendMessage(ColorText.translate("&cYou're already in the events!"));
                sender.sendMessage(ColorText.translate("&cType /leave to leave the events."));
                return;
            }
            if (gameHandler.getPlayers().size() >= gameHandler.getUpcomingGame().getMaxPlayers()) {
                sender.sendMessage(ColorText.translate("&cThe event is full."));
                return;
            }*/
            gameHandler.join((Player) sender);
            profile.setCurrentKit(null);
            sender.sendMessage(ColorText.translate("&aYou have now joined the event."));
        } else {
            sender.sendMessage(ColorText.translate("&cYou're unable to join events!"));
        }
    }
}