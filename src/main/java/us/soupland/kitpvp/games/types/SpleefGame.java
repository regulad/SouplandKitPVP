package us.soupland.kitpvp.games.types;

import us.soupland.kitpvp.KitPvP;
import us.soupland.kitpvp.enums.Theme;
import us.soupland.kitpvp.games.Game;
import us.soupland.kitpvp.games.GameHandler;
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
import org.apache.commons.lang3.StringUtils;
import org.bukkit.*;
import org.bukkit.block.BlockState;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class SpleefGame extends Game {

    private Map<Location, BlockState> rollbackBlocks;
    private List<Entity> entities;
    private GameState gameState;
    private int i;
    private GameMap gameMap;

    public SpleefGame() {
        super("Spleef");
        this.setItemStack(new ItemMaker(Material.DIAMOND_SPADE).create());
    }

    @Override
    public void start() {
        this.gameState = GameState.STARTING;
        this.rollbackBlocks = new HashMap<>();
        this.entities = new ArrayList<>();

        for (Player spectators : KitPvP.getInstance().getGameHandler().getSpectators()) {
            spectators.teleport(getLocation());
        }

        for (Entry<Player, GamePlayerState> entry : getPlayers().entrySet()) {
            Player player = entry.getKey();
            player.teleport(getLocation().add(0, 1, 0));
            if (entry.getValue() == GamePlayerState.ALIVE) {
                player.getInventory().addItem(new ItemMaker(Material.DIAMOND_SPADE).setEnchant(Enchantment.DIG_SPEED, 5).create());
            }
            player.updateInventory();
        }
        i = 5;
        new BukkitRunnable() {
            public void run() {
                if(KitPvP.getInstance().getGameHandler().getActiveGame() == null){
                    this.cancel();
                }
                if (i == 0) {
                    gameState = GameState.STARTED;
                    this.cancel();
                }
                switch (i) {
                    case 5:
                    case 4:
                    case 3:
                    case 2:
                    case 1:
                        for (Player player : getPlayers().keySet()) {
                            if (player == null) {
                                continue;
                            }
                            Theme theme = ProfileManager.getProfile(player).getTheme();
                            player.sendMessage(ColorText.translate(theme.getPrimaryColor() + "&l[Event] " + theme.getSecondaryColor() + "Game starting in " + i + " second" + (i == 1 ? "" : "s") + '.'));
                            player.playSound(player.getLocation(), Sound.NOTE_PIANO, 1L, 1L);
                        }
                        break;
                    default:
                        break;
                }

                i--;
            }
        }.runTaskTimer(KitPvP.getInstance(), 20L, 20L);
    }

    private boolean isWinner() {
        return getPlayers(GamePlayerState.ALIVE).size() <= 1;
    }

    public void eliminate(Player player) {
        if (this.gameState != GameState.STARTED) return;
        if (getPlayers().get(player) == GamePlayerState.DEAD) return;

        getPlayers().put(player, GamePlayerState.DEAD);

        for (Player faggot : getPlayers().keySet()) {
            if (faggot == null) {
                continue;
            }
            Theme theme = ProfileManager.getProfile(faggot).getTheme();
            faggot.sendMessage(ColorText.translate(theme.getPrimaryColor() + "&l[Event] " + player.getName() + theme.getSecondaryColor() + " has been eliminated."));
        }

        if (this.isWinner()) {
            this.finish(this.getPlayers(GamePlayerState.ALIVE).get(0));
        } else {
            this.eliminated(player);
            player.teleport(getLocation());
        }
    }

    public void finish(Player player) {
        if (this.gameState == GameState.FINISHED) return;  //Prevent being called more times then needed.
        this.gameState = GameState.FINISHED;

        entities.forEach(Entity::remove);

        if (player != null) {
            winner = player;
            new PlayerWinGameEvent(winner, this).call();
        }

        for (Entry<Player, GamePlayerState> entry : getPlayers().entrySet()) {
            Player player2 = entry.getKey();
            if (entry.getValue() == GamePlayerState.REMOVED) continue;
            if (entry.getValue() == GamePlayerState.ALIVE) {
                player2.getInventory().clear();
            }
            PlayerUtils.resetPlayer(player2, false, true);
            if (KitPvP.getInstance().getGameHandler().getSpectators().contains(player2)) {
                KitPvP.getInstance().getGameHandler().removeSpectator(player2);
            }
        }
        for (Entry<Location, BlockState> state : this.rollbackBlocks.entrySet()) {
            Location location = state.getKey();
            BlockState block = state.getValue();

            location.getBlock().setType(block.getType());
            location.getBlock().setData(block.getData().getData());

        }
        this.rollbackBlocks.clear();
        this.getPlayers().clear();
        KitPvP.getInstance().getGameHandler().destroy();
    }

    @Override
    public String getDescription() {
        return "Break blocks under your opponents!";
    }

    @Override
    public List<String> getPlayerScoreboard(Player player) {
        List<String> lines = new ArrayList<>();
        Theme theme = ProfileManager.getProfile(player).getTheme();

        lines.add(theme.getPrimaryColor() + "Event: " + theme.getSecondaryColor() + getName());
        lines.add(theme.getPrimaryColor() + "Players: " + theme.getSecondaryColor() + getPlayers(GamePlayerState.ALIVE).size() + '/' + getPlayers().size());
        if (i > 0) {
            lines.add("&7&m" + StringUtils.repeat("-", 25));
            lines.add(theme.getSecondaryColor() + "Grace Period: " + theme.getPrimaryColor() + i + 's');
        }
        return lines;
    }

    @Override
    public int getMaxPlayers() {
        return 50;
    }

    @Override
    public int getReward() {
        return 250;
    }

    @Override
    public int getCredits() {
        return 5000;
    }

    @Override
    public Material getItem() {
        return Material.DIAMOND_SPADE;
    }

    @Override
    public String getPermission() {
        return "soupland.games.spleef";
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
    public void spectator(Player player) {
        player.teleport(getLocation());
    }

    @Override
    public void searchArena() {
        if (gameMap == null) {
            List<GameMap> gameMaps = new ArrayList<>();
            for (GameMap gameMap : KitPvP.getInstance().getGameMapHandler().getGameMap().values()) {
                if (gameMap.getGame().toLowerCase().startsWith("spleef")) {
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
    public Location getLocation() {
        return gameMap.getLocations().get(0);
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && !(event.getDamager() instanceof Snowball)) {
            Player player = (Player) event.getEntity();
            Game game = KitPvP.getInstance().getGameHandler().getActiveGame();
            if (game instanceof SpleefGame) {
                if (game.getPlayers().containsKey(player)) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Game game = KitPvP.getInstance().getGameHandler().getActiveGame();
        if (game instanceof SpleefGame) {
            if (game.getPlayers(GamePlayerState.ALIVE).contains(player)) {
                event.setCancelled(true);
                if (event.getBlock().getType() == Material.SNOW_BLOCK) {
                    event.setCancelled(true);
                    if (i > 0) {
                        return;
                    }
                    if (this.gameState == GameState.STARTED) {
                        this.rollbackBlocks.put(event.getBlock().getLocation(), event.getBlock().getState());
                        event.getBlock().setType(Material.AIR);
                        player.getInventory().addItem(new ItemStack(Material.SNOW_BALL));
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();
        if (from.getBlockX() == to.getBlockX() && from.getBlockY() == to.getBlockY() && from.getBlockZ() == to.getBlockZ()) {
            return;
        }

        Player player = event.getPlayer();

        GameHandler gameHandler = KitPvP.getInstance().getGameHandler();
        Game game = gameHandler.getActiveGame();
        if (game != null) {
            if (this.getPlayers(GamePlayerState.ALIVE).contains(player)) {
                if (event.getTo().getBlock().isLiquid()) {
                    if (this.gameState != GameState.ENDED) {
                        player.setHealth(0);
                    }
                }
            }
        }

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
    public void onPlayerItemDrop(PlayerDropItemEvent event) {

        GameHandler gameHandler = KitPvP.getInstance().getGameHandler();
        Game game = gameHandler.getActiveGame();
        if (game instanceof SpleefGame) {
            if (getPlayers(GamePlayerState.ALIVE).contains(event.getPlayer())) {
                entities.add(event.getItemDrop());
            }
        }
    }

}
