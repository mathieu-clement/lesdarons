#pragma once
#ifndef COMPUTER_PLAYER_HPP
#define COMPUTER_PLAYER_HPP

#include "Player.hpp"
#include "Game.hpp"

#include <climits>

/**
 * A player who is really a computer algorithm (minimax with alpha-beta-pruning)
 */
class ComputerPlayer : public virtual Player {
    public:
        /**
         * Create a new computer player with the specified intelligence / depth.
         *
         * @param depth Depth to go in the min max strategy tree. The greater the better.
         */
        explicit ComputerPlayer(int depth);// explicit prevents autocasting via constructor

        /**
         * Play a move in the game.
         *
         * @param game the game instance
         */
        virtual void Play(Game&) const;

        /**
         * Calculates the best score a player can achieve in this game,
         * using the minimax algorithm along with alpha-beta-pruning.
         *
         * @param playerNo          the player number (0 or 1)
         * @param game              the game instance
         * @param bestMove          the move to achieve that score. YOU must allocate a 2 char array.
         * @param depth             Optional. Depth to go in the tree, the greater the better.
         * @param alpha             Optional. Alpha parameter in alpha-beta-pruning.
         * @param beta              Optional. Beta paramter in alpha-beta-pruning.
         * @param maximizingPlayer  set to true if the current player is the one we are
         *                          trying to figure out the best score
         *
         * @return the best score for the specified player
         */
        Score ExpectedScore (
                int playerNo, // player number
                Game* game, // instance of the game
                char* bestMove, // the 2nd return value of the method, the best move to do
                                // to achieve the returned score
                                // must be 2 characters / bytes long.
                int depth=INT_MAX, // how many times we should play. typically from 1 to 5, 
                                   // but any positive value (incl. INT_MAX) is accepted.
                Score alpha = INT_MIN, // alpha param from "alpha-beta-pruning" method
                Score beta = INT_MAX,  // beta  param from "alpha-beta-prunging" method
                bool maximizingPlayer=true // true if player0 is current player
                ) const;
    protected:
        int m_depth; // depth parameter passed in the constructor
};

#endif
