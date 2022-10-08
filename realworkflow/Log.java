//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package realworkflow;

import java.io.IOException;
import java.io.OutputStream;

public class Log {
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private static OutputStream output;
    private static boolean disabled;

    public Log() {
    }

    public static void print(String message) {
        if (!isDisabled()) {
            try {
                getOutput().write(message.getBytes());
            } catch (IOException var2) {
                var2.printStackTrace();
            }
        }

    }

    public static void print(Object message) {
        if (!isDisabled()) {
            print(String.valueOf(message));
        }

    }

    public static void printLine(String message) {
        if (!isDisabled()) {
            print(message + LINE_SEPARATOR);
        }

    }

    public static void printLine() {
        if (!isDisabled()) {
            print(LINE_SEPARATOR);
        }

    }

    public static void printLine(Object message) {
        if (!isDisabled()) {
            printLine(String.valueOf(message));
        }

    }

    public static void format(String format, Object... args) {
        if (!isDisabled()) {
            print(String.format(format, args));
        }

    }

    public static void formatLine(String format, Object... args) {
        if (!isDisabled()) {
            printLine(String.format(format, args));
        }

    }

    public static void setOutput(OutputStream _output) {
        output = _output;
    }

    public static OutputStream getOutput() {
        if (output == null) {
            setOutput(System.out);
        }

        return output;
    }

    public static void setDisabled(boolean _disabled) {
        disabled = _disabled;
    }

    public static boolean isDisabled() {
        return disabled;
    }

    public static void disable() {
        setDisabled(true);
    }

    public static void enable() {
        setDisabled(false);
    }
}
