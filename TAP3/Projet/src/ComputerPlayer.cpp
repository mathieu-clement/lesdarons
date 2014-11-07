
#include <string>
#include <cstring>
#include <iostream>
#include <climits>
#include <cstdio>
#include <cstring>
#include <cstdlib>

#include "ComputerPlayer.hpp"
#include "Game.hpp"


/**
 * Create a new computer player with the specified intelligence / depth.
 *
 * @param depth Depth to go in the min max strategy tree. The greater the better.
 */
ComputerPlayer::ComputerPlayer(int depth) : Player()
{
    m_depth = depth;
}

/**
 * Calculates the best score a player can achieve in this game,
 * using the minimax algorithm along with alpha-beta-pruning.
 *
 * @param playerNo          the player number (0 or 1)
 * @param game              the game instance
 * @param bestMove          the move to achieve that score. 
 *                          YOU must allocate a 2 char array.
 * @param depth             Optional. Depth to go in the tree, 
 *                          the greater the better.
 * @param alpha             Optional. Alpha parameter in alpha-beta-pruning.
 * @param beta              Optional. Beta paramter in alpha-beta-pruning.
 * @param maximizingPlayer  set to true if the current player is the one we are
 *                          trying to figure out the best score
 *
 * @return the best score for the specified player
 */
Score ComputerPlayer::ExpectedScore (int playerNo, Game* game, char* bestMove,
                                     int depth,
                                     Score alpha, Score beta,
                                     bool maximizingPlayer) const
{
    Score m = 0;

    if(game->IsFinished() || depth == 0)
        return game->GetScore(playerNo);
    
    char* move = new char[20]; // move can be 1 digits (0 - 5) in Awele
                               // We suppose 20 is a safe value...
    bool hasNextMove = true;
    int moveIndex = 0;
    while(true) {
        move = game->GetNextMove(moveIndex);
        if(strcmp(move, "STOP") == 0) break;
        moveIndex++;

        Game* newGame = game->Clone();
        moveStatus valid = newGame->Move(move);
        newGame->GetNextPlayer();
        if (!valid) {
            delete newGame;
            continue;
        }

        // Copy bestMove before passing it
        size_t moveSize = strlen(move) + 1;
        char* tempMove = (char*) malloc(moveSize);
        memcpy(tempMove, bestMove, moveSize);

        m = ExpectedScore(playerNo == 0 ? 1 : 0, newGame, tempMove, depth-1, 
                          alpha, beta, 
                          maximizingPlayer ? false : true);
        delete newGame;
        delete[] tempMove;

        if (maximizingPlayer) {
            if (m >= alpha) {
                alpha = m;
                memcpy(bestMove, move, moveSize);
            }
            if (beta <= alpha) break; // Beta cut-off
        } else {
            if (m <= beta) {
                beta = m;
            }
            if (beta <= alpha) break; // Alpha cut-off
        } // end if maximizingPlayer
    } // end for
    delete[] move;

    if (maximizingPlayer) {
        return alpha;
    } else {
        return beta;
    }
} // end ExpectedScore()


/**
 * Play a move in the game.
 *
 * @param game the game instance
 */
void ComputerPlayer::Play(Game& game) const
{
    char* bestMove = new char[2]; // move can be 1 digits (0 - 5)
    ExpectedScore(m_playerNo, &game, bestMove, m_depth);
    std::cout << "Computer will now play " << bestMove << std::endl;
    game.Move(bestMove);
    delete[] bestMove;
}

/**
 * Assignment operator
 */
 ComputerPlayer* ComputerPlayer::operator= (const ComputerPlayer* other) {
     if (this != other) {
         this->m_depth = other->m_depth;
         this->m_playerNo = other->m_playerNo;
         this->m_playerName = other->m_playerName;
     }
     return this;
 }
