package us.soupland.kitpvp.games.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import us.soupland.kitpvp.KitPvP;
import us.soupland.kitpvp.enums.Achievement;
import us.soupland.kitpvp.enums.PlayerStat;
import us.soupland.kitpvp.enums.Theme;
import us.soupland.kitpvp.games.Game;
import us.soupland.kitpvp.games.GameHandler;
import us.soupland.kitpvp.games.GamePlayerState;
import us.soupland.kitpvp.games.events.GameStartedEvent;
import us.soupland.kitpvp.games.events.PlayerJoinGameEvent;
import us.soupland.kitpvp.games.events.PlayerWinGameEvent;
import us.soupland.kitpvp.games.types.OITCGame;
import us.soupland.kitpvp.games.types.SumoGame;
import us.soupland.kitpvp.profile.Profile;
import us.soupland.kitpvp.profile.ProfileManager;
import us.soupland.kitpvp.utilities.chat.ColorText;
import us.soupland.kitpvp.utilities.task.TaskUtil;

public class GamesListener implements Listener {

    @EventHandler
    public void onPlayerJoinGame(PlayerJoinGameEvent event) {
        Player player = event.getPlayer();
        GameHandler gameHandler = KitPvP.getInstance().getGameHandler();
        Game game = event.getGame();

        if (gameHandler.getActiveGame() != null) {
            player.sendMessage(ColorText.translate("&cThe event has already began!"));
        } else if (gameHandler.getUpcomingGame() == null) {
            player.sendMessage(ColorText.translate("&cThere is currently no ongoing events!"));
        } else if (gameHandler.getPlayers().contains(player)) {
            player.sendMessage(ColorText.translate("&cYou're already in the events!"));
            player.sendMessage(ColorText.translate("&cType /leave to leave the events."));
        } else if (gameHandler.getPlayers().size() >= game.getMaxPlayers()) {
            player.sendMessage(ColorText.translate("&cThe event is full."));
        } else if (ProfileManager.getProfile(player).getPlayerCombat() > 0L) {
            player.sendMessage(ColorText.translate("&cYou mustn't be spawn-tagged to participate."));
        } else {
            game.broadcast(ColorText.translate(GameHandler.getPrefix() + player.getName() + " &7has joined the game. &c(" + (gameHandler.getPlayers().size() + 1) + '/' + game.getMaxPlayers() + ')'));
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onGameStarted(GameStartedEvent event) {
        for (Player player : event.getPlayers()) {
            if (player == null) {
                continue;
            }
            ProfileManager.getProfile(player).setCanModifyState(false);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        GameHandler gameHandler = KitPvP.getInstance().getGameHandler();
        Game game = gameHandler.getActiveGame();
        if (game != null) {
            if (game.getPlayers(GamePlayerState.ALIVE).contains(player) || game.getPlayers(GamePlayerState.DEAD).contains(player)) {
                event.getDrops().clear();
                if (game instanceof OITCGame || game instanceof SumoGame) {
                    return;
                }
                TaskUtil.runTask(() -> {
                    player.spigot().respawn();
                    game.eliminate(player);
                });
            }
        }
    }

    @EventHandler
    public void onPlayerWinGame(PlayerWinGameEvent event) {
        Player player = event.getPlayer();
        Profile profile = ProfileManager.getProfile(player);
        if (!profile.getAchievements().contains(Achievement.CHAMPION)) {
            profile.getAchievements().add(Achievement.CHAMPION);
            Achievement.CHAMPION.broadcast(player);
        }
        profile.getEventsWin().put(event.getGame().getName(), profile.getEventsWin().getOrDefault(event.getGame().getName(), 0) + 1);

        profile.incrementStat(PlayerStat.EVENT_WINS);
        profile.setStat(PlayerStat.CREDITS, (profile.getStat(PlayerStat.CREDITS) + event.getGame().getReward()));

        for (Player online : Bukkit.getOnlinePlayers()) {
            Theme theme = ProfileManager.getProfile(online).getTheme();
            if (online == player) {
                player.sendMessage(ColorText.translate(theme.getPrimaryColor() + "&l[Event] &aCongratulations! You received &7" + event.getGame().getReward() + " Credits&a."));
                continue;
            }
            online.sendMessage(ColorText.translate(theme.getPrimaryColor() + "&l[Event] " + player.getName() + theme.getSecondaryColor() + " has won the &d" + event.getGame().getName() + ' ' + theme.getSecondaryColor() + "event. &7(+" + event.getGame().getReward() + " credits)"));
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Game game = KitPvP.getInstance().getGameHandler().getActiveGame();
        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
            if (game != null) {
                /*if (event.getFinalDamage() >= player.getHealth()) {
                    TaskUtil.runTaskLater(() -> player.spigot().respawn(), 2L);
                    game.eliminate(player);
                    System.out.println("xdxd");
                    return;
                }*/
                if (game.getPlayers(GamePlayerState.SPECTATING).contains(player)) {
                    event.setCancelled(true);
                }
            }
        } else if (event.getDamager() instanceof Projectile) {
            if (((Projectile) event.getDamager()).getShooter() instanceof Player) {
                Player player = (Player) ((Projectile) event.getDamager()).getShooter();
                if (game != null) {
                    if (game.getPlayers(GamePlayerState.SPECTATING).contains(player)) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        Game game = KitPvP.getInstance().getGameHandler().getActiveGame();
        if (game != null) {
            if (game.getPlayers(GamePlayerState.SPECTATING).contains(player)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Game game = KitPvP.getInstance().getGameHandler().getUpcomingGame();
        if (game == null) {
            game = KitPvP.getInstance().getGameHandler().getActiveGame();
        }
        if (game != null && game.getPlayers().containsKey(event.getPlayer())) {
            game.eliminate(event.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        Game game = KitPvP.getInstance().getGameHandler().getUpcomingGame();
        if (game == null) {
            game = KitPvP.getInstance().getGameHandler().getActiveGame();
        }
        if (game != null && game.getPlayers().containsKey(event.getPlayer())) {
            game.eliminate(event.getPlayer());
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
                Player player = (Player) event.getEntity();
                Game game = KitPvP.getInstance().getGameHandler().getActiveGame();
                if (game != null && (game.getPlayers(GamePlayerState.ALIVE).contains(player) || game.getPlayers(GamePlayerState.DEAD).contains(player))) {
                    event.setCancelled(true);
                }
            }
        }
    }
}