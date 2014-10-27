#include "Game.hpp"
#include "Player.hpp"
        
#include <string>
#include <iostream>

/**
 * Create a new player.
 */
Player::Player() {
    m_playerName.assign("");
}

/**
 * Create a new player with the specified player number.
 *
 * @param playerNo player number
 */
Player::Player(int playerNo) : Player() {
    m_playerNo = playerNo;
}

/**
 * Set the name of the player
 *
 * @param name player name
 */
void Player::SetName(char* name){
    m_playerName.assign(name);
}

/**
 * Set the player number
 *
 * @param no player number
 */
void Player::SetPlayerNo(int no){
    m_playerNo = no;
}

/**
 * Friend function to push player REFERENCE to stream
 */
std::ostream& operator<<(std::ostream &stream, Player const& player) {
    return stream << player.m_playerName;
}

/**
 * Friend function to push player POINTER to stream
 */
std::ostream& operator<<(std::ostream &stream, Player* player) {
    return stream << player->m_playerName;
}
