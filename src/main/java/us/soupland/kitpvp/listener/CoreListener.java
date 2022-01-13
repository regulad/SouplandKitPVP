package us.soupland.kitpvp.listener;

import us.soupland.kitpvp.KitPvP;
import us.soupland.kitpvp.enums.*;
import us.soupland.kitpvp.games.Game;
import us.soupland.kitpvp.games.GameHandler;
import us.soupland.kitpvp.games.GamePlayerState;
import us.soupland.kitpvp.games.types.PotPvPGame;
import us.soupland.kitpvp.kits.types.QuickdropKit;
import us.soupland.kitpvp.managers.BountyManager;
import us.soupland.kitpvp.profile.Profile;
import us.soupland.kitpvp.profile.ProfileManager;
import us.soupland.kitpvp.utilities.KitPvPUtils;
import us.soupland.kitpvp.utilities.chat.ColorText;
import us.soupland.kitpvp.utilities.player.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.PlayerInventory;
import us.soupland.kitpvp.utilities.task.TaskUtil;

public class CoreListener implements Listener {

    @EventHandler
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        Profile profile = new Profile(event.getUniqueId());

        profile.setPlayerName(event.getName());
        profile.setPlayerIp(event.getAddress().getHostAddress());

        ProfileManager.loadProfile(profile);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);

        Player player = event.getPlayer();

        player.setFoodLevel(20);

        player.setGameMode(GameMode.SURVIVAL);

        Profile profile = ProfileManager.getProfile(player);

        player.setPlayerTime((profile.getServerTime() == ServerTime.DAY ? 6000L : 15000L), true);

        profile.incrementStat(PlayerStat.JOINS);

        PlayerUtils.resetPlayer(player, false, true);
        profile.setCanModifyState(true);

        if (player.hasPermission(KitPvPUtils.DONATOR_PERMISSION) && profile.isJoinAndQuitMessageEnabled()) {
            Bukkit.broadcastMessage(ColorText.translate(player.getName() + " &7has connected."));
        }

        TaskUtil.runTask(() -> Bukkit.getOnlinePlayers().forEach(online -> ProfileManager.getProfile(online).updateTab()));
    }


    /*@EventHandler
    public void PotionAddEvent(PotionEff event) {
        if (event.getEntity() instanceof Player) {
            if (event.getEffect().getType().equals(PotionEffectType.INVISIBILITY)) {
                Player player = (Player) event.getEntity();
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    NameTagHandler.removeFromTeams(onlinePlayer, player);
                }
            }
        }
    }

    @EventHandler
    public void PotionAddEvent(PotionEffectExtendEvent event) {
        if (event.getEntity() instanceof Player) {
            if (event.getEffect().getType().equals(PotionEffectType.INVISIBILITY)) {
                Player player = (Player) event.getEntity();
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    NameTagHandler.removeFromTeams(onlinePlayer, player);
                }
            }
        }
    }

    @EventHandler
    public void PotionAddEvent(PotionEffectRemoveEvent event) {
        if (event.getEntity() instanceof Player) {
            if (event.getEffect().getType().equals(PotionEffectType.INVISIBILITY)) {
                Player player = (Player) event.getEntity();
                TaskUtil.runTask(() -> KitPvPBoard.refreshDisplayName(player));
            }
        }
    }*/

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);

        Player player = event.getPlayer();
        Profile profile = ProfileManager.getProfile(player);

        if (player.hasPermission(KitPvPUtils.DONATOR_PERMISSION) && profile.isJoinAndQuitMessageEnabled()) {
            Bukkit.broadcastMessage(ColorText.translate(player.getName() + " &7has disconnected."));
        }

        BountyManager.getFaggotMap().remove(player.getUniqueId());
        if (BountyManager.getPriceMap().containsKey(player.getUniqueId())) {
            profile.setStat(PlayerStat.CREDITS, (profile.getStat(PlayerStat.CREDITS) + BountyManager.getPriceMap().get(player.getUniqueId())));
        }

        ProfileManager.saveProfile(profile, true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE && player.isOp()) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE && player.isOp()) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        Player player = (Player) event.getEntity();
        if (ProfileManager.getProfile(player).getPlayerState() == PlayerState.DUELPRACTICE) {
            return;
        }
        GameHandler gameHandler = KitPvP.getInstance().getGameHandler();
        Game game = gameHandler.getUpcomingGame();
        if (game == null) {
            game = gameHandler.getActiveGame();
        }
        if (game != null) {
            if (game.getPlayers(GamePlayerState.ALIVE).contains(player) || game.getPlayers(GamePlayerState.DEAD).contains(player) || game.getPlayers(GamePlayerState.SPECTATING).contains(player)) {
                if (game instanceof PotPvPGame) {
                    return;
                }
                event.setCancelled(true);
            }
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Profile profile = ProfileManager.getProfile(event.getPlayer());
        Game game = KitPvP.getInstance().getGameHandler().getUpcomingGame();
        if (game == null) {
            game = KitPvP.getInstance().getGameHandler().getActiveGame();
        }
        if (game != null && (KitPvP.getInstance().getGameHandler().getPlayers().contains(event.getPlayer()) || KitPvP.getInstance().getGameHandler().getSpectators().contains(event.getPlayer()))) {
            /*if (game instanceof BracketsGame) {
                if (events.getItemDrop().getItemStack().getType() == Material.BOWL) {
                    events.getItemDrop().remove();
                } else {
                    events.setCancelled(true);
                }
            }*/
            return;
        }
        if (profile.getPlayerState() == PlayerState.SPAWN || profile.getPlayerState() == PlayerState.SPAWNPRACTICE) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        Profile profile = ProfileManager.getProfile(event.getPlayer());
        Game game = KitPvP.getInstance().getGameHandler().getUpcomingGame();
        if (game == null) {
            game = KitPvP.getInstance().getGameHandler().getActiveGame();
        }
        if (game != null && (KitPvP.getInstance().getGameHandler().getPlayers().contains(event.getPlayer()) || KitPvP.getInstance().getGameHandler().getSpectators().contains(event.getPlayer()))) {
            event.setCancelled(true);
            return;
        }
        if (profile.getPlayerState() == PlayerState.SPAWN || profile.getPlayerState() == PlayerState.SPAWNPRACTICE) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            Profile profile = ProfileManager.getProfile(player);
            Game game = KitPvP.getInstance().getGameHandler().getUpcomingGame();
            if (game == null) {
                game = KitPvP.getInstance().getGameHandler().getActiveGame();
            }
            if (game != null && (KitPvP.getInstance().getGameHandler().getPlayers().contains(player) || KitPvP.getInstance().getGameHandler().getSpectators().contains(player))) {
                event.setCancelled(true);
                return;
            }
            if (profile.getPlayerState() == PlayerState.SPAWN || profile.getPlayerState() == PlayerState.SPAWNPRACTICE) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            Profile profile = ProfileManager.getProfile((Player) event.getEntity());
            if (profile.getPlayerState() == PlayerState.SPAWN || profile.getPlayerState() == PlayerState.SPAWNPRACTICE) {
                event.setCancelled(true);
            } else if (profile.getPlayerState() == PlayerState.PLAYING && event.getDamager() instanceof Player) {
                Profile profileDamager = ProfileManager.getProfile((Player) event.getDamager());
                if (profileDamager.getPlayerState() == PlayerState.PLAYING) {
                    profile.setLastDamager((Player) event.getDamager());
                }
            }
        }
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.CUSTOM) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (player.isOp() && player.getGameMode() == GameMode.CREATIVE) {
            return;
        }
        Game game = KitPvP.getInstance().getGameHandler().getUpcomingGame();
        if (game == null) {
            game = KitPvP.getInstance().getGameHandler().getActiveGame();
        }
        if (game != null && (KitPvP.getInstance().getGameHandler().getPlayers().contains(player) || KitPvP.getInstance().getGameHandler().getSpectators().contains(player))) {
            event.setCancelled(true);
            return;
        }
        if (player.getGameMode() == GameMode.CREATIVE && player.isOp()) {
            return;
        }
        Profile profile = ProfileManager.getProfile(player);
        if (profile.getPlayerState() == PlayerState.SPAWN && profile.getCurrentKit() == null) {
            event.setCancelled(true);
            if (event.getClickedInventory() instanceof PlayerInventory) {
                switch (event.getRawSlot()) {
                    case 17: {
                        player.performCommand("achievements");
                        break;
                    }
                }
            }
        }

    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            BlockState state = event.getClickedBlock().getState();
            if ((state instanceof Skull)) {
                Skull skull = (Skull) state;

                if (skull.getOwner() == null || skull.getOwner().equalsIgnoreCase("null")) return;

                Theme theme = ProfileManager.getProfile(player).getTheme();
                player.sendMessage(ColorText.translate(theme.getPrimaryColor() + "This skull once belonged to " + theme.getSecondaryColor() + skull.getOwner() + theme.getPrimaryColor() + '.'));
                return;
            }
        }
        if (event.hasItem() && event.getAction().name().startsWith("RIGHT")) {

            if (player.getGameMode() == GameMode.CREATIVE || player.getHealth() >= player.getMaxHealth()) {
                return;
            }

            if (player.getItemInHand().getType() == Material.MUSHROOM_SOUP) {
                int v = (int) (player.getHealth() + 7);
                player.setHealth((v >= player.getMaxHealth() ? player.getMaxHealth() : v));
                player.getItemInHand().setType(Material.BOWL);
                if (ProfileManager.getProfile(player).getCurrentKit() instanceof QuickdropKit) {
                    player.getInventory().remove(Material.BOWL);
                }
                player.updateInventory();

                Profile profile = ProfileManager.getProfile(player);
                profile.incrementStat(PlayerStat.SOUPS_ATE);
                Achievement achievement;
                switch (profile.getStat(PlayerStat.SOUPS_ATE)) {
                    case 1000000: {
                        achievement = Achievement.BRAMMKINDS_SON;
                        break;
                    }
                    case 500000: {
                        achievement = Achievement.ORIGINAL_SOUPER;
                        break;
                    }
                    case 200000: {
                        achievement = Achievement.REGULAR;
                        break;
                    }
                    case 50000: {
                        achievement = Achievement.NEWCOMER;
                        break;
                    }
                    default: {
                        return;
                    }
                }
                if (!profile.getAchievements().contains(achievement)) {
                    profile.getAchievements().add(achievement);
                    achievement.broadcast(player);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        if (event.getReason().startsWith("Flying is not enabled")) {
            event.setCancelled(true);
        }
    }


}