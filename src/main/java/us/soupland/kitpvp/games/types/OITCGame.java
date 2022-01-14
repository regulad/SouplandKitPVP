package us.soupland.kitpvp.games.types;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.*;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
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
import us.soupland.kitpvp.utilities.player.DurationFormatter;
import us.soupland.kitpvp.utilities.player.PlayerUtils;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

@Getter
public class OITCGame extends Game {

    private GameState gameState;
    private GameMap gameMap;

    private HashMap<Player, Integer> killsMap;
    private HashMap<Player, Long> immunityMap, timerMap;

    private int i;

    public OITCGame() {
        super("OITC");
        this.setItemStack(new ItemMaker(Material.BOW).create());
    }

    private static LinkedHashMap<Player, Integer> sortByValues(HashMap<Player, Integer> map) {
        LinkedList<Entry<Player, Integer>> list = new LinkedList<Entry<Player, Integer>>(map.entrySet());
        list.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));
        LinkedHashMap<Player, Integer> sortedHashMap = new LinkedHashMap<Player, Integer>();
        for (Entry<Player, Integer> entry : list) {
            sortedHashMap.put(entry.getKey(), entry.getValue());
        }
        return sortedHashMap;
    }

    public void giveItems(Player player) {
        ItemStack item = new ItemStack(Material.BOW);
        ItemMeta meta = item.getItemMeta();

        meta.spigot().setUnbreakable(true);
        item.setItemMeta(meta);
        player.getInventory().addItem(item);

        item = new ItemStack(Material.WOOD_PICKAXE);
        meta = item.getItemMeta();
        meta.spigot().setUnbreakable(true);
        item.setItemMeta(meta);
        player.getInventory().addItem(item);

        player.getInventory().addItem(new ItemStack(Material.ARROW, 1));
        player.updateInventory();
    }

    @Override
    public void start() {
        this.gameState = GameState.STARTING;
        this.killsMap = new HashMap<>();
        this.immunityMap = new HashMap<>();
        this.timerMap = new HashMap<>();

        for (Entry<Player, GamePlayerState> entry : getPlayers().entrySet()) {
            Player player = entry.getKey();
            player.teleport(getLocation());

            if (entry.getValue() == GamePlayerState.ALIVE) {
                killsMap.put(player, 0);
                immunityMap.put(player, 0L);
                timerMap.put(player, 0L);
            }
        }
        i = 5;
        new BukkitRunnable() {
            public void run() {
                if (KitPvP.getInstance().getGameHandler().getActiveGame() == null) {
                    this.cancel();
                }
                if (i == 0) {
                    startGame();
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
                            player.sendMessage(ColorText.translateAmpersand(theme.getPrimaryColor() + "&l[Event] " + theme.getSecondaryColor() + "Game starting in " + i + " second" + (i == 1 ? "" : "s") + '.'));
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

    private void startGame() {
        this.gameState = GameState.STARTED;
        for (Entry<Player, GamePlayerState> entry : getPlayers().entrySet()) {
            if (entry.getValue() == GamePlayerState.ALIVE) {
                entry.getKey().getInventory().clear();
                this.giveItems(entry.getKey());
            }
        }
    }

    private void checkForWinner() {
        for (Entry<Player, Integer> entry : this.killsMap.entrySet()) {

            Player entryPlayer = entry.getKey();
            Integer entryKills = entry.getValue();

            if (entryKills >= 20) {
                finish(entryPlayer);
            }

        }
    }

    private void respawn(Player player) {
        checkForWinner();
        if (this.gameState != GameState.STARTED) return;
        if (getPlayers().get(player) != GamePlayerState.ALIVE) return;

        Bukkit.getScheduler().runTaskLater(KitPvP.getInstance(), () -> {
            player.getInventory().clear();
            player.setHealth(20.0D);
            player.teleport(getLocation());
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 120, 0));
            this.giveItems(player);
            this.immunityMap.put(player, System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(5L));
        }, 4L);
    }

    public void finish(Player player) {
        if (this.gameState == GameState.FINISHED) return;  //Prevent being called more times then needed.
        this.gameState = GameState.FINISHED;

        if (player != null) {
            winner = player;
            new PlayerWinGameEvent(winner, this).call();
        }

        for (Entry<Player, GamePlayerState> entry : getPlayers().entrySet()) {
            Player player2 = entry.getKey();
            if (entry.getValue() == GamePlayerState.ALIVE) {
                player2.getInventory().clear();
            }
            PlayerUtils.resetPlayer(player2, false, true);
            if (KitPvP.getInstance().getGameHandler().getSpectators().contains(player2)) {
                KitPvP.getInstance().getGameHandler().removeSpectator(player2);
            }
        }

        this.getPlayers().clear();
        KitPvP.getInstance().getGameHandler().destroy();
    }

    @Override
    public String getDescription() {
        return "Sniper. One shot, one kill.\nLast man standing wins!";
    }

    @Override
    public List<String> getPlayerScoreboard(Player player) {
        List<String> lines = new ArrayList<>();
        Theme theme = ProfileManager.getProfile(player).getTheme();

        lines.add(theme.getPrimaryColor() + "Event: " + theme.getSecondaryColor() + getName());
        lines.add(theme.getPrimaryColor() + "Players: " + theme.getSecondaryColor() + getPlayers(GamePlayerState.ALIVE).size() + '/' + getPlayers().size());
        if (i > 0) {
            lines.add(theme.getPrimaryColor() + "Starting In: " + theme.getSecondaryColor() + i + 's');
        }

        HashMap<Player, Integer> kills = new HashMap<>();

        for (Entry<Player, Integer> entry : this.killsMap.entrySet()) {
            kills.put(entry.getKey(), entry.getValue());

        }
        LinkedHashMap<Player, Integer> sortedPlayerCount = sortByValues(kills);
        int index = 1;
        lines.add("&7&m" + StringUtils.repeat("-", 25));

        for (Entry<Player, Integer> teamEntry : sortedPlayerCount.entrySet()) {
            if (index > 5) {
                break;
            }
            lines.add(theme.getSecondaryColor() + index + ". " + theme.getPrimaryColor() + teamEntry.getKey().getName() + ": &f" + teamEntry.getValue());
            index++;
        }
        if (getImmunityMap().containsKey(player) && getImmunityMap().get(player) > System.currentTimeMillis()) {
            lines.add("&7&m" + StringUtils.repeat("-", 25));
            lines.add(theme.getPrimaryColor() + "Grace Period: " + theme.getSecondaryColor() + DurationFormatter.getRemaining(getImmunityMap().get(player) - System.currentTimeMillis(), true));
        }
        return lines;
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
        return 8500;
    }


    @Override
    public Material getItem() {
        return Material.BOW;
    }

    @Override
    public String getPermission() {
        return "soupland.games.oitc";
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
        return gameMap.getLocations().get(KitPvPUtils.getRandomNumber(gameMap.getLocations().size()));
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
                if (gameMap.getGame().toLowerCase().startsWith("oitc")) {
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

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {

            Player player = (Player) event.getEntity();
            Game game = KitPvP.getInstance().getGameHandler().getActiveGame();
            if (game instanceof OITCGame) {
                if (this.getPlayers().containsKey(player)) {

                    if (this.gameState == GameState.FINISHED) {
                        event.setCancelled(true);
                    } else if (event.getCause() == DamageCause.FALL) {
                        event.setCancelled(true);
                    } else if (getImmunityMap().get(player) > System.currentTimeMillis()) {
                        event.setCancelled(true);
                        if (event.getDamager() instanceof Player) {
                            event.getDamager().sendMessage(ColorText.translateAmpersand("&c" + player.getName() + " still has invincibility."));
                        }
                    } else if (event.getDamager() instanceof Player && getImmunityMap().containsKey(event.getDamager()) && getImmunityMap().get(event.getDamager()) > System.currentTimeMillis()) {
                        event.setCancelled(true);
                        event.getDamager().sendMessage(ColorText.translateAmpersand("&cYou still have your Invincibility Timer."));
                    } else if (i > 0) {
                        event.setCancelled(true);
                    } else if (event.getDamager() instanceof Arrow && ((Arrow) event.getDamager()).getShooter() instanceof Player) {
                        if (event.getEntity() instanceof Player && getImmunityMap().get(player) > System.currentTimeMillis()) {
                            event.getDamager().sendMessage(ColorText.translateAmpersand("&c" + player.getName() + " still has invincibility."));
                            event.setCancelled(true);
                        } else {
                            event.setDamage(100.0D);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Game game = KitPvP.getInstance().getGameHandler().getActiveGame();
        if (game instanceof OITCGame) {
            if (this.getPlayers(GamePlayerState.ALIVE).contains(event.getEntity()) && this.gameState == GameState.STARTED) {
                event.getDrops().clear();
                if (event.getEntity().getKiller() != null) {
                    this.addKill(event.getEntity().getKiller());
                    event.getEntity().getKiller().getInventory().addItem(new ItemStack(Material.ARROW));
                    event.getEntity().getKiller().updateInventory();
                    broadcast(ColorText.translateAmpersand("&4&l[Event] &a" + event.getEntity().getName() + "&4[" + getKills(event.getEntity()) + "] &7killed by &c" + event.getEntity().getKiller().getName() + "&4[" + getKills(event.getEntity().getKiller()) + ']'));
                }
                this.respawn(event.getEntity());
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
        if (game instanceof OITCGame) {
            if (this.getPlayers().containsKey(event.getPlayer())) {
                if (event.getTo().getBlock().isLiquid() && !event.getFrom().getBlock().isLiquid()) {
                    if (this.gameState == GameState.STARTED) {
                        if (getImmunityMap().containsKey(player) && getImmunityMap().get(player) > System.currentTimeMillis())
                            return;
                        this.removeKill(player);
                        Theme theme = ProfileManager.getProfile(player).getTheme();
                        player.sendMessage(ColorText.translateAmpersand(theme.getPrimaryColor() + "&l[Event] " + theme.getSecondaryColor() + "You fell in water, and you lost a kill."));
                        this.respawn(player);
                    }
                }
            }
        }

    }

    private void addKill(Player player) {
        this.killsMap.put(player, killsMap.getOrDefault(player, 0) + 1);
    }

    private void removeKill(Player player) {
        this.killsMap.put(player, (this.killsMap.get(player) > 0 ? this.killsMap.get(player) - 1 : 0));
    }

    private int getKills(Player player) {
        return (this.killsMap.get(player) != null ? this.killsMap.get(player) : 0);
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        if (event.isSneaking()) {
            GameHandler gameHandler = KitPvP.getInstance().getGameHandler();
            Game game = gameHandler.getActiveGame();
            if (game instanceof OITCGame) {
                if (this.getPlayers().containsKey(player)) {
                    if (this.timerMap.get(player) > System.currentTimeMillis()) {
                        return;
                    }
                    this.timerMap.put(player, System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(5L));
                    if (player.getLocation().getPitch() >= -80) {
                        player.setVelocity(player.getLocation().getDirection().multiply(2.3D).setY(0.3D));
                    } else {
                        player.setVelocity(player.getLocation().getDirection().multiply(0.1D).setY(1.3D));
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
            faggot.sendMessage(ColorText.translateAmpersand(theme.getPrimaryColor() + "&l[Event] " + player.getName() + theme.getSecondaryColor() + " has been eliminated."));
        }

        if (this.isWinner()) {
            this.finish(this.getPlayers(GamePlayerState.ALIVE).get(0));
        } else {
            this.eliminated(player);
            player.teleport(getLocation());
        }
    }
}
