package attilathehun.daycounter;
 
import android.appwidget.AppWidgetProvider;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.ComponentName;
import android.widget.RemoteViews;
 
import attilathehun.daycounter.Util;
import attilathehun.daycounter.Counter;
import attilathehun.daycounter.CounterManager;
 
/**
 * This class manages the behavior of our light launcher windget(s).
 */
public class WidgetLightProvider extends AppWidgetProvider implements CounterEventListener {
 
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        //Util.log("WidgetLightProvider.onUpdate()");
        final int N = appWidgetIds.length;
        for (int i = 0; i < N; i++) {
            int appWidgetId = appWidgetIds[i];
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }
 
    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            CounterManager.getInstance().unbindWidgetOfId(appWidgetId);
        }
    }
 
    @Override
    public void onEnabled(Context context) {
 
    }
 
    @Override
    public void onDisabled(Context context) {
 
    }
 
    @Override
    public void onCounterNotificationStateChanged(Counter counter) {
 
    }
 
    @Override
    public void onCounterRemoved(Counter counter) {
        if (!counter.hasWidget()) {
            return;
        }
        Context context = counter.withdraw();
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_light);
        Util.setContextIfNull(context.getApplicationContext());
        AppWidgetManager manager = AppWidgetManager.getInstance(context.getApplicationContext());
        views.setTextViewText(R.id.name_indicator, context.getResources().getString(R.string.counter_removed));
        views.setTextViewText(R.id.days_indicator, String.format(context.getResources().getString(R.string.days_left), "N/A"));
        manager.updateAppWidget(counter.getWidgetId(), views);
 }
 
    /**
     * Updates the text on the widget's TextView to match the current day count
     *
     * @param context a context for emergency purposes
     * @ appWindgetManager target widget manager
     * @ appWidgetId target widget id
     */
    void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_light);
        Counter counter = CounterManager.getInstance().getWidgetCounterForId(appWidgetId);
        // When you remove the counter but not the widget
        if (counter == null) {
            views.setTextViewText(R.id.name_indicator, context.getResources().getString(R.string.counter_removed));
            views.setTextViewText(R.id.days_indicator, String.format(context.getResources().getString(R.string.days_left), "N/A"));
           appWidgetManager.updateAppWidget(appWidgetId, views);
            return;
        }
        String daysRemaining = Util.getDaysRemaining(counter, context);
        views.setTextViewText(R.id.days_indicator, daysRemaining);
        views.setTextViewText(R.id.name_indicator, counter.getName());
        // Tell the widget manager
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
 
    /**
     * Manually refreshes the light widgets, for the cases when you delete a counter, its widget should no longer display the data.
     */
    public static void refresh(Context context) {
        int[] ids = AppWidgetManager.getInstance(context.getApplicationContext()).getAppWidgetIds(new ComponentName(context.getApplicationContext(), WidgetLightProvider.class));
        //Util.log("Widget ids: " + Arrays.toString(ids));
        WidgetLightProvider myWidget = new WidgetLightProvider();
        myWidget.onUpdate(context, AppWidgetManager.getInstance(context),ids);
    }
    
    public static void registerListener() {
        Counter.addEventListener(new WidgetLightProvider());
    }
 
}