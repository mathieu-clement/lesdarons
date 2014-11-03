#define protected public

#include "Game.hpp"
#include "Awele.hpp"
#include "ComputerPlayer.hpp"

#undef protected

#include "gtest/gtest.h"
#include <string>
#include <sstream>
#include <iostream>
#include <random>

namespace {

// The fixture for testing class Foo.
class AweleTest : public ::testing::Test {
    public:
        virtual void SetUp() {
            game = new Awele();
        }

        virtual void TearDown() {
            delete game;
        }
    protected:
        Game* game;
};

TEST_F(AweleTest, NewGameNotFinished) {
    ASSERT_FALSE(game->IsFinished());
}

TEST_F(AweleTest, TakeFromZero) {
    game->Move("0");
    EXPECT_EQ(0, game->board[0]);
    for (int i = 1; i < 5; i++)
        EXPECT_EQ(5, game->board[i]) << "Move didn't affect board[" << i << "]";
}

TEST_F(AweleTest, TakeFromFour) {
    game->Move("4");
    EXPECT_EQ(0, game->board[4]);
    for (int i = 5; i < 9; i++)
        EXPECT_EQ(5, game->board[i]) << "Move didn't affect board[" << i << "]";
}

TEST_F(AweleTest, CloneKeepsOriginalBoard) {
    Game* copy;
    copy = game->Clone();
    copy->Move("0");
    ASSERT_EQ(4, game->board[4]) << "original changed";
    ASSERT_EQ(5, copy->board[4]) << "copy didn't change";
}

TEST_F(AweleTest, DisplayCellValue) {
    Awele* awele = (Awele*) game;
    const char* targets[] = {"[  ] ",
        "[ 1] ","[ 2] ","[ 3] ","[ 4] ","[ 5] ","[ 6] ","[ 7] ",
        "[ 8] ","[ 9] ","[10] ","[11] ","[12] ","[13] ","[14] ",
        "[15] ","[16] ","[17] ","[18] ","[19] ","[20] "};
    for (int i = 0; i <= 20; i++) {
        std::ostringstream os;
        awele->Awele::DisplayCellValue(i, os);
        char* s = (char*) os.str().c_str();
        EXPECT_STREQ(targets[i], s);
    }
}

TEST_F(AweleTest, GameFinishesEventually) {
    // Generate number between 0 and 5
    std::default_random_engine generator;
    std::uniform_int_distribution<int> distribution(0,5); 

    bool valid = false;
    char* moveStr = (char*) malloc(2);
    
    static int MAX_MOVES_TRIED = 1000;

    int i;
    for (i = 0; i <= MAX_MOVES_TRIED && !game->IsFinished(); i++) {
        sprintf(moveStr, "%d", distribution(generator));
        valid = game->Move(moveStr);
        if (!valid) continue;
        valid = false;
        game->GetNextPlayer();
    }
    //std::cout << "Score 0: " << game->GetScore(0) << std::endl;
    //std::cout << "Score 1: " << game->GetScore(1) << std::endl;
    game->DisplayEndOfGame();

    ASSERT_TRUE(game->IsFinished()) << "Game did NOT finish in " 
                << MAX_MOVES_TRIED << " moves";
}

TEST_F(AweleTest, SmartestComputerPlayerWins) {
    int DUMB_COMPUTER_DEPTH = 1;
    int SMART_COMPUTER_DEPTH = 10;
    ComputerPlayer computer0(DUMB_COMPUTER_DEPTH);
    ComputerPlayer computer1(SMART_COMPUTER_DEPTH);

    bool valid = true;

    char* bestMove = new char[2]; // move can be 1 digits (0 - 5)
    while (valid && !game->IsFinished()) {
        int playerIdx = game->currentPlayerIndex;
        if(playerIdx == 0)
            computer0.ExpectedScore(playerIdx, game, bestMove, 
                                    DUMB_COMPUTER_DEPTH);
        else
            computer1.ExpectedScore(playerIdx, game, bestMove, 
                                    SMART_COMPUTER_DEPTH);
        valid = game->Move(bestMove);
        game->GetNextPlayer();
        ASSERT_TRUE(valid) << "Computer[" << playerIdx << "] " 
                    << "played an invalid move.";
    }
   
    delete[] bestMove;
    
    ASSERT_TRUE(game->GetScore(1) > game->GetScore(0)) << "The dumber computer won.";
}

/*
// Deprecated, already done by SmartestComputerPlayerWins
TEST_F(AweleTest, ComputerPlaysValidMoves) {
    ComputerPlayer computer(5);

    bool valid = true;

    char* bestMove = new char[2]; // move can be 1 digits (0 - 5)
    while (valid && !game->IsFinished()) {
        computer.ExpectedScore(game->currentPlayerIndex, game, bestMove, 5);
        valid = game->Move(bestMove);
        game->GetNextPlayer();
        ASSERT_TRUE(valid) << "Computer played an invalid move";
    }
   
    delete[] bestMove;
}
*/



}  // namespace
