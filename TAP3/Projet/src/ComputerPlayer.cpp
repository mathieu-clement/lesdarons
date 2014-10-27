
#include <string>
#include <iostream>
#include <climits>
#include <cstdio>
#include <cstring>

#include "ComputerPlayer.hpp"
#include "Game.hpp"

ComputerPlayer::ComputerPlayer(int depth) : Player()
{
    m_depth = depth;
}

Score ComputerPlayer::ExpectedScore (int playerNo, Game* game, char* bestMove,
                                     int depth,
                                     Score alpha, Score beta,
                                     bool maximizingPlayer)
{
    Score m = 0;

    if(game->IsFinished() || depth == 0)
        return game->GetScore(playerNo);
    
    char* move = new char[2]; // move can be 1 digits (0 - 5)
    for (int i = 0; i < 6; i++) {
        // Convert int to char*
        sprintf(move, "%d", i);
        Game* newGame = game->Clone();
        moveStatus valid = newGame->Move(move);
        newGame->GetNextPlayer();
        if (!valid) {
            delete newGame;
            continue;
        }

        // Copy bestMove before passing it
        char* tempMove = new char[2];
        memcpy(tempMove, bestMove, 2);

        m = ExpectedScore(playerNo == 0 ? 1 : 0, newGame, tempMove, depth-1, alpha, beta, maximizingPlayer ? false : true);
        delete newGame;
        delete[] tempMove;

        if (maximizingPlayer) {
            if (m >= alpha) {
                alpha = m;
                memcpy(bestMove, move, 2);
            }
            if (beta <= alpha) break; // Beta cut-off
        } else {
            if (m <= beta) {
                beta = m;
                // memcpy(bestMove, move, 2); // no need to know the best move of the other player
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


void ComputerPlayer::Play(Game& game) const
{
    char* bestMove = new char[2]; // move can be 1 digits (0 - 5)
    // for error checking, if bestMove is unchanged it will keep the value "N"
    bestMove[0] = 'N'; bestMove[1] = 0;
    ExpectedScore(m_playerNo, &game, bestMove, m_depth);
    std::cout << "Computer will now play cell " << bestMove << std::endl;
    game.Move(bestMove);
    delete[] bestMove;
}
