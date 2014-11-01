#pragma once
#ifndef ADVICED_PLAYER_HPP
#define ADVICED_PLAYER_HPP

#include "HumanPlayer.hpp"
#include "ComputerPlayer.hpp"
#include "Game.hpp"

/**
 * A Player who is told advice by the computer on what he should play.
 */
class AdvicedPlayer : public HumanPlayer, public ComputerPlayer {
    
    using ComputerPlayer::ComputerPlayer; // inherit constructor from super class. Requires C++11 standard.

    public:
        /**
         * Play a move in the game.
         *
         * @param game the game instance
         */
        virtual void Play(Game&) const;
};

#endif
