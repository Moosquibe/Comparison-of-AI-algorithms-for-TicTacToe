class UCTGame extends Game {
	public UCTGame(int height, int width, boolean playersTurn) {
		super(height, width, playersTurn);
	}

	protected int computerMoves() {
		/* Computer moves. Afterwards returns
		0 : Nobody won yet
		2 : Computer won
		3 : Table is full  */

		int moveY = 0;
		int moveX = 0;
		
		if (whoWon(moveY, moveX, 2) == 2)
			return 2;
		else if (isFull())
			return 3;
		else
			return 0;
	}
}