package jwf.debugport.internal.debug.commands;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;

import bsh.CallStack;
import bsh.Interpreter;
import jwf.debugport.annotations.Command;
import jwf.debugport.internal.CommandHelpInfo;
import jwf.debugport.internal.Utils;

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
                    builder.append(Utils.spaces(GROUP_START_COL))
                            .append(commandGroup)
                            .append(":")
                            .append("\n");
                    lastCommandGroup = commandGroup;
                }
                info.appendHelpInfoForMethods(builder, NAME_START_COL, HELP_START_COL, MAX_WIDTH);
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
        sCommandHelp.add(new CommandHelpInfo(command));
        Collections.sort(sCommandHelp);
    }

}
