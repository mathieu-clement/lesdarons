#pragma once
#ifndef COMPUTER_PLAYER_HPP
#define COMPUTER_PLAYER_HPP

#include "Player.hpp"
#include "Game.hpp"

#include <climits>

class ComputerPlayer : public Player {
    using Player::Player;
    
    public:
        explicit ComputerPlayer(int depth);
        virtual void Play(Game&) const;
        static Score ExpectedScore (
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
                );
    protected:
        int m_depth;
};

#endif
