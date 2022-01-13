package us.soupland.kitpvp.kits.types;

import us.soupland.kitpvp.utilities.chat.ColorText;
import us.soupland.kitpvp.utilities.item.ItemMaker;
import us.soupland.kitpvp.enums.PlayerState;
import us.soupland.kitpvp.enums.Refill;
import us.soupland.kitpvp.kits.Kit;
import us.soupland.kitpvp.profile.Profile;
import us.soupland.kitpvp.profile.ProfileManager;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BackstabKit extends Kit {

    public BackstabKit() {
        super("Backstab", "&bBackstab", "");
    }

    @Override
    public void execute(PlayerInteractEvent event) {

    }

    @Override
    public void onLoad(Player player) {
        PlayerInventory inventory = player.getInventory();
        Profile profile = ProfileManager.getProfile(player);

        inventory.setHelmet(new ItemMaker(Material.CHAINMAIL_HELMET).setUnbreakable(true).create());
        inventory.setChestplate(new ItemMaker(Material.CHAINMAIL_CHESTPLATE).setUnbreakable(true).create());
        inventory.setLeggings(new ItemMaker(Material.GOLD_LEGGINGS).setUnbreakable(true).create());
        inventory.setBoots(new ItemMaker(Material.DIAMOND_BOOTS).setUnbreakable(true).create());

        inventory.setItem(0, new ItemMaker(Material.IRON_SWORD).setUnbreakable(true).create());
        inventory.setItem(1, new ItemMaker(Material.GOLD_SWORD).setDisplayname("&bDagger &7(Backstab Someone)").create());

        for (int i = 0; i < 36; i++) {
            inventory.addItem(new ItemMaker((profile.getRefill() == Refill.POTION && !profile.getPlayerState().name().contains("PRACTICE") && profile.getPlayerState() != PlayerState.INGAME ? Material.POTION : Material.MUSHROOM_SOUP)).setDurability(profile.getRefill() == Refill.POTION && !profile.getPlayerState().name().contains("PRACTICE") && profile.getPlayerState() != PlayerState.INGAME ? 16421 : 0).setDisplayname(profile.getRefill() == Refill.POTION && !profile.getPlayerState().name().contains("PRACTICE") && profile.getPlayerState() != PlayerState.INGAME ? "&dPotion" : "&6Soup").addLore(profile.getRefill() == Refill.POTION && !profile.getPlayerState().name().contains("PRACTICE") && profile.getPlayerState() != PlayerState.INGAME ? "" : "&7Heals 3.5 Health").create());
        }

        setPlayerInventory(inventory.getArmorContents());
        setInventory(inventory.getContents());

        player.updateInventory();

        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200000, 0));
        setEffects(new ArrayList<>(player.getActivePotionEffects()));
    }

    @Override
    public ItemStack getItem() {
        return new ItemMaker(Material.GOLD_SWORD).setDisplayname(getDisplayName()).addLore(getDescription()).create();
    }

    @Override
    public String getPermissions() {
        return "soupland.kit." + getName().toLowerCase();
    }

    @Override
    public int getCredits() {
        return 2500;
    }

    @Override
    public List<String> getDescription() {
        List<String> list = new ArrayList<>();
        list.add("");
        list.add("&7Use your &6GOLDEN SWORD &7to");
        list.add("&7backstab people (does 8+ hearts).");
        list.add("");
        return getConfig().getStringList("Kits." + this.getName() + ".description");
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player player = (Player) event.getEntity();
            Player attacker = (Player) event.getDamager();

            Profile profile = ProfileManager.getProfile(attacker);

            if (profile.getPlayerState() == PlayerState.SPAWN || ProfileManager.getProfile(attacker).getPlayerState() == PlayerState.SPAWN) {
                return;
            }

            if (!(profile.getCurrentKit() instanceof BackstabKit)) {
                return;
            }

            if (attacker.getItemInHand() == null || attacker.getItemInHand().getType() != Material.GOLD_SWORD) {
                return;
            }

            double dot = player.getLocation().getDirection().dot(attacker.getLocation().getDirection()) + 1.0D;

            if (dot >= 0.86D) {
                if (profile.isFrozenToUseAbility()) {
                    player.sendMessage(ColorText.translate("&cYou are currently jammed, so you can not use your ability."));
                    return;
                }
                attacker.setItemInHand(new ItemStack(Material.AIR, 1));
                attacker.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 2));
                attacker.playSound(player.getLocation(), Sound.ITEM_BREAK, 1.0F, 1.0F);
                player.playSound(player.getLocation(), Sound.ITEM_BREAK, 1.0F, 1.0F);
                attacker.updateInventory();
                //event.setDamage(8.0D);
                event.setCancelled(true);
                Profile profilDeath = ProfileManager.getProfile(player);
                player.setHealth(player.getHealth() - 8);
                profilDeath.setLastDamager(attacker);
            } else {
                attacker.sendMessage(ColorText.translate("&cBackstab failed!"));
            }
        }
    }
    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Profile profile = ProfileManager.getProfile(player);

        Player killer = player.getKiller();

        if (player.getKiller() == null) {
            killer = profile.getLastDamager();
        }

        if(killer == null){
            return;
        }
        if (!isEquped(killer)) {
            return;
        }

        List<ItemStack> items = Arrays.asList(killer.getInventory().getContents().clone());

        if (items.stream().noneMatch(itemStack -> itemStack != null && itemStack.getType() == Material.GOLD_SWORD)) {
            killer.getInventory().addItem(new ItemMaker(Material.GOLD_SWORD).create());
        }
    }

}