package ru.gb.jtwo.chat.client.giu.fx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import ru.gb.jtwo.chat.client.core.ChatClient;
import ru.gb.jtwo.chat.client.core.ChatClientListener;
import ru.gb.jtwo.chat.common.Library;

import java.util.Arrays;


public class ClientController implements ChatClientListener {

    @FXML
    private TextField tfAddress ;
    @FXML
    private TextField tfPort ;
    @FXML
    private TextField tfMessage ;
    @FXML
    private TextArea taLog;
    @FXML
    private TextField tfLogin;
    @FXML
    private PasswordField pfPassword ;

    private ChatClient client ;

    public ClientController() {
        client = new ChatClient(this) ;
    }

    public void onClickedLogin(ActionEvent actionEvent) {
        String address = tfAddress.getText() ;
        String port = tfPort.getText() ;
        String login = tfLogin.getText() ;
        String password = pfPassword.getText();
        if ("".equals(address) || "".equals(port) || "".equals(login) || "".equals(password)) {
            printMessage("Заполните параметры подключения");
        } else {
            client.connect(address, port, login, password);
        }
    }

    public void onClickedDisconnect(ActionEvent actionEvent) {
        client.disconnect();
    }

    public void onSendMessage(ActionEvent actionEvent) {
        String msg = tfMessage.getText() ;
        if (!"".equals(msg))
        client.sendMessage(msg);
        tfMessage.setText("");
    }

    @Override
    public void onClientMessage(String msg) {
        printMessage(msg);
    }

    private void printMessage(String msg) {
        String[] arrMsg = msg.split(Library.DELIMITER) ;
        if (arrMsg.length < 2) return;

        switch (arrMsg[0]) {
            case Library.TYPE_BROADCAST: printBroadcastMessage(arrMsg[1], arrMsg[2], arrMsg[3]);
                break;
            case Library.AUTH_ACCEPT: printMessageAuthAccept(arrMsg[1]) ;
                break;
            case Library.AUTH_DENIED: printMessageAuthDenied(arrMsg[1]) ;
                break;
            case Library.MSG_FORMAT_INFO: printMessageInfo(arrMsg[1], arrMsg[2]);
                break;
            case Library.MSG_FORMAT_ERROR: printMessageError(arrMsg[1]);
                break;
            case Library.USER_ADD_CHAT: addUser(arrMsg[1]);
                break;
            case Library.USER_REMOVE_CHAT: removeUser(arrMsg[1]);
                break;
            case Library.USERS_LIST: initUserList(Arrays.copyOfRange(arrMsg, 1 , arrMsg.length));
                break;
            default: throw new RuntimeException(msg) ;
        }

        taLog.appendText(msg + "\n");
    }

    private void printBroadcastMessage(String date, String author, String msg ) {
        // TODO
    }

    private void printMessageAuthAccept(String nickname) {
        // TODO
    }

    private void printMessageAuthDenied(String nickname) {
        // TODO
    }

    private void printMessageInfo(String date, String msg) {
        // TODO
    }

    private void printMessageError(String msg) {
        // TODO
    }

    private void addUser(String nickname) {
        // TODO
    }

    private void removeUser(String nickname) {
        // TODO
    }

    private void initUserList(String[] nicknames) {
        for (String nickname : nicknames) {
            addUser(nickname);
        }
    }
}
