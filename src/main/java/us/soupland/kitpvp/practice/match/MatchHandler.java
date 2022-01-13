package us.soupland.kitpvp.practice.match;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class MatchHandler {

    @Getter
    private static List<Match> matches = new ArrayList<>();

    static void registerMatch(Match match) {
        if (!matches.contains(match)) {
            matches.add(match);
        }
    }

    static void unregisterMatch(Match match) {
        matches.remove(match);
    }
}