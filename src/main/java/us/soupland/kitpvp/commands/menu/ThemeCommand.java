package us.soupland.kitpvp.commands.menu;

import us.soupland.kitpvp.enums.Theme;
import us.soupland.kitpvp.utilities.inventory.InventoryMaker;
import us.soupland.kitpvp.profile.Profile;
import us.soupland.kitpvp.profile.ProfileManager;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import us.soupland.kitpvp.utilities.KitPvPUtils;
import us.soupland.kitpvp.utilities.chat.ColorText;
import us.soupland.kitpvp.utilities.command.KitPvPCommand;
import us.soupland.kitpvp.utilities.item.ItemMaker;

public class ThemeCommand extends KitPvPCommand {

    public ThemeCommand() {
        super("theme", null, "themes");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(KitPvPUtils.ONLY_PLAYERS);
            return false;
        }
        Player player = (Player) sender;
        InventoryMaker inventoryMaker = new InventoryMaker("&9&lTheme", 4);

        for (int i = 0; i < 9; i++) {
            inventoryMaker.setItem(i, new InventoryMaker.ClickableItem() {
                @Override
                public void onClick(InventoryClickEvent inventoryClickEvent) {

                }

                @Override
                public ItemStack getItemStack() {
                    return new ItemMaker(Material.STAINED_GLASS_PANE).setDisplayname(" ").setDurability(7).create();
                }
            });
        }

        inventoryMaker.setItem(0, new InventoryMaker.ClickableItem() {
            @Override
            public void onClick(InventoryClickEvent inventoryClickEvent) {
                player.performCommand("cosmetics");
            }

            @Override
            public ItemStack getItemStack() {
                return new ItemMaker(Material.INK_SACK).setDurability(1).setDisplayname("&c&lGo Back").create();
            }
        });

        inventoryMaker.setItem(4, new InventoryMaker.ClickableItem() {
            @Override
            public void onClick(InventoryClickEvent inventoryClickEvent) {

            }

            @Override
            public ItemStack getItemStack() {
                return new ItemMaker(Material.BOOK_AND_QUILL).setDisplayname("&9&lTheme Menu").addLore("&7Welcome to the &fthemes &7menu!", "", "&7Themes allow you to select specific colors", "&7on your scoreboard!", "").create();
            }
        });

        Profile profile = ProfileManager.getProfile(player);

        int i = 9;
        for (Theme theme : Theme.values()) {
            inventoryMaker.setItem(i, new InventoryMaker.ClickableItem() {
                @Override
                public void onClick(InventoryClickEvent inventoryClickEvent) {
                    player.closeInventory();
                    if (profile.getTheme() == theme) {
                        player.sendMessage(ColorText.translate("&cYou're already using this theme!"));
                    } else if (theme.getPermission() != null && !player.hasPermission(theme.getPermission())) {
                        player.sendMessage("");
                        player.sendMessage(ColorText.translate("&eWe currently offer &c&l" + Theme.values().length + " &ethemes! All of these themes are available with the &7&lPRO &erank and above!"));
                        player.sendMessage(ColorText.translate("&7&oStore Link: &ahttps://store.soupland.us"));
                        player.sendMessage("");
                    } else {
                        profile.setTheme(theme);
                        player.sendMessage(ColorText.translate("&eTheme changed to " + theme.getPrimaryColor() + "&l" + theme.name()));
                    }
                }

                @Override
                public ItemStack getItemStack() {
                    return new ItemMaker(Material.PAPER).setDisplayname(theme.getPrimaryColor() + WordUtils.capitalize(theme.name())).addLore("", theme.getPrimaryColor() + "&lDon't like the current theme?", "&7This will modify the spawn scoreboard,", "&7event scoreboard, 1v1 scoreboard.", "", "&7Primary Color: " + theme.getPrimaryColor() + "This is the primary color!", "&7Secondary Color: " + theme.getSecondaryColor() + "This is the secondary color!", "", (theme == Theme.DEFAULT ? "&7This is the default theme." : theme.getPermission() == null || player.hasPermission(theme.getPermission()) ? "&7- " + theme.getPrimaryColor() + "Click to select this theme!" : profile.getTheme() == theme ? theme.getPrimaryColor() + "You're already using this theme!" : "&cYou do not have access to this theme!"), "").create();
                }
            });
            i++;
        }

        player.openInventory(inventoryMaker.getCurrentPage());
        return true;
    }
}
