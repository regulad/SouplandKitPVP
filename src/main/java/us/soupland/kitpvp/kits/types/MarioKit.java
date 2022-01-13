package us.soupland.kitpvp.kits.types;

import us.soupland.kitpvp.KitPvP;
import us.soupland.kitpvp.utilities.cooldown.Cooldown;
import us.soupland.kitpvp.utilities.chat.ColorText;
import us.soupland.kitpvp.utilities.item.ItemMaker;
import us.soupland.kitpvp.utilities.time.TimeUtils;
import us.soupland.kitpvp.enums.PlayerState;
import us.soupland.kitpvp.enums.Refill;
import us.soupland.kitpvp.kits.Kit;
import us.soupland.kitpvp.profile.Profile;
import us.soupland.kitpvp.profile.ProfileManager;
import us.soupland.kitpvp.utilities.player.DurationFormatter;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Fireball;
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

public class MarioKit extends Kit {

    public MarioKit() {
        super("Mario", "&cMario", "15s");
        new Cooldown(getName(), TimeUtils.parse(getCooldown()), getDisplayName(), null);
    }

    @Override
    public void execute(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Profile profile = ProfileManager.getProfile(player);

        if (player.getItemInHand() == null || player.getItemInHand().getType() != Material.FIREBALL || !event.getAction().name().startsWith("RIGHT"))
            return;
        if (profile.getPlayerState() == PlayerState.SPAWN) {
            player.sendMessage(ColorText.translate("&cYou can't do this in Spawn."));
            return;
        }

        Cooldown cooldown = KitPvP.getCooldown(getName());
        if (cooldown.isOnCooldown(player)) {
            player.sendMessage(ColorText.translate("&cYou are on cooldown for another &e" + DurationFormatter.getRemaining(cooldown.getDuration(player), true) + "&c."));
            return;
        }
        cooldown.setCooldown(player);

        Fireball fireball = player.launchProjectile(Fireball.class);
        fireball.setVelocity(player.getEyeLocation().getDirection().multiply(3).normalize());
        player.getInventory().remove(player.getItemInHand());
    }

