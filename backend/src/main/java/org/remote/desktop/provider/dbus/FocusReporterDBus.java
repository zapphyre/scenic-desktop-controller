package org.remote.desktop.provider.dbus;

import org.freedesktop.dbus.annotations.DBusInterfaceName;
import org.freedesktop.dbus.exceptions.DBusException;
import org.freedesktop.dbus.interfaces.DBusInterface;
import org.freedesktop.dbus.messages.DBusSignal;
import org.freedesktop.dbus.types.UInt32;

@DBusInterfaceName("org.zapphyre.Gpad.Controller")
public interface FocusReporterDBus extends DBusInterface {

    // Optional: method to query current focused window
    String[] GetCurrent();  // returns [title, wm_class, wm_class_instance, pid_as_string]

    // Signal emitted on every focus change
    public class FocusChanged extends DBusSignal {
        public final String title;
        public final String wmClass;
        public final String wmClassInstance;
        public final int pid;

        public FocusChanged(String path, String title, String wmClass,
                            String wmClassInstance, UInt32 pid) throws DBusException {
            super(path, title, wmClass, wmClassInstance, pid);
            this.title = title;
            this.wmClass = wmClass;
            this.wmClassInstance = wmClassInstance;
            this.pid = pid.intValue();  // or pid.getValue().intValue()
        }
    }
}