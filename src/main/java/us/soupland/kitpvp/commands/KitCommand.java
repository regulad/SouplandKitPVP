package us.soupland.kitpvp.commands;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import us.soupland.kitpvp.KitPvP;
import us.soupland.kitpvp.enums.KitMenuType;
import us.soupland.kitpvp.enums.PlayerStat;
import us.soupland.kitpvp.enums.PlayerState;
import us.soupland.kitpvp.kits.Kit;
import us.soupland.kitpvp.kits.KitHandler;
import us.soupland.kitpvp.profile.Profile;
import us.soupland.kitpvp.profile.ProfileManager;
import us.soupland.kitpvp.utilities.KitPvPUtils;
import us.soupland.kitpvp.utilities.Utils;
import us.soupland.kitpvp.utilities.chat.ColorText;
import us.soupland.kitpvp.utilities.command.KitPvPCommand;
import us.soupland.kitpvp.utilities.cooldown.Cooldown;
import us.soupland.kitpvp.utilities.inventory.InventoryMaker;
import us.soupland.kitpvp.utilities.item.ItemMaker;
import us.soupland.kitpvp.utilities.time.TimeUtils;

import java.util.ArrayList;
import java.util.List;

public class KitCommand extends KitPvPCommand {

    public KitCommand() {
        super("kits", null, "kit");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(KitPvPUtils.ONLY_PLAYERS);
            return false;
        }
        Player player = (Player) sender;
        Profile profile = ProfileManager.getProfile(player);
        if (profile.getPlayerState() != PlayerState.SPAWN) {
            player.sendMessage(ColorText.translateAmpersand("&cYou need spawn protection to choose a kit."));
            return false;
        }
        if (profile.getKitMenuType() == KitMenuType.GUI) {
            InventoryMaker inventoryMaker = new InventoryMaker("&4&lYour Kits", 5);

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

            for (int i = 36; i < 45; i++) {
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
                    int unlocked = 0;
                    for (Kit kit : KitHandler.getKitList()) {
                        if (kit.getPermissionNode() == null || player.hasPermission(kit.getPermissionNode())) {
                            unlocked++;
                        }
                    }
                    return new ItemMaker(Material.BOOK).setDisplayname("&4&lKits").addLore("", "&7You have kits from the Default rank!", "", "&7This menu &7only &7displays the kits you have!", "&7If you want more kits, buy on our &dstore", "&d&nshop.soupland.us &7(/buy)", "", "&7Unlocked Kits: &a" + unlocked + "&7/&f" + KitHandler.getKitList().size(), "").create();
                }
            });

            inventoryMaker.setItem(37, new InventoryMaker.ClickableItem() {
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

                    for (int i = 45; i < 54; i++) {
                        maker.setItem(i, new InventoryMaker.ClickableItem() {
                            @Override
                            public void onClick(InventoryClickEvent inventoryClickEvent) {

                            }

                            @Override
                            public ItemStack getItemStack() {
                                return new ItemMaker(Material.STAINED_GLASS_PANE).setDisplayname(" ").setDurability(7).create();
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
                            return new ItemMaker(Material.DIAMOND_SWORD).setDisplayname("&4&lKits").addLore("&7Welcome to the &fkit &7shop!", "", "&7You can return to your kits", "&7by using the red dye.").create();
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
                                if (kit.getPermissionNode() == null || player.hasPermission(kit.getPermissionNode())) {
                                    player.sendMessage(ColorText.translateAmpersand("&aYou already own this kit! Want to use it? Type &f/kit " + kit.getName()));
                                } else {
                                    player.closeInventory();
                                    if (profile.getStat(PlayerStat.CREDITS) < kit.getCreditCost()) {
                                        player.sendMessage(ColorText.translateAmpersand("&cYou don't have credits enough to acquire this &4Kit&c."));
                                    } else {
                                        profile.setStat(PlayerStat.CREDITS, (profile.getStat(PlayerStat.CREDITS) - kit.getCreditCost()));
                                        player.sendMessage(ColorText.translateAmpersand(kit.getDisplayName() + " &ahas been successfully purchased."));

                                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "perms add " + player.getName() + ' ' + kit.getPermissionNode());
                                    }
                                }
                            }

                            @Override
                            public ItemStack getItemStack() {
                                List<String> lore = kit.getDescription();
                                if (kit.getPermissionNode() == null || player.hasPermission(kit.getPermissionNode())) {
                                    lore.add("&aYou already own this Kit.");
                                } else {
                                    lore.add("&6&lPrice: &a" + Utils.getFormat(kit.getCreditCost()));
                                }
                                return new ItemMaker(kit.getItem()).setDisplayname((kit.getPermissionNode() == null || player.hasPermission(kit.getPermissionNode()) ? "&7[Owned] " + kit.getDisplayName() : "&c" + kit.getName())).addLore(lore).create();
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

                                for (int i = 45; i < 54; i++) {
                                    secondPage.setItem(i, new InventoryMaker.ClickableItem() {
                                        @Override
                                        public void onClick(InventoryClickEvent inventoryClickEvent) {

                                        }

                                        @Override
                                        public ItemStack getItemStack() {
                                            return new ItemMaker(Material.STAINED_GLASS_PANE).setDisplayname(" ").setDurability(7).create();
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
                                            if (kit.getPermissionNode() == null || player.hasPermission(kit.getPermissionNode())) {
                                                player.sendMessage(ColorText.translateAmpersand("&aYou already own this kit! Want to use it? Type &f/kit " + kit.getName()));
                                            } else {
                                                player.closeInventory();
                                                if (profile.getStat(PlayerStat.CREDITS) < kit.getCreditCost()) {
                                                    player.sendMessage(ColorText.translateAmpersand("&cYou don't have credits enough to acquire this &4Kit&c."));
                                                } else {
                                                    profile.setStat(PlayerStat.CREDITS, (profile.getStat(PlayerStat.CREDITS) - kit.getCreditCost()));
                                                    player.sendMessage(ColorText.translateAmpersand(kit.getDisplayName() + " &ahas been successfully purchased."));

                                                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "perms add " + player.getName() + ' ' + kit.getPermissionNode());
                                                }
                                            }
                                        }

                                        @Override
                                        public ItemStack getItemStack() {
                                            List<String> lore = kit.getDescription();
                                            if (kit.getPermissionNode() == null || player.hasPermission(kit.getPermissionNode())) {
                                                lore.add("&aYou already own this Kit.");
                                            } else {
                                                lore.add("&6&lPrice: &a" + Utils.getFormat(kit.getCreditCost()));
                                            }
                                            return new ItemMaker(kit.getItem()).setDisplayname((kit.getPermissionNode() == null || player.hasPermission(kit.getPermissionNode()) ? "&7[Owned] " + kit.getDisplayName() : "&c" + kit.getName())).addLore(lore).create();
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
                    return new ItemMaker(Material.ANVIL).setDisplayname("&4&lShop").addLore("", "&7Purchase kits", "&7and more using your &ccredits&7!", "", "&a&nClick here", "").create();
                }
            });

            inventoryMaker.setItem(43, new InventoryMaker.ClickableItem() {
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

                    for (int i = 45; i < 54; i++) {
                        maker.setItem(i, new InventoryMaker.ClickableItem() {
                            @Override
                            public void onClick(InventoryClickEvent inventoryClickEvent) {

                            }

                            @Override
                            public ItemStack getItemStack() {
                                return new ItemMaker(Material.STAINED_GLASS_PANE).setDisplayname(" ").setDurability(7).create();
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
                                if (kit.getPermissionNode() == null || player.hasPermission(kit.getPermissionNode())) {
                                    player.sendMessage(ColorText.translateAmpersand("&aYou already own this kit! Want to use it? Type &f/kit " + kit.getName()));
                                } else {
                                    player.closeInventory();
                                    if (profile.getStat(PlayerStat.CREDITS) < kit.getCreditCost()) {
                                        player.sendMessage(ColorText.translateAmpersand("&cYou don't have credits enough to acquire this &4Kit&c."));
                                    } else {
                                        profile.setStat(PlayerStat.CREDITS, (profile.getStat(PlayerStat.CREDITS) - kit.getCreditCost()));
                                        player.sendMessage(ColorText.translateAmpersand(kit.getDisplayName() + " &ahas been successfully purchased."));

                                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "perms add " + player.getName() + ' ' + kit.getPermissionNode());
                                    }
                                }
                            }

                            @Override
                            public ItemStack getItemStack() {
                                List<String> lore = kit.getDescription();
                                if (kit.getPermissionNode() == null || player.hasPermission(kit.getPermissionNode())) {
                                    lore.add("&aYou already own this Kit.");
                                } else {
                                    lore.add("&6&lPrice: &a" + Utils.getFormat(kit.getCreditCost()));
                                }
                                return new ItemMaker(kit.getItem()).setDisplayname((kit.getPermissionNode() == null || player.hasPermission(kit.getPermissionNode()) ? "&7[Owned] " + kit.getDisplayName() : "&c" + kit.getName())).addLore(lore).create();
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

                                for (int i = 45; i < 54; i++) {
                                    secondPage.setItem(i, new InventoryMaker.ClickableItem() {
                                        @Override
                                        public void onClick(InventoryClickEvent inventoryClickEvent) {

                                        }

                                        @Override
                                        public ItemStack getItemStack() {
                                            return new ItemMaker(Material.STAINED_GLASS_PANE).setDisplayname(" ").setDurability(7).create();
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
                                            if (kit.getPermissionNode() == null || player.hasPermission(kit.getPermissionNode())) {
                                                player.sendMessage(ColorText.translateAmpersand("&aYou already own this kit! Want to use it? Type &f/kit " + kit.getName()));
                                            } else {
                                                player.closeInventory();
                                                if (profile.getStat(PlayerStat.CREDITS) < kit.getCreditCost()) {
                                                    player.sendMessage(ColorText.translateAmpersand("&cYou don't have credits enough to acquire this &4Kit&c."));
                                                } else {
                                                    profile.setStat(PlayerStat.CREDITS, (profile.getStat(PlayerStat.CREDITS) - kit.getCreditCost()));
                                                    player.sendMessage(ColorText.translateAmpersand(kit.getDisplayName() + " &ahas been successfully purchased."));

                                                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "perms add " + player.getName() + ' ' + kit.getPermissionNode());
                                                }
                                            }
                                        }

                                        @Override
                                        public ItemStack getItemStack() {
                                            List<String> lore = kit.getDescription();
                                            if (kit.getPermissionNode() == null || player.hasPermission(kit.getPermissionNode())) {
                                                lore.add("&aYou already own this Kit.");
                                            } else {
                                                lore.add("&6&lPrice: &a" + Utils.getFormat(kit.getCreditCost()));
                                            }
                                            return new ItemMaker(kit.getItem()).setDisplayname((kit.getPermissionNode() == null || player.hasPermission(kit.getPermissionNode()) ? "&7[Owned] " + kit.getDisplayName() : "&c" + kit.getName())).addLore(lore).create();
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
                    return new ItemMaker(Material.ANVIL).setDisplayname("&4&lShop").addLore("", "&7Purchase kits", "&7and more using your &ccredits&7!", "", "&a&nClick here", "").create();
                }
            });

            List<Kit> kitsNoLoaded = new ArrayList<>();

            int i = 8;
            for (Kit kit : KitHandler.getKitList()) {
                if (kit.getPermissionNode() == null || player.hasPermission(kit.getPermissionNode()) || KitPvP.getInstance().getServerData().isFreeKitsMode()) {
                    if (i++ >= 35) {
                        kitsNoLoaded.add(kit);
                        continue;
                    }
                    inventoryMaker.setItem(i, new InventoryMaker.ClickableItem() {
                        @Override
                        public void onClick(InventoryClickEvent event) {
                            if (profile.getPlayerState() != PlayerState.SPAWN) {
                                player.sendMessage(ColorText.translateAmpersand("&cYou need spawn protection to choose a kit."));
                                return;
                            }

                            if (event.isShiftClick() && !player.isOp() && kit.getPermissionNode() != null && player.hasPermission(kit.getPermissionNode())) {
                                //player.closeInventory();
                                //player.sendMessage(ColorText.translate(profile.getTheme().getPrimaryColor() + "You just sold " + kit.getDisplayName() + " Kit " + profile.getTheme().getPrimaryColor() + "for " + profile.getTheme().getSecondaryColor() + (kit.getCredits() - 1000) + " credits" + profile.getTheme().getPrimaryColor() + '.'));
                                //profile.setStat(PlayerStat.CREDITS, (profile.getStat(PlayerStat.CREDITS) + (kit.getCredits() - 1000)));
                                //Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "perms remove " + player.getName() + ' ' + kit.getPermissions());
                                player.sendMessage(ColorText.translateAmpersand("&cSelling is disabled temporarily."));
                                return;
                            }

                            if (profile.getLastKitUsed() > 0L) {
                                player.sendMessage(ColorText.translateAmpersand("&cPlease wait &e" + DurationFormatUtils.formatDurationWords(profile.getLastKitUsed(), true, true) + " &cbefore using &e" + kit.getName() + " &cagain."));
                                return;
                            }

                            profile.setLastKitUsed(TimeUtils.parse("20s") + System.currentTimeMillis());

                            PlayerInventory inventory = player.getInventory();
                            inventory.clear();
                            inventory.setArmorContents(null);

                            for (PotionEffect effect : player.getActivePotionEffects()) {
                                player.removePotionEffect(effect.getType());
                            }

                            for (Cooldown cooldown : Cooldown.getCooldownMap().values()) {
                                if (!cooldown.isOnCooldown(player)) {
                                    continue;
                                }
                                cooldown.remove(player);
                            }

                            player.closeInventory();
                            profile.setLastKit(kit);
                            profile.getKitUses().put(kit.getName(), profile.getKitUses().getOrDefault(kit.getName(), 0) + 1);
                            kit.onLoad(player);
                            profile.setCurrentKit(kit);
                            player.sendMessage(ColorText.translateAmpersand("&7You selected the " + profile.getTheme().getPrimaryColor() + kit.getName() + " &7kit."));
                        }

                        @Override
                        public ItemStack getItemStack() {
                            List<String> strings = new ArrayList<>();
                            strings.add("&7&m" + StringUtils.repeat("-", 35));
                            strings.addAll(kit.getDescription());
                            if (!player.isOp() && kit.getPermissionNode() != null && player.hasPermission(kit.getPermissionNode())) {
                                strings.add("&7Shit-Click to sell this kit for &c" + (kit.getCreditCost() - 1000) + " &7credits!");
                                strings.add("");
                            }
                            if (KitPvP.getInstance().getServerData().isFreeKitsMode()) {
                                strings.add("&7This kit is &a&lFREE &7because");
                                strings.add("&b&lFREE KITS MODE &7is &aenabled&7!");
                                strings.add("");
                            }
                            strings.add(profile.getTheme().getPrimaryColor() + "Uses: " + profile.getTheme().getSecondaryColor() + profile.getKitUses().getOrDefault(kit.getName(), 0));
                            strings.add(profile.getTheme().getPrimaryColor() + "Kills: " + profile.getTheme().getSecondaryColor() + profile.getKitKills().getOrDefault(kit.getName(), 0));
                            strings.add(profile.getTheme().getPrimaryColor() + "Deaths: " + profile.getTheme().getSecondaryColor() + profile.getKitDeaths().getOrDefault(kit.getName(), 0));
                            strings.add("&7&m" + StringUtils.repeat("-", 35));
                            return new ItemMaker(kit.getItem()).setDisplayname((KitPvP.getInstance().getServerData().isFreeKitsMode() ? "&a&l[FREE] " : "") + profile.getTheme().getPrimaryColor() + "&l" + kit.getName()).addLore(strings).create();
                        }
                    });
                }
            }

            InventoryMaker maker = new InventoryMaker("&4&lYour Kits", 5);

            if (!kitsNoLoaded.isEmpty()) {
                inventoryMaker.setItem(40, new InventoryMaker.ClickableItem() {
                    @Override
                    public void onClick(InventoryClickEvent inventoryClickEvent) {
                        for (int i = 0; i < 9; i++) {
                            maker.setItem(i, new InventoryMaker.ClickableItem() {
                                @Override
                                public void onClick(InventoryClickEvent inventoryClickEvent) {

                                }

                                @Override
                                public ItemStack getItemStack() {
                                    return new ItemMaker(Material.STAINED_GLASS_PANE).setDisplayname(" ").setDurability(7).create();
                                }
                            });
                        }

                        for (int i = 36; i < 45; i++) {
                            maker.setItem(i, new InventoryMaker.ClickableItem() {
                                @Override
                                public void onClick(InventoryClickEvent inventoryClickEvent) {

                                }

                                @Override
                                public ItemStack getItemStack() {
                                    return new ItemMaker(Material.STAINED_GLASS_PANE).setDisplayname(" ").setDurability(7).create();
                                }
                            });
                        }

                        maker.setItem(4, new InventoryMaker.ClickableItem() {
                            @Override
                            public void onClick(InventoryClickEvent inventoryClickEvent) {

                            }

                            @Override
                            public ItemStack getItemStack() {
                                int unlocked = 0;
                                for (Kit kit : KitHandler.getKitList()) {
                                    if (kit.getPermissionNode() == null || player.hasPermission(kit.getPermissionNode())) {
                                        unlocked++;
                                    }
                                }
                                return new ItemMaker(Material.BOOK).setDisplayname("&4&lKits").addLore("", "&7You have kits from the Default rank!", "", "&7This menu &7only &7displays the kits you have!", "&7If you want more kits, buy on our &dstore", "&d&nshop.soupland.us &7(/buy)", "", "&7Unlocked Kits: &a" + unlocked + "&7/&f" + KitHandler.getKitList().size(), "").create();
                            }
                        });

                        maker.setItem(37, new InventoryMaker.ClickableItem() {
                            @Override
                            public void onClick(InventoryClickEvent inventoryClickEvent) {

                                kitShop(player, maker);

                            }

                            @Override
                            public ItemStack getItemStack() {
                                return new ItemMaker(Material.ANVIL).setDisplayname("&4&lShop").addLore("", "&7Purchase kits", "&7and more using your &ccredits&7!", "", "&a&nClick here", "").create();
                            }
                        });

                        maker.setItem(43, new InventoryMaker.ClickableItem() {
                            @Override
                            public void onClick(InventoryClickEvent inventoryClickEvent) {
                                kitShop(player, maker);
                            }

                            @Override
                            public ItemStack getItemStack() {
                                return new ItemMaker(Material.ANVIL).setDisplayname("&4&lShop").addLore("", "&7Purchase kits", "&7and more using your &ccredits&7!", "", "&a&nClick here", "").create();
                            }
                        });

                        int i = 9;
                        for (Kit kit : kitsNoLoaded) {
                            if (kit.getPermissionNode() != null && !player.hasPermission(kit.getPermissionNode()) && !KitPvP.getInstance().getServerData().isFreeKitsMode()) {
                                continue;
                            }
                            maker.setItem(i, new InventoryMaker.ClickableItem() {
                                @Override
                                public void onClick(InventoryClickEvent event) {
                                    if (profile.getPlayerState() != PlayerState.SPAWN) {
                                        player.sendMessage(ColorText.translateAmpersand("&cYou need spawn protection to choose a kit."));
                                        return;
                                    }

                                    if (event.isShiftClick() && !player.isOp() && kit.getPermissionNode() != null && player.hasPermission(kit.getPermissionNode())) {
                                        //player.closeInventory();
                                        //player.sendMessage(ColorText.translate(profile.getTheme().getPrimaryColor() + "You just sold " + kit.getDisplayName() + " Kit " + profile.getTheme().getPrimaryColor() + "for " + profile.getTheme().getSecondaryColor() + (kit.getCredits() - 1000) + " credits" + profile.getTheme().getPrimaryColor() + '.'));
                                        //profile.setStat(PlayerStat.CREDITS, (profile.getStat(PlayerStat.CREDITS) + (kit.getCredits() - 1000)));
                                        //Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "perms remove " + player.getName() + ' ' + kit.getPermissions());
                                        player.sendMessage(ColorText.translateAmpersand("&cSelling is disabled temporarily."));
                                        return;
                                    }

                                    if (profile.getLastKitUsed() > 0L) {
                                        player.sendMessage(ColorText.translateAmpersand("&cPlease wait &e" + DurationFormatUtils.formatDurationWords(profile.getLastKitUsed(), true, true) + " &cbefore using &e" + kit.getName() + " &cagain."));
                                        return;
                                    }

                                    profile.setLastKitUsed(TimeUtils.parse("20s") + System.currentTimeMillis());

                                    PlayerInventory inventory = player.getInventory();
                                    inventory.clear();
                                    inventory.setArmorContents(null);

                                    for (PotionEffect effect : player.getActivePotionEffects()) {
                                        player.removePotionEffect(effect.getType());
                                    }

                                    for (Cooldown cooldown : Cooldown.getCooldownMap().values()) {
                                        if (!cooldown.isOnCooldown(player)) {
                                            continue;
                                        }
                                        cooldown.remove(player);
                                    }

                                    player.closeInventory();
                                    profile.setLastKit(kit);
                                    profile.getKitUses().put(kit.getName(), profile.getKitUses().getOrDefault(kit.getName(), 0) + 1);
                                    kit.onLoad(player);
                                    profile.setCurrentKit(kit);
                                    player.sendMessage(ColorText.translateAmpersand("&7You selected the " + profile.getTheme().getPrimaryColor() + kit.getName() + " &7kit."));
                                }

                                @Override
                                public ItemStack getItemStack() {
                                    List<String> strings = new ArrayList<>();
                                    strings.add("&7&m" + StringUtils.repeat("-", 35));
                                    strings.addAll(kit.getDescription());
                                    if (!player.isOp() && kit.getPermissionNode() != null && player.hasPermission(kit.getPermissionNode())) {
                                        strings.add("&7Shit-Click to sell this kit for &c" + (kit.getCreditCost() - 1000) + " &7credits!");
                                        strings.add("");
                                    }
                                    if (KitPvP.getInstance().getServerData().isFreeKitsMode()) {
                                        strings.add("&7This kit is &a&lFREE &7because");
                                        strings.add("&b&lFREE KITS MODE &7is &aenabled&7!");
                                        strings.add("");
                                    }
                                    strings.add(profile.getTheme().getPrimaryColor() + "Uses: " + profile.getTheme().getSecondaryColor() + profile.getKitUses().getOrDefault(kit.getName(), 0));
                                    strings.add(profile.getTheme().getPrimaryColor() + "Kills: " + profile.getTheme().getSecondaryColor() + profile.getKitKills().getOrDefault(kit.getName(), 0));
                                    strings.add(profile.getTheme().getPrimaryColor() + "Deaths: " + profile.getTheme().getSecondaryColor() + profile.getKitDeaths().getOrDefault(kit.getName(), 0));
                                    strings.add("&7&m" + StringUtils.repeat("-", 35));
                                    return new ItemMaker(kit.getItem()).setDisplayname((KitPvP.getInstance().getServerData().isFreeKitsMode() ? "&a&l[FREE] " : "") + profile.getTheme().getPrimaryColor() + "&l" + kit.getName()).addLore(strings).create();
                                }
                            });
                            if (i++ >= 35) {
                                break;
                            }
                        }

                        maker.setItem(40, new InventoryMaker.ClickableItem() {
                            @Override
                            public void onClick(InventoryClickEvent inventoryClickEvent) {
                                player.openInventory(inventoryMaker.getCurrentPage());
                            }

                            @Override
                            public ItemStack getItemStack() {
                                return new ItemMaker(Material.getMaterial(101)).setDisplayname("&cPrevious Page").create();
                            }
                        });

                        player.openInventory(maker.getCurrentPage());
                    }

                    @Override
                    public ItemStack getItemStack() {
                        return new ItemMaker(Material.getMaterial(101)).setDisplayname("&cNext Page").create();
                    }
                });
            }

            player.openInventory(inventoryMaker.getCurrentPage());
        } else {

            List<String> strings = new ArrayList<>();
            for (Kit kit : KitHandler.getKitList()) {
                if (kit.getPermissionNode() == null || player.hasPermission(kit.getPermissionNode()) || KitPvP.getInstance().getServerData().isFreeKitsMode()) {
                    strings.add("&a" + kit.getName());
                } else {
                    strings.add("&c" + kit.getName());
                }
            }

            if (args.length < 1) {
                player.sendMessage(ColorText.translateAmpersand("&cUsage: /" + label + " <kit>"));
                player.sendMessage(ColorText.translateAmpersand(StringUtils.join(strings, "&7, ") + "&7."));
            } else {
                Kit kit = KitHandler.getByName(args[0]);
                if (kit == null) {
                    player.performCommand("kits");
                } else {
                    if (profile.getLastKitUsed() > 0L) {
                        player.sendMessage(ColorText.translateAmpersand("&cPlease wait &e" + DurationFormatUtils.formatDurationWords(profile.getLastKitUsed(), true, true) + " &cbefore using &e" + kit.getName() + " &cagain."));
                        return false;
                    }

                    profile.setLastKitUsed(TimeUtils.parse("20s") + System.currentTimeMillis());

                    PlayerInventory inventory = player.getInventory();
                    inventory.clear();
                    inventory.setArmorContents(null);

                    for (PotionEffect effect : player.getActivePotionEffects()) {
                        player.removePotionEffect(effect.getType());
                    }

                    for (Cooldown cooldown : Cooldown.getCooldownMap().values()) {
                        if (!cooldown.isOnCooldown(player)) {
                            continue;
                        }
                        cooldown.remove(player);
                    }

                    player.closeInventory();
                    profile.setLastKit(kit);
                    profile.getKitUses().put(kit.getName(), profile.getKitUses().getOrDefault(kit.getName(), 0) + 1);
                    kit.onLoad(player);
                    profile.setCurrentKit(kit);
                    player.sendMessage(ColorText.translateAmpersand("&7You selected the " + profile.getTheme().getPrimaryColor() + kit.getName() + " &7kit."));
                }
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> toReturn = new ArrayList<>();
        if (sender instanceof Player) {
            if (args.length == 1 && ProfileManager.getProfile((Player) sender).getKitMenuType() == KitMenuType.TEXT) {
                for (Kit kit : KitHandler.getKitList()) {
                    toReturn.add(kit.getName());
                }
            }
        }
        return KitPvPUtils.getCompletions(args, toReturn);
    }

    public InventoryMaker kitShop(Player player, InventoryMaker previus) {
        InventoryMaker maker = new InventoryMaker("&4&lKits", 6);
        Profile profile = ProfileManager.getProfile(player);

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

        for (int i = 45; i < 54; i++) {
            maker.setItem(i, new InventoryMaker.ClickableItem() {
                @Override
                public void onClick(InventoryClickEvent inventoryClickEvent) {

                }

                @Override
                public ItemStack getItemStack() {
                    return new ItemMaker(Material.STAINED_GLASS_PANE).setDisplayname(" ").setDurability(7).create();
                }
            });
        }

        maker.setItem(0, new InventoryMaker.ClickableItem() {
            @Override
            public void onClick(InventoryClickEvent inventoryClickEvent) {
                player.openInventory(previus.getCurrentPage());
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
                    if (kit.getPermissionNode() == null || player.hasPermission(kit.getPermissionNode())) {
                        player.sendMessage(ColorText.translateAmpersand("&aYou already own this kit! Want to use it? Type &f/kit " + kit.getName()));
                    } else {
                        player.closeInventory();
                        if (profile.getStat(PlayerStat.CREDITS) < kit.getCreditCost()) {
                            player.sendMessage(ColorText.translateAmpersand("&cYou don't have credits enough to acquire this &4Kit&c."));
                        } else {
                            profile.setStat(PlayerStat.CREDITS, (profile.getStat(PlayerStat.CREDITS) - kit.getCreditCost()));
                            player.sendMessage(ColorText.translateAmpersand(kit.getDisplayName() + " &ahas been successfully purchased."));

                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "perms add " + player.getName() + ' ' + kit.getPermissionNode());
                        }
                    }
                }

                @Override
                public ItemStack getItemStack() {
                    List<String> lore = kit.getDescription();
                    if (kit.getPermissionNode() == null || player.hasPermission(kit.getPermissionNode())) {
                        lore.add("&aYou already own this Kit.");
                    } else {
                        lore.add("&6&lPrice: &a" + Utils.getFormat(kit.getCreditCost()));
                    }
                    return new ItemMaker(kit.getItem()).setDisplayname((kit.getPermissionNode() == null || player.hasPermission(kit.getPermissionNode()) ? "&7[Owned] " + kit.getDisplayName() : "&c" + kit.getName())).addLore(lore).create();
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

                    for (int i = 45; i < 54; i++) {
                        secondPage.setItem(i, new InventoryMaker.ClickableItem() {
                            @Override
                            public void onClick(InventoryClickEvent inventoryClickEvent) {

                            }

                            @Override
                            public ItemStack getItemStack() {
                                return new ItemMaker(Material.STAINED_GLASS_PANE).setDisplayname(" ").setDurability(7).create();
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
                            player.openInventory(previus.getCurrentPage());
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
                                if (kit.getPermissionNode() == null || player.hasPermission(kit.getPermissionNode())) {
                                    player.sendMessage(ColorText.translateAmpersand("&aYou already own this kit! Want to use it? Type &f/kit " + kit.getName()));
                                } else {
                                    player.closeInventory();
                                    if (profile.getStat(PlayerStat.CREDITS) < kit.getCreditCost()) {
                                        player.sendMessage(ColorText.translateAmpersand("&cYou don't have credits enough to acquire this &4Kit&c."));
                                    } else {
                                        profile.setStat(PlayerStat.CREDITS, (profile.getStat(PlayerStat.CREDITS) - kit.getCreditCost() - 1000));
                                        player.sendMessage(ColorText.translateAmpersand(kit.getDisplayName() + " &ahas been successfully purchased."));

                                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "perms add " + player.getName() + ' ' + kit.getPermissionNode());
                                    }
                                }
                            }

                            @Override
                            public ItemStack getItemStack() {
                                List<String> lore = kit.getDescription();
                                if (kit.getPermissionNode() == null || player.hasPermission(kit.getPermissionNode())) {
                                    lore.add("&aYou already own this Kit.");
                                } else {
                                    lore.add("&6&lPrice: &a" + Utils.getFormat(kit.getCreditCost()));
                                }
                                return new ItemMaker(kit.getItem()).setDisplayname((kit.getPermissionNode() == null || player.hasPermission(kit.getPermissionNode()) ? "&7[Owned] " + kit.getDisplayName() : "&c" + kit.getName())).addLore(lore).create();
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
        return maker;
    }
}