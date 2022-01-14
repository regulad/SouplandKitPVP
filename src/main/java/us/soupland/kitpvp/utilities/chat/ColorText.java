package us.soupland.kitpvp.utilities.chat;

import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ColorText {
    private ColorText() {
    }

    public static @NotNull String translateAmpersand(final @NotNull String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static List<String> translateAmpersand(List<String> message) {
        for (int i = 0; i < message.size(); i++) {
            message.set(i, translateAmpersand(message.get(i)));
        }
        return message;
    }
}