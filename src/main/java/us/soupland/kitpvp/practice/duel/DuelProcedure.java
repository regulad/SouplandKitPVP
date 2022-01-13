package us.soupland.kitpvp.practice.duel;

import lombok.Data;
import us.soupland.kitpvp.practice.arena.Arena;
import us.soupland.kitpvp.practice.ladder.Ladder;
import us.soupland.kitpvp.profile.Profile;
import us.soupland.kitpvp.profile.ProfileManager;
import org.bukkit.entity.Player;
import us.soupland.kitpvp.utilities.chat.ChatUtil;
import us.soupland.kitpvp.utilities.chat.ColorText;

@Data
public class DuelProcedure {

    private Player sender, target;
    private Ladder ladder;
    private Arena arena;

    public void send() {
        if (!sender.isOnline() || !target.isOnline()) {
            return;
        }

        DuelRequest request = new DuelRequest(sender.getUniqueId());
        request.setLadder(ladder);
        request.setArena(arena);

        Profile profile = ProfileManager.getProfile(sender);
        profile.setDuelProcedure(null);
        profile.getSentDuelRequests().put(target.getUniqueId(), request);

        sender.sendMessage(ColorText.translate("&aYou sent a duel request to &e" + target.getName() + " &aon arena &e" + arena.getName() + "&a."));

        new ChatUtil("&e" + sender.getName() + " &asent you " + (ladder.getName().startsWith("u") ? "an" : "") + " &e" + ladder.getName() + " &aduel request on arena &e" + arena.getName() + "&a.", "&6Click here or type &b/accept " + sender.getName() + " &6to accept the invite.", "/accept " + sender.getName()).send(target);
    }
}