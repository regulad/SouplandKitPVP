package us.soupland.kitpvp.games.arguments;

import us.soupland.kitpvp.KitPvP;
import us.soupland.kitpvp.enums.PlayerState;
import us.soupland.kitpvp.games.GameHandler;
import us.soupland.kitpvp.profile.Profile;
import us.soupland.kitpvp.profile.ProfileManager;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import us.soupland.kitpvp.utilities.chat.ColorText;
import us.soupland.kitpvp.utilities.command.KitPvPArgument;
import us.soupland.kitpvp.utilities.item.ItemMaker;

public class GameSpectateArgument extends KitPvPArgument {

    public GameSpectateArgument() {
        super("spectate", "Participate in the events as spectator");
        onlyplayers = true;
    }

    @Override
    public String getUsage(String s) {
        return '/' + s + ' ' + name;
    }

    @Override
    public void onExecute(CommandSender sender, String label, String[] args) {
        Player player = (Player) sender;
        Profile profile = ProfileManager.getProfile(player);
        GameHandler gameHandler = KitPvP.getInstance().getGameHandler();
        if (profile.getPlayerState() == PlayerState.SPAWN || profile.getPlayerState() == PlayerState.PLAYING) {
            if (profile.getPlayerCombat() > 0L && profile.getPlayerState() == PlayerState.PLAYING) {
                sender.sendMessage(ColorText.translate("&cYou must not be Spawn-Tagged."));
                return;
            }
            if (gameHandler.getSpectators().contains(player)) {
                sender.sendMessage(ColorText.translate("&cYou are already spectating an event."));
                return;
            }
            if (gameHandler.getActiveGame() != null) {
                for (PotionEffect effect : player.getActivePotionEffects()) {
                    player.removePotionEffect(effect.getType());
                }
                profile.setPlayerState(PlayerState.INGAME);
                player.setGameMode(GameMode.CREATIVE);
                gameHandler.addSpectator(player);
                PlayerInventory inventory = player.getInventory();
                inventory.clear();
                inventory.setArmorContents(null);
                inventory.setItem(4, new ItemMaker(Material.INK_SACK).setDurability(1).setDisplayname("&c&lStop Spectating").addLore("&4Bound").setPlayerInteract(player1 -> gameHandler.removeSpectator(player)).create());
                player.updateInventory();
            } else {
                sender.sendMessage(ColorText.translate("&cThere is no events that is active."));
            }
        } else {
            sender.sendMessage(ColorText.translate("&cYou're unable to spectate events."));
        }
    }
}