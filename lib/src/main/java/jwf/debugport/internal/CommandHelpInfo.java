package jwf.debugport.internal;

import android.text.TextUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.StringTokenizer;

import jwf.debugport.annotations.Command;

/**
 * Created by jason on 5/15/16.
 */
public class CommandHelpInfo implements Comparable<CommandHelpInfo> {
    private final Class mCommandClass;
    private Command.Help mHelpAnnotation;
    private Command mCommandAnnotation;

    public CommandHelpInfo(Class commandClass) {
        mCommandClass = commandClass;
        if (commandClass.isAnnotationPresent(Command.class)) {
            mCommandAnnotation = (Command) commandClass.getAnnotation(Command.class);
        }
        if (commandClass.isAnnotationPresent(Command.Help.class)) {
            mHelpAnnotation = (Command.Help) commandClass.getAnnotation(Command.Help.class);
        }
    }

    public String getCommandGroup() {
        if (mHelpAnnotation != null && !TextUtils.isEmpty(mHelpAnnotation.group())) {
            return mHelpAnnotation.group();
        }
        if (mCommandAnnotation != null && !TextUtils.isEmpty(mCommandAnnotation.group())) {
            return mCommandAnnotation.group();
        }
        return Command.GROUP_OTHER;
    }

    public void appendHelpInfoForMethods(StringBuilder target, int nameStartCol, int helpStartCol, int maxCols) {
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

            target.append(Utils.spaces(nameStartCol));
            String signature = getSignature(method);
            target.append(signature);
            target.append("\n");

            appendHelpText(target, method.getAnnotation(Command.Help.class).value(), helpStartCol, maxCols);
        }
    }

    public void appendHelpInfoForClass(StringBuilder target, int nameStartCol, int helpStartCol, int maxCols) {
        target.append(Utils.spaces(nameStartCol));
        if (!TextUtils.isEmpty(mHelpAnnotation.format())) {
            target.append(mHelpAnnotation.format());
        } else {
            target.append(mCommandClass.getSimpleName());
        }
        target.append("\n");
        appendHelpText(target, mHelpAnnotation.value(), helpStartCol, maxCols);
    }

    String getSignature(Method method) {
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
            if (j == params.length - 1 && method.isVarArgs() && params[j].isArray()) {
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

    public static void appendHelpText(StringBuilder target, String helpText, int helpStartCol, int maxCols) {
        int cols = helpStartCol;
        target.append(Utils.spaces(helpStartCol));

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
                target.append(Utils.spaces(helpStartCol));
            }
        }
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public int compareTo(CommandHelpInfo another) {
        if (another == null) {
            return -1;
        }
        String myGroup = getCommandGroup();
        String theirGroup = another.getCommandGroup();
        int groupCompare = myGroup.compareTo(theirGroup);
        if (groupCompare != 0) {
            // "Other" group should always come last.
            if (myGroup.equals(Command.GROUP_OTHER)) {
                return 1;
            }
            if (theirGroup.equals(Command.GROUP_OTHER)) {
                return - 1;
            }
            return groupCompare;
        }
        return mCommandClass.getSimpleName().compareTo(another.mCommandClass.getSimpleName());
    }

    public Class getCommandClass() {
        return mCommandClass;
    }
}
