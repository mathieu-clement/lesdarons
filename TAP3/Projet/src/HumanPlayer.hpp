#pragma once
#ifndef HUMAN_PLAYER_HPP
#define HUMAN_PLAYER_HPP

#include "Player.hpp"
#include "Game.hpp"

class HumanPlayer : public Player {
    using Player::Player;
    
    public:
        virtual void Play(Game&) const;
};

#endif

