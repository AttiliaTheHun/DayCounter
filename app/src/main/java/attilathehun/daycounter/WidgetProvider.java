package attilathehun.daycounter;

import java.util.Arrays;

import android.appwidget.AppWidgetProvider;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.app.PendingIntent;
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
            //   Util.log("Default: " + Arrays.toString(appWidgetIds));
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
        AppWidgetManager manager = AppWidgetManager.getInstance(context.getApplicationContext());
        int[] ids = manager.getAppWidgetIds(new ComponentName(context.getApplicationContext(), WidgetProvider.class));
        boolean belongs = false;
        for (int id : ids) {
            if (id == counter.getWidgetId()) {
                belongs = true;
                break;
            }
        }
        if (!belongs) {
            return;
        }
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
        Util.setContextIfNull(context.getApplicationContext());
        views.setTextViewText(R.id.name_indicator, context.getResources().getString(R.string.counter_removed));
        views.setTextViewText(R.id.days_indicator, String.format(context.getResources().getString(R.string.days_left), "N/A"));
        manager.updateAppWidget(counter.getWidgetId(), views);
    }

    /**
     * Updates the text on the widget's TextView to match the current day count.
     *
     * @param context a context for emergency purposes
     * @ appWindgetManager target widget manager
     * @ appWidgetId target widget id
     */
    void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
        Util.setContextIfNull(context.getApplicationContext());
        Counter counter = CounterManager.getInstance().getWidgetCounterForId(appWidgetId);
        Intent intent = new Intent(context, WidgetActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("created", "false");
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, appWidgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.linear1, pendingIntent);

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
        Util.clearContextIfEquals(context);
    }

    /**
     * Manually refreshes the widgets, for the cases when you delete a counter, its widget should no longer display the data.
     */
    public static void refresh(Context context) {
        // last resort
        int[] ids = AppWidgetManager.getInstance(context.getApplicationContext()).getAppWidgetIds(new ComponentName(context.getApplicationContext(), WidgetProvider.class));
        // Util.log("Widget ids: " + Arrays.toString(ids));
        WidgetProvider myWidget = new WidgetProvider();
        myWidget.onUpdate(context, AppWidgetManager.getInstance(context), ids);
    }

    public static void registerListener() {
        Counter.addEventListener(new WidgetProvider());
    }

}