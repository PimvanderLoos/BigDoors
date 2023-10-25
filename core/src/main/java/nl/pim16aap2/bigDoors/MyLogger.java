package nl.pim16aap2.bigDoors;

import nl.pim16aap2.bigDoors.util.ConfigLoader;
import nl.pim16aap2.bigDoors.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;

public class MyLogger
{
    private final BigDoors plugin;
    private final File logFile;

    public MyLogger(BigDoors plugin, File logFile)
    {
        this.plugin = plugin;
        this.logFile = logFile;
        loadLog();
    }

    // Initialise log
    public void loadLog()
    {
        if (!logFile.exists())
            try
            {
                logFile.getParentFile().mkdirs();
                logFile.createNewFile();
                myLogger(Level.INFO, "New file created at " + logFile);
            }
            catch (IOException e)
            {
                myLogger(Level.SEVERE, "File write error: " + logFile);
                e.printStackTrace();
            }
    }

    // Print a string to the console.
    public void myLogger(Level level, String str)
    {
        Bukkit.getLogger().log(level, "[" + plugin.getName() + "] " + str);
    }

    // Send a message to whomever issued a command.
    public void returnToSender(CommandSender sender, Level level, ChatColor color, String str)
    {
        if (sender instanceof Player)
            Util.messagePlayer((Player) sender, color + str);
        else
            myLogger(level, ChatColor.stripColor(str));
    }

    public void logMessage(Level level, String msg)
    {
        logMessage(msg, true, false, level);
    }

    // Log a message to the log file. Can print to console and/or
    // add some new lines before the message in the logfile to make it stand out.
    public void logMessage(String msg, boolean printToConsole, boolean startSkip, final Level level)
    {
        if (printToConsole)
            myLogger(level, msg);

        // Don't write stuff to the logfile if this is disabled in the config.
        if (plugin.getConfigLoader() != null && !plugin.getConfigLoader().enableFileLogging())
            return;

        msg = String.format("[%d/%s] %s", Thread.currentThread().getId(), Thread.currentThread().getName(), msg);

        try
        {
            BufferedWriter bw = new BufferedWriter(new FileWriter(logFile, true));
            Date now = new Date();
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            if (startSkip)
                bw.write("\n\n[" + format.format(now) + "] " + msg);
            else
                bw.write("[" + format.format(now) + "] " + msg);
            bw.newLine();
            bw.flush();
            bw.close();
        }
        catch (IOException e)
        {
            myLogger(Level.SEVERE, "Logging error! Could not log to logFile!");
            e.printStackTrace();
        }
    }

    // Log a message to the log file. Can print to console and/or
    // add some new lines before the message in the logfile to make it stand out.
    public void logMessage(String msg, boolean printToConsole, boolean startSkip)
    {
        logMessage(msg, printToConsole, startSkip, Level.WARNING);
    }

    // Log a message to the logfile. Does not print to console or add newlines in
    // front of the actual message.
    public void logMessageToLogFile(String msg)
    {
        logMessage(msg, false, false);
    }

    public void logMessageToConsole(String msg)
    {
        logMessage(msg, true, false);
    }

    public void logMessageToConsoleOnly(String msg)
    {
        myLogger(Level.INFO, msg);
    }

    public void debug(String str)
    {
        if (ConfigLoader.DEBUG)
            // Log at INFO level because lower levels are filtered by Spigot.
            logMessage(Level.INFO, str);
    }

    public void info(String str)
    {
        logMessage(Level.INFO, str);
    }

    public void warn(String str)
    {
        myLogger(Level.WARNING, str);
        logMessage(str, false, false);
    }

    public void severe(String str)
    {
        myLogger(Level.SEVERE, str);
        logMessage(str, false, false);
    }

    public static void logMessage(Level level, String pluginName, String message)
    {
        Bukkit.getLogger().log(level, "[" + pluginName + "] " + message);
    }

    public void log(Throwable throwable)
    {
        severe(Util.throwableToString(throwable));
    }

    public void log(String message, Throwable throwable)
    {
        severe(message + "\n" + Util.throwableToString(throwable));
    }
}
