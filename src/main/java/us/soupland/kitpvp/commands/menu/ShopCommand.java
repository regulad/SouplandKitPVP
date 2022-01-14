package us.soupland.kitpvp.commands.menu;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import us.soupland.kitpvp.KitPvP;
import us.soupland.kitpvp.enums.PlayerStat;
import us.soupland.kitpvp.games.Game;
import us.soupland.kitpvp.kits.Kit;
import us.soupland.kitpvp.kits.KitHandler;
import us.soupland.kitpvp.profile.Profile;
import us.soupland.kitpvp.profile.ProfileManager;
import us.soupland.kitpvp.utilities.KitPvPUtils;
import us.soupland.kitpvp.utilities.Utils;
import us.soupland.kitpvp.utilities.WoolUtils;
import us.soupland.kitpvp.utilities.chat.ColorText;
import us.soupland.kitpvp.utilities.command.KitPvPCommand;
import us.soupland.kitpvp.utilities.inventory.InventoryMaker;
import us.soupland.kitpvp.utilities.item.ItemMaker;

import java.util.ArrayList;
import java.util.List;

public class ShopCommand extends KitPvPCommand {

    public ShopCommand() {
        super("shop");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(KitPvPUtils.ONLY_PLAYERS);
            return false;
        }
        Player player = (Player) sender;
        Profile profile = ProfileManager.getProfile(player);
        InventoryMaker inventoryMaker = new InventoryMaker("&4&lShop", 3);

