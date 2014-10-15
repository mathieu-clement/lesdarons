#pragma once
#ifndef PLAYER_HPP
#define PLAYER_HPP

// Here we would usually this line:
//#include "Game.hpp"
// but it will create a dependency cycle, so we'll do what's known as a
// forward declaration.
class Game;

#include <string>

class Player {
    public:
        /*
         * Constructor inheritance implemented as per 
         * http://stackoverflow.com/questions/347358/inheriting-constructors
         * It requires compiling against the C++11 standard.
         */

        Player();
        Player(int);

        void Play(Game&);
        void SetName(char*);
        void SetPlayerNo(int);

        friend std::ostream& operator<< (std::ostream&, Player const&);
        friend std::ostream& operator<< (std::ostream&, Player*);
    protected:
        int m_playerNo;
        std::string m_playerName;
};

#endif
