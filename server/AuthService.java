package gb.server;

import java.sql.*;

public class AuthService {
    private static Connection connection;
    private static Statement stmt;

    public static void connect() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:mainDB.db");
            stmt = connection.createStatement();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static String getNickByLoginAndPass(String login, String pass) {
        String active = getActiveByLoginAndPass(login, pass);
        String sql = String.format("SELECT nickname FROM main \n" +
                "where login = '%s'\n" +
                "and password= '%s'", login, pass);

        try {
            ResultSet rs = stmt.executeQuery(sql);
            //вызываем метод для проверки активности
            if (active.equals("0")) {
                if (rs.next()) {
                    return rs.getString(1);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    //метод проверки активности пользователя (возвращает 0 или 1 из столбца Active)
    public static String getActiveByLoginAndPass(String login, String pass) {
        String actriveUser = String.format("SELECT Active FROM main \n" +
                "where login = '%s'\n" +
                "and password = '%s'", login, pass);
        try {
            ResultSet aUser = stmt.executeQuery(actriveUser);
            return aUser.getString(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    //метод записи активности пользователя, выставляется в БД значение 1 в столбце Active
    public static void setActiveByLogin(String login) {
        String actriveUser = String.format("UPDATE main\n" +
                "SET Active = 1\n" +
                "WHERE login = '%s';", login);
        try {
            stmt.executeQuery(actriveUser);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //метод записи активности пользователя, выставляется в БД значение 0 в столбце Active
    public static void setNotActiveByLogin(String nickname) {
        String actriveUser = String.format("UPDATE main\n" +
                "SET Active = 0\n" +
                "WHERE nickname = '%s';", nickname);
        try {
            stmt.executeQuery(actriveUser);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static void disconnect() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
