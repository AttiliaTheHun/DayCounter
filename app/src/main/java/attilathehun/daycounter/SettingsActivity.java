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
import java.util.ArrayList;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Switch;
import android.content.SharedPreferences;
import android.content.Intent;
import android.net.Uri;
import android.widget.CompoundButton;
import android.widget.AdapterView;
import androidx.core.*;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.DialogFragment;


public class SettingsActivity extends  Activity { 
	
	
	private ArrayList<String> developer_options_list = new ArrayList<>();
	private ArrayList<String> functional_options_list = new ArrayList<>();
	
	private TextView textview1;
	private LinearLayout linear1;
	private ListView functional_options_view;
	private TextView textview3;
	private ListView developer_options_view;
	private Switch enable_notification_switch;
	
	private SharedPreferences sp;
	private Intent intent = new Intent();
	@Override
	protected void onCreate(Bundle _savedInstanceState) {
		super.onCreate(_savedInstanceState);
		setContentView(R.layout.settings);
		initialize(_savedInstanceState);
		initializeLogic();
	}
	
	private void initialize(Bundle _savedInstanceState) {
		
		textview1 = (TextView) findViewById(R.id.textview1);
		linear1 = (LinearLayout) findViewById(R.id.linear1);
		functional_options_view = (ListView) findViewById(R.id.functional_options_view);
		textview3 = (TextView) findViewById(R.id.textview3);
		developer_options_view = (ListView) findViewById(R.id.developer_options_view);
		enable_notification_switch = (Switch) findViewById(R.id.enable_notification_switch);
		sp = getSharedPreferences("data", Activity.MODE_PRIVATE);
		
		functional_options_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> _param1, View _param2, int _param3, long _param4) {
				final int _position = _param3;
				if (_position == 0) {
					Util.resetData();
					SketchwareUtil.showMessage(getApplicationContext(), "Data reset");
					intent.setClass(getApplicationContext(), MainActivity.class);
					startActivity(intent);
					finish();
				}
				else {
					
				}
			}
		});
		
		functional_options_view.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> _param1, View _param2, int _param3, long _param4) {
				final int _position = _param3;
				if (_position == 0) {
					SketchwareUtil.showMessage(getApplicationContext(), "Resets counter data, enabling creating a different counter");
				}
				else {
					
				}
				return true;
			}
		});
		
		developer_options_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> _param1, View _param2, int _param3, long _param4) {
				final int _position = _param3;
				if (_position == 0) {
					Util.startService(getApplicationContext());
				}
				else {
					if (_position == 1) {
						Util.viewLog(getApplicationContext());
					}
					else {
						if (_position == 2) {
							Util.clearLog();
							showMessage("log cleared");
						}
						else {
							
						}
					}
				}
			}
		});
		
		developer_options_view.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> _param1, View _param2, int _param3, long _param4) {
				final int _position = _param3;
				if (_position == 0) {
					SketchwareUtil.showMessage(getApplicationContext(), "Starts the notification service if not already running");
				}
				else {
					if (_position == 1) {
						SketchwareUtil.showMessage(getApplicationContext(), "Opens application log file");
					}
					else {
						if (_position == 2) {
							SketchwareUtil.showMessage(getApplicationContext(), "Erases application log file");
						}
						else {
							
						}
					}
				}
				return true;
			}
		});
		
		enable_notification_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton _param1, boolean _param2)  {
				final boolean _isChecked = _param2;
				sp.edit().putString("enableNotification", String.valueOf(_isChecked)).commit();
				if (_isChecked) {
					Util.startService(SettingsActivity.this);
				}
				else {
					stopService(new Intent(SettingsActivity.this, NotificationService.class));
				}
			}
		});
	}
	
	private void initializeLogic() {
		setTitle("Settings");
		if (!sp.getString("enableNotification", "").equals("")) {
			enable_notification_switch.setChecked(sp.getString("enableNotification", "").equals("true"));
		}
		_initListsAndViews();
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
	public void onBackPressed() {
		intent.setClass(getApplicationContext(), MainActivity.class);
		startActivity(intent);
		finish();
	}
	public void _initListsAndViews () {
		developer_options_list.add("Start service");
		developer_options_list.add("View log");
		developer_options_list.add("Clear log");
		developer_options_view.setAdapter(new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, developer_options_list));
		((BaseAdapter)developer_options_view.getAdapter()).notifyDataSetChanged();
		functional_options_list.add("Reset data");
		functional_options_view.setAdapter(new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, functional_options_list));
		((BaseAdapter)functional_options_view.getAdapter()).notifyDataSetChanged();
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