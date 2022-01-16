package us.soupland.kitpvp.kits.types;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import us.soupland.kitpvp.enums.PlayerState;
import us.soupland.kitpvp.enums.Refill;
import us.soupland.kitpvp.kits.Kit;
import us.soupland.kitpvp.profile.Profile;
import us.soupland.kitpvp.profile.ProfileManager;
import us.soupland.kitpvp.utilities.item.ItemMaker;

import java.util.ArrayList;

public class SpeedMonsterKit extends Kit {

    public SpeedMonsterKit() {
        super("Speed Monster", "&aSpeed Monster", "");
    }

    @Override
    public void onLoad(Player player) {
        PlayerInventory inventory = player.getInventory();
        Profile profile = ProfileManager.getProfile(player);

        inventory.setHelmet(new ItemMaker(Material.LEATHER_HELMET).setColor(Color.BLUE).setUnbreakable(true).create());
        inventory.setChestplate(new ItemMaker(Material.CHAINMAIL_CHESTPLATE).setUnbreakable(true).create());
        inventory.setLeggings(new ItemMaker(Material.CHAINMAIL_LEGGINGS).setUnbreakable(true).create());
        inventory.setBoots(new ItemMaker(Material.LEATHER_BOOTS).setColor(Color.BLUE).setUnbreakable(true).create());

        inventory.setItem(0, new ItemMaker(Material.DIAMOND_SWORD).setEnchant(Enchantment.DAMAGE_ALL, 1).setUnbreakable(true).create());

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
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (event.getEntity().getKiller() != null) {
            Player player = event.getEntity().getKiller();
            Profile profile = ProfileManager.getProfile(player);
            if (profile.getCurrentKit() instanceof SpeedMonsterKit) {
                int level = getSpeedLevel(player) + 1;
                if (level <= 4) {
                    player.removePotionEffect(PotionEffectType.SPEED);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200000, level), true);
                }
            }
        }
    }

    private int getSpeedLevel(Player player) {
        for (PotionEffect effect : player.getActivePotionEffects()) {
            if (effect.getType() == PotionEffectType.SPEED) {
                return effect.getAmplifier();
            }
        }
        return 0;
    }

}