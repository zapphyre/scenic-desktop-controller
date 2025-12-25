package org.remote.desktop.provider.dbus;

import org.freedesktop.dbus.Struct;
import org.freedesktop.dbus.annotations.DBusInterfaceName;
import org.freedesktop.dbus.annotations.Position;
import org.freedesktop.dbus.exceptions.DBusException;
import org.freedesktop.dbus.interfaces.DBusInterface;
import org.freedesktop.dbus.messages.DBusSignal;
import org.freedesktop.dbus.types.UInt32;

@DBusInterfaceName("org.zapphyre.Gpad.Controller")
public interface FocusReporterDBus extends DBusInterface {

    FocusedWindowInfo GetCurrent();

    public class FocusedWindowInfo extends Struct {

        @Position(0) public final String title;
        @Position(1) public final String wmClass;
        @Position(2) public final String wmClassInstance;
        @Position(3) public final UInt32 pid;

        public FocusedWindowInfo(
                String title,
                String wmClass,
                String wmClassInstance,
                UInt32 pid
        ) {
            this.title = title;
            this.wmClass = wmClass;
            this.wmClassInstance = wmClassInstance;
            this.pid = pid;
        }
    }


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