package us.soupland.kitpvp.practice.match;

import lombok.Getter;
import lombok.Setter;
import us.soupland.kitpvp.KitPvP;
import us.soupland.kitpvp.enums.PlayerItem;
import us.soupland.kitpvp.enums.PlayerState;
import us.soupland.kitpvp.practice.arena.Arena;
import us.soupland.kitpvp.practice.kit.Kit;
import us.soupland.kitpvp.practice.ladder.Ladder;
import us.soupland.kitpvp.practice.match.player.MatchPlayer;
import us.soupland.kitpvp.practice.match.task.MatchResetTask;
import us.soupland.kitpvp.practice.match.task.MatchStartTask;
import us.soupland.kitpvp.profile.Profile;
import us.soupland.kitpvp.profile.ProfileManager;
import us.soupland.kitpvp.utilities.chat.ChatUtil;
import us.soupland.kitpvp.utilities.chat.ColorText;
import us.soupland.kitpvp.utilities.elo.EloUtil;
import us.soupland.kitpvp.utilities.location.LocationUtils;
import us.soupland.kitpvp.utilities.player.PlayerUtils;
import net.minecraft.server.v1_8_R3.EntityLightning;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityWeather;
import org.apache.commons.lang.*;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;
import us.soupland.kitpvp.utilities.task.TaskUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class Match {

    private UUID queuedId;
    @Setter
    private Ladder ladder;
    private Arena arena;
    private boolean ranked;
    @Setter
    private MatchState state;
    @Setter
    private long startTimestamp;
    private Player firstPlayer, secondPlayer, winner;
    private MatchPlayer firstMatchPlayer, secondMatchPlayer;
    private List<Location> placedBlocks;
    private List<BlockState> changedBlocks;
    private int firstPlayerRoundWins, secondPlayerRoundWins;
    private Kit kit;
    private List<MatchSnapshot> snapshots;

    public Match(UUID queuedId, Ladder ladder, Arena arena, boolean ranked, Player... players) {
        this.queuedId = queuedId;
        this.ladder = ladder;
        this.arena = arena;
        this.ranked = ranked;
        this.state = MatchState.STARTING;
        this.firstPlayer = players[0];
        this.secondPlayer = players[1];
        this.winner = null;
        this.firstMatchPlayer = new MatchPlayer(firstPlayer);
        this.secondMatchPlayer = new MatchPlayer(secondPlayer);
        this.placedBlocks = new ArrayList<>();
        this.changedBlocks = new ArrayList<>();
        this.kit = ladder.getPlayerKit();
        this.snapshots = new ArrayList<>();

        MatchHandler.registerMatch(this);
    }

    private void prepareFight() {
        MatchPlayer first = getFirstMatchPlayer();
        MatchPlayer second = getSecondMatchPlayer();

        first.setAlive(true);
        second.setAlive(true);

        for (Player player : Bukkit.getOnlinePlayers()) {
            firstPlayer.hidePlayer(player);
            secondPlayer.hidePlayer(player);
        }

        firstPlayer.showPlayer(secondPlayer);
        secondPlayer.showPlayer(firstPlayer);

        if (arena.getFirstPosition() != null) {
            firstPlayer.teleport(arena.getFirstPosition());
        }
        if (arena.getSecondPosition() != null) {
            secondPlayer.teleport(arena.getSecondPosition());
        }

        for (Player player : new Player[]{firstPlayer, secondPlayer}) {
            for (PotionEffect effect : player.getActivePotionEffects()) {
                player.removePotionEffect(effect.getType());
            }
            if (getLadder().getName().toLowerCase().contains("sumo")) {
                PlayerUtils.managePlayerMovement(player, false);
            }
            player.setAllowFlight(false);
            player.setFlying(false);
            player.setGameMode(GameMode.SURVIVAL);
            player.setHealth(player.getMaxHealth());
            player.setFoodLevel(20);

            player.getInventory().clear();
            player.getInventory().setArmorContents(null);

            kit.onEquip(player);
            /*if (ladder.getKnockbackProfile() != null) {
                KnockbackProfile knockbackProfile = ladder.getKnockbackProfile();
                ((CraftPlayer) player).getHandle().setKnockback(knockbackProfile);
            }*/
        }
    }

    public void handleStart() {
        prepareFight();
        setState(MatchState.STARTING);
        setStartTimestamp(-1);

        for (Player player : new Player[]{firstPlayer, secondPlayer}) {
            if (player != null) {
                Profile profile = ProfileManager.getProfile(player);
                profile.setPlayerState(PlayerState.FIGHTINGPRACTICE);
                profile.setMatch(this);
            }
        }

        TaskUtil.runTaskTimer(new MatchStartTask(this), 20L, 20L);
    }

    private void handleEnd() {
        getArena().setActive(false);
        setState(MatchState.ENDING);

        Player firstPlayer = getFirstPlayer();
        Player secondPlayer = getSecondPlayer();

        for (Player player : new Player[]{firstPlayer, secondPlayer}) {
            if (player != null) {
                Profile profile = ProfileManager.getProfile(player);

                PlayerInventory inventory = player.getInventory();

                MatchPlayer matchPlayer = getMatchPlayer(player);

                MatchSnapshot snapshot = new MatchSnapshot(matchPlayer);
                snapshot.setSwitchTo(getMatchPlayer(getOpponent(player)));
                snapshot.setPotion(kit.getName().equalsIgnoreCase("PotPvP"));
                snapshots.add(snapshot);

                profile.setCurrentKit(null);
                profile.setPlayerState(PlayerState.SPAWNPRACTICE);

                player.setHealth(player.getMaxHealth());
                player.setFoodLevel(20);

                inventory.clear();
                inventory.setArmorContents(null);

                for (PotionEffect effect : player.getActivePotionEffects()) {
                    player.removePotionEffect(effect.getType());
                }

                Location location = LocationUtils.getLocation(KitPvP.getInstance().getServerData().getSpawnPractice());

                if (location != null) {
                    player.teleport(location);
                }

                inventory.setItem(0, PlayerItem.SPAWN_PRACTICE_CUSTOM_DUEL.getItem());
                inventory.setItem(8, PlayerItem.SPAWN_PRACTICE_RETURN_SPAWN.getItem());

                player.setFireTicks(0);
                //((CraftPlayer) player).getHandle().setKnockback(null);
                ProfileManager.getProfile(player).setMatch(null);
                player.updateInventory();

                for (Player online : Bukkit.getOnlinePlayers()) {
                    player.showPlayer(online);
                }

                //PlayerUtils.resetPlayer(player, false, true);
            }
        }

        snapshots.forEach(matchSnapshot -> {
            matchSnapshot.setCreatedAt(System.currentTimeMillis());
            MatchSnapshot.getSnapshotMap().put(matchSnapshot.getMatchPlayer().getUuid(), matchSnapshot);
        });

        new MatchResetTask(this).runTask(KitPvP.getInstance());
        onFinish();

        MatchHandler.unregisterMatch(this);
    }

    public void handleDeath(Player damaged, Player killer, boolean disconnected) {
        MatchPlayer matchPlayer = getMatchPlayer(damaged);

        if (!matchPlayer.isAlive()) {
            return;
        }

        matchPlayer.setAlive(false);
        matchPlayer.setDisconnected(disconnected);

        List<Player> players = new ArrayList<>();
        players.add(getFirstPlayer());
        players.add(getSecondPlayer());

        EntityLightning lightning = new EntityLightning(((CraftWorld) damaged.getWorld()).getHandle(), damaged.getLocation().getX(), damaged.getLocation().getY(), damaged.getLocation().getZ());
        PacketPlayOutSpawnEntityWeather lightningPacket = new PacketPlayOutSpawnEntityWeather(lightning);

        players.forEach(other -> {
            if (other != null) {
                other.playSound(damaged.getLocation(), Sound.AMBIENCE_THUNDER, 1.0F, 1.0F);
                ((CraftPlayer) other).getHandle().playerConnection.sendPacket(lightningPacket);
            }
        });

        for (Player player : players) {
            String deadName = ColorText.translate("&c" + damaged.getName());

            if (player == null) {
                continue;
            }
            if (damaged == player) {
                deadName = ColorText.translate("&a" + player.getName());
            }

            if (matchPlayer.isDisconnected()) {
                player.sendMessage(ColorText.translate(deadName + " &chas disconnected."));
                continue;
            }

            String killerName = null;

            if (killer != null) {
                killerName = ColorText.translate((killer == player ? "&a" : "&c") + killer.getName());
            }

            if (killerName != null) {
                player.sendMessage(ColorText.translate(deadName + " &7was killed by " + killerName + "&7."));
            } else {
                player.sendMessage(ColorText.translate(deadName + " &7has died."));
            }
        }

        onPlayerDeath(damaged);

        if (canFinish()) {
            handleEnd();
        }
    }

    private void onPlayerDeath(Player player) {
        MatchPlayer roundWinner = getMatchPlayer(getOpponent(player));

        winner = getOpponent(player);

        snapshots.add(new MatchSnapshot(getMatchPlayer(player), roundWinner));

        if (getLadder().getName().toLowerCase().contains("sumo")) {
            if (player == getFirstPlayer()) {
                firstPlayerRoundWins++;
            } else {
                secondPlayerRoundWins++;
            }

            if (canFinish()) {
                setState(MatchState.ENDING);
                broadcastMessage("&a" + roundWinner.getPlayer().getName() + " &7won the match.");
                getOpponent(player).hidePlayer(player);
            } else {
                broadcastMessage("&a" + roundWinner.getPlayer().getName() + " &7won the round, &ahe&7/&dshe &7needs &e" + getRoundsNeeded(roundWinner.getPlayer()) + " &7more to win.");
                handleStart();
            }
        }
    }

    public void handleRespawn(Player player) {
        player.setVelocity(new Vector());

        onPlayerRespawn(player);
    }

    private void onPlayerRespawn(Player player) {
        if (getLadder().getName().toLowerCase().contains("sumo") && getState() != MatchState.ENDING) {
            player.teleport(getArena().getFirstPosition());
            getOpponent(player).teleport(getArena().getSecondPosition());
        } else {
            player.teleport(player.getLocation());
        }
    }

    private int getRoundsNeeded(Player player) {
        if (player == getFirstPlayer()) {
            return 3 - getFirstPlayerRoundWins();
        } else if (player == getSecondPlayer()) {
            return 3 - getSecondPlayerRoundWins();
        }
        return 0;
    }

    private boolean canFinish() {
        if (getLadder().getName().toLowerCase().contains("sumo")) {
            return getFirstMatchPlayer().isDisconnected() || getSecondMatchPlayer().isDisconnected() || (isRanked() ? (firstPlayerRoundWins == 3 || secondPlayerRoundWins == 3) : (firstPlayerRoundWins == 1 || secondPlayerRoundWins == 1));
        }
        return !getFirstMatchPlayer().isAlive() || !getSecondMatchPlayer().isAlive();
    }

    private void onFinish() {
        Player winner = getWinner();
        Player loser = getOpponent(winner);
        MatchPlayer winnerMatchPlayer = getMatchPlayer(winner);
        MatchPlayer loserMatchPlayer = getMatchPlayer(loser);

        int oldWinnerElo, oldLoserElo, newWinnerElo = 0, newLoserElo = 0, winnerEloChange = 0, loserEloChange = 0;

        if (isRanked()) {
            oldWinnerElo = winnerMatchPlayer.getPlayerElo();
            oldLoserElo = loserMatchPlayer.getPlayerElo();
            newWinnerElo = EloUtil.getNewRating(oldWinnerElo, oldLoserElo, true);
            newLoserElo = EloUtil.getNewRating(oldLoserElo, oldWinnerElo, false);

            winnerMatchPlayer.setPlayerElo(newWinnerElo);
            loserMatchPlayer.setPlayerElo(newLoserElo);

            winnerEloChange = newWinnerElo - oldWinnerElo;
            loserEloChange = newLoserElo - oldLoserElo;

        }

        for (Player player : new Player[]{winner, loser}) {
            if (player != null) {
                player.sendMessage(ColorText.translate("&7&m" + StringUtils.repeat("-", 30)));
                player.sendMessage(ColorText.translate("&6&lInventories &7(Clickable names)"));
                new ChatUtil(" &aWinner: &f" + winner.getName(), "&7Click to view this player's inventory", "/viewinv " + winner.getUniqueId().toString()).send(player);
                new ChatUtil(" &cLoser: &f" + loser.getName(), "&7Click to view this player's inventory", "/viewinv " + loser.getUniqueId().toString()).send(player);
                if (isRanked()) {
                    player.sendMessage(ColorText.translate(" &eELO Changes: &a" + winner.getName() + " +" + winnerEloChange + " (" + newWinnerElo + ") &c" + loser.getName() + " -" + loserEloChange + " (" + newLoserElo + ')'));
                }
                player.sendMessage(ColorText.translate("&7&m" + StringUtils.repeat("-", 30)));
            }
        }
    }

    public MatchPlayer getMatchPlayer(Player player) {
        if (player == firstPlayer) {
            return getFirstMatchPlayer();
        }
        if (player == secondPlayer) {
            return getSecondMatchPlayer();
        }
        return new MatchPlayer(player);
    }

    public Player getOpponent(Player player) {
        return player == firstPlayer ? secondPlayer : firstPlayer;
    }

    public void playSound(Sound sound) {
        for (Player player : new Player[]{firstPlayer, secondPlayer}) {
            if (player != null) {
                player.playSound(player.getLocation(), sound, 1, 1);
            }
        }
    }

    public void broadcastMessage(String message) {
        for (Player player : new Player[]{firstPlayer, secondPlayer}) {
            if (player != null) {
                player.sendMessage(ColorText.translate(message));
            }
        }
    }
}