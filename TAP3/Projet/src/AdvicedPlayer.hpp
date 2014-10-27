#pragma once
#ifndef ADVICED_PLAYER_HPP
#define ADVICED_PLAYER_HPP

#include "Player.hpp"
#include "Game.hpp"

/**
 * A Player who is told advice by the computer on what he should play.
 */
class AdvicedPlayer : public Player {
    public:
        /**
         * Create a new advised player with the specified intelligence / depth.
         *
         * @param depth Depth to go in the min max strategy tree. The greater the better.
         */
        explicit AdvicedPlayer(int depth);

        /**
         * Play a move in the game.
         *
         * @param game the game instance
         */
        virtual void Play(Game&) const;
    protected:
        int m_depth; // depth parameter passed in the constructor
};

#endif
