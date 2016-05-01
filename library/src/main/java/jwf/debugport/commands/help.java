package jwf.debugport.commands;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.StringTokenizer;

import bsh.CallStack;
import bsh.Interpreter;
import jwf.debugport.annotations.Command;

/**
 *
 */
@Command
public class help {
    private static final ArrayList<CommandHelpInfo> sCommandHelp = new ArrayList<>();
    private static final int GAP_SPACES = 2;
    private static final int MAX_WIDTH = 80;

    @Command.Help("Show this help message.")
    public static void invoke(Interpreter interpreter, CallStack callStack) {
        int nameStartCol = 2;
        int maxLengthName = 0;
        for (CommandHelpInfo info : sCommandHelp) {
            int maxSigLen = info.getMaxSignatureLength();
            if (maxSigLen > maxLengthName) {
                maxLengthName = maxSigLen;
            }
        }
        int helpStartCol = nameStartCol + maxLengthName + GAP_SPACES;

        StringBuilder builder = new StringBuilder();
        builder.append("Available Commands:");
        for (CommandHelpInfo info : sCommandHelp) {
            builder.append("\n");
            info.appendHelpInfo(builder, nameStartCol, helpStartCol, MAX_WIDTH);
        }
        builder.append("\n");

        interpreter.println(builder.toString());
    }

    public static void registerCommand(Class command) {
        if (!command.isAnnotationPresent(Command.class)) {
            return;
        }
        sCommandHelp.add(new CommandHelpInfo(command));
        Collections.sort(sCommandHelp);
    }

    public static class CommandHelpInfo implements Comparable<CommandHelpInfo> {
        private final Class mCommandClass;

        public CommandHelpInfo(Class commandClass) {
            mCommandClass = commandClass;
        }

        public int getMaxSignatureLength() {
            Method[] methods = mCommandClass.getMethods();
            int maxLength = 0;
            for (Method method : methods) {
                if (!"invoke".equals(method.getName())) {
                    continue;
                }

                Class<?>[] params = method.getParameterTypes();
                StringBuilder signatureBuilder = new StringBuilder();
                signatureBuilder.append(mCommandClass.getSimpleName());
                signatureBuilder.append("(");
                for (int j = 2; j < params.length; j++) {
                    // first two params are Interpreter and CallStack, ignore those.
                    if (j != 2) {
                        signatureBuilder.append(", ");
                    }
                    signatureBuilder.append(params[j].getSimpleName());
                }
                signatureBuilder.append(")");
                if (signatureBuilder.length() > maxLength) {
                    maxLength = signatureBuilder.length();
                }
            }
            return maxLength;
        }

        private String getSignature(Method method) {
            Class<?>[] params = method.getParameterTypes();
            StringBuilder signatureBuilder = new StringBuilder();
            signatureBuilder.append(mCommandClass.getSimpleName());
            signatureBuilder.append("(");
            for (int j = 2; j < params.length; j++) {
                // first two params are Interpreter and CallStack, ignore those.
                if (j != 2) {
                    signatureBuilder.append(", ");
                }
                signatureBuilder.append(params[j].getSimpleName());
            }
            signatureBuilder.append(")");
            return signatureBuilder.toString();
        }

        private void appendHelpInfo(StringBuilder target, int nameStartCol, int helpStartCol, int maxCols) {
            Method[] methods = mCommandClass.getMethods();
            boolean isFirst = true;
            for (Method method : methods) {
                if (!method.getName().equals("invoke")) {
                    continue;
                }

                // separate each item with a new line.
                if (!isFirst) {
                    target.append("\n");
                }
                isFirst = false;

                int cols = nameStartCol;
                target.append(CommandUtils.spaces(nameStartCol));

                String signature = getSignature(method);
                target.append(signature);
                cols += signature.length();

                if (method.isAnnotationPresent(Command.Help.class)) {
                    int spaces = helpStartCol - cols;
                    target.append(CommandUtils.spaces(spaces));
                    cols += spaces;

                    String helpText = method.getAnnotation(Command.Help.class).value();
                    StringTokenizer tokens = new StringTokenizer(helpText, " ");
                    while (tokens.hasMoreTokens()) {
                        String token = tokens.nextToken();
                        target.append(token);
                        if (tokens.hasMoreTokens()) {
                            target.append(" ");
                        }
                        cols += token.length() + 1;
                        if (cols >= maxCols && tokens.hasMoreTokens()) {
                            cols = helpStartCol;
                            target.append("\n");
                            target.append(CommandUtils.spaces(helpStartCol));
                        }
                    }
                }
            }
        }

        @SuppressWarnings("NullableProblems")
        @Override
        public int compareTo(CommandHelpInfo another) {
            if (another == null) {
                return -1;
            }
            return mCommandClass.getSimpleName().compareTo(another.mCommandClass.getSimpleName());
        }

        public Class getCommandClass() {
            return mCommandClass;
        }
    }
}
