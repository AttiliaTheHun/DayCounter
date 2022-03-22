package attilathehun.daycounter;

import android.app.Activity;
import android.app.*;
import android.os.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import android.content.*;
import android.content.res.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.media.*;
import android.net.*;
import android.text.*;
import android.text.style.*;
import android.util.*;
import android.webkit.*;
import android.animation.*;
import android.view.animation.*;
import java.util.*;
import java.util.regex.*;
import java.text.*;
import org.json.*;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.content.Intent;
import android.net.Uri;
import android.content.SharedPreferences;
import androidx.core.*;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.DialogFragment;
import attilathehun.daycounter.DateChangedListener;
/** <SKETCHWARE-DANGER-ZONE> **/
/*

public class MainActivity extends  Activity { 


private double daysRemaining = 0;

private LinearLayout linear1;
private LinearLayout linear2;
private TextView days_indicator;

private Intent intent = new Intent();
private SharedPreferences file;
@Override
protected void onCreate(Bundle _savedInstanceState) {
super.onCreate(_savedInstanceState);
setContentView(R.layout.main);
initialize(_savedInstanceState);
initializeLogic();
}

private void initialize(Bundle _savedInstanceState) {

linear1 = (LinearLayout) findViewById(R.id.linear1);
linear2 = (LinearLayout) findViewById(R.id.linear2);
days_indicator = (TextView) findViewById(R.id.days_indicator);
file = getSharedPreferences("data", Activity.MODE_PRIVATE);
}

private void initializeLogic() {
*/
/** </SKETCHWARE-DANGER-ZONE> **/
/** <SUBSTITUTE-CODE> **/
public class MainActivity extends Activity implements DateChangedListener {
	
	private double daysRemaining = 0;
	
	private LinearLayout linear1;
	private LinearLayout linear2;
	private TextView days_indicator;
	
	private Intent intent = new Intent();
	private SharedPreferences file;
	
	 @Override
	protected void onCreate (Bundle _savedInstanceState) {
		super.onCreate(_savedInstanceState);
		setContentView(R.layout.main);
		initialize(_savedInstanceState);
		initializeLogic();
	}
	
	private void initialize(Bundle _savedInstanceState) {
		linear1 = (LinearLayout) findViewById(R.id.linear1);
				linear2 = (LinearLayout) findViewById(R.id.linear2);
				days_indicator = (TextView) findViewById(R.id.days_indicator);
		file = getSharedPreferences("data", Activity.MODE_PRIVATE);
	}
	
	public void onDateChanged() {
		Util.log("MainActivity.onDateChanged()");
		_updateDaysIndicator();
	}
	
	
	private void initializeLogic() {
		/** </SUBSTITUTE-CODE> **/
		Util.log("\nMainActivity.onCreate()");
		Util.setContext(getApplicationContext());
		if (file.getString("enableNotification", "").equals("")) {
			file.edit().putString("enableNotification", "true").commit();
		}
		NotificationService.createNotificationChannel();
		Util.startService(getApplicationContext());
		ServiceLauncher.addListener(this);
		if (file.getString("counterExists", "").equals("") || file.getString("counterExists", "").equals("false")) {
			intent.setClass(getApplicationContext(), CreateCounterActivity.class);
			startActivity(intent);
			finish();
		}
		else {
			_updateDaysIndicator();
		}
	}
	
	@Override
	protected void onActivityResult(int _requestCode, int _resultCode, Intent _data) {
		
		super.onActivityResult(_requestCode, _resultCode, _data);
		
		switch (_requestCode) {
			
			default:
			break;
		}
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		menu.add(0, 0, 0, "About");
		menu.add(0, 1, 1, "Settings");
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
		public boolean onOptionsItemSelected(MenuItem item){
		final int _id = item.getItemId();
		final String _title = (String) item.getTitle();
		switch (_id){
			case 0:
			final AlertDialog aboutdialog = new AlertDialog.Builder(MainActivity.this).create();
			View inflate = getLayoutInflater().inflate(R.layout.about_dialog, null);
			aboutdialog.setView(inflate);
			TextView close_view = (TextView) inflate.findViewById(R.id.close_view);
			TextView textview2 = (TextView) inflate.findViewById(R.id.textview2);
			android.text.util.Linkify.addLinks(textview2, android.text.util.Linkify.ALL);
			textview2.setLinkTextColor(Color.parseColor("#CDDC39"));
			textview2.setLinksClickable(true);
			close_view.setOnClickListener(new OnClickListener() { 
				public void onClick(View view) { 
					aboutdialog.dismiss();
					 } 
			});
			aboutdialog.show();
			
			break;
			case 1:
			intent.setClass(getApplicationContext(), SettingsActivity.class);
			startActivity(intent);
			finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	public void _updateDaysIndicator () {
		daysRemaining = Counter.getDaysRemaining();
		days_indicator.setText(String.valueOf((long)(daysRemaining)).concat(" days left!"));
	}
	
	
	@Deprecated
	public void showMessage(String _s) {
		Toast.makeText(getApplicationContext(), _s, Toast.LENGTH_SHORT).show();
	}
	
	@Deprecated
	public int getLocationX(View _v) {
		int _location[] = new int[2];
		_v.getLocationInWindow(_location);
		return _location[0];
	}
	
	@Deprecated
	public int getLocationY(View _v) {
		int _location[] = new int[2];
		_v.getLocationInWindow(_location);
		return _location[1];
	}
	
	@Deprecated
	public int getRandom(int _min, int _max) {
		Random random = new Random();
		return random.nextInt(_max - _min + 1) + _min;
	}
	
	@Deprecated
	public ArrayList<Double> getCheckedItemPositionsToArray(ListView _list) {
		ArrayList<Double> _result = new ArrayList<Double>();
		SparseBooleanArray _arr = _list.getCheckedItemPositions();
		for (int _iIdx = 0; _iIdx < _arr.size(); _iIdx++) {
			if (_arr.valueAt(_iIdx))
			_result.add((double)_arr.keyAt(_iIdx));
		}
		return _result;
	}
	
	@Deprecated
	public float getDip(int _input){
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, _input, getResources().getDisplayMetrics());
	}
	
	@Deprecated
	public int getDisplayWidthPixels(){
		return getResources().getDisplayMetrics().widthPixels;
	}
	
	@Deprecated
	public int getDisplayHeightPixels(){
		return getResources().getDisplayMetrics().heightPixels;
	}
	
}