package attilathehun.daycounter;

import android.appwidget.AppWidgetProvider;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.widget.RemoteViews;

import attilathehun.daycounter.Util;
import attilathehun.daycounter.Counter;

public class WidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
          Util.log("WidgetProvider.onUpdate()");
           final int N = appWidgetIds.length;
           for (int i=0; i<N; i++) {
                int appWidgetId = appWidgetIds[i]; 
                updateAppWidget(context, appWidgetManager, appWidgetId); 
           }
     }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        
    }



    @Override
    public void onEnabled(Context context) {
        
        
    }
    
    
    @Override
    public void onDisabled(Context context) {
        
    }
    
     void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
         RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
         Util.setContextIfNull(context);
				int daysRemaining = Counter.getDaysRemaining();
         views.setTextViewText(R.id.days_indicator, "You have " + daysRemaining + " days left!"); 
         // Tell the widget manager
          appWidgetManager.updateAppWidget(appWidgetId, views);
    }

}