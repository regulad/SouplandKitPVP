package us.soupland.kitpvp.games;

import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import us.soupland.kitpvp.KitPvP;
import us.soupland.kitpvp.games.arenas.GameMap;
import us.soupland.kitpvp.utilities.item.ItemMaker;
import us.soupland.kitpvp.utilities.player.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@RequiredArgsConstructor
@Data
public abstract class Game implements Listener {

    @Getter
    protected Map<Player, GamePlayerState> players = new HashMap<>();
    protected Player winner;
    @NonNull
    private String name;
    private ItemStack itemStack;

    public static Game getByName(String name) {
        for (Game game : KitPvP.getInstance().getGameHandler().getGames()) {
            if (game.getName().equalsIgnoreCase(name)) {
                return game;
            }
        }
        return null;
    }

    public abstract void start();

    public abstract void eliminate(Player player);

    public abstract String getDescription();

    public abstract List<String> getPlayerScoreboard(Player player);

    public abstract int getMaxPlayers();

    public abstract int getReward();

    public abstract int getCredits();

    public abstract Material getItem();

    public abstract String getPermission();

    public abstract boolean isTeams();

    public abstract int maxPlayersInTeams();

    public abstract Location getLocation();

    public abstract void finish(Player player);

    public abstract void spectator(Player player);

    public abstract void searchArena();

    public abstract void setArena(GameMap arena);

    public void quit(Player player) {
        this.eliminate(player);
        PlayerUtils.resetPlayer(player, false, true);
    }

    protected void eliminated(Player player) {
        player.setHealth(player.getMaxHealth());
        //KitsPlugin.getInstance().getProfileHandler().addEventSpectator(player);

        GameHandler gameHandler = KitPvP.getInstance().getGameHandler();

        gameHandler.addSpectator(player);

        if (gameHandler.getActiveGame() != null) {
            for (Player online : Bukkit.getOnlinePlayers()) {
                if (gameHandler.getActiveGame().getPlayers(GamePlayerState.SPECTATING).contains(online)) {
                    online.showPlayer(player);
                    player.showPlayer(online);
                    continue;
                }
                online.hidePlayer(player);
            }
        }

        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }

        PlayerInventory inventory = player.getInventory();

        inventory.setArmorContents(null);
        inventory.clear();

        player.setAllowFlight(true);
        player.setFlying(true);
        inventory.setItem(4, new ItemMaker(Material.INK_SACK).setDurability(1).setDisplayname("&c&lStop Spectating").addLore("&4Bound").setInteractRight(player1 -> player.performCommand("game leave")).create());
        player.updateInventory();
    }

    private void shuffle(List<Player> myList) {
        int n = myList.size();
        for (int i = 0; i < n; i++) {
            int randIdx = (int) (Math.random() * n);
            swap(myList, i, randIdx);
        }
    }

    private void swap(List<Player> list, int idx1, int idx2) {
        if (idx1 != idx2) { //don't do swap if the indexes to swap between are the same - skip it.
            Player tmp = list.get(idx1);
            list.set(idx1, list.get(idx2));
            list.set(idx2, tmp);
        }
    }

    public List<Player> getPlayers(GamePlayerState state) {
        List<Player> players2 = new ArrayList<>();
        for (Entry<Player, GamePlayerState> entry : players.entrySet()) {
            if (entry.getValue() == state) {
                players2.add(entry.getKey());
            }
        }
        shuffle(players2);
        return players2;
    }

    public void broadcast(String message) {
        for (Entry<Player, GamePlayerState> entry : players.entrySet()) {
            Player player = entry.getKey();
            if (entry.getValue() == GamePlayerState.REMOVED) continue;
            player.sendMessage(message);
        }
        for (Player player : KitPvP.getInstance().getGameHandler().getPlayers()) {
            player.sendMessage(message);
        }
    }

}
