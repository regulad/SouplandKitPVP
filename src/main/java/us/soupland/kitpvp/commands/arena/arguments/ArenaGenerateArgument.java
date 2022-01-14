package us.soupland.kitpvp.commands.arena.arguments;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.soupland.kitpvp.KitPvP;
import us.soupland.kitpvp.practice.arena.ArenaGenerator;
import us.soupland.kitpvp.practice.arena.Schematic;
import us.soupland.kitpvp.utilities.chat.ColorText;
import us.soupland.kitpvp.utilities.command.KitPvPArgument;
import us.soupland.kitpvp.utilities.task.TaskUtil;

import java.io.File;
import java.util.Objects;

public class ArenaGenerateArgument extends KitPvPArgument {

    public ArenaGenerateArgument() {
        super("generate");
    }

    @Override
    public String getUsage(String s) {
        return '/' + s + ' ' + name;
    }

    @Override
    public void onExecute(CommandSender sender, String label, String[] args) {
        File file = new File(KitPvP.getInstance().getDataFolder().getPath() + File.separator + "schematics");
        if (!file.exists()) {
            sender.sendMessage(ColorText.translateAmpersand("&cThe schematics folder doesn't exists."));
            return;
        }

        for (File faggot : Objects.requireNonNull(file.listFiles())) {
            if (faggot.isDirectory() || !faggot.getName().endsWith(".schematic")) {
                continue;
            }
            String name = faggot.getName().replace(".schematic", "");

            TaskUtil.runTask(() -> {
                try {
                    new ArenaGenerator(name, new Schematic(faggot), Bukkit.getWorld("arenas")).generate(faggot);
                } catch (Exception ignored) {
                    ignored.printStackTrace();
                    System.out.println("[ArenaGenerator] Arena named '" + name + "' could not be loaded.");
                }
            });
        }

        sender.sendMessage(ColorText.translateAmpersand("&aGenerating arenas."));
        if (sender instanceof Player) {
            sender.sendMessage(ColorText.translateAmpersand("&aSee console for details please!"));
        }
    }
}