#pragma once
#ifndef AWELE_HPP
#define AWELE_HPP

#include "Game.hpp"
#include <iostream>

class Awele : public Game {
    public:
        /**
         * Create a new game of Awele.
         */
        Awele();

        /**
         * Destroy a game of Awele.
         */
        ~Awele();

        /**
         * Initialize the Awele game.
         */
        void InitGame();

        /**
         * Clone the game
         */
        virtual Game* Clone()const; // Here no "=0" 
                                    // (which indicates a "pure" virtual method)

        // GetNextMove provides the next available move for the current game
        // GetNextMove will be called as an iterator for the min-max algorithm
        char* GetNextMove(int moveIndex);

        /**
         * Display the board on the console.
         */
        void Display() const;

        /**
         * Returns finished if the game is finished, else returns running.
         *
         * @return finished if game finished, else running
         */
        virtual GameStatus IsFinished() const;

        /**
         * Execute the move in the current game.
         *
         * @param move "0" - "5"
         *
         * @return moveOK if move is valid, else badMove
         */
        virtual moveStatus Move(const char * move);

        /**
         * Display end of game
         */
        void DisplayEndOfGame() const;

    private:
        /**
         * Count the seeds of a player
         *
         * @param playerId ID of the player (0 or 1)
         *
         * @return number of seeds of a player
         */
        // Re-use board* from super, need to be initialized in constructor
        //int board[12]; // 11 10 9  8  7  6  => 2nd player: 
                                           // seen as 5 4 3 2 1 0 from his side 
                         // 0  1  2  3  4  5  => 1st player
        int countSeeds(int playerId) const; // playerId 0 or 1

    protected:
        /**
         * Display a cell with the specified value.
         *
         * @param cellValue the cell value
         */
        void DisplayCellValue(int cellValue, 
                              std::ostream& out = std::cout) const;

};

#endif
