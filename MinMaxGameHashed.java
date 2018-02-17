import java.util.HashMap;

class MinMaxGameHashed extends MinMaxGame {
	private HashMap<Integer, ValueWithMove> dataBase = new HashMap<Integer, ValueWithMove>(1000000);

	public MinMaxGameHashed(int height, int width, boolean playersTurn) {
		super(height, width, playersTurn);
	}
	@Override
	protected ValueWithMove getBestMove() {
		// Getting best move at a current state of the table.
		// RETURNS: Value of position and best move realizing that value.
		//    
		// NOTE: opponent = 3 - player. The second coordinate is needed so that the 
		// computer player wants to win as quickly as possible or survive as long 
		// as it can.
		//
		// Return the value if the game is over after opponents move.
		// In this case (-1,-1) for best move in place of null.

		int key = getKey();
		ValueWithMove entry = dataBase.get(key);
		if (entry == null) {
			entry = super.getBestMove();
			dataBase.put(key, entry);
		}
		return entry;
	}
	private int getKey() {
		int temp = 0;
		for (int i=0; i < table.length; i++)
			for(int j=0; j < table[i].length; j++)
				temp = 3*temp + table[i][j];
		return 3*temp+activeAgent;
	}
}