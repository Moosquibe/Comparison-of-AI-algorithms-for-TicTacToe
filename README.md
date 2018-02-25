# Implementation of a couple of  AI algorithms for TicTacToe

I am currently working on implementing a simple generalized Tic-Tac-Toe game on a board of arbitrary size (so that there are relatively small amount of implementation details) using (1) Evolutionary algorithms: (a) Brute Force [Negamax](https://en.wikipedia.org/wiki/Negamax) search of the game tree; (b) Alpha-Beta Pruning of the game tree; (2) Reinforcement learning based on a learned value function. Eventually, I want to use a neural network to learn this value function by having my program battle my friends. The main file is TicTacToe.java.


Mini-Diary: 

**2/1/2018**: The naive minmax search is working. As expected, it is terribly inefficient and cannot handle larger than a 3x4 board. This is not surprising at all as the search space for the $k$th move is $(n+1-k)!$ large, where $n$ stands for the area of the board. Thus the total running time to play a game (if the computer was playing against itself) would be given by the [Smarandache-Kurepa Function](http://mathworld.wolfram.com/LeftFactorial.html):

$$K(n)=\sum_{k=1}^n(n+1-k)!=\sum_{k=1}^n k! $$

Here just the largest term is $16! = 20922789888000 \approx 2 * 10^{13}$ for a 4x4 board. Thankfully for a 3x3 board, it is only $9! = 362880\approx 3.5 * 10^5$. Big difference.

(You might need [GitHub with Math Jax](https://chrome.google.com/webstore/detail/github-with-mathjax/ioemnmodlmafdkllaclgeombjnmnbima/related) chrome extension to view the formula rendered.)

Next I will try to speed up the naive recursion by implementing memoization through hashing the already evaluated game configurations so that no reexploration is necessary and see what kind of speed-up I can get.

**2/3/2018** Fixed a little bug so that the computer now tries to win as fast as it can or survive as long as it can even if it would surely loose with optimal play from all players.

**2/3/2018** Implemented memoization using a hashmap to avoid having to recompute and now the program can handle a 4x4 board, although not gracefully. Since this is an inefficient baseline algorithm anyway, there is no point in optimizing it further and thus next I will move on to implementing the AlphaBetaPruning.

**2/11/2018** Implemented the alpha-beta pruning algorithm. Unfortunately, the speedup isn't enough to allow for larger boards. In fact, it ended up slower compared to the hashed full tree search. Next time I will implement an evaluation function to be able to limit the search depth.

**2/16/2018** Started cleaning up the code on a separate branch, minmax search is done.

**2/24/2018** Finished cleaning up the code so far. Next I will either implement some heuristic evaluation function or get rigth to the UCT.
