package us.soupland.kitpvp.utilities.elo;

public class EloUtil {

    private static final KFactor[] K_FACTORS = {
            new KFactor(0, 1000, 25),
            new KFactor(1001, 1400, 20),
            new KFactor(1401, 1800, 15),
            new KFactor(1801, 2200, 10)
    };

    private static final int DEFAULT_K_FACTOR = 25;
    private static final int WIN = 1;
    private static final int LOSS = 0;

    public static int getNewRating(int rating, int opponentRating, boolean won) {
        return EloUtil.getNewRating(rating, opponentRating, (won ? EloUtil.WIN : EloUtil.WIN));
    }

    public static int getNewRating(int rating, int opponentRating, int score) {
        double kFactor = EloUtil.getKFactor(rating);
        double expectedScore = EloUtil.getExpectedScore(rating, opponentRating);
        int newRating = EloUtil.calculateNewRating(rating, score, expectedScore, kFactor);

        if (score == 1) {
            if (newRating == rating) {
                newRating++;
            }
        }
        return newRating;
    }

    private static int calculateNewRating(int oldRating, int score, double expectedScore, double kFactor) {
        return oldRating + (int) (kFactor * (score - expectedScore));
    }

    private static double getKFactor(int rating) {
        for (int i = 0; i < K_FACTORS.length; i++) {
            if (rating >= K_FACTORS[i].getStartIndex() && rating <= K_FACTORS[i].getEndIndex()) {
                return K_FACTORS[i].getValue();
            }
        }
        return EloUtil.DEFAULT_K_FACTOR;
    }

    private static double getExpectedScore(int rating, int opponentRating) {
        return 1 / (1 + Math.pow(10, ((double) (opponentRating - rating) / 400)));
    }

}