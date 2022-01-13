package us.soupland.kitpvp.leaderboard;

import com.google.common.collect.Lists;
import us.soupland.kitpvp.KitPvP;
import us.soupland.kitpvp.enums.PlayerStat;
import us.soupland.kitpvp.games.Game;
import us.soupland.kitpvp.utilities.inventory.InventoryMaker;
import us.soupland.kitpvp.profile.Profile;
import us.soupland.kitpvp.profile.ProfileManager;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import us.soupland.kitpvp.utilities.chat.ColorText;
import us.soupland.kitpvp.utilities.item.ItemMaker;

import java.util.List;

public class LeaderboardMenu {

    public static void openMenu(Player player) {

        Profile profile = ProfileManager.getProfile(player);

        InventoryMaker inventoryMaker = new InventoryMaker(ColorText.translate(profile.getTheme().getPrimaryColor() + "Leaderboards"), 5);

        inventoryMaker.setItem(10, new InventoryMaker.ClickableItem() {
            @Override
            public void onClick(InventoryClickEvent inventoryClickEvent) {

            }

            @Override
            public ItemStack getItemStack() {

                List<Profile> profiles = KitPvP.getInstance().getLeaderboardManager().getGlobalStats().get("KILLS");

                List<String> lore = Lists.newArrayList();

                int pos = 1;

                for (Profile playerData : profiles) {
                    OfflinePlayer player1 = Bukkit.getOfflinePlayer(playerData.getUuid());
                    lore.add(ColorText.translate(profile.getTheme().getPrimaryColor() + (pos == 1 || pos == 2 ? "&l" : pos == 3 ? "&o" : "") + pos + ' '+ player1.getName() + "&7: &f" + playerData.getStat(PlayerStat.KILLS)));
                    pos++;
                }

                lore.add(0, "&7&m" + StringUtils.repeat("-", 40));
                lore.add("&7&m" + StringUtils.repeat("-", 40));

                return new ItemMaker(Material.DIAMOND_SWORD).setDisplayname("&a&lTop Kills").addLore(lore).create();
            }
        });

        inventoryMaker.setItem(11, new InventoryMaker.ClickableItem() {
            @Override
            public void onClick(InventoryClickEvent inventoryClickEvent) {

            }

            @Override
            public ItemStack getItemStack() {

                List<Profile> profiles = KitPvP.getInstance().getLeaderboardManager().getGlobalStats().get("DEATHS");

                List<String> lore = Lists.newArrayList();

                int pos = 1;

                for (Profile playerData : profiles) {
                    OfflinePlayer player1 = Bukkit.getOfflinePlayer(playerData.getUuid());
                    lore.add(ColorText.translate(profile.getTheme().getPrimaryColor() + (pos == 1 || pos == 2 ? "&l" : pos == 3 ? "&o" : "") + pos + ' '  + player1.getName() + "&7: &f" + playerData.getStat(PlayerStat.DEATHS)));
                    pos++;
                }

                lore.add(0, "&7&m" + StringUtils.repeat("-", 40));
                lore.add("&7&m" + StringUtils.repeat("-", 40));

                return new ItemMaker(Material.SKULL_ITEM).setDisplayname("&c&lTop Deaths").addLore(lore).create();
            }
        });

        inventoryMaker.setItem(13, new InventoryMaker.ClickableItem() {
            @Override
            public void onClick(InventoryClickEvent inventoryClickEvent) {

            }

            @Override
            public ItemStack getItemStack() {
                List<Profile> profiles = KitPvP.getInstance().getLeaderboardManager().getGlobalStats().get("CREDITS");

                List<String> lore = Lists.newArrayList();

                int pos = 1;

                for (Profile playerData : profiles) {
                    OfflinePlayer player1 = Bukkit.getOfflinePlayer(playerData.getUuid());
                    lore.add(ColorText.translate(profile.getTheme().getPrimaryColor() + (pos == 1 || pos == 2 ? "&l" : pos == 3 ? "&o" : "") + pos + ' '+ player1.getName() + "&7: &f" + playerData.getStat(PlayerStat.HIGHEST_STREAK)));
                    pos++;
                }

                lore.add(0, "&7&m" + StringUtils.repeat("-", 40));
                lore.add("&7&m" + StringUtils.repeat("-", 40));

                return new ItemMaker(Material.GOLD_INGOT).setDisplayname("&6&lCredits").addLore(lore).create();
            }
        });

        inventoryMaker.setItem(15, new InventoryMaker.ClickableItem() {
            @Override
            public void onClick(InventoryClickEvent inventoryClickEvent) {

            }

            @Override
            public ItemStack getItemStack() {
                List<Profile> profiles = KitPvP.getInstance().getLeaderboardManager().getGlobalStats().get("HIGHEST_STREAK");

                List<String> lore = Lists.newArrayList();

                int pos = 1;

                for (Profile playerData : profiles) {
                    OfflinePlayer player1 = Bukkit.getOfflinePlayer(playerData.getUuid());
                    lore.add(ColorText.translate(profile.getTheme().getPrimaryColor() + (pos == 1 || pos == 2 ? "&l" : pos == 3 ? "&o" : "") + pos + ' ' + player1.getName() + "&7: &f" + playerData.getStat(PlayerStat.HIGHEST_STREAK)));
                    pos++;
                }

                lore.add(0, "&7&m" + StringUtils.repeat("-", 40));
                lore.add("&7&m" + StringUtils.repeat("-", 40));

                return new ItemMaker(Material.GOLD_SWORD).setDisplayname("&e&lTop Highest Streak").addLore(lore).create();
            }
        });

        inventoryMaker.setItem(16, new InventoryMaker.ClickableItem() {
            @Override
            public void onClick(InventoryClickEvent inventoryClickEvent) {

            }

            @Override
            public ItemStack getItemStack() {

                List<Profile> profiles = KitPvP.getInstance().getLeaderboardManager().getGlobalStats().get("KDR");

                List<String> lore = Lists.newArrayList();

                int pos = 1;

                for (Profile playerData : profiles) {
                    OfflinePlayer player1 = Bukkit.getOfflinePlayer(playerData.getUuid());
                    lore.add(ColorText.translate(profile.getTheme().getPrimaryColor() + (pos == 1 || pos == 2 ? "&l" : pos == 3 ? "&o" : "") + pos + ' ' + player1.getName() + "&7: &f" + playerData.getKdr()));
                    pos++;
                }

                lore.add(0, "&7&m" + StringUtils.repeat("-", 40));
                lore.add("&7&m" + StringUtils.repeat("-", 40));

                return new ItemMaker(Material.IRON_SWORD).setDisplayname("&b&lTop K/D Ratio").addLore(lore).create();
            }
        });


        int pos = 28;

        for (Game event : KitPvP.getInstance().getGameHandler().getGames()) {
            pos++;
            if (pos == 18) {
                pos += 2;
            }
            if (pos == 26) {
                pos += 2;
            }
            if (pos == 35) {
                pos += 2;
            }


            inventoryMaker.setItem(pos, new InventoryMaker.ClickableItem() {
                @Override
                public void onClick(InventoryClickEvent inventoryClickEvent) {
                }

                @Override
                public ItemStack getItemStack() {

                    List<Profile> profiles = KitPvP.getInstance().getLeaderboardManager().getListByEvent(event);

                    List<String> lore = Lists.newArrayList();

                    int pos = 1;

                    for (Profile playerData : profiles) {
                        OfflinePlayer player1 = Bukkit.getOfflinePlayer(playerData.getUuid());
                        lore.add(ColorText.translate(profile.getTheme().getPrimaryColor() + (pos == 1 || pos == 2 ? "&l" : pos == 3 ? "&o" : "") + pos + ' ' + player1.getName() + "&7: &f" + playerData.getEventsWin().getOrDefault(event.getName(), 0)));
                        ;
                        pos++;
                    }

                    lore.add(0, "&7&m" + StringUtils.repeat("-", 40));
                    lore.add("&7&m" + StringUtils.repeat("-", 40));

                    return new ItemMaker(event.getItemStack()).setDisplayname("&e&l" + event.getName() + " &eTop wins").addLore(lore).create();
                }
            });
        }

        player.openInventory(inventoryMaker.getCurrentPage());
    }


}
