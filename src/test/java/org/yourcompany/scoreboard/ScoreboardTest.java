package org.yourcompany.scoreboard;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ScoreboardTest {

    private Scoreboard scoreboard;

    @BeforeEach
    void setUp() {
        Clock clock = Clock.fixed(Instant.parse("2025-06-29T10:00:00Z"), ZoneOffset.UTC);
        scoreboard = new Scoreboard(clock);
    }

    @Test
    void noMatchInProgress() {
        List<Match> summary = scoreboard.getScoreboard();
        assertTrue(summary.isEmpty(), "Scoreboard should be empty.");
    }

    @Test
    void startNewMatchAndAddToScoreboard() {
        scoreboard.startNewMatch("Rosenborg", "Bodø/Glimt"); // The '1' is ignored by my Java impl's matchId
        List<Match> summary = scoreboard.getScoreboard();

        assertEquals(1, summary.size());
        assertEquals("Rosenborg", summary.get(0).getHomeTeam().getName());
        assertEquals("Bodø/Glimt", summary.get(0).getAwayTeam().getName());
        assertEquals(new Score(0, 0), summary.get(0).getScore());
    }

    @Test
    void updateScore() {
        scoreboard.startNewMatch("Rosenborg", "Bodø/Glimt");
        scoreboard.updateScore(0, new int[]{1, 0}); // Update match by index

        assertEquals(new Score(1, 0), scoreboard.getScoreboard().get(0).getScore());
    }

    @Test
    void endMatchAndRemoveFromScoreboard() {
        scoreboard.startNewMatch("Rosenborg", "Bodø/Glimt");
        scoreboard.endMatch(0); // Remove match by index.

        assertTrue(scoreboard.getScoreboard().isEmpty());
    }

    @Test
    void getSummaryInTheRightOrderBasedOnScore() {
        scoreboard.startNewMatch("Rosenborg", "Bodø/Glimt"); // Index 0
        scoreboard.startNewMatch("Leirfjord IL", "Sandnessjøen IL"); // Index 1
        scoreboard.startNewMatch("Brann", "Lyn"); // Index 2

        scoreboard.updateScore(0, new int[]{2, 0}); // Rosenborg 2-0 Bodø/Glimt (Total: 2)
        scoreboard.updateScore(1, new int[]{2, 1}); // Leirfjord IL 2-1 Sandnessjøen IL (Total: 3)
        scoreboard.updateScore(2, new int[]{1, 0}); // Brann 1-0 Lyn (Total: 1)

        List<Match> summary = scoreboard.getSummary();

        // Expected order: Leirfjord (3), Rosenborg (2), Brann (1)
        assertEquals(new Score(2, 1), summary.get(0).getScore()); // Leirfjord IL vs Sandnessjøen IL
        assertEquals(new Score(2, 0), summary.get(1).getScore()); // Rosenborg vs Bodø/Glimt
        assertEquals(new Score(1, 0), summary.get(2).getScore()); // Brann vs Lyn
    }

    @Test
    void getSummaryInTheRightOrderBasedOnTime() {
        //Mock Clock for testing.
        Clock fixedClock1 = Clock.fixed(Instant.parse("2025-06-29T10:00:00Z"), ZoneOffset.UTC);
        Clock fixedClock2 = Clock.fixed(Instant.parse("2025-06-29T11:00:00Z"), ZoneOffset.UTC);

        scoreboard.setClock(fixedClock1);
        scoreboard.startNewMatch("Rosenborg", "Bodø/Glimt");
        scoreboard.setClock(fixedClock2);
        scoreboard.startNewMatch("Brann", "Lyn");

        scoreboard.updateScore(0, new int[]{2, 0}); // Rosenborg 2-0
        scoreboard.updateScore(1, new int[]{2, 0}); // Brann 2-0

        List<Match> summary = scoreboard.getSummary();

        // newest match first (Brann)
        assertEquals(2, summary.size());
        assertEquals("Brann", summary.get(0).getHomeTeam().getName());
        assertEquals("Rosenborg", summary.get(1).getHomeTeam().getName());

        assertEquals(Instant.now(fixedClock2), summary.get(0).getStartTime());
        assertEquals(Instant.now(fixedClock1), summary.get(1).getStartTime());
    }
}
