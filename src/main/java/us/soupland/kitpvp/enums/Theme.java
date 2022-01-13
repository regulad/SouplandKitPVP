package us.soupland.kitpvp.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Theme {

    DEFAULT("&4", "&f"), RED("&c", "&f"), GOLD("&6", "&f"), GREEN("&a", "&f"), GREEN2("&2", "&f"), AQUA("&b", "&f"),
    YELLOW("&e", "&f"), PINK("&d", "&f"), PURPLE("&5", "&f"), PURPLEPINK("&5", "&d"),
    PEACH("&d", "&e"), BLUE("&9", "&f"),
    APPLE("&c", "&e"), REDANDGOLD("&c", "&6"), LEMON("&e", "&6"), INVERTED("&b", "&6"),
    COTTONCANDY("&b", "&d"), AZURE("&9", "&1"),
    GREEN3("&2", "&a"), BLUELIGHTBLUE("&3", "&b"),
    GOLDRED("&6", "&c"), GOLDAQUA("&6", "&b"), RAGE("&4", "&c");

    private String primaryColor, secondaryColor;

    public String getPermission() {
        if (this == DEFAULT) {
            return null;
        }
        return "soupland.theme." + name().toLowerCase();
    }

}