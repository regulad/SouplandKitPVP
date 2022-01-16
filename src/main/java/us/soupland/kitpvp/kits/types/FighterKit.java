package us.soupland.kitpvp.kits.types;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
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
import us.soupland.kitpvp.utilities.time.TimeUtils;

import java.util.ArrayList;

public class FighterKit extends Kit {

    public FighterKit() {
        super("Fighter", "&aFighter", "12s");
        new Cooldown(getName(), TimeUtils.parse(getCooldown()), getDisplayName(), null);
    }

    @Override
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Profile profile = ProfileManager.getProfile(player);

        if (player.getItemInHand() == null || player.getItemInHand().getType() != Material.ENDER_CHEST) return;
        if (profile.getPlayerState() == PlayerState.SPAWN) {
            player.sendMessage(ColorText.translateAmpersand("&cYou can't do this in Spawn."));
            return;
        }

        if (player.getHealth() >= player.getMaxHealth()) {
            player.sendMessage(ColorText.translateAmpersand("&c" + player.getName() + " has already full heart."));
            return;
        }

        Cooldown cooldown = KitPvP.getCooldown(getName());
        if (cooldown.isOnCooldown(player)) {
            player.sendMessage(ColorText.translateAmpersand("&cYou are on cooldown for another &e" + DurationFormatter.getRemaining(cooldown.getDuration(player), true) + "&c."));
            return;
        }
        cooldown.setCooldown(player);

        for (PotionEffect effect : player.getActivePotionEffects()) {
            if (effect.getType().equals(PotionEffectType.SPEED)) {
                profile.getEffectsSaved().add(effect);
            }
        }

        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 10 * 20, 2), true);
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 10 * 20, 1), true);

        if (player.getItemInHand().getAmount() < 2) {
            player.setItemInHand(new ItemStack(Material.AIR));
        } else {
            player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
        }
        player.updateInventory();
        player.sendMessage(ColorText.translateAmpersand("&eYou have used your &a&lHealth Package&e."));
    }

    @Override
    public void onLoad(Player player) {
        PlayerInventory inventory = player.getInventory();
        Profile profile = ProfileManager.getProfile(player);

        inventory.setHelmet(new ItemMaker(Material.IRON_HELMET).setEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).setUnbreakable(true).create());
        inventory.setChestplate(new ItemMaker(Material.IRON_CHESTPLATE).setEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).setUnbreakable(true).create());
        inventory.setLeggings(new ItemMaker(Material.IRON_LEGGINGS).setEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).setUnbreakable(true).create());
        inventory.setBoots(new ItemMaker(Material.IRON_BOOTS).setEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).setUnbreakable(true).create());

        inventory.setItem(0, new ItemMaker(Material.IRON_SWORD).setEnchant(Enchantment.DAMAGE_ALL, 1).setUnbreakable(true).create());
        inventory.setItem(1, new ItemMaker(Material.ENDER_CHEST).setDisplayname(profile.getTheme().getPrimaryColor() + "Health Package").setAmount(3).create());

        for (int i = 0; i < 36; i++) {
            inventory.addItem(new ItemMaker((profile.getRefill() == Refill.POTION && !profile.getPlayerState().name().contains("PRACTICE") && profile.getPlayerState() != PlayerState.INGAME ? Material.POTION : Material.MUSHROOM_SOUP)).setDurability(profile.getRefill() == Refill.POTION && !profile.getPlayerState().name().contains("PRACTICE") && profile.getPlayerState() != PlayerState.INGAME ? 16421 : 0).setDisplayname(profile.getRefill() == Refill.POTION && !profile.getPlayerState().name().contains("PRACTICE") && profile.getPlayerState() != PlayerState.INGAME ? "&dPotion" : "&6Soup").addLore(profile.getRefill() == Refill.POTION && !profile.getPlayerState().name().contains("PRACTICE") && profile.getPlayerState() != PlayerState.INGAME ? "" : "&7Heals 3.5 Health").create());
        }

        setPlayerInventory(inventory.getArmorContents());
        setInventory(inventory.getContents());

        player.updateInventory();

        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200000, 1));
        setEffects(new ArrayList<>(player.getActivePotionEffects()));
    }
}