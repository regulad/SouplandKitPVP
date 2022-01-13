package us.soupland.kitpvp.listener;

import us.soupland.kitpvp.utilities.cooldown.Cooldown;
import us.soupland.kitpvp.utilities.KitPvPUtils;
import us.soupland.kitpvp.utilities.chat.ColorText;
import us.soupland.kitpvp.utilities.item.ItemMaker;
import us.soupland.kitpvp.utilities.location.LocationUtils;
import us.soupland.kitpvp.utilities.task.TaskUtil;
import us.soupland.kitpvp.utilities.time.TimeUtils;
import us.soupland.kitpvp.KitPvP;
import us.soupland.kitpvp.enums.PlayerState;
import us.soupland.kitpvp.enums.Refill;
import us.soupland.kitpvp.events.PlayerGainExpEvent;
import us.soupland.kitpvp.games.Game;
import us.soupland.kitpvp.games.GameHandler;
import us.soupland.kitpvp.kits.Kit;
import us.soupland.kitpvp.kits.KitHandler;
import us.soupland.kitpvp.kits.types.FalconKit;
import us.soupland.kitpvp.kits.types.SnailKit;
import us.soupland.kitpvp.profile.Profile;
import us.soupland.kitpvp.profile.ProfileManager;
import us.soupland.kitpvp.server.ServerData;
import org.apache.commons.lang.StringUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GameListener implements Listener {

    private KitPvP plugin = KitPvP.getInstance();
    private ServerData serverData = plugin.getServerData();

    private List<UUID> spongeList = new ArrayList<>();

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        Profile profile = ProfileManager.getProfile(player);
        if (profile.getPlayerState() == PlayerState.PLAYING) {
            if (event.getItemDrop().getItemStack().getType() == Material.BOWL) {
                event.getItemDrop().setPickupDelay(10000);
                TaskUtil.runTaskLater(() -> {
                    event.getItemDrop().remove();
                }, 2 * 20L);
            } else {

                if (player.getGameMode() == GameMode.CREATIVE && player.isOp()) {
                    return;
                }

                event.setCancelled(true);
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
        Profile profile = ProfileManager.getProfile(player);
        if (profile.getPlayerState() == PlayerState.SPAWNPRACTICE) {
            if (to.getBlockY() <= 0) {
                Location spawnLocation = LocationUtils.getLocation(plugin.getServerData().getSpawnPractice());
                if (spawnLocation != null) {
                    player.teleport(spawnLocation);
                    player.setHealth(player.getMaxHealth());
                }
            }
            return;
        }
        if (profile.getPlayerState() == PlayerState.INGAME) {
            if (to.getBlockY() <= 0) {
                if (KitPvP.getInstance().getGameHandler().getUpcomingGame() != null && KitPvP.getInstance().getGameHandler().getPlayers().contains(player)) {
                    Location spawnLocation = plugin.getServerData().getSpawnEventsLocation();
                    if (spawnLocation != null) {
                        player.teleport(spawnLocation);
                        player.setHealth(player.getMaxHealth());
                    }
                }
            }
            return;
        }
        /*if (to.getBlockY() <= 0) {
            Location spawnLocation = plugin.getServerData().getSpawnEventsLocation();
            if (profile.getPlayerState() == PlayerState.INGAME && spawnLocation != null) {
                player.teleport(spawnLocation);
                player.setHealth(player.getMaxHealth());
            } else if (profile.getPlayerState() == PlayerState.SPAWNPRACTICE && (spawnLocation = LocationUtils.getLocation(plugin.getServerData().getSpawnPractice())) != null) {
                player.teleport(spawnLocation);
                player.setHealth(player.getMaxHealth());
                return;
            }
            player.setHealth(0);
        }*/
        KitPvP.getCooldown("SpawnTimer").remove(player);
        if (profile.getPlayerState() == PlayerState.SPAWN) {
            if (event.getTo().getBlock().getType().name().endsWith("PLATE")) {
                player.setVelocity(player.getLocation().getDirection().multiply(4.5));
                player.setVelocity(new Vector(player.getVelocity().getX(), 1.0, player.getVelocity().getZ()));
                player.playSound(player.getLocation(), Sound.PISTON_EXTEND, 10.0f, 1.0f);
            }
        }
        if (to.getBlock().getRelative(BlockFace.DOWN).getType() == Material.SPONGE) {
            double v = 0;
            for (int i = 0; i < 10; i++) {
                if (to.getBlock().getRelative(BlockFace.DOWN).getLocation().clone().add(0, -i, 0).getBlock().getType() == Material.SPONGE) {
                    v += 0.8;
                }
            }
            player.setVelocity(new Vector(0, v, 0));
            if (!spongeList.contains(player.getUniqueId())) {
                spongeList.add(player.getUniqueId());
            }
        }
        if (!profile.isCanModifyState()) {
            return;
        }
        if (serverData.getSpawnCuboID() != null) {
            if (profile.getPlayerState() == PlayerState.SPAWN) {
                if (!serverData.getSpawnCuboID().contains(event.getTo())) {
                    profile.setPlayerState(PlayerState.PLAYING);
                    if (profile.getCurrentKit() == null) {
                        Kit kit = (profile.getLastKit() != null ? profile.getLastKit() : KitHandler.getByName("PvP"));
                        assert kit != null;
                        if (kit.getPermissions() != null && !player.hasPermission(kit.getPermissions())) {
                            kit = KitHandler.getByName("PvP");
                        }
                        if (kit != null) {
                            PlayerInventory inventory = player.getInventory();
                            inventory.clear();
                            inventory.setArmorContents(null);

                            for (PotionEffect effect : player.getActivePotionEffects()) {
                                player.removePotionEffect(effect.getType());
                            }

                            kit.onLoad(player);
                            profile.setCurrentKit(kit);
                        }
                    }
                    player.sendMessage(ColorText.translate("&7You are no longer protected."));
                }
            } else if (profile.getPlayerState() == PlayerState.PLAYING) {
                if (serverData.getSpawnCuboID().contains(event.getTo())) {
                    if (profile.getPlayerCombat() > 0L) {
                        if (player.isOp()) {
                            profile.setPlayerCombat(0L);
                        } else {
                            event.setTo(event.getFrom());
                        }
                        return;
                    }
                    profile.setPlayerState(PlayerState.SPAWN);
                    player.sendMessage(ColorText.translate("&aYou are now protected."));
                    profile.setFell(false);
                    player.setHealth(player.getMaxHealth());
                }
            }
        }
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        onPlayerMove(event);
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Player attacker = getFinalAttacker(event, true);
        Entity entity;
        if ((attacker != null) && ((entity = event.getEntity()) instanceof Player)) {
            Player damaged = (Player) entity;

            Profile damagedProfile = ProfileManager.getProfile(damaged);
            Profile attackerProfile = ProfileManager.getProfile(attacker);

            if (attackerProfile.getPlayerState() != PlayerState.PLAYING || damagedProfile.getPlayerState() != PlayerState.PLAYING) {
                return;
            }

            if (attackerProfile.getCurrentKit() instanceof SnailKit) {
                if (KitPvPUtils.getRandomNumber(10) <= 2) {
                    Cooldown cooldown = KitPvP.getCooldown(attackerProfile.getCurrentKit().getName());

                    if (cooldown.isOnCooldown(attacker)) {
                        return;
                    }
                    ((Player) entity).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 6 * 3, 2));
                    cooldown.setCooldown(attacker);
                }
            }
            int hits;
            attackerProfile.setLastHits((hits = attackerProfile.getLastHits() + 1));
            damagedProfile.setLastHits(0);

            if (damagedProfile.getCurrentKit() instanceof FalconKit) {
                damaged.setAllowFlight(false);
                damaged.setFlying(false);
            }

            if (damagedProfile.getPlayerCombat() <= 0L) {
                event.getEntity().sendMessage(ColorText.translate("&aYou are now in combat."));
            }

            if (attackerProfile.getPlayerCombat() <= 0L) {
                event.getDamager().sendMessage(ColorText.translate("&aYou are now in combat."));
            }

            damagedProfile.setPlayerCombat(TimeUtils.parse("20s") + System.currentTimeMillis());
            attackerProfile.setPlayerCombat(TimeUtils.parse("20s") + System.currentTimeMillis());
        }
    }

    private Player getFinalAttacker(EntityDamageEvent ede, boolean ignoreSelf) {
        Player attacker = null;
        if (ede instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) ede;
            Entity damager = event.getDamager();
            if (event.getDamager() instanceof Player) {
                attacker = (Player) damager;
            } else if (event.getDamager() instanceof Projectile) {
                Projectile projectile = (Projectile) damager;
                ProjectileSource shooter = projectile.getShooter();
                if (shooter instanceof Player) {
                    attacker = (Player) shooter;
                }
            }
            if (attacker != null && ignoreSelf && event.getEntity().equals(attacker)) {
                attacker = null;
            }
        }
        return attacker;
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            Profile profile = ProfileManager.getProfile(player);
            if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
                if (event.isCancelled()) {
                    return;
                }
                if (!profile.isFell()) {
                    profile.setFell(true);
                } else if (spongeList.contains(event.getEntity().getUniqueId())) {
                    spongeList.remove(player.getUniqueId());
                } else {
                    return;
                }
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Profile profile = ProfileManager.getProfile(player);
        if (profile.getPlayerState() == PlayerState.SPAWN && event.getAction().name().startsWith("RIGHT") && event.hasItem()) {
            if (player.getItemInHand().getType() == Material.ENDER_PEARL) {
                event.setCancelled(true);
                player.sendMessage(ColorText.translate("&cYou cannot throw enderpearls while protected."));
            } else if (player.getItemInHand().getType() == Material.POTION) {
                Potion potion = Potion.fromItemStack(player.getItemInHand());
                if (potion != null && potion.isSplash()) {
                    event.setCancelled(true);
                    if (profile.getCurrentKit() != null) {
                        player.sendMessage(ColorText.translate("&cYou cannot interact with potions while protected."));
                    }
                }
            } else {
                return;
            }
            player.updateInventory();
            return;
        }
        if (event.getAction() == Action.PHYSICAL) {
            if (player.getGameMode() == GameMode.CREATIVE && player.isOp()) {
                return;
            }
            if (profile.getPlayerState() != PlayerState.PLAYING) {
                event.setCancelled(true);
            }
        } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block block = event.getClickedBlock();
            if (block.getState() instanceof Sign) {
                Sign sign = (Sign) block.getState();
                if (sign.getLine(1).contains("Free") && sign.getLine(2).contains("Refill")) {
                    Inventory inventory = Bukkit.createInventory(null, 4 * 9, ColorText.translate((profile.getRefill() == Refill.SOUP ? "&4Soup" : "&dPotion") + " Refill"));
                    for (int i = 0; i < 36; i++) {
                        inventory.addItem(new ItemMaker((profile.getRefill() == Refill.SOUP ? Material.MUSHROOM_SOUP : Material.POTION)).setDurability((profile.getRefill() == Refill.SOUP ? 0 : 16421)).setDisplayname((profile.getRefill() == Refill.SOUP ? "&6Soup" : "&dPotion")).create());
                    }
                    player.openInventory(inventory);
                }
            } else {
                if (player.getGameMode() == GameMode.CREATIVE && player.isOp()) {
                    return;
                }
                GameHandler gameHandler = KitPvP.getInstance().getGameHandler();
                Game game = gameHandler.getUpcomingGame();
                if (game == null) {
                    game = gameHandler.getActiveGame();
                }
                if (profile.getPlayerState() == PlayerState.SPAWN || profile.getPlayerState() == PlayerState.SPAWNPRACTICE || profile.getPlayerState() == PlayerState.FIGHTINGPRACTICE || (game != null && game.getPlayers().containsKey(player))) {
                    event.setCancelled(true);
                    player.updateInventory();
                }
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        spongeList.remove(player.getUniqueId());
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Profile profile = ProfileManager.getProfile(player);
        if (profile.getPlayerState() == PlayerState.PLAYING) {
            player.getWorld().strikeLightningEffect(player.getLocation());
        }
    }

    @EventHandler
    public void onEntityDamageByArrow(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Arrow) {
            // event.setDamage(event.getDamage() + 6);
        }
    }

    @EventHandler
    public void onPotionSplash(PotionSplashEvent event) {
        if (event.getPotion().getShooter() instanceof Player) {
            Profile profile = ProfileManager.getProfile((Player) event.getPotion().getShooter());
            if (profile.getPlayerState() == PlayerState.SPAWN || profile.getPlayerState() == PlayerState.SPAWNPRACTICE) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerPreprocessCommand(PlayerCommandPreprocessEvent event) {
        if (event.getMessage().split(" ")[0].equalsIgnoreCase("/join")) {
            event.setCancelled(true);
            event.getPlayer().performCommand("game join");
        }
    }

    @EventHandler
    public void onGainExp(PlayerGainExpEvent event) {
        Player player = event.getPlayer();
        Profile profile = ProfileManager.getProfile(player);

        if (profile.canRankUp()) {
            profile.rankUp();
        }

        String type = StringUtils.capitalize(event.getType().name().toLowerCase());

        player.sendMessage(ColorText.translate("&6+ " + event.getAmount() + " XP (" + type + ")"));
    }

}