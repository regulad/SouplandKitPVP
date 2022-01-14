package us.soupland.kitpvp.commands;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import us.soupland.kitpvp.KitPvP;
import us.soupland.kitpvp.enums.PlayerStat;
import us.soupland.kitpvp.profile.Profile;
import us.soupland.kitpvp.profile.ProfileManager;
import us.soupland.kitpvp.sidebar.team.Team;
import us.soupland.kitpvp.utilities.KitPvPUtils;
import us.soupland.kitpvp.utilities.chat.ColorText;
import us.soupland.kitpvp.utilities.command.KitPvPCommand;
import us.soupland.kitpvp.utilities.cooldown.Cooldown;
import us.soupland.kitpvp.utilities.inventory.InventoryMaker;
import us.soupland.kitpvp.utilities.item.ItemMaker;
import us.soupland.kitpvp.utilities.player.DurationFormatter;
import us.soupland.kitpvp.utilities.time.TimeUtils;

import java.text.SimpleDateFormat;
import java.util.UUID;

public class TeamsCommand extends KitPvPCommand {

    public TeamsCommand() {
        super("teams");
        new Cooldown("LFF", TimeUtils.parse("12h"), "&6LFF", null);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(KitPvPUtils.ONLY_PLAYERS);
            return false;
        }
        Player player = (Player) sender;
        Profile profile = ProfileManager.getProfile(player);
        Team team = profile.getTeam();
        InventoryMaker inventoryMaker = new InventoryMaker("&c&lTeams", 3);

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
                return new ItemMaker(Material.DIAMOND_CHESTPLATE).setDisplayname("&c&lTeams").addLore("", "&7You can create your own team", "&7and manage as you want!", "", "&7Teams Crated: &c" + Team.getTeams().size(), "").create();
            }
        });

        inventoryMaker.setItem(8, new InventoryMaker.ClickableItem() {
            @Override
            public void onClick(InventoryClickEvent inventoryClickEvent) {
                InventoryMaker maker = new InventoryMaker("&d&lList of all Teams", 5);

                maker.setItem(40, new InventoryMaker.ClickableItem() {
                    @Override
                    public void onClick(InventoryClickEvent inventoryClickEvent) {
                        player.openInventory(inventoryMaker.getCurrentPage());
                    }

                    @Override
                    public ItemStack getItemStack() {
                        return new ItemMaker(Material.INK_SACK).setDisplayname("&c&lGo Back").setDurability(1).create();
                    }
                });

                for (Team faggot : Team.getTeams()) {
                    maker.addItem(new InventoryMaker.ClickableItem() {
                        @Override
                        public void onClick(InventoryClickEvent inventoryClickEvent) {
                            InventoryMaker inventory = new InventoryMaker("Team - " + faggot.getName(), 3);

                            for (int i = 0; i < 9; i++) {
                                inventory.setItem(i, new InventoryMaker.ClickableItem() {
                                    @Override
                                    public void onClick(InventoryClickEvent inventoryClickEvent) {

                                    }

                                    @Override
                                    public ItemStack getItemStack() {
                                        return new ItemMaker(Material.STAINED_GLASS_PANE).setDurability(7).setDisplayname(" ").create();
                                    }
                                });
                            }

                            inventory.setItem(4, new InventoryMaker.ClickableItem() {
                                @Override
                                public void onClick(InventoryClickEvent inventoryClickEvent) {

                                }

                                @Override
                                public ItemStack getItemStack() {
                                    SimpleDateFormat format = new SimpleDateFormat("dd-MM HH:mm:ss");
                                    return new ItemMaker(Material.PAPER).setDisplayname("&e&lInformation").addLore("", "&7Leader: " + Bukkit.getOfflinePlayer(faggot.getLeader()).getName(), "&7Captains: &f" + faggot.getOfficers().size() + " &e(Included Leader)", "&7Members: &f" + faggot.getMembers().size(), "&7Announcement: &f" + (faggot.getDescription() == null ? "Default!" : faggot.getDescription()), "&7Created At: &a" + format.format(faggot.getCreatedAt()), "").create();
                                }
                            });

                            inventory.setItem(9, new InventoryMaker.ClickableItem() {
                                @Override
                                public void onClick(InventoryClickEvent inventoryClickEvent) {
                                    player.closeInventory();
                                    player.performCommand("stats " + Bukkit.getOfflinePlayer(faggot.getLeader()).getName());
                                }

                                @Override
                                public ItemStack getItemStack() {
                                    String name = Bukkit.getOfflinePlayer(faggot.getLeader()).getName();
                                    if (name == null) {
                                        name = "-";
                                    }
                                    SimpleDateFormat format = new SimpleDateFormat("dd-MM HH:mm:ss");
                                    ItemStack stack = new ItemMaker(Material.SKULL_ITEM).setDisplayname(name).addLore("Role: &fLEADER", "Joined: &f" + format.format(faggot.getPlayerJoined().getOrDefault(faggot.getLeader(), System.currentTimeMillis())), "", "Click here for more information", "").create();
                                    SkullMeta meta = (SkullMeta) stack.getItemMeta();
                                    meta.setOwner(Bukkit.getOfflinePlayer(faggot.getLeader()).getName());
                                    stack.setItemMeta(meta);
                                    return stack;
                                }
                            });

                            int i = 10;
                            for (UUID uuid : faggot.getOfficers()) {
                                if (uuid == faggot.getLeader()) {
                                    continue;
                                }
                                inventory.setItem(i, new InventoryMaker.ClickableItem() {
                                    @Override
                                    public void onClick(InventoryClickEvent inventoryClickEvent) {
                                        player.closeInventory();
                                        player.performCommand("stats " + Bukkit.getOfflinePlayer(uuid).getName());
                                    }

                                    @Override
                                    public ItemStack getItemStack() {
                                        String name = Bukkit.getOfflinePlayer(uuid).getName();
                                        if (name == null) {
                                            name = "-";
                                        }
                                        SimpleDateFormat format = new SimpleDateFormat("dd-MM HH:mm:ss");
                                        ItemStack stack = new ItemMaker(Material.SKULL_ITEM).setDisplayname(name).addLore("Role: &fCAPTAIN", "Joined: &f" + format.format(faggot.getPlayerJoined().getOrDefault(uuid, System.currentTimeMillis())), "", "Click here for more information", "").create();
                                        SkullMeta meta = (SkullMeta) stack.getItemMeta();
                                        meta.setOwner(Bukkit.getOfflinePlayer(uuid).getName());
                                        stack.setItemMeta(meta);
                                        return stack;
                                    }
                                });
                                i++;
                            }

                            for (UUID uuid : faggot.getMembers()) {
                                inventory.setItem(i, new InventoryMaker.ClickableItem() {
                                    @Override
                                    public void onClick(InventoryClickEvent inventoryClickEvent) {
                                        player.closeInventory();
                                        player.performCommand("stats " + Bukkit.getOfflinePlayer(uuid).getName());
                                    }

                                    @Override
                                    public ItemStack getItemStack() {
                                        String name = Bukkit.getOfflinePlayer(uuid).getName();
                                        if (name == null) {
                                            name = "-";
                                        }
                                        SimpleDateFormat format = new SimpleDateFormat("dd-MM HH:mm:ss");
                                        ItemStack stack = new ItemMaker(Material.SKULL_ITEM).setDisplayname(name).addLore("Role: &fMEMBER", "Joined: &f" + format.format(faggot.getPlayerJoined().getOrDefault(uuid, System.currentTimeMillis())), "", "Click here for more information", "").create();
                                        SkullMeta meta = (SkullMeta) stack.getItemMeta();
                                        meta.setOwner(Bukkit.getOfflinePlayer(uuid).getName());
                                        stack.setItemMeta(meta);
                                        return stack;
                                    }
                                });
                                i++;
                            }

                            player.openInventory(inventory.getCurrentPage());
                        }

                        @Override
                        public ItemStack getItemStack() {
                            String name = Bukkit.getOfflinePlayer(faggot.getLeader()).getName();
                            if (name == null) {
                                name = "-";
                            }
                            ItemStack stack = new ItemMaker(Material.SKULL_ITEM).setDurability(3).setDisplayname(name + "'s Team").addLore("", "&7Members: &f" + faggot.getMembers().size(), "&7Captains: &f" + faggot.getOfficers().size() + " &e(Included Leader)", "&7Announcement: &f" + (faggot.getDescription() == null ? "Default!" : faggot.getDescription()), "&7Invited Players: &f" + faggot.getInvitedPlayers().size(), "", "&a&nClick here", "").create();
                            SkullMeta meta = (SkullMeta) stack.getItemMeta();
                            meta.setOwner(Bukkit.getOfflinePlayer(faggot.getLeader()).getName());
                            stack.setItemMeta(meta);
                            return stack;
                        }
                    });
                }

                player.openInventory(maker.getCurrentPage());
            }

            @Override
            public ItemStack getItemStack() {
                return new ItemMaker(Material.PAPER).setDisplayname("&d&lTeam List").addLore("&7List of all Teams!", "", "&a&nClick Here", "").create();
            }
        });

        int[] ints = new int[]{9, 17};
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
        }

        for (int i = 18; i < 27; i++) {
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

        inventoryMaker.setItem(10, new InventoryMaker.ClickableItem() {
            @Override
            public void onClick(InventoryClickEvent inventoryClickEvent) {
                Cooldown cooldown = KitPvP.getCooldown("LFF");
                if (cooldown != null) {
                    if (cooldown.isOnCooldown(player)) {
                        player.sendMessage(ColorText.translate("&cYou are on cooldown for another &e" + DurationFormatter.getRemaining(cooldown.getDuration(player), true) + "&c."));
                        return;
                    }
                    cooldown.setCooldown(player, true);
                }
                Bukkit.broadcastMessage(ColorText.translate("&7&m-------------------------------------"));
                Bukkit.broadcastMessage(ColorText.translate("&6" + player.getName() + " &eis looking for a team!"));
                Bukkit.broadcastMessage(ColorText.translate("&7&m-------------------------------------"));
            }

            @Override
            public ItemStack getItemStack() {
                return new ItemMaker(Material.FENCE).setDisplayname("&e&lPlayers Looking For Team").addLore("", "&7By clicking this item", "&7you would be announcing", "&7that you are free to be", "&7a team member.").create();
            }
        });

        if (team == null) {
            inventoryMaker.setItem(13, new InventoryMaker.ClickableItem() {
                @Override
                public void onClick(InventoryClickEvent inventoryClickEvent) {
                    player.closeInventory();

                    if (profile.getStat(PlayerStat.CREDITS) < 5000) {
                        player.sendMessage(ColorText.translate("&cYou don't have credits enough to create a Team."));
                        return;
                    }

                    profile.setCreatingTeam(true);
                    player.sendMessage(ColorText.translate("&4&l[Team] &cYou are creating a team. Please type the name in chat."));
                    player.sendMessage(ColorText.translate("&cType '&fcancel&c' to cancel it."));
                }

                @Override
                public ItemStack getItemStack() {
                    return new ItemMaker(Material.EMERALD).setDisplayname("&a&lCreate a Team").addLore("&7Click here to create a team", "&7or use &d/team create <teamName>", "", "&a&nClick here", "").create();
                }
            });
        }

        player.openInventory(inventoryMaker.getCurrentPage());
        return true;
    }
}