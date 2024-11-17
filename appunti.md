### notes:  
- DL evaluation		https://github.com/zjeffer/chess-deep-rl  
- RL  
  
lookup table, negamax, alphabeta pruning	https://eleonoramisino.altervista.org/tablut-ai/  
self play, MCTS		https://medium.com/@samgill1256/reinforcement-learning-in-chess-73d97fad96b3  
  
other project repo and results     http://ai.unibo.it/games/boardgamecompetition/tablut  
  
  
## DONE:  
lookup table  
Minmax  
  
## TODO:  
Negamax  
evaluations  
evaluations testing   
vm enviroment test   
  


## Tree search pseudocode:

0. checks the state for win/lose/draw   (state.getTurn())  
1. get the game state   (as matrix or array)  
2. checks if can win in a move      (TODO)  
3. starts the search of the best possible move  (NegaMax con AlphaBeta o MCTS)  
    iteration:      (Negamax + AlphaBeta)  
        - checks time   
	    - evaluates a level of the tree  
        - choose a move  
  
// later  
    iteration:      (iterative deepening)  
        - checks time   
	    - evaluates a level of the tree  
        - choose a move  
        - if max_level:  
            restart  
          else:  
            next level  
  

## ML evaluation weight optimization (python): 

define the fun to optimize  
create a labelled dataset   
define a loss fun from a correct eval fun  
apply a ML method: SGD, Adam, GA, LinReg, SVM, DL  


## Lookup Table:

defalut max dim: 1000  
note: when deleting a few moves (1/10000 x lookup) every round the lookup is more efficient in time and hit rate
note: when incresig the max dim to 2000 and 4000 the effectiveness and efficiency didn't change -> no need to find the highest possible value, adopt 
rotating the board doesn't change the evaluation -> check for equivalent states on lookup

## Results:
SemiRand vs Rand, 100 games: 93 - 7
Rand vs SemiRand, 100 games: 6 - 94

MMTS(4) vs Rand, 10 games: 10 - 0
Rand vs MMTS(4), 10 games: 2 - 8

MMTS(4) vs SemiRand, 10 games: 10 - 0
SemiRand vs MMTS(4), 10 games: 6 - 4