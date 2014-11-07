#include "Nim.hpp"

#include <iostream>
#include <stdlib.h>
#include <stdio.h>

#include <cstring>

using namespace std;

Nim::Nim()
{
    stack = 10; 
}

Nim::~Nim()
{
    this->Game::~Game();
}

// init the game
void Nim::InitGame()
{
    Game::InitGame();

    cout << "Welcome to a new game of Awele!\n" <<
        "When asked for \"Your move:\", enter a number between 1 and 3, " <<
        "Number of egals stick will be removed."<<
        "The goal is to NOT take the last stick :)"<<
        "Have fun!" << endl;
}

// clone the game, to be used for the min-max algorithms
Game* Nim::Clone() const
{
    Nim* game = new Nim();
    game->stack = stack;
    for (int i=0; i < NB_OF_PLAYERS; i++) {
        game->players[i] = players[i];
        game->score[i] = score[i];
    }
    game->currentPlayerIndex = currentPlayerIndex;
    return game;
}

// GetNextMove provides the next available move for the current game
// GetNextMove will be called as an iterator for the min-max algortihm
char* Nim::GetNextMove(int moveIndex)
{

    if(moveIndex > 3 || moveIndex > stack ) {

        char* returnVal = (char*) malloc((size_t)(std::strlen("STOP") + 1));
        sprintf(returnVal, "STOP");
        return returnVal;
    }

    // Convert int to char*
    char* move = new char[2]; // moves can be 1 digit (0 - 5);
    sprintf(move, "%d", moveIndex);

    return move;

}

// displays the board on the console
void Nim::Display() const
{
    for (int stick = 0; stick <= stack; stick++)
        cout << "| ";
    cout << endl;

} // end Display()


// displays the mesages for the end of game
void Nim::DisplayEndOfGame() const
{
    cout << "Nim is finished." << endl;
    cout << "Scores:" << endl; 

    Score score0 = GetScore(0);
    Score score1 = GetScore(1); 

    cout << players[0] << ": " << score0 << endl;
    cout << players[1] << ": " << score1 << endl;

}

// is the game finished?
GameStatus Nim::IsFinished() const 
{
    return (stack == 0) ? finished : running;
}

// execute the move in the current game
moveStatus Nim::Move(const char * move)
{
    int userInput = atoi(move);

    if (userInput < 1 || userInput > 3) return badMove;
    if(userInput > stack) return badMove;

    stack -= userInput;

    if(stack == 0)
    {
        int winner = currentPlayerIndex;
        score[winner] ++;
    }

    return moveOK;
}


