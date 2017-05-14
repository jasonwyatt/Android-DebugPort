package jwf.debugport.internal.debug.commands;

import bsh.CallStack;
import bsh.EvalError;
import bsh.Interpreter;
import jwf.debugport.annotations.Command;

/**
 *
 */
@Command
public class exit {
    @Command.Help("Exit this interpreter.")
    public static void invoke(Interpreter env, CallStack callStack) {
        env.println("Thanks for using Android DebugPort!");
        try {
            ((Commands)env.get("cmd")).exit();
        } catch (EvalError evalError) {
            env.error(evalError.toString());
        }
    }
}
