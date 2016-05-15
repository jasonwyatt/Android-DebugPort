package jwf.debugport.internal.sqlite.commands;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Pattern;

import jwf.debugport.annotations.Command;
import jwf.debugport.internal.CommandHelpInfo;
import jwf.debugport.internal.Utils;
import jwf.debugport.internal.sqlite.SQLiteClientConnection;

/**
 * Created by jason on 5/15/16.
 */
@Command.Help(
        value = "Show this help message.",
        format = "help;"
)
public class HelpCommand extends SQLiteCommand {
    private static final Pattern command = Pattern.compile("^help$", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
    private static final int MAX_WIDTH = 100;
    private static final int PREAMBLE_START_COL = 2;
    private static final int NAME_START_COL = 8;
    private static final int HELP_START_COL = 12;
    private static final int GROUP_START_COL = 4;

    @Override
    public boolean isCommandString(String candidate) {
        return command.matcher(candidate).matches();
    }

    @Override
    public void execute(SQLiteClientConnection connection, PrintWriter out, String commandString) {
        SQLiteCommand[] commands = Commands.getInstance().getCommands();
        ArrayList<CommandHelpInfo> helpInfo = new ArrayList<>();
        for (SQLiteCommand cmd : commands) {
            if (cmd.getClass().isAnnotationPresent(Command.Help.class)) {
                helpInfo.add(new CommandHelpInfo(cmd.getClass()));
            }
        }
        Collections.sort(helpInfo);

        StringBuilder builder = new StringBuilder();
        builder.append("Help:\n");
        CommandHelpInfo.appendHelpText(
                builder,
                "As you'd expect, you can execute any valid SQLite statements against the database " +
                        "to which you're currently connected (see: `USE [database name];` below).",
                PREAMBLE_START_COL,
                MAX_WIDTH);
        builder.append("\n\n");
        CommandHelpInfo.appendHelpText(
                builder,
                "In addition to regular SQLite commands, Android DebugPort provides additional " +
                        "functionality via several additional commands.",
                PREAMBLE_START_COL,
                MAX_WIDTH);
        builder.append("\n\n");
        CommandHelpInfo.appendHelpText(
                builder,
                "Available non-SQLite commands (case insensitive):",
                PREAMBLE_START_COL,
                MAX_WIDTH);
        String lastCommandGroup = "__not_a_group__";
        for (CommandHelpInfo info : helpInfo) {
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
            info.appendHelpInfoForClass(builder, NAME_START_COL, HELP_START_COL, MAX_WIDTH);
        }
        builder.append("\n");

        out.print(builder.toString());
        out.flush();
    }
}
