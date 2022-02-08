package ie.gmit.dip;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * An enumeration that stores any Strings used by the application as constants to keep the code clean and the text uniform.
 */
public enum Strings {
    APP_HEADER_CLIENT(hlBlue("***************************************************") + "\n" +
            hlBlue("* GMIT - Dept. Computer Science & Applied Physics *") + "\n" +
            hlBlue("*                                                 *") + "\n" +
            hlBlue("*           Socket API Chat Client V1.0           *") + "\n" +
            hlBlue("*     H.Dip in Science (Software Development)     *") + "\n" +
            hlBlue("*                                                 *") + "\n" +
            hlBlue("***************************************************") + "\n"),
    APP_HEADER_SERVER(hlBlue("***************************************************") + "\n" +
            hlBlue("* GMIT - Dept. Computer Science & Applied Physics *") + "\n" +
            hlBlue("*                                                 *") + "\n" +
            hlBlue("*           Socket API Chat Server V1.0           *") + "\n" +
            hlBlue("*     H.Dip in Science (Software Development)     *") + "\n" +
            hlBlue("*                                                 *") + "\n" +
            hlBlue("***************************************************") + "\n"),
    APP_EXIT("Good bye!"),
    APP_WELCOME_CLIENT("Welcome to Socket API Chat Client. Use this program to connect to a host.\r\n"),
    APP_WELCOME_SERVER("Welcome to Socket API Chat Server. Use this program to host a session.\r\n"),
    CURSOR(" >> "),
    CLIENT_CONNECTING_TO_HOST("Connecting... Please wait."),
    CLIENT_ENTER_HOSTNAME("Please enter a host name or IP address:\r\n" + CURSOR.get()),
    CLIENT_ENTER_PORT("Please enter a port number (1024 through 65535):\r\n" + CURSOR.get()),
    CLIENT_NO_CONNECTION_TRY_AGAIN_YN("The host was not found or rejected the connection."),
    CLIENT_RUN_AGAIN_YN(sYellow("Connect to a different host? (y/n)\r\n") + CURSOR.get()),
    CONNECTION_USER_LIST_HEADER("Currently connected:"),
    SERVER_CLOSING_CONNECTIONS("Closing all connections..."),
    SERVER_ENTER_PORT(CLIENT_ENTER_PORT.get()),
    SERVER_LISTENING_ON_PORT("Now listening on port %s.\r\n"),
    SERVER_RUN_AGAIN_YN(sYellow("Server shut down. Start listening on another port? (y/n)\r\n") + CURSOR.get()),
    SESSION_CHANGE_NICK("Enter a username:\r\n" + CURSOR.get()),
    SESSION_PENDING("Username set. Waiting for server to join session..."),
    SESSION_TERMINATED_FLUSH_KEYBOARD("\r\nYour session has ended, please press ENTER."),
    SESSION_USER_JOINED(sGrey("%s has entered the chat.") + "%n"),
    SESSION_USER_LEFT(sGrey("%s has left the chat.")),
    WARN_SERVER_CONNECTION_FAILED("Failed to accept incoming connection."),
    ERR_INVALID_INPUT("That doesn't seem quite right. Please try again."),
    ERR_SERVER_LOCAL_SESSION("Failed to create local session. Shutting down..."),
    ERR_SERVER_SOCKET_OPEN("Failed to create server socket."),
    ERR_SERVER_SOCKET_CLOSE("Failed to close server socket.");

    private final String string;

    Strings(String string) {
        this.string = string;
    }

    public String get() {
        return this.string;
    }

    public String yellow() {
        return "\033[0;33m" + this.string + "\033[0m";
    }

    public void println() {
        System.out.println(yellow());
    }

    public void printf(Object... args) {
        System.out.printf(yellow(), args);
    }

    public static String sYellow(String s) {
        return "\033[0;33m" + s + "\033[0m";
    }

    public static String sGrey(String s)  { return "\033[0;90m" + s + "\033[0m"; }

    private static String hlBlue(String s) {
        return "\033[0;97m" + "" + "\033[44m" + s + "\033[0m";
    }

    static String timestamp(String message) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String timestamp = sdf.format(new Date());
        return " " + timestamp + " " + message;
    }

    public static void clearConsole() { // NOT MY WORK, SOURCE: https://www.delftstack.com/howto/java/java-clear-console/ (sans typos)
        try {
            String operatingSystem = System.getProperty("os.name"); //Check the current operating system
            if (operatingSystem.contains("Windows")) {
                ProcessBuilder pb = new ProcessBuilder("cmd", "/c", "cls");
                Process startProcess = pb.inheritIO().start();
                startProcess.waitFor();
            } else {
                ProcessBuilder pb = new ProcessBuilder("clear");
                Process startProcess = pb.inheritIO().start();
                startProcess.waitFor();
            }
        } catch (Exception ignored) {
        }
    }
}