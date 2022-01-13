package us.soupland.kitpvp.games;

import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.libs.joptsimple.internal.Strings;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

@Getter
public class Team {

    private HashMap<Player, GamePlayerState> players;
    private String name;

    public Team(HashMap<Player, GamePlayerState> players, String name) {
        this.players = players;
        this.name = name;
    }

    public static Team registerTeam(List<Player> members, String name) {
        HashMap<Player, GamePlayerState> players = new HashMap<>();

        for (Player player : members) {
            players.put(player, GamePlayerState.ALIVE);
        }

        return new Team(players, name);
    }

    public boolean isEliminated() {
        return !players.containsValue(GamePlayerState.ALIVE);
    }

    public void sendMessage(String message) {
        for (Player player : this.getPlayers().keySet()) {
            if (this.getPlayers().get(player) == GamePlayerState.REMOVED) continue;
            player.sendMessage(message);
        }
    }

    public String formatTeam() {
        List<String> hot = new ArrayList<>();
        for (Player player : this.getPlayers().keySet()) {
            if (this.getPlayers().get(player) == GamePlayerState.REMOVED) continue;
            hot.add(ChatColor.LIGHT_PURPLE + player.getName());
        }
        return Strings.join(hot, ChatColor.YELLOW + " & " + ChatColor.LIGHT_PURPLE);
    }

    public String formatColorTeam() {
        List<String> hot = new ArrayList<>();
        for (Player player : this.getPlayers().keySet()) {
            if (this.getPlayers().get(player) == GamePlayerState.REMOVED) continue;
            hot.add(ChatColor.WHITE + player.getDisplayName());
        }
        return Strings.join(hot, ChatColor.YELLOW + " & " + ChatColor.WHITE);
    }

    public void showPlayer(Player target) {
        for (Player player : this.getPlayers().keySet()) {
            if (this.getPlayers().get(player) == GamePlayerState.REMOVED) continue;
            player.showPlayer(target);
        }
    }

    public void hidePlayer(Player target) {
        for (Player player : this.getPlayers().keySet()) {
            if (this.getPlayers().get(player) == GamePlayerState.REMOVED) continue;
            player.hidePlayer(target);
        }
    }

    public void teleport(Location location) {
        for (Player player : this.getPlayers().keySet()) {
            if (this.getPlayers().get(player) == GamePlayerState.REMOVED) continue;
            player.teleport(location);
        }
    }


    public List<Player> getPlayers(GamePlayerState state) {
        List<Player> players2 = new ArrayList<>();
        for (Entry<Player, GamePlayerState> entry : players.entrySet()) {
            if (entry.getValue() == state) {
                players2.add(entry.getKey());
            }
        }
        return players2;
    }
}
