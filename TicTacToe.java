import java.util.Scanner;

public class TicTacToe {
	final static int MAX_WIDTH = 30;
	final static int MAX_HEIGHT = 25;

	private static Game game;

	public static void main(String[] args) {
		Scanner input = new Scanner(System.in);
		boolean gameOn = true
		initialize(input);
		while (gameOn) {
			clearScreen();
			Game.printTable();
		}
	}

	private static void initialize(Scanner input) {
		boolean playerStart;
		int algorithmUsed;

		int height, width;
		while (true) {
			clearScreen();
			System.out.print("Height of the board (1 - " + MAX_HEIGHT + "): ");
			try {
				height = Integer.parseInt(input.next());
				if (0 <= height && height <= MAX_HEIGHT)
					break;
			}
			catch(NumberFormatException nfe) {
			}
		}
		while (true) {
			clearScreen();
			System.out.print("Width of the board (1 - " + MAX_WIDTH + "): ");
			try {
				width = Integer.parseInt(input.next());
				if (0 <= height && height <= MAX_WIDTH)
					break;
			}
			catch(NumberFormatException nfe) {}
		}


		while (true) {
			clearScreen();
			System.out.print("Which algorithm would you like the computer to use?" +
							"\n\n 1) Brute force search" +
							"\n 2) AlphaBeta pruning" +
							"\n\nInput (1-2): ");
			try {
				algorithmUsed = Integer.parseInt(input.next());
				if (algorithmUsed == 1 || algorithmUsed == 2)
					break;
			}
			catch(NumberFormatException nfe) {}
		}

		char playerStartChar;
		while (true) {
			clearScreen();
			System.out.print("Would you like to start? (Y/N): ");
			try {
				playerStartChar = input.nextLine().charAt(0);
				if (playerStartChar == 'Y') {
					playerStart = true;
					break;
				}
				else if (playerStartChar == 'N') {
					playerStart = false;
					break;
				}
			}
			catch(RuntimeException e) {}
		}


	}

	private static void clearScreen() {  
    	System.out.print("\033[H\033[2J");  
    	System.out.flush();  
	} 
}