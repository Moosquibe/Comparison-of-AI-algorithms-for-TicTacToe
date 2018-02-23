import java.util.Scanner;

public class TicTacToe {
	final static int MAX_WIDTH = 20;
	final static int MAX_HEIGHT = 20;
	final static int MIN_WIDTH = 3;
	final static int MIN_HEIGHT = 3;

	private static Game game;
	private static Scanner input = new Scanner(System.in);

	public static void main(String[] args) {
		String move;
		initialize();
		while(true) {
			while (game.getGameStatus() == 0) {
				move = getPlayersMove();
				try{
					game.step(move);
					// if (stringResponse.length() == 1 && stringResponse.charAt(0) == 'Q')
					// 	game.gameOn = false;
					// else if (game.isLegalMove(stringResponse)) {
				}
				catch(Exception e) {
				 	System.out.print(e.getMessage());
				 	input.nextLine();
				}
			}
			printEndGameMessage();
			if (playAnotherGame()) 
				game.restart(getStartingPlayer());
			else
				break;
		}
	}
	private static void initialize() {
		/* Initializes game using user input */
		int[] dimsOfBoard = getDimsOfBoard();
		int algorithmUsed = getAlgorithmUsed();
		int startingPlayer = getstartingPlayer();

		switch (algorithmUsed) {
			case 1: game = new MinMaxGame(dimsOfBoard, startingPlayer);
					break;
			//case 2: game = new MinMaxGameHashed(dimsOfBoard, startingPlayer);
			// 		break;
			//case 3: game = new AlphaBetaGame(dimsOfBoard, startingPlayer);
			 		break;
			//case 4: game = new UCTGame(dimsOfBoard, startingPlayer);
			// 		break;
		}
	}
	////////////////////////
	// User Input methods //
	////////////////////////
	private static int[] getDimsOfBoard() {
		// Get height and width of the board. Clears screen beforehand.
		int height, width;
		while (true) {
			clearScreen();
			System.out.print("Height of the board (" + MIN_HEIGHT + 
											" - " + MAX_HEIGHT + "): ");
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
			System.out.print("Width of the board ("+ MIN_WIDTH + 
											" - " + MAX_WIDTH + "): ");
			try {
				width = Integer.parseInt(input.next());
				if (MIN_WIDTH <= height && height <= MAX_WIDTH)
					break;
			}
			catch(NumberFormatException nfe) {}
		}
	return new int[] {height, width};
	}
	private static int getAlgorithmUsed() {
		// Choose the algorithm to be used
		int algorithmUsed;
		while (true) {
			clearScreen();
			System.out.print(
				"Which algorithm would you like the computer to use?" +
				"\n\n 1) Minmax search" +
				"\n 2) Minmax search with memoization" +
				"\n 3) AlphaBeta pruning and memoization" +
				"\n 4) UCT Algorithm" +
				"\n\nInput (1-4): ");
			try {
				algorithmUsed = Integer.parseInt(input.next());
				if (algorithmUsed == 1 || algorithmUsed == 2 || 
					algorithmUsed == 3 || algorithmUsed == 4)
					return algorithmUsed;
			}
			catch(NumberFormatException nfe) {}
		}
	}
	private static int getStartingPlayer() {
		// Get if the player wants to start
		char response;
		while (true) {
			clearScreen();
			System.out.print("Would you like to start? (Y/N): ");
			try {
				response = input.nextLine().charAt(0);
				if (response == 'Y') 
					return 1;
				else if (response == 'N') 
					return 2;
			}
			catch(StringIndexOutOfBoundsException e) {}
		}
	}
	private static String getPlayersMove() {
		clearScreen();
		System.out.print(game.boardToString);
		System.out.print(game.playerInputMessage);
		return input.nextLine();
	}
	private static void printEndGameMessage() {
		// Prints message at the end of each game
		clearScreen();
		System.out.print(game.boardToString);
		switch(game.getGameStatus()) {
			case 0: break;
			case 1: System.out.print("\nCongratulations, you won!");
					break;
			case 2: System.out.print("\nThe computer won, better luck next time");
					break;
			case 3: System.out.print("\nNo more possible moves, it's a tie.");
					break;
		}
		input.nextLine();
	}
	private static boolean playAnotherGame() {
		// Asks if the player wants to play another game
		while (true) {
			char response;
			clearScreen();
			System.out.print("Would you like to play another game? (Y/N): ");
			try {
				response = input.nextLine().charAt(0);
				if (response == 'N') 
					return false;
				else if (response == 'Y') 
					return true;
			}
			catch(StringIndexOutOfBoundsException e) {}
		}
	}
	////////////////////
	// Helper methods //
	////////////////////
	private static void clearScreen() {  
		// Clears the console display
    	System.out.print("\033[H\033[2J");  
    	System.out.flush();  
	} 
}