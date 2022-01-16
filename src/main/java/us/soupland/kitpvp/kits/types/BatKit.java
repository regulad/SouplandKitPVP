package us.soupland.kitpvp.kits.types;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
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

public class BatKit extends Kit {

    public BatKit() {
        super("Bat", "&aBat", "15s");
        new Cooldown(getName(), TimeUtils.parse(getCooldown()), getDisplayName(), null);
    }

    @Override
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Profile profile = ProfileManager.getProfile(player);

        if (player.getItemInHand() == null || player.getItemInHand().getType() != Material.REDSTONE_TORCH_ON) return;
        if (profile.getPlayerState() == PlayerState.SPAWN) {
            player.sendMessage(ColorText.translateAmpersand("&cYou can't do this in Spawn."));
            return;
        }

        if (player.getNearbyEntities(8, 8, 8).isEmpty()) {
            player.sendMessage(ColorText.translateAmpersand("&cThere are no players in a 8 block radius to target."));
            return;
        }
        Cooldown cooldown = KitPvP.getCooldown(getName());
        if (cooldown.getDuration(player) > 0L) {
            player.sendMessage(ColorText.translateAmpersand("&cYou are on cooldown for another &e" + DurationFormatter.getRemaining(cooldown.getDuration(player), true) + "&c."));
            return;
        }
        cooldown.setCooldown(player);

        for (Entity entity : player.getNearbyEntities(8, 8, 8)) {
            if (!(entity instanceof Player) || entity == player) {
                continue;
            }
            ((Player) entity).addPotionEffect(new PotionEffect(PotionEffectType.POISON, 20 * 5, 3));
        }

        for (int i = 0; i < 3; i++) {
            Bat bat = (Bat) player.getWorld().spawnEntity(player.getEyeLocation(), EntityType.BAT);
            bat.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200000, 3));
            bat.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 200000, 3));
            bat.setCanPickupItems(false);
            bat.setMaxHealth(2000);
            bat.setHealth(2000);
            bat.setCustomName(ColorText.translateAmpersand(player.getName()));
            bat.setCustomNameVisible(true);
            bat.setMetadata(player.getUniqueId().toString(), new FixedMetadataValue(KitPvP.getInstance(), "faggotBat"));
        }

        player.setVelocity(player.getLocation().getDirection().multiply(2).setY(1));
        player.playSound(player.getLocation(), Sound.BAT_TAKEOFF, 1, 1);

        World world = player.getWorld();
        String uuid = player.getUniqueId().toString();

        TaskUtil.runTaskLater(() -> {
            for (Entity entity : world.getEntities()) {
                if (entity.hasMetadata(uuid)) {
                    entity.remove();
                }
            }
        }, 5 * 20L);
    }

    @Override
    public void onLoad(Player player) {
        PlayerInventory inventory = player.getInventory();
        Profile profile = ProfileManager.getProfile(player);

        inventory.setHelmet(new ItemMaker(Material.LEATHER_HELMET).setColor(Color.BLACK).setEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).setUnbreakable(true).create());
        inventory.setChestplate(new ItemMaker(Material.LEATHER_CHESTPLATE).setColor(Color.GREEN).setEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).setUnbreakable(true).create());
        inventory.setLeggings(new ItemMaker(Material.LEATHER_LEGGINGS).setColor(Color.BLACK).setEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).setUnbreakable(true).create());
        inventory.setBoots(new ItemMaker(Material.LEATHER_BOOTS).setColor(Color.GREEN).setEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).setUnbreakable(true).create());

        inventory.setItem(0, new ItemMaker(Material.STONE_SWORD).setEnchant(Enchantment.DAMAGE_ALL, 3).setUnbreakable(true).create());
        inventory.setItem(1, new ItemMaker(Material.REDSTONE_TORCH_ON).setDisplayname(profile.getTheme().getPrimaryColor() + getName() + " Ability").create());

        for (int i = 0; i < 36; i++) {
            inventory.addItem(new ItemMaker((profile.getRefill() == Refill.POTION && !profile.getPlayerState().name().contains("PRACTICE") && profile.getPlayerState() != PlayerState.INGAME ? Material.POTION : Material.MUSHROOM_SOUP)).setDurability(profile.getRefill() == Refill.POTION && !profile.getPlayerState().name().contains("PRACTICE") && profile.getPlayerState() != PlayerState.INGAME ? 16421 : 0).setDisplayname(profile.getRefill() == Refill.POTION && !profile.getPlayerState().name().contains("PRACTICE") && profile.getPlayerState() != PlayerState.INGAME ? "&dPotion" : "&6Soup").addLore(profile.getRefill() == Refill.POTION && !profile.getPlayerState().name().contains("PRACTICE") && profile.getPlayerState() != PlayerState.INGAME ? "" : "&7Heals 3.5 Health").create());
        }

        setPlayerInventory(inventory.getArmorContents());
        setInventory(inventory.getContents());

        player.updateInventory();

        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200000, 1));
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 200000, 0));
        setEffects(new ArrayList<>(player.getActivePotionEffects()));
    }
}