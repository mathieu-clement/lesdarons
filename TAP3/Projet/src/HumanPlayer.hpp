#pragma once
#ifndef HUMAN_PLAYER_HPP
#define HUMAN_PLAYER_HPP

#include "Player.hpp"
#include "Game.hpp"


/**
 * A Human player who inputs moves with a keyboard.
 */
class HumanPlayer : public virtual Player {
    using Player::Player; // inherit constructor from super class. Requires C++11 standard.
    
    public:
        /**
         * Play a move in the game.
         *
         * @param game the instance of the game
         */
        virtual void Play(Game&) const;
};

#endif

