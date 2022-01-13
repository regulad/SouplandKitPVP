package us.soupland.kitpvp.practice.duel;

import lombok.Data;
import us.soupland.kitpvp.practice.arena.Arena;
import us.soupland.kitpvp.practice.ladder.Ladder;

import java.util.UUID;

@Data
public class DuelRequest {

    private UUID uuid;
    private Ladder ladder;
    private Arena arena;
    private long timestamp = System.currentTimeMillis();

    public DuelRequest(UUID uuid) {
        this.uuid = uuid;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() - timestamp >= 30_000;
    }
}