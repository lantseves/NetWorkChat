package ru.gb.jtwo.chat.client.core;

import com.sun.javafx.image.BytePixelSetter;
import jdk.nashorn.internal.runtime.OptimisticReturnFilters;
import ru.gb.jtwo.chat.common.Library;
import ru.gb.jtwo.network.SocketThread;
import ru.gb.jtwo.network.SocketThreadListener;

import java.io.EOFException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

//Класс отвечает только за получение и отправку сообщения серверу, теперь к этому классу можно подключить любой GUI
public class ChatClient implements SocketThreadListener {

    private final List<String> userList = new ArrayList<>();
    private SocketThread socketThread;
    private ChatClientListener listener ;
    private String login ;
    private String password;

    public ChatClient(ChatClientListener listener) {
        this.listener = listener;
    }

    public void connect(String address, String port, String login, String password) {

        try {
            Socket socket = new Socket(address, Integer.parseInt(port));
            socketThread = new SocketThread(this, "Client", socket);
            this.login = login;
            this.password = password;
        } catch (ConnectException e) {
            listener.onClientMessage(Library.getMsgFormatError("Нет соединения с сервером"));
        } catch (IOException e) {
            listener.onClientMessage(Library.getMsgFormatError(e.getMessage()));
        }
    }

    public void disconnect() {
        if(socketThread != null)
            socketThread.close();
    }

    public void sendMessage(String msg) {
        if(socketThread != null)
            socketThread.sendMessage(msg);
        else
            putLog(Library.getMsgFormatError("Вы не авторизованы на сервере"));
    }

    @Override
    public void onSocketStart(SocketThread thread, Socket socket) {
        putLog(Library.getMsgFormatInfo("Start"));
    }

    @Override
    public void onSocketStop(SocketThread thread) {
        putLog(Library.getMsgFormatInfo("Stop"));
    }

    @Override
    public void onSocketReady(SocketThread thread, Socket socket) {
        thread.sendMessage(Library.getAuthRequest(login, password));
    }

    @Override
    public void onReceiveString(SocketThread thread, Socket socket, String msg) {
        putLog(msg);
    }

    @Override
    public void onSocketException(SocketThread thread, Throwable throwable) {

        if(!(throwable instanceof SocketException || throwable instanceof EOFException))
            putLog(Library.getMsgFormatError(throwable.getMessage()));
    }

    private void putLog(String msg) {
        listener.onClientMessage(msg);
    }
}
