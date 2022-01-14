package us.soupland.kitpvp.utilities.chat;

import org.bukkit.ChatColor;

import java.util.List;

public class ColorText {

    public static String translate(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static List<String> translate(List<String> message) {
        for (int i = 0; i < message.size(); i++) {
            message.set(i, translate(message.get(i)));
        }
        return message;
    }
}