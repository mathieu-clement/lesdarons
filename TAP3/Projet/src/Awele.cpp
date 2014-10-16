#include "Awele.hpp"

#include <iostream>
#include <stdlib.h>
#include <stdio.h>

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
    // Fill the 12 holes with 4 seeds
    fill_n(board, 12, 4);
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
    // Prints something like this:
    // [ 1] [ ]  [ 2] [  ] [ 4] [ 8]
    // [  ] [ 2] [ 4] [  ] [10] [12]
    
    // top player (cells are reversed)
    for (int cell = 11; cell >= 6; cell--) {
        int cellValue = board[cell];
        cout << "[";
		if (cellValue > 0)
            printf("%2d", cellValue);
        else
            cout << "  ";

        cout << "] ";
    }
    cout << endl;

    // bottom player
    for (int cell = 0; cell < 6; cell++) {
        int cellValue = board[cell];
        cout << "[";
		if (cellValue > 0)
            printf("%2d", cellValue);
        else
            cout << "  ";

        cout << "] ";
    }
    cout << endl;
} // end Display()

// is the game finished?
GameStatus Awele::IsFinished() const
{
   return (countSeeds(0) == 0 || countSeeds(1) == 0) ? finished : running;
}

// execute the move in the current game
moveStatus Awele::Move(const char * move)
{
    int userInput = atoi(move);
    if (userInput < 0 || userInput > 5) return badMove;

    int cellId = currentPlayerIndex * 6 + userInput;
    if(board[cellId] == 0) return badMove; // empty cell

    int seedsToDistribute = board[cellId];

    board[cellId] = 0; // Empty current cell

    // Perform move
    int nextCell = (cellId+1) % 12;
    while (seedsToDistribute > 0) {
        board[nextCell]++;
        nextCell = (nextCell+1) % 12;
        seedsToDistribute--;
    }
    
    int lastCell = (nextCell-1) % 12;
    // Take content of cell with 2 or 3 stones
    // but not the ones of the contestant
    while(
        lastCell >= (((currentPlayerIndex+1)%2)*6)+0 &&
        lastCell <= (((currentPlayerIndex+1)%2)*6)+5 && 
        (board[lastCell] == 2 || board[lastCell] == 3)) {
        score[currentPlayerIndex] += board[lastCell];
        board[lastCell] = 0;
        lastCell = (nextCell-1) % 12;
    }

    return moveOK;
}

int Awele::countSeeds(int playerId) const // playerId 0 or 1
{
    int offset = playerId * 6;
    int nbSeeds = 0;
    for (int i = 0; i < 6; i++) {
        nbSeeds += board[i+offset];
    }
    return nbSeeds;
}
