#include "Game.hpp"
#include "Player.hpp"
        
#include <string>
#include <iostream>

Player::Player() {
    m_playerName.assign("");
}

Player::Player(int playerNo) : Player() {
    m_playerNo = playerNo;
}

void Player::Play(Game& game){

}

void Player::SetName(char* name){
    m_playerName.assign(name);
}

void Player::SetPlayerNo(int no){
    m_playerNo = no;
}

std::ostream& operator<<(std::ostream &stream, Player const& player) {
    return stream << player.m_playerName;
}

std::ostream& operator<<(std::ostream &stream, Player* player) {
    return stream << player->m_playerName;
}
