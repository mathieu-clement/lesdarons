#pragma once
#ifndef AWELE_HPP
#define AWELE_HPP

#include "Game.hpp"

class Awele : public Game {
    public:
        Awele();
        ~Awele();

        // init the game
        void InitGame();

        // run the game
        void Run();

        // clone the game, to be used for the min-max algorithms
        virtual Game* Clone()const; // Here no "=0" (which indicates a "pure" virtual method)

        // evaluate a game
        int Evaluate() const;

        // GetNextMove provides the next available move for the current game
        // GetNextMove will be called as an iterator for the min-max algortihm
        char* GetNextMove(int moveIndex);

        // return the current score of player playerNo
        Score GetScore(int playerNo) const;

        // displays the board on the console
        void Display() const;

        // displays the mesages for the end of game
        void DisplayEndOfGame() const;

        // is the game finished?
        virtual GameStatus IsFinished() const;

        // execute the move in the current game
        virtual moveStatus Move(const char * move);

};

#endif
