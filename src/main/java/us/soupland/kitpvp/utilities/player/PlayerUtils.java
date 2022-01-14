package us.soupland.kitpvp.utilities.player;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;
import us.soupland.kitpvp.KitPvP;
import us.soupland.kitpvp.enums.PlayerItem;
import us.soupland.kitpvp.enums.PlayerState;
import us.soupland.kitpvp.enums.Refill;
import us.soupland.kitpvp.games.Game;
import us.soupland.kitpvp.kits.Kit;
import us.soupland.kitpvp.profile.Profile;
import us.soupland.kitpvp.profile.ProfileManager;
import us.soupland.kitpvp.utilities.chat.ColorText;
import us.soupland.kitpvp.utilities.item.ItemMaker;
import us.soupland.kitpvp.utilities.task.TaskUtil;

public class PlayerUtils {

    public static void resetPlayer(Player player, boolean died, boolean spawn) {
        Profile profile = ProfileManager.getProfile(player);

        profile.setCanModifyState(true);
        profile.setGgMode(false);
        profile.setFell(false);
        profile.setFrozenToUseAbility(false);
        profile.setFrozenByAbility(false);
        profile.setCurrentKit(null);
        profile.setRecallLocation(null);

        if (profile.getPlayerCombat() > 0L) {
            profile.setPlayerCombat(0L);
        }
        if (died) {
            player.spigot().respawn();
            TaskUtil.runTask(() -> ((CraftPlayer) player).getHandle().getDataWatcher().watch(9, (byte) 0));
        }
        if (spawn) {
            Location location = KitPvP.getInstance().getServerData().getSpawnLocation();
            if (location != null) {
                TaskUtil.runTask(() -> profile.setPlayerState(PlayerState.SPAWN));
                player.teleport(location);
            }
        }

        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }

        player.setMaxHealth(20);
        player.setHealth(player.getMaxHealth());
        player.setFoodLevel(20);
        player.setFireTicks(0);
        player.setSaturation(20);
        player.setExp(0);
        player.setLevel(0);
        player.setGameMode(GameMode.SURVIVAL);
        player.setAllowFlight(false);
        player.setFlying(false);

        Bukkit.getOnlinePlayers().forEach(online -> {
            online.showPlayer(player);
        });

        PlayerInventory inventory = player.getInventory();

        inventory.setHeldItemSlot(0);

        inventory.clear();
        inventory.setArmorContents(null);

        inventory.setItem(0, PlayerItem.SPAWN_KIT_ITEM.getItem());
        if (profile.getLastKit() != null) {
            ItemStack stack = new ItemMaker(PlayerItem.SPAWN_PREVIOUS_ITEM.getItem()).setInteractRight(data -> {
                Kit kit = profile.getLastKit();
                if (kit == null) {
                    inventory.setItem(1, new ItemStack(Material.AIR));
                    player.sendMessage(ColorText.translateAmpersand("&cKit not found."));
                } else {

                    if (kit.getPermissionNode() != null && !player.hasPermission(kit.getPermissionNode())) {
                        player.sendMessage(ColorText.translateAmpersand("&7&m" + StringUtils.repeat("-", 30)));
                        player.sendMessage(ColorText.translateAmpersand("&eYou do not have access to " + kit.getDisplayName() + "&e!"));
                        player.sendMessage(ColorText.translateAmpersand("&ePurchase kits at our store, complete achievements & rankup, or buy kits at the /shop!"));
                        player.sendMessage(ColorText.translateAmpersand("&6Donate at &ahttps://store.soupland.us"));
                        player.sendMessage(ColorText.translateAmpersand("&7&m" + StringUtils.repeat("-", 30)));
                        return;
                    }

                    inventory.clear();
                    inventory.setArmorContents(null);

                    for (PotionEffect effect : player.getActivePotionEffects()) {
                        player.removePotionEffect(effect.getType());
                    }
                    profile.setCurrentKit(kit);
                    kit.onLoad(player);
                }
                player.updateInventory();
            }).create();
            ItemMeta meta = stack.getItemMeta();
            meta.setDisplayName(meta.getDisplayName().replace("%NAME%", profile.getLastKit().getName().toUpperCase()));
            stack.setItemMeta(meta);
            inventory.setItem(1, stack);
        }
        Game game = KitPvP.getInstance().getGameHandler().getUpcomingGame();
        if (game == null) {
            game = KitPvP.getInstance().getGameHandler().getActiveGame();
        }

        if (game != null) {
            inventory.setItem(3, PlayerItem.SPAWN_EVENT_ITEM.getItem());
        } else {
            inventory.setItem(3, PlayerItem.SPAWN_LEADERBOARD_ITEM.getItem());
        }
        inventory.setItem(4, PlayerItem.SPAWN_MENU_ITEM.getItem());
        inventory.setItem(7, PlayerItem.SPAWN_HOST_ITEM.getItem());
        inventory.setItem(8, PlayerItem.SPAWN_ARENA_ITEM.getItem());
        inventory.setItem(17, PlayerItem.SPAWN_ACHIEVEMENTS_ITEM.getItem());

        setRefill(player);

        player.updateInventory();
    }

    public static void managePlayerMovement(Player player, boolean allowed) {
        player.setWalkSpeed((allowed ? 0.2F : 0.0F));
        player.setFlySpeed((allowed ? 0.1F : 0.0F));
        player.setFoodLevel((allowed ? 20 : 0));
        player.setSprinting(allowed);
        if (allowed) {
            player.removePotionEffect(PotionEffectType.JUMP);
        } else {
            player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 200));
        }
    }

    private static void setRefill(Player player) {
        PlayerInventory inventory = player.getInventory();
        Profile profile = ProfileManager.getProfile(player);

        inventory.setItem(5, new ItemMaker((profile.getRefill() == Refill.SOUP ? Material.POTION : Material.MUSHROOM_SOUP)).setDurability((profile.getRefill() == Refill.SOUP ? 16421 : 0)).setDisplayname((profile.getRefill() == Refill.SOUP ? "&dSwitch to Potion" : "&eSwitch to Soup")).addLore("&4Bound").setInteractRight(player1 -> {
            profile.setRefill((profile.getRefill() == Refill.SOUP ? Refill.POTION : Refill.SOUP));
            setRefill(player);
            player.updateInventory();
        }).create());
    }

    public static Player getFinalAttacker(EntityDamageEvent ede, boolean ignoreSelf) {
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
}