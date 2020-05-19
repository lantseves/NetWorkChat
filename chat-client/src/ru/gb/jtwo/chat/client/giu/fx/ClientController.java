package ru.gb.jtwo.chat.client.giu.fx;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import ru.gb.jtwo.chat.client.core.ChatClient;
import ru.gb.jtwo.chat.client.core.ChatClientListener;
import ru.gb.jtwo.chat.common.Library;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;


public class ClientController implements ChatClientListener , Initializable {
    private final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM HH:mm:ss");

    private final static Font FRONT_TITLE = Font.font("Verdana", FontWeight.BOLD , 12) ;
    private final static Font FRONT_TEXT = Font.font("Verdana", 12) ;

    private final static Color COLOR_TITLE_USERS_MESSAGE = Color.GREEN ;
    private final static Color COLOR_TITLE_MY_MESSAGE = Color.BLUE ;
    private final static Color COLOR_INFO_MESSAGE = Color.GREY ;
    private final static Color COLOR_ERROR_MESSAGE = Color.RED ;
    private final static Color COLOR_TEXT_USERS_MESSAGE = Color.BLACK ;

    @FXML
    private TextField tfAddress ;
    @FXML
    private TextField tfPort ;
    @FXML
    private TextField tfMessage ;
    @FXML
    private TextFlow tfLog;
    @FXML
    private TextField tfLogin;
    @FXML
    private PasswordField pfPassword ;
    @FXML
    private TextField tfnickname ;
    @FXML
    private TextArea taUsers ;
    @FXML
    private Button btnSend ;
    @FXML
    private Button btnLogin ;
    @FXML
    private Button btnDisconnect ;
    @FXML
    private Button btnRename ;

    private ChatClient client ;
    private String nickname ;
    private SortedSet<String> clients = new TreeSet<>() ;

    public ClientController() {
        client = new ChatClient(this) ;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        printLogMessage() ;
    }

    public void onClickedLogin(ActionEvent actionEvent) {
        String address = tfAddress.getText() ;
        String port = tfPort.getText() ;
        String login = tfLogin.getText() ;
        String password = pfPassword.getText();
        if ("".equals(address) || "".equals(port) || "".equals(login) || "".equals(password)) {
            onClientMessage(Library.getMsgFormatError("Заполните параметры подключения"));
        } else {
            client.connect(address, port, login, password);
        }
    }

    public void onClickedDisconnect(ActionEvent actionEvent) {
        client.disconnect();
    }

    public void onClickedRename(ActionEvent actionEvent) {
        if(!nickname.equals(tfnickname.getText()))
        client.rename(tfLogin.getText() , tfnickname.getText());
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
        client.addMessageInLog(msg);
    }

    @Override
    public void onStartConnect() {

    }

    @Override
    public void onReadyConnect() {
        btnSend.setDisable(false);
        tfMessage.setDisable(false);
        btnDisconnect.setDisable(false);
        btnRename.setDisable(false);

        tfLogin.setDisable(true);
        pfPassword.setDisable(true);
        btnLogin.setDisable(true);
        tfAddress.setDisable(true);
        tfPort.setDisable(true);
        tfnickname.setText(this.nickname);

        printLogMessage();
    }

    @Override
    public void onDisconnect() {
        btnSend.setDisable(true);
        tfMessage.setDisable(true);
        btnDisconnect.setDisable(true);
        btnRename.setDisable(true);

        tfLogin.setDisable(false);
        pfPassword.setDisable(false);
        btnLogin.setDisable(false);
        tfAddress.setDisable(false);
        tfPort.setDisable(false);

        tfnickname.clear();
        clients.clear();
        updateListUsers();
    }

    private void printMessage(String msg) {
        String[] arrMsg = msg.split(Library.DELIMITER) ;
        if (arrMsg.length < 1) return;
        switch (arrMsg[0]) {
            case Library.TYPE_BROADCAST: printBroadcastMessage(arrMsg[1], arrMsg[2], arrMsg[3]);
                break;
            case Library.AUTH_ACCEPT: printMessageAuthAccept(arrMsg[1]) ;
                break;
            case Library.AUTH_DENIED: printMessageAuthDenied() ;
                break;
            case Library.RENAME_ACCEPT: printRenameAccept(arrMsg[1]);
                break;
            case Library.RENAME_DENIED: printMessageError("Не удалось выполнить смену имени пользователия, повторите попытку!!!");
                break;
            case Library.MSG_FORMAT_INFO: printMessageInfo(arrMsg[1], arrMsg[2]);
                break;
            case Library.MSG_FORMAT_ERROR: printMessageError(arrMsg[1]);
                break;
            case Library.USER_ADD_CHAT: printAddUser(arrMsg[1]);
                break;
            case Library.USER_REMOVE_CHAT: printRemoveUser(arrMsg[1]);
                break;
            case Library.USERS_LIST: initUserList(Arrays.copyOfRange(arrMsg, 1 , arrMsg.length));
                break;
            default: throw new RuntimeException(msg) ;
        }
    }

