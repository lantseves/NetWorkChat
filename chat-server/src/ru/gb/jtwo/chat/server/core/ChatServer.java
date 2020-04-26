package ru.gb.jtwo.chat.server.core;

import ru.gb.jtwo.chat.common.Library;
import ru.gb.jtwo.network.ServerSocketThread;
import ru.gb.jtwo.network.ServerSocketThreadListener;
import ru.gb.jtwo.network.SocketThread;
import ru.gb.jtwo.network.SocketThreadListener;

import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.NonWritableChannelException;
import java.util.List;
import java.util.Vector;

public class ChatServer implements ServerSocketThreadListener, SocketThreadListener {

    ServerSocketThread server;
    ChatServerListener listener;
    Vector<SocketThread> clients = new Vector<>();

    public ChatServer(ChatServerListener listener) {
        this.listener = listener;
    }

    public void start(int port) {
        if (server != null && server.isAlive())
            putLog("Already running");
        else
            server = new ServerSocketThread(this, "Server", port, 2000);
    }

    public void stop() {
        if (server == null || !server.isAlive()) {
            putLog("Nothing to stop");
        } else {
            server.interrupt();
        }
    }

    private void putLog(String msg) {
        listener.onChatServerMessage(msg);
    }

    /**
     * Server Socket Thread Listener methods
     * */

    @Override
    public void onServerStarted(ServerSocketThread thread) {
        putLog("Server thread started");
        SqlClient.connect();
    }

    @Override
    public void onServerCreated(ServerSocketThread thread, ServerSocket server) {
        putLog("Server socket started");
    }

    @Override
    public void onServerTimeout(ServerSocketThread thread, ServerSocket server) {
        //putLog("Server timeout");
    }

    @Override
    public void onSocketAccepted(ServerSocketThread thread, ServerSocket server, Socket socket) {
        putLog("Client connected");
        String name = "SocketThread " + socket.getInetAddress() + ":" + socket.getPort();
        new ClientThread(this, name, socket);
    }

    @Override
    public void onServerException(ServerSocketThread thread, Throwable throwable) {
        putLog("Server exception");
        throwable.printStackTrace();
    }

    @Override
    public void onServerStop(ServerSocketThread thread) {
        putLog("Server thread stopped");
        SqlClient.disconnect();
    }

    /**
     * Socket Thread Listener methods
     * */

    @Override
    public void onSocketStart(SocketThread thread, Socket socket) {
        putLog("Socket started");
    }

    @Override
    public void onSocketStop(SocketThread thread) {
        ClientThread client = (ClientThread) thread ;
        putLog("Socket" + client.getNickname() +" stopped");
        clients.remove(thread);
        sendToAllAuthorizedClients(Library.getUserRemoveChat(client.getNickname()));
    }

    @Override
    public void onSocketReady(SocketThread thread, Socket socket) {
        putLog("Socket ready");
        clients.add(thread);
    }

    @Override
    public void onReceiveString(SocketThread thread, Socket socket, String msg) {
        ClientThread client = (ClientThread) thread;
        if (client.isAuthorized()) {
            handleAuthMessage(client, msg);
        } else
           handleNonAuthMessage(client, msg);
    }

    @Override
    public void onSocketException(SocketThread thread, Throwable throwable) {
        throwable.printStackTrace();
    }

    void handleAuthMessage(ClientThread client, String msg) {
        sendToAllAuthorizedClients(Library.getTypeBroadcast(client.getNickname() , msg));
    }

    void handleNonAuthMessage(ClientThread client, String msg) {
        String[] arr = msg.split(Library.DELIMITER);
        if (arr.length != 3 || !arr[0].equals(Library.AUTH_REQUEST)) {
            client.msgFormatError(msg);
            return;
        }
        String login = arr[1];
        String password = arr[2];
        String nickname = SqlClient.getNickname(login, password);
        if (nickname == null) {
            putLog(Library.getAuthDenied());
            client.authFail();
            return;
        }
        client.authAccept(nickname);
        //Автору сначало приходило сообщение об успешной авторизации, а следом рассылка, о том что он добавлен в чат
        sendToAllAuthorizedClientsWithoutAuthor(Library.getUserAddChat(nickname) , client);
        client.sendMessage(Library.getUsersList(getUserList(clients))) ;
    }

    //Синхронизировал, чтобы пока собираем список ников для отправки, другой поток не удалил пользователя
    private synchronized String[] getUserList(List<SocketThread> clients) {
        String[] arr = new String[clients.size()] ;
        for(int i = 0 ; i < arr.length ; i++) {
            ClientThread client = ((ClientThread) clients.get(i)) ;
            if(client.isAuthorized())
            arr[i] =client.getNickname() ;
        }
        return arr;
    }

    private void sendToAllAuthorizedClients(String msg) {
        sendToAllAuthorizedClientsWithoutAuthor(msg , null);
    }

    private void sendToAllAuthorizedClientsWithoutAuthor(String msg , ClientThread clientThread) {
        for (SocketThread socketThread : clients) {
            ClientThread client = (ClientThread) socketThread;
            if (!client.isAuthorized() || client == clientThread) continue;
            client.sendMessage(msg);
        }
    }

    public void dropAllClients() {
        for(SocketThread clientThread: clients) {
            clientThread.close();
        }
    }
}
