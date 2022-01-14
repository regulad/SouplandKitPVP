package us.soupland.kitpvp.commands.menu;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import us.soupland.kitpvp.enums.KitMenuType;
import us.soupland.kitpvp.enums.ServerTime;
import us.soupland.kitpvp.profile.Profile;
import us.soupland.kitpvp.profile.ProfileManager;
import us.soupland.kitpvp.utilities.KitPvPUtils;
import us.soupland.kitpvp.utilities.chat.ColorText;
import us.soupland.kitpvp.utilities.command.KitPvPCommand;
import us.soupland.kitpvp.utilities.inventory.InventoryMaker;
import us.soupland.kitpvp.utilities.item.ItemMaker;

public class SettingsCommand extends KitPvPCommand {

    public SettingsCommand() {
        super("settings", null, "personalsettings", "options");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(KitPvPUtils.ONLY_PLAYERS);
            return false;
        }
        Player player = (Player) sender;
        Profile profile = ProfileManager.getProfile(player);
        InventoryMaker inventoryMaker = new InventoryMaker("&4&lPersonal Settings", 4);

        for (int i = 0; i < 36; i++) {
            inventoryMaker.setItem(i, new InventoryMaker.ClickableItem() {
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
                return new ItemMaker(Material.INK_SACK).setDurability(1).setDisplayname("&c&lGo Back").create();
            }
        });

        inventoryMaker.setItem(4, new InventoryMaker.ClickableItem() {
            @Override
            public void onClick(InventoryClickEvent inventoryClickEvent) {

            }

            @Override
            public ItemStack getItemStack() {
                return new ItemMaker(Material.REDSTONE_TORCH_ON).setDisplayname("&4&lPersonal Settings").addLore("", "&7Welcome to the settings menu!", "&7You can change your options below.", "").create();
            }
        });

        inventoryMaker.setItem(10, new InventoryMaker.ClickableItem() {
            @Override
            public void onClick(InventoryClickEvent inventoryClickEvent) {
                player.closeInventory();
                if (profile.getServerTime() == ServerTime.DAY) {
                    profile.setServerTime(ServerTime.NIGHT);
                    player.sendMessage(ColorText.translateAmpersand("&7You have changed your server time to &fNight&7."));
                    player.setPlayerTime(15000L, true);
                } else {
                    profile.setServerTime(ServerTime.DAY);
                    player.sendMessage(ColorText.translateAmpersand("&7You have changed your server time to &fDay&7."));
                    player.setPlayerTime(6000L, true);
                }
            }

            @Override
            public ItemStack getItemStack() {
                return new ItemMaker(Material.WATCH).setDisplayname("&cServer Time &7(" + WordUtils.capitalize(profile.getServerTime().name()) + ')').addLore("&7Click to change your server time to " + (profile.getServerTime() == ServerTime.DAY ? "&8Night" : "&aDay")).create();
            }
        });

        inventoryMaker.setItem(12, new InventoryMaker.ClickableItem() {
            @Override
            public void onClick(InventoryClickEvent inventoryClickEvent) {
                player.closeInventory();
                //   originalProfile.setMessagesEnabled(!originalProfile.isMessagesEnabled());
                //  player.sendMessage(ColorText.translate("&7You have toggled your sounds " + (originalProfile.isMessagesEnabled() ? "&aON" : "&cOFF") + "&7."));
            }

            @Override
            public ItemStack getItemStack() {
                return new ItemMaker(Material.BOOK).setDisplayname("&cPrivate Messages").create();
            }
        });

        inventoryMaker.setItem(14, new InventoryMaker.ClickableItem() {
            @Override
            public void onClick(InventoryClickEvent inventoryClickEvent) {
                player.closeInventory();
                profile.setScoreboardEnabled(!profile.isScoreboardEnabled());
                player.sendMessage(ColorText.translateAmpersand("&7You have toggled your scoreboard " + (profile.isScoreboardEnabled() ? "&aON" : "&cOFF") + "&7."));
            }

            @Override
            public ItemStack getItemStack() {
                return new ItemMaker(Material.PAPER).setDisplayname("&cScoreboard &7(" + (profile.isScoreboardEnabled() ? "ON" : "OFF") + ')').addLore((profile.isScoreboardEnabled() ? "&a\u25B6 ON" : "&7\u25B6 ON"), (profile.isScoreboardEnabled() ? "&7\u25B6 OFF" : "&a\u25B6 OFF")).create();
            }
        });

