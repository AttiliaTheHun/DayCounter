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
public class WidgetLightProvider extends AppWidgetProvider {

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

    /**
     * Updates the text on the widget's TextView to match the current day count
     * @param context a context for emergency purposes
     * @ appWindgetManager target widget manager
     * @ appWidgetId target widget id
     */
    void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_light);
        Util.setContextIfNull(context);
        Counter counter = CounterManager.getInstance().getWidgetCounterForId(appWidgetId);
        // When you remove the counter but not the widget
        if (counter == null) {
            Util.log("counter is null");
            return;
        }
        String daysRemaining = Util.getDaysRemaining(counter);
        views.setTextViewText(R.id.days_indicator, daysRemaining);
        views.setTextViewText(R.id.name_indicator, counter.getName());
        // Tell the widget manager
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    /**
     * Manually refreshes the light widgets, for the cases when you delete a counter, its widget should no longer display the data.
     */
    public static void refresh() {
        Intent intent = new Intent(Util.getContext(), WidgetLightProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        // Use an array and EXTRA_APPWIDGET_IDS instead of AppWidgetManager.EXTRA_APPWIDGET_ID,
        // since it seems the onUpdate() is only fired on that:
        int[] ids = AppWidgetManager.getInstance(Util.getContext()).getAppWidgetIds(new ComponentName(Util.getContext(), WidgetLightProvider.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        Util.getContext().sendBroadcast(intent);

        /* last resort
        int[] ids = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), MyWidget.class));
        MyWidget myWidget = new MyWidget();
        myWidget.onUpdate(this, AppWidgetManager.getInstance(this),ids);
        */

    }

}