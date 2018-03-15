# Implementation of a couple of  AI algorithms for TicTacToe

I implemented a simple generalized Tic-Tac-Toe game on a board of arbitrary size (so that there are relatively small amount of implementation details) using the following algorithms.

(1) Evolutionary algorithms: These direct methods were only able to handle boards of maximum size 4x4. (The memoized versions)

>(a) Brute Force [Negamax](https://en.wikipedia.org/wiki/Negamax) search of the game tree also with memoization. The search space for the $k$th move is $(n+1-k)!$ large, where $n$ stands for the area of the board and thus the total running time to play a game (if the computer was playing against itself) would be given by the [Smarandache-Kurepa Function](http://mathworld.wolfram.com/LeftFactorial.html):

$$K(n)=\sum_{k=1}^n(n+1-k)!=\sum_{k=1}^n k! $$

> Here just the largest term is $16! = 20922789888000 \approx 2 * 10^{13}$ for a 4x4 board. Thankfully for a 3x3                 board, it is only $9! = 362880\approx 3.5 * 10^5$. Big difference.

> (You might need [GitHub with Math Jax](https://chrome.google.com/webstore/detail/github-with-                   mathjax/ioemnmodlmafdkllaclgeombjnmnbima/related) chrome extension to view the formula rendered.)
          
> (b) [Alpha-Beta Pruning](https://www.cs.cmu.edu/~arielpro/mfai_papers/lecture1.pdf) of the game tree. This approach               prunes the search tree and discards parts of it that are clearly suboptimal.
      
(2) Reinforcement learning:

> (a) [Upper Confidence Bound for Trees (UCT)](https://gnunet.org/sites/default/files/Browne%20et%20al%20-%20A%20survey%20of%20MCTS%20methods.pdf) This is a Monte Carlo Search Tree (MCTS) algorithm with UCB1 as tree-policy and                  uniformly random moves as the default policy. The computation budget is regulated by the number of Monte Carlo                rollouts allowed per move. We retain part of the search tree corresponding to the actual moves. This algorithm can            handle larger board sizes, however, due to the random nature of the rollout, the computer seemingly plays random on            these boards, however, it does keep the player from winning in one move (if it can) and recognizes if it can win in            one move as well.
      
      
The main file is [TicTacToe.java](https://github.com/Moosquibe/Comparison-of-AI-algorithms-for-TicTacToe/blob/master/TicTacToe.java).

<p align="center">
<img https://github.com/Moosquibe/Comparison-of-AI-algorithms-for-TicTacToe/blob/master/Screenshot.png >
</p>

Further improvements possible:
- Implement a handcrafted value function approximator to guide the tree search with immediate rewards not just terminal ones.
- Perhaps do this value function approximator using a neural network as in AlphaGoZero.

Mini-Diary: 

**2/1/2018**: The naive minmax search is working. As expected, it is terribly inefficient and cannot handle larger than a 3x4 board. 

Next I will try to speed up the naive recursion by implementing memoization through hashing the already evaluated game configurations so that no reexploration is necessary and see what kind of speed-up I can get.

**2/3/2018** Fixed a little bug so that the computer now tries to win as fast as it can or survive as long as it can even if it would surely loose with optimal play from all players.

**2/3/2018** Implemented memoization using a hashmap to avoid having to recompute and now the program can handle a 4x4 board, although not gracefully. Since this is an inefficient baseline algorithm anyway, there is no point in optimizing it further and thus next I will move on to implementing the AlphaBetaPruning.

**2/11/2018** Implemented the alpha-beta pruning algorithm. Unfortunately, the speedup isn't enough to allow for larger boards. In fact, it ended up slower compared to the hashed full tree search. Next time I will implement an evaluation function to be able to limit the search depth.

**2/16/2018** Started cleaning up the code on a separate branch, minmax search is done.

**2/24/2018** Finished cleaning up the code so far. Next I will either implement some heuristic evaluation function or get rigth to the UCT.

**3/14/2018** Implemented UCT, and sometimes it plays pleasant but other times inexplicably dumb (surely buggy). For example:

- it should always be able to detect imminent victories as the game tree should be fully expanded one level deep.
- it should aways be able to detect imminent loss as the game tree should be expanded two levels deep unless the board is too big.

**3/15/2018** Fixed the previous bug, now the game plays nice, stops you from winning immediately. However, on larger boards the variance of the tree search is way too big and the computer seemingly plays randomly for most of the time.

I stop here for now, but given lots of free time (which virtually never happens), I might return to implement the neural network approach.
