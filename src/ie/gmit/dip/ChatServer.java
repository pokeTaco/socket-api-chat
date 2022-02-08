package ie.gmit.dip;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

import static ie.gmit.dip.UserPrompts.*;

import static ie.gmit.dip.Strings.*;

public final class ChatServer {
    private static ServerSocket serverSocket;
    private static boolean keepRunning = true;

    private static int openSocket() {
        int port = userInt(1023, 65535, SERVER_ENTER_PORT.get());
        try {
            serverSocket = new ServerSocket(port);
            serverSocket.setSoTimeout(1000); // This timeout prevents .accept() from blocking on shutdown.
            SERVER_LISTENING_ON_PORT.printf(port);
        } catch (IOException e) {
            ERR_SERVER_SOCKET_OPEN.println();
            openSocket();
        }
        return port;
    }

    private static void closeSocket() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            ERR_SERVER_SOCKET_CLOSE.println();
        }
    }

    private static ExecutorService start() {
        ExecutorService localSession = Executors.newSingleThreadExecutor();
        try {
            localSession.execute(new UserSession("localhost", openSocket()));
            localSession.shutdown();
            return localSession;
        } catch (IOException e) {
            e.printStackTrace();
            ERR_SERVER_LOCAL_SESSION.println();
            closeSocket();
            return null;
        }
    }

    public static void main(String[] args) {
        while (keepRunning) {
            clearConsole();
            System.out.println(APP_HEADER_SERVER.get());
            System.out.println(APP_WELCOME_SERVER.get());
            ExecutorService localSession = start();
            if (localSession == null) return;

            List<Connection> connections = Collections.synchronizedList(new ArrayList<>());
            ExecutorService connectionService = Executors.newCachedThreadPool();

            while (!localSession.isTerminated()) {
                try {
                    Socket socket = serverSocket.accept();
                    Connection connection = new Connection(socket, connections);
                    connectionService.execute(connection);
                    connections.add(connection);
                } catch (SocketTimeoutException ignored) {
                } catch (IOException e) {
                    WARN_SERVER_CONNECTION_FAILED.println();
                }
            }

            if (!connections.isEmpty()) {
                SERVER_CLOSING_CONNECTIONS.println();
                for (Connection connection : connections) {
                    connection.close();
                }
                while (true) if (connections.isEmpty()) break;
            }

            connectionService.shutdown();
            while (true) if (connectionService.isTerminated()) break;
            closeSocket();

            synchronized (System.out) {
                keepRunning = userBoolean(SERVER_RUN_AGAIN_YN.get());
            }
        }
        APP_EXIT.println();
    }

    private ChatServer() {
    }
}