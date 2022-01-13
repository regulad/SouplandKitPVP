package us.soupland.kitpvp.kits.types;

import us.soupland.kitpvp.KitPvP;
import us.soupland.kitpvp.utilities.cooldown.Cooldown;
import us.soupland.kitpvp.utilities.chat.ColorText;
import us.soupland.kitpvp.utilities.item.ItemMaker;
import us.soupland.kitpvp.utilities.task.TaskUtil;
import us.soupland.kitpvp.utilities.time.TimeUtils;
import us.soupland.kitpvp.enums.PlayerState;
import us.soupland.kitpvp.enums.Refill;
import us.soupland.kitpvp.kits.Kit;
import us.soupland.kitpvp.profile.Profile;
import us.soupland.kitpvp.profile.ProfileManager;
import us.soupland.kitpvp.utilities.player.DurationFormatter;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class AssassinKit extends Kit {

    public AssassinKit() {
        super("Assassin", "&aAssassin", "45s");
        new Cooldown(getName(), TimeUtils.parse(this.getCooldown()), getDisplayName(), "&eYou may now use your &b&lPowerful Sword&e.");
    }

    @Override
    public void execute(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Profile profile = ProfileManager.getProfile(player);

        if (player.getItemInHand() == null || player.getItemInHand().getType() != Material.DIAMOND_SWORD || !event.getAction().name().startsWith("RIGHT"))
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
        cooldown.setCooldown(player, true);

        player.getItemInHand().addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 5);
        player.updateInventory();

        TaskUtil.runTaskLater(() -> {
            if (player.isOnline() && profile.getCurrentKit() instanceof AssassinKit) {
                if (player.getItemInHand() != null && player.getItemInHand().getType() == Material.DIAMOND_SWORD) {
                    player.sendMessage(ColorText.translate("&aYour sword has lost its power."));
                    player.getItemInHand().removeEnchantment(Enchantment.DAMAGE_ALL);
                    player.getItemInHand().addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1);
                    player.updateInventory();
                } else {
                    for (ItemStack itemStack : player.getInventory()) {
                        if (itemStack == null) {
                            continue;
                        }
                        if (itemStack.getType().name().endsWith("SWORD")) {
                            player.sendMessage(ColorText.translate("&aYour sword has lost its power."));
                            itemStack.removeEnchantment(Enchantment.DAMAGE_ALL);
                            itemStack.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1);
                            player.updateInventory();
                            break;
                        }
                    }
                }
            }
        }, 10 * 20L);

    }

    @Override
    public void onLoad(Player player) {
        PlayerInventory inventory = player.getInventory();
        Profile profile = ProfileManager.getProfile(player);

        inventory.setHelmet(new ItemMaker(Material.IRON_HELMET).setUnbreakable(true).create());
        inventory.setChestplate(new ItemMaker(Material.GOLD_CHESTPLATE).setUnbreakable(true).create());
        inventory.setLeggings(new ItemMaker(Material.IRON_LEGGINGS).setUnbreakable(true).create());
        inventory.setBoots(new ItemMaker(Material.IRON_BOOTS).setUnbreakable(true).create());

        inventory.setItem(0, new ItemMaker(Material.DIAMOND_SWORD).setEnchant(Enchantment.DAMAGE_ALL, 1).addLore(profile.getTheme().getPrimaryColor() + "Right-Click to use your Powerful Sword").setUnbreakable(true).create());

        for (int i = 0; i < 36; i++) {
            inventory.addItem(new ItemMaker((profile.getRefill() == Refill.POTION && !profile.getPlayerState().name().contains("PRACTICE") && profile.getPlayerState() != PlayerState.INGAME ? Material.POTION : Material.MUSHROOM_SOUP)).setDurability(profile.getRefill() == Refill.POTION && !profile.getPlayerState().name().contains("PRACTICE") && profile.getPlayerState() != PlayerState.INGAME ? 16421 : 0).setDisplayname(profile.getRefill() == Refill.POTION && !profile.getPlayerState().name().contains("PRACTICE") && profile.getPlayerState() != PlayerState.INGAME ? "&dPotion" : "&6Soup").addLore(profile.getRefill() == Refill.POTION && !profile.getPlayerState().name().contains("PRACTICE") && profile.getPlayerState() != PlayerState.INGAME ? "" : "&7Heals 3.5 Health").create());
        }

        setPlayerInventory(inventory.getArmorContents());
        setInventory(inventory.getContents());

        player.updateInventory();

        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200000, 1));
        setEffects(new ArrayList<>(player.getActivePotionEffects()));
    }

    @Override
    public ItemStack getItem() {
        return new ItemMaker(Material.IRON_SWORD).setDisplayname(getDisplayName()).addLore(getDescription()).create();
    }

    @Override
    public String getPermissions() {
        return "soupland.kit." + getName().toLowerCase();
    }

    @Override
    public int getCredits() {
        return 8100;
    }

    @Override
    public List<String> getDescription() {
        List<String> list = new ArrayList<>();
        list.add("");
        list.add("&7Get a sword that deals a massive amount");
        list.add("&7of damage, but only have 1 use.");
        list.add("");
        return getConfig().getStringList("Kits." + this.getName() + ".description");
    }

}