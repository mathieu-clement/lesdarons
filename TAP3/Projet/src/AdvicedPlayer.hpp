#pragma once
#ifndef ADVICED_PLAYER_HPP
#define ADVICED_PLAYER_HPP

#include "Player.hpp"
#include "Game.hpp"

class AdvicedPlayer : public Player {
    using Player::Player;
    
    public:
        explicit AdvicedPlayer(int depth);
        virtual void Play(Game&) const;
    protected:
        int m_depth;
};

#endif
