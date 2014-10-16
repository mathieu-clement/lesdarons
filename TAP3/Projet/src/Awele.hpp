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

        // clone the game, to be used for the min-max algorithms
        virtual Game* Clone()const; // Here no "=0" (which indicates a "pure" virtual method)

        // GetNextMove provides the next available move for the current game
        // GetNextMove will be called as an iterator for the min-max algortihm
        char* GetNextMove(int moveIndex);

        // displays the board on the console
        void Display() const;

        // is the game finished?
        virtual GameStatus IsFinished() const;

        // execute the move in the current game
        virtual moveStatus Move(const char * move);

    private:
        // Re-use board* from super, need to be initialized in constructor
        //int board[12]; // 11 10 9  8  7  6  => 2nd player: seen as 5 4 3 2 1 0 from his side 
                         // 0  1  2  3  4  5  => 1st player
        int countSeeds(int playerId) const; // playerId 0 or 1

    protected:
        inline void DisplayCellValue(int cellValue) const;

};

#endif
