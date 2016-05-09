package jwf.debugport.internal.debug.commands;

import android.app.Application;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import bsh.CallStack;
import bsh.EvalError;
import bsh.Interpreter;
import jwf.debugport.annotations.Command;

/**
 *
 */
@Command
public class source {
    @Command.Help("Load and run a Beanshell script within your app's assets folder.")
    public static Object invoke(
            Interpreter interpreter,
            CallStack callStack,
            @Command.ParamName("scriptPath") String assetScriptPath) throws EvalError, IOException {
        Application app = (Application) interpreter.get("app");
        Reader scriptIn = new InputStreamReader(app.getAssets().open(assetScriptPath));
        Object result = interpreter.eval(scriptIn);
        scriptIn.close();
        return result;
    }
}
