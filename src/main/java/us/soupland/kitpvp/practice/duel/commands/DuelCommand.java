package us.soupland.kitpvp.practice.duel.commands;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import us.soupland.kitpvp.enums.PlayerState;
import us.soupland.kitpvp.practice.arena.Arena;
import us.soupland.kitpvp.practice.arena.ArenaHandler;
import us.soupland.kitpvp.practice.duel.DuelProcedure;
import us.soupland.kitpvp.practice.ladder.Ladder;
import us.soupland.kitpvp.practice.ladder.LadderHandler;
import us.soupland.kitpvp.profile.Profile;
import us.soupland.kitpvp.profile.ProfileManager;
import us.soupland.kitpvp.utilities.KitPvPUtils;
import us.soupland.kitpvp.utilities.chat.ColorText;
import us.soupland.kitpvp.utilities.command.KitPvPCommand;
import us.soupland.kitpvp.utilities.inventory.InventoryMaker;
import us.soupland.kitpvp.utilities.item.ItemMaker;

public class DuelCommand extends KitPvPCommand {

    public DuelCommand() {
        super("duel");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(KitPvPUtils.ONLY_PLAYERS);
            return false;
        }
        Player player = (Player) sender;
        Profile profile = ProfileManager.getProfile(player);
        if (profile.getPlayerState() != PlayerState.SPAWNPRACTICE) {
            player.sendMessage(ColorText.translateAmpersand("&cYou cannot duel right now."));
            return false;
        }
        if (args.length < 1) {
            player.sendMessage(ColorText.translateAmpersand("&cUsage: /" + label + " <playerName>"));
        } else {
            Player target = Bukkit.getPlayer(args[0]);
            if (!KitPvPUtils.isOnline(target)) {
                sender.sendMessage(KitPvPUtils.getPlayerNotFoundMessage(args[0]));
                return false;
            }
            if (target == player) {
                player.sendMessage(ColorText.translateAmpersand("&cYou cannot duel yourself."));
                return false;
            }
            Profile targetProfile = ProfileManager.getProfile(target);
            if (targetProfile.getPlayerState() != PlayerState.SPAWNPRACTICE) {
                player.sendMessage(ColorText.translateAmpersand("&c" + target.getName() + " is currently busy."));
                return false;
            }
            if (!profile.canSendDuelRequest(target)) {
                player.sendMessage(ColorText.translateAmpersand("&cYou have already sent that player a duel request."));
                return false;
            }

            DuelProcedure duelProcedure = new DuelProcedure();

            duelProcedure.setSender(player);
            duelProcedure.setTarget(target);
            profile.setDuelProcedure(duelProcedure);

            InventoryMaker inventoryMaker = new InventoryMaker(profile.getTheme().getPrimaryColor() + "Duel Versus " + target.getName(), 1);

            for (Ladder ladder : LadderHandler.getLadders().values()) {
                inventoryMaker.addItem(new InventoryMaker.ClickableItem() {
                    @Override
                    public void onClick(InventoryClickEvent inventoryClickEvent) {
                        duelProcedure.setLadder(ladder);

                        Arena found = ArenaHandler.getRandom(ladder);
                        if (found == null) {
                            player.sendMessage(ColorText.translateAmpersand("&cThere are no arenas!"));
                            return;
                        }
                        if (player.hasPermission(KitPvPUtils.DONATOR_PERMISSION)) {
                            InventoryMaker maker = new InventoryMaker(profile.getTheme().getSecondaryColor() + "Select an arena", 3);

                            for (Arena arena : ArenaHandler.getArenaMap()) {
                                if (arena.getFirstPosition() == null || arena.getSecondPosition() == null) {
                                    continue;
                                }
                                if (!arena.getLadders().contains(duelProcedure.getLadder().getName())) {
                                    continue;
                                }
                                if (arena.isActive()) {
                                    continue;
                                }

                                maker.addItem(new InventoryMaker.ClickableItem() {
                                    @Override
                                    public void onClick(InventoryClickEvent inventoryClickEvent) {
                                        player.closeInventory();
                                        duelProcedure.setArena(arena);
                                        duelProcedure.send();
                                    }

                                    @Override
                                    public ItemStack getItemStack() {
                                        return new ItemMaker(Material.PAPER).setDisplayname("&a&l" + arena.getName()).create();
                                    }
                                });
                            }

                            player.openInventory(maker.getCurrentPage());
                        } else {
                            player.closeInventory();
                            duelProcedure.setArena(found);
                            duelProcedure.send();
                        }
                    }

                    @Override
                    public ItemStack getItemStack() {
                        return ladder.getDisplayIcon();
                    }
                });
            }

            player.openInventory(inventoryMaker.getCurrentPage());
        }
        return true;
    }
}