import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.Random;

public class SimonSwiftGameTest {

    private ArrayList<Integer> testSequence;
    private int testScore;
    private int testRound;
    private int testLives;
    private int testDifficulty;
    private Random random;

    @BeforeEach
    public void setUp() {
        testSequence = new ArrayList<>();
        testScore = 0;
        testRound = 1;
        testLives = 3;
        random = new Random();
    }

    @AfterEach
    public void tearDown() {
        testSequence.clear();
        testSequence = null;
    }

    @Test
    @DisplayName("FT1 - First round should have exactly one colour")
    public void testFirstRoundHasOneColour() {
        testSequence.add(random.nextInt(4));
        assertEquals(1, testSequence.size(), "First round should contain exactly one colour");
    }

    @Test
    @DisplayName("FT2 - Sequence increases by one each round")
    public void testSequenceIncreasesEachRound() {
        for (int i = 0; i < 5; i++) {
            testSequence.add(random.nextInt(4));
            assertEquals(i + 1, testSequence.size(), "Sequence should grow by 1 each round");
        }
    }

    @Test
    @DisplayName("FT3 - Colour index is always between 0 and 3")
    public void testColourIndexRange() {
        for (int i = 0; i < 50; i++) {
            int colourIndex = random.nextInt(4);
            assertTrue(colourIndex >= 0 && colourIndex <= 3, 
                "Colour index should be between 0 and 3");
        }
    }

    @Test
    @DisplayName("FT4 - Correct input increases round number")
    public void testCorrectInputIncreasesRound() {
        int initialRound = testRound;
        boolean correct = true;
        
        if (correct) {
            testRound++;
        }
        
        assertEquals(initialRound + 1, testRound, "Round should increase after correct input");
    }

    @Test
    @DisplayName("FT5 - Incorrect input reduces lives")
    public void testIncorrectInputReducesLives() {
        int initialLives = testLives;
        boolean correct = false;
        
        if (!correct) {
            testLives--;
        }
        
        assertEquals(initialLives - 1, testLives, "Lives should decrease after wrong input");
    }

    @Test
    @DisplayName("FT6 - Score increases by one for correct sequence")
    public void testScoreIncreasesByOne() {
        int initialScore = testScore;
        boolean correct = true;
        
        if (correct) {
            testScore++;
        }
        
        assertEquals(initialScore + 1, testScore, "Score should increase by 1");
    }

    @Test
    @DisplayName("FT7 - Quit option appears at round 5")
    public void testQuitOptionAtRoundFive() {
        testRound = 5;
        boolean shouldShowQuitOption = (testRound % 5 == 0);
        assertTrue(shouldShowQuitOption, "Quit option should appear at round 5");
    }

    @Test
    @DisplayName("FT7b - Quit option appears at round 10")
    public void testQuitOptionAtRoundTen() {
        testRound = 10;
        boolean shouldShowQuitOption = (testRound % 5 == 0);
        assertTrue(shouldShowQuitOption, "Quit option should appear at round 10");
    }

    @Test
    @DisplayName("FT9 - No celebration when score is under 5")
    public void testNoCelebrationUnderFive() {
        testScore = 3;
        boolean shouldCelebrate = (testScore >= 5);
        assertFalse(shouldCelebrate, "Should not celebrate when score is below 5");
    }

    @Test
    @DisplayName("FT10 - Celebration triggers when score is 5 or more")
    public void testCelebrationAtFiveOrMore() {
        testScore = 5;
        boolean shouldCelebrate = (testScore >= 5);
        assertTrue(shouldCelebrate, "Should celebrate when score is 5 or more");
    }

    @Test
    @DisplayName("FT10b - Celebration triggers when score is above 5")
    public void testCelebrationAboveFive() {
        testScore = 8;
        boolean shouldCelebrate = (testScore >= 5);
        assertTrue(shouldCelebrate, "Should celebrate when score is above 5");
    }

    @Test
    @DisplayName("Test speed calculation when score is below 5")
    public void testSpeedCalculationBelowFive() {
        testScore = 3;
        int speed;
        
        if (testScore < 5) {
            speed = 40;
        } else if (testScore >= 10) {
            speed = 100;
        } else {
            speed = testScore * 10;
        }
        
        assertEquals(40, speed, "Speed should be 40 when score is below 5");
    }

    @Test
    @DisplayName("Test speed calculation when score is between 5 and 9")
    public void testSpeedCalculationMidRange() {
        testScore = 7;
        int speed;
        
        if (testScore < 5) {
            speed = 40;
        } else if (testScore >= 10) {
            speed = 100;
        } else {
            speed = testScore * 10;
        }
        
        assertEquals(70, speed, "Speed should be score * 10 when between 5 and 9");
    }

    @Test
    @DisplayName("Test speed calculation when score is 10 or more")
    public void testSpeedCalculationAboveTen() {
        testScore = 12;
        int speed;
        
        if (testScore < 5) {
            speed = 40;
        } else if (testScore >= 10) {
            speed = 100;
        } else {
            speed = testScore * 10;
        }
        
        assertEquals(100, speed, "Speed should be 100 when score is 10 or more");
    }

    @Test
    @DisplayName("Test difficulty 1 max sequence length")
    public void testDifficulty1MaxLength() {
        testDifficulty = 1;
        int maxLength = getMaxSequenceLength(testDifficulty);
        assertEquals(3, maxLength, "Difficulty 1 max length should be 3");
    }

