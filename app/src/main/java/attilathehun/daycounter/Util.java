package attilathehun.daycounter;

import java.io.IOException;
import java.io.File;
import java.io.FileWriter;

import android.content.Context;
import android.content.Intent;
import androidx.core.content.FileProvider;

import android.net.Uri;
import android.app.Activity;

import android.os.Build;

import attilathehun.daycounter.FileUtil;
import attilathehun.daycounter.ServiceLauncher;
import attilathehun.daycounter.NotificationService;
import attilathehun.daycounter.WidgetProvider;
import attilathehun.daycounter.WidgetLightProvider;

/**
 * A collection of methods that are crucial for other classes yet belong in none of them.
 */
public class Util {

    public static final boolean DEBUG = true;
    private static Context context = null;

    /**
     * @return stringified path of the log file
     */
    private static String getLogPath() {
        return FileUtil.getExternalStorageDir() + "/DayCounterLog.txt";
    }

    /**
     * Logs a message inside the log file
     */
    public static void log(String message) {
        if (DEBUG) {
            appendFile(getLogPath(), message + "\n");
        }
    }

    /**
     * Empties the log file.
     */
    public static void clearLog() {
        if (DEBUG) {
            FileUtil.writeFile(Util.getLogPath(), "");
        }
    }

    /**
     * Attemps to open the log file in some kind of file viewing app the user has installed,
     * crashes on my friend's phone though.
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
     * A version of Util#viewLog(context) that uses the default context.
     */
    public static void viewLog() {
        Util.viewLog(Util.getContext());
    }

    /**
     * Creates a new file on the specified path.
     * I think I stole this from Sketchware's FileUtil.java.
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
     * Sets the default context, allowing the use of contextless methods.
     * @param context the context object to use as default
     */
    public static void setContext(Context context) {
        Util.context = context;
    }

    /**
     * A version of Util#setContext(context) to use when expecting to have a default context set.
     */
    public static void setContextIfNull(Context context) {
        if (Util.getContext() == null) {
            Util.setContext(context);
        }
    }

    /**
     * Returns the default context.
     * @return default context
     */
    public static Context getContext() {
        return Util.context;
    }


    /**
     * Starts the notification service, if there is any notification to be displayed.
     * @param context a context to use
     */
    public static void startService(Context context) {
        if (CounterManager.getInstance().getNotificationCounters().size() == 0) {
            // Util.log("No notification counters found");
            return;
        }

        final Intent intent = new Intent(context, NotificationService.class);
        NotificationService.createNotificationChannel();
        // Start the service in a new thread so it does not block the UI
        new Thread(new Runnable() {
            @Override
            public void run() {
                getContext().startService(intent);
            }
        }).start();

    }

    /**
     * A contextless version of Util#startService(context) which uses the default context
     */
    public static void startService() {
        Util.startService(Util.getContext());
    }

    /**
     * It is bad enough to have on service, no need for more.
     */
    public static void startServiceIfNotRunning() {
        if (!NotificationService.isRunning()) {
            startService();
        }
    }

    /**
     * A wrapper over Counter#getDaysRemaining() that works with translations.
     */
    public static String getDaysRemaining(Counter counter) {
        String daysLeft = String.format("%,d\n", counter.getDaysRemaining());
        String output = getContext().getResources().getString(R.string.days_left);
        return String.format(output, daysLeft);
    }

    /**
     * Delete as soon as the multicounter version is built.
     */
    public static String getDaysRemaining() {
        return getDaysRemaining(CounterManager.getInstance().getCounters().get(0));
    }

    /*
     * We will want this, soon.
     * @param activity any activity
     * @param languageCode remove and hardcode english!
     * @return
     */
    /*
    public static void forceEnglish(Activity activity, String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Resources resources = activity.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }*/

    public static int random(int min, int max) {
        return (int) Math.floor((Math.random()) * (max - min + 1) + min);
    }

    /**
     * The same way notifications need to be refreshed, this method refreshes the homescreen widgets.
     */
    public static void refreshWidgets() {
        WidgetProvider.refresh();
        WidgetLightProvider.refresh();
    }

}