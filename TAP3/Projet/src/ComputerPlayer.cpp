
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

Score ComputerPlayer::ExpectedScore (bool isPlayer0, Game* game, char* bestMove, int depth)
{
    Score best = 0;
    Score m = 0;

    if(game->IsFinished() || depth == 0)
        return game->GetScore(isPlayer0 ? 0 : 1);
    
    if (isPlayer0)      best = INT_MIN;
    else                best = INT_MAX;

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

        m = ExpectedScore(isPlayer0 ? 1 : 0, newGame, bestMove, depth-1);
        delete newGame;

        if (isPlayer0) {
            if (m >= best) {
                best = m;
                memcpy(bestMove, move, 2);
            }
        } else {
            if (m <= best) {
                best = m;
                //memcpy(bestMove, move, 2);
            }
        } // end if isPlayer0
    } // end for
    delete[] move;

    return best;
} // end ExpectedScore()


void ComputerPlayer::Play(Game& game) const
{
    char* bestMove = new char[2]; // move can be 1 digits (0 - 5)
    // for error checking, if bestMove is unchanged it will keep the value "N"
    bestMove[0] = 'N'; bestMove[1] = 0;
    ExpectedScore(m_playerNo == 0, &game, bestMove, m_depth);
    std::cout << "Computer will now play cell " << bestMove << std::endl;
    game.Move(bestMove);
    delete[] bestMove;
}
