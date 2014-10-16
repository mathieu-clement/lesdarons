#include "Awele.hpp"

#include <iostream>
#include <stdlib.h>
#include <stdio.h>

using namespace std;

Awele::Awele() : Game()
{
    board = new int[12];
}

Awele::~Awele()
{
    this->Game::~Game();
    delete[] board;
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
    Awele* game = new Awele();
    for (int i = 0; i < 12; i++) {
        game->board[i] = board[i];
    }
    for (int i=0; i < NB_OF_PLAYERS; i++) {
        game->players[i] = players[i];
        game->score[i] = score[i];
    }
    game->currentPlayerIndex = currentPlayerIndex;
    return game;
}

// GetNextMove provides the next available move for the current game
// GetNextMove will be called as an iterator for the min-max algorithm
char* Awele::GetNextMove(int moveIndex)
{

    return "1";
}

void Awele::DisplayCellValue(int cellValue) const
{
        cout << "[";
		if (cellValue > 0)
            printf("%2d", cellValue);
        else
            cout << "  ";
        cout << "] ";
}

// displays the board on the console
void Awele::Display() const
{
    // Prints something like this:
    // [ 1] [  ] [ 2] [  ] [ 4] [ 8] <= Player 1, cell 5 4 3 2 1 0      board[] : 11 10  9  8  7  6
    // [  ] [ 2] [ 4] [  ] [10] [12] <= Player 0, cell 0 1 2 3 4 5                 0  1  2  3  4  5
    
    // top player (cells are reversed)
    for (int cell = 11; cell >= 6; cell--)  Awele::DisplayCellValue(board[cell]);
    cout << endl;

    // bottom player
    for (int cell = 0; cell < 6; cell++)    Awele::DisplayCellValue(board[cell]);
    cout << endl;

    // Scores
    cout << "Score player 0: " << GetScore(0) << ",  Score player 1: " << GetScore(1) << endl; 
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
    // but not the ones of the other contestant
    while(
        lastCell >= (((currentPlayerIndex+1)%NB_OF_PLAYERS)*6)+0 &&
        lastCell <= (((currentPlayerIndex+1)%NB_OF_PLAYERS)*6)+5 && 
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
