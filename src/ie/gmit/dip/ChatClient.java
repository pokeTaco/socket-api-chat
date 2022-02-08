package ie.gmit.dip;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import static ie.gmit.dip.Strings.*;
import static ie.gmit.dip.UserPrompts.*;

public final class ChatClient {
    private static boolean keepRunning = true;

    public static void main(String[] args) {
        while (keepRunning) {
            clearConsole();
            System.out.println(APP_HEADER_CLIENT.get());
            System.out.println(APP_WELCOME_CLIENT.get());
            String hostname = userString(1, 100, CLIENT_ENTER_HOSTNAME.get());
            int port = userInt(1023, 65535, CLIENT_ENTER_PORT.get());
            try {
                ExecutorService session = Executors.newSingleThreadExecutor();
                CLIENT_CONNECTING_TO_HOST.println();
                session.execute(new UserSession(hostname, port));
                session.shutdown();
                while (true) if (session.isTerminated()) break;
                /*
                 * This next block is a little awkward but necessary; the UserSession's
                 * ChatInput has a tendency to live on even after it has supposedly
                 * been terminated.
                 * This zombie thread dies after the user presses Enter once (or twice),
                 * however, this needs to happen BEFORE the user is prompted to enter any
                 * new host name etc. or else their console will misbehave.
                 *
                 * (Any resource reading from System.in cannot be properly closed, or it
                 * will render System.in unusable until the end of the entire program.)
                 */
                synchronized (System.out) {
                    Scanner sc = new Scanner(System.in);
                    SESSION_TERMINATED_FLUSH_KEYBOARD.println();
                    sc.nextLine();
                }
            } catch (IOException e) {
                CLIENT_NO_CONNECTION_TRY_AGAIN_YN.println();
            }
            keepRunning = userBoolean(CLIENT_RUN_AGAIN_YN.get());
        }
        APP_EXIT.println();
    }

    private ChatClient() {}
}