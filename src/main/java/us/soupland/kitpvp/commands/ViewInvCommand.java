package us.soupland.kitpvp.commands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import us.soupland.kitpvp.practice.match.MatchSnapshot;
import us.soupland.kitpvp.profile.ProfileManager;
import us.soupland.kitpvp.utilities.KitPvPUtils;
import us.soupland.kitpvp.utilities.chat.ColorText;
import us.soupland.kitpvp.utilities.command.KitPvPCommand;
import us.soupland.kitpvp.utilities.inventory.InventoryMaker;
import us.soupland.kitpvp.utilities.item.ItemMaker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class ViewInvCommand extends KitPvPCommand {

    public ViewInvCommand() {
        super("viewinv");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(KitPvPUtils.ONLY_PLAYERS);
            return false;
        }
        Player player = (Player) sender;
        if (!ProfileManager.getProfile(player).getPlayerState().name().contains("PRACTICE")) {
            return false;
        }
        if (args.length < 1) {
            player.sendMessage(ColorText.translate("&cUsage: /" + label + " <playerUUID>"));
        } else {
            MatchSnapshot snapshot;

            try {
                snapshot = MatchSnapshot.getByUuid(UUID.fromString(args[0]));
            } catch (Exception ignored) {
                snapshot = MatchSnapshot.getByName(args[0]);
            }

            if (snapshot == null || snapshot.getMatchPlayer().getPlayer() == null) {
                player.sendMessage(ColorText.translate("&cCouldn't find an inventory for that ID."));
                return false;
            }

            InventoryMaker inventoryMaker = new InventoryMaker("&a&l" + snapshot.getMatchPlayer().getPlayer().getName() + "&7's Inventory", 6);

            ItemStack[] fixedContents = fixInventoryOrder(snapshot.getContents());

            for (int i = 0; i < fixedContents.length; i++) {
                ItemStack stack = fixedContents[i];

                if (stack == null || stack.getType() == Material.AIR) {
                    continue;
                }
                inventoryMaker.setItem(i, new InventoryMaker.ClickableItem() {
                    @Override
                    public void onClick(InventoryClickEvent inventoryClickEvent) {

                    }

                    @Override
                    public ItemStack getItemStack() {
                        return stack;
                    }
                });
            }

            for (int i = 0; i < snapshot.getArmor().length; i++) {
                ItemStack stack = snapshot.getArmor()[i];

                if (stack == null || stack.getType() == Material.AIR) {
                    continue;
                }

                inventoryMaker.setItem(39 - i, new InventoryMaker.ClickableItem() {
                    @Override
                    public void onClick(InventoryClickEvent inventoryClickEvent) {

                    }

                    @Override
                    public ItemStack getItemStack() {
                        return stack;
                    }
                });
            }

            int position = 45;

            MatchSnapshot finalSnapshot = snapshot;
            inventoryMaker.setItem(position++, new InventoryMaker.ClickableItem() {
                @Override
                public void onClick(InventoryClickEvent inventoryClickEvent) {

                }

                @Override
                public ItemStack getItemStack() {
                    return new ItemMaker(Material.MELON).setDisplayname("&a&lHealth: &b" + finalSnapshot.getHeal() + "/10 &4&l\u2764").setAmount((finalSnapshot.getHeal() == 0 ? 1 : finalSnapshot.getHeal())).create();
                }
            });

            inventoryMaker.setItem(position++, new InventoryMaker.ClickableItem() {
                @Override
                public void onClick(InventoryClickEvent inventoryClickEvent) {

                }

                @Override
                public ItemStack getItemStack() {
                    return new ItemMaker(Material.COOKED_BEEF).setDisplayname("&a&lHunger: &b" + finalSnapshot.getHunger() + "/20").setAmount((finalSnapshot.getHunger() == 0 ? 1 : finalSnapshot.getHunger())).create();
                }
            });

            inventoryMaker.setItem(position++, new InventoryMaker.ClickableItem() {
                @Override
                public void onClick(InventoryClickEvent inventoryClickEvent) {

                }

                @Override
                public ItemStack getItemStack() {
                    Collection<PotionEffect> effects = finalSnapshot.getPotionEffects();
                    List<String> lore = new ArrayList<>();

                    if (effects.isEmpty()) {
                        lore.add("&cNo potion effects");
                    } else {
                        effects.forEach(potionEffect -> {
                            lore.add("&b" + getName(potionEffect.getType()) + " &7" + getMMSS((potionEffect.getDuration() / 20) * 1000));
                        });
                    }

                    return new ItemMaker(Material.POTION).setDisplayname("&a&lPotion Effects").addLore(lore).create();
                }
            });

            if (snapshot.shouldDisplayReaminingHeal()) {
                inventoryMaker.setItem(position++, new InventoryMaker.ClickableItem() {
                    @Override
                    public void onClick(InventoryClickEvent inventoryClickEvent) {

                    }

                    @Override
                    public ItemStack getItemStack() {
                        int amount = finalSnapshot.getRemainingHeal();
                        return new ItemMaker(Material.POTION).setDisplayname("&a&l" + (finalSnapshot.isPotion() ? "Potions" : "Soups")).addLore("&b" + finalSnapshot.getMatchPlayer().getPlayer().getName() + " &7has &b" + amount + ' ' + (finalSnapshot.isPotion() ? "potion" : "soup") + (amount == 1 ? "" : "s") + " left.").setDurability(16421).create();
                    }
                });
            }

            inventoryMaker.setItem(position, new InventoryMaker.ClickableItem() {
                @Override
                public void onClick(InventoryClickEvent inventoryClickEvent) {

                }

                @Override
                public ItemStack getItemStack() {
                    List<String> lore = new ArrayList<>();
                    lore.add("&cHits: &f" + finalSnapshot.getMatchPlayer().getHits());
                    lore.add("&cLongest Combo: &f" + finalSnapshot.getMatchPlayer().getLongestCombo());
                    if (finalSnapshot.isPotion()) {
                        lore.add("&cPotions Thrown: &f" + finalSnapshot.getMatchPlayer().getPotionsThrown());
                        lore.add("&cPotions Missed: &f" + finalSnapshot.getMatchPlayer().getPotionsMissed());
                        lore.add("&cPotions Accuracy: &f" + finalSnapshot.getMatchPlayer().getPotionAccuracy());
                    }
                    return new ItemMaker(Material.PAPER).setDisplayname("&a&lStatistics").addLore(lore).create();
                }
            });

            if (snapshot.getSwitchTo() != null && snapshot.getSwitchTo().getPlayer() != null) {
                inventoryMaker.setItem(53, new InventoryMaker.ClickableItem() {
                    @Override
                    public void onClick(InventoryClickEvent inventoryClickEvent) {
                        player.performCommand("viewinv " + finalSnapshot.getSwitchTo().getPlayer().getName());
                    }

                    @Override
                    public ItemStack getItemStack() {
                        return new ItemMaker(Material.NAME_TAG).setDisplayname("&c&lOpponent's Inventory").addLore("&7Switch to &a" + finalSnapshot.getMatchPlayer().getPlayer().getName() + "&7's Inventory").create();
                    }
                });
            }

            player.sendMessage(ColorText.translate("&7You're viewing &a" + snapshot.getMatchPlayer().getPlayer().getName() + "&7's inventory."));

            player.openInventory(inventoryMaker.getCurrentPage());
        }
        return true;
    }

    private ItemStack[] fixInventoryOrder(ItemStack[] source) {
        ItemStack[] fixed = new ItemStack[36];

        System.arraycopy(source, 0, fixed, 27, 9);
        System.arraycopy(source, 9, fixed, 0, 27);

        return fixed;
    }

    private String getName(PotionEffectType potionEffectType) {
        switch (potionEffectType.getId()) {
            case 1: {
                return "Speed";
            }
            case 2: {
                return "Slowness";
            }
            case 3: {
                return "Haste";
            }
            case 4: {
                return "Slow Digging";
            }
            case 5: {
                return "Strength";
            }
            case 6: {
                return "Heal";
            }
            case 7: {
                return "Harm";
            }
            case 8: {
                return "Jump";
            }
            case 9: {
                return "Confusion";
            }
            case 10: {
                return "Regeneration";
            }
            case 11: {
                return "Resistance";
            }
            case 12: {
                return "Fire Resistance";
            }
            case 13: {
                return "Water Breathing";
            }
            case 14: {
                return "Invisibility";
            }
            case 15: {
                return "Blindness";
            }
            case 16: {
                return "Night Vision";
            }
            case 17: {
                return "Hunger";
            }
            case 18: {
                return "Weakness";
            }
            case 19: {
                return "Poison";
            }
            case 20: {
                return "Wither";
            }
            case 21: {
                return "Halth Boost";
            }
            case 22: {
                return "Absortion";
            }
            case 23: {
                return "Saturation";
            }
            default: {
                return "Unknown";
            }
        }
    }

    private String getMMSS(final int seconds) {
        int millis = seconds * 1000, sec = millis / 1000 % 60, min = millis / 60000 % 60, hr = millis / 3600000 % 24;
        return ((hr > 0) ? String.format("%02d", hr) : "") + String.format("%02d:%02d", min, sec);
    }
}