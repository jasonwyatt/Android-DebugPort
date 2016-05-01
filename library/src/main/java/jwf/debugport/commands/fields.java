package jwf.debugport.commands;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import bsh.CallStack;
import bsh.Interpreter;
import jwf.debugport.annotations.Command;
import jwf.debugport.commands.descriptors.FieldDescriptor;

/**
 *
 */
@Command
public class fields {
    @Command.Help("List all of the fields available for a particular object.")
    public static void invoke(Interpreter interpreter, CallStack callStack, Object obj) {
        invoke(interpreter, callStack, obj.getClass());
    }

    @Command.Help("List all of the fields available for a particular class.")
    public static void invoke(Interpreter interpreter, CallStack callStack, Class klass) {
        if (klass == null) {
            interpreter.println("value is null");
            return;
        }
        FieldDescriptor[] fields = FieldDescriptor.fromFields(klass.getFields());
        StringBuilder sb = new StringBuilder();

        sb.append("fields {\n");

        for (int i = 0; i < fields.length; i++) {
            sb.append(CommandUtils.indent(1));
            sb.append(fields[i].toString());
            sb.append("\n");
        }

        sb.append("}");

        interpreter.println(sb.toString());
    }
}
