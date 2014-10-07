#include <iostream>
#include "Game.hpp"
#include "Awele.hpp"
#include "Nim.hpp"

using namespace std;

int main()
{
    int gameType;

    cout << "Which game do you want to play?" << endl;
    cout << "1: Awele" << endl;
    cout << "2: Nim" << endl;
    cin >> gameType ;
    cout << endl;

    /*
    Game* game;
    if(gameType==1)
        game = new Awele();
    else
        game = new Nim();
    game->Run();
    delete game;
    */

    return 0;
}

