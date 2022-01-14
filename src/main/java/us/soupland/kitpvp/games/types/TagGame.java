package us.soupland.kitpvp.games.types;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
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

public class TagGame extends Game {

    private GameState gameState;

    private List<Player> tagged = new ArrayList<>();

    private int round, i;

    private GameMap gameMap;

    public TagGame() {
        super("TNTTag");
        this.setItemStack(new ItemMaker(Material.TNT).create());
    }

    private void setTagged(Player player, Player attacker) {
        PlayerInventory inventory = player.getInventory();

        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
        if (attacker != null) {
            for (PotionEffect effect : attacker.getActivePotionEffects()) {
                attacker.removePotionEffect(effect.getType());
            }
            attacker.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200000, 1));
            tagged.remove(attacker);
            attacker.getInventory().clear();
            attacker.getInventory().setArmorContents(null);
        }

        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200000, 2));

        inventory.clear();
        inventory.setArmorContents(null);
        inventory.setHelmet(new ItemMaker(Material.TNT).setDisplayname("&cTag Someone!").create());

        tagged.add(player);

        player.playSound(player.getLocation(), Sound.FIZZ, 1, 1);
        for (int i = 0; i < 9; i++) {
            inventory.setItem(i, new ItemMaker(Material.TNT).setDisplayname("&c&lTag Someone!").create());
        }
    }

    @Override
    public void start() {
        this.gameState = GameState.STARTING;
        this.round = 1;

        for (Player spectators : KitPvP.getInstance().getGameHandler().getSpectators()) {
            spectators.teleport(getLocation());
        }

        for (Entry<Player, GamePlayerState> entry : getPlayers().entrySet()) {
            Player player = entry.getKey();
            player.teleport(getLocation());
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200000, 1));
        }

        List<Player> players = getPlayers(GamePlayerState.ALIVE);
        Player random = players.get(KitPvPUtils.getRandomNumber(players.size()));
        if (random != null) {
            setTagged(random, null);
        }

        startNewRound();
    }

    private void startNewRound() {
        this.gameState = GameState.STARTED;
        round++;
        i = 35;
        new BukkitRunnable() {
            public void run() {
                if (KitPvP.getInstance().getGameHandler().getActiveGame() == null) {
                    this.cancel();
                }
                if (i <= 0) {
                    if (!tagged.isEmpty()) {
                        for (Player player : tagged) {
                            if (player == null) {
                                continue;
                            }
                            eliminate(player);
                            player.sendMessage(ColorText.translate("&eYou were blow up!"));
                        }
                        tagged.clear();
                    }
                    if (!getPlayers(GamePlayerState.ALIVE).isEmpty()) {
                        List<Player> players = getPlayers(GamePlayerState.ALIVE);
                        Player random = players.get(KitPvPUtils.getRandomNumber(players.size()));
                        broadcast(ColorText.translate("&f&lRound " + round + " has started!"));
                        if (random != null) {
                            setTagged(random, null);
                            broadcast(ColorText.translate("&eThe TNT has been released to " + random.getName() + "&e!"));
                        }
                        for (Player player : players) {
                            player.teleport(getLocation());
                            player.playSound(player.getLocation(), Sound.NOTE_PIANO, 1, 1);
                        }
                        TaskUtil.runTask(() -> startNewRound());
                    }
                    cancel();
                    return;
                }
                switch (i) {
                    case 3:
                    case 2:
                    case 1:
                        for (Entry<Player, GamePlayerState> entry : getPlayers().entrySet()) {
                            entry.getKey().playSound(entry.getKey().getLocation(), Sound.NOTE_PIANO, 1L, 1L);
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
        if (getPlayers(GamePlayerState.DEAD).contains(player)) {
            return;
        }
        if (this.gameState == GameState.ENDED) return;
        getPlayers().put(player, GamePlayerState.DEAD);
        if (isWinner()) {
            finish(getPlayers(GamePlayerState.ALIVE).get(0));
        } else {
            eliminated(player);
            player.teleport(getLocation());
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
        tagged.clear();
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
        if (gameState == GameState.STARTED) {
            lines.add(theme.getPrimaryColor() + "Explosion: " + theme.getSecondaryColor() + i + 's');
            if (getPlayers(GamePlayerState.ALIVE).contains(player)) {
                lines.add(theme.getPrimaryColor() + "Goal: " + (tagged.contains(player) ? "&cTag someone" : "&aRun away") + '!');
            }
            int alive = getPlayers(GamePlayerState.ALIVE).size();
            lines.add(theme.getPrimaryColor() + "Alive: " + theme.getSecondaryColor() + alive + " player" + (alive == 1 ? "" : "s"));
        } else {
            lines.add(theme.getPrimaryColor() + "Waiting...");
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
        return 3500;
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

    @Override
    public String getDescription() {
        return "Tag.";
    }

    @Override
    public Material getItem() {
        return Material.TNT;
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
                if (gameMap.getGame().toLowerCase().startsWith("tnttag")) {
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
        return "soupland.games.tnttag";
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
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player player = (Player) event.getEntity();
            Player attacker = (Player) event.getDamager();
            Game game = KitPvP.getInstance().getGameHandler().getActiveGame();
            if (game instanceof TagGame) {
                if (game.getPlayers().containsKey(player) && !KitPvP.getInstance().getGameHandler().getActiveGame().getPlayers(GamePlayerState.SPECTATING).contains(player)) {
                    if (gameState == GameState.STARTING) {
                        event.setCancelled(true);
                        return;
                    }
                    event.setDamage(0);
                    if (tagged.contains(attacker) && !tagged.contains(player)) {
                        setTagged(player, attacker);
                        broadcast(ColorText.translate(player.getName() + " &7is IT!"));
                    }
                }
            }
        }
    }

}
