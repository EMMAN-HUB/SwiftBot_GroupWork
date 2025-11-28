import swiftbot.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Scanner;

public class DoesMySwiftBotWork {
	static SwiftBotAPI swiftBot;
	// ANSI color codes
	static final String RESET = "\u001B[0m";
	static final String CYAN = "\u001B[36m";
	static final String YELLOW = "\u001B[33m";
	static final String GREEN = "\u001B[32m";
	static final String WHITE = "\u001B[37m";
	static final String BOLD = "\u001B[1m";

	public static void main(String[] args) throws InterruptedException {
		try {
			swiftBot = SwiftBotAPI.INSTANCE;
		} catch (Exception e) {
			/*
			 * Outputs a warning if I2C is disabled. 
			 * This only needs to be turned on once by our staff.
			 * Let us know if this problem occurs!
			 */
			System.out.println("\nI2C disabled!");
			System.exit(5);
		}

		// --- HEADER (printed once) ---
		System.out.printf("%1$s%2$s***************************************************************%3$"
				+ "%1$s%2$s*                     SWIFTBOT TEST MENU                      *%3$s"
				+ "%1$s%2$s***************************************************************%3$s", CYAN, BOLD, RESET);
		Scanner reader = new Scanner(System.in); // Reading from System.in

		// loops main menu after each user action
		while (true) {
			// --- MENU ---
			System.out.printf(
					"%sSelect a feature to test:%s\n\n" +
							"%s [1] %sTest Camera          %s|%s  [2] %sTest Button Lights\n" +
							"%s [3] %sTest Ultrasound      %s|%s  [4] %sTest Left Wheel\n" +
							"%s [5] %sTest Right Wheel     %s|%s  [6] %sTest Buttons\n" +
							"%s [7] %sTest Underlighting   %s|%s  [8] %sTest QR Code Detection\n" +
							"%s---------------------------------------------------------------%s\n\n" +
							"%s [0] %sExit%s\n\n" +
							"%sEnter a number: %s",
							WHITE, RESET,
							YELLOW, GREEN, WHITE, YELLOW, GREEN,
							YELLOW, GREEN, WHITE, YELLOW, GREEN,
							YELLOW, GREEN, WHITE, YELLOW, GREEN,
							YELLOW, GREEN, WHITE, YELLOW, GREEN,
							CYAN, RESET,
							YELLOW, GREEN, RESET,
							WHITE, RESET
					);

			// reads user input and performs corresponding action
			String ans = reader.next();

			switch (ans) {

			// Testing Camera
			case "1":
				System.out.println("Testing Camera.");
				testCamera();
				break;

				// Testing Button lights
			case "2":
				System.out.println("Testing Button Lights");
				testButtonLights();
				break;

				// Testing Ultrasound
			case "3":
				testUltrasound();
				break;

				// Testing Left Wheel
			case "4":
				testWheel("left");
				Thread.sleep(1000);
				break;

				// Testing Right Wheel
			case "5":
				testWheel("right");
				Thread.sleep(1000);
				break;

				// testing Buttons
			case "6":
				testButtons();
				break;

				// Testing Under lights
			case "7":
				testAllUnderlights();
				testIndividualUnderlights();
				break;

				// Testing QR Code
			case "8":
				testQRCodeDetection();
				break;

				// EXIT
			case "0":
				reader.close();
				System.exit(0);
				break;

			default:
				System.out.println("ERROR: Please enter a valid number.");
				break;
			}
		}
	}

	public static void testCamera() {
		try {
			/*
			 * Taking 720x720 images for the test, however, it is recommended to use smaller
			 * sizes for faster
			 * code execution
			 */

			BufferedImage bwImage = swiftBot.takeGrayscaleStill(ImageSize.SQUARE_720x720);
			BufferedImage image = swiftBot.takeStill(ImageSize.SQUARE_720x720);

			if (image == null || bwImage == null) {
				System.out.println("ERROR: Image is null");
				System.exit(5);
			} else {
				// Save the bwImage to a directory.
				ImageIO.write(bwImage, "png", new File("/data/home/pi/bwImage.png"));
				ImageIO.write(image, "png", new File("/data/home/pi/colourImage.png"));

				System.out.println("SUCCESS: Camera tests succeeded");
				Thread.sleep(1000);
			}
		} catch (Exception e) {
			System.out.println("\nCamera not enabled!");
			System.out.println("Try running the following command: ");
			System.out.println("sudo raspi-config nonint do_camera 0\n");
			System.out.println("Then reboot using the following command: ");
			System.out.println("sudo reboot\n");
			System.exit(5);
		}
	}

