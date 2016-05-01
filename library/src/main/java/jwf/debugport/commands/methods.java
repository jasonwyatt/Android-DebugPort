package jwf.debugport.commands;

import bsh.CallStack;
import bsh.Interpreter;
import jwf.debugport.annotations.Command;
import jwf.debugport.commands.descriptors.MethodDescriptor;

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
        if (klass == null) {
            interpreter.println("value is null");
            return;
        }
        MethodDescriptor[] methods = MethodDescriptor.fromMethods(klass.getMethods());
        StringBuilder sb = new StringBuilder();

        sb.append("available methods: {\n");
        for (MethodDescriptor method : methods) {
            sb.append(CommandUtils.indent(1));
            sb.append(method.toString());
            sb.append("\n");
        }
        sb.append("}");

        interpreter.println(sb.toString());
    }

}