        for (int i = 0; i < 26; i++) {
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

        inventoryMaker.setItem(2, new InventoryMaker.ClickableItem() {
            @Override
            public void onClick(InventoryClickEvent inventoryClickEvent) {

            }

            @Override
            public ItemStack getItemStack() {
                return new ItemMaker(Material.getMaterial(342))
                        .setDisplayname("&4&lShop")
                        .addLore("&7Welcome to the new shop!",
                                "",
                                "&7You can purchase anything from combat perks",
                                "&7to kits, cosmetics, and more!",
                                "", "&6&lYour Credits: &f" + profile.getStat(PlayerStat.CREDITS)).create();
            }
        });

        inventoryMaker.setItem(6, new InventoryMaker.ClickableItem() {
            @Override
            public void onClick(InventoryClickEvent inventoryClickEvent) {
                InventoryMaker maker = new InventoryMaker("&a&lConfirm Your Stats Reset", 1);

                maker.setItem(2, new InventoryMaker.ClickableItem() {
                    @Override
                    public void onClick(InventoryClickEvent inventoryClickEvent) {
                        player.closeInventory();
                        if (profile.getStat(PlayerStat.CREDITS) < 5000) {
                            player.sendMessage(ColorText.translate("&cYou need &45,000 &cto purchase this item. You only have &4" + profile.getStat(PlayerStat.CREDITS) + "&c!"));
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
                return new ItemMaker(Material.TNT).setDisplayname("&cReset Stats").addLore("", "&cWhat is reset?", "&7Everything except your rank. This is not", "&7reversible. Do not purchase this if you", "&7are not 100% about resetting your stats.", "", "&6&lPrice: &a5K").create();
            }
        });


        inventoryMaker.setItem(10, new InventoryMaker.ClickableItem() {
            @Override
            public void onClick(InventoryClickEvent inventoryClickEvent) {
                InventoryMaker maker = new InventoryMaker("&6&lEvents", 5);

                for (int i = 0; i < 9; i++) {
                    maker.setItem(i, new InventoryMaker.ClickableItem() {
                        @Override
                        public void onClick(InventoryClickEvent inventoryClickEvent) {

                        }

                        @Override
                        public ItemStack getItemStack() {
                            return new ItemMaker(Material.STAINED_GLASS_PANE).setDurability(7).setDisplayname(" ").create();
                        }
                    });
                }

                maker.setItem(0, new InventoryMaker.ClickableItem() {
                    @Override
                    public void onClick(InventoryClickEvent inventoryClickEvent) {
                        player.openInventory(inventoryMaker.getCurrentPage());
                    }

                    @Override
                    public ItemStack getItemStack() {
                        return new ItemMaker(Material.INK_SACK).setDurability(1).setDisplayname("&c&lGo Back").create();
                    }
                });

                maker.setItem(4, new InventoryMaker.ClickableItem() {
                    @Override
                    public void onClick(InventoryClickEvent inventoryClickEvent) {

                    }

                    @Override
                    public ItemStack getItemStack() {
                        return new ItemMaker(Material.NETHER_STAR).setDisplayname("&6&lEvents").addLore("&7Welcome to the &fevent &7shop!", "", "&7You can return to the Shop", "&7by using the red dye.").create();
                    }
                });

                int i = 9;
                for (Game game : KitPvP.getInstance().getGameHandler().getGames()) {
                    maker.setItem(i, new InventoryMaker.ClickableItem() {
                        @Override
                        public void onClick(InventoryClickEvent inventoryClickEvent) {
                            if (game.getPermission() == null || player.hasPermission(game.getPermission()) || profile.getGamesPurchased().contains(game)) {
                                player.sendMessage(ColorText.translate("&aYou already own this events! Want to use it? Type &f/game host"));
                                return;
                            }
                            player.closeInventory();
                            if (profile.getStat(PlayerStat.CREDITS) < game.getCredits()) {
                                player.sendMessage(ColorText.translate("&cYou don't have credits enough to acquire this &4Event&c."));
                                return;
                            }
                            profile.getGamesPurchased().add(game);
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "perms add " + player.getName() + ' ' + game.getPermission());
                            profile.setStat(PlayerStat.CREDITS, (profile.getStat(PlayerStat.CREDITS) - game.getCredits()));
                            player.sendMessage(ColorText.translate("&c" + game.getName() + " &ahas been successfully purchased."));
                        }

                        @Override
                        public ItemStack getItemStack() {
                            return new ItemMaker(game.getItem()).setDisplayname((game.getPermission() == null || player.hasPermission(game.getPermission()) ? "&7[Owned] &a" : "&c") + game.getName()).addLore("", "&7" + game.getDescription(), "", (game.getPermission() == null || player.hasPermission(game.getPermission()) ? "&aYou already own this Event." : "&6&lPrice: &a" + Utils.getFormat(game.getCredits()))).create();
                        }
                    });
                    i++;

                    player.openInventory(maker.getCurrentPage());
                }
            }

            @Override
            public ItemStack getItemStack() {
                return new ItemMaker(Material.LEASH).setDisplayname("&6&lEvents").addLore("", "&7Events are fun mini-games that", "&7are player hosted.", "", "&a&nClick here", "").create();
            }
        });

        inventoryMaker.setItem(11, new InventoryMaker.ClickableItem() {
            @Override
            public void onClick(InventoryClickEvent inventoryClickEvent) {
                InventoryMaker maker = new InventoryMaker("&4&lKits", 6);

                for (int i = 0; i < 9; i++) {
                    maker.setItem(i, new InventoryMaker.ClickableItem() {
                        @Override
                        public void onClick(InventoryClickEvent inventoryClickEvent) {

                        }

                        @Override
                        public ItemStack getItemStack() {
                            return new ItemMaker(Material.STAINED_GLASS_PANE).setDurability(7).setDisplayname(" ").create();
                        }
                    });
                }

                maker.setItem(0, new InventoryMaker.ClickableItem() {
                    @Override
                    public void onClick(InventoryClickEvent inventoryClickEvent) {
                        player.openInventory(inventoryMaker.getCurrentPage());
                    }

                    @Override
                    public ItemStack getItemStack() {
                        return new ItemMaker(Material.INK_SACK).setDurability(1).setDisplayname("&c&lGo Back").create();
                    }
                });

                maker.setItem(4, new InventoryMaker.ClickableItem() {
                    @Override
                    public void onClick(InventoryClickEvent inventoryClickEvent) {

                    }

                    @Override
                    public ItemStack getItemStack() {
                        return new ItemMaker(Material.DIAMOND_SWORD).setDisplayname("&4&lKits").addLore("&7Welcome to the &fkit &7shop!", "", "&7You can return to the Shop", "&7by using the red dye.").create();
                    }
                });

                List<Kit> kitsNoLoaded = new ArrayList<>();

                int i = 8;
                for (Kit kit : KitHandler.getKitList()) {
                    if (i++ >= 44) {
                        kitsNoLoaded.add(kit);
                        continue;
                    }
                    maker.setItem(i, new InventoryMaker.ClickableItem() {
                        @Override
                        public void onClick(InventoryClickEvent inventoryClickEvent) {
                            if (kit.getPermissions() == null || player.hasPermission(kit.getPermissions())) {
                                player.sendMessage(ColorText.translate("&aYou already own this kit! Want to use it? Type &f/kit " + kit.getName()));
                            } else {
                                player.closeInventory();
                                if (profile.getStat(PlayerStat.CREDITS) < kit.getCredits()) {
                                    player.sendMessage(ColorText.translate("&cYou don't have credits enough to acquire this &4Kit&c."));
                                } else {
                                    profile.setStat(PlayerStat.CREDITS, (profile.getStat(PlayerStat.CREDITS) - kit.getCredits()));
                                    player.sendMessage(ColorText.translate(kit.getDisplayName() + " &ahas been successfully purchased."));

                                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "perms add " + player.getName() + ' ' + kit.getPermissions());
                                }
                            }
                        }

                        @Override
                        public ItemStack getItemStack() {
                            List<String> lore = kit.getDescription();
                            if (kit.getPermissions() == null || player.hasPermission(kit.getPermissions())) {
                                lore.add("&aYou already own this Kit.");
                            } else {
                                lore.add("&6&lPrice: &a" + Utils.getFormat(kit.getCredits()));
                            }
                            return new ItemMaker(kit.getItem()).setDisplayname((kit.getPermissions() == null || player.hasPermission(kit.getPermissions()) ? "&7[Owned] " + kit.getDisplayName() : "&c" + kit.getName())).addLore(lore).create();
                        }
                    });
                }

                InventoryMaker secondPage = new InventoryMaker("&4&lKits", 6);

                if (!kitsNoLoaded.isEmpty()) {
                    maker.setItem(49, new InventoryMaker.ClickableItem() {
                        @Override
                        public void onClick(InventoryClickEvent inventoryClickEvent) {

                            for (int i = 0; i < 9; i++) {
                                secondPage.setItem(i, new InventoryMaker.ClickableItem() {
                                    @Override
                                    public void onClick(InventoryClickEvent inventoryClickEvent) {

                                    }

                                    @Override
                                    public ItemStack getItemStack() {
                                        return new ItemMaker(Material.STAINED_GLASS_PANE).setDurability(7).setDisplayname(" ").create();
                                    }
                                });
                            }

                            secondPage.setItem(49, new InventoryMaker.ClickableItem() {
                                @Override
                                public void onClick(InventoryClickEvent inventoryClickEvent) {
                                    player.openInventory(maker.getCurrentPage());
                                }

                                @Override
                                public ItemStack getItemStack() {
                                    return new ItemMaker(Material.getMaterial(101)).setDisplayname("&cPrevious Page").create();
                                }
                            });

                            secondPage.setItem(0, new InventoryMaker.ClickableItem() {
                                @Override
                                public void onClick(InventoryClickEvent inventoryClickEvent) {
                                    player.openInventory(inventoryMaker.getCurrentPage());
                                }

                                @Override
                                public ItemStack getItemStack() {
                                    return new ItemMaker(Material.INK_SACK).setDurability(1).setDisplayname("&c&lGo Back").create();
                                }
                            });

                            secondPage.setItem(4, new InventoryMaker.ClickableItem() {
                                @Override
                                public void onClick(InventoryClickEvent inventoryClickEvent) {

                                }

                                @Override
                                public ItemStack getItemStack() {
                                    return new ItemMaker(Material.DIAMOND_SWORD).setDisplayname("&4&lKits").addLore("&7Welcome to the &fkit &7shop!", "", "&7You can return to the Shop", "&7by using the red dye.").create();
                                }
                            });

                            int i = 9;
                            for (Kit kit : kitsNoLoaded) {
                                secondPage.setItem(i, new InventoryMaker.ClickableItem() {
                                    @Override
                                    public void onClick(InventoryClickEvent inventoryClickEvent) {
                                        if (kit.getPermissions() == null || player.hasPermission(kit.getPermissions())) {
                                            player.sendMessage(ColorText.translate("&aYou already own this kit! Want to use it? Type &f/kit " + kit.getName()));
                                        } else {
                                            player.closeInventory();
                                            if (profile.getStat(PlayerStat.CREDITS) < kit.getCredits()) {
                                                player.sendMessage(ColorText.translate("&cYou don't have credits enough to acquire this &4Kit&c."));
                                            } else {
                                                profile.setStat(PlayerStat.CREDITS, (profile.getStat(PlayerStat.CREDITS) - kit.getCredits()));
                                                player.sendMessage(ColorText.translate(kit.getDisplayName() + " &ahas been successfully purchased."));

                                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "perms add " + player.getName() + ' ' + kit.getPermissions());
                                            }
                                        }
                                    }

                                    @Override
                                    public ItemStack getItemStack() {
                                        List<String> lore = kit.getDescription();
                                        if (kit.getPermissions() == null || player.hasPermission(kit.getPermissions())) {
                                            lore.add("&aYou already own this Kit.");
                                        } else {
                                            lore.add("&6&lPrice: &a" + Utils.getFormat(kit.getCredits()));
                                        }
                                        return new ItemMaker(kit.getItem()).setDisplayname((kit.getPermissions() == null || player.hasPermission(kit.getPermissions()) ? "&7[Owned] " + kit.getDisplayName() : "&c" + kit.getName())).addLore(lore).create();
                                    }
                                });
                                i++;
                            }

                            player.openInventory(secondPage.getCurrentPage());

                        }

                        @Override
                        public ItemStack getItemStack() {
                            return new ItemMaker(Material.getMaterial(101)).setDisplayname("&cNext Page").create();
                        }
                    });
                }

                player.openInventory(maker.getCurrentPage());
            }

            @Override
            public ItemStack getItemStack() {
                return new ItemMaker(Material.DIAMOND_SWORD).setDisplayname("&4&lKits").addLore("", "&7Kits are the main feature on the", "&7server! Click here to buy some!", "", "&a&nClick here", "").create();
            }
        });

        inventoryMaker.setItem(12, new InventoryMaker.ClickableItem() {
            @Override
            public void onClick(InventoryClickEvent inventoryClickEvent) {
                InventoryMaker maker = new InventoryMaker("&a&lChat Colors", 3);

                for (int i = 0; i < 9; i++) {
                    maker.setItem(i, new InventoryMaker.ClickableItem() {
                        @Override
                        public void onClick(InventoryClickEvent inventoryClickEvent) {

                        }

                        @Override
                        public ItemStack getItemStack() {
                            return new ItemMaker(Material.STAINED_GLASS_PANE).setDurability(7).setDisplayname(" ").create();
                        }
                    });
                }

                maker.setItem(0, new InventoryMaker.ClickableItem() {
                    @Override
                    public void onClick(InventoryClickEvent inventoryClickEvent) {
                        player.openInventory(inventoryMaker.getCurrentPage());
                    }

                    @Override
                    public ItemStack getItemStack() {
                        return new ItemMaker(Material.INK_SACK).setDurability(1).setDisplayname("&c&lGo Back").create();
                    }
                });

                maker.setItem(4, new InventoryMaker.ClickableItem() {
                    @Override
                    public void onClick(InventoryClickEvent inventoryClickEvent) {

                    }

                    @Override
                    public ItemStack getItemStack() {
                        return new ItemMaker(Material.WOOL).setDurability(5).setDisplayname("&a&lChat Colors").addLore("&7Welcome to the &fcolor &7shop!", "", "&7You can return to the Shop", "&7by using the red dye.", "").create();
                    }
                });

                int i = 9;
                for (ChatColor color : ChatColor.values()) {
                    if (isInvalid(color)) {
                        continue;
                    }
                    maker.setItem(i, new InventoryMaker.ClickableItem() {
                        @Override
                        public void onClick(InventoryClickEvent inventoryClickEvent) {
                            if (player.hasPermission("chatcolor." + color.name().toLowerCase().replace("_", ""))) {
                                player.sendMessage(ColorText.translate("&aYou already own this Color."));
                            } else {
                                player.closeInventory();
                                if (profile.getStat(PlayerStat.CREDITS) < 2000) {
                                    player.sendMessage(ColorText.translate("&cYou don't have credits enough to acquire this &aColor&c."));
                                } else {
                                    profile.setStat(PlayerStat.CREDITS, (profile.getStat(PlayerStat.CREDITS) - 2000));
                                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "perms add " + player.getName() + " soupland.chatcolor." + color.name().toLowerCase().replace("_", ""));
                                    player.sendMessage(ColorText.translate(color + color.name().replace("_", " ") + " &ahas been successfully purchased."));
                                }
                            }
                        }

                        @Override
                        public ItemStack getItemStack() {
                            return new ItemMaker(WoolUtils.getWool(color)).setDisplayname((player.hasPermission("chatcolor." + color.name().toLowerCase().replace("_", "")) ? "&a[Purchased]" : "&c[Purchase]") + ' ' + color + color.name().replace("_", " ")).addLore("", "&7Colors are a cool chat cosmetic that", "&7modify your messages color!", "", (player.hasPermission("chatcolor." + color.name().toLowerCase().replace("_", "")) ? "&aYou already own this Color." : "&7Want to buy this color? &a&nClick here"), "").create();
                        }
                    });
                    i++;
                }

                player.openInventory(maker.getCurrentPage());
            }

            @Override
            public ItemStack getItemStack() {
                return new ItemMaker(Material.WOOL).setDurability(5).setDisplayname("&a&lChat Colors").addLore("", "&7Chat Colors allow you to change the", "&7color you type in chat with!", "", "&7Example: &7[&3Developer&7] &3Mauuuh&7: &aHi " + player.getName(), "", "&a&nClick here", "").create();
            }
        });

        player.openInventory(inventoryMaker.getCurrentPage());

        return true;
    }

    private boolean isInvalid(ChatColor color) {
        return color == ChatColor.UNDERLINE || color == ChatColor.BLACK || color == ChatColor.BOLD || color == ChatColor.RESET || color == ChatColor.DARK_BLUE || color == ChatColor.ITALIC || color == ChatColor.MAGIC || color == ChatColor.STRIKETHROUGH || color == ChatColor.DARK_GRAY;
    }
}