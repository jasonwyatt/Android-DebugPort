package jwf.debugport.internal.debug.commands;

import android.text.TextUtils;
import android.util.Log;

import java.lang.annotation.Annotation;
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
    private static final int MAX_WIDTH = 100;
    private static final int NAME_START_COL = 6;
    private static final int HELP_START_COL = 10;
    private static final int GROUP_START_COL = 2;

    @Command.Help("Show this help message.")
    public static void invoke(Interpreter interpreter, CallStack callStack) {
        try {
            int nameStartCol = NAME_START_COL;
            int maxLengthName = 0;
            for (CommandHelpInfo info : sCommandHelp) {
                int maxSigLen = info.getMaxSignatureLength();
                if (maxSigLen > maxLengthName) {
                    maxLengthName = maxSigLen;
                }
            }
            int helpStartCol = HELP_START_COL;

            StringBuilder builder = new StringBuilder();
            builder.append("Available Commands:");
            String lastCommandGroup = "__not_a_group__";
            for (CommandHelpInfo info : sCommandHelp) {
                builder.append("\n");
                String commandGroup = info.getCommandGroup();
                if (!commandGroup.equalsIgnoreCase(lastCommandGroup)) {
                    if (!lastCommandGroup.equals("__not_a_group__")) {
                        builder.append("\n");
                    }
                    builder.append(CommandUtils.spaces(GROUP_START_COL))
                            .append(commandGroup)
                            .append(":")
                            .append("\n");
                    lastCommandGroup = commandGroup;
                }
                info.appendHelpInfo(builder, nameStartCol, helpStartCol, MAX_WIDTH);
            }
            builder.append("\n");

            interpreter.println(builder.toString());
        } catch (Exception e) {
            Log.e("OOPS", "hmm", e);
        }
    }

    public static void registerCommand(Class command) {
        if (!command.isAnnotationPresent(Command.class)) {
            return;
        }
        sCommandHelp.add(new CommandHelpInfo(command, (Command) command.getAnnotation(Command.class)));
        Collections.sort(sCommandHelp);
    }

    public static class CommandHelpInfo implements Comparable<CommandHelpInfo> {
        private final Class mCommandClass;
        private final Command mAnnotation;

        public CommandHelpInfo(Class commandClass, Command annotation) {
            mCommandClass = commandClass;
            mAnnotation = annotation;
        }

        public int getMaxSignatureLength() {
            Method[] methods = mCommandClass.getMethods();
            int maxLength = 0;
            for (Method method : methods) {
                if (!"invoke".equals(method.getName()) || !method.isAnnotationPresent(Command.Help.class)) {
                    continue;
                }

                StringBuilder signatureBuilder = new StringBuilder();
                signatureBuilder.append(getSignature(method));
                if (signatureBuilder.length() > maxLength) {
                    maxLength = signatureBuilder.length();
                }
            }
            return maxLength;
        }

        public String getCommandGroup() {
            return mAnnotation.group();
        }

        private String getSignature(Method method) {
            Annotation[][] paramAnnotations = method.getParameterAnnotations();
            Class<?>[] params = method.getParameterTypes();
            StringBuilder signatureBuilder = new StringBuilder();
            signatureBuilder.append(mCommandClass.getSimpleName());
            signatureBuilder.append("(");
            for (int j = 2; j < params.length; j++) {
                // first two params are Interpreter and CallStack, ignore those.
                if (j != 2) {
                    signatureBuilder.append(", ");
                }

                String name = params[j].getSimpleName();
                if (j == params.length-1 && method.isVarArgs() && params[j].isArray()) {
                    signatureBuilder.append(name.replace("[]", ""));
                    signatureBuilder.append("...");
                } else {
                    signatureBuilder.append(name);
                }

                Annotation[] annotations = paramAnnotations[j];
                Command.ParamName paramAnnotation = null;
                for (Annotation a : annotations) {
                    if (a instanceof Command.ParamName) {
                        paramAnnotation = (Command.ParamName) a;
                        break;
                    }
                }
                if (paramAnnotation != null) {
                    if (!TextUtils.isEmpty(paramAnnotation.value())) {
                        signatureBuilder.append(" ");
                        signatureBuilder.append(paramAnnotation.value());
                    }
                }
            }
            signatureBuilder.append(")");
            return signatureBuilder.toString();
        }

        private void appendHelpInfo(StringBuilder target, int nameStartCol, int helpStartCol, int maxCols) {
            Method[] methods = mCommandClass.getMethods();
            boolean isFirst = true;
            for (Method method : methods) {
                if (!method.getName().equals("invoke") || !method.isAnnotationPresent(Command.Help.class)) {
                    continue;
                }

                // separate each item with a new line.
                if (!isFirst) {
                    target.append("\n");
                }
                isFirst = false;

                target.append(CommandUtils.spaces(nameStartCol));
                String signature = getSignature(method);
                target.append(signature);
                target.append("\n");

                int cols = helpStartCol;
                target.append(CommandUtils.spaces(helpStartCol));

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

        @SuppressWarnings("NullableProblems")
        @Override
        public int compareTo(CommandHelpInfo another) {
            if (another == null) {
                return -1;
            }
            int groupCompare = mAnnotation.group().compareTo(another.mAnnotation.group());
            if (groupCompare != 0) {
                return groupCompare;
            }
            return mCommandClass.getSimpleName().compareTo(another.mCommandClass.getSimpleName());
        }

        public Class getCommandClass() {
            return mCommandClass;
        }
    }
}
