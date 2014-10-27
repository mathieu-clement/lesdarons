#pragma once
#ifndef PLAYER_HPP
#define PLAYER_HPP

// Here we would usually this line:
//#include "Game.hpp"
// but it will create a dependency cycle, so we'll do what's known as a
// forward declaration.
class Game;

#include <string>

/**
 * A generic player
 */
class Player {
    public:
        /*
         * Constructor inheritance implemented as per 
         * http://stackoverflow.com/questions/347358/inheriting-constructors
         * It requires compiling against the C++11 standard.
         */

        /**
         * Create a new player.
         */
        Player();

        /**
         * Create a new player with the specified player number.
         *
         * @param playerNo player number
         */
        explicit Player(int); // explicit prevents autocasting via constructor

        /**
         * Play a move in the game.
         *
         * @param game the game instance
         */
        virtual void Play(Game&) const =0;

        /**
         * Set the name of the player
         *
         * @param name player name
         */
        void SetName(char*);

        /**
         * Set the player number
         *
         * @param no player number
         */
        void SetPlayerNo(int);

        /**
         * Friend function to push player REFERENCE to stream
         */
        friend std::ostream& operator<< (std::ostream&, Player const&);

        /**
         * Friend function to push player POINTER to stream
         */
        friend std::ostream& operator<< (std::ostream&, Player*);
    protected:
        int m_playerNo; // player number parameter from constructor
        std::string m_playerName; // player name paramter from constructor
};

#endif
