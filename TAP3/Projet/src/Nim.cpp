#include "Nim.hpp"

Nim::Nim()
{

}

Nim::~Nim()
{

}

// init the game
void Nim::InitGame()
{

}

// clone the game, to be used for the min-max algorithms
Game* Nim::Clone() const
{
    // TODO Do correctly
    return (Game*) this;
}

// evaluate a game
int Nim::Evaluate() const
{
    return 0;
}

// GetNextMove provides the next available move for the current game
// GetNextMove will be called as an iterator for the min-max algortihm
char* Nim::GetNextMove(int moveIndex)
{
    return (char*) (char) 'a'; // TODO probably wrong
}

// return the current score of player playerNo
Score Nim::GetScore(int playerNo) const
{
    return playerNo; // TODO probably wrong
}

// displays the board on the console
void Nim::Display() const
{

}

// displays the mesages for the end of game
void Nim::DisplayEndOfGame() const
{

}

// is the game finished?
GameStatus Nim::IsFinished() const 
{
    return running;
}

// execute the move in the current game
moveStatus Nim::Move(const char * move)
{
    return moveOK;
}


