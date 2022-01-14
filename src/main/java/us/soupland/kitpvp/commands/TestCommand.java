package us.soupland.kitpvp.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import us.soupland.kitpvp.KitPvP;
import us.soupland.kitpvp.kits.KitHandler;
import us.soupland.kitpvp.utilities.KitPvPUtils;
import us.soupland.kitpvp.utilities.command.KitPvPCommand;
import us.soupland.kitpvp.utilities.configuration.Config;

import java.util.List;
import java.util.stream.Collectors;

public class TestCommand extends KitPvPCommand {

    public TestCommand() {
        super("test");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(KitPvPUtils.ONLY_PLAYERS);
            return false;
        }
        Player player = (Player) sender;

        Config config = KitPvP.getInstance().getKitConfig();

        ConfigurationSection section = config.createSection("Kits");

        KitHandler.getKitList().forEach(kit -> {
            for (PotionEffect effect : player.getActivePotionEffects()) {
                player.removePotionEffect(effect.getType());
            }
            kit.onLoad(player);
            ConfigurationSection sectionKit = section.createSection(kit.getName());
            sectionKit.set("name", kit.getDisplayName());
            sectionKit.set("display-item", kit.getItem().getType().name());
            sectionKit.set("description", kit.getDescription());
            sectionKit.set("credits", kit.getCreditCost());
            sectionKit.set("cooldown", kit.getCooldown());
            List<String> effects = kit.getEffects().stream().map(potionEffect -> potionEffect.getType().getName()).collect(Collectors.toList());
            sectionKit.set("effects", effects);
            ConfigurationSection armorSelection = sectionKit.createSection("armor");
            for (ItemStack armor : kit.getPlayerInventory()) {
                if (armor.getType().name().contains("HELMET")) {
                    armorSelection.set("helmet", armor.getType().name());
                } else if (armor.getType().name().contains("CHESTPLATE")) {
                    armorSelection.set("chestplate", armor.getType().name());
                } else if (armor.getType().name().contains("LEGGINGS")) {
                    armorSelection.set("leggins", armor.getType().name());
                } else if (armor.getType().name().contains("BOOTS")) {
                    armorSelection.set("boots", armor.getType().name());
                }
            }

            ConfigurationSection inventorySelection = sectionKit.createSection("inventory");
            int pos = 0;
            for (ItemStack item : kit.getInventory()) {
                inventorySelection.set(String.valueOf(pos), item.getType().name());
                pos++;
            }


        });

        config.save();

        return true;
    }
}