package attilathehun.daycounter;
 
import java.util.ArrayList;
 
import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.RadioGroup;
import android.widget.RadioButton;
import android.widget.RemoteViews;
import android.os.Bundle;
 
import attilathehun.daycounter.Util;
import attilathehun.daycounter.Counter;
import attilathehun.daycounter.CounterManager;
 
/**
 * This is the widget configuration activity, that gets open when you create a widget.
 */
public class WidgetActivity extends Activity {
 
    /**
     * When the activity is instantiated. It creates a dialog listing all the counters for the user to select the source data counter for the widget.
     * @param savedInstanceState yeah
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
 
        Util.setContextIfNull(getApplicationContext());
 
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        final int mAppWidgetId;
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        } else {
            mAppWidgetId = -1;
        }
 
        final AlertDialog.Builder builder = new AlertDialog.Builder(WidgetActivity.this);
        final RadioGroup rgroup = new RadioGroup(WidgetActivity.this);
        ArrayList<Counter> counters = CounterManager.getInstance().getCounters();
        View inflate = getLayoutInflater().inflate(R.layout.about_dialog, null);
        for (int i = 0; i < counters.size(); i++) {
            if (i == 0) {
                // rgroup.check(Integer.parseInt(counters.get(i).getId()));
            }
            final RadioButton rb = new RadioButton(WidgetActivity.this);
            final LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            rb.setText(counters.get(i).getName() + " (" + counters.get(i).getDateString() + ")");
            rb.setId(Integer.parseInt(counters.get(i).getId()));
            rgroup.addView(rb, i, lp);
        }
 
        builder.setView(rgroup);
        builder.setTitle(Util.getContext().getResources().getString(R.string.counter_dialog_title));
        builder.setCancelable(false);
 
        builder.setPositiveButton(Util.getContext().getResources().getString(R.string.counter_dialog_positive), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface _dialog, int _which) {
                CounterManager.getInstance().bindWidget(String.valueOf(rgroup.getCheckedRadioButtonId()), mAppWidgetId);
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(Util.getContext());
                //Update the App Widget with a RemoteViews layout by calling updateAppWidget(int, RemoteViews):
                RemoteViews views = new RemoteViews(Util.getContext().getPackageName(),
                        R.layout.widget);
                appWidgetManager.updateAppWidget(mAppWidgetId, views);
                 Util.refreshWidgets();
                //Finally, create the return Intent, set it with the Activity result, and finish the Activity:
                Intent resultValue = new Intent();
                resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
                setResult(RESULT_OK, resultValue);
                finish();
            }
        });
 
        builder.setNegativeButton(Util.getContext().getResources().getString(R.string.counter_dialog_negative), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface _dialog, int _which) {
                finish();
            }
        });
 
        builder.create().show();
 
    }
 
    @Override
    public void onBackPressed() {
        //don't call super!!!
        do_widget();
    }
 
    /**
     * Quit the activity.
     */
    private void do_widget() {
        try {
            Intent resultValue = new Intent();
            // resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
 
}
 