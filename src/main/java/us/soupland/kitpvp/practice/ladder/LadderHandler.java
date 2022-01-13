package us.soupland.kitpvp.practice.ladder;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public class LadderHandler {

    @Getter
    private static Map<String, Ladder> ladders = new HashMap<>();

    public void registerLadder(Ladder... ladders) {
        for (Ladder ladder : ladders) {
            if (!getLadders().containsKey(ladder.getName())) {
                getLadders().put(ladder.getName(), ladder);
            }
        }
    }
}