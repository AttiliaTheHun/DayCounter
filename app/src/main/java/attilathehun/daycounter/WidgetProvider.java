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
import attilathehun.daycounter.CounterEventListener;

/**
 * This class manages the behavior of our launcher windget(s).
 */
public class WidgetProvider extends AppWidgetProvider implements CounterEventListener {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        //Util.log("WidgetProvider.onUpdate()");
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
        //  or acquire AppWidgetManager and call updateAppWidget using counter.widgetId
        refresh(Util.getContext());
    }

    /**
     * Updates the text on the widget's TextView to match the current day count.
     * @param context a context for emergency purposes
     * @ appWindgetManager target widget manager
     * @ appWidgetId target widget id
     */
    void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
        Util.setContextIfNull(context.getApplicationContext());
        Counter counter = CounterManager.getInstance().getWidgetCounterForId(appWidgetId);
        // When you remove the counter but not the widget
        if (counter == null) {
            views.setTextViewText(R.id.name_indicator, context.getResources().getString(R.string.counter_removed));
            views.setTextViewText(R.id.days_indicator, String.format(context.getResources().getString(R.string.days_left), "N/A"));
            return;
        }
        String daysRemaining = Util.getDaysRemaining(counter, context);
        views.setTextViewText(R.id.days_indicator, daysRemaining);
        views.setTextViewText(R.id.name_indicator, counter.getName());
        // Tell the widget manager
        appWidgetManager.updateAppWidget(appWidgetId, views);
        Util.clearContextIfEquals(context);
    }

    /**
     * Manually refreshes the widgets, for the cases when you delete a counter, its widget should no longer display the data.
     */
    public static void refresh(Context context) {
        Intent intent = new Intent(context, WidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        // Use an array and EXTRA_APPWIDGET_IDS instead of AppWidgetManager.EXTRA_APPWIDGET_ID,
        // since it seems the onUpdate() is only fired on that:
        int[] ids = AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, WidgetProvider.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        Util.setContextIfNull(context.getApplicationContext());
        Util.log("Update broadcast sent");
        context.sendBroadcast(intent);
 
        /* last resort
         int[] ids = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), MyWidget.class));
        MyWidget myWidget = new MyWidget();
        myWidget.onUpdate(this, AppWidgetManager.getInstance(this),ids);
        */
    }

}