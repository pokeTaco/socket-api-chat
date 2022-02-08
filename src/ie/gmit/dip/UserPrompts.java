package ie.gmit.dip;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import static ie.gmit.dip.Strings.*;

public final class UserPrompts {
    private static final BufferedReader KB = new BufferedReader(new InputStreamReader(System.in));

    public synchronized static int userInt(int min, int max, String message) {
        int result = 0;
        boolean done = false;
        while (!done) {
            System.out.print(message);
            try {
                int i = Integer.parseInt(KB.readLine());
                if (i >= min && i <= max) {
                    done = true;
                    result = i;
                } else throw new Exception();
            } catch (Exception e) {
                ERR_INVALID_INPUT.println();
            }
        }
        return result;
    }

    public synchronized static String userString(int minLength, int maxLength, String message) {
        String result = null;
        while (result == null) {
            System.out.print(message);
            try {
                String input;
                if (!(input = KB.readLine()).equals("") && input.length() >= minLength && input.length() <= maxLength) {
                    result = input;
                } else throw new Exception();
            } catch (Exception e) {
                ERR_INVALID_INPUT.println();
            }
        }
        return result;
    }

    public synchronized static boolean userBoolean(String question) {
        while (true) {
            try {
                System.out.print(question);
                String answer = KB.readLine().toLowerCase();
                if (answer.equals("n")) return false;
                if (answer.equals("y")) return true;
            } catch (IOException ignored) {
            }
        }
    }

    private UserPrompts() {}
}