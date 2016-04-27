package jwf.debugport;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Configuration parameters for the {@link DebugPortService}
 */
public class Params implements Parcelable {
    public static final int DEFAULT_PORT = 8562;
    private String[] mStartupCommands;
    private int mPort;

    public Params() {
        mPort = DEFAULT_PORT;
        mStartupCommands = new String[0];
    }

    /**
     * Set the port on which the {@link jwf.debugport.internal.TelnetServer} should be made available.
     */
    public Params setPort(int port) {
        mPort = port;
        return this;
    }

    /**
     * Get the port on which the {@link jwf.debugport.internal.TelnetServer} will be made available.
     */
    public int getPort() {
        return mPort;
    }

    /**
     * Set an array of commands which should be executed on the interpreter before the telnet client
     * is given control.  This can be useful if you need to run a bunch of <code>import</code>
     * statements, or configure some state.
     */
    public Params setStartupCommands(String[] commands) {
        mStartupCommands = commands;
        return this;
    }

    /**
     * Get the startup commands which will be executed on the interpreter before each telnet client
     * is given control.
     */
    public String[] getStartupCommands() {
        return mStartupCommands;
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mPort);
        dest.writeInt(mStartupCommands.length);
        dest.writeStringArray(mStartupCommands);
    }

    public static final Creator<Params> CREATOR = new Creator<Params>() {
        @Override
        public Params createFromParcel(Parcel source) {
            Params p = new Params();
            p.setPort(source.readInt());

            int startupCommandsLength = source.readInt();
            String[] startupCommands = new String[startupCommandsLength];
            source.readStringArray(startupCommands);
            p.setStartupCommands(startupCommands);
            return p;
        }

        @Override
        public Params[] newArray(int size) {
            return new Params[size];
        }
    };
}
