package attilathehun.daycounter;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import attilathehun.daycounter.Util;

/**
* This is the Activity for our launcher widget, honestly I have no idea what it does,
* but the windget did not work until I added it, so I suggest not touching this piece
* of code
*/
public class WidgetActivity extends Activity {
		
		@Override
		protected void onCreate(Bundle savedInstanceState) {
				super.onCreate(savedInstanceState);
				Util.log("WidgetActivity.onCreate()");
				
				
		}
		
		@Override public void onBackPressed() { 
		//don't call super!!! 
		do_widget(); 
		} 
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
