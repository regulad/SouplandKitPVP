package us.soupland.kitpvp.games;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import us.soupland.kitpvp.utilities.time.TimeUtils;

import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class GamePlayerInvite {

    private UUID sender, target;
    private long sent;

    public static GamePlayerInvite createInvite(UUID sender, UUID target) {
        return new GamePlayerInvite(sender, target, System.currentTimeMillis());
    }

    private int getLifetime() {
        return ((int) (System.currentTimeMillis() - sent) / 1000);
    }

    boolean isValid() {
        return (getLifetime() <= TimeUtils.parse("30s"));
    }

}