#include "Awele.hpp"

#include <iostream>
#include <string.h>

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
    // [1] [ ] [2] [ ] [4] [8]
    // [ ] [2] [4] [ ] [ ] [ ]
    
    // top player (cells are reversed)
    for (int cell = 11; cell >= 6; cell--) {
        int cellValue = board[cell];
        cout << "[" << (cellValue > 0 ? std::to_string(cellValue) : " ") << "] ";
    }
    cout << endl;

    // bottom player
    for (int cell = 0; cell < 6; cell++) {
        int cellValue = board[cell];
        cout << "[" << (cellValue > 0 ? std::to_string(cellValue) : " ") << "] ";
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
    int userInput = strtol(move, NULL, 10);
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
