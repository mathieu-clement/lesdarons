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
        static Score ExpectedScore (bool isPlayer0, Game* game, char* bestMove, int depth=INT_MAX);
    protected:
        int m_depth;
};

#endif