	/*
	 * Testing each light for the SwiftBot Buttons. Each button is first
	 * individually
	 * turned on using the `testIndividualButtonLights()` method below this method.
	 * then, all button
	 * lights are turned on with `testAllButtonLights()`.
	 */
	public static void testButtonLights() {
		try {
			testIndividualButtonLights();
			testAllButtonLights();
		} catch (Exception e) {
			System.out.println("ERROR: An error occurred testing button lights.");
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	
	/*
	 * This method tests the ultrasound 
	 * 
	 * 
	 * 
	 */
	
	public static void testUltrasound() {
	      double distanceToObject = 0.0;
	      System.out.println("Turning on Ultrasound...");
	      try {
	          distanceToObject = SwiftBotAPI.INSTANCE.useUltrasound();
	          System.out.println("Distance to object:" + distanceToObject);
	      } catch (Exception e) {
	          e.printStackTrace();
	      }
	}

	/*
	 * This method tests each individual button light, by firstly turning them on
	 * with {@code setButtonLight();},
	 * before toggling them off with {@code toggleButtonLight();}. Afterwards, a for
	 * loop is ran that will turn up
	 * the brightness of the individual button light from 0 to 100 with {@code
	 * setButtonLightBrightness}, before
	 * turning it off completely with {@code setButtonLight();}.
	 */
	public static void testIndividualButtonLights() {
		try {
			// Creating an array with all the robot buttons
			Button buttons[] = { Button.A, Button.B, Button.X, Button.Y };

			for (Button button : buttons) {
				// Turning it on
				swiftBot.setButtonLight(button, true); 

				// Wait a second
				Thread.sleep(1000);

				// Toggling the button light off.
				swiftBot.toggleButtonLight(button); 

				// Small wait so they're completely off.
				Thread.sleep(250);

				// Completely turning off the button light.
				swiftBot.setButtonLight(button, false); 
				System.out.println("SUCCESS: Changes for button " + button + " successful.");
			}
		} catch (Exception e) {
			System.out.println("ERROR: An error occurred setting button lights.");
			e.printStackTrace();
			System.exit(5);
		}
	}

	/*
	 * This method test all the button lights at once by using the {@code
	 * fillButtonLights();} method. After 2.5
	 * seconds, all the button lights will be turned off with {@code
	 * disableButtonLights();}.
	 */
	public static void testAllButtonLights() {
		try {
			// Turns on all button lights
			swiftBot.fillButtonLights();

			// Waiting for 2.5 seconds
			Thread.sleep(2500);

			// Turns off all button lights at once.
			swiftBot.disableButtonLights();
		} catch (Exception e) {
			System.out.println("ERROR: An error occurred setting all button lights.");
			e.printStackTrace();
			System.exit(5);
		}
	}

	/*
	 * Tests the user-selected wheel, taking the wheel as a parameter: either "left"
	 * or "right".
	 * If the "left" wheel is selected, the function sets the left wheel velocity to
	 * 100%
	 * If the "right" wheel is selected, the function sets the right wheel velocity
	 * to 100%
	 */
	public static void testWheel(String wheel) {
		// These variables represent the % velocity of each wheel.
		// They are updated later in the method, depending on the chosen wheel
		int leftWheelVelocity = 0;
		int rightWheelVelocity = 0;

		// If left wheel is chosen, set left wheel velocity to 100
		if (wheel.equals("left")) {
			System.out.println("Testing the left wheel");
			leftWheelVelocity = 100;
		}
		// Otherwise, set the right wheel velocity to 100.
		else {
			System.out.println("Testing the right wheel");
			rightWheelVelocity = 100;
		}

		try {
			// Moves the wheels with the set left wheel and right wheel velocity, for 3
			// seconds.
			swiftBot.move(leftWheelVelocity, rightWheelVelocity, 3000);
			testWheelReverse(wheel);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("ERROR: Error while testing wheel");
			System.exit(5);
		}
	}

	/*
	 * Tests the user-selected wheel defined in the method above, but reverses the
	 * wheels instead.
	 * (Setting the velocity to -100 instead of 100)
	 */
	public static void testWheelReverse(String wheel) {
		// These variables represent the % velocity of each wheel.
		// They are updated later in the method, depending on the chosen wheel
		int leftWheelVelocity = 0;
		int rightWheelVelocity = 0;

		// If left wheel is chosen, set left wheel velocity to 100
		if (wheel.equals("left")) {
			System.out.println("Testing the left wheel");
			leftWheelVelocity = -100;
		}
		// Otherwise, set the right wheel velocity to 100.
		else {
			System.out.println("Testing the right wheel");
			rightWheelVelocity = -100;
		}

		try {
			// Moves the wheels with the set left wheel and right wheel velocity, for 3
			// seconds.
			swiftBot.move(leftWheelVelocity, rightWheelVelocity, 3000);
			System.out.println("SUCCESS: wheel tests successful");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("ERROR: Error while testing wheel");
			System.exit(5);
		}

	}

	/*
	 * Tests each button by making them all active for 10 seconds. You can extend,
	 * or shorten the time by changing
	 * the {@code endTime} variable. When you enable a button, you are free to
	 * encase a method inside of it that will
	 * only run once a button has been pressed. In this example, once a button has
	 * been pressed, it'll be disabled using
	 * {@code swiftBot.disableButton();}, meaning it cannot be pressed again. You
	 * are also able to disable all buttons
	 * by running {@code swiftBot.disableAllButtons();}
	 */
	public static void testButtons() throws InterruptedException {
		System.out.println("All buttons on your Bot will be active for the next 10 seconds.. \n Press each one separately");

		try {
			long endtime = System.currentTimeMillis() + 10_000;
			swiftBot.enableButton(Button.A, () -> {
				System.out.println("Button A Pressed.");
				swiftBot.disableButton(Button.A);
			});

			swiftBot.enableButton(Button.B, () -> {
				System.out.println("Button B Pressed.");
				swiftBot.disableButton(Button.B);
			});

			swiftBot.enableButton(Button.X, () -> {
				System.out.println("Button X Pressed.");
				swiftBot.disableButton(Button.X);
			});

			swiftBot.enableButton(Button.Y, () -> {
				System.out.println("Button Y Pressed.");
				swiftBot.disableButton(Button.Y);
			});

			while (System.currentTimeMillis() < endtime) {
				; // This while loop does nothing for 10 seconds.
			}
			swiftBot.disableAllButtons(); // Turns off all buttons now that it's been 10 seconds.
			System.out.println("All buttons are now off.");

		} catch (Exception e) {
			System.out.println("ERROR occurred when setting up buttons.");
			e.printStackTrace();
			System.exit(5);
		}

	}

	/*
	 * This method uses a range of colours to test all under lights, where each
	 * colour is represented
	 * as an integer array, with the first value being the amount of red, the second
	 * the amount of green
	 * and the third the amount of blue. These colours are then looped through with
	 * a for loop and assigned
	 * to all under lights using the {@code fillUnderlights();} method.
	 */
	public static void testAllUnderlights() throws InterruptedException {
		int[][] colours = {
				{ 255, 0, 0 }, // Red
				{ 0, 255, 0 }, // Green
				{ 0, 0, 255 }, // Blue
				{ 255, 255, 255 } // White
		};

		try {
			// This for loop iterates through all colours in the colours array.
			for (int[] rgb : colours) {
				swiftBot.fillUnderlights(rgb);
				Thread.sleep(300);
			}
			swiftBot.disableUnderlights();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("ERROR: failed to set all under lights");
			System.exit(5);
		}

	}

	/*
	 * This method uses three colours to test each individual under light: red,
	 * green and blue.
	 * An array of under lights are created, and then iterated through with a for
	 * loop.
	 * Inside this for loop, each under light is set to red, then 200 milliseconds
	 * later set to blue,
	 * then 200 milliseconds to green. After this, the colour of the under light is
	 * not removed, such that
	 * when the method finishes, all under lights should be green.
	 */
	public static void testIndividualUnderlights() throws InterruptedException {
		// Declaring three variables containing the RGB values for red, green and blue.
		int[] red = new int[] { 255, 0, 0 };
		int[] blue = new int[] { 0, 0, 255 };
		int[] green = new int[] { 0, 255, 0 };

		try {
			// Declaring an array of under lights.
			Underlight[] underlights = new Underlight[] { Underlight.BACK_LEFT, Underlight.BACK_RIGHT,
					Underlight.MIDDLE_LEFT,
					Underlight.MIDDLE_RIGHT, Underlight.FRONT_LEFT, Underlight.FRONT_RIGHT };

			for (Underlight underlight : underlights) { // Iterates through the array of under lights
				System.out.println("Testing: " + underlight);

				swiftBot.setUnderlight(underlight, red);
				Thread.sleep(200);

				swiftBot.setUnderlight(underlight, green);
				Thread.sleep(200);

				swiftBot.setUnderlight(underlight, blue);
				Thread.sleep(200);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("ERROR: Unable to set under light");
			System.exit(5);
		}

		System.out.println("SUCCESS: All under lights should be blue");
		Thread.sleep(2000);
		swiftBot.disableUnderlights();
	}

	/*
	 * This method takes a capture from the SwiftBot's camera to try and find a QR
	 * code in an image, and then
	 * decode it. If a QR code is found, the method will print the decoded message
	 * to the console.
	 * You could use these methods to have the SwiftBot take commands such as
	 * movement, lights, etc by scanning them!
	 * 
	 * Note - the distance between the SwiftBot's camera and the QR code matters. If
	 * the QR code is too close or too far,
	 * the camera's focus might not be able to pick it up.
	 */
	public static void testQRCodeDetection() {


		int attempts = 0;

		while(attempts <=10) {
			
			System.out.println("Scanning for QR code...");

			try {

				BufferedImage img = swiftBot.getQRImage();
				String decodedMessage = swiftBot.decodeQRImage(img);

				if (!decodedMessage.isEmpty()) {
					System.out.println("SUCCESS: QR code found");
					System.out.println("Decoded message: " + decodedMessage);
					break;
				}
				
				++attempts;

			} catch (Exception e) {
				System.out.println("Unable to find QR code...trying again...");
				++attempts;
			}
		}
	}
}