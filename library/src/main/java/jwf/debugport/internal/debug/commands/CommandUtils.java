package jwf.debugport.internal.debug.commands;

/**
 *
 */
public class CommandUtils {
    public static final int INDENT_SPACES = 2;

    public static String spaces(int spaces) {
        StringBuilder builder = new StringBuilder(spaces);
        for (int i = 0; i < spaces; i++) {
            builder.append(" ");
        }
        return builder.toString();
    }

    public static String indent(int indentations) {
        return spaces(indentations * INDENT_SPACES);
    }
}
