package us.soupland.kitpvp.kits.types;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import us.soupland.kitpvp.enums.PlayerState;
import us.soupland.kitpvp.enums.Refill;
import us.soupland.kitpvp.kits.Kit;
import us.soupland.kitpvp.profile.Profile;
import us.soupland.kitpvp.profile.ProfileManager;
import us.soupland.kitpvp.utilities.KitPvPUtils;
import us.soupland.kitpvp.utilities.chat.ColorText;
import us.soupland.kitpvp.utilities.item.ItemMaker;
import us.soupland.kitpvp.utilities.time.TimeUtils;

import java.util.*;

public class IronmanKit extends Kit {

    private Map<UUID, Long> longMap = new HashMap<>();

    public IronmanKit() {
        super("Ironman", "&cIronman", "");
    }

    @Override
    public void execute(PlayerInteractEvent event) {

    }

    @Override
    public void onLoad(Player player) {
        PlayerInventory inventory = player.getInventory();
        Profile profile = ProfileManager.getProfile(player);

        inventory.setHelmet(new ItemMaker(Material.CHAINMAIL_HELMET).setEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).setUnbreakable(true).create());
        inventory.setChestplate(new ItemMaker(Material.CHAINMAIL_CHESTPLATE).setEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).setUnbreakable(true).create());
        inventory.setLeggings(new ItemMaker(Material.CHAINMAIL_LEGGINGS).setEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).setUnbreakable(true).create());
        inventory.setBoots(new ItemMaker(Material.CHAINMAIL_BOOTS).setEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).setUnbreakable(true).create());

        inventory.setItem(0, new ItemMaker(Material.IRON_SWORD).setEnchant(Enchantment.DAMAGE_ALL, 1).setUnbreakable(true).create());

        for (int i = 0; i < 36; i++) {
            inventory.addItem(new ItemMaker((profile.getRefill() == Refill.POTION && !profile.getPlayerState().name().contains("PRACTICE") && profile.getPlayerState() != PlayerState.INGAME ? Material.POTION : Material.MUSHROOM_SOUP)).setDurability(profile.getRefill() == Refill.POTION && !profile.getPlayerState().name().contains("PRACTICE") && profile.getPlayerState() != PlayerState.INGAME ? 16421 : 0).setDisplayname(profile.getRefill() == Refill.POTION && !profile.getPlayerState().name().contains("PRACTICE") && profile.getPlayerState() != PlayerState.INGAME ? "&dPotion" : "&6Soup").addLore(profile.getRefill() == Refill.POTION && !profile.getPlayerState().name().contains("PRACTICE") && profile.getPlayerState() != PlayerState.INGAME ? "" : "&7Heals 3.5 Health").create());
        }

        setPlayerInventory(inventory.getArmorContents());
        setInventory(inventory.getContents());

        player.updateInventory();
        setEffects(new ArrayList<>(player.getActivePotionEffects()));
    }

    @Override
    public ItemStack getItem() {
        return new ItemMaker(Material.IRON_INGOT).setDisplayname(getDisplayName()).addLore(getDescription()).create();
    }

    @Override
    public String getPermissions() {
        return "soupland.kit." + getName().toLowerCase();
    }

    @Override
    public int getCredits() {
        return 4900;
    }

    @Override
    public List<String> getDescription() {
        List<String> list = new ArrayList<>();
        list.add("");
        list.add("&7Reduce damage by 5-20%.");
        list.add("");
        return getConfig().getStringList("Kits." + this.getName() + ".description");
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
            Profile profile = ProfileManager.getProfile(player);
            if (profile.getCurrentKit() instanceof IronmanKit) {
                if (profile.isFrozenToUseAbility()) {
                    if (getRemaining(player.getUniqueId()) > 0L) {
                        return;
                    }
                    longMap.put(player.getUniqueId(), TimeUtils.parse("5s") + System.currentTimeMillis());
                    player.sendMessage(ColorText.translate("&cYou are currently jammed, so you can not use your ability."));
                    return;
                }
                event.setDamage(KitPvPUtils.getRandomNumber(20) / 0.25);
            }
        }
    }

    private long getRemaining(UUID uuid) {
        long faggot;
        if (longMap.containsKey(uuid) && (faggot = longMap.get(uuid) - System.currentTimeMillis()) > 0L) {
            return faggot;
        }
        return 0L;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        longMap.remove(event.getPlayer().getUniqueId());
    }

}