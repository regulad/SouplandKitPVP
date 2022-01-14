package us.soupland.kitpvp.commands.arena.arguments;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import us.soupland.kitpvp.practice.arena.Arena;
import us.soupland.kitpvp.practice.arena.ArenaHandler;
import us.soupland.kitpvp.practice.ladder.Ladder;
import us.soupland.kitpvp.practice.ladder.LadderHandler;
import us.soupland.kitpvp.utilities.chat.ColorText;
import us.soupland.kitpvp.utilities.command.KitPvPArgument;
import us.soupland.kitpvp.utilities.inventory.InventoryMaker;
import us.soupland.kitpvp.utilities.item.ItemMaker;

public class ArenaLadderArgument extends KitPvPArgument {

    public ArenaLadderArgument() {
        super("ladder");
        onlyplayers = true;
    }

    @Override
    public String getUsage(String s) {
        return '/' + s + ' ' + name + " <arenaName>";
    }

    @Override
    public void onExecute(CommandSender sender, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ColorText.translateAmpersand("&cUsage: " + getUsage(label)));
        } else {
            Arena arena = ArenaHandler.getByName(args[1]);
            if (arena == null) {
                sender.sendMessage(ColorText.translateAmpersand("&cAn arena with that name doesn't exists."));
                return;
            }
            InventoryMaker inventoryMaker = new InventoryMaker(arena.getName(), 4);

            for (Ladder ladder : LadderHandler.getLadders().values()) {
                inventoryMaker.addItem(new InventoryMaker.ClickableItem() {
                    @Override
                    public void onClick(InventoryClickEvent inventoryClickEvent) {
                        if (arena.getLadders().contains(ladder.getName())) {
                            arena.getLadders().remove(ladder.getName());
                        } else {
                            arena.getLadders().add(ladder.getName());
                        }
                        sender.sendMessage(ColorText.translateAmpersand((arena.getLadders().contains(ladder.getName()) ? "&a" : "&c") + arena.getName() + " &7-> " + ladder.getName()));
                    }

                    @Override
                    public ItemStack getItemStack() {
                        return new ItemMaker(Material.PAPER).setDisplayname((arena.getLadders().contains(ladder.getName()) ? "&a" : "&c") + "&l" + ladder.getName()).create();
                    }
                });
            }

            ((Player) sender).openInventory(inventoryMaker.getCurrentPage());
        }
    }
}