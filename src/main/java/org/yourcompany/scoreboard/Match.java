package org.yourcompany.scoreboard;

import java.time.Instant;
import java.util.Comparator;

public class Match {

    private final Team homeTeam;
    private final Team awayTeam;
    private Score score;
    private final int matchId;
    private final Instant startTime;

    public Match(Team homeTeam, Team awayTeam, int matchId, Instant startTime) {
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.score = new Score(0, 0); // Start at 0-0
        this.matchId = matchId;
        this.startTime = startTime;
    }

    public Team getHomeTeam() {
        return homeTeam;
    }

    public Team getAwayTeam() {
        return awayTeam;
    }

    public Score getScore() {
        return score;
    }

    public void updateScore(int homeScore, int awayScore) {
        if (homeScore >= this.score.home() && awayScore >= this.score.away()) {
            this.score = new Score(homeScore, awayScore);
        } else {
            // error handle for bad score.
            System.err.println("Error - invalid score" + matchId);
        }
    }

    public int getTotalScore() {
        return score.home() + score.away();
    }

    public int getMatchId() {
        return matchId;
    }

    public Instant getStartTime() {
        return startTime;
    }

    //  sorting based on total score, then by start time (newer first)
    public static Comparator<Match> getSummaryComparator() {
        return Comparator
                .comparing(Match::getTotalScore)
                .thenComparing(Match::getStartTime)
                .reversed();
    }

}
