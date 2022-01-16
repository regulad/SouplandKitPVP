package us.soupland.kitpvp.kits.types;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import us.soupland.kitpvp.KitPvP;
import us.soupland.kitpvp.enums.PlayerState;
import us.soupland.kitpvp.enums.Refill;
import us.soupland.kitpvp.kits.Kit;
import us.soupland.kitpvp.profile.Profile;
import us.soupland.kitpvp.profile.ProfileManager;
import us.soupland.kitpvp.utilities.chat.ColorText;
import us.soupland.kitpvp.utilities.cooldown.Cooldown;
import us.soupland.kitpvp.utilities.item.ItemMaker;
import us.soupland.kitpvp.utilities.player.DurationFormatter;
import us.soupland.kitpvp.utilities.task.TaskUtil;
import us.soupland.kitpvp.utilities.time.TimeUtils;

import java.util.ArrayList;

public class StomperKit extends Kit {

    public StomperKit() {
        super("Stomper", "&bStomper", "20s");
        new Cooldown(getName(), TimeUtils.parse(getCooldown()), getDisplayName(), null);
    }

    @Override
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Profile profile = ProfileManager.getProfile(player);

        if (player.getItemInHand() == null || player.getItemInHand().getType() != Material.ANVIL) return;
        if (profile.getPlayerState() == PlayerState.SPAWN) {
            player.sendMessage(ColorText.translateAmpersand("&cYou can't do this in Spawn."));
            return;
        }
        if (player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR) {
            return;
        }
        Cooldown cooldown = KitPvP.getCooldown(getName());
        if (cooldown.getDuration(player) > 0L) {
            player.sendMessage(ColorText.translateAmpersand("&cYou are on cooldown for another &e" + DurationFormatter.getRemaining(cooldown.getDuration(player), true) + "&c."));
            return;
        }
        cooldown.setCooldown(player);
        player.setVelocity(new Vector(0, 2, 0));
        profile.setFell(true);
        TaskUtil.runTaskLater(() -> player.setVelocity(new Vector(0, -2, 0).normalize()), 20L);
    }

    @Override
    public void onLoad(Player player) {
        PlayerInventory inventory = player.getInventory();
        Profile profile = ProfileManager.getProfile(player);

        inventory.setHelmet(new ItemMaker(Material.CHAINMAIL_HELMET).setEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).setUnbreakable(true).create());
        inventory.setChestplate(new ItemMaker(Material.CHAINMAIL_CHESTPLATE).setEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).setUnbreakable(true).create());
        inventory.setLeggings(new ItemMaker(Material.CHAINMAIL_LEGGINGS).setEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).setUnbreakable(true).create());
        inventory.setBoots(new ItemMaker(Material.CHAINMAIL_BOOTS).setEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).setUnbreakable(true).create());

        inventory.setItem(0, new ItemMaker(Material.DIAMOND_SWORD).setUnbreakable(true).create());
        inventory.setItem(1, new ItemMaker(Material.ANVIL).setDisplayname(profile.getTheme().getPrimaryColor() + getName() + " Ability").create());

        for (int i = 0; i < 36; i++) {
            inventory.addItem(new ItemMaker((profile.getRefill() == Refill.POTION && !profile.getPlayerState().name().contains("PRACTICE") && profile.getPlayerState() != PlayerState.INGAME ? Material.POTION : Material.MUSHROOM_SOUP)).setDurability(profile.getRefill() == Refill.POTION && !profile.getPlayerState().name().contains("PRACTICE") && profile.getPlayerState() != PlayerState.INGAME ? 16421 : 0).setDisplayname(profile.getRefill() == Refill.POTION && !profile.getPlayerState().name().contains("PRACTICE") && profile.getPlayerState() != PlayerState.INGAME ? "&dPotion" : "&6Soup").addLore(profile.getRefill() == Refill.POTION && !profile.getPlayerState().name().contains("PRACTICE") && profile.getPlayerState() != PlayerState.INGAME ? "" : "&7Heals 3.5 Health").create());
        }

        setPlayerInventory(inventory.getArmorContents());
        setInventory(inventory.getContents());

        player.updateInventory();

        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200000, 0));
        setEffects(new ArrayList<>(player.getActivePotionEffects()));
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            Profile profile = ProfileManager.getProfile(player);

            if (profile.getCurrentKit() instanceof StomperKit && event.getCause() == EntityDamageEvent.DamageCause.FALL) {
                double damage = Math.max(18, event.getDamage() * 0.75);
                event.setCancelled(true);
                player.setHealth(player.getHealth() - 8);

                if (!profile.isFell()) {
                    return;
                }

                if (profile.isFrozenToUseAbility()) {
                    player.sendMessage(ColorText.translateAmpersand("&cYou are currently jammed, so you can not use your ability."));
                    return;
                }

                if (player.getNearbyEntities(3, 3, 3).isEmpty()) {
                    return;
                }

                for (Entity entity : player.getNearbyEntities(3, 3, 3)) {
                    if (!(entity instanceof Player)) {
                        continue;
                    }
                    Player nearby = (Player) entity;
                    if (nearby.getLocation().getBlockY() <= player.getLocation().getBlockY()) {
                        if (!nearby.isSneaking()) {
                            nearby.damage(damage, player);
                            continue;
                        }
                        nearby.damage((damage / 2), player);
                        /*if ((damage / 5) >= 10) {
                            nearby.damage(10.0, player);
                        } else {
                            nearby.damage((damage / 5), player);
                        }*/
                    }
                }
            }
        }
    }

}