package ie.gmit.dip;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

import static ie.gmit.dip.Strings.*;

class Connection implements Runnable {
    private final Socket socket;
    private final List<Connection> connections;
    private final BufferedReader in;
    private final PrintWriter out;
    private String username;

    public void close() {
        try {
            this.socket.close();
        } catch (IOException ignored) {
        }
    }

    public Connection(Socket socket, List<Connection> connections) throws IOException {
        this.socket = socket;
        this.connections = connections;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(), true);
    }

    private void listUsers() {
        int i = 1;
        this.out.println(CONNECTION_USER_LIST_HEADER.get());
        for (Connection connection : connections) {
            this.out.println(" (" + i + ")" + " " + connection);
            i++;
        }
        this.out.println(" ");
    }

    private void broadcast(String message) {
        for (Connection connection : connections) {
            connection.out.println(timestamp(message));
        }
    }

    public void run() {
        while (!socket.isClosed()) {
            try {
                String message;
                if ((message = in.readLine()) != null) {
                    if (message.startsWith("/nick ")) this.username = message.substring(6);
                    else
                        switch (message.toLowerCase()) {
                            case "/q":
                                socket.close();
                                break;
                            case "/who":
                                listUsers();
                                break;
                            default:
                                broadcast(message);
                        }
                } else {
                    socket.close();
                }
            } catch (IOException e) {
                break;
            }
        }
        broadcast(String.format(SESSION_USER_LEFT.get(), username));
        connections.remove(this);
    }

    public String toString() {
        return this.username + " " + this.socket.getInetAddress() + ":" + this.socket.getPort();
    }
}