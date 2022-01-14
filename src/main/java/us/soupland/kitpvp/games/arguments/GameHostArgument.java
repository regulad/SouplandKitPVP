package us.soupland.kitpvp.games.arguments;

import org.apache.commons.lang.WordUtils;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import us.soupland.kitpvp.KitPvP;
import us.soupland.kitpvp.enums.Achievement;
import us.soupland.kitpvp.enums.Theme;
import us.soupland.kitpvp.games.Game;
import us.soupland.kitpvp.games.GameHandler;
import us.soupland.kitpvp.games.arenas.GameMap;
import us.soupland.kitpvp.profile.Profile;
import us.soupland.kitpvp.profile.ProfileManager;
import us.soupland.kitpvp.utilities.KitPvPUtils;
import us.soupland.kitpvp.utilities.chat.ChatUtil;
import us.soupland.kitpvp.utilities.chat.ColorText;
import us.soupland.kitpvp.utilities.command.KitPvPArgument;
import us.soupland.kitpvp.utilities.inventory.InventoryMaker;
import us.soupland.kitpvp.utilities.item.ItemMaker;

import java.util.ArrayList;
import java.util.List;

public class GameHostArgument extends KitPvPArgument {

    public GameHostArgument() {
        super("host", "Host an Event");
        onlyplayers = true;
    }

    @Override
    public String getUsage(String s) {
        return '/' + s + ' ' + name;
    }

