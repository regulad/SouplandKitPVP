package us.soupland.kitpvp.managers;

import lombok.Getter;
import us.soupland.kitpvp.enums.PlayerState;
import us.soupland.kitpvp.enums.Refill;
import us.soupland.kitpvp.profile.Profile;
import us.soupland.kitpvp.profile.ProfileManager;
import us.soupland.kitpvp.streak.KillStreak;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import us.soupland.kitpvp.utilities.KitPvPUtils;
import us.soupland.kitpvp.utilities.chat.ColorText;
import us.soupland.kitpvp.utilities.item.ItemMaker;

import java.util.ArrayList;
import java.util.List;

public class KillStreakManager {

    @Getter
    private static List<KillStreak> streaks = new ArrayList<>();

    public KillStreakManager() {
        streaks.add(new KillStreak(5, "&6Gapples") {
            @Override
            public boolean execute(Player player) {

                int amount = (player.hasPermission(KitPvPUtils.DONATOR_PERMISSION + ".pro") ? 1 : player.hasPermission(KitPvPUtils.DONATOR_PERMISSION + ".titan") ? 2 : player.hasPermission(KitPvPUtils.DONATOR_PERMISSION + ".legend") ? 3 : player.hasPermission(KitPvPUtils.DONATOR_PERMISSION + ".god") ? 4 : player.hasPermission(KitPvPUtils.DONATOR_PERMISSION + ".souper") ? 5 : player.hasPermission(KitPvPUtils.DONATOR_PERMISSION + ".ultimate") ? 6 : player.hasPermission(KitPvPUtils.DONATOR_PERMISSION + ".sponsor") ? 7 : 0);

                if (amount == 0) {
                    return false;
                }

                Profile profile = ProfileManager.getProfile(player);
                PlayerInventory inventory = player.getInventory();

                if (inventory.firstEmpty() < 1) {
                    inventory.remove((profile.getRefill() == Refill.SOUP ? Material.MUSHROOM_SOUP : Material.POTION));
                }

                inventory.addItem(new ItemMaker(Material.GOLDEN_APPLE).setAmount(amount).create());

                player.updateInventory();
                return true;
            }
        });
        streaks.add(new KillStreak(10, "&aEnderpearl") {
            @Override
            public boolean execute(Player player) {

                int amount = (player.hasPermission(KitPvPUtils.DONATOR_PERMISSION + ".pro") ? 1 : player.hasPermission(KitPvPUtils.DONATOR_PERMISSION + ".titan") ? 2 : player.hasPermission(KitPvPUtils.DONATOR_PERMISSION + ".legend") ? 3 : player.hasPermission(KitPvPUtils.DONATOR_PERMISSION + ".god") ? 4 : player.hasPermission(KitPvPUtils.DONATOR_PERMISSION + ".souper") ? 5 : player.hasPermission(KitPvPUtils.DONATOR_PERMISSION + ".ultimate") ? 6 : player.hasPermission(KitPvPUtils.DONATOR_PERMISSION + ".sponsor") ? 7 : 0);

                if (amount == 0) {
                    return false;
                }

                Profile profile = ProfileManager.getProfile(player);
                PlayerInventory inventory = player.getInventory();

                if (!inventory.contains(Material.ENDER_PEARL)) {
                    inventory.remove((profile.getRefill() == Refill.SOUP ? Material.MUSHROOM_SOUP : Material.POTION));
                }

                inventory.addItem(new ItemMaker(Material.ENDER_PEARL).setAmount(amount).create());

                player.updateInventory();
                return true;
            }
        });
        streaks.add(new KillStreak(15, "&cRefill") {
            @Override
            public boolean execute(Player player) {
                if (player.hasPermission(KitPvPUtils.DONATOR_PERMISSION)) {
                    Profile profile = ProfileManager.getProfile(player);
                    PlayerInventory inventory = player.getInventory();

                    for (int i = 0; i < 36; i++) {
                        inventory.addItem(new ItemMaker((profile.getRefill() == Refill.POTION && !profile.getPlayerState().name().contains("PRACTICE") && profile.getPlayerState() != PlayerState.INGAME ? Material.POTION : Material.MUSHROOM_SOUP)).setDurability(profile.getRefill() == Refill.POTION && !profile.getPlayerState().name().contains("PRACTICE") && profile.getPlayerState() != PlayerState.INGAME ? 16421 : 0).setDisplayname(profile.getRefill() == Refill.POTION && !profile.getPlayerState().name().contains("PRACTICE") && profile.getPlayerState() != PlayerState.INGAME ? "&dPotion" : "&6Soup").addLore(profile.getRefill() == Refill.POTION && !profile.getPlayerState().name().contains("PRACTICE") && profile.getPlayerState() != PlayerState.INGAME ? "" : "&7Heals 3.5 Health").create());
                    }

                    player.updateInventory();
                    return false;
                }

                return false;
            }
        });
        streaks.add(new KillStreak(20, "&8Invisibility") {
            @Override
            public boolean execute(Player player) {

                int amount = (player.hasPermission(KitPvPUtils.DONATOR_PERMISSION + ".pro") ? 5 : player.hasPermission(KitPvPUtils.DONATOR_PERMISSION + ".titan") ? 10 : player.hasPermission(KitPvPUtils.DONATOR_PERMISSION + ".legend") ? 15 : player.hasPermission(KitPvPUtils.DONATOR_PERMISSION + ".god") ? 20 : player.hasPermission(KitPvPUtils.DONATOR_PERMISSION + ".souper") ? 25 : player.hasPermission(KitPvPUtils.DONATOR_PERMISSION + ".ultimate") ? 30 : player.hasPermission(KitPvPUtils.DONATOR_PERMISSION + ".sponsor") ? 35 : 0);

                if (amount == 0) {
                    return false;
                }
                Profile profile = ProfileManager.getProfile(player);

                amount++;

                player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, amount * 20, 1));
                return true;
            }
        });
        streaks.add(new KillStreak(20, "&4Strength") {
            @Override
            public boolean execute(Player player) {

                int amount = (player.hasPermission(KitPvPUtils.DONATOR_PERMISSION + ".pro") ? 5 : player.hasPermission(KitPvPUtils.DONATOR_PERMISSION + ".titan") ? 10 : player.hasPermission(KitPvPUtils.DONATOR_PERMISSION + ".legend") ? 15 : player.hasPermission(KitPvPUtils.DONATOR_PERMISSION + ".god") ? 20 : player.hasPermission(KitPvPUtils.DONATOR_PERMISSION + ".souper") ? 25 : player.hasPermission(KitPvPUtils.DONATOR_PERMISSION + ".ultimate") ? 30 : player.hasPermission(KitPvPUtils.DONATOR_PERMISSION + ".sponsor") ? 35 : 0);

                if (amount == 0) {
                    return false;
                }
                amount++;

                player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, amount * 20, 0));
                return true;
            }
        });
        streaks.add(new KillStreak(30, "&dRegeneration") {
            @Override
            public boolean execute(Player player) {
                int amount = (player.hasPermission(KitPvPUtils.DONATOR_PERMISSION + ".pro") ? 5 : player.hasPermission(KitPvPUtils.DONATOR_PERMISSION + ".titan") ? 10 : player.hasPermission(KitPvPUtils.DONATOR_PERMISSION + ".legend") ? 15 : player.hasPermission(KitPvPUtils.DONATOR_PERMISSION + ".god") ? 20 : player.hasPermission(KitPvPUtils.DONATOR_PERMISSION + ".souper") ? 25 : player.hasPermission(KitPvPUtils.DONATOR_PERMISSION + ".ultimate") ? 30 : player.hasPermission(KitPvPUtils.DONATOR_PERMISSION + ".sponsor") ? 35 : 0);

                if (amount == 0) {
                    return false;
                }

                amount++;

                player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, amount * 20, 0));
                return false;
            }
        });
    }
}