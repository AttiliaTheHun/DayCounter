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
    final static String[] ACTIONS = {
            Intent.ACTION_DATE_CHANGED,
            Intent.ACTION_TIMEZONE_CHANGED
    };
 
    /**
     * Service#onCreate() override, gets called every time a Context#startService() is called!
     */
    @Override
    // Gets called before onStartCommand()
    public void onCreate() {
        super.onCreate();
    }
 
 
    @Override
    public void onDestroy() {
        super.onDestroy();
        NotificationService.setRunning(false);
        NotificationService.setRegistred(false);
        NotificationService.setListening(false);
        stopSelf();
    }
 
    /**
     * A compulsory method for Services we do not use.
     * @param intent the intent
     * @return RuntimeException
     */
    @Override
    public IBinder onBind(Intent intent) {
        throw new RuntimeException("Read the docs, bruh");
    }
 
    /**
     * Executes when the service actually starts. Permorms the necessary setup and creates the notifications.
     * @param intent  the starting intent
     * @param flags   metadata flags
     * @param startId startId
     * @return execution status
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        registerReceiver();
        registerListener();
        Util.setContextIfNull(this);
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
        this.updateNotifications();
    }
 
    /**
     * Should be called when the notification status of a counter has been changed. Creates or removes the notification accordingly.
     * @param counter said counter represantation, NOT reference!
     */
    @Override
    public void onCounterNotificationStateChanged(Counter counter) {
        //  Util.log("NotificationService.onCounterNotificationStateChanged()");
        if (counter.hasNotification()) {
            this.createNotification(counter);
        } else {
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.cancel(Integer.parseInt(counter.getId()));
        }
    }
 
    /**
     * Removes notification for the target counter.
     * @param counter said counter represantation, NOT reference!
     */
    @Override
    public void onCounterRemoved(Counter counter) {
        counter.removeNotification();
        onCounterNotificationStateChanged(counter);
    }
 
    private static void setRunning(boolean state) {
        NotificationService.isRunning = state;
    }
 
    public static boolean isRunning() {
        return NotificationService.isRunning;
    }
 
    /**
     * Creates a Notification object for the target counter.
     * @param counter target counter
     * @return corresponding Notification object
     */
    private Notification buildNotification(Counter counter) {
        String title;
        if (counter.getName().equals("N/A")) {
            title = Util.getContext().getResources().getString(R.string.default_notification_name);
        } else {
            title = counter.getName();
        }
        String text = Util.getDaysRemaining(counter);
        Uri sound = Uri.parse("android.resource://" + Util.getContext().getPackageName() + "/" + R.raw.blank);
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        Notification.Builder builder = new Notification.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.icon)
                .setContentTitle(title)
                .setContentText(text)
                //.setPriority(Notification.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(false)
                .setOngoing(true)
                .setCategory(Notification.CATEGORY_REMINDER)
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .setSound(sound);
 
        return builder.build();
    }
 
	/**
	 * Updates all notifications, recreates is more precise.
	 */
    private void updateNotifications() {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        ArrayList<Counter> counters = CounterManager.getInstance().getNotificationCounters();
        for (Counter counter : counters) {
            notificationManager.notify(Integer.parseInt(counter.getId()), buildNotification(counter));
        }
    }
 
	/**
	 * Registers a NotificationChannel to the system, which is necessary for ouw notifications to be approved.
	 */
    public static void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = Util.getContext().getResources().getString(R.string.notification_channel_name);
            String description = Util.getContext().getResources().getString(R.string.notification_channel_description);
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.enableVibration(false);
            channel.enableLights(false);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = (NotificationManager) Util.getContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
 
 
	/**
	 * Registers ServiceLauncher as a BroadcastReceiver to the system, allowing it to receive broadcasts as long as this service is running.
	 * This is crucial for date refreshing.
	 */
    private void registerReceiver() {
        if (NotificationService.isRegistred()) {
            //     Util.log("Prevented creating another receiver");
            return;
        }
        IntentFilter intentFilter = new IntentFilter();
        for (String action : NotificationService.ACTIONS) {
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
    }
 
	/**
	 * Registers <i>this</i> as a DateChangedListener and CounterEventListener to the appropriate classes. Necessary for event receiving.
	 */
    private void registerListener() {
        if (NotificationService.isListening()) {
            //  Util.log("Prevented creating another listener");
            return;
        }
        NotificationService.setListening(true);
        ServiceLauncher.addListener(this);
        MainActivity.addListener(this);
    }
 
    private static boolean isListening() {
        return NotificationService.isListening;
    }
 
    private static void setListening(boolean state) {
        NotificationService.isListening = state;
    }
 
	/**
	 * Creates a notification for every counter with positive notification status.
	 */
    private void createNotifications() {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        ArrayList<Counter> counters = CounterManager.getInstance().getNotificationCounters();
        for (Counter counter : counters) {
            notificationManager.notify(Integer.parseInt(counter.getId()), buildNotification(counter));
        }
    }
 
	/**
	 * Creates a notification for the target counter.
	 * @param counter counter representation, NOT reference!
	 */
    private void createNotification(Counter counter) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(Integer.parseInt(counter.getId()), buildNotification(counter));
    }
 
 
    public void refresh() {
        updateNotifications();
    }
 
}