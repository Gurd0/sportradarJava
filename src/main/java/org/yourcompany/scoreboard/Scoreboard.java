package org.yourcompany.scoreboard;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Scoreboard {

    private final List<Match> matchesInProgress;
    private final AtomicInteger nextMatchId;
    private Clock clock;

    public Scoreboard(Clock clock) {
        this.matchesInProgress = new ArrayList<>();
        this.nextMatchId = new AtomicInteger(1); // Auto-Id, starts at 0. 
        this.clock = clock;
    }

    public Match startNewMatch(String homeTeamName, String awayTeamName) {
        Instant startTime = Instant.now(clock);
        Team homeTeam = new Team(homeTeamName);
        Team awayTeam = new Team(awayTeamName);
        Match newMatch = new Match(homeTeam, awayTeam, nextMatchId.getAndIncrement(), startTime); // use internal ID 
        this.matchesInProgress.add(newMatch);
        return newMatch;
    }

    public void updateScore(int matchIndex, int[] newScores) {
        if (matchIndex >= 0 && matchIndex < matchesInProgress.size()) {
            matchesInProgress.get(matchIndex).updateScore(newScores[0], newScores[1]);
        } else {
            throw new IndexOutOfBoundsException("Match index " + matchIndex + " is out of bounds.");
        }
    }

    public void endMatch(int matchIndex) {
        if (matchIndex >= 0 && matchIndex < matchesInProgress.size()) {
            matchesInProgress.remove(matchIndex);
        } else {
            throw new IndexOutOfBoundsException("Match index " + matchIndex + " is out of bounds.");
        }
    }

    public List<Match> getScoreboard() {
        return new ArrayList<>(matchesInProgress);
    }

    public List<Match> getSummary() {
        List<Match> summary = new ArrayList<>(matchesInProgress);
        // sort using the match comparator
        Collections.sort(summary, Match.getSummaryComparator());
        return summary;
    }

    /*
        Had problems getting mockito-inline to work. 
        Made a setter for clock and included it in the scoreboard to help get test case to work. 
     */
    public void setClock(Clock clock) {
        this.clock = clock;
    }
}
