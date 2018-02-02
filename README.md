# Mathematics Of Deep Learning Parallel Curricula
This repository contains my implementations to the algorithms covered in the Parallel Curricula of the Mathematics of Deep Learning class in the Spring semester of 2018. The main goal of the semester is to understand [Alpha Go](https://deepmind.com/research/alphago/).

I am currently working on implementing a simple generalized Tic-Tac-Toe game on a board of arbitrary size (so that there are relatively small amount of implementation details) using (1) Evolutionary algorithms: (a) Brute Force Minmax search of the game tree; (b) Alpha-Beta Pruning of the game tree; (2) Reinforcement learning based on a learned value function. The main file is TicTacToe.java.


Current status: The naive minmax search is working. As expected, it is terribly inefficient and cannot handle larger than a 3x4 board. Next I will try to speed up the naive recursion by hashing the game configurations so that no reexploration is necessary.
