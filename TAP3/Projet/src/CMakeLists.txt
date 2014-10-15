cmake_minimum_required (VERSION 2.6)
project (TAP3Project)
add_executable (TAP3Project Awele.cpp  Game.cpp  main.cpp  Nim.cpp  Player.cpp AdvicedPlayer.cpp HumanPlayer.cpp ComputerPlayer.cpp)
set (TAP3Project_VERSION_MAJOR 1)
set (TAP3Project_VERSION_MINOR 0)

#list( APPEND CMAKE_CXX_FLAGS "-std=c++0x ${CMAKE_CXX_FLAGS} -g -Wall -Wextra")
add_definitions (
    -std=c++0x
    -g
    -Wall -Wextra
)
