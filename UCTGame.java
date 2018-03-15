import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

class UCTGame extends Game {
	/* Implements the UCT algorithm. This is a Monte Carlo Tree search
	with tree policy given by the UCB1 selection method. This is very
	different in nature from the deterministic NegaMax tree search in
	the previous implementations.*/
	///////////////////////////////////
	// Compatibility with Game class //
	///////////////////////////////////
	public UCTGame(int[] dimsOfBoard, int startingPlayer) {
		super(dimsOfBoard, startingPlayer);
	}
	protected Value valueOfMove(int i, int j, boolean keepLastMove) {
		/* Not using this method, overriding getBestMove() directly */
		return null;
	}
	@Override
	protected int[] getBestMove() {
 		/* Returns the best move by the active agent in the current state
 		   of the board. */
 		assert activeAgent == 2;
		return UCTSearch();
	}
	//////////////////////////////////////////////////
	// Class representing a vertex of the game tree //
	//////////////////////////////////////////////////
	private class Node {
		/* This encodes the game tree as nodes */
		// Node value variables
		private int numOfVisits = 0;
		private double totalReward = 0;
		// State of the game at node
		private int[][] board;
		private int activeAgent;
		private int[] lastMove;
		private ArrayList unexploredMoves = new ArrayList<int[]>();
		// Familial relations of Node
		private Node parent;
		private ArrayList children = new ArrayList<Node>();
		// Useful internal variables
		private int terminalOutcome; // -1 if not terminal, result otherwise
		private int numberOfMoves; // Number of moves taken to get to current node

		public Node(int[][] board, Node parent, int[] lastMove, 
					int activeAgent) {
			this.parent = parent;
			this.lastMove = lastMove;
			this.activeAgent = activeAgent;
			this.board = UCTGame.deepCopy(board);

			collectAvailableMoves(); // Sets unexploredMoves, numberOfMoves
			if (initialIsTerminal()) {
				unexploredMoves = null;
				children = null;
			}
			else {
				Collections.shuffle(unexploredMoves);
			}
		}
		//////////////////////////
		// UCT specific methods //
		//////////////////////////
		public Node expand() {
			/* Randomly selects a previously unexplored child and returns
			it. After last child is added, the board variable is freed up to 
			save memory. */
			assert (!isFullyExpanded());
			Object rawMove = unexploredMoves.get(unexploredMoves.size()-1);
			unexploredMoves.remove(unexploredMoves.size()-1);
			int[] move = (int[])rawMove;
			board[move[0]][move[1]] = activeAgent;
			Node newNode = new Node(board, this, move, 3-activeAgent);
			children.add(newNode);
			if (isFullyExpanded()) {
				if (!isTerminal())
					board = null;
			}
			else
				board[move[0]][move[1]] = 0;
			assert (isFullyExpanded() || (children.size() < board.length * board[0].length - 
									  numberOfMoves));
			return newNode;
		}
		public Node getRandomChild() {
			/* Creates  and returns a random child of the current node without 
			either adding it to the list of children or making it keep track
			of its parent */
			assert (isLeaf());
			assert (!isTerminal());
			Object rawMove = unexploredMoves.get(unexploredMoves.size()-1);
			int[] move = (int[])rawMove;
			board[move[0]][move[1]] = activeAgent;
			Node newNode = new Node(board, null, move, 3-activeAgent);
			board[move[0]][move[1]] = 0;
			return newNode;
		}
		public Node getBestChild(double c) {
			/* Selects best child using UCB1 */
			assert (!isLeaf());
			assert (isFullyExpanded() || children.size() <= board.length * board[0].length - 
						  numberOfMoves);
			double currentBestUcb = -Double.MAX_VALUE;
			Node bestChild = null;
			for(Object obj : children) {
				Node child = (Node)obj;
				double ucb = child.getUcb(c);

				if (ucb > currentBestUcb) {
					currentBestUcb = ucb;
					bestChild = child;
				}
			}
			assert (bestChild != null);
			return bestChild;
		}
		public void backup(double reward) {
			numOfVisits += 1;
			totalReward = totalReward + reward;
			if (parent != null) {	
				parent.backup(-reward);
			}
		}
		//////////////////////////
		// Diagnostic functions //
		//////////////////////////
		public boolean isLeaf() {
			return (isTerminal() || children.size() == 0);
		}
		public boolean isTerminal() {
			return (terminalOutcome  != -1);
		}
		public boolean isFullyExpanded() {
			return (isTerminal() || unexploredMoves.size() == 0);
		}
		public double getUcb(double c) {
			assert numOfVisits != 0;
			double exploitPart = totalReward / numOfVisits;
			double explorePart = Math.log(parent.numOfVisits) / numOfVisits;
			explorePart = c * Math.sqrt(2 * explorePart);
			return exploitPart + explorePart;
		}
		////////////////////
		// Getter/Setters //
		////////////////////
		public int getActiveAgent() {
			return activeAgent;
		}
		public int getNumberOfMoves() {
			return numberOfMoves;
		}
		public int getTerminalAndOutcome() {
			return terminalOutcome;
		}
		public int getNumOfVisits() {
			return numOfVisits;
		}
		public double getTotalReward() {
			return totalReward;
		}
		public int getTerminalOutcome() {
			assert isTerminal() == true;
			return terminalOutcome;
		}
		public Node getParent() {
			return parent;
		}
		public int[][] getBoard() {
			return board;
		}
		public void setParent(Node parent) {
			this.parent = parent;
		}
		public Node getChild(int[] move) {
			/* Finds the child corresponding to move or returns null */
			if (children == null)
				return null;
			for (Object obj : children) {
				Node child = (Node)obj;
				if (Arrays.equals(child.lastMove, move))
					return child;
			}
			return null;
		}
		/////////////
		// Helpers //
		/////////////
		private boolean initialIsTerminal() {
			if (lastMove != null &&  Game.winOnLastMove(this.board, 
															  this.lastMove, 
															  lengthToWin, 
					 										  activeAgent)) {
				terminalOutcome = 3-activeAgent; // Opponent won
				return true;
			}
			else if (lastMove != null && Game.isFull(board)) {
				terminalOutcome = 0; // Tie
				return true;
			}
			else {
				terminalOutcome = -1;
				return false;
			}
		}
		private void collectAvailableMoves() {
			numberOfMoves = 0;
			for(int i = 0; i < board.length; i++) {
				for(int j = 0; j < board[i].length; j++) {
					if (board[i][j] == 0)
						unexploredMoves.add(new int[] {i,j});
					else
						numberOfMoves += 1;
				}
			}
			assert (numberOfMoves + unexploredMoves.size() == 
				   board.length * board[0].length);
		}
	}
	//////////////////////////
	// UCT implementation ////
	//////////////////////////
	final long MAX_SIMULATIONS = 100000; //Long.MAX_VALUE; // Max UCT iterations in one move.
	final double CP = 0.70710678118654746;
	Node root;