        inventoryMaker.setItem(22, new InventoryMaker.ClickableItem() {
            @Override
            public void onClick(InventoryClickEvent inventoryClickEvent) {
                player.closeInventory();
                if (profile.getKitMenuType() == KitMenuType.GUI) {
                    profile.setKitMenuType(KitMenuType.TEXT);
                    player.sendMessage(ColorText.translateAmpersand("&7You have toggled your kit list style to &cTEXT&7."));
                } else {
                    profile.setKitMenuType(KitMenuType.GUI);
                    player.sendMessage(ColorText.translateAmpersand("&7You have toggled your kit list style to &9GUI&7."));
                }
            }

            @Override
            public ItemStack getItemStack() {
                return new ItemMaker(Material.DIAMOND_SWORD).setDisplayname("&cKit List &7(" + profile.getKitMenuType().name() + ')').addLore((profile.getKitMenuType() == KitMenuType.GUI ? "&a" : "&7") + "\u25B6 GUI", (profile.getKitMenuType() == KitMenuType.TEXT ? "&a" : "&7") + "\u25B6 TEXT").create();
            }
        });

        inventoryMaker.setItem(16, new InventoryMaker.ClickableItem() {
            @Override
            public void onClick(InventoryClickEvent inventoryClickEvent) {
                player.closeInventory();
                //      player.sendMessage(ColorText.translate("&7You have toggled your sounds " + (originalProfile.isSoundsEnabled() ? "&aON" : "&cOFF") + "&7."));
            }

            @Override
            public ItemStack getItemStack() {
                return new ItemMaker(Material.RECORD_11).setDisplayname("&cSounds").create();
            }
        });

        inventoryMaker.setItem(20, new InventoryMaker.ClickableItem() {
            @Override
            public void onClick(InventoryClickEvent inventoryClickEvent) {
                if (player.hasPermission(KitPvPUtils.DONATOR_PERMISSION)) {
                    profile.setJoinAndQuitMessageEnabled(!profile.isJoinAndQuitMessageEnabled());
                    player.sendMessage(ColorText.translateAmpersand("&7You have " + (profile.isJoinAndQuitMessageEnabled() ? "&aenabled" : "&cdisabled") + "&7."));
                }
            }

            @Override
            public ItemStack getItemStack() {
                return new ItemMaker(Material.LEVER).setDisplayname("&cJoin/Quit Messages").addLore("&7If you have enabled this, when you enter/leave", "&7in the chat will appear \"" + player.getName() + " &7has dis/connected.\"", "", "&7Click here to toggle").create();
            }
        });

        /*inventoryMaker.setItem(20, new InventoryMaker.ClickableItem() {
            @Override
            public void onClick(InventoryClickEvent inventoryClickEvent) {
                player.performCommand("dr");
            }

            @Override
            public ItemStack getItemStack() {
                return new ItemMaker(Material.SKULL_ITEM).setDisplayname("&cDeath Reason").addLore("&7Death reasons are messages that pop up", "&7to the player when you kill them.", "", "&7Click here to enter the change menu!").create();
            }
        });*/

        inventoryMaker.setItem(24, new InventoryMaker.ClickableItem() {
            @Override
            public void onClick(InventoryClickEvent inventoryClickEvent) {
                player.closeInventory();
                // originalProfile.setTipsEnabled(!originalProfile.isTipsEnabled());
                //  player.sendMessage(ColorText.translate("&7You have toggled your Auto Messages " + (originalProfile.isTipsEnabled() ? "&aON" : "&cOFF") + "&7."));
            }

            @Override
            public ItemStack getItemStack() {
                return new ItemMaker(Material.NOTE_BLOCK).setDisplayname("&cAuto Messages").create();
            }
        });

        player.openInventory(inventoryMaker.getCurrentPage());
        return true;
    }
}