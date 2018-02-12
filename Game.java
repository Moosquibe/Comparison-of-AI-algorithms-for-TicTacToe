import java.util.Scanner;

abstract class Game {
	protected int height, width;
	protected int[][] table;
	protected int[] scoreBoard;
	protected int lengthToWin;

	public Game(int height, int width, boolean playersTurn) {
		/* Initializes a game on a table of size height X width,
		   where lengthToWin consequtive entries are needed for victory
		   either horizontally, vertically, or diagonally. If playersTurn
		   is true then player starts */

		this.width = width;
		this.height = height;
		scoreBoard = new int[] {0,0};

		int area = width * height;

		if (width * height < 16)
			lengthToWin = 3;
		else if (width * height <=  36)
			lengthToWin = 4;
		else
			lengthToWin = 5;
		
		table = new int[height][width];
		clearTable();
		if (!playersTurn)
			computerMoves();
	}

	public void printTable() {
		// Displays the current standing of the board
		System.out.print("Player(X): " + scoreBoard[0] + " Computer (O): " + 
							scoreBoard[1] + "\n\n");
		String output = "  ";
		for(int i = 0; i < width; i++) {
			output += String.format( "|% 3d",i+1);
		}
		output += "|\n====";
		for(int i = 0; i < width; i++) {
			output += "====";
		}
		
		for(int i = 0; i < height; i++) {
			output += "\n" + (char)(65 + i) + "|";
			for(int j = 0; j < width; j++) {
				switch(table[i][j]) {
					case 0: output += "|   ";
							break;
					case 1: output += "| X ";
							break;
					case 2: output += "| O ";
							break;
				}
			}
			output += "|";
		}
		System.out.print(output + "\n\nGoal: Get " + lengthToWin + " in a row.\n\n");
	}
	protected void clearTable() {
		// Put empty values to table
		for(int i = 0; i < height; i++) {
			for(int j = 0; j < width; j++) {
				table[i][j] = 0;
			}
		}
	}

	public boolean isLegalMove(String move) {
		// Checks if the string move represents a legal move
		int moveHeight = ((int)move.charAt(0)) - 65;
		if (moveHeight < 0 || moveHeight > (height - 1)) {
			System.out.print("3");
			System.out.print("\n\nIllegal input!");
			(new Scanner(System.in)).nextLine();
			return false;
		}
		try {
			int moveWidth = Integer.parseInt(move.substring(1,move.length())) - 1;
			if (moveWidth < 0 || moveWidth > width - 1) {
				System.out.print("2");
				System.out.print("\n\nIllegal input!");
				(new Scanner(System.in)).nextLine();
				return false;
			}
			if (table[moveHeight][moveWidth] != 0) {
				System.out.print("\n\nField is already taken!");
				(new Scanner(System.in)).nextLine();
				return false;
			}
		}
		catch (NumberFormatException e) {
			System.out.print("1");
			System.out.print("\n\nIllegal input!");
			(new Scanner(System.in)).nextLine();
			return false;
		}
		return true;
	}
	protected static int whoWon(int[][] table, int lastMoveY, int lastMoveX, int player, int lengthToWin) {
		/* Checks if the game was won after player made a move to
		   (lastMoveX,lastMoveY). Returns:
			O: Nobody has won yet
		   	1: Player won
		   	2: Computer won */

		// Check the if a win was triggered vertically
		int count = 1;
		int currentY = lastMoveY + 1;
		while (currentY < table.length && table[currentY][lastMoveX] == player) {
			count++;
			currentY++;
		}
		currentY = lastMoveY - 1;
		while (currentY >= 0 && table[currentY][lastMoveX] == player) {
			count++;
			currentY--;
		}
		if (count >= lengthToWin) {
			return player;
		}

		// Check the if a win was triggered horizontally
		count = 1;
		int currentX = lastMoveX + 1;
		while (currentX < table[0].length && table[lastMoveY][currentX] == player) {
			count++;
			currentX++;
		}
		currentX = lastMoveX - 1;
		while (currentX >= 0 && table[lastMoveY][currentX] == player) {
			count++;
			currentX--;
		}
		if (count >= lengthToWin) {
			return player;
		}

		// Check the if a win was triggered NW-SE diagonally
		count = 1;
		currentY = lastMoveY + 1;
		currentX = lastMoveX + 1;
		while (currentY < table.length && currentX < table[0].length && 
					table[currentY][currentX] == player) {
			count++;
			currentY++;
			currentX++;
		}
		currentY = lastMoveY - 1;
		currentX = lastMoveX - 1;
		while (currentY >= 0 &&  currentX >= 0 &&
					table[currentY][currentX] == player) {
			count++;
			currentY--;
			currentX--;
		}
		if (count >= lengthToWin) 
			return player;
		
		// Check the if a win was triggered SW-NE diagonally
		count = 1;
		currentY = lastMoveY - 1;
		currentX = lastMoveX + 1;
		while (currentY >= 0 && currentX < table[0].length && 
					table[currentY][currentX] == player) {
			count++;
			currentY--;
			currentX++;
		}
		currentY = lastMoveY + 1;
		currentX = lastMoveX - 1;
		while (currentY < table.length &&  currentX >= 0 &&
					table[currentY][currentX] == player) {
			count++;
			currentY++;
			currentX--;
		}
		if (count >= lengthToWin)
			return player;

		return 0;
	}
	protected int whoWon(int lastMoveY, int lastMoveX, int player) {
		/* Checks if the game was won after player made a move to
		   (lastMoveX,lastMoveY). Returns:
			O: Nobody has won yet
		   	1: Player won
		   	2: Computer won */

		if (Game.whoWon(table, lastMoveY, lastMoveX, player, lengthToWin) == player) {
			scoreBoard[player-1]++;
			return player;
		}
		return 0;
	}
	protected static boolean isFull(int[][] table){
		// Check wether there are no moves yet in table
		for(int i = 0; i < table.length; i++){
			for(int j = 0; j < table[i].length; j++) {
				if (table[i][j] == 0)
					return false;
			}
		}
		return true;
	}
	protected boolean isFull() {
		// Check wether there are no moves yet
		return isFull(table);
	}
	public int step(String move) {
		/* Moves the game ahead by one step. Returns:
			O: Nobody has won yet
		   	1: Player won
		   	2: Computer won 
		   	3: No more legal moves*/

		int move_height = (int)(move.charAt(0)) - 65;
		int move_width = Integer.parseInt(move.substring(1,move.length())) - 1;
		table[move_height][move_width] = 1;
		if (whoWon(move_height, move_width, 1) == 1)
			return 1; // Player won
		else if (isFull())
			return 3; // Tie
		else 
			// Computer's turn
			return computerMoves(); 
	}
	public void restart(boolean PlayerStart) {
		// Restarts the game
		clearTable();
		if (!PlayerStart)
			computerMoves();
	}
	abstract protected int computerMoves();
	/* Computer moves. Afterwards returns
		0 : Nobody won yet
		2 : Computer won
		3 : Table is full 

	   This method will be implemented differently in each subclass
	   according to the algorithm to use. The implementation has to c
	   all whoWon() and isFull() after the step has been executed. */
}