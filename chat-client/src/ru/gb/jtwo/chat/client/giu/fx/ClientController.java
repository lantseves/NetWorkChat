package ru.gb.jtwo.chat.client.giu.fx;

import com.sun.org.apache.bcel.internal.generic.NEW;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import ru.gb.jtwo.chat.client.core.ChatClient;
import ru.gb.jtwo.chat.client.core.ChatClientListener;

//Класс отвечает только за обновление GUI
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
            printInfoMessage("Заполните параметры подключения");
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

    private void printInfoMessage(String msg) {
        taLog.appendText(msg + "\n");
    }
    
    @Override
    public void onClientMessage(String msg) {
        printInfoMessage(msg);
    }
}
