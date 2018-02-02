# Mathematics Of Deep Learning Parallel Curricula
This repository contains my implementations to the algorithms covered in the Parallel Curricula of the Mathematics of Deep Learning class in the Spring semester of 2018. The main goal of the semester is to understand [Alpha Go](https://deepmind.com/research/alphago/).

I am currently working on implementing a simple generalized Tic-Tac-Toe game on a board of arbitrary size (so that there are relatively small amount of implementation details) using (1) Evolutionary algorithms: (a) Brute Force Minmax search of the game tree; (b) Alpha-Beta Pruning of the game tree; (2) Reinforcement learning based on a learned value function. The main file is TicTacToe.java.


Mini-Diary: 

**2/1/2018**: The naive minmax search is working. As expected, it is terribly inefficient and cannot handle larger than a 3x4 board. This is not surprising at all as the search space for the $k$th move is $(n+1-k)!$ large, where $n$ stands for the area of the board. Thus the total running time to play a game (if the computer was playing against itself) would be

$$\sum_{k=1}^n(n+1-k)!=\sum_{k=1}^n k! \sim n! =O(n^{n+1}e^{-n})=O(e^{(n+1)\log n - n}),$$

which is $16! = 20922789888000 \approx 2 * 10^13$ for a 4x4 board. Thankfully for a 3x3 board, it is only $9! = 362880\approx 3.5 * 10^5$. Big difference.

(You might need [GitHub with Math Jax](https://chrome.google.com/webstore/detail/github-with-mathjax/ioemnmodlmafdkllaclgeombjnmnbima/related) chrome extension to view the formula rendered.)

Next I will try to speed up the naive recursion by hashing the already evaluated game configurations so that no reexploration is necessary and see what kind of speed-up I can get. 