    private void printBroadcastMessage(String date, String author, String msg ) {

        Text title = new Text(String.format("%s %s:\n" , author, getDateString(date)));
        Color color = author.equals(nickname) ? COLOR_TITLE_MY_MESSAGE : COLOR_TITLE_USERS_MESSAGE;
        title.setFill(color);
        title.setFont(FRONT_TITLE);

        Text text = new Text(msg + "\n\n") ;
        text.setFill(COLOR_TEXT_USERS_MESSAGE);
        text.setFont(FRONT_TEXT);

        print(title, text);
    }

    private void printMessageAuthAccept(String nickname) {
        Text text = getTextLineTextFlow(nickname + " успешно авторизован.", COLOR_INFO_MESSAGE, FRONT_TITLE) ;
        print(text);
        this.nickname = nickname ;
        tfnickname.setText(nickname);
    }

    private void printMessageAuthDenied() {
        Text text = getTextLineTextFlow("Введённые вами данные не верны, проверьте логин и пароль.", COLOR_INFO_MESSAGE, FRONT_TITLE) ;
        print(text);
    }

    private void printRenameAccept(String newNickname) {
        this.nickname = newNickname ;
        updateListUsers();
        Text text = getTextLineTextFlow("Имя пользователя изменено на " + nickname, COLOR_INFO_MESSAGE, FRONT_TITLE) ;
        print(text);
    }

    private void printMessageInfo(String date, String msg) {
        Text text = getTextLineTextFlow(getDateString(date) + " " +msg, COLOR_INFO_MESSAGE, FRONT_TITLE) ;
        print(text);
    }

    private void printMessageError(String msg) {
        Text text = getTextLineTextFlow(msg, COLOR_ERROR_MESSAGE, FRONT_TITLE) ;
        print(text);
    }

    private void printAddUser(String nickname) {
        Text text = getTextLineTextFlow("Пользователь " + nickname + " добавился в чат.", COLOR_INFO_MESSAGE, FRONT_TITLE) ;
        print(text);
        addUserList(nickname);
    }

    private void printRemoveUser(String nickname) {
        Text text = getTextLineTextFlow("Пользователь " + nickname + " покинул чат.", COLOR_INFO_MESSAGE, FRONT_TITLE) ;
        print(text);
        removeUserList(nickname);
    }

    private void initUserList(String[] nicknames) {
        clients.clear();
        clients.addAll(Arrays.asList(nicknames)) ;
        updateListUsers();
    }

    private void printLogMessage() {
        clearTfLog();
        String[] msgs = client.loadMessageFromLog(tfLogin.getText()) ;
        if (msgs != null) {
            for (String s : msgs) {
                printMessage(s);
            }
        }
    }

    /**
    UtilsMethod
     */


    private Text getTextLineTextFlow(String msg , Color color , Font font) {
        Text text = new Text(msg + "\n\n") ;
        text.setFont(font);
        text.setFill(color);
        return text ;
    }

    private void addUserList(String nickname) {
        clients.add(nickname) ;
        updateListUsers();
    }

    private void removeUserList(String nickname) {
        clients.remove(nickname) ;
        updateListUsers();
    }

    private void updateListUsers() {
        taUsers.clear();
        for(String nickname : clients) {
            taUsers.appendText(nickname + "\n");
        }
    }

    private String getDateString(String date) {
        return DATE_FORMAT.format( new Date(Long.parseLong(date))) ;
    }

    private void print(Text... texts) {
        Platform.runLater(() -> {
            for(Text text: texts) {
                tfLog.getChildren().add(text);
            }
        });
    }

    private void clearTfLog() {
        Platform.runLater(() -> tfLog.getChildren().clear());
    }
}
