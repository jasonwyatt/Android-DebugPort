package jwf.debugport.internal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.io.PrintWriter;
import java.util.Locale;

/**
 *
 */
public class Utils {
    public static final int INDENT_SPACES = 2;

    @SuppressLint("DefaultLocale")
    public static String getIpAddress(Context context) {
        WifiManager wifiMan = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInf = wifiMan.getConnectionInfo();
        int ipAddress = wifiInf.getIpAddress();
        return String.format("%d.%d.%d.%d", (ipAddress & 0xff),(ipAddress >> 8 & 0xff),(ipAddress >> 16 & 0xff),(ipAddress >> 24 & 0xff));
    }

    public static void printTable(PrintWriter out, String[] headers, Object[][] data) {
        int[] longest = new int[headers.length];
        for (int column = 0; column < headers.length; column++) {
            longest[column] = headers[column].length();
        }
        for (int row = 0; row < data.length; row++) {
            for (int column = 0; column < data[row].length; column++) {
                String dataStr = data[row][column].toString();
                if (dataStr.length() > longest[column]) {
                    longest[column] = dataStr.length();
                }
            }
        }

        int gutter = 1;

        printTableDivider(out, longest, gutter);
        printTableRow(out, longest, gutter, headers);
        printTableDivider(out, longest, gutter);
        for (int row = 0; row < data.length; row++) {
            printTableRow(out, longest, gutter, data[row]);
        }
        printTableDivider(out, longest, gutter);
    }

    private static void printTableRow(PrintWriter out, int[] columnWidths, int columnGutterSize, Object[] data) {
        out.print("|");
        for (int i = 0; i < data.length; i++) {
            out.print(multStr(" ", columnGutterSize));
            String dataStr = data[i].toString();
            out.print(dataStr);
            out.print(multStr(" ", columnGutterSize + (columnWidths[i] - dataStr.length())));
            out.print("|");
        }
        out.println();
    }

    private static void printTableDivider(PrintWriter out, int[] columnWidths, int columnGutterSize) {
        out.print("+");
        for (int i = 0; i < columnWidths.length; i++) {
            out.print(multStr("-", columnWidths[i] + columnGutterSize * 2));
            out.print("+");
        }
        out.println();
    }

    public static String multStr(String s, int times) {
        StringBuilder sb = new StringBuilder(s.length() * times);
        for (int i = 0; i < times; i++) {
            sb.append(s);
        }
        return sb.toString();
    }

    public static void printTable(PrintWriter out, Cursor c) {
        int columns = c.getColumnCount();
        if (columns == 0) {
            return;
        }
        String[] columnNames = c.getColumnNames();
        int gutter = 1;
        int[] longest = new int[columns];
        for (int column = 0; column < columns; column++) {
            longest[column] = columnNames[column].length();
        }
        int row = 0;
        while (c.moveToNext()) {
            for (int column = 0; column < columns; column++) {
                String dataStr = c.isNull(column) ? "null" : c.getString(column);
                if (dataStr.length() > longest[column]) {
                    longest[column] = dataStr.length();
                }
            }
        }

        printTableDivider(out, longest, gutter);
        printTableRow(out, longest, gutter, columnNames);
        printTableDivider(out, longest, gutter);

        // let's rewind to actually print the contents.
        boolean hasFirst = c.moveToFirst();


        if (hasFirst) {
            do {
                String[] data = new String[columns];
                for (int i = 0; i < columns; i++) {
                    data[i] = c.isNull(i) ? "NULL" : c.getString(i);
                }
                printTableRow(out, longest, gutter, data);
            } while (c.moveToNext());
        }

        printTableDivider(out, longest, gutter);
    }

    public static String nanosToMillis(long nanos) {
        float millis = nanos / 1000000.0f;
        return String.format(Locale.getDefault(), "%.3fms", millis);
    }

    public static String spaces(int spaces) {
        return multStr(" ", spaces);
    }

    public static String indent(int indentations) {
        return spaces(indentations * INDENT_SPACES);
    }
}
