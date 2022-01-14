package us.soupland.kitpvp.commands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import us.soupland.kitpvp.enums.PlayerStat;
import us.soupland.kitpvp.profile.Profile;
import us.soupland.kitpvp.profile.ProfileManager;
import us.soupland.kitpvp.utilities.KitPvPUtils;
import us.soupland.kitpvp.utilities.chat.ColorText;
import us.soupland.kitpvp.utilities.command.KitPvPCommand;
import us.soupland.kitpvp.utilities.inventory.InventoryMaker;
import us.soupland.kitpvp.utilities.item.ItemMaker;

public class MenuCommand extends KitPvPCommand {

    public MenuCommand() {
        super("menu", null, "settings", "preferences", "prefs", "playermenu");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(KitPvPUtils.ONLY_PLAYERS);
            return false;
        }
        Player player = (Player) sender;
        Profile profile = ProfileManager.getProfile(player);
        InventoryMaker inventoryMaker = new InventoryMaker("&4&lPlayer Menu", 4);

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

        /*int[] ints = new int[]{9, 17, 18, 26};
        for (int i : ints) {
            inventoryMaker.setItem(i, new InventoryMaker.ClickableItem() {
                @Override
                public void onClick(InventoryClickEvent inventoryClickEvent) {

                }

                @Override
                public ItemStack getItemStack() {
                    return new ItemMaker(Material.STAINED_GLASS_PANE).setDisplayname(" ").setDurability(7).create();
                }
            });
        }*/

        for (int i = 27; i < 36; i++) {
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

        inventoryMaker.setItem(4, new InventoryMaker.ClickableItem() {
            @Override
            public void onClick(InventoryClickEvent inventoryClickEvent) {

            }

            @Override
            public ItemStack getItemStack() {
                return new ItemMaker(Material.ENDER_CHEST).setDisplayname("&4&lPlayer Menu").addLore("", "&7Welcome to the player menu!", "", "&7Using this menu, you can access", "&7things easily just by clicking!", "", "&c&lYour Information", " &7Current Rank: " + player.getName(), " &7Joins: &f" + profile.getStat(PlayerStat.JOINS), "").create();
            }
        });

        inventoryMaker.setItem(19, new InventoryMaker.ClickableItem() {
            @Override
            public void onClick(InventoryClickEvent inventoryClickEvent) {
                player.performCommand("personalsettings");
            }

            @Override
            public ItemStack getItemStack() {
                return new ItemMaker(Material.BED).setDisplayname("&c&lPersonal Settings").addLore("", "&7With our options, you can toggle", "&7things like your scoreboard,", "&7server timezone, and much more!", "", "&7You can quick access this menu", "&7using &c/options&7!", "", "&a&nClick here", "").create();
            }
        });

        inventoryMaker.setItem(14, new InventoryMaker.ClickableItem() {
            @Override
            public void onClick(InventoryClickEvent inventoryClickEvent) {
                player.closeInventory();
                player.performCommand("achievements");
            }

            @Override
            public ItemStack getItemStack() {
                return new ItemMaker(Material.PAPER).setDisplayname("&c&lMissions").addLore("", "&7You would see here the missions", "&7to get rewards, kits, etc.", "&7You can quick access this menu", "&7using &c/achievements&7!", "", "&a&nClick here").create();
            }
        });

        inventoryMaker.setItem(12, new InventoryMaker.ClickableItem() {
            @Override
            public void onClick(InventoryClickEvent inventoryClickEvent) {
                player.performCommand("teams");
            }

            @Override
            public ItemStack getItemStack() {
                return new ItemMaker(Material.DIAMOND_CHESTPLATE).setDisplayname("&c&lTeams").addLore("", "&7You can create our own team", "&7and manage as you want!", "", "&7You can quick access this menu", "&7using &c/teams&7!", "", "&a&nClick here", "").create();
            }
        });

        inventoryMaker.setItem(23, new InventoryMaker.ClickableItem() {
            @Override
            public void onClick(InventoryClickEvent inventoryClickEvent) {
                InventoryMaker maker = new InventoryMaker("&a&lConfirm Your Stats Reset", 1);

                maker.setItem(2, new InventoryMaker.ClickableItem() {
                    @Override
                    public void onClick(InventoryClickEvent inventoryClickEvent) {
                        player.closeInventory();
                        if (profile.getStat(PlayerStat.CREDITS) < 5000) {
                            player.sendMessage(ColorText.translateAmpersand("&cYou need &45,000 &cto purchase this item. You only have &4" + profile.getStat(PlayerStat.CREDITS) + "&c!"));
                            return;
                        }
                        profile.reset();
                    }

                    @Override
                    public ItemStack getItemStack() {
                        return new ItemMaker(Material.WOOL).setDisplayname("&a&lAccept").setDurability(13).create();
                    }
                });

                maker.setItem(6, new InventoryMaker.ClickableItem() {
                    @Override
                    public void onClick(InventoryClickEvent inventoryClickEvent) {
                        player.openInventory(inventoryMaker.getCurrentPage());
                    }

                    @Override
                    public ItemStack getItemStack() {
                        return new ItemMaker(Material.WOOL).setDisplayname("&c&lDecline").setDurability(14).create();
                    }
                });

                player.openInventory(maker.getCurrentPage());
            }

            @Override
            public ItemStack getItemStack() {
                return new ItemMaker(Material.TNT).setDisplayname("&c&lReset Stats").addLore("", "&cWhat is reset?", "&7Everything except your rank. This is not", "&7reversible. Do not purchase this if you", "&7are not 100% about resetting your stats.", "", "&6&lPrice: &a5K").create();
            }
        });

        inventoryMaker.setItem(25, new InventoryMaker.ClickableItem() {
            @Override
            public void onClick(InventoryClickEvent inventoryClickEvent) {
                player.closeInventory();
                player.performCommand("shop");
            }

            @Override
            public ItemStack getItemStack() {
                return new ItemMaker(Material.CHEST).setDisplayname("&c&lShop").addLore("", "&7Our brand new shop allows you to", "&7purchase kits, events, cosmetics,", "&7and more!", "", "&7You can quick access this menu", "&7using &c/shop&7!", "", "&a&nClick here", "").create();
            }
        });

        inventoryMaker.setItem(21, new InventoryMaker.ClickableItem() {
            @Override
            public void onClick(InventoryClickEvent inventoryClickEvent) {
                player.performCommand("bountyhunter");
            }

            @Override
            public ItemStack getItemStack() {
                return new ItemMaker(Material.BOW).setDisplayname("&c&lBounty Hunter").addLore("", "&7You can create your", "&7own Bounty hunter!", "", "&7You can quick access this menu", "&7using &c/bounty&7!", "", "&a&nClick here", "").create();
            }
        });

        player.openInventory(inventoryMaker.getCurrentPage());
        return true;
    }
}