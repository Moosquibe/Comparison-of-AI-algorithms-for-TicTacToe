import java.util.Scanner;

class AlphaBetaGame extends Game {
	private final int MAX_GAME_LENGTH;

	public AlphaBetaGame(int height, int width, boolean playersTurn) {
		super(height, width, playersTurn);
		MAX_GAME_LENGTH = height * width;
	}
	protected class Value implements Comparable<Value>{
		int outcome, lengthOfGame;

		public Value(int outcome, int lengthOfGame) {
			this.outcome = outcome;
			this.lengthOfGame = lengthOfGame;
		}

		public void flipPlayer() {
			outcome = - outcome;
			lengthOfGame += 1;
		}
		public int compareTo(AlphaBetaGame.Value other) {
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
	private class ValueWithMove extends Value {
			int bestMoveY, bestMoveX;

			public ValueWithMove(int outcome, int lengthOfGame, int bestMoveY, int bestMoveX) {
				super(outcome, lengthOfGame);
				this.bestMoveY = bestMoveY;
				this.bestMoveX = bestMoveX;
			}
	}
	@override
	protected int computerMoves() {
		/* Computer moves. Afterwards returns
		0 : Nobody won yet
		2 : Computer won
		3 : Table is full  */
		
		ValueWithMove move = alphaBeta(table, 2, new Value(-1, 0), new Value(1,0));
		System.out.println("Outcome: " + move.outcome + " Length: " + move.lengthOfGame);
		(new Scanner(System.in)).nextLine();
		table[move.bestMoveY][move.bestMoveX] = 2;
		if (whoWon(move.bestMoveY, move.bestMoveX, 2) == 2)
			return 2;
		else if (isFull())
			return 3;
		else
			return 0;
	}
	protected ValueWithMove alphaBeta(int[][] table, int player, Value alpha, Value beta) { 
		// RETURNS: 
		//    First entry:  The value of the position for player when it is his move.
		//    Second entry: The length of the resulting game using optimal play.
		//	  Third entry:  Optimal move Y
		//	  Fourth entry: Optimal move X
		// 
		// NOTE: opponent = 3 - player. The second coordinate is needed so that the 
		// computer player wants to win as quickly as possible or survive as long 
		// as it can.

		// Find out if game is over.
		int whoWon = 0;

		for(int i = 0; i < table.length; i++) {
			for(int j = 0; j < table[i].length; j++) {
				if(table[i][j] == 3 - player) {
					if (Game.whoWon(table, i, j, 3-player, lengthToWin) == 3-player)
						whoWon = 3 - player;
				}
				if(table[i][j] == player) {
					if (Game.whoWon(table, i, j, player, lengthToWin) == player)
						whoWon = player;
				}
			}
		}

		// Return the value if the game is over after opponents move.
		// In this case (-1,-1) for best move in place of null.
		// These final states are only visited during the recursion.
		if (whoWon == player) {
			// printTable();
			// System.out.println("Player won: " + player);
			// (new Scanner(System.in)).nextLine();
			return new ValueWithMove(1, 0, -1, -1);
		}
		else if (whoWon == 3-player) {
			// printTable();
			// System.out.println("Player won: " + (3-player));
			// (new Scanner(System.in)).nextLine();
			return new ValueWithMove(-1, 0, -1, -1);
		}
		else if (Game.isFull(table)) {
			// printTable();
			// System.out.println("Table full!");
			// (new Scanner(System.in)).nextLine();
			return new ValueWithMove(0, 0, -1, -1);
		}

		assert whoWon == 0;

		Value bestValue = alpha;
		ValueWithMove value;

		int moveY=0;
		int moveX=0;

		for(int i = 0; i < table.length; i++) {
			for(int j = 0; j < table[i].length; j++) {
				if(table[i][j] == 0) {
					// Trying move (i,j)
					table[i][j] = player;
					value = alphaBeta(table, 3-player, 
										new Value(-beta.outcome, beta.lengthOfGame-1), 
										new Value(-bestValue.outcome, bestValue.lengthOfGame-1));
					value.flipPlayer();
					table[i][j] = 0;

					if (value.compareTo(bestValue) == 1) {
						bestValue = value;
						moveY = i;
						moveX = j;
					}

					if (bestValue.compareTo(beta) == 1) {
						return new ValueWithMove(bestValue.outcome,bestValue.lengthOfGame, 
																				moveY, moveX);
					}
				}
			}
		}
	return new ValueWithMove(bestValue.outcome,bestValue.lengthOfGame, moveY, moveX);
	}
}