import java.util.Scanner;

public class TicTacToe {
	final static int MAX_WIDTH = 20;
	final static int MAX_HEIGHT = 20;
	final static int MIN_WIDTH = 3;
	final static int MIN_HEIGHT = 3;

	private static Game game;

	public static void main(String[] args) {
		Scanner input = new Scanner(System.in);
		String stringResponse;
		char charResponse;
		boolean progOn = true;
		boolean gameOn = true;
		initialize(input);
		// Main program loop
		while(progOn) {
			// Main game loop
			while (gameOn) {
				clearScreen();
				game.printTable();
				System.out.print("Your move (Q to Quit): ");
				try{
					stringResponse = input.nextLine();
					if (stringResponse.length() == 1 && stringResponse.charAt(0) == 'Q')
						gameOn = false;
					else if (game.isLegalMove(stringResponse)) {
						switch(game.step(stringResponse)) {
							case 0: break;
							case 1: {
								clearScreen();
								game.printTable();
								System.out.print("\nCongratulations, you won!");
								gameOn = false;
								input.nextLine();
								break;
							}
							case 2: {
								clearScreen();
								game.printTable();
								System.out.print("\nThe computer won, better luck next time");
								gameOn = false;
								input.nextLine();
								break;
							}
							case 3: {
								clearScreen();
								game.printTable();
								System.out.print("\nNo more possible moves, it's a tie.");
								gameOn = false;
								input.nextLine();
								break;
							}
						}
					}
				}
				catch(StringIndexOutOfBoundsException e) {
				 	System.out.print("\n\nIllegal input!");
				 	(new Scanner(System.in)).nextLine();
				}
			}
			while (true) {
				clearScreen();
				System.out.print("Would you like to play another game? (Y/N): ");
				try {
					charResponse = input.nextLine().charAt(0);
					if (charResponse == 'N') {
						progOn = false;
						break;
					}
					else if (charResponse == 'Y') {
						gameOn = true;
						clearScreen();
						while (true) {
							clearScreen();
							System.out.print("Would you like to start? (Y/N): ");
							try {
								charResponse = input.nextLine().charAt(0);
								if (charResponse == 'Y') {
									game.restart(true);
									break;
								}
								else if (charResponse == 'N') {
									game.restart(false);
									break;
								}
							}
							catch(RuntimeException e) {}
						}
						break;
					}
				}
				catch(RuntimeException e) {}
			}
		}
	}

	private static void initialize(Scanner input) {
		/* Initializes game using user input */
		boolean playerStart;
		int algorithmUsed;
		int height, width;
		while (true) {
			clearScreen();
			System.out.print("Height of the board (" + MIN_HEIGHT + " - " + MAX_HEIGHT + "): ");
			try {
				height = Integer.parseInt(input.next());
				if (MIN_HEIGHT <= height && height <= MAX_HEIGHT)
					break;
			}
			catch(NumberFormatException nfe) {
			}
		}
		while (true) {
			clearScreen();
			System.out.print("Width of the board ("+ MIN_WIDTH + " - " + MAX_WIDTH + "): ");
			try {
				width = Integer.parseInt(input.next());
				if (MIN_WIDTH <= height && height <= MAX_WIDTH)
					break;
			}
			catch(NumberFormatException nfe) {}
		}
		while (true) {
			clearScreen();
			System.out.print("Which algorithm would you like the computer to use?" +
							"\n\n 1) Minmax search" +
							"\n 2) AlphaBeta pruning" +
							"\n 3) Learned value function" +
							"\n\nInput (1-3): ");
			try {
				algorithmUsed = Integer.parseInt(input.next());
				if (algorithmUsed == 1 || algorithmUsed == 2 || algorithmUsed == 3)
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
		switch (algorithmUsed) {
			case 1: game = new MinMaxGame(height, width, playerStart);
					break;
			case 2: game = new AlphaBetaGame(height, width, playerStart);
					break;
			case 3: game = new LearnedValueGame(height, width, playerStart);
					break;
		}
	}
	private static void clearScreen() {  
		// Clears the console display
    	System.out.print("\033[H\033[2J");  
    	System.out.flush();  
	} 
}