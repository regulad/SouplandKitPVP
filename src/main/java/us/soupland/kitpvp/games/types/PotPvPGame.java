package us.soupland.kitpvp.games.types;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import us.soupland.kitpvp.KitPvP;
import us.soupland.kitpvp.enums.Theme;
import us.soupland.kitpvp.games.Game;
import us.soupland.kitpvp.games.GamePlayerState;
import us.soupland.kitpvp.games.GameState;
import us.soupland.kitpvp.games.arenas.GameMap;
import us.soupland.kitpvp.games.events.GameStartedEvent;
import us.soupland.kitpvp.games.events.PlayerWinGameEvent;
import us.soupland.kitpvp.profile.ProfileManager;
import us.soupland.kitpvp.utilities.KitPvPUtils;
import us.soupland.kitpvp.utilities.chat.ColorText;
import us.soupland.kitpvp.utilities.item.ItemMaker;
import us.soupland.kitpvp.utilities.player.PlayerUtils;
import us.soupland.kitpvp.utilities.task.TaskUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

public class PotPvPGame extends Game {

    private GameState gameState;
    private Player firstPlayer, secondPlayer;
    private int round, i;
    private List<Player> alreadyPlayed;
    private GameMap gameMap;

    public PotPvPGame() {
        super("PotPvP Brackets");
        this.setItemStack(new ItemMaker(Material.POTION).setDurability(16421).create());
    }

    @Override
    public void start() {
        this.gameState = GameState.STARTING;
		/*for(Chunk chunk : KitsPlugin.getInstance().getRegionHandler().getRegion("event_sumo").getChunks()) {
			chunk.load();
		}*/

        this.round = 0;
        this.alreadyPlayed = new ArrayList<>();

        for (Player spectators : KitPvP.getInstance().getGameHandler().getSpectators()) {
            spectators.teleport(getLocation());
        }

        for (Entry<Player, GamePlayerState> entry : getPlayers().entrySet()) {
            Player player = entry.getKey();
            player.teleport(getLocation());
        }

        Bukkit.getScheduler().runTaskLater(KitPvP.getInstance(), this::startNewRound, 60L);

    }

    private void startNewRound() {
        this.gameState = GameState.STARTING;
        round++;

        int already = 0;
        int playing = 0;
        for (Player player : this.getPlayers(GamePlayerState.ALIVE)) {
            if (player == null) continue;
            if (this.alreadyPlayed.contains(player)) {
                already++;
            }
            playing++;
        }

        if (playing <= already) {
            this.alreadyPlayed.clear();
        }
        i = 3;
        Player firstFound = null, secondFound = null;

        for (Player player : this.getPlayers(GamePlayerState.ALIVE)) {
            if (player == null) continue;
            if (this.alreadyPlayed.contains(player)) continue;
            if (this.firstPlayer == player) continue;
            //this.player2 = player;
            secondFound = player;
            break;
        }

        for (Player player : this.getPlayers(GamePlayerState.ALIVE)) {
            if (player == null) continue;
            if (this.alreadyPlayed.contains(player)) continue;
            if (this.secondPlayer == player) continue;
            if (secondFound == player) continue;
            //this.player1 = player;
            firstFound = player;
            break;
        }


        if (secondFound == null) {
            this.alreadyPlayed.clear();
            for (Player player : this.getPlayers(GamePlayerState.ALIVE)) {
                if (player == null) continue;
                if (this.alreadyPlayed.contains(player)) continue;
                if (this.firstPlayer == player) continue;
                if (firstFound == player) continue;
                //this.player2 = player;
                secondFound = player;
                break;
            }
        }

        if (firstFound == null) {
            this.alreadyPlayed.clear();
            for (Player player : this.getPlayers(GamePlayerState.ALIVE)) {
                if (player == null) continue;
                if (this.alreadyPlayed.contains(player)) continue;
                if (this.secondPlayer == player) continue;
                if (secondFound == player) continue;
                //this.player1 = player;
                firstFound = player;
                break;
            }
        }
        firstPlayer = firstFound;
        secondPlayer = secondFound;

        if (playing == 2) {
            for (Player player : this.getPlayers(GamePlayerState.ALIVE)) {
                if (secondPlayer == player) continue;
                firstPlayer = player;
            }
            for (Player player : this.getPlayers(GamePlayerState.ALIVE)) {
                if (firstPlayer == player) continue;
                secondPlayer = player;
            }
        }

        for (Player player : new Player[]{firstPlayer, secondPlayer}) {
            if (player == null) {
                continue;
            }
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200000, 1));

            PlayerInventory inventory = player.getInventory();

