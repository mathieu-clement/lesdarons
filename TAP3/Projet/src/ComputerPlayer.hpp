#pragma once
#ifndef COMPUTER_PLAYER_HPP
#define COMPUTER_PLAYER_HPP

#include "Player.hpp"
#include "Game.hpp"

class ComputerPlayer : public Player {
    using Player::Player;
    
    public:
        virtual void Play(Game&) const;
};

#endif
