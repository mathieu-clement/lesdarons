#include <iostream>
#include <cstdlib>
#include "Game.hpp"
#include "AdvicedPlayer.hpp"
#include "ComputerPlayer.hpp"
#include "HumanPlayer.hpp"

// Uncomment the following line to display more debugging information
#define DEBUG
#include "Debug.hpp"

// Sorry, your file was missing dependendices:
#include <string.h> // without this, compiler reports an error because strcpy() cannot be found.

using namespace std;

Game::Game()
{
    currentPlayerIndex=0;
    for(int i =0; i< NB_OF_PLAYERS; i++)
        score[i]=0;
}

Game::~Game()
{
    //dtor
}

Score Game::GetScore(int playerNo) const
{
    if(playerNo>=0 && playerNo<NB_OF_PLAYERS)
        return score[playerNo];
    else
        return -1;
}

int Game::Evaluate() const
{
    return GetScore(currentPlayerIndex);
}

Player* Game::GetNextPlayer()
{
    currentPlayerIndex=(currentPlayerIndex+1)%NB_OF_PLAYERS;
    printDebugValueEndl(currentPlayerIndex); // DEBUG!!!
    return players[currentPlayerIndex];
}

void Game::InitGame()
{
    char* playername= new char[256];
    for(int i=0; i< NB_OF_PLAYERS; i++)
    {
        char playerType = ' ';
        while(playerType!='a' && playerType!='c' && playerType!='h')
        {
            cout << "What kind of player is Player No "<< i << "?" << endl;
            cout << " c: a computer" << endl;
            cout << " h: a human" << endl;
            cout << " a: a adviced player" << endl;
            cin >> playerType ;
            if (playerType=='a')
            {
                cout << "What is level of the advice player (1 to 5): " ;
                int depth =1;
                cin >> depth ;
                if (depth<1)
                    depth=1;
                if (depth>5)
                    depth=5;
                players[i]=new AdvicedPlayer(depth);
            }
            else if (playerType=='c')
            {
                cout << "What is level of the computer player (1 to 5): " ;
                int depth =1;
                cin >> depth ;
                if (depth<1)
                    depth=1;
                if (depth>5)
                    depth=5;
                players[i]=new ComputerPlayer(depth);
            }
            else if (playerType=='h')
                players[i]=new HumanPlayer();
            else //if (playerType=='q') => quit
                exit(0);

            if(playerType=='c')
            {
                strcpy(playername, "Computer  ");
                playername[9]= '0'+i;
            }
            else
            {
                cout << "Please enter the name of Player No "<< i << ": " ;
                cin >> playername ;
            }
            players[i]->SetName(playername);
            players[i]->SetPlayerNo(i);
            cout << endl;
         }
    }
   delete [] playername;
}

void Game::Display() const
{
}

void Game::DisplayEndOfGame() const
{
    cout << "The game is finished." << endl;
    if(GetScore(0)>GetScore(1))
    {
       cout << players[0] << " wins with score " << this->GetScore(0) << " : " << this->GetScore(1) << endl;
    }
    else
    {
       cout << players[1] << " wins with score " << this->GetScore(1) << " : " << this->GetScore(0) << endl;
    }
}

void Game::Run()
{
    InitGame();
    Player* currentPlayer = players[currentPlayerIndex];
    Display();
    do {
        currentPlayer->Play(*this);
        Display();
        currentPlayer = GetNextPlayer();
    } while (!IsFinished());
    DisplayEndOfGame();
}

