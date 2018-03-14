import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

class UCTGame extends Game {
	/* Implements the UCT algorithm. This is a Monte Carlo Tree search
	with tree policy given by the UCB1 selection method. This is very
	different in nature from the deterministic NegaMax tree search in
	the previous implementations.*/
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
	////////////////////////
	// UCT Implementation //
	////////////////////////
	final long MAX_SIMULATIONS = 1000000; //Long.MAX_VALUE; // Max UCT iterations in one move.
	final double CP = 1/Math.sqrt(2);
	private class Node {
		/* This encodes the game tree as nodes */
		// Node value variables
		int numOfVisits = 0;
		double totalReward = 0;
		// State of the game at node
		int[][] board;
		int activeAgent;
		int[] lastMove;
		ArrayList unexploredMoves;
		// Familial relations of Node
		Node parent;
		ArrayList children;
		// Useful internal variables
		int terminalAndOutcome; // -1 if not terminal, result otherwise
		int numberOfMoves; // Number of moves taken to get to current node


		public Node(int[][] board, Node parent, int[] lastMove, 
					int activeAgent) {
			this.parent = parent;
			this.lastMove = lastMove;
			this.activeAgent = activeAgent;
			this.board = board;

			if (lastMove == null) {
				// Beginning of the game
				terminalAndOutcome = -1;
			}
			else if (Game.winOnLastMove(board, this.lastMove, lengthToWin, 
								   activeAgent)) {
				terminalAndOutcome = 3-activeAgent;
			}
			else if (Game.isFull(board))
				terminalAndOutcome = 0;
			else {
				terminalAndOutcome = -1;
			}
			if (terminalAndOutcome == -1) {
				unexploredMoves = new ArrayList<int[]>();
				children = new ArrayList<Node>();
			}
			numberOfMoves = 0;
			for(int i = 0; i < board.length; i++) {
				for(int j = 0; j < board[i].length; j++) {
					if (this.board[i][j] == 0) {
						if (terminalAndOutcome == -1)
							unexploredMoves.add(new int[] {i,j});
					}
					else
						numberOfMoves += 1;
				}
			}
			if (terminalAndOutcome == -1)
				Collections.shuffle(unexploredMoves);
		}
		//////////////////////////
		// UCT specific methods //
		//////////////////////////
		protected Node expand() {
			/* Randomly selects a previously unexplored child and returns
			it. After last child is added, the 
			state variable is freed up to save memory. */
			if (!isFullyExpanded()) {
				int[] nextMove = (int[]) unexploredMoves.get(unexploredMoves.size()-1);
				unexploredMoves.remove(unexploredMoves.size()-1);
				int[][] newBoard = deepCopy(board);
				if (isFullyExpanded()) {
					board = null;
				}
				newBoard[nextMove[0]][nextMove[1]] = activeAgent;
				Node newNode = new Node(newBoard, this, nextMove,3-activeAgent);
				children.add(newNode);
				return newNode;
			}
			return null;
		}
		public Node getRandomChild() {
			int[] nextMove = (int[]) unexploredMoves.get(unexploredMoves.size()-1);
			int[][] newBoard = board.clone();
			newBoard[nextMove[0]][nextMove[1]] = activeAgent;
			return new Node(newBoard, null, nextMove, 3-activeAgent);
		}
		public Node getBestChild(double c) {
			/* Selects best child using UCB1 */
			if (children != null && children.size() != 0) {
				double current_best_UCB = -Double.MAX_VALUE;
				Node bestChild = null;
				for(Object obj : children) {
					Node child = (Node)obj;
					double exploitPart = 
						((double)child.totalReward)/child.numOfVisits;
					double explorePart = 
						Math.log(numOfVisits) / child.numOfVisits;
					explorePart = c * Math.sqrt(2 * explorePart);
					double Ucb = exploitPart + explorePart;
					if (Ucb > current_best_UCB) {
						current_best_UCB = Ucb;
						bestChild = child;
					}
				}
				return bestChild;
			}
			return null;
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
			return (children == null || children.size() == 0);
		}
		public boolean isTerminal() {
			return (terminalAndOutcome  != -1);
		}
		public boolean isFullyExpanded() {
			return (isTerminal() || unexploredMoves.size() == 0);
		}
		public int getActiveAgent() {
			return activeAgent;
		}
	}
	//////////////////////////
	// UCT implementation //
	//////////////////////////
	private int[] UCTSearch() {
		Node root = new Node(deepCopy(board), null, null, activeAgent);
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
		return root.getBestChild(0).lastMove;
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
		Then normalize to [0, 1]*/

		Node currentNode = leaf;
		// Random rollout
		while(!currentNode.isTerminal()) {
			currentNode = currentNode.getRandomChild();
		}
		// Assigning reward
		int maxMoves = board.length * board[0].length;
		double reward;
		if (currentNode.terminalAndOutcome == leaf.activeAgent)
			reward = maxMoves - currentNode.numberOfMoves + 1;
		else if (currentNode.terminalAndOutcome == 3 - leaf.activeAgent)
			reward = - maxMoves + currentNode.numberOfMoves;
		else
			reward = 0;
		return (reward + maxMoves) / (2 * maxMoves);
		//return reward;
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