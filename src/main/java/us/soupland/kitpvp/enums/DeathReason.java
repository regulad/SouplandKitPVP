package us.soupland.kitpvp.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import us.soupland.kitpvp.utilities.item.ItemMaker;

@AllArgsConstructor
@Getter
public enum DeathReason {

    OBLITERATED(new ItemMaker(Material.NAME_TAG).setDisplayname("&4&lObliterated").addLore("", "&7When killing a player, they will see:", "&7{PLAYER} obliterated you!", "").create()),
    DEMOLISHED(new ItemMaker(Material.NAME_TAG).setDisplayname("&4&lDemolished").addLore("", "&7When killing a player, they will see:", "&7{PLAYER} demolished you!", "").create()),
    LEGENDARY_SKILLS(new ItemMaker(Material.NAME_TAG).setDisplayname("&4&lLegendary Skills").addLore("", "&7When killing a player, they will see:", "&7{PLAYER} killed you with their legendary skills!", "").create()),
    CLEVERNESS(new ItemMaker(Material.NAME_TAG).setDisplayname("&4&lCleverness").addLore("", "&7When killing a player, they will see:", "&7{PLAYER} slayed you with their cleverness!", "").create()),
    NOOB_SLAYER(new ItemMaker(Material.NAME_TAG).setDisplayname("&4&lNoob Slayer").addLore("", "&7When killing a player, they will see:", "&7{PLAYER} killed you with their noob slaying sword!", "").create()),
    TWOHUNDRED_IQ(new ItemMaker(Material.NAME_TAG).setDisplayname("&4&l200 IQ").addLore("", "&7When killing a player, they will see:", "&7{PLAYER}'s 200 IQ resulted in you being outplayed", "&7and you subsequent death!", "").create()),
    EZ(new ItemMaker(Material.NAME_TAG).setDisplayname("&4&leZ").addLore("", "&7When killing a player, they will see:", "&7{PLAYER} thought you were eZ!", "").create());


    private ItemStack item;

}