    @Override
    public void onExecute(CommandSender sender, String label, String[] args) {
        Player player = (Player) sender;
        InventoryMaker inventoryMaker = new InventoryMaker("&4&lHost an Event", 4);
        GameHandler gameHandler = KitPvP.getInstance().getGameHandler();
        Profile profile = ProfileManager.getProfile(player);

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
                return new ItemMaker(Material.NETHER_STAR).setDisplayname("&6&lEvents").create();
            }
        });

        int pos = 9;
        for (Game game : gameHandler.getGames()) {
            inventoryMaker.setItem(pos++, new InventoryMaker.ClickableItem() {
                @Override
                public void onClick(InventoryClickEvent inventoryClickEvent) {
                    player.closeInventory();
                    if (profile.getPlayerCombat() > 0L) {
                        player.sendMessage(ColorText.translateAmpersand("&cYou must not be Spawn-Tagged."));
                        return;
                    }
                    if (game.getPermission() == null || player.hasPermission(game.getPermission()) || KitPvP.getInstance().getServerData().isFreeEventsMode() || profile.getGamesPurchased().contains(game)) {
                        if (gameHandler.getCooldown().containsKey(game) && (gameHandler.getCooldown().get(game) - System.currentTimeMillis()) > 0L && !player.isOp()) {
                            player.sendMessage(ColorText.translateAmpersand("&cThis events is on cooldown for another &e" + DurationFormatUtils.formatDurationWords(gameHandler.getCooldown().get(game) - System.currentTimeMillis(), true, true) + "&c."));
                            return;
                        }
                        if (gameHandler.getActiveGame() != null || gameHandler.getUpcomingGame() != null) {
                            player.sendMessage(ColorText.translateAmpersand("&cThere is already an active events ongoing!"));
                        } else {
                            List<GameMap> gameMaps = new ArrayList<>();
                            for (GameMap gameMap : KitPvP.getInstance().getGameMapHandler().getGameMap().values()) {
                                if (gameMap.getGame().toLowerCase().startsWith(game.getName().toLowerCase().replace(" ", ""))) {
                                    gameMaps.add(gameMap);
                                }
                            }
                            if (gameMaps.isEmpty()) {
                                player.sendMessage(ColorText.translateAmpersand("&cThere are no maps available for this events."));
                            } else {
                                if (player.hasPermission(KitPvPUtils.DONATOR_TOP_PERMISSION)) {
                                    InventoryMaker maker = new InventoryMaker("&4Select Map", 2);

                                    for (GameMap map : gameMaps) {
                                        maker.addItem(new InventoryMaker.ClickableItem() {
                                            @Override
                                            public void onClick(InventoryClickEvent inventoryClickEvent) {
                                                player.closeInventory();
                                                game.setArena(map);
                                                createGame(player, game, profile);
                                            }

                                            @Override
                                            public ItemStack getItemStack() {
                                                return new ItemMaker(Material.PAPER).setDisplayname(profile.getTheme().getPrimaryColor() + WordUtils.capitalize(map.getGame().replace(game.getName().toLowerCase().replace(" ", ""), ""))).addLore("", "&7Click here to select this map", "", "&a&nClick here", "").create();
                                            }
                                        });
                                    }

                                    maker.setItem(17, new InventoryMaker.ClickableItem() {
                                        @Override
                                        public void onClick(InventoryClickEvent inventoryClickEvent) {
                                            player.closeInventory();
                                            game.setArena(gameMaps.get(KitPvPUtils.getRandomNumber(gameMaps.size())));
                                            createGame(player, game, profile);
                                        }

                                        @Override
                                        public ItemStack getItemStack() {
                                            return new ItemMaker(Material.QUARTZ).setDisplayname("&6Random Map").addLore("", "&7Click here to select a random map", "", "&a&nClick here", "").create();
                                        }
                                    });

                                    player.openInventory(maker.getCurrentPage());
                                } else {
                                    createGame(player, game, profile);
                                }
                            }
                        }
                    } else {
                        player.sendMessage(ColorText.translateAmpersand("&cYou don't have permissions to host this events."));
                    }
                }

                @Override
                public ItemStack getItemStack() {
                    List<String> lore = new ArrayList<>();
                    lore.add("&7&m" + StringUtils.repeat("-", 25));
                    lore.add("");
                    for (String description : game.getDescription().split("\n")) {
                        lore.add(profile.getTheme().getPrimaryColor() + description);
                    }
                    lore.add("");
                    if (game.getPermission() == null || player.hasPermission(game.getPermission()) || KitPvP.getInstance().getServerData().isFreeEventsMode() || profile.getGamesPurchased().contains(game)) {
                        if (gameHandler.getCooldown().containsKey(game) && (gameHandler.getCooldown().get(game) - System.currentTimeMillis()) > 0L) {
                            lore.add(profile.getTheme().getPrimaryColor() + "&lEvent Cooldown:");
                            lore.add(profile.getTheme().getSecondaryColor() + DurationFormatUtils.formatDurationWords(gameHandler.getCooldown().get(game) - System.currentTimeMillis(), true, true));
                        }
                        if (KitPvP.getInstance().getServerData().isFreeEventsMode()) {
                            if (gameHandler.getCooldown().containsKey(game) && (gameHandler.getCooldown().get(game) - System.currentTimeMillis()) > 0L) {
                                lore.add("");
                            }
                            lore.add("&7This events is &a&lFREE &7because");
                            lore.add("&d&lFREE EVENTS MODE &7is &aenabled&7!");
                        }
                    } else {
                        lore.add("&cYou don't have perms to host this event!");
                    }
                    lore.add("&7&m" + StringUtils.repeat("-", 25));
                    return new ItemMaker(game.getItem()).setDisplayname((KitPvP.getInstance().getServerData().isFreeEventsMode() ? "&a&l[FREE] " : "") + profile.getTheme().getPrimaryColor() + "&l" + game.getName()).addLore(lore).create();
                }
            }, player, true);
        }

        player.openInventory(inventoryMaker.getCurrentPage());
    }

    private void createGame(Player player, Game game, Profile profile) {
        KitPvP.getInstance().getGameHandler().startGame(game, 60, player);
        KitPvP.getInstance().getGameHandler().join(player);
        profile.setCurrentKit(null);

        for (Player online : Bukkit.getOnlinePlayers()) {
            Theme theme = ProfileManager.getProfile(online).getTheme();
            online.sendMessage(" ");
            online.sendMessage(ColorText.translateAmpersand(theme.getSecondaryColor() + StringUtils.repeat("\u2588", 9)));
            online.sendMessage(ColorText.translateAmpersand(theme.getSecondaryColor() + "\u2588" + theme.getPrimaryColor() + StringUtils.repeat("\u2588", 7) + theme.getSecondaryColor() + "\u2588 &7[" + game.getName() + " Event]"));
            online.sendMessage(ColorText.translateAmpersand(theme.getSecondaryColor() + "\u2588" + theme.getPrimaryColor() + "\u2588" + theme.getSecondaryColor() + StringUtils.repeat("\u2588", 7) + ' ' + player.getName() + " &7is hosting a minigame!"));
            online.sendMessage(ColorText.translateAmpersand(theme.getSecondaryColor() + "\u2588" + theme.getPrimaryColor() + "\u2588" + theme.getSecondaryColor() + StringUtils.repeat("\u2588", 7) + " &7Starts in " + theme.getPrimaryColor() + "1 minute"));
            online.sendMessage(ColorText.translateAmpersand(theme.getSecondaryColor() + "\u2588" + theme.getPrimaryColor() + StringUtils.repeat("\u2588", 7) + theme.getSecondaryColor() + "\u2588"));
            new ChatUtil(theme.getSecondaryColor() + "\u2588" + theme.getPrimaryColor() + "\u2588" + theme.getSecondaryColor() + StringUtils.repeat("\u2588", 7) + " &a&l[Click to join]", "&7Click here to join!", "/game join").send(online);
            online.sendMessage(ColorText.translateAmpersand(theme.getSecondaryColor() + "\u2588" + theme.getPrimaryColor() + "\u2588" + theme.getSecondaryColor() + StringUtils.repeat("\u2588", 7)));
            online.sendMessage(ColorText.translateAmpersand(theme.getSecondaryColor() + "\u2588" + theme.getPrimaryColor() + StringUtils.repeat("\u2588", 7) + theme.getSecondaryColor() + "\u2588"));
            online.sendMessage(ColorText.translateAmpersand(theme.getSecondaryColor() + StringUtils.repeat("\u2588", 9)));
            online.sendMessage(" ");
        }

        if (!profile.getAchievements().contains(Achievement.HOSTER)) {
            profile.getAchievements().add(Achievement.HOSTER);
            Achievement.HOSTER.broadcast(player);
        }

        if (KitPvP.getInstance().getServerData().isFreeEventsMode()) {
            return;
        }

        if (profile.getGamesPurchased().remove(game)) {
            new ChatUtil("&eYou have used your &b" + game.getName() + " Event&e. &7(Click here to purchase again)", "&7Click to purchase a new Event", "/shop").send(player);
        }
    }
}