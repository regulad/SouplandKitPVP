package us.soupland.kitpvp.commands;

import us.soupland.kitpvp.KitPvP;
import us.soupland.kitpvp.utilities.inventory.InventoryMaker;
import us.soupland.kitpvp.server.ServerData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import us.soupland.kitpvp.utilities.KitPvPUtils;
import us.soupland.kitpvp.utilities.chat.ColorText;
import us.soupland.kitpvp.utilities.command.KitPvPCommand;
import us.soupland.kitpvp.utilities.item.ItemMaker;

public class EventsCommand extends KitPvPCommand {

    public EventsCommand() {
        super("hostevent");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(KitPvPUtils.ONLY_PLAYERS);
            return false;
        }
        Player player = (Player) sender;
        ServerData data = KitPvP.getInstance().getServerData();

        InventoryMaker inventoryMaker = new InventoryMaker("&6&lEvents", 1);

        inventoryMaker.setItem(2, new InventoryMaker.ClickableItem() {
            @Override
            public void onClick(InventoryClickEvent inventoryClickEvent) {
                player.closeInventory();
                data.setDoubleCredits(!data.isDoubleCredits());
                Bukkit.broadcastMessage(ColorText.translate("&a&lDOUBLE CREDITS &ehas been " + (data.isDoubleCredits() ? "&a&lENABLED" : "&c&lDISABLED") + " &eby " + player.getName() + "&e" + (data.isDoubleCredits() ? " for unlimited time" : "") + '.'));
                Bukkit.getOnlinePlayers().forEach(o -> o.playSound(o.getLocation(), Sound.NOTE_PLING, 1, 1));
            }

            @Override
            public ItemStack getItemStack() {
                return new ItemMaker(Material.MAGMA_CREAM).setDisplayname("&a&lDOUBLE CREDITS").addLore("&7Click here to toggle").create();
            }
        });

        inventoryMaker.setItem(4, new InventoryMaker.ClickableItem() {
            @Override
            public void onClick(InventoryClickEvent inventoryClickEvent) {
                player.closeInventory();
                data.setFreeKitsMode(!data.isFreeKitsMode());
                Bukkit.broadcastMessage(ColorText.translate("&b&lFREE KITS MODE &ehas been " + (data.isFreeKitsMode() ? "&a&lENABLED" : "&c&lDISABLED") + " &eby " + player.getName() + "&e" + (data.isFreeKitsMode() ? " for unlimited time" : "") + '.'));
                Bukkit.getOnlinePlayers().forEach(o -> o.playSound(o.getLocation(), Sound.NOTE_PLING, 1, 1));
            }

            @Override
            public ItemStack getItemStack() {
                return new ItemMaker(Material.CHEST).setDisplayname("&b&lFREE KITS MODE").addLore("&7Click here to toggle").create();
            }
        });

        inventoryMaker.setItem(6, new InventoryMaker.ClickableItem() {
            @Override
            public void onClick(InventoryClickEvent inventoryClickEvent) {
                player.closeInventory();
                data.setFreeEventsMode(!data.isFreeEventsMode());
                Bukkit.broadcastMessage(ColorText.translate("&d&lFREE EVENTS MODE &ehas been " + (data.isFreeEventsMode() ? "&a&lENABLED" : "&c&lDISABLED") + " &eby " + player.getName() + "&e" + (data.isFreeEventsMode() ? " for unlimited time" : "") + '.'));
                Bukkit.getOnlinePlayers().forEach(o -> o.playSound(o.getLocation(), Sound.NOTE_PLING, 1, 1));
            }

            @Override
            public ItemStack getItemStack() {
                return new ItemMaker(Material.NETHER_STAR).setDisplayname("&d&lFREE EVENTS MODE").addLore("&7Click here to toggle").create();
            }
        });

        player.openInventory(inventoryMaker.getCurrentPage());
        return true;
    }
}