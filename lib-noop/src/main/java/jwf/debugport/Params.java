package jwf.debugport;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Duplicate of Params class for no-op production usage.
 */
public class Params implements Parcelable {
    public static final int DEFAULT_ANDROID_PORT = -1;
    public static final int DEFAULT_SQLITE_PORT = -1;
    private int mDebugPort;
    private int mSQLitePort;
    private String[] mStartupCommands;

    public Params() {
        mDebugPort = DEFAULT_ANDROID_PORT;
        mSQLitePort = DEFAULT_SQLITE_PORT;
        mStartupCommands = new String[0];
    }

    @Deprecated
    public Params setPort(int port) {
        return this;
    }

    @Deprecated
    public int getPort() {
        return mDebugPort;
    }

    public Params setDebugPort(int port) {
        return this;
    }

    public int getDebugPort() {
        return mDebugPort;
    }

    public Params setSQLitePort(int port) {
        return this;
    }

    public int getSQLitePort() {
        return mSQLitePort;
    }

    public Params setStartupCommands(String[] commands) {
        return this;
    }

    public String[] getStartupCommands() {
        return mStartupCommands;
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mDebugPort);
        dest.writeInt(mSQLitePort);
        dest.writeInt(mStartupCommands.length);
        dest.writeStringArray(mStartupCommands);
    }

    @Override
    public String toString() {
        return "No-Op Params<>";
    }

    public static final Creator<Params> CREATOR = new Creator<Params>() {
        @Override
        public Params createFromParcel(Parcel source) {
            Params p = new Params();
            p.setDebugPort(source.readInt());
            p.setSQLitePort(source.readInt());

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
