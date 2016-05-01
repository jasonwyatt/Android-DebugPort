package jwf.debugport.commands;

import bsh.CallStack;
import bsh.Interpreter;
import jwf.debugport.annotations.Command;
import jwf.debugport.commands.descriptors.FieldDescriptor;

/**
 *
 */
@Command
public class fieldsLocal {
    @Command.Help("List all of the fields defined locally for an object.")
    public static void invoke(Interpreter interpreter, CallStack callStack, Object obj) {
        invoke(interpreter, callStack, obj.getClass());
    }

    @Command.Help("List all of the fields defined locally for a particular class.")
    public static void invoke(Interpreter interpreter, CallStack callStack, Class klass) {
        if (klass == null) {
            interpreter.println("null");
            return;
        }
        FieldDescriptor[] fields = FieldDescriptor.fromFields(klass.getDeclaredFields());
        StringBuilder sb = new StringBuilder();

        sb.append("declared fields {\n");

        for (int i = 0; i < fields.length; i++) {
            sb.append(CommandUtils.indent(1));
            sb.append(fields[i].toString());
            sb.append("\n");
        }

        sb.append("}");

        interpreter.println(sb.toString());
    }
}
