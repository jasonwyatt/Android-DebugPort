package jwf.debugport.commands;

import java.lang.reflect.Method;

import bsh.CallStack;
import bsh.Interpreter;
import jwf.debugport.annotations.Command;

/**
 *
 */
@Command
public class methods {
    @Command.Help("Get the available methods for the provided object.")
    public static void invoke(Interpreter interpreter, CallStack callStack, Object obj) {
        invoke(interpreter, callStack, obj.getClass());
    }

    @Command.Help("Get the available methods for the provided class.")
    public static void invoke(Interpreter interpreter, CallStack callStack, Class klass) {
        Method[] methods = klass.getMethods();
        StringBuilder sb = new StringBuilder();

        sb.append("available methods: {\n");
        for (Method method : methods) {
            sb.append(CommandUtils.indent(1));
            sb.append(method.getName());
            sb.append("(");

            Class<?>[] params = method.getParameterTypes();
            for (int j = 0; j < params.length; j++) {
                if (j != 0) {
                    sb.append(", ");
                }
                sb.append(params[j].getSimpleName());
            }

            sb.append(")\n");
        }
        sb.append("}");

        interpreter.println(sb.toString());
    }

}
