package attilathehun.daycounter;

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
import attilathehun.daycounter.DateChangedListener;
import attilathehun.daycounter.ServiceLauncher;

/**
* This class governs the notification service
*/
public class NotificationService extends Service implements DateChangedListener{
	
     private static boolean isRunning = false;
     private static boolean isRegistred = false;
     private static boolean isListening = false;
	   private static final String CHANNEL_ID = "days_reminder";
	   private static final int NOTIFICATION_ID = 1;
	   final static String[] ACTIONS = { Intent.ACTION_DATE_CHANGED };

	   @Override
	   public void onCreate() {
		   super.onCreate();
		   Util.log("NotificationService created");
		   if(NotificationService.isRunning()) {
			   Util.log("NotificationService self-stopped");
			   stopSelf();
			   return;
		   }
		   NotificationService.setRunning(true);
	   }
	    
		
	   @Override
	   public void onDestroy() {
		   super.onDestroy();
		   Util.log("NotificationService destroyed");
		   NotificationService.setRunning(false);
		   NotificationService.setRegistred(false);
		   NotificationService.setListening(false);
		   this.stopForeground(true);
	   } 
	   
	   @Override
	   public IBinder onBind(Intent intent) {
		  throw new RuntimeException("Read the docs, idiot");
	   }
	   
	   @Override
	   public int onStartCommand (Intent intent, int flags, int startId){
		    Util.log("onStartCommand()");
  			registerReceiver();
  			registerListener();
	  		Util.setContextIfNull(this);
		    NotificationService.createNotificationChannel(this);
	        //this.setForegroundServiceBehavior(Notification.FOREGROUND_SERVICE_IMMEDIATE);
		    this.startForeground(NOTIFICATION_ID, createNotification());
			return super.onStartCommand(intent, flags, startId);
	   }
	   
	   @Override
	   public void onDateChanged() {
		   Util.log("NotificationService.onDateChanged()");
		   updateNotification();
	   }
	   
	   private static void setRunning(boolean state) {
		   NotificationService.isRunning = state;
	   }
	   
	   public static boolean isRunning() {
		   return NotificationService.isRunning;
	   }
	   
	   private Notification createNotification() {
		   Util.log("createNotification()");
		   int targetAge = Util.getData()[3];
		   int daysLeft = Counter.getDaysRemaining();
		   String title = "Time is running";
		   String text = "You have " + daysLeft + " days left!";
		   
		   Intent intent = new Intent(this, MainActivity.class);
           intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
           PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
		   
		   Notification.Builder builder = new Notification.Builder(this, CHANNEL_ID)
		      .setSmallIcon(R.drawable.icon)
              .setContentTitle(title)
              .setContentText(text)
              .setPriority(Notification.PRIORITY_MIN)
			  .setContentIntent(pendingIntent)
              .setAutoCancel(false)
			  .setCategory(Notification.CATEGORY_REMINDER)
			  .setVisibility(Notification.VISIBILITY_PUBLIC);
			  
			  return builder.build();
	   }
	   
	   private void updateNotification() {
		   Util.log("updateNotification()");
		   NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
           notificationManager.notify(NOTIFICATION_ID, createNotification());
	   }
	   
	   public static void createNotificationChannel(Context context) {
		   Util.log("createNotificationChannel()");
          // Create the NotificationChannel, but only on API 26+ because
          // the NotificationChannel class is new and not in the support library
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
              CharSequence name = "Days left Reminder";
              String description = "Displays the number of days left.";
              int importance = NotificationManager.IMPORTANCE_MIN;
              NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
              channel.setDescription(description);
              // Register the channel with the system; you can't change the importance
              // or other notification behaviors after this
              NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
              notificationManager.createNotificationChannel(channel);
          }
       }
	   
	   public static void createNotificationChannel() {
		   NotificationService.createNotificationChannel(Util.getContext());
	   }
	   
	   
	   private void registerReceiver() {
	      if(NotificationService.isRegistred()) {
	          Util.log("Prevented creating another receiver");
	          return;
	      }
	      NotificationService.setRegistred(true);
		   Util.log("registerReceiver()");
		   IntentFilter intentFilter = new IntentFilter(); 
		   for(String action : NotificationService.ACTIONS) {
			   Util.log("registred " + action);
			   intentFilter.addAction(action);
		   }  
		   this.registerReceiver(new ServiceLauncher(), intentFilter);
	   }
		  
		  public static boolean isRegistred() {
		      return NotificationService.isRegistred;
		  }
		  
		  private static void setRegistred(boolean state) {
		      NotificationService.isRegistred = state;
		  }
		  
		  private void registerListener() {
		      if(NotificationService.isListening()) {
		          Util.log("Prevented creating another listener");
		          return;
		      }
		      NotificationService.setListening(true);
		      Util.log("registerListener()");
		     ServiceLauncher.addListener(this);
		  }
		  
		  private static boolean isListening() {
		      return NotificationService.isListening;
		  }
		  
		  private static void setListening(boolean state) {
		      NotificationService.isListening = state;
		  }
		  
}