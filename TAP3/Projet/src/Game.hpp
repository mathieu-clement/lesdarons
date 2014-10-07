#ifndef GAME_HPP
#define GAME_HPP

#include "Player.h"

typedef int Score;

typedef enum{running=0, finished=1} GameStatus;
typedef enum{badMove=0, moveOK=1} moveStatus;

class Game
{
    public:
        Game();
        virtual ~Game();

    // init the game
    virtual void InitGame();

    // run the game
    void Run();

    // clone the game, to be used for the min-max algorithms
    virtual Game* Clone()const =0;

    // evaluate a game
    virtual int Evaluate() const;

    // GetNextMove provides the next available move for the current game
    // GetNextMove will be called as an iterator for the min-max algortihm
    virtual char* GetNextMove(int moveIndex)=0;

    // return the current score of player playerNo
    virtual Score GetScore(int playerNo) const;

    // displays the board on the console
    virtual void Display() const;

    // displays the mesages for the end of game
    virtual void DisplayEndOfGame() const;

    // is the game finished?
    virtual GameStatus IsFinished() const =0;

    // execute the move in the current game
    virtual moveStatus Move(const char * move)=0;

    // Change to the other player
    // used by the Game class and by the minmax algorithm
    Player* GetNextPlayer();

    protected:
        static const int NB_OF_PLAYERS=2;
        Player *players[NB_OF_PLAYERS];
        int currentPlayerIndex;
        int score[NB_OF_PLAYERS];

        // this is the board that should be used by the game implementation
        int* board;

   private:

 };

#endif // GAME_HPP

