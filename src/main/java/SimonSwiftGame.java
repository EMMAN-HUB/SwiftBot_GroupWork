//If you have a problem, try finding the problem in the console or text me the gc, note you can temper with this code as it may not be 100% right(this is my first real java project).
import swiftbot.*;
import java.util.*;

public class SimonSwiftGame {

    static SwiftBotAPI swiftBot;
    static ArrayList<Integer> gameSequence = new ArrayList<>();
    static int score = 0, round = 1, lives = 3, difficulty;
    static Scanner scanner = new Scanner(System.in);
    static Random random = new Random();

    // Each colour is [R, G, B]
    // 0 = RED, 1 = GREEN, 2 = BLUE, 3 = YELLOW
    static int[][] colours = {
        {255, 0,   0},   // RED
        {0,   255, 0},   // GREEN
        {0,   0,   255}, // BLUE
        {255, 255, 0}    // YELLOW
    };

    public static void main(String[] args) {
        try {
            // connect to SwiftBot
            System.out.println("Connectingg to SwiftBot...");
            swiftBot = SwiftBotAPI.INSTANCE;
            System.out.println("Connected!");

            // turn everything off to start clean
            swiftBot.disableUnderlights();

            playGame();

        } catch (Exception e) {
            System.out.println("Fatal error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // make sure robot is stopped and lights are off
            try {
                if (swiftBot != null) {
                    swiftBot.disableUnderlights();
                    swiftBot.stopMove();
                }
            } catch (Exception ignored) {}
            System.out.println("Thank you for playing!");
        }
    }

    private static void playGame() throws InterruptedException {
        System.out.println("WELCOME TO SIMON SWIFT!");
        System.out.println("A=RED  B=GREEN  X=BLUE  Y=YELLOW");

        // ask for difficulty safely
        while (true) {
            System.out.print("\nSelect Difficulty (1-4): ");
            String line = scanner.nextLine();
            try {
                difficulty = Integer.parseInt(line);
                if (difficulty >= 1 && difficulty <= 4) break;
                System.out.println("Please enter a number between 1 and 4.");
            } catch (NumberFormatException e) {
                System.out.println("Please enter a *number* between 1 and 4.");
            }
        }

        // main game loop
        while (lives > 0) {
            System.out.println("\nROUND " + round + " | Score: " + score + " | Lives: " + lives);

            // add one random colour
            gameSequence.add(random.nextInt(4));

            // show sequence (all underlights in that colour)
            System.out.println("Watch carefully...");
            Thread.sleep(1000);
            for (int colourIndex : gameSequence) {
                int[] rgb = colours[colourIndex];
                swiftBot.fillUnderlights(rgb);
                Thread.sleep(500);
                swiftBot.disableUnderlights();
                Thread.sleep(300);
            }

            // get user input
            System.out.println("Your turn! Enter sequence:");
            boolean correct = true;

            for (int i = 0; i < gameSequence.size(); i++) {
                System.out.print("Button " + (i + 1) + " (A/B/X/Y): ");
                String input = scanner.nextLine().trim().toUpperCase();

                int entered = -1;
                switch (input) {
                    case "A": entered = 0; break; // RED
                    case "B": entered = 1; break; // GREEN
                    case "X": entered = 2; break; // BLUE
                    case "Y": entered = 3; break; // YELLOW
                    default:
                        System.out.println("Invalid button! Use A, B, X, or Y");
                        correct = false;
                }

                if (entered != gameSequence.get(i)) {
                    correct = false;
                    break;
                }
            }

            if (correct) {
                score++;
                System.out.println("✓ Correct!");

                // flash green
                swiftBot.fillUnderlights(colours[1]);
                Thread.sleep(200);
                swiftBot.disableUnderlights();

                // every 5 rounds offer to quit
                if (round % 5 == 0) {
                    System.out.print("Continue? (Y/N): ");
                    if (scanner.nextLine().trim().toUpperCase().equals("N")) {
                        System.out.println("See you again champ!");
                        break;
                    }
                }

                // reached target length for this difficulty?
                if ((difficulty == 1 && gameSequence.size() >= 3) ||
                    (difficulty == 2 && gameSequence.size() >= 6) ||
                    (difficulty == 3 && gameSequence.size() >= 10) ||
                    (difficulty == 4 && gameSequence.size() >= 15)) {
                    System.out.println("Max difficulty reached!");
                    break;
                }

                round++;
            } else {
                lives--;
                System.out.println("✗ Wrong!");

                // flash red
                swiftBot.fillUnderlights(colours[0]);
                Thread.sleep(200);
                swiftBot.disableUnderlights();

                if (lives == 0) {
                    System.out.println("Game Over!");
                }
            }
        }

        System.out.println("\nFinal Score: " + score + " | Rounds: " + (round - 1));

        // simple celebration if player did well
        if (score >= 5) {
            celebrate();
        }
    }

    private static void celebrate() throws InterruptedException {
        System.out.println("Celebration time!");
        int speed = Math.min(Math.max(score * 10, 40), 100);

        // random colours
        for (int i = 0; i < 4; i++) {
            swiftBot.fillUnderlights(colours[random.nextInt(4)]);
            Thread.sleep(250);
            swiftBot.disableUnderlights();
        }

        // simple V-ish movement
        swiftBot.move(speed, speed, 2000);
        Thread.sleep(2000);
        swiftBot.move(-50, 50, 850);
        Thread.sleep(1000);
        swiftBot.move(speed, speed, 2000);
        Thread.sleep(2000);
        swiftBot.move(-50, 50, 850);
        Thread.sleep(1000);
        swiftBot.move(speed, speed, 2000);
        Thread.sleep(2000);

        for (int i = 0; i < 4; i++) {
            swiftBot.fillUnderlights(colours[random.nextInt(4)]);
            Thread.sleep(250);
            swiftBot.disableUnderlights();
        }
    }
}


	
