package ru.gb.jtwo.chat.client.core;

public interface ChatClientListener {
    void onClientMessage(String msg) ;

    void onStartConnect() ;

    void onReadyConnect() ;

    void onDisconnect() ;
}
