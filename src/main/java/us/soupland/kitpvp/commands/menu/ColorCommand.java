package us.soupland.kitpvp.commands.menu;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import us.soupland.kitpvp.profile.Profile;
import us.soupland.kitpvp.profile.ProfileManager;
import us.soupland.kitpvp.utilities.KitPvPUtils;
import us.soupland.kitpvp.utilities.WoolUtils;
import us.soupland.kitpvp.utilities.chat.ColorText;
import us.soupland.kitpvp.utilities.command.KitPvPCommand;
import us.soupland.kitpvp.utilities.inventory.InventoryMaker;
import us.soupland.kitpvp.utilities.item.ItemMaker;

public class ColorCommand extends KitPvPCommand {

    public ColorCommand() {
        super("color");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(KitPvPUtils.ONLY_PLAYERS);
            return false;
        }
        Player player = (Player) sender;
        Profile profile = ProfileManager.getProfile(player);
        InventoryMaker inventoryMaker = new InventoryMaker("&a&lSelect a Color", 2);

        for (ChatColor color : ChatColor.values()) {
            if (isInvalid(color)) {
                continue;
            }

            inventoryMaker.addItem(new InventoryMaker.ClickableItem() {
                @Override
                public void onClick(InventoryClickEvent inventoryClickEvent) {
                    if (!player.hasPermission("soupland.chatcolor." + color.name().toLowerCase().replace("_", ""))) {
                        return;
                    }

                    if (profile.getChatColor() == color) {
                        return;
                    }

                    player.closeInventory();

                    profile.setChatColor(color);
                    player.sendMessage(ColorText.translate("&eChat color updated."));
                }

                @Override
                public ItemStack getItemStack() {
                    return new ItemMaker(WoolUtils.getWool(color)).setDisplayname((profile.getChatColor() == color ? "&7[Selected] " : "") + color + color.name().replace("_", " ")).addLore((profile.getChatColor() == color ? "&aColor selected." : player.hasPermission("chatcolor." + color.name().toLowerCase()) ? "&aClick to activate!" : "&cYou don't own this color.")).create();
                }
            });
        }

        player.openInventory(inventoryMaker.getCurrentPage());
        return true;
    }

    private boolean isInvalid(ChatColor color) {
        return color == ChatColor.UNDERLINE || color == ChatColor.BLACK || color == ChatColor.BOLD || color == ChatColor.RESET || color == ChatColor.DARK_BLUE || color == ChatColor.ITALIC || color == ChatColor.MAGIC || color == ChatColor.STRIKETHROUGH || color == ChatColor.DARK_GRAY;
    }
}