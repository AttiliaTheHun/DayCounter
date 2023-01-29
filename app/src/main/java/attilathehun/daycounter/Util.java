package attilathehun.daycounter;

import java.io.IOException;
import java.io.File;
import java.io.FileWriter;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

import android.content.Context;
import android.content.Intent;
import androidx.core.content.FileProvider;

import android.net.Uri;
import android.app.Activity;

import android.os.Build;

import attilathehun.daycounter.Counter;
import attilathehun.daycounter.FileUtil;
import attilathehun.daycounter.ServiceLauncher;
import attilathehun.daycounter.NotificationService;
import attilathehun.daycounter.WidgetProvider;
import attilathehun.daycounter.WidgetLightProvider;

/**
 * A collection of handy methods to simplify tasks in other classes.
 */
public class Util {

    public static final boolean DEBUG = false;
    private static Context context = null;
    private static boolean PROVIDERS_REGISTERED = false;

    /**
     * @return stringified path of the log file
     */
    private static String getLogPath() {
        return FileUtil.getExternalStorageDir() + "/DayCounterLog.txt";
    }

    /**
     * Logs a String to the end of the log file. Works only in debug mode.
     */
    public static void log(String message) {
        if (DEBUG) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            appendFile(getLogPath(), "[" + dtf.format(now) + "] " + message + "\n");
        }
    }

    /**
     * Empties the log file. Works only in debug mode.
     */
    public static void clearLog() {
        if (DEBUG) {
            FileUtil.writeFile(Util.getLogPath(), "");
        }
    }

    /**
     * Attemps to open the log file in some kind of file viewing app the user has installed,
     * crashes on my friend's phone though.
     *
     * @param Context context to use for the action
     */
    public static void viewLog(Context context) {
        try {
            String authority = "attilathehun.daycounter.fileprovider";
            Intent intent = new Intent();
            File logFile = new File(Util.getLogPath());
            Uri uri = FileProvider.getUriForFile(context, authority, logFile);
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "text/plain");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(intent);
        } catch (Exception e) {
            Util.log(e.toString());
        }
    }

    /**
     * Creates a new file on the specified path.
     * I think I stole this from Sketchware's FileUtil.java.
     *
     * @param path path the create the file at
     */
    private static void createNewFile(String path) {
        int lastSep = path.lastIndexOf(File.separator);
        if (lastSep > 0) {
            String dirPath = path.substring(0, lastSep);
            FileUtil.makeDir(dirPath);
        }

        File file = new File(path);

        try {
            if (!file.exists())
                file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Attaches a String to the file's content.
     * I think I stole this from Sketchware' FileUtil.java.
     *
     * @param path path of the file
     * @str the text to attach
     */
    private static void appendFile(String path, String str) {
        createNewFile(path);
        FileWriter fileWriter = null;

        try {
            fileWriter = new FileWriter(new File(path), true);
            fileWriter.write(str);
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileWriter != null)
                    fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Sets the default context, allowing the use of contextless methods. The storing of context has always been problematical,
     * because it is the easiest memory leak to implement. It is discouraged to store Activites and Services, use getApplicationContext() instead.
     *
     * @param context the context object to use as default
     */
    private static void setContext(Context context) {
        Util.context = context.getApplicationContext();
    }

    /**
     * Returns the default context. Be sure to set the default context beforehand.
     *
     * @return default context
     */
    public static Context getContext() {
        if (Util.context == null) {
            throw new RuntimeException("The default context is null.");
        }
        return Util.context;
    }

    /**
     * Yeah, from now on we use this with respect for memory leaks, yeye.
     *
     * @param context the context
     */
    public static void setContextIfNull(Context context) {
        try {
            if (Util.context == null || !Class.forName("android.app.Application").isInstance(Util.context)) {
                Util.log("Context set");
                Util.setContext(context);
            }
            // This shouldn't happen under any circumstances
            // If this class is not found , the app shouldn't make it this far
        } catch (ClassNotFoundException e) {
            Util.setContext(context);
            Util.log(e.getMessage());
            Util.log("You broke the Universe...");
        }
    }


    /**
     * We can use this method to clear the default context if it is the same as the one we passed into it.
     *
     * @param context the context
     */
    public static void clearContextIfEquals(Context context) {
        if (Util.context != null && Util.context.equals(context)) {
            Util.context = null;
            Util.log("Context nulled");
        }
    }


    /**
     * Starts the notification service, if there is any notification to be displayed.
     *
     * @param context a context to use
     */
    public static void startService(final Context context) {
        if (CounterManager.getInstance().getNotificationCounters().size() == 0) {
            Util.log("No notification counters found");
            return;
        }

        final Intent intent = new Intent(context.getApplicationContext(), NotificationService.class);
        // Start the service in a new thread so it does not block the UI
        new Thread(new Runnable() {
            @Override
            public void run() {
                // It is bad enough to have one service, no need for more.
                if (!NotificationService.isRunning()) {
                    context.startForegroundService(intent);
                }
            }
        }).start();
    }

    /**
     * A wrapper over Counter#getDaysRemaining() that supports translations.
     */
    public static String getDaysRemaining(Counter counter, Context context) {
        String daysLeft = String.format("%,d\n", counter.getDaysRemaining());
        String output = context.getResources().getString(R.string.days_left);
        return String.format(output, daysLeft);
    }

    public static int random(int min, int max) {
        return (int) Math.floor((Math.random()) * (max - min + 1) + min);
    }

    /**
     * The same way notifications need to be refreshed, this method refreshes the homescreen widgets.
     */
    public static void refreshWidgets(Context context) {
        WidgetProvider.refresh(context);
        WidgetLightProvider.refresh(context);
    }

    public static String getString(int id, Context context) {
        return context.getResources().getString(id);
    }

    public static void stopService(Context context) {
        Intent intent = new Intent(context, NotificationService.class);
        intent.setAction("ACTION_STOP_FOREGROUND_SERVICE");
        context.stopService(intent);
    }

    public static void restartService(Context context) {
        Util.stopService(context);
        Util.startService(context);
    }

    public static void registerProviders() {
        if (Util.PROVIDERS_REGISTERED) {
            return;
        }
        WidgetProvider.registerListener();
        WidgetLightProvider.registerListener();
        Util.PROVIDERS_REGISTERED = false;
    }

}