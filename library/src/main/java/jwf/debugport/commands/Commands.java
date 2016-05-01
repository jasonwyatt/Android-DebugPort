package jwf.debugport.commands;

import java.lang.ref.WeakReference;

/**
 *
 */
public final class Commands {
    private final WeakReference<ExitListener> mListener;

    static {
        // helper block to initialize all the commands... TODO: maybe a better way to do this?
        help.registerCommand(help.class);
        help.registerCommand(exit.class);
        help.registerCommand(methods.class);
        help.registerCommand(methodsLocal.class);
        help.registerCommand(fields.class);
        help.registerCommand(fieldsLocal.class);
    }

    public Commands(ExitListener listener) {
        mListener = new WeakReference<>(listener);
    }

    public void exit() {
        ExitListener listener = mListener.get();
        if (listener != null) {
            listener.onExit();
        }
    }

    public interface ExitListener {
        void onExit();
    }
}
