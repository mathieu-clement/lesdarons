cmake_minimum_required (VERSION 2.6)
project (TAP3Project)
add_executable (TAP3Project 
                    main.cpp
                    Awele.cpp  Game.cpp  Nim.cpp  
                    Player.cpp AdvicedPlayer.cpp HumanPlayer.cpp ComputerPlayer.cpp)
set (TAP3Project_VERSION_MAJOR 1)
set (TAP3Project_VERSION_MINOR 0)

#list( APPEND CMAKE_CXX_FLAGS "-std=c++0x ${CMAKE_CXX_FLAGS} -g -Wall -Wextra")
add_definitions (
    #-std=c++0x
    -std=gnu++0x
    -g
    -Wall -Wextra
)

# Google Test framework
add_subdirectory (gtest-1.7.0)
enable_testing()
include_directories(${gtest_SOURCE_DIR}/include ${gtest_SOURCE_DIR})

# Unit tests
add_executable (runUnitTests awele_test.cpp 
                    Awele.cpp  Game.cpp  Nim.cpp  
                    Player.cpp AdvicedPlayer.cpp HumanPlayer.cpp ComputerPlayer.cpp)
target_link_libraries (runUnitTests gtest gtest_main)
add_test (runUnitTests runUnitTests )
