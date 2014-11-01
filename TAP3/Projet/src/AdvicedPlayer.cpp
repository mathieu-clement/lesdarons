
#include <string>
#include <iostream>
#include <climits>
#include <cstdlib>

#include "AdvicedPlayer.hpp"
#include "ComputerPlayer.hpp"

/**
 * Create a new advised player with the specified intelligence / depth.
 *
 * @param depth Depth to go in the min max strategy tree. The greater the better.
 */
AdvicedPlayer::AdvicedPlayer(int depth) : Player()
{
    m_depth = depth;
}

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
        // Computer advice
        // for error checking, if bestMove is unchanged it will keep the value "N"
        bestMove[0] = 'N'; bestMove[1] = 0;
        ComputerPlayer::ExpectedScore(m_playerNo, &game, bestMove, m_depth);
        std::cout << "Computer advises move '" << bestMove << "'" << std::endl;

        // What does the human wants to do?
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

}
