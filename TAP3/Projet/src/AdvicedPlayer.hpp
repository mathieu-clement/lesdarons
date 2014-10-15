#pragma once
#ifndef ADVICED_PLAYER_HPP
#define ADVICED_PLAYER_HPP

#include "Player.hpp"
#include "Game.hpp"

class AdvicedPlayer : public Player {
    using Player::Player;
    
    public:
        virtual void Play(Game&) const;
};

#endif
