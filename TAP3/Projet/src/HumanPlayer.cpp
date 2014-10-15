#include <string>
#include <iostream>

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
        if (valid) {
            break;
        } else {
            std::cout << "Invalid move." << std::endl;
        }
    }
}
