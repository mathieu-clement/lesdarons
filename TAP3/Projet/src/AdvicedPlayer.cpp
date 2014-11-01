
#include <string>
#include <iostream>
#include <climits>
#include <cstdlib>

#include "AdvicedPlayer.hpp"
#include "ComputerPlayer.hpp"
#include "HumanPlayer.hpp"

/**
 * Play a move in the game.
 *
 * @param game the game instance
 */
void AdvicedPlayer::Play(Game& game) const
{
    // Use logic from AdvicedPlayer to advise the player
    // But input from HumanPlayer
    char* bestMove = new char[2]; // move can be 1 digits (0 - 5) => advice from computer

    bool valid = false;
    while(true) {

        // What does the human wants to do?
        std::string str;
        std::cout << "Your move: ";
        std::cin >> str;
        if (str.compare("a") == 0) {
            // Computer advice if user types "a"
            ComputerPlayer::ExpectedScore(m_playerNo, &game, bestMove, m_depth);
            std::cout << "Computer advises move '" << bestMove << "'" << std::endl;
        } else if (str.compare("q") == 0) {
            std::exit(EXIT_SUCCESS);
        } else {
            valid = game.Move(str.c_str()) == moveOK;
            if (valid)
                break;
            else
                std::cout << "Invalid move." << std::endl;
        }
    } // end while

}
