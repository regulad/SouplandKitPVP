package us.soupland.kitpvp.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import us.soupland.kitpvp.KitPvP;
import us.soupland.kitpvp.utilities.item.ItemMaker;
import us.soupland.kitpvp.utilities.player.PlayerUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
@Getter
public enum PlayerItem {

    SPAWN_KIT_ITEM(new ItemMaker(Material.BOOK).setDisplayname("&4&lKits &7(Right Click)").addLore("&4Bound").setInteractRight(player -> player.performCommand("kits")).create()),
    SPAWN_PREVIOUS_ITEM(new ItemMaker(Material.WATCH).setDisplayname("&b&lPrevious Kit &7(%NAME%)").addLore("&4Bound").create()),
    SPAWN_LEADERBOARD_ITEM(new ItemMaker(Material.EMERALD).setDisplayname("&a&lLeaderboard &7(Right Click)").addLore("&4Bound").setInteractRight(player -> player.performCommand("leaderboard")).create()),
    SPAWN_MENU_ITEM(new ItemMaker(Material.ENDER_CHEST).setDisplayname("&c&lPlayer Menu &7(Right Click)").addLore("&4Bound").setInteractRight(player -> player.performCommand("menu")).create()),
    SPAWN_HOST_ITEM(new ItemMaker(Material.NETHER_STAR).setDisplayname("&6&lHost Event &7(Right Click)").addLore("&4Bound").setInteractRight(player -> player.performCommand("events host")).create()),
    SPAWN_ARENA_ITEM(new ItemMaker(Material.DIAMOND_SWORD).setDisplayname("&b&l1v1 &7(Right Click)").addLore("&4Bound").setInteractRight(player -> player.performCommand("onevsone")).create()),
    SPAWN_EVENT_ITEM(new ItemMaker(Material.REDSTONE_TORCH_ON).setDisplayname("&d&lEvent Tracker &7(Right Click)").addLore("&4Bound").setInteractRight(player -> player.performCommand("game " + (KitPvP.getInstance().getGameHandler().getActiveGame() == null ? "join" : "spectate"))).create()),
    SPAWN_ACHIEVEMENTS_ITEM(new ItemMaker(Material.FISHING_ROD).setDisplayname("&b&lAchievements &7(¡Click me!)").addLore("&4Bound").setInteractRight(player -> player.performCommand("achievements")).create()),
    SPAWN_PRACTICE_QUEUE_JOIN(new ItemMaker(Material.INK_SACK).setDurability(8).setDisplayname("&7Quick Queue (Right Click)").create()),
    SPAWN_PRACTICE_QUEUE_LEAVE(new ItemMaker(Material.INK_SACK).setDurability(8).setDisplayname("&aSearching for players...").create()),
    SPAWN_PRACTICE_CUSTOM_DUEL(new ItemMaker(Material.BLAZE_ROD).setDisplayname("&6Custom Duel &7(Right Click Opponent)").create()),
    SPAWN_PRACTICE_RETURN_SPAWN(new ItemMaker(Material.INK_SACK).setDurability(1).setDisplayname("&cReturn to Spawn").setInteractRight(player -> PlayerUtils.resetPlayer(player, false, true)).create()),
    EVENT_LEAVE_ITEM(new ItemMaker(Material.INK_SACK).setDurability(1).setDisplayname("&c&lLeave &7(Right Click)").addLore("&4Bound").setInteractRight(player -> player.performCommand("game leave")).create());

    private ItemStack item;
}