	private int[] UCTSearch() {
		/* If this is the first search then root is null. Otherwise it is the 
		child of the root of the previous search tree corresponding to the
		move actually taken */
		if (root == null)
			root = new Node(board, null, null, activeAgent);
		else {
			/* Need to extract the subtree corresponding to the move of the
			human player */
			root = root.getChild(lastMove);
			if (root == null)
				root = new Node(board, null, null, activeAgent);
			else
				root.setParent(null); // Discard the rest of the tree
		}
		int simulations = 0;

		while (simulations <= MAX_SIMULATIONS) {
			Node leaf = treePolicy(root);
			double reward = defaultPolicy(leaf);
			leaf.backup(reward);
			// Stop search if tree has been exhausted.
			if (leaf.isTerminal()) {
				if (leaf.parent.isFullyExpanded()) {
					break;
				}
			}
			simulations++;
		}
		// Change root by the selected move
		root = root.getBestChild(0);
		root.setParent(null); // Discard the rest of the tree
		return root.lastMove;
	}
	private Node treePolicy(Node root) {
		/* Uses UCB to search the tree for best leaf.*/
		Node currentNode = root;
		while (!currentNode.isTerminal()){
			if (!currentNode.isFullyExpanded()) {
				return currentNode.expand();
			}
			else {
				currentNode = currentNode.getBestChild(CP);
 			}
		}
		return currentNode;
	}
	private double defaultPolicy(Node leaf) {
		/* Uses uniformly random moves to reach a terminal state.
		Reward is calculated as follows: 
		   	Tie: 0
		   	Victory for leaf activeAgent: maxMoves - movesAtEnd + 1 
		   	Loss for leaf activeAgent: -maxMoves + movesAtEnd - 1
		Then possibly normalize to [0, 1]*/
		Node currentNode = leaf;
		// Random rollout
		while(!currentNode.isTerminal()) {
			currentNode = currentNode.getRandomChild();
		}
		// Assigning reward
		//return (reward + areaOfBoard) / (2 * maxMoves);
		return assignReward(leaf, currentNode);
	}
	double assignReward(Node leaf, Node terminalNode) {
		assert terminalNode.isTerminal();
		if (terminalNode.getTerminalOutcome() == 3 - leaf.getActiveAgent())
			return areaOfBoard - terminalNode.getNumberOfMoves() + 1;
		else if (terminalNode.getTerminalOutcome() == leaf.getActiveAgent())
			return - areaOfBoard + terminalNode.getNumberOfMoves();
		else
			return 0;
	}
	//////////////////
	// Miscellanous //
	//////////////////
	public static int[][] deepCopy(int[][] original) {
	    if (original == null) {
	        return null;
	    }
	    final int[][] result = new int[original.length][];
	    for (int i = 0; i < original.length; i++) {
	        result[i] = Arrays.copyOf(original[i], original[i].length);
	    }
	    return result;
	}
}