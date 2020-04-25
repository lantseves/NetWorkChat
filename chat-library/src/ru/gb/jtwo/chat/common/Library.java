package ru.gb.jtwo.chat.common;

import com.sun.javafx.sg.prism.web.NGWebView;
import jdk.nashorn.internal.runtime.OptimisticReturnFilters;

import java.util.List;

public class Library {
    /*
    * /auth_request±login±password
    * /auth_accept±nickname
    * /auth_denied
    * /broadcast±msg
    *
    * /msg_format_error±msg
    * */

    public static final String DELIMITER = "±";
    //Запрос авторизации
    public static final String AUTH_REQUEST = "/auth_request";
    //Положительный ответ об авторизации
    public static final String AUTH_ACCEPT = "/auth_accept";
    //Отрицательный ответ об авторизации
    public static final String AUTH_DENIED = "/auth_denied";
    //Сообщение об ошибке
    public static final String MSG_FORMAT_ERROR = "/msg_format_error";
    // Сообщения с инфорамцией от сервера
    public static final String MSG_FORMAT_INFO = "/msg_format_info";
    //Пользователь добавился в чат
    public static final String USER_ADD_CHAT = "/user_add_chat";
    //Пользователь покинул чат
    public static final String USER_REMOVE_CHAT = "/user_remove_chat";
    // Список пользователей в чате
    public static final String USERS_LIST = "users_list" ;

    // если мы вдруг не поняли, что за сообщение и не смогли разобрать
    public static final String TYPE_BROADCAST = "/bcast";
    // то есть сообщение, которое будет посылаться всем

    // Метод собирает сообщение авторизации для отправки на сервер
    public static String getAuthRequest(String login, String password) {
        return AUTH_REQUEST + DELIMITER + login + DELIMITER + password;
    }

    //Метод возвращает положительный ответ об авторизации
    public static String getAuthAccept(String nickname) {
        return AUTH_ACCEPT + DELIMITER + nickname;
    }

    //Метод возвращает отрицательный ответ об авторизации
    public static String getAuthDenied() {
        return AUTH_DENIED;
    }

    //Метод для сообщений об ошибке, не для массовой рассылки
    public static String getMsgFormatError(String message) {
        return MSG_FORMAT_ERROR + DELIMITER + message;
    }

    public static String getMsgFormatInfo(String msg) {
        return MSG_FORMAT_INFO + DELIMITER + System.currentTimeMillis() + DELIMITER + msg ;
    }
    //Метод сообщает, что один из пользователей добавился в чат
    public static  String getUserAddChat(String nickname) {
        return USER_ADD_CHAT + DELIMITER + nickname;
    }

    //Метод сообщает, что один из пользователей покинул чат
    public static String getUserRemoveChat(String nickname) {
        return USER_REMOVE_CHAT + DELIMITER + nickname ;
    }

    //Метод собирает список пользователей в 1 строку
    public static String getUsersList(String[] users) {
        StringBuilder result = new StringBuilder(USERS_LIST) ;
        for(String nickname : users) {
            result.append(DELIMITER + nickname);
        }
        return result.toString() ;
    }

    //Метод формирует сообщение для массовой рассылки
    public static String getTypeBroadcast(String src, String message) {
        return TYPE_BROADCAST + DELIMITER + System.currentTimeMillis() +
                DELIMITER + src + DELIMITER + message;
    }

}
