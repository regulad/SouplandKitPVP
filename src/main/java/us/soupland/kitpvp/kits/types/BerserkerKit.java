package us.soupland.kitpvp.kits.types;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import us.soupland.kitpvp.enums.PlayerState;
import us.soupland.kitpvp.enums.Refill;
import us.soupland.kitpvp.kits.Kit;
import us.soupland.kitpvp.profile.Profile;
import us.soupland.kitpvp.profile.ProfileManager;
import us.soupland.kitpvp.utilities.KitPvPUtils;
import us.soupland.kitpvp.utilities.item.ItemMaker;

import java.util.ArrayList;
import java.util.List;

public class BerserkerKit extends Kit {

    public BerserkerKit() {
        super("Berserker", "&bBerserker", "");
    }

    @Override
    public void execute(PlayerInteractEvent event) {
    }

    @Override
    public void onLoad(Player player) {
        PlayerInventory inventory = player.getInventory();
        Profile profile = ProfileManager.getProfile(player);

        inventory.setHelmet(new ItemMaker(Material.GLASS).setDurability(11).setUnbreakable(true).create());
        inventory.setChestplate(new ItemMaker(Material.IRON_CHESTPLATE).setEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).setUnbreakable(true).create());
        inventory.setLeggings(new ItemMaker(Material.CHAINMAIL_LEGGINGS).setEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).setUnbreakable(true).create());
        inventory.setBoots(new ItemMaker(Material.IRON_BOOTS).setEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).setUnbreakable(true).create());

        inventory.setItem(0, new ItemMaker(Material.STONE_SWORD).setEnchant(Enchantment.DAMAGE_ALL, 2).setUnbreakable(true).create());

        for (int i = 0; i < 36; i++) {
            inventory.addItem(new ItemMaker((profile.getRefill() == Refill.POTION && !profile.getPlayerState().name().contains("PRACTICE") && profile.getPlayerState() != PlayerState.INGAME ? Material.POTION : Material.MUSHROOM_SOUP)).setDurability(profile.getRefill() == Refill.POTION && !profile.getPlayerState().name().contains("PRACTICE") && profile.getPlayerState() != PlayerState.INGAME ? 16421 : 0).setDisplayname(profile.getRefill() == Refill.POTION && !profile.getPlayerState().name().contains("PRACTICE") && profile.getPlayerState() != PlayerState.INGAME ? "&dPotion" : "&6Soup").addLore(profile.getRefill() == Refill.POTION && !profile.getPlayerState().name().contains("PRACTICE") && profile.getPlayerState() != PlayerState.INGAME ? "" : "&7Heals 3.5 Health").create());
        }

        setPlayerInventory(inventory.getArmorContents());
        setInventory(inventory.getContents());
        setEffects(new ArrayList<>(player.getActivePotionEffects()));
        player.updateInventory();
    }

    @Override
    public ItemStack getItem() {
        return new ItemMaker(Material.POTION).setDisplayname(getDisplayName()).addLore(getDescription()).create();
    }

    @Override
    public String getPermissions() {
        return "soupland.kit." + getName().toLowerCase();
    }

    @Override
    public int getCredits() {
        return 5300;
    }

    @Override
    public List<String> getDescription() {
        List<String> list = new ArrayList<>();
        list.add("");
        list.add("&7Every kill you get, you receive a positive effect.");
        list.add("");
        return getConfig().getStringList("Kits." + this.getName() + ".description");
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (event.getEntity().getKiller() != null && event.getEntity() != event.getEntity().getKiller()) {
            Player player = event.getEntity().getKiller();
            Profile profile = ProfileManager.getProfile(player);
            if (profile.getCurrentKit() instanceof BerserkerKit) {
                List<PotionEffectType> effects = new ArrayList<>();
                effects.add(PotionEffectType.SPEED);
                effects.add(PotionEffectType.DAMAGE_RESISTANCE);
                effects.add(PotionEffectType.INCREASE_DAMAGE);
                effects.add(PotionEffectType.ABSORPTION);
                effects.add(PotionEffectType.FIRE_RESISTANCE);
                effects.add(PotionEffectType.INVISIBILITY);
                effects.add(PotionEffectType.JUMP);
                effects.add(PotionEffectType.REGENERATION);
                player.addPotionEffect(new PotionEffect(effects.get(KitPvPUtils.getRandomNumber(effects.size())), 6 * 20, 2));
            }
        }
    }

}