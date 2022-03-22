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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.content.SharedPreferences;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.text.Editable;
import android.text.TextWatcher;
import androidx.core.*;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.DialogFragment;


public class CreateCounterActivity extends  Activity { 
	
	
	private ArrayList<String> months = new ArrayList<>();
	private ArrayList<String> days = new ArrayList<>();
	
	private LinearLayout linear1;
	private TextView textview1;
	private LinearLayout linear2;
	private TextView textview2;
	private LinearLayout linear3;
	private Button create_button;
	private Spinner day_selector;
	private Spinner month_selector;
	private EditText enter_year;
	private EditText enter_target_age;
	
	private SharedPreferences file;
	private Intent intent = new Intent();
	@Override
	protected void onCreate(Bundle _savedInstanceState) {
		super.onCreate(_savedInstanceState);
		setContentView(R.layout.create_counter);
		initialize(_savedInstanceState);
		initializeLogic();
	}
	
	private void initialize(Bundle _savedInstanceState) {
		
		linear1 = (LinearLayout) findViewById(R.id.linear1);
		textview1 = (TextView) findViewById(R.id.textview1);
		linear2 = (LinearLayout) findViewById(R.id.linear2);
		textview2 = (TextView) findViewById(R.id.textview2);
		linear3 = (LinearLayout) findViewById(R.id.linear3);
		create_button = (Button) findViewById(R.id.create_button);
		day_selector = (Spinner) findViewById(R.id.day_selector);
		month_selector = (Spinner) findViewById(R.id.month_selector);
		enter_year = (EditText) findViewById(R.id.enter_year);
		enter_target_age = (EditText) findViewById(R.id.enter_target_age);
		file = getSharedPreferences("data", Activity.MODE_PRIVATE);
		
		create_button.setOnLongClickListener(new View.OnLongClickListener() {
			 @Override
				public boolean onLongClick(View _view) {
				SketchwareUtil.showMessage(getApplicationContext(), "Hmm?");
				return true;
				}
			 });
		
		create_button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				if (enter_year.getText().toString().equals("")) {
					SketchwareUtil.showMessage(getApplicationContext(), "Fill out all the fileds please");
				}
				else {
					if (enter_target_age.getText().toString().equals("")) {
						SketchwareUtil.showMessage(getApplicationContext(), "Fill out all the fileds please");
					}
					else {
						SketchwareUtil.hideKeyboard(getApplicationContext());
						file.edit().putString("counterExists", "true").commit();
						file.edit().putInt("targetDay", day_selector.getSelectedItemPosition()).commit();
						file.edit().putInt("targetMonth", month_selector.getSelectedItemPosition()).commit();
						file.edit().putInt("targetYear", Integer.parseInt(enter_year.getText().toString()) + Integer.parseInt(enter_target_age.getText().toString())).commit();
						file.edit().putInt("targetAge", Integer.parseInt(enter_target_age.getText().toString())).commit();
						intent.setClass(getApplicationContext(), MainActivity.class);
						startActivity(intent);
						finish();
					}
				}
			}
		});
		
		enter_year.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence _param1, int _param2, int _param3, int _param4) {
				final String _charSeq = _param1.toString();
				if (enter_year.getText().toString().length() > 4) {
					enter_year.setText(enter_year.getText().toString().substring((int)(0), (int)(4)));
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence _param1, int _param2, int _param3, int _param4) {
				
			}
			
			@Override
			public void afterTextChanged(Editable _param1) {
				
			}
		});
		
		enter_target_age.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence _param1, int _param2, int _param3, int _param4) {
				final String _charSeq = _param1.toString();
				if (enter_target_age.getText().toString().length() > 3) {
					enter_target_age.setText(enter_target_age.getText().toString().substring((int)(0), (int)(3)));
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence _param1, int _param2, int _param3, int _param4) {
				
			}
			
			@Override
			public void afterTextChanged(Editable _param1) {
				
			}
		});
	}
	
	private void initializeLogic() {
		_initLists();
		month_selector.setAdapter(new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_dropdown_item, months));
		day_selector.setAdapter(new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_dropdown_item, days));
		((ArrayAdapter)day_selector.getAdapter()).notifyDataSetChanged();
		((ArrayAdapter)month_selector.getAdapter()).notifyDataSetChanged();
	}
	
	@Override
	protected void onActivityResult(int _requestCode, int _resultCode, Intent _data) {
		
		super.onActivityResult(_requestCode, _resultCode, _data);
		
		switch (_requestCode) {
			
			default:
			break;
		}
	}
	
	public void _initLists () {
		for(int i = 1; i <= 31; i++){
			  days.add(Integer.toString(i));
		}
		months.add("January");
		months.add("February");
		months.add("March");
		months.add("April");
		months.add("May");
		months.add("June");
		months.add("July");
		months.add("August");
		months.add("September");
		months.add("October");
		months.add("November");
		months.add("December");
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