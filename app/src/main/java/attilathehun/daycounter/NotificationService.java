package attilathehun.daycounter;

import java.util.ArrayList;

import android.net.Uri;

import android.app.Service;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Context;
import android.app.PendingIntent;
import android.provider.Settings;
import android.os.IBinder;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import attilathehun.daycounter.Util;
import attilathehun.daycounter.Counter;
import attilathehun.daycounter.CounterManager;
import attilathehun.daycounter.DateChangedListener;
import attilathehun.daycounter.CounterEventListener;
import attilathehun.daycounter.ServiceLauncher;

/**
 * This class governs the notification service.
 */
public class NotificationService extends Service implements DateChangedListener, CounterEventListener {

    private static boolean isRunning = false;
    private static boolean isRegistred = false;
    private static boolean isListening = false;
    private static final String CHANNEL_ID = "days_reminder";
    private static final int SERVICE_NOTIFICATION_ID = 69;
    private static int SERVICE_NOTIFICATION_COUNTER_ID = -1;

    /**
     * Service#onCreate() override, gets called every time a Context#startService() is called!
     */
    @Override
    // Gets called before onStartCommand()
    public void onCreate() {
        super.onCreate();
    }

    public void cleanup() {
        Util.log("Service cleanup");
        NotificationService.setRunning(false);
        NotificationService.setRegistred(false);
        NotificationService.setListening(false);
        Util.clearContextIfEquals(this);
        NotificationManagerCompat.from(this).cancelAll();
        stopForeground(true);
        stopSelf();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.cleanup();
        Util.log("Notification service destroyed");
    }

    /**
     * A compulsory method for Services we do not use.
     *
     * @param intent the intent
     * @return RuntimeException
     */
    @Override
    public IBinder onBind(Intent intent) {
        throw new RuntimeException("Read the docs, bruh");
    }

    /**
     * Executes when the service actually starts. Performs the necessary setup and creates the notifications.
     *
     * @param intent  the starting intent
     * @param flags   metadata flags
     * @param startId startId
     * @return execution status
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (NotificationService.isRunning()) {
            return super.onStartCommand(intent, flags, startId);
        }
        this.registerReceiver();
        this.registerListener();
        Util.setContextIfNull(getApplicationContext());
        this.createNotificationChannel();
        NotificationManagerCompat.from(this).cancelAll();
        this.createNotifications();
        NotificationService.setRunning(true);
        Util.log("Notification service started");
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * Should be called when the date changes to update the notifications. DateChangedListener#onDateChanged().
     */
    @Override
    public void onDateChanged() {
        this.refreshNotifications();
    }

    /**
     * Should be called when the notification status of a counter has been changed. Creates or removes the notification accordingly.
     *
     * @param counter said counter represantation, NOT reference!
     */
    @Override
    public void onCounterNotificationStateChanged(Counter counter) {
        Util.log("NotificationService.onCounterNotificationStateChanged()");
        if (counter.hasNotification()) {
            // This one gets handled when the service is instantiated
            if (Integer.parseInt(counter.getId()) == SERVICE_NOTIFICATION_COUNTER_ID) {
                return;
            }
            this.createNotification(counter);
        } else {
            if (Integer.parseInt(counter.getId()) == SERVICE_NOTIFICATION_COUNTER_ID) {
                this.ropeIsTheWayPleaseHelpMe();
            } else {
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
                Util.log("Notification cancelled id - " + counter.getId());
                notificationManager.cancel(Integer.parseInt(counter.getId()));
            }
        }
    }

    /**
     * Removes notification from the target counter.
     *
     * @param counter said counter represantation, NOT reference!
     */
    @Override
    public void onCounterRemoved(Counter counter) {
        counter.removeNotification();
        //onCounterNotificationStateChanged(counter);
    }

    private static void setRunning(boolean state) {
        NotificationService.isRunning = state;
    }

    public static boolean isRunning() {
        return NotificationService.isRunning;
    }

    /**
     * Creates a Notification object for the target counter.
     *
     * @param counter target counter
     * @return corresponding Notification object
     */
    private Notification buildNotification(Counter counter) {
        String title;
        if (counter.getName().equals("N/A")) {
            title = this.getResources().getString(R.string.default_notification_name);
        } else {
            title = counter.getName();
        }
        String text = Util.getDaysRemaining(counter, this);
        Uri sound = Uri.parse("android.resource://" + this.getPackageName() + "/" + R.raw.blank);
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        Notification.Builder builder = new Notification.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.icon)
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(Notification.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(false)
                .setOngoing(true)
                .setCategory(Notification.CATEGORY_REMINDER)
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .setSound(sound);
        //   .setForegroundServiceBehavior(FOREGROUND_SERVICE_IMMEDIATE);

        return builder.build();
    }

