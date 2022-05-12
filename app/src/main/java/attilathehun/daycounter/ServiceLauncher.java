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


/**
* This class is making sure the app starts on boot and the calendar refreshes 
* when necessary
*/
public class ServiceLauncher extends BroadcastReceiver {
        
		private static List<DateChangedListener> listeners = new ArrayList<DateChangedListener>();
		
		@Override
		public void onReceive(Context context, Intent intent) {
			switch (intent.getAction()) {
				case "android.intent.action.BOOT_COMPLETED" :
			         Util.log("BOOT_COMPLETED received");
			         Util.startService(context);
					 break;
			    case "android.intent.action.DATE_CHANGED" :
				     Util.log("DATE_CHANGED received");
				     Counter.refresh();
					 notifyDateChanged();
				     break;
			    default:
				Util.log("received " + intent.getAction() + " for some reason");
			}
			
		}
		
		/**
		* Notifies all implementors of DateChangedListener that the date has changed
		*/
		private void notifyDateChanged() {
			Util.log("notifyDateChanged()");
			for(DateChangedListener listener : ServiceLauncher.listeners) {
				listener.onDateChanged();		
			}
	    }	
		
		/**
		* Adds an article to the list of DateChangedListener implementors
		* @param listener the listener to catalog
		*/
	    public static void addListener(DateChangedListener listener) {
	        Util.log("addListener()");
            ServiceLauncher.listeners.add(listener);
        }
		
}