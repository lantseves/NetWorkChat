package ru.gb.jtwo.chat.client.core;

import com.sun.javafx.image.BytePixelSetter;
import jdk.nashorn.internal.runtime.OptimisticReturnFilters;
import ru.gb.jtwo.chat.common.Library;
import ru.gb.jtwo.network.SocketThread;
import ru.gb.jtwo.network.SocketThreadListener;

import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
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

    public void rename(String login , String newNickname) {
        if(socketThread !=null) {
            socketThread.sendMessage(Library.getRenameRequest(login , newNickname)) ;
        } else {
            putLog(Library.getMsgFormatError("Вы не авторизованы на сервере"));
        }
    }

    @Override
    public void onSocketStart(SocketThread thread, Socket socket) {
        putLog(Library.getMsgFormatInfo("Начало подключения к серверу"));
        listener.onStartConnect();
    }

    @Override
    public void onSocketStop(SocketThread thread) {
        putLog(Library.getMsgFormatInfo("Соединение с сервером остановлено"));
        listener.onDisconnect();
    }

    @Override
    public void onSocketReady(SocketThread thread, Socket socket) {
        thread.sendMessage(Library.getAuthRequest(login, password));
        listener.onReadyConnect();
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

    public void addMessageInLog(String msg) {
        if (login == null) return;

        File file = new File("chat-client/logs") ;
        if (!file.exists()) if(!file.mkdirs()) return;

        String fileName = String.format("chat-client/logs/history_%s.txt" , login) ;
        try(BufferedWriter out = new BufferedWriter(new FileWriter(fileName, true))) {
            out.write(msg + System.lineSeparator());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String[] loadMessageFromLog(String login) {
        File file = new File(String.format("chat-client/logs/history_%s.txt", login)) ;
        if (file.exists()) {
            List<String> logList = new ArrayList<>(100);
            try (BufferedReader in = new BufferedReader(new FileReader(file))) {
                String str;
                while ((str = in.readLine()) != null)
                    logList.add(str);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return Arrays.copyOfRange(logList.toArray(new String[0]), Math.max(logList.size() - 100, 0), logList.size());
        } else {
            return null ;
        }
    }


    private void putLog(String msg) {
        listener.onClientMessage(msg);
    }
}
