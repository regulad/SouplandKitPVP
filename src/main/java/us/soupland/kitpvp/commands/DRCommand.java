package us.soupland.kitpvp.commands;

import org.apache.commons.lang.WordUtils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import us.soupland.kitpvp.enums.DeathReason;
import us.soupland.kitpvp.profile.Profile;
import us.soupland.kitpvp.profile.ProfileManager;
import us.soupland.kitpvp.utilities.KitPvPUtils;
import us.soupland.kitpvp.utilities.chat.ColorText;
import us.soupland.kitpvp.utilities.command.KitPvPCommand;
import us.soupland.kitpvp.utilities.inventory.InventoryMaker;
import us.soupland.kitpvp.utilities.item.ItemMaker;

public class DRCommand extends KitPvPCommand {

    public DRCommand() {
        super("dr", null, "deathreason");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(KitPvPUtils.ONLY_PLAYERS);
            return false;
        }
        Player player = (Player) sender;
        InventoryMaker inventoryMaker = new InventoryMaker("&4&lCustom Death Message", 2);

        for (int a = 0; a < 9; a++) {
            inventoryMaker.setItem(a, new InventoryMaker.ClickableItem() {
                @Override
                public void onClick(InventoryClickEvent inventoryClickEvent) {

                }

                @Override
                public ItemStack getItemStack() {
                    return new ItemMaker(Material.STAINED_GLASS_PANE).setDurability(7).setDisplayname(" ").create();
                }
            });
        }

        inventoryMaker.setItem(0, new InventoryMaker.ClickableItem() {
            @Override
            public void onClick(InventoryClickEvent inventoryClickEvent) {
                player.performCommand("menu");
            }

            @Override
            public ItemStack getItemStack() {
                return new ItemMaker(Material.INK_SACK).setDisplayname("&c&lGo Back").setDurability(1).create();
            }
        });

        inventoryMaker.setItem(4, new InventoryMaker.ClickableItem() {
            @Override
            public void onClick(InventoryClickEvent inventoryClickEvent) {

            }

            @Override
            public ItemStack getItemStack() {
                return new ItemMaker(Material.SKULL_ITEM).setDisplayname("&4&lCustom Death Message").addLore("", "&7Death Reasons are displayed to a player", "&7when you kill them. The player will receive", "&7the selected message when they die!", "", "&7Death Reasons are only obtainable by", "&7purchasing a rank on our store.", " &c&l> &7shop.soupland.us", "").create();
            }
        });

        inventoryMaker.setItem(8, new InventoryMaker.ClickableItem() {
            @Override
            public void onClick(InventoryClickEvent inventoryClickEvent) {
                Profile profile = ProfileManager.getProfile(player);
                player.closeInventory();
                profile.setDeathReason(null);
                player.sendMessage(ColorText.translateAmpersand("&cYou disabled your custom death message. It will now be the default reason when you kill a player."));
            }

            @Override
            public ItemStack getItemStack() {
                return new ItemMaker(Material.SUGAR).setDisplayname("&4&lDisable").addLore("", "&7Click here to disable your custom death message!", "").create();
            }
        });

        int i = 9;
        for (DeathReason reason : DeathReason.values()) {
            inventoryMaker.setItem(i, new InventoryMaker.ClickableItem() {
                @Override
                public void onClick(InventoryClickEvent inventoryClickEvent) {
                    player.closeInventory();
                    if (!player.hasPermission("soupland.deathreason." + reason.name().replace("_", "").toLowerCase())) {
                        player.sendMessage(ColorText.translateAmpersand("&7&m" + StringUtils.repeat("-", 30)));
                        player.sendMessage(ColorText.translateAmpersand("&eYou do not have access to &b&l" + WordUtils.capitalize(reason.name().replace("_", " ")) + "&e!"));
                        player.sendMessage(ColorText.translateAmpersand("&ePurchase kits at our store, complete achievements & rankup, or buy kits at the /kitshop!"));
                        player.sendMessage(ColorText.translateAmpersand("&6Donate at &ahttps://donate.soupland.us"));
                        player.sendMessage(ColorText.translateAmpersand("&7&m" + StringUtils.repeat("-", 30)));
                        return;
                    }
                    Profile profile = ProfileManager.getProfile(player);
                    profile.setDeathReason(reason);
                    player.sendMessage(ColorText.translateAmpersand("&eYour death's reason message has been updated to &b&l" + reason.name().replace("_", " ").toLowerCase() + "&e!"));
                }

                @Override
                public ItemStack getItemStack() {
                    return reason.getItem();
                }
            });
            i++;
        }

        player.openInventory(inventoryMaker.getCurrentPage());
        return true;
    }
}