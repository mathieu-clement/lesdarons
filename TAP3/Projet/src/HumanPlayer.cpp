#include <string>
#include <iostream>
#include <cstdlib>

#include "HumanPlayer.hpp"
#include "Game.hpp"

void HumanPlayer::Play(Game& game) const
{
    bool valid = false;
    while(true) {
        std::string str;
        std::cout << "Your move: ";
        std::cin >> str;
        valid = game.Move(str.c_str()) == moveOK;
        if (valid)
            break;
        else if (str.compare("q") == 0)
            std::exit(EXIT_SUCCESS);
        else
            std::cout << "Invalid move." << std::endl;
    } // end while
} // end Play()