    /**
     * Registers a NotificationChannel to the system, which is necessary for the approval of our notifications.
     */
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = this.getResources().getString(R.string.notification_channel_name);
            String description = this.getResources().getString(R.string.notification_channel_description);
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.enableVibration(false);
            channel.enableLights(false);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = (NotificationManager) this.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    /**
     * Registers ServiceLauncher as a BroadcastReceiver to the system, allowing it to receive broadcasts as long as this service is running.
     * This is crucial for date refreshing.
     */
    private void registerReceiver() {
        if (NotificationService.isRegistred()) {
            Util.log("Prevented creating another receiver");
            return;
        }
        IntentFilter intentFilter = new IntentFilter();
        for (String action : ServiceLauncher.ACTIONS) {
            intentFilter.addAction(action);
        }
        this.registerReceiver(new ServiceLauncher(), intentFilter);
        NotificationService.setRegistred(true);
    }

    public static boolean isRegistred() {
        return NotificationService.isRegistred;
    }

    private static void setRegistred(boolean state) {
        NotificationService.isRegistred = state;
        if (state) {
            Util.log("Receiver registered.");
        } else {
            Util.log("Receiver unregistered.");
        }
    }

    /**
     * Registers <i>this</i> as a DateChangedListener and CounterEventListener to the appropriate classes. Necessary for event interception.
     */
    private void registerListener() {
        if (NotificationService.isListening()) {
            Util.log("Prevented creating another listener");
            return;
        }
        NotificationService.setListening(true);
        ServiceLauncher.addListener(this);
        Counter.addEventListener(this);
    }

    private static boolean isListening() {
        return NotificationService.isListening;
    }

    private static void setListening(boolean state) {
        NotificationService.isListening = state;
    }

    /**
     * Creates a notification for the target counter.
     *
     * @param counter counter representation, NOT reference!
     */
    private void createNotification(Counter counter) {
        Util.log("Notification created id - " + counter.getId());
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(Integer.parseInt(counter.getId()), buildNotification(counter));
    }

    /**
     * Creates the actual notifications.
     */
    public void createNotifications() {
        boolean serviceNotificationDone = false;
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        ArrayList<Counter> counters = CounterManager.getInstance().getNotificationCounters();

        for (Counter counter : counters) {
            if (!serviceNotificationDone) {
                Util.log("Notification created id - " + SERVICE_NOTIFICATION_ID + " (" + counter.getId() + ")");
                NotificationService.setServiceNotificationCounterId(Integer.parseInt(counter.getId()));
                startForeground(SERVICE_NOTIFICATION_ID, buildNotification(counter));
                serviceNotificationDone = true;
            } else {
                Util.log("Notification created id - " + counter.getId());
                notificationManager.notify(Integer.parseInt(counter.getId()), buildNotification(counter));
            }
        }
    }

    /**
     * Update the content of all running notifications.
     */
    public void refreshNotifications() {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        ArrayList<Counter> counters = CounterManager.getInstance().getNotificationCounters();
        int id;
        for (Counter counter : counters) {
            id = Integer.parseInt(counter.getId());
            if (id == SERVICE_NOTIFICATION_COUNTER_ID) {
                notificationManager.notify(SERVICE_NOTIFICATION_ID, buildNotification(counter));
            } else {
                notificationManager.notify(Integer.parseInt(counter.getId()), buildNotification(counter));
            }
        }
    }

    /**
     * A serious name would be removeNotification(), but compared to this method the influence of social media
     * on one's psyche is a joke.
     * This method removes a notification, but needs to keep in mind that one notification is mandatory for a foreground service.
     */
    public void ropeIsTheWayPleaseHelpMe() {
        Util.log("At the hangman's tree");
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        ArrayList<Counter> counters = CounterManager.getInstance().getNotificationCounters();
        if (counters.size() == 0) {
            Util.log("Hangman out");
            this.cleanup();
            return;
        } else if (counters.size() == 1) {
            notificationManager.cancelAll();
            NotificationService.setServiceNotificationCounterId(Integer.parseInt(counters.get(0).getId()));
            Util.log("Notification hanged id - " + SERVICE_NOTIFICATION_ID + " (" + counters.get(0).getId() + ")");
            notificationManager.notify(SERVICE_NOTIFICATION_ID, buildNotification(counters.get(0)));
        } else {
            Counter counter = counters.get(0);
            Util.log("Notification rehanged id - " + SERVICE_NOTIFICATION_ID + " (" + counter.getId() + ")");
            notificationManager.cancel(Integer.parseInt(counter.getId()));
            NotificationService.setServiceNotificationCounterId(Integer.parseInt(counter.getId()));
            notificationManager.notify(SERVICE_NOTIFICATION_ID, buildNotification(counter));
        }
    }

    private static void setServiceNotificationCounterId(int id) {
        NotificationService.SERVICE_NOTIFICATION_COUNTER_ID = id;
    }

}