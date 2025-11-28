import swiftbot.*;
import java.util.*;
import java.util.concurrent.*;

public class SimonSwiftGame {
    static SwiftBotAPI swiftBot;
    static ArrayList<Integer> gameSequence = new ArrayList<>();
    static int score = 0, round = 1, lives = 3, difficulty;
    static Scanner scanner = new Scanner(System.in);
    static Random random = new Random();
    static int[][] colours = {{255,0,0}, {0,255,0}, {0,0,255}, {255,255,0}};
    static Underlight[] leds = {Underlight.FRONT_LEFT, Underlight.FRONT_RIGHT, Underlight.MIDDLE_LEFT, Underlight.MIDDLE_RIGHT};
    
    public static void main(String[] args) throws Exception {
        try {
            swiftBot = SwiftBotAPI.INSTANCE;
        } catch (Exception e) {
            System.out.println("Failed to connect to SwiftBot!");
            System.exit(5);
        }
        
        swiftBot.disableUnderlights();
        System.out.println("WELCOME TO SIMON SWIFT GAME,I DONT KNOW WHAT TO WRITE HERE:)");
        System.out.println("A=RED  B=GREEN  X=BLUE  Y=YELLOW");
        System.out.println("\nSelect Difficulty (1-4): ");
        difficulty = scanner.nextInt();
        scanner.nextLine();
        
        while (lives > 0) {
            System.out.println("\nROUND " + round + " | Score: " + score + " | Lives: " + lives);
            gameSequence.add(random.nextInt(4));
            
            System.out.println("Watch carefully...");
            Thread.sleep(1000);
            for (int colour : gameSequence) {
                swiftBot.setUnderlight(leds[colour], colours[colour]);
                Thread.sleep(500);
                swiftBot.disableUnderlights();
                Thread.sleep(300);
            }
            
            System.out.println("Your turn! Enter sequence:");
            boolean correct = true;
            for (int i = 0; i < gameSequence.size(); i++) {
                System.out.print("Button " + (i+1) + ": ");
                String input = scanner.nextLine().toUpperCase();
                int expected = gameSequence.get(i);
                int entered = -1;
                
                switch(input) {
                    case "A": entered = 0; break;
                    case "B": entered = 1; break;
                    case "X": entered = 2; break;
                    case "Y": entered = 3; break;
                }
                
                if (entered != expected) {
                    correct = false;
                    break;
                }
            }
            
            if (correct) {
                score++;
                System.out.println("✓ Correct!");
                swiftBot.fillUnderlights(colours[1]);
                Thread.sleep(200);
                swiftBot.disableUnderlights();
                
                if (round % 5 == 0) {
                    System.out.print("Continue? (Y/N): ");
                    if (scanner.nextLine().toUpperCase().equals("N")) {
                        System.out.println("See you again champ!");
                        break;
                    }
                }
                
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
                swiftBot.fillUnderlights(colours[0]);
                Thread.sleep(200);
                swiftBot.disableUnderlights();
                if (lives == 0) System.out.println("Game Over!");
            }
        }
        
        System.out.println("\nFinal Score: " + score + " | Rounds: " + (round-1));
        
        if (score >= 5) {
            System.out.println("Celebration time!");
            int speed = Math.min(Math.max(score * 10, 40), 100);
            
            for (int i = 0; i < 4; i++) {
                swiftBot.fillUnderlights(colours[random.nextInt(4)]);
                Thread.sleep(250);
                swiftBot.disableUnderlights();
            }
            
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
        
        swiftBot.disableUnderlights();
        System.out.println("Thank you for playing!");
    }
}


	