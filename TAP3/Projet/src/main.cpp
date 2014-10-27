#include <iostream>
#include "Game.hpp"
#include "Awele.hpp"
#include "Nim.hpp"

using namespace std;

/**
 * The main method of this program.
 */
int main()
{
    int gameType; // number assigned to type of game

    cout << "Which game do you want to play?" << endl;
    cout << "1: Awele" << endl;
    cout << "2: Nim" << endl;
    cin >> gameType ;
    cout << endl;

    Game* game; // instance of the selected game
    if(gameType==1)
        game = new Awele(); // new game of Awele
    else
        game = new Nim(); // new game of Nim
    game->Run();
    delete game;

    return 0;
}

