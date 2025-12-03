import swiftbot.*;
import java.util.*;

public class SimonSwiftGame {

    static SwiftBotAPI swiftBot;
    static ArrayList<Integer> gameSequence = new ArrayList<>();
    static int score = 0, round = 1, lives = 3, difficulty;
    static Scanner scanner = new Scanner(System.in);
    static Random random = new Random();

    static volatile int lastButtonPressed = -1;
    static final Object buttonLock = new Object();

    static final int RED = 0;
    static final int GREEN = 1;
    static final int BLUE = 2;
    static final int YELLOW = 3;

    static final int BUTTON_A = 0;
    static final int BUTTON_B = 1;
    static final int BUTTON_X = 2;
    static final int BUTTON_Y = 3;

    static int[][] colours = {
        {255, 0,   0},   // RED
        {0,   255, 0},   // GREEN
        {0,   0,   255}, // BLUE
        {255, 255, 0}    // YELLOW
    };

    // Button to Colour mapping
    // A = RED, B = GREEN, X = BLUE, Y = YELLOW
    static int[] buttonToColour = {
        RED,      // BUTTON_A
        GREEN,    // BUTTON_B
        BLUE,     // BUTTON_X
        YELLOW    // BUTTON_Y
    };

    // Colour to LED position mapping
    // RED = FRONT_LEFT, GREEN = FRONT_RIGHT, BLUE = BACK_LEFT, YELLOW = BACK_RIGHT
    static Underlight[] colourToLED = {
        Underlight.FRONT_LEFT,   // RED
        Underlight.FRONT_RIGHT,  // GREEN
        Underlight.BACK_LEFT,    // BLUE
        Underlight.BACK_RIGHT    // YELLOW
    };

    static final int TIME_FOR_30CM_AT_100 = 1500;

    public static void main(String[] args) {
        try {
            System.out.println("Connecting to SwiftBot...");
            // Attempt to get the SwiftBot instance
            swiftBot = SwiftBotAPI.INSTANCE;
            System.out.println("Connected!");

            // Setup routine
            swiftBot.disableUnderlights();
            setupButtons();
            
            // Start the main game loop
            playGame();

        } catch (Exception e) {
            // Catches any error during connection or gameplay
            System.out.println("A fatal error occurred during program execution or connection.");
            System.out.println("Error details: " + e.getMessage());
            e.printStackTrace();

        } finally {
            // Cleanup: always runs, even if an exception occurs
            try {
                if (swiftBot != null) {
                    System.out.println("Performing cleanup...");
                    disableAllButtons();
                    swiftBot.disableUnderlights();
                    swiftBot.stopMove();
                }
            } catch (Exception cleanupException) {
                // Ignore exceptions during cleanup, as the system might already be down
                System.err.println("Warning: Cleanup operation failed, the SwiftBot may need manual reset.");
            }
            System.out.println("Thank you for playing!");
        }
    }

    private static void setupButtons() {
        swiftBot.enableButton(Button.A, () -> {
            synchronized (buttonLock) {
                lastButtonPressed = BUTTON_A;
                buttonLock.notifyAll();
            }
        });

        swiftBot.enableButton(Button.B, () -> {
            synchronized (buttonLock) {
                lastButtonPressed = BUTTON_B;
                buttonLock.notifyAll();
            }
        });

        swiftBot.enableButton(Button.X, () -> {
            synchronized (buttonLock) {
                lastButtonPressed = BUTTON_X;
                buttonLock.notifyAll();
            }
        });

        swiftBot.enableButton(Button.Y, () -> {
            synchronized (buttonLock) {
                lastButtonPressed = BUTTON_Y;
                buttonLock.notifyAll();
            }
        });
    }

    private static void disableAllButtons() {
        swiftBot.disableButton(Button.A);
        swiftBot.disableButton(Button.B);
        swiftBot.disableButton(Button.X);
        swiftBot.disableButton(Button.Y);
    }

