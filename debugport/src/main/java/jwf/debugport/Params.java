package jwf.debugport;

import android.os.Parcel;
import android.os.Parcelable;

/**
 *
 */
public class Params implements Parcelable {
    public static final int DEFAULT_PORT = 8562;
    private int mPort;

    public Params() {
        mPort = DEFAULT_PORT;
    }

    public Params setPort(int port) {
        mPort = port;
        return this;
    }

    public int getPort() {
        return mPort;
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mPort);
    }

    public static final Creator<Params> CREATOR = new Creator<Params>() {
        @Override
        public Params createFromParcel(Parcel source) {
            Params p = new Params();
            p.setPort(source.readInt());
            return p;
        }

        @Override
        public Params[] newArray(int size) {
            return new Params[size];
        }
    };
}
