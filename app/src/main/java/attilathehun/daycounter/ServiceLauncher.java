package attilathehun.daycounter;
 
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.Context;
import android.os.Build;
 
import java.util.List;
import java.util.ArrayList;
 
import attilathehun.daycounter.Util;
import attilathehun.daycounter.DateChangedListener;
import attilathehun.daycounter.Counter;
import attilathehun.daycounter.WidgetProvider;
import attilathehun.daycounter.LocaleChangedListener;
 
/**
 * This class makes sure the app starts on boot and the calendar refreshes when necessary.
 */
public class ServiceLauncher extends BroadcastReceiver {
 
    private static List<DateChangedListener> dateChangedListeners = new ArrayList<DateChangedListener>();
    private static List<LocaleChangedListener> localeChangedListeners = new ArrayList<LocaleChangedListener>();
 
    public static final String[] ACTIONS = {
            Intent.ACTION_DATE_CHANGED,
            Intent.ACTION_TIMEZONE_CHANGED,
            Intent.ACTION_LOCALE_CHANGED
    };
 
    /**
     * Gets called whenever a registered broadcast is received. When received, deals out the corresponding action depending on the broadcast.
     *
     * @param context context
     * @param intent  intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        Util.setContextIfNull(context.getApplicationContext());
        Util.log(intent.getAction() + " received");
        switch (intent.getAction()) {
            case "android.intent.action.BOOT_COMPLETED":
                Util.startService(context);
                Util.registerProviders();
                break;
            case "android.intent.action.DATE_CHANGED":
            case "android.intent.action.TIMEZONE_CHANGED":
                Counter.refresh();
                notifyDateChanged(context.getApplicationContext());
                break;
            case "android.intent.action.LOCALE_CHANGED":
                notifyLocaleChanged(context);
                break;
            default:
                Util.log("received " + intent.getAction() + " for some reason");
        }
        Util.clearContextIfEquals(context);
    }
 
    /**
     * Notifies all implementors of DateChangedListener that the date has changed.
     */
    private void notifyDateChanged(Context context) {
        for (DateChangedListener listener : ServiceLauncher.dateChangedListeners) {
            listener.onDateChanged(context);
        }
    }
 
    /**
     * Notifies all implementors of LocaleChangedListener that the system locale has changed.
     */
    private void notifyLocaleChanged(Context context) {
        for (LocaleChangedListener listener : ServiceLauncher.localeChangedListeners) {
            listener.onLocaleChanged(context);
        }
    }
 
    /**
     * Adds an article to the list of DateChangedListener implementors.
     *
     * @param listener the listener to catalog
     */
    public static void addDateChangedListener(DateChangedListener listener) {
        ServiceLauncher.dateChangedListeners.add(listener);
    }
 
    /**
     * Adds an article to the list of LocaleChangedListener implementors.
     *
     * @param listener the listener to catalog
     */
    public static void addLocaleChangedListener(LocaleChangedListener listener) {
        ServiceLauncher.localeChangedListeners.add(listener);
    }
}
 