package us.soupland.kitpvp.commands.arena.arguments;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.soupland.kitpvp.utilities.chat.ColorText;
import us.soupland.kitpvp.utilities.command.KitPvPArgument;

public class ArenaGenHelpArgument extends KitPvPArgument {

    public ArenaGenHelpArgument() {
        super("generatehelp", null, null, "genhelp");
        onlyplayers = true;
    }

    @Override
    public String getUsage(String s) {
        return '/' + s + ' ' + name;
    }

    @Override
    public void onExecute(CommandSender sender, String label, String[] args) {
        Player player = (Player) sender;
        Block block = player.getLocation().getBlock();
        Block upBlock = block.getRelative(BlockFace.UP);
        block.setType(Material.SPONGE);
        upBlock.setType(Material.SIGN_POST);
        if (upBlock.getState() instanceof Sign) {
            Sign sign = (Sign) upBlock.getState();
            sign.setLine(0, String.valueOf(((int) player.getLocation().getPitch())));
            sign.setLine(1, String.valueOf(((int) player.getLocation().getYaw())));
            sign.update();

            player.sendMessage(ColorText.translate("&aGenerator helper successfully placed!"));
        }
    }
}