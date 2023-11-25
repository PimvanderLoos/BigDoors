package nl.pim16aap2.bigDoors;

import nl.pim16aap2.bigDoors.util.ILoggableDoor;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Level;

public interface ILogger
{
    static void logMessage(Level level, String pluginName, String message)
    {
        Bukkit.getLogger().log(level, "[" + pluginName + "] " + message);
    }

    void logMessage(Level level, String msg);

    // Log a message to the log file. Can print to console and/or
    // add some new lines before the message in the logfile to make it stand out.
    void logMessage(String msg, boolean printToConsole, boolean startSkip, Level level);

    // Log a message to the log file. Can print to console and/or
    // add some new lines before the message in the logfile to make it stand out.
    void logMessage(String msg, boolean printToConsole, boolean startSkip);

    // Log a message to the logfile. Does not print to console or add newlines in
    // front of the actual message.
    void logMessageToLogFile(String msg);

    void logMessageToLogFileForDoor(@Nullable ILoggableDoor door, String message);

    void logMessageToLogFileForDoor(@Nullable ILoggableDoor door, @Nullable Throwable throwable, String message);

    void logMessageToConsole(String msg);

    void logMessageToConsoleOnly(String msg);

    void debug(String str);

    void info(String str);

    void warn(String str);

    void severe(String str);

    void log(Throwable throwable);

    void log(String message, Throwable throwable);
}
