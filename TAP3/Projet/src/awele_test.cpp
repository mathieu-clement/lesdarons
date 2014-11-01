#define protected public

#include "Game.hpp"
#include "Awele.hpp"

#undef protected

#include "gtest/gtest.h"
#include <string>
#include <sstream>
#include <iostream>

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


}  // namespace
