#define protected public

#include "Game.hpp"
#include "Awele.hpp"

#undef protected

#include "gtest/gtest.h"
#include <string>

namespace {

// The fixture for testing class Foo.
class AweleTest : public ::testing::Test {
    public:
        AweleTest() {
            game = new Awele();
        }

        ~AweleTest() {
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
        EXPECT_EQ(5, game->board[i]);
}

TEST_F(AweleTest, TakeFromFour) {
    game->Move("4");
    EXPECT_EQ(0, game->board[4]);
    for (int i = 5; i < 9; i++)
        EXPECT_EQ(5, game->board[i]);
}

TEST_F(AweleTest, CloneKeepsOriginalBoard) {
    Game* copy;
    copy = game->Clone();
    copy->Move("0");
    ASSERT_EQ(4, game->board[4]); // check original didn't change
    ASSERT_EQ(5, copy->board[4]);
}


}  // namespace
