#include "Awele.hpp"

#include <iostream>

using namespace std;

Awele::Awele() : Game()
{
}

Awele::~Awele()
{
    this->Game::~Game();
}

// init the game
void Awele::InitGame()
{
    Game::InitGame();
    cout << "Awele::InitGame()" << endl;
}

// clone the game, to be used for the min-max algorithms
Game* Awele::Clone() const
{
    // TODO
    return new Awele();
}

// GetNextMove provides the next available move for the current game
// GetNextMove will be called as an iterator for the min-max algortihm
char* Awele::GetNextMove(int moveIndex)
{
    return "";
}

// displays the board on the console
void Awele::Display() const
{

}

// is the game finished?
GameStatus Awele::IsFinished() const
{
    return finished;
}

// execute the move in the current game
moveStatus Awele::Move(const char * move)
{
    return moveOK;
}