            inventory.setHelmet(new ItemMaker(Material.DIAMOND_HELMET).setEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).setUnbreakable(true).create());
            inventory.setChestplate(new ItemMaker(Material.DIAMOND_CHESTPLATE).setEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).setUnbreakable(true).create());
            inventory.setLeggings(new ItemMaker(Material.DIAMOND_LEGGINGS).setEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).setUnbreakable(true).create());
            inventory.setBoots(new ItemMaker(Material.DIAMOND_BOOTS).setEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).setUnbreakable(true).create());

            inventory.setItem(0, new ItemMaker(Material.DIAMOND_SWORD).setUnbreakable(true).setEnchant(Enchantment.DAMAGE_ALL, 2).create());
            inventory.setItem(1, new ItemMaker(Material.ENDER_PEARL).setAmount(16).create());
            inventory.setItem(8, new ItemMaker(Material.BAKED_POTATO).setAmount(64).create());

            for (int i = 0; i < 36; i++) {
                inventory.addItem(new ItemMaker(Material.POTION).setDurability(16421).create());
            }

            player.updateInventory();
        }

        new BukkitRunnable() {
            public void run() {
                if (KitPvP.getInstance().getGameHandler().getActiveGame() == null) {
                    this.cancel();
                }
                if (i == 0) {
                    newFight();
                    this.cancel();
                }
                switch (i) {
                    case 3:
                    case 2:
                    case 1:
                        for (Entry<Player, GamePlayerState> entry : getPlayers().entrySet()) {
                            if (entry.getValue() == GamePlayerState.REMOVED) continue;
                            entry.getKey().playSound(entry.getKey().getLocation(), Sound.NOTE_PIANO, 1L, 1L);
                        }
                        broadcast(ColorText.translateAmpersand("&7[&4Round #" + round + "&7] &fStarting in &c" + i + " second(s)"));
                        break;
                    default:
                        break;
                }

                i--;
            }
        }.runTaskTimerAsynchronously(KitPvP.getInstance(), 20L, 20L);
    }

    private void newFight() {
        this.gameState = GameState.STARTED;
        for (Entry<Player, GamePlayerState> entry : getPlayers().entrySet()) {
            if (entry.getValue() == GamePlayerState.REMOVED) {
                continue;
            }
            TaskUtil.runTaskLater(() -> {
                entry.getKey().showPlayer(firstPlayer);
                entry.getKey().showPlayer(secondPlayer);
                entry.getKey().playSound(entry.getKey().getLocation(), Sound.NOTE_PIANO, 1L, 20L);
            }, 2L);
        }
        if (gameMap != null) {
            Location location = gameMap.getLocations().get(1);
            if (location != null) {
                firstPlayer.teleport(location);
            }
            if ((location = gameMap.getLocations().get(2)) != null) {
                secondPlayer.teleport(location);
            }
        }
        for (Player player : getPlayers().keySet()) {
            if (player == null) {
                continue;
            }
            Theme theme = ProfileManager.getProfile(player).getTheme();
            player.sendMessage(ColorText.translateAmpersand("&2\u2713 " + theme.getPrimaryColor() + "Next Round: " + firstPlayer.getName() + theme.getPrimaryColor() + " vs " + secondPlayer.getName()));
        }
    }


    private boolean isWinner() {
        return getPlayers(GamePlayerState.ALIVE).size() <= 1;
    }

    public void eliminate(Player player) {
        if (getPlayers(GamePlayerState.DEAD).contains(player)) {
            return;
        }
        if (this.gameState == GameState.ENDED) return;
        getPlayers().put(player, GamePlayerState.DEAD);

        if (firstPlayer == player || secondPlayer == player) {

            gameState = GameState.ENDED;
            alreadyPlayed.add(getOpponent(player));
            alreadyPlayed.add(player);
        }
        if (this.isWinner()) {
            this.finish(this.getPlayers(GamePlayerState.ALIVE).get(0));
        } else {
            if (firstPlayer == player || secondPlayer == player) {
                Player winner = getOpponent(player);
                eliminated(player);


                for (PotionEffect effect : winner.getActivePotionEffects()) {
                    winner.removePotionEffect(effect.getType());
                }

                winner.getInventory().clear();
                winner.getInventory().setArmorContents(null);
                winner.updateInventory();

                winner.teleport(getLocation());
                for (Player faggot : getPlayers().keySet()) {
                    if (faggot == null) {
                        continue;
                    }
                    Theme theme = ProfileManager.getProfile(faggot).getTheme();
                    faggot.sendMessage(ColorText.translateAmpersand("&2\u2713 " + winner.getName() + theme.getPrimaryColor() + " won round " + round + theme.getPrimaryColor() + '!'));
                }
                winner.sendMessage(ColorText.translateAmpersand("&eYou're next up, good luck! They don't stand a chance."));
                player.teleport(getLocation());

                startNewRound();
            } else {
                PlayerUtils.resetPlayer(player, false, true);
            }
        }
    }

    public void finish(Player player) {
        if (this.gameState == GameState.FINISHED) return; //Prevent being called more times then needed.
        this.gameState = GameState.FINISHED;
        if (player != null) {
            winner = player;
            new PlayerWinGameEvent(winner, this).call();
        }

        for (Entry<Player, GamePlayerState> entry : getPlayers().entrySet()) {
            Player player2 = entry.getKey();
            if (entry.getValue() == GamePlayerState.REMOVED) continue;
            PlayerUtils.resetPlayer(player2, false, true);
            if (KitPvP.getInstance().getGameHandler().getSpectators().contains(player2)) {
                KitPvP.getInstance().getGameHandler().removeSpectator(player2);
            }
        }
        this.getPlayers().clear();
        this.alreadyPlayed.clear();
        KitPvP.getInstance().getGameHandler().destroy();
    }

    @Override
    public List<String> getPlayerScoreboard(Player player) {
        List<String> lines = new ArrayList<>();
        Theme theme = ProfileManager.getProfile(player).getTheme();
        lines.add(theme.getPrimaryColor() + "Event: " + theme.getSecondaryColor() + getName());
        lines.add(theme.getPrimaryColor() + "Players: " + theme.getSecondaryColor() + getPlayers(GamePlayerState.ALIVE).size() + '/' + getPlayers().size());
        lines.add(theme.getPrimaryColor() + "Round: " + theme.getSecondaryColor() + round + (i > 0 ? " &7(" + i + "s)" : ""));
        lines.add("&7&m" + StringUtils.repeat("-", 25));

        if (this.gameState == GameState.STARTED) {
            lines.add(theme.getPrimaryColor() + firstPlayer.getName() + ' ' + theme.getSecondaryColor() + "vs" + theme.getPrimaryColor() + ' ' + secondPlayer.getName());
            lines.add(theme.getPrimaryColor() + getPing(firstPlayer) + "ms " + theme.getSecondaryColor() + "vs " + theme.getPrimaryColor() + getPing(secondPlayer) + "ms");
            lines.add(theme.getPrimaryColor() + ProfileManager.getProfile(firstPlayer).getPlayerCps() + "CPS " + theme.getSecondaryColor() + "vs " + theme.getPrimaryColor() + ProfileManager.getProfile(secondPlayer).getPlayerCps() + "CPS");
        } else {
            lines.add(theme.getPrimaryColor() + "Waiting...");
        }
        return lines;
    }

    public int getPing(Player player) {
        return ((CraftPlayer) player).getHandle().ping;
    }

    @Override
    public int getMaxPlayers() {
        return 50;
    }

    @Override
    public int getReward() {
        return 500;
    }

    @Override
    public int getCredits() {
        return 9000;
    }

    private Player getOpponent(Player player) {
        return player == firstPlayer ? secondPlayer : firstPlayer;
    }

    @Override
    public String getDescription() {
        return "Fight players.\nLast man standing wins.";
    }

    @Override
    public Material getItem() {
        return Material.POTION;
    }

    @Override
    public void spectator(Player player) {
        player.teleport(getLocation());
        this.getPlayers().put(player, GamePlayerState.SPECTATING);
    }

    @Override
    public void searchArena() {
        if (gameMap == null) {
            List<GameMap> gameMaps = new ArrayList<>();
            for (GameMap gameMap : KitPvP.getInstance().getGameMapHandler().getGameMap().values()) {
                if (gameMap.getGame().toLowerCase().startsWith("potbrackets")) {
                    gameMaps.add(gameMap);
                }
            }
            if (!gameMaps.isEmpty()) {
                gameMap = gameMaps.get(KitPvPUtils.getRandomNumber(gameMaps.size()));
            }
        }
    }

    @Override
    public void setArena(GameMap arena) {
        gameMap = arena;
    }

    @Override
    public String getPermission() {
        return "soupland.games.potffa";
    }

    @Override
    public boolean isTeams() {
        return false;
    }

    @Override
    public int maxPlayersInTeams() {
        return 0;
    }

    @Override
    public Location getLocation() {
        return gameMap.getLocations().get(0);
    }

    @EventHandler
    public void onGameStart(GameStartedEvent event) {
        if (!event.getGame().getName().equals(this.getName())) return;

        for (Player player : event.getPlayers()) {
            if (player != null) {
                PlayerInventory inventory = player.getInventory();
                inventory.clear();
                inventory.setArmorContents(null);
                player.setGameMode(GameMode.SURVIVAL);
                this.getPlayers().put(player, GamePlayerState.ALIVE);
            }
        }
        this.start();
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            Game game = KitPvP.getInstance().getGameHandler().getActiveGame();
            if (game instanceof PotPvPGame) {
                if (game.getPlayers(GamePlayerState.ALIVE).contains(player) || game.getPlayers(GamePlayerState.DEAD).contains(player)) {
                    if (gameState == GameState.STARTING) {
                        event.setCancelled(true);
                        return;
                    }
                    if (player == firstPlayer || player == secondPlayer) {
                        return;
                    }
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerItemDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        Game game = KitPvP.getInstance().getGameHandler().getActiveGame();
        if (game != null) {
            if (event.getItemDrop().getItemStack().getType() == Material.BOWL) {
                event.getItemDrop().setPickupDelay(10000);
                TaskUtil.runTaskLater(() -> {
                    event.getItemDrop().remove();
                }, 2 * 20L);
                return;
            }
            if (game.getPlayers().containsKey(player)) {
                event.setCancelled(true);
            }
        }
    }

}
