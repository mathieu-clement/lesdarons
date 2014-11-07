#pragma once
#ifndef NIM_H
#define NIM_H

#include "Game.hpp"

class Nim : public Game {
    public:
        Nim();
        ~Nim();

        // init the game
        void InitGame();

        // run the game
        void Run();

        // clone the game, to be used for the min-max algorithms
        virtual Game* Clone()const; // Here no "=0" 
                                    // (which indicates a "pure" virtual method)

        // GetNextMove provides the next available move for the current game
        // GetNextMove will be called as an iterator for the min-max algorithm
        char* GetNextMove(int moveIndex);


        // displays the board on the console
        void Display() const;

        // displays the mesages for the end of game
        void DisplayEndOfGame() const;

        // is the game finished?
        virtual GameStatus IsFinished() const;

        // execute the move in the current game
        virtual moveStatus Move(const char * move);

    private:
        int stack;
};

#endif