    private static int waitForButtonPress() throws InterruptedException {
        synchronized (buttonLock) {
            lastButtonPressed = -1;
            while (lastButtonPressed == -1) {
                // The main thread waits here until a button event fires notifyAll()
                buttonLock.wait();
            }
            int pressed = lastButtonPressed;
            lastButtonPressed = -1;
            return pressed;
        }
    }

    private static int getColourForButton(int button) {
        return buttonToColour[button];
    }

    // Shows a single colour on its specific LED position
    private static void showColour(int colourIndex) {
        int[] rgb = colours[colourIndex];
        Underlight led = colourToLED[colourIndex];
        // FIX for: The method setUnderlight(Underlight, int[]) is not applicable for the arguments (Underlight, int, int, int)
        swiftBot.setUnderlight(led, rgb); 
    }

    // Shows all four colours on their respective LEDs
    private static void showAllColours() {
        for (int i = 0; i < 4; i++) {
            int[] rgb = colours[i];
            Underlight led = colourToLED[i];
            // FIX for: The method setUnderlight(Underlight, int[]) is not applicable for the arguments (Underlight, int, int, int)
            swiftBot.setUnderlight(led, rgb);
        }
    }

    private static void turnOffAllLEDs() {
        swiftBot.disableUnderlights();
    }

    private static void blinkAllColoursRandomly() throws InterruptedException {
        ArrayList<Integer> order = new ArrayList<>(Arrays.asList(RED, GREEN, BLUE, YELLOW));
        Collections.shuffle(order);
        
        for (int colourIndex : order) {
            showColour(colourIndex);
            Thread.sleep(250);
            turnOffAllLEDs();
            Thread.sleep(100);
        }
    }

    private static void initializeSequence() {
        int startLength = 0;
        
        switch (difficulty) {
            case 1: 
                startLength = 0;
                break;
            case 2: 
                startLength = 4;
                break;
            case 3: 
                startLength = 6;
                break;
            case 4: 
                startLength = 10;
                break;
        }
        
        for (int i = 0; i < startLength; i++) {
            gameSequence.add(random.nextInt(4));
        }
        
        round = startLength + 1;
    }

    private static int getMaxSequenceLength() {
        switch (difficulty) {
            case 1: return 4;
            case 2: return 6;
            case 3: return 10;
            case 4: return 15;
            default: return 15;
        }
    }

