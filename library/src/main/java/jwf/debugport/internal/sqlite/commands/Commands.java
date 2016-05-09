package jwf.debugport.internal.sqlite.commands;

/**
 *
 */
public final class Commands {
    private static Commands sInstance;
    private final SQLiteCommand[] mAvailableCommands;

    private Commands() {
        mAvailableCommands = new SQLiteCommand[] {
                new ExitCommand(),
                new ShowDatabasesCommand(),
                new CreateDatabaseCommand(),
                new DropDatabaseCommand(),
                new UseCommand(),
                new ShowTablesCommand(),
                new ShowCreateTableCommand(),
        };
    }

    public SQLiteCommand getCommand(String input) {
        for (SQLiteCommand cmd : mAvailableCommands) {
            if (cmd.isCommandString(input)) {
                return cmd;
            }
        }
        return null;
    }

    public static final Commands getInstance() {
        if (sInstance == null) {
            sInstance = new Commands();
        }
        return sInstance;
    }
}
