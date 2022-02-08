package ie.gmit.dip;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static ie.gmit.dip.Strings.*;
import static ie.gmit.dip.UserPrompts.*;

public class UserSession implements Runnable {
    private final Socket socket;
    private final PrintWriter out;
    private final BufferedReader KB = new BufferedReader(new InputStreamReader(System.in));
    private String username;
    private volatile boolean keepRunning = true;
    private volatile ExecutorService chatInput;

    private synchronized void changeNick() {
        this.username = userString(1, 20, SESSION_CHANGE_NICK.get());
        out.println("/nick " + username);
    }

    public UserSession(String hostname, int port) throws IOException {
        this.socket = new Socket(hostname, port);
        this.out = new PrintWriter(socket.getOutputStream(), true);
        changeNick();
        SESSION_PENDING.println();
    }

    private void exit() {
        try {
            keepRunning = false;
            chatInput.shutdown();
            chatInput.awaitTermination(10, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            exit(); // ChatInput must die before this thread
        }
    }

    private void post(String message) {
        switch (message) {
            case "":
                break;
            case "/q":
                this.out.println("/q");
                keepRunning = false;
                break;
            case "/who":
                this.out.println("/who");
                break;
            default:
                if (message.startsWith("/me ")) {
                    this.out.printf(sGrey("@%s %s") + "%n", username, message.substring(4));
                } else {
                    this.out.printf(sGrey("@%s") + " %s%n", username, message);
                }
        }
    }

    public void run() {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            chatInput = Executors.newSingleThreadExecutor();
            chatInput.execute(new ChatInput());
            this.out.printf(SESSION_USER_JOINED.get(), username);
            String incomingMessage;
            while (keepRunning) {
                if ((incomingMessage = in.readLine()) != null) System.out.println(incomingMessage);
                else exit();
            }
        } catch (Exception ignored) {
        }
        exit();
    }

    private class ChatInput implements Runnable {
        private StringBuffer buffer = new StringBuffer();

        public boolean hasLine() {
            if (this.buffer != null) return this.buffer.toString().endsWith("\n");
            else return false;
        }

        public void run() {
            char c = ' '; // The input stream needs to be read character by character, else it blocks.
            try {
                while (keepRunning) { // Check needed before each new line, while typing and after pressing enter. Otherwise, the input will block either Server or Client.
                    while (!hasLine()) {
                        if (!keepRunning) return;
                        c = (char) KB.read();
                        this.buffer.append(c);
                        c = ' ';
                    }
                    if (!keepRunning) return;
                    String message = this.buffer.toString().replaceAll("[\r\n]", "");
                    if (!message.matches("[\\u0020]+")) post(message);
                    this.buffer = new StringBuffer();
                }
            } catch (IOException ignored) {
            }
        }
    }
}