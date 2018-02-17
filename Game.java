import java.util.Scanner;

abstract class Game {
	protected int height, width;
	protected int[][] table;
	protected int[] scoreBoard;
	protected int lengthToWin;
	protected int lastMoveY, lastMoveX;
	protected int activeAgent;
	protected int movesCompleted;

	protected class Value implements Comparable<Value>{
		// Represents the values of positions. This is the default implementation,
		// override if necessary by defining a subclass of it nested in a subclass
		// of game.

		int outcome, lengthOfGame;

		public Value(int outcome, int lengthOfGame) {
			this.outcome = outcome;
			this.lengthOfGame = lengthOfGame;
		}
		public void flipValue() {
			outcome = - outcome;
			lengthOfGame += 1;
		}
		public int compareTo(Value other) {
			// Winning is better than losing and winning sooner is
			// better than later and losing later is better than sooner
			if (outcome > other.outcome)
				return 1;
			else if (outcome < other.outcome) 
				return -1;
			else {
				if (outcome == 1) 
					return (lengthOfGame < other.lengthOfGame) ? 1 : -1;
				else if (outcome == -1)
					return (lengthOfGame > other.lengthOfGame) ? 1 : -1;
				else
					return 0;
			}
		}
	}
	protected class ValueWithMove extends Value {
			// Value of a position with the best move out of that position
			int bestMoveY, bestMoveX;

			public ValueWithMove(int outcome, int lengthOfGame, int bestMoveY, int bestMoveX) {
				super(outcome, lengthOfGame);
				this.bestMoveY = bestMoveY;
				this.bestMoveX = bestMoveX;
			}
	}

	public Game(int height, int width, boolean playersTurn) {
		/* Initializes a game on a table of size height X width,
		   where lengthToWin consequtive entries are needed for victory
		   either horizontally, vertically, or diagonally. If playersTurn
		   is true then human player starts. */

		this.width = width;
		this.height = height;
		activeAgent = (playersTurn == true) ? 1 : 2;
		scoreBoard = new int[] {0,0};
		movesCompleted = 0;

		int area = width * height;

		// Setting the condition for victory
		if (area < 16)
			lengthToWin = 3;
		else if (area <=  36)
			lengthToWin = 4;
		else
			lengthToWin = 5;
		
		table = new int[height][width];
		clearTable();
		if (activeAgent == 2)
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
	protected boolean winOnLastMove() {
		// Checks if the last move triggers a win

		// Check if a win was triggered vertically
		int count = 1;
		int currentY = lastMoveY + 1;
		while (currentY < table.length && table[currentY][lastMoveX] == activeAgent) {
			count++;
			currentY++;
		}
		currentY = lastMoveY - 1;
		while (currentY >= 0 && table[currentY][lastMoveX] == activeAgent) {
			count++;
			currentY--;
		}
		if (count >= lengthToWin) {
			return true;
		}
		// Check the if a win was triggered horizontally
		count = 1;
		int currentX = lastMoveX + 1;
		while (currentX < table[0].length && table[lastMoveY][currentX] == activeAgent) {
			count++;
			currentX++;
		}
		currentX = lastMoveX - 1;
		while (currentX >= 0 && table[lastMoveY][currentX] == activeAgent) {
			count++;
			currentX--;
		}
		if (count >= lengthToWin) {
			return true;
		}
		// Check the if a win was triggered NW-SE diagonally
		count = 1;
		currentY = lastMoveY + 1;
		currentX = lastMoveX + 1;
		while (currentY < table.length && currentX < table[0].length && 
					table[currentY][currentX] == activeAgent) {
			count++;
			currentY++;
			currentX++;
		}
		currentY = lastMoveY - 1;
		currentX = lastMoveX - 1;
		while (currentY >= 0 &&  currentX >= 0 &&
					table[currentY][currentX] == activeAgent) {
			count++;
			currentY--;
			currentX--;
		}
		if (count >= lengthToWin) {
			return true;
		}

		// Check the if a win was triggered SW-NE diagonally
		count = 1;
		currentY = lastMoveY - 1;
		currentX = lastMoveX + 1;
		while (currentY >= 0 && currentX < table[0].length && 
					table[currentY][currentX] == activeAgent) {
			count++;
			currentY--;
			currentX++;
		}
		currentY = lastMoveY + 1;
		currentX = lastMoveX - 1;
		while (currentY < table.length &&  currentX >= 0 &&
					table[currentY][currentX] == activeAgent) {
			count++;
			currentY++;
			currentX--;
		}
		if (count >= lengthToWin) {
			return true;
		}

		return false;
	}
	protected boolean isFull(){
		// Check whether there are no moves left in table
		for(int i = 0; i < table.length; i++){
			for(int j = 0; j < table[i].length; j++) {
				if (table[i][j] == 0)
					return false;
			}
		}
		return true;
	}
	public int step(String move) {
		/*  Human player makes a step followed by the AI making one.
		Returns:
			O: Nobody has won yet
		   	1: Player won
		   	2: Computer won 
		   	3: No more legal moves*/

		assert activeAgent == 1;

		lastMoveY = (int)(move.charAt(0)) - 65;
		lastMoveX = Integer.parseInt(move.substring(1,move.length())) - 1;
		table[lastMoveY][lastMoveX] = 1;
		movesCompleted += 1;
		if (winOnLastMove()) {
			// Player won
			scoreBoard[0] += 1;
			return 1;
		}
		else if (isFull())
			return 3; // Tie
		else {
			// Computer's turn
			activeAgent = 2;
			return computerMoves(); 
		}
	}
	public void restart(boolean playerStart) {
		// Restarts the game
		clearTable();
		if (!playerStart)
			computerMoves();
	}
	abstract protected int computerMoves();
	/* Computer moves. Afterwards returns
		0 : Nobody won yet
		2 : Computer won
		3 : Table is full 

	   This method will be implemented differently in each subclass
	   according to the algorithm to use.*/
}