abstract class Game {
	// Not changing on execution
	protected String playerInputMessage = "Your move (Q to Quit): ";
	protected int[][] board;
	protected int[] scoreBoard;
	protected int lengthToWin;
	protected int areaOfBoard;
	// Changing on execution
	protected int[] lastMove;
	protected int activeAgent;
	protected int movesCompleted;
	protected int gameStatus;

	protected class Value implements Comparable<Value>{
		/* Represents the values of positions from the viewpoint of the player
		   whose turn it is. The value of a position is -1 if it leads to 
		   certain loss, 1 if it leads to certain victory (assuming optimal 
		   play) and zero if it leads to a tie. This is the default 
		   implementation, override if necessary by defining a subclass of it 
		   nested in a subclass of game. */
		int outcome, lengthOfGame;
		public Value(int outcome, int lengthOfGame) {
			this.outcome = outcome;
			this.lengthOfGame = lengthOfGame;
		}
		public void flipValue() {
			outcome = -outcome;
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
	/////////////////////
	// Initializiation //
	/////////////////////
	public Game() {}
	public Game(int[] dimsOfBoard, int startingPlayer) {
		/* Initializes a game on a board of size height X width,
		   where lengthToWin consequtive entries are needed for victory
		   either horizontally, vertically, or diagonally. If playersTurn
		   is true then human player starts. */
		scoreBoard = new int[] {0,0};
		board = new int[dimsOfBoard[0]][dimsOfBoard[1]];
		areaOfBoard = dimsOfBoard[0] * dimsOfBoard[1];
		lastMove = new int[] {-1, -1};
		setVictoryCondition();
		restart(startingPlayer);
	}
	protected void setVictoryCondition() {
		int area = board.length * board[0].length;
		if (area < 16)
			lengthToWin = 3;
		else if (area <  36)
			lengthToWin = 4;
		else
			lengthToWin = 5;
	}
	protected void restart(int startingPlayer) {
		// Restarts the game
		gameStatus = 0;
		movesCompleted = 0;
		clearBoard();
		activeAgent = startingPlayer;
		if (activeAgent == 2)
			computerMoves();
	}
	///////////////////
	// Game dynamics //
	///////////////////
	public void step(String move) throws Exception {
		/*  Human player makes a step followed by the AI making one.
		    Checks for victory */
		assert activeAgent == 1;
		if (move.length() == 1 && move.charAt(0) == 'Q') {
			gameStatus = 4;
			return;
		}
		lastMove = getMove(move);
		board[lastMove[0]][lastMove[1]] = 1;
		movesCompleted += 1;
		if (winOnLastMove()) {
			// Player won
			scoreBoard[0] += 1;
			gameStatus = 1;
		}
		else if (isFull())
			gameStatus = 3; // Tie
		else {
			// Computer's turn
			activeAgent = 2;
			computerMoves(); 
		}
	}
	protected void computerMoves() {
		/* Computer moves. */
		//System.out.println("Thinking...");
		lastMove = getBestMove();
		
		board[lastMove[0]][lastMove[1]] = 2;
		movesCompleted += 1;
		if (winOnLastMove()) {
			scoreBoard[1] += 1;
			gameStatus = 2;
		}
		else if (isFull())
			gameStatus = 3;
		else
			activeAgent = 1;
	}
	protected int[] getBestMove() {
		/* Getting best move at a current state of the board.  */
		assert activeAgent == 2;
		Value bestValue = new Value(-2,0); // Acts as -infty
		Value newValue;
		int[] bestMove = new int[] {-1,-1};
		for(int i = 0; i < board.length; i++) {
			for(int j = 0; j < board[i].length; j++) {
				if (board[i][j] == 0) {
					// Trying out move (i,j)
					newValue = valueOfMove(i,j, true);
					if (newValue.compareTo(bestValue) == 1) {
						bestValue = newValue;
						bestMove = new int[] {i, j};
					}
				}
			}
		}
		assert bestMove[0] != -1 && bestMove[1] != -1;
		assert board[bestMove[0]][bestMove[1]] == 0;
		return bestMove;
	}
	protected abstract Value valueOfMove(int i, int j, boolean keepLastMove);
	/* This method will be implemented differently in 
	   each subclass according to the algorithm to use.*/

	///////////////////////////
	// Endcondition checking //
	///////////////////////////
	protected boolean winOnLastMove() {
		return Game.winOnLastMove(board, lastMove, lengthToWin, activeAgent);
	}
	public static boolean winOnLastMove(int[][] board, int[] lastMove,
										int lengthToWin, int activeAgent){
		// Checks if the last move triggers a win
		// 1. Check if a win was triggered vertically
		int count = 1;
		int currentY = lastMove[0] + 1;
		while (currentY < board.length && 
			   board[currentY][lastMove[1]] == activeAgent) {
			count++;
			currentY++;
		}
		currentY = lastMove[0] - 1;
		while (currentY >= 0 && 
			   board[currentY][lastMove[1]] == activeAgent) {
			count++;
			currentY--;
		}
		if (count >= lengthToWin) {
			return true;
		}
		// 2. Check the if a win was triggered horizontally
		count = 1;
		int currentX = lastMove[1] + 1;
		while (currentX < board[0].length && 
			   board[lastMove[0]][currentX] == activeAgent) {
			count++;
			currentX++;
		}
		currentX = lastMove[1] - 1;
		while (currentX >= 0 && 
			   board[lastMove[0]][currentX] == activeAgent) {
			count++;
			currentX--;
		}
		if (count >= lengthToWin) {
			return true;
		}
		// 3. Check the if a win was triggered NW-SE diagonally
		count = 1;
		currentY = lastMove[0] + 1;
		currentX = lastMove[1] + 1;
		while (currentY < board.length && currentX < board[0].length && 
					board[currentY][currentX] == activeAgent) {
			count++;
			currentY++;
			currentX++;
		}
		currentY = lastMove[0] - 1;
		currentX = lastMove[1] - 1;
		while (currentY >= 0 &&  currentX >= 0 &&
					board[currentY][currentX] == activeAgent) {
			count++;
			currentY--;
			currentX--;
		}
		if (count >= lengthToWin) {
			return true;
		}
		// 4. Check the if a win was triggered SW-NE diagonally
		count = 1;
		currentY = lastMove[0] - 1;
		currentX = lastMove[1] + 1;
		while (currentY >= 0 && currentX < board[0].length && 
					board[currentY][currentX] == activeAgent) {
			count++;
			currentY--;
			currentX++;
		}
		currentY = lastMove[0] + 1;
		currentX = lastMove[1] - 1;
		while (currentY < board.length &&  currentX >= 0 &&
					board[currentY][currentX] == activeAgent) {
			count++;
			currentY++;
			currentX--;
		}
		if (count >= lengthToWin) {
			return true;
		}
		return false;
	}

	protected boolean isFull() {
		return Game.isFull(board);
	}
	public static boolean isFull(int[][] board) {
		// Check whether there are no moves left in board
		for(int i = 0; i < board.length; i++){
			for(int j = 0; j < board[i].length; j++) {
				if (board[i][j] == 0)
					return false;
			}
		}
		return true;
	}
	///////////////////////////////
	// Diagnostic getter methods //
	///////////////////////////////
	public int getGameStatus() {
		return gameStatus;
	}
	public String boardToString() {
		return Game.boardToString(board, scoreBoard, lengthToWin);
	}
	public static String boardToString(int[][] board, int[] scoreBoard,
									   int lengthToWin) {
		int height = board.length;
		int width = board[0].length;
		// Displays the current standing of the board
		String output = "Player(X): " + scoreBoard[0] + " Computer (O): " + 
							scoreBoard[1] + "\n\n ";
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
				switch(board[i][j]) {
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
		output += "\n\nGoal: Get " + lengthToWin + " in a row.\n\n";
		return output;
	}
	////////////////////
	// Helper methods //
	////////////////////
	protected void clearBoard() {
		// Put empty values to board
		int height = board.length;
		int width = board[0].length;
		for(int i = 0; i < height; i++) {
			for(int j = 0; j < width; j++) {
				board[i][j] = 0;
			}
		}
	}
	protected int[] getMove(String move) throws Exception {
		// Returns the coordinate
		int height = board.length;
		int width = board[0].length;
		try {
			int moveHeight = ((int)move.charAt(0)) - 65;
			int moveWidth = Integer.parseInt(move.substring(1,move.length()))-1;
			if (moveHeight < 0 || moveHeight > (height - 1)) 
				throw new Exception("\n\nIllegal input!");
			if (moveWidth < 0 || moveWidth > width - 1) 
				throw new Exception("\n\nIllegal input!");
			if (board[moveHeight][moveWidth] != 0)
				throw new Exception("\n\nField is already taken!");
			return new int[] {moveHeight, moveWidth};
		}
		catch (NumberFormatException | StringIndexOutOfBoundsException e) {
			throw new Exception("\n\nIllegal input!");
		}
	}
}
