package us.soupland.kitpvp.commands;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import us.soupland.kitpvp.enums.PlayerStat;
import us.soupland.kitpvp.profile.Profile;
import us.soupland.kitpvp.profile.ProfileManager;
import us.soupland.kitpvp.utilities.KitPvPUtils;
import us.soupland.kitpvp.utilities.chat.ColorText;
import us.soupland.kitpvp.utilities.command.KitPvPCommand;
import us.soupland.kitpvp.utilities.inventory.InventoryMaker;
import us.soupland.kitpvp.utilities.item.ItemMaker;

import java.util.ArrayList;
import java.util.List;

public class StatsCommand extends KitPvPCommand {

    public StatsCommand() {
        super("stats");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(KitPvPUtils.ONLY_PLAYERS);
            return false;
        }
        OfflinePlayer target;
        if (args.length < 1) {
            target = (Player) sender;
        } else {
            target = Bukkit.getOfflinePlayer(args[0]);
            if ((!target.hasPlayedBefore()) && (!target.isOnline())) {
                sender.sendMessage(ColorText.translateAmpersand("&c" + args[0] + " has never played before."));
                return false;
            }
        }
        Profile profile = ProfileManager.getProfile(target);
        InventoryMaker inventoryMaker = new InventoryMaker(profile.getTheme().getPrimaryColor() + target.getName() + '\'' + (target.getName().endsWith("s") ? "" : "s") + " Stats", 3);

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

        inventoryMaker.setItem(4, new InventoryMaker.ClickableItem() {
            @Override
            public void onClick(InventoryClickEvent inventoryClickEvent) {

            }

            @Override
            public ItemStack getItemStack() {
                ItemStack stack = new ItemMaker(Material.SKULL_ITEM).setDurability(3).create();
                SkullMeta meta = (SkullMeta) stack.getItemMeta();
                meta.setOwner(target.getName());
                meta.setDisplayName(ColorText.translateAmpersand(profile.getTheme().getPrimaryColor() + target.getName() + '\'' + (target.getName().endsWith("s") ? "" : "s") + " Stats"));
                stack.setItemMeta(meta);
                return stack;
            }
        });

        inventoryMaker.setItem(10, new InventoryMaker.ClickableItem() {
            @Override
            public void onClick(InventoryClickEvent inventoryClickEvent) {

            }

            @Override
            public ItemStack getItemStack() {
                return new ItemMaker(Material.WOOD_SWORD).setDisplayname("&a&lKills").addLore("&7Total Kills: " + profile.getTheme().getSecondaryColor() + profile.getStat(PlayerStat.KILLS)).create();
            }
        });

        inventoryMaker.setItem(13, new InventoryMaker.ClickableItem() {
            @Override
            public void onClick(InventoryClickEvent inventoryClickEvent) {

            }

            @Override
            public ItemStack getItemStack() {
                return new ItemMaker(Material.GOLD_SWORD).setDisplayname("&c&lDeaths").addLore("&7Total Deaths: " + profile.getTheme().getSecondaryColor() + profile.getStat(PlayerStat.DEATHS)).create();
            }
        });

        inventoryMaker.setItem(16, new InventoryMaker.ClickableItem() {
            @Override
            public void onClick(InventoryClickEvent inventoryClickEvent) {

            }

            @Override
            public ItemStack getItemStack() {
                return new ItemMaker(Material.STONE_SWORD).setDisplayname("&e&lKillStreak").addLore("&7KillStreak: " + profile.getTheme().getSecondaryColor() + profile.getStat(PlayerStat.STREAK)).create();
            }
        });

        inventoryMaker.setItem(20, new InventoryMaker.ClickableItem() {
            @Override
            public void onClick(InventoryClickEvent inventoryClickEvent) {

            }

            @Override
            public ItemStack getItemStack() {
                return new ItemMaker(Material.IRON_SWORD).setDisplayname("&b&lHighest KillStreak").addLore("&7Highest KillStreak: " + profile.getTheme().getSecondaryColor() + profile.getStat(PlayerStat.HIGHEST_STREAK)).create();
            }
        });

        inventoryMaker.setItem(22, new InventoryMaker.ClickableItem() {
            @Override
            public void onClick(InventoryClickEvent inventoryClickEvent) {

            }

            @Override
            public ItemStack getItemStack() {
                List<String> lore = new ArrayList<>();
                lore.add("&7Total Event Wins: " + profile.getTheme().getSecondaryColor() + profile.getStat(PlayerStat.EVENT_WINS));
                if (!profile.getEventsWin().isEmpty()) {
                    lore.add("");
                    lore.add(profile.getTheme().getPrimaryColor() + StringUtils.repeat("=", 6));
                    lore.add("");
                    profile.getEventsWin().forEach((s, integer) -> lore.add("&7" + s + ": " + profile.getTheme().getSecondaryColor() + integer));
                    lore.add("");
                }
                return new ItemMaker(Material.DIAMOND_BLOCK).setDisplayname("&3&lEvent Wins").addLore(lore).create();
            }
        });

        inventoryMaker.setItem(24, new InventoryMaker.ClickableItem() {
            @Override
            public void onClick(InventoryClickEvent inventoryClickEvent) {

            }

            @Override
            public ItemStack getItemStack() {
                return new ItemMaker(Material.DIAMOND_SWORD).setDisplayname("&d&lCredits").addLore("&7Total Credits: " + profile.getTheme().getSecondaryColor() + profile.getStat(PlayerStat.CREDITS)).create();
            }
        });

        ((Player) sender).openInventory(inventoryMaker.getCurrentPage());
        sender.sendMessage(ColorText.translateAmpersand("&7You're viewing &a" + target.getName() + "&7'" + (target.getName().endsWith("s") ? "" : "s") + " stats."));
        return true;
    }
}