    @Override
    public void onLoad(Player player) {
        PlayerInventory inventory = player.getInventory();
        Profile profile = ProfileManager.getProfile(player);

        inventory.setHelmet(new ItemMaker(Material.LEATHER_HELMET).setColor(Color.RED).setUnbreakable(true).create());
        inventory.setChestplate(new ItemMaker(Material.DIAMOND_CHESTPLATE).setUnbreakable(true).create());
        inventory.setLeggings(new ItemMaker(Material.IRON_LEGGINGS).setUnbreakable(true).create());
        inventory.setBoots(new ItemMaker(Material.IRON_BOOTS).setUnbreakable(true).create());

        inventory.setItem(0, new ItemMaker(Material.DIAMOND_SWORD).setEnchant(Enchantment.DAMAGE_ALL, 1).setUnbreakable(true).create());
        inventory.setItem(1, new ItemMaker(Material.FIREBALL).setDisplayname("&cFireball").create());

        for (int i = 0; i < 36; i++) {
            inventory.addItem(new ItemMaker((profile.getRefill() == Refill.POTION && !profile.getPlayerState().name().contains("PRACTICE") && profile.getPlayerState() != PlayerState.INGAME ? Material.POTION : Material.MUSHROOM_SOUP)).setDurability(profile.getRefill() == Refill.POTION && !profile.getPlayerState().name().contains("PRACTICE") && profile.getPlayerState() != PlayerState.INGAME ? 16421 : 0).setDisplayname(profile.getRefill() == Refill.POTION && !profile.getPlayerState().name().contains("PRACTICE") && profile.getPlayerState() != PlayerState.INGAME ? "&dPotion" : "&6Soup").addLore(profile.getRefill() == Refill.POTION && !profile.getPlayerState().name().contains("PRACTICE") && profile.getPlayerState() != PlayerState.INGAME ? "" : "&7Heals 3.5 Health").create());
        }

        setPlayerInventory(inventory.getArmorContents());
        setInventory(inventory.getContents());

        player.updateInventory();

        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200000, 0));
        player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 200000, 0));
        setEffects(new ArrayList<>(player.getActivePotionEffects()));
    }

    @Override
    public ItemStack getItem() {
        return new ItemMaker(Material.FIREBALL).setDisplayname(getDisplayName()).addLore(getDescription()).create();
    }

    @Override
    public String getPermissions() {
        return "soupland.kit." + getName().toLowerCase();
    }

    @Override
    public int getCredits() {
        return 5100;
    }

    @Override
    public List<String> getDescription() {
        List<String> list = new ArrayList<>();
        list.add("");
        list.add("&7Shoot your enemies with fireballs");
        list.add("&7that refill with every kill you get.");
        list.add("");
        return getConfig().getStringList("Kits." + this.getName() + ".description");
    }

    @EventHandler
    public void onDamageByFireBall(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Fireball) {
            event.setDamage(event.getDamage() * 4);
        }
    }

    /*@EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
            Player player = event.getEntity().getKiller();
            Profile profile = ProfileManager.getProfile(player);
            if (player.getKiller() == null) {
                player = profile.getLastDamager();
            }

            if(player == null){
                return;
            }
            if (isEquped(player)) {

                List<ItemStack> items = Arrays.asList(player.getInventory().getContents());

                if (items.stream().noneMatch(itemStack -> itemStack.getType() == Material.FIREBALL)) {
                    player.getInventory().addItem( new ItemMaker(Material.FIREBALL).setDisplayname("&cFireball").create());
                }

                if (profile.isFrozenToUseAbility()) {
                    player.sendMessage(ColorText.translate("&cYou are currently jammed, so you can not use your ability."));
                    return;
                }

                PlayerInventory inventory = player.getInventory();
                for (int i = 0; i < 36; i++) {
                    inventory.addItem(new ItemMaker((profile.getRefill() == Refill.POTION && !profile.getPlayerState().name().contains("PRACTICE") && profile.getPlayerState() != PlayerState.INGAME ? Material.POTION : Material.MUSHROOM_SOUP)).setDurability(profile.getRefill() == Refill.POTION && !profile.getPlayerState().name().contains("PRACTICE") && profile.getPlayerState() != PlayerState.INGAME ? 16421 : 0).setDisplayname(profile.getRefill() == Refill.POTION && !profile.getPlayerState().name().contains("PRACTICE") && profile.getPlayerState() != PlayerState.INGAME ? "&dPotion" : "&6Soup").addLore(profile.getRefill() == Refill.POTION && !profile.getPlayerState().name().contains("PRACTICE") && profile.getPlayerState() != PlayerState.INGAME ? "" : "&7Heals 3.5 Health").create());
                }

                player.updateInventory();
                player.sendMessage(ColorText.translate("&eHey &c&l" + player.getName() + "&e, your inventory has been refilled with " + (profile.getRefill() == Refill.POTION && !profile.getPlayerState().name().contains("PRACTICE") && profile.getPlayerState() != PlayerState.INGAME ? "&dPotions" : "&6Soups") + "&e!"));
                player.playSound(player.getLocation(), Sound.ANVIL_USE, 1, 1);
            }
    }*/

    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Player killer = player.getKiller();
        Profile profile = ProfileManager.getProfile(player);

        if (player.getKiller() == null) {
            killer = profile.getLastDamager();
        }

        if(killer == null){
            return;
        }

        if (!isEquped(killer)) {
            return;
        }

        List<ItemStack> items = Arrays.asList(killer.getInventory().getContents());

        if (items.stream().noneMatch(itemStack -> itemStack != null && itemStack.getType() == Material.FIREBALL)) {
            killer.getInventory().addItem(new ItemMaker(Material.FIREBALL).setDisplayname("&cFireball").create());
        }

        for(ItemStack itemStack : killer.getInventory()){
            if(itemStack == null || itemStack.getType() == Material.AIR){
                killer.getInventory().addItem(new ItemMaker((profile.getRefill() == Refill.POTION && !profile.getPlayerState().name().contains("PRACTICE") && profile.getPlayerState() != PlayerState.INGAME ? Material.POTION : Material.MUSHROOM_SOUP)).setDurability(profile.getRefill() == Refill.POTION && !profile.getPlayerState().name().contains("PRACTICE") && profile.getPlayerState() != PlayerState.INGAME ? 16421 : 0).setDisplayname(profile.getRefill() == Refill.POTION && !profile.getPlayerState().name().contains("PRACTICE") && profile.getPlayerState() != PlayerState.INGAME ? "&dPotion" : "&6Soup").addLore(profile.getRefill() == Refill.POTION && !profile.getPlayerState().name().contains("PRACTICE") && profile.getPlayerState() != PlayerState.INGAME ? "" : "&7Heals 3.5 Health").create());
            }
        }

        killer.updateInventory();
        killer.sendMessage(ColorText.translate("&eHey &c&l" + killer.getName() + "&e, your inventory has been refilled with " + (profile.getRefill() == Refill.POTION && !profile.getPlayerState().name().contains("PRACTICE") && profile.getPlayerState() != PlayerState.INGAME ? "&dPotions" : "&6Soups") + "&e!"));
        killer.playSound(killer.getLocation(), Sound.ANVIL_USE, 1, 1);
    }

}