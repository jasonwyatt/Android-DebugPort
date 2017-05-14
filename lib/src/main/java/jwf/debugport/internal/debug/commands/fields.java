package jwf.debugport.internal.debug.commands;

import bsh.CallStack;
import bsh.Interpreter;
import jwf.debugport.annotations.Command;
import jwf.debugport.internal.Utils;
import jwf.debugport.internal.debug.commands.descriptors.FieldDescriptor;

/**
 *
 */
@Command(group = Command.GROUP_FIELD_INSPECTION)
public class fields {
    @Command.Help("List all of the fields available for a particular object.")
    public static void invoke(Interpreter interpreter, CallStack callStack, @Command.ParamName("obj") Object obj) {
        invoke(interpreter, callStack, obj.getClass());
    }

    @Command.Help("List all of the fields available for a particular class.")
    public static void invoke(Interpreter interpreter, CallStack callStack, @Command.ParamName("class") Class klass) {
        if (klass == null) {
            interpreter.println("value is null");
            return;
        }
        FieldDescriptor[] fields = FieldDescriptor.fromFields(klass.getFields());
        StringBuilder sb = new StringBuilder();

        sb.append("fields {\n");

        for (FieldDescriptor field : fields) {
            sb.append(Utils.indent(1));
            sb.append(field.toString());
            sb.append("\n");
        }

        sb.append("}");

        interpreter.println(sb.toString());
    }
}
