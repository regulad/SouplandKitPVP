package us.soupland.kitpvp.commands.menu;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import us.soupland.kitpvp.enums.PlayerState;
import us.soupland.kitpvp.managers.BountyManager;
import us.soupland.kitpvp.profile.ProfileManager;
import us.soupland.kitpvp.utilities.KitPvPUtils;
import us.soupland.kitpvp.utilities.chat.ColorText;
import us.soupland.kitpvp.utilities.command.KitPvPCommand;
import us.soupland.kitpvp.utilities.inventory.InventoryMaker;
import us.soupland.kitpvp.utilities.item.ItemMaker;

import java.util.Map;
import java.util.UUID;

public class BountyHunterCommand extends KitPvPCommand {

    public BountyHunterCommand() {
        super("bountyhunter", null, "bh", "bounty");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(KitPvPUtils.ONLY_PLAYERS);
            return false;
        }
        Player player = (Player) sender;
        InventoryMaker inventoryMaker = new InventoryMaker("&c&lBounty Hunter", 4);

        for (int i = 0; i < 9; i++) {
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
                return new ItemMaker(Material.INK_SACK).setDisplayname("&c&lGo Back").setDurability(1).create();
            }
        });

        inventoryMaker.setItem(4, new InventoryMaker.ClickableItem() {
            @Override
            public void onClick(InventoryClickEvent inventoryClickEvent) {

            }

            @Override
            public ItemStack getItemStack() {
                return new ItemMaker(Material.BOW).setDisplayname("&c&lBounty Hunter").create();
            }
        });

        inventoryMaker.setItem(8, new InventoryMaker.ClickableItem() {
            @Override
            public void onClick(InventoryClickEvent inventoryClickEvent) {
                InventoryMaker maker = new InventoryMaker("&c&lBounty Hunter", 4);

                for (Player online : Bukkit.getOnlinePlayers()) {
                    if (player == online || !player.canSee(online) || ProfileManager.getProfile(online).getPlayerState() != PlayerState.PLAYING || BountyManager.isInMap(online.getUniqueId())) {
                        continue;
                    }
                    maker.addItem(new InventoryMaker.ClickableItem() {
                        @Override
                        public void onClick(InventoryClickEvent inventoryClickEvent) {
                            player.closeInventory();
                            if (BountyManager.isInMap(online.getUniqueId())) {
                                return;
                            }
                            if (BountyManager.isInMap(player.getUniqueId())) {
                                return;
                            }
                            ProfileManager.getProfile(player).setCreatingBounty(true);
                            BountyManager.getHunterHunted().put(player.getUniqueId(), online.getUniqueId());
                            player.sendMessage(ColorText.translate("&4&l[Bounty] &cYou are creating a Bounty. Please type the amount in chat."));
                            player.sendMessage(ColorText.translate("&cType '&fcancel&c' to cancel it."));
                        }

                        @Override
                        public ItemStack getItemStack() {
                            ItemStack stack = new ItemMaker(Material.SKULL_ITEM).setDurability(3).setDisplayname(online.getName()).addLore("", "&7Click here to create a Bounty Hunter", "", "&a&nClick here", "").create();
                            SkullMeta meta = (SkullMeta) stack.getItemMeta();
                            meta.setOwner(online.getName());
                            stack.setItemMeta(meta);
                            return stack;
                        }
                    });
                }

                maker.setItem(31, new InventoryMaker.ClickableItem() {
                    @Override
                    public void onClick(InventoryClickEvent inventoryClickEvent) {
                        player.openInventory(inventoryMaker.getCurrentPage());
                    }

                    @Override
                    public ItemStack getItemStack() {
                        return new ItemMaker(Material.INK_SACK).setDisplayname("&c&lGo Back").setDurability(1).create();
                    }
                });

                player.openInventory(maker.getCurrentPage());
            }

            @Override
            public ItemStack getItemStack() {
                return new ItemMaker(Material.SKULL_ITEM).setDurability(1).setDisplayname("&e&lCreate").addLore("&7Create a Bounty", "", "&a&nClick here", "").create();
            }
        });

        int i = 9;
        for (Map.Entry<UUID, UUID> entry : BountyManager.getHunterHunted().entrySet()) {
            if (!BountyManager.getPriceMap().containsKey(entry.getKey())) {
                continue;
            }
            inventoryMaker.setItem(i, new InventoryMaker.ClickableItem() {
                @Override
                public void onClick(InventoryClickEvent inventoryClickEvent) {

                }

                @Override
                public ItemStack getItemStack() {
                    String name = Bukkit.getOfflinePlayer(entry.getValue()).getName();
                    ItemStack stack = new ItemMaker(Material.SKULL_ITEM).setDurability(3).setDisplayname(name).addLore("", "&7Created by: &f" + Bukkit.getOfflinePlayer(entry.getKey()).getName(), "&7Quantity: &f" + BountyManager.getPriceMap().get(entry.getKey()), "").create();
                    SkullMeta meta = (SkullMeta) stack.getItemMeta();
                    meta.setOwner(name);
                    stack.setItemMeta(meta);
                    return stack;
                }
            });
            i++;
        }

        player.openInventory(inventoryMaker.getCurrentPage());
        return true;
    }
}