    private static void playGame() throws InterruptedException {
        System.out.println("WELCOME TO SIMON SWIFT!");
        System.out.println("Button to Colour and LED Mapping:");
        System.out.println("Button A = RED    = FRONT LEFT");
        System.out.println("Button B = GREEN  = FRONT RIGHT");
        System.out.println("Button X = BLUE   = BACK LEFT");
        System.out.println("Button Y = YELLOW = BACK RIGHT");
        System.out.println("You have " + lives + " lives.");

        System.out.println("Difficulty Levels:");
        System.out.println("1 = Easy (sequences of 1-4 colours)");
        System.out.println("2 = Medium (sequences of 5-6 colours)");
        System.out.println("3 = Hard (sequences of 7-10 colours)");
        System.out.println("4 = Expert (sequences of 11-15 colours)");
        
        while (true) {
            System.out.print("Select Difficulty (1-4): ");
            String line = scanner.nextLine();
            try {
                difficulty = Integer.parseInt(line);
                if (difficulty >= 1 && difficulty <= 4) break;
                System.out.println("Please enter a number between 1 and 4.");
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number between 1 and 4.");
            }
        }

        initializeSequence();
        
        int maxLength = getMaxSequenceLength();
        System.out.println("Starting at round " + round + ". Max sequence length: " + maxLength);
        System.out.println("Get ready...");
        Thread.sleep(2000);

        // Main game loop
        while (lives > 0) {
            System.out.println("ROUND " + round + " | Score: " + score + " | Lives: " + lives);

            gameSequence.add(random.nextInt(4));

            System.out.println("Watch carefully...");
            Thread.sleep(1000);
            
            // Display the sequence - each colour lights its specific LED
            for (int colourIndex : gameSequence) {
                showColour(colourIndex);
                Thread.sleep(500);
                turnOffAllLEDs();
                Thread.sleep(300);
            }

            System.out.println("Your turn! Press the buttons:");
            boolean correct = true;

            // Get player input for each colour in the sequence
            for (int i = 0; i < gameSequence.size(); i++) {
                System.out.println("Button " + (i + 1) + " of " + gameSequence.size() + "...");
                
                int buttonPressed = waitForButtonPress();
                int colourEntered = getColourForButton(buttonPressed);
                
                String buttonName = getButtonName(buttonPressed);
                System.out.println("You pressed: " + buttonName);
                
                // Show the colour on its specific LED
                showColour(colourEntered);
                Thread.sleep(200);
                turnOffAllLEDs();

                if (colourEntered != gameSequence.get(i)) {
                    correct = false;
                    break;
                }
            }

            if (correct) {
                score++;
                System.out.println("Correct! Score: " + score);

                // Flash green on FRONT_RIGHT for success
                showColour(GREEN);
                Thread.sleep(300);
                turnOffAllLEDs();

                if (gameSequence.size() >= maxLength) {
                    System.out.println("CONGRATULATIONS!");
                    System.out.println("You completed Level " + difficulty + "!");
                    break;
                }

                if (round % 5 == 0) {
                    System.out.print("Continue? (Y/N): ");
                    String response = scanner.nextLine().trim().toUpperCase();
                    if (response.equals("N")) {
                        System.out.println("See you again champ!");
                        break;
                    }
                }

                round++;
                
            } else {
                lives--;
                System.out.println("Wrong!");

                // Flash red on FRONT_LEFT for wrong answer
                showColour(RED);
                Thread.sleep(300);
                turnOffAllLEDs();

                if (lives == 0) {
                    System.out.println("Game Over!");
                } else {
                    gameSequence.remove(gameSequence.size() - 1);
                    System.out.println("Lives remaining: " + lives);
                    System.out.println("Try the same sequence again!");
                }
            }
        }

        System.out.println("FINAL RESULTS");
        System.out.println("Final Score: " + score);
        System.out.println("Final Round: " + round);

        if (score >= 5) {
            celebrate();
        }
    }

    private static String getButtonName(int button) {
        switch (button) {
            case BUTTON_A: return "A (RED - FRONT LEFT)";
            case BUTTON_B: return "B (GREEN - FRONT RIGHT)";
            case BUTTON_X: return "X (BLUE - BACK LEFT)";
            case BUTTON_Y: return "Y (YELLOW - BACK RIGHT)";
            default: return "UNKNOWN";
        }
    }

    private static int calculateSpeed(int playerScore) {
        if (playerScore < 5) {
            return 40;
        } else if (playerScore >= 10) {
            return 100;
        } else {
            return playerScore * 10;
        }
    }

    private static void celebrate() throws InterruptedException {
        System.out.println("Celebration time!");
        
        int speed = calculateSpeed(score);
        
        System.out.println("Celebration speed: " + speed + "%");

        int timeFor30cm = (int) (TIME_FOR_30CM_AT_100 * (100.0 / speed));
        int turnTime = 850;

        System.out.println("Light show...");
        blinkAllColoursRandomly();

        System.out.println("Moving in V shape...");
        
        swiftBot.move(speed, speed, timeFor30cm);
        Thread.sleep(timeFor30cm + 100);
        
        swiftBot.move(speed, -speed, turnTime);
        Thread.sleep(turnTime + 100);
        
        swiftBot.move(speed, speed, timeFor30cm);
        Thread.sleep(timeFor30cm + 100);
        
        swiftBot.stopMove();

        System.out.println("Final light show...");
        blinkAllColoursRandomly();
        
        System.out.println("Celebration complete!");
    }
}