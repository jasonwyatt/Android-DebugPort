package jwf.debugport.internal.debug.commands;

import bsh.CallStack;
import bsh.Interpreter;
import jwf.debugport.annotations.Command;
import jwf.debugport.internal.debug.commands.descriptors.MethodDescriptor;

/**
 *
 */
@Command(group = Command.GROUP_METHOD_INSPECTION)
public class methods {
    @Command.Help("Get the available methods for the provided object.")
    public static void invoke(Interpreter interpreter, CallStack callStack, @Command.ParamName("obj") Object obj) {
        invoke(interpreter, callStack, obj.getClass());
    }

    @Command.Help("Get the available methods for the provided class.")
    public static void invoke(Interpreter interpreter, CallStack callStack, @Command.ParamName("class") Class klass) {
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
