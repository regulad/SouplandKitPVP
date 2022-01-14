package us.soupland.kitpvp.kits.types;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import us.soupland.kitpvp.enums.PlayerState;
import us.soupland.kitpvp.enums.Refill;
import us.soupland.kitpvp.kits.Kit;
import us.soupland.kitpvp.profile.Profile;
import us.soupland.kitpvp.profile.ProfileManager;
import us.soupland.kitpvp.utilities.chat.ColorText;
import us.soupland.kitpvp.utilities.item.ItemMaker;
import us.soupland.kitpvp.utilities.time.TimeUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CactusKit extends Kit {

    private Map<Player, Long> playerLongMap = new HashMap<>();

    public CactusKit() {
        super("Cactus", "&2Cactus", "");
    }

    @Override
    public void onLoad(Player player) {
        PlayerInventory inventory = player.getInventory();
        Profile profile = ProfileManager.getProfile(player);

        inventory.setHelmet(new ItemMaker(Material.LEATHER_HELMET).setColor(Color.GREEN).setUnbreakable(true).create());
        inventory.setChestplate(new ItemMaker(Material.LEATHER_CHESTPLATE).setColor(Color.GREEN).setUnbreakable(true).create());
        inventory.setLeggings(new ItemMaker(Material.LEATHER_LEGGINGS).setColor(Color.GREEN).setUnbreakable(true).create());
        inventory.setBoots(new ItemMaker(Material.LEATHER_BOOTS).setColor(Color.GREEN).setUnbreakable(true).create());

        inventory.setItem(0, new ItemMaker(Material.DIAMOND_SWORD).setEnchant(Enchantment.DAMAGE_ALL, 1).setUnbreakable(true).create());

        for (int i = 0; i < 36; i++) {
            inventory.addItem(new ItemMaker((profile.getRefill() == Refill.POTION && !profile.getPlayerState().name().contains("PRACTICE") && profile.getPlayerState() != PlayerState.INGAME ? Material.POTION : Material.MUSHROOM_SOUP)).setDurability(profile.getRefill() == Refill.POTION && !profile.getPlayerState().name().contains("PRACTICE") && profile.getPlayerState() != PlayerState.INGAME ? 16421 : 0).setDisplayname(profile.getRefill() == Refill.POTION && !profile.getPlayerState().name().contains("PRACTICE") && profile.getPlayerState() != PlayerState.INGAME ? "&dPotion" : "&6Soup").addLore(profile.getRefill() == Refill.POTION && !profile.getPlayerState().name().contains("PRACTICE") && profile.getPlayerState() != PlayerState.INGAME ? "" : "&7Heals 3.5 Health").create());
        }

        setPlayerInventory(inventory.getArmorContents());
        setInventory(inventory.getContents());

        player.updateInventory();

        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200000, 0));
        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 200000, 0));
        setEffects(new ArrayList<>(player.getActivePotionEffects()));
    }

    @Override
    public ItemStack getItem() {
        return new ItemMaker(Material.CACTUS).setDisplayname(getDisplayName()).addLore(getDescription()).create();
    }

    @Override
    public int getCreditCost() {
        return 4600;
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Profile profile = ProfileManager.getProfile((Player) event.getEntity());
            if (profile.getCurrentKit() instanceof CactusKit) {
                if (profile.isFrozenToUseAbility()) {
                    if (isOnCooldown((Player) event.getEntity())) {
                        return;
                    }
                    event.getEntity().sendMessage(ColorText.translateAmpersand("&cYou are currently jammed, so you can not use your ability."));
                    playerLongMap.put((Player) event.getEntity(), TimeUtils.parse("5s") + System.currentTimeMillis());
                    return;
                }
                ((Player) event.getDamager()).damage(event.getDamage() * 0.25);
                //events.getDamager().sendMessage(ColorText.translate("&a" + events.getEntity().getName() + " has pricked you!"));
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        playerLongMap.remove(event.getPlayer());
    }

    private boolean isOnCooldown(Player player) {
        return playerLongMap.containsKey(player) && (playerLongMap.get(player) - System.currentTimeMillis()) > 0L;
    }

}