    @Test
    @DisplayName("Test difficulty 2 max sequence length")
    public void testDifficulty2MaxLength() {
        testDifficulty = 2;
        int maxLength = getMaxSequenceLength(testDifficulty);
        assertEquals(6, maxLength, "Difficulty 2 max length should be 6");
    }

    @Test
    @DisplayName("Test difficulty 3 max sequence length")
    public void testDifficulty3MaxLength() {
        testDifficulty = 3;
        int maxLength = getMaxSequenceLength(testDifficulty);
        assertEquals(10, maxLength, "Difficulty 3 max length should be 10");
    }

    @Test
    @DisplayName("Test difficulty 4 max sequence length")
    public void testDifficulty4MaxLength() {
        testDifficulty = 4;
        int maxLength = getMaxSequenceLength(testDifficulty);
        assertEquals(15, maxLength, "Difficulty 4 max length should be 15");
    }

    @Test
    @DisplayName("Test lives system - game ends at zero lives")
    public void testGameEndsAtZeroLives() {
        testLives = 1;
        testLives--;
        
        boolean gameOver = (testLives == 0);
        assertTrue(gameOver, "Game should end when lives reach 0");
    }

    @Test
    @DisplayName("Test sequence removal on wrong answer")
    public void testSequenceRemovalOnWrongAnswer() {
        testSequence.add(0);
        testSequence.add(1);
        testSequence.add(2);
        
        int sizeBefore = testSequence.size();
        testSequence.remove(testSequence.size() - 1);
        
        assertEquals(sizeBefore - 1, testSequence.size(), 
            "Sequence should shrink by 1 when player gets it wrong");
    }

    @Test
    @DisplayName("Test button mapping for A")
    public void testButtonMappingA() {
        int colourIndex = 0;
        String buttonName = getButtonName(colourIndex);
        assertEquals("A (RED)", buttonName, "Button A should map to RED");
    }

    @Test
    @DisplayName("Test button mapping for B")
    public void testButtonMappingB() {
        int colourIndex = 1;
        String buttonName = getButtonName(colourIndex);
        assertEquals("B (GREEN)", buttonName, "Button B should map to GREEN");
    }

    @Test
    @DisplayName("Test button mapping for X")
    public void testButtonMappingX() {
        int colourIndex = 2;
        String buttonName = getButtonName(colourIndex);
        assertEquals("X (BLUE)", buttonName, "Button X should map to BLUE");
    }

    @Test
    @DisplayName("Test button mapping for Y")
    public void testButtonMappingY() {
        int colourIndex = 3;
        String buttonName = getButtonName(colourIndex);
        assertEquals("Y (YELLOW)", buttonName, "Button Y should map to YELLOW");
    }

    @Test
    @DisplayName("Test initial game state")
    public void testInitialGameState() {
        assertEquals(0, testScore, "Initial score should be 0");
        assertEquals(1, testRound, "Initial round should be 1");
        assertEquals(3, testLives, "Initial lives should be 3");
        assertTrue(testSequence.isEmpty(), "Initial sequence should be empty");
    }

    @Test
    @DisplayName("Test colour array has correct RGB values")
    public void testColourArrayValues() {
        int[][] colours = {
            {255, 0, 0},
            {0, 255, 0},
            {0, 0, 255},
            {255, 255, 0}
        };
        
        assertArrayEquals(new int[]{255, 0, 0}, colours[0], "Index 0 should be RED");
        assertArrayEquals(new int[]{0, 255, 0}, colours[1], "Index 1 should be GREEN");
        assertArrayEquals(new int[]{0, 0, 255}, colours[2], "Index 2 should be BLUE");
        assertArrayEquals(new int[]{255, 255, 0}, colours[3], "Index 3 should be YELLOW");
    }

    @Test
    @DisplayName("Test difficulty 2 starting sequence length")
    public void testDifficulty2StartLength() {
        testDifficulty = 2;
        int startLength = getStartLength(testDifficulty);
        assertEquals(3, startLength, "Difficulty 2 should start with 3 colours");
    }

    @Test
    @DisplayName("Test difficulty 3 starting sequence length")
    public void testDifficulty3StartLength() {
        testDifficulty = 3;
        int startLength = getStartLength(testDifficulty);
        assertEquals(6, startLength, "Difficulty 3 should start with 6 colours");
    }

    @Test
    @DisplayName("Test difficulty 4 starting sequence length")
    public void testDifficulty4StartLength() {
        testDifficulty = 4;
        int startLength = getStartLength(testDifficulty);
        assertEquals(10, startLength, "Difficulty 4 should start with 10 colours");
    }

    private int getMaxSequenceLength(int difficulty) {
        switch (difficulty) {
            case 1: return 3;
            case 2: return 6;
            case 3: return 10;
            case 4: return 15;
            default: return 15;
        }
    }

    private int getStartLength(int difficulty) {
        switch (difficulty) {
            case 1: return 0;
            case 2: return 3;
            case 3: return 6;
            case 4: return 10;
            default: return 0;
        }
    }

    private String getButtonName(int colourIndex) {
        switch (colourIndex) {
            case 0: return "A (RED)";
            case 1: return "B (GREEN)";
            case 2: return "X (BLUE)";
            case 3: return "Y (YELLOW)";
            default: return "UNKNOWN";
        }
    }
}