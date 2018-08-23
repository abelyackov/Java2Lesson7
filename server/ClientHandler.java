package gb.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {
    private Server server;
    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;
    private String nick;

    public ClientHandler(Server server, Socket socket) {
        try {
            this.socket = socket;
            this.server = server;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
            new Thread(() -> {
                try {
                    // цикл авторизации
                    while (true) {
                        String str = in.readUTF();
                        if (str.startsWith("/auth")) {
                            String[] tokens = str.split(" ");
                            String newNick = AuthService.getNickByLoginAndPass(tokens[1], tokens[2]);
                            if (newNick != null) {
                                sendMsg("/authok");
                                AuthService.setActiveByLogin(tokens[1]);
                                nick = newNick;
                                server.subscibe(this);
                                break;
                            } else {
                                sendMsg("Неверный логин/пароль");
                            }
                        }
                    }
                    // цикл для работы
                    while (true) {
                        String str = in.readUTF();
                        //проверка на введенный текст, если текст начинается на /w, то вызываем метод отправки сообщения на сервере
                        //и передает в метод nickname и сообщение
                        String[] msgForNickname = str.split(" ");
                        if (msgForNickname[0].equals("/w"))
                            server.msgForName(msgForNickname[1], msgForNickname[2]);
                        else {
                            if (str.equals("/end")) {
                                AuthService.setNotActiveByLogin(nick);
                                out.writeUTF("/serverclosed");
                                break;
                            }
                            server.broadcastMsg(nick + ": " + str);
                            System.out.println("Client: " + str);
                        }

                    }


                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    server.unsubscibe(this);
                }
            }).start();
        } catch (
                Exception e)

        {
            e.printStackTrace();
        }

    }

    public void sendMsg(String msg) {
        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
