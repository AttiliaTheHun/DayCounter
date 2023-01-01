package attilathehun.daycounter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.*;
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
import java.io.*;
import java.util.*;
import java.util.regex.*;
import java.text.*;
import org.json.*;
import java.util.ArrayList;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.Intent;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import androidx.core.*;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.DialogFragment;

public class CreateCounterActivity extends AppCompatActivity {
	
	private ArrayList<String> months = new ArrayList<>();
	private ArrayList<String> days = new ArrayList<>();
	
	private LinearLayout linear1;
	private TextView counter_name_label;
	private LinearLayout linear4;
	private TextView birth_date_label;
	private LinearLayout linear2;
	private TextView highest_estimated_age_label;
	private LinearLayout linear3;
	private Button create_button;
	private EditText name_box;
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
		linear1 = findViewById(R.id.linear1);
		counter_name_label = findViewById(R.id.counter_name_label);
		linear4 = findViewById(R.id.linear4);
		birth_date_label = findViewById(R.id.birth_date_label);
		linear2 = findViewById(R.id.linear2);
		highest_estimated_age_label = findViewById(R.id.highest_estimated_age_label);
		linear3 = findViewById(R.id.linear3);
		create_button = findViewById(R.id.create_button);
		name_box = findViewById(R.id.name_box);
		day_selector = findViewById(R.id.day_selector);
		month_selector = findViewById(R.id.month_selector);
		enter_year = findViewById(R.id.enter_year);
		enter_target_age = findViewById(R.id.enter_target_age);
		file = getSharedPreferences("data", Activity.MODE_PRIVATE);
		
		create_button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				if (enter_year.getText().toString().equals("")) {
					SketchwareUtil.showMessage(getApplicationContext(), getResources().getString(R.string.fill_all_fields));
				}
				else {
					if (enter_target_age.getText().toString().equals("")) {
						SketchwareUtil.showMessage(getApplicationContext(), getResources().getString(R.string.fill_all_fields));
					}
					else {
						SketchwareUtil.hideKeyboard(getApplicationContext());
						String name = name_box.getText().toString();
						int targetDay =  day_selector.getSelectedItemPosition();
						int targetMonth = month_selector.getSelectedItemPosition();
						int targetAge = Integer.parseInt(enter_target_age.getText().toString());
							int targetYear = Integer.parseInt(enter_year.getText().toString()) + targetAge;
						boolean success = CounterManager.getInstance().addCounter(name, targetDay, targetMonth, targetYear, targetAge);
						if (success) {
							Util.startService(CreateCounterActivity.this);
							intent.setClass(getApplicationContext(), MainActivity.class);
							startActivity(intent);
							finish();
						} else {
							SketchwareUtil.showMessage(getApplicationContext(), getResources().getString(R.string.invalid_input));
							
						}
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
		Util.setContextIfNull(this);
		_initLists();
		_initTranslation();
		month_selector.setAdapter(new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_dropdown_item, months));
		((ArrayAdapter)month_selector.getAdapter()).notifyDataSetChanged();
		day_selector.setAdapter(new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_dropdown_item, days));
		((ArrayAdapter)day_selector.getAdapter()).notifyDataSetChanged();
	}
	
	@Override
	public void onBackPressed() {
		Util.clearContextIfEquals(this);
		intent.setClass(getApplicationContext(), MainActivity.class);
		intent.putExtra("ABORTED", "true");
		startActivity(intent);
		finish();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		Util.clearContextIfEquals(this);
	}
	public void _initLists() {
		for(int i = 1; i <= 31; i++){
			  days.add(Integer.toString(i));
		}
		months.add(getResources().getString(R.string.january));
				months.add(getResources().getString(R.string.february));
				months.add(getResources().getString(R.string.march));
				months.add(getResources().getString(R.string.april));
				months.add(getResources().getString(R.string.may));
				months.add(getResources().getString(R.string.june));
				months.add(getResources().getString(R.string.july));
				months.add(getResources().getString(R.string.august));
				months.add(getResources().getString(R.string.september));
				months.add(getResources().getString(R.string.october));
				months.add(getResources().getString(R.string.november));
				months.add(getResources().getString(R.string.december));
		
	}
	
	
	public void _initTranslation() {
		counter_name_label.setText(getResources().getString(R.string.counter_name_label));
		birth_date_label.setText(getResources().getString(R.string.birth_date_label));
		highest_estimated_age_label.setText(getResources().getString(R.string.highest_estimated_age_label));
		create_button.setText(getResources().getString(R.string.create_counter_label));
		name_box.setHint(getResources().getString(R.string.counter_name_hint));
		enter_year.setHint(getResources().getString(R.string.birth_date_year_label));
		enter_target_age.setHint(getResources().getString(R.string.highest_estimated_age_hint));
	}
	
}