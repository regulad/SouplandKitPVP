package us.soupland.kitpvp.commands.kitpvp;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
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
import us.soupland.kitpvp.utilities.command.KitPvPArgument;
import us.soupland.kitpvp.utilities.inventory.InventoryMaker;
import us.soupland.kitpvp.utilities.item.ItemMaker;

public class KitProfileArgument extends KitPvPArgument {

    public KitProfileArgument() {
        super("profile");
        onlyplayers = true;
    }

    @Override
    public String getUsage(String s) {
        return '/' + s + ' ' + name + " <playerName>";
    }

    @Override
    public void onExecute(CommandSender sender, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ColorText.translate("&cUsage: " + getUsage(label)));
        } else {
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
            if ((!target.hasPlayedBefore()) && (!target.isOnline())) {
                sender.sendMessage(ColorText.translate("&c" + args[1] + " has never played before."));
                return;
            }
            Profile profile = ProfileManager.getProfile(target);
            InventoryMaker inventoryMaker = new InventoryMaker(target.getName() + "'s Profile", 6);

            for (int i = 0; i < 54; i++) {
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
                    ItemStack skull = new ItemMaker(Material.SKULL_ITEM).setDurability(3).create();
                    SkullMeta meta = (SkullMeta) skull.getItemMeta();
                    meta.setOwner(target.getName());
                    meta.setDisplayName(ColorText.translate(target.getName() + "'s Profile"));
                    skull.setItemMeta(meta);
                    return skull;
                }
            });

            inventoryMaker.setItem(8, new InventoryMaker.ClickableItem() {
                @Override
                public void onClick(InventoryClickEvent inventoryClickEvent) {

                }

                @Override
                public ItemStack getItemStack() {
                    return new ItemMaker(Material.PAPER).setDisplayname("&7&l* &eStatistics &7&l*").addLore("", "&4Theme: &f" + profile.getTheme().getPrimaryColor() + profile.getTheme().name(), "&4Player Elo: &f" + profile.getStat(PlayerStat.ELO), "&4Scoreboard Enabled? &f" + profile.isScoreboardEnabled(), "&4Chat Color: " + profile.getChatColor() + profile.getChatColor().name(), "&4Joins: &f" + profile.getStat(PlayerStat.JOINS)
                            , "&4Event Wins: &f" + profile.getStat(PlayerStat.EVENT_WINS), "&4Team: &f" + (profile.getTeam() == null ? "NONE" : profile.getTeam().getDisplayName()), "&4Server Time: &f" + profile.getServerTime().name(), "&4Kit Menu Type: &f" + profile.getKitMenuType().name(), "").create();
                }
            });

            inventoryMaker.setItem(10, new InventoryMaker.ClickableItem() {
                @Override
                public void onClick(InventoryClickEvent event) {
                    if (!sender.isOp()) {
                        sender.sendMessage(ColorText.translate("&cYou must be an operator."));
                        return;
                    }
                    if (event.isRightClick()) {
                        profile.incrementStat(PlayerStat.DEATHS);
                        KitPvPUtils.getOnlineStaff().forEach(player -> player.sendMessage(ColorText.translate("&7" + sender.getName() + ": &cAdded a death")));
                    } else {
                        int deaths = profile.getStat(PlayerStat.DEATHS) - 1;
                        profile.setStat(PlayerStat.DEATHS, (deaths < 0 ? 0 : deaths));
                        KitPvPUtils.getOnlineStaff().forEach(player -> player.sendMessage(ColorText.translate("&7" + sender.getName() + ": &aRemoved one death")));
                    }
                    if (!target.isOnline()) {
                        ProfileManager.saveProfile(profile, false);
                    }
                }

                @Override
                public ItemStack getItemStack() {
                    return new ItemMaker(Material.IRON_SWORD).setDisplayname("&cDeaths &7> &f" + profile.getStat(PlayerStat.DEATHS)).addLore("&aRight click to increment", "&cLeft click to reduce").create();
                }
            });

            inventoryMaker.setItem(19, new InventoryMaker.ClickableItem() {
                @Override
                public void onClick(InventoryClickEvent event) {
                    if (!sender.isOp()) {
                        sender.sendMessage(ColorText.translate("&cYou must be an operator."));
                        return;
                    }
                    if (event.isRightClick()) {
                        profile.incrementStat(PlayerStat.KILLS);
                        KitPvPUtils.getOnlineStaff().forEach(player -> player.sendMessage(ColorText.translate("&7" + sender.getName() + ": &cAdded a kill")));
                    } else {
                        int kills = profile.getStat(PlayerStat.KILLS) - 1;
                        profile.setStat(PlayerStat.KILLS, (kills < 0 ? 0 : kills));
                        KitPvPUtils.getOnlineStaff().forEach(player -> player.sendMessage(ColorText.translate("&7" + sender.getName() + ": &aRemoved one kill")));
                    }
                    if (!target.isOnline()) {
                        ProfileManager.saveProfile(profile, false);
                    }
                }

                @Override
                public ItemStack getItemStack() {
                    return new ItemMaker(Material.DIAMOND_SWORD).setDisplayname("&aKills &7> &f" + profile.getStat(PlayerStat.KILLS)).addLore("&aRight click to increment", "&cLeft click to reduce").create();
                }
            });

            inventoryMaker.setItem(28, new InventoryMaker.ClickableItem() {
                @Override
                public void onClick(InventoryClickEvent event) {
                }

                @Override
                public ItemStack getItemStack() {
                    return new ItemMaker(Material.BOW).setDisplayname("&dHighest KillStreak &7> &f" + profile.getStat(PlayerStat.HIGHEST_STREAK)).addLore("&7You can't modify this").create();
                }
            });

            inventoryMaker.setItem(37, new InventoryMaker.ClickableItem() {
                @Override
                public void onClick(InventoryClickEvent event) {
                    if (!sender.isOp()) {
                        sender.sendMessage(ColorText.translate("&cYou must be an operator."));
                        return;
                    }
                    if (event.isRightClick()) {
                        profile.incrementStat(PlayerStat.CREDITS);
                        KitPvPUtils.getOnlineStaff().forEach(player -> player.sendMessage(ColorText.translate("&7" + sender.getName() + ": &cAdded a credit")));
                    } else {
                        int credits = profile.getStat(PlayerStat.CREDITS) - 1;
                        profile.setStat(PlayerStat.CREDITS, (credits < 0 ? 0 : credits));
                        KitPvPUtils.getOnlineStaff().forEach(player -> player.sendMessage(ColorText.translate("&7" + sender.getName() + ": &aRemoved one credit")));
                    }
                    if (!target.isOnline()) {
                        ProfileManager.saveProfile(profile, false);
                    }
                }

                @Override
                public ItemStack getItemStack() {
                    return new ItemMaker(Material.MAGMA_CREAM).setDisplayname("&6Credits &7> &f" + profile.getStat(PlayerStat.CREDITS)).addLore("&aRight click to increment", "&cLeft click to reduce").create();
                }
            });

            ((Player) sender).openInventory(inventoryMaker.getCurrentPage());
        }
    }
}