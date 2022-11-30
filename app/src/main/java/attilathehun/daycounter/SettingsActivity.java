package attilathehun.daycounter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.*;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.appbar.AppBarLayout;
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
import android.widget.TextView;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.Intent;
import android.net.Uri;
import android.widget.AdapterView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.DialogFragment;

public class SettingsActivity extends AppCompatActivity {
	
	private Toolbar _toolbar;
	private AppBarLayout _app_bar;
	private CoordinatorLayout _coordinator;
	
	private ArrayList<String> developer_options_list = new ArrayList<>();
	private ArrayList<String> functional_options_list = new ArrayList<>();
	
	private TextView textview1;
	private ListView functional_options_view;
	private TextView textview3;
	private ListView developer_options_view;
	
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
		_app_bar = findViewById(R.id._app_bar);
		_coordinator = findViewById(R.id._coordinator);
		_toolbar = findViewById(R.id._toolbar);
		setSupportActionBar(_toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		_toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _v) {
				onBackPressed();
			}
		});
		textview1 = findViewById(R.id.textview1);
		functional_options_view = findViewById(R.id.functional_options_view);
		textview3 = findViewById(R.id.textview3);
		developer_options_view = findViewById(R.id.developer_options_view);
		sp = getSharedPreferences("data", Activity.MODE_PRIVATE);
		
		functional_options_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> _param1, View _param2, int _param3, long _param4) {
				final int _position = _param3;
				if (_position == 0) {
					CounterManager.getInstance().clearCounters();
					SketchwareUtil.showMessage(getApplicationContext(), "All clear!");
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
					SketchwareUtil.showMessage(getApplicationContext(), "Delete all counters");
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
							SketchwareUtil.showMessage(getApplicationContext(), "log cleared");
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
	}
	
	private void initializeLogic() {
		setTitle("Settings");
		_initListsAndViews();
	}
	
	@Override
	public void onBackPressed() {
		intent.setClass(getApplicationContext(), MainActivity.class);
		startActivity(intent);
		finish();
	}
	public void _initListsAndViews() {
		developer_options_list.add("Start service");
		developer_options_list.add("View log");
		developer_options_list.add("Clear log");
		developer_options_view.setAdapter(new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, developer_options_list));
		((BaseAdapter)developer_options_view.getAdapter()).notifyDataSetChanged();
		functional_options_list.add("Clear data");
		functional_options_list.add("Export Data (bytes)");
		functional_options_list.add("Export Data (JSON)");
		functional_options_list.add("Import Data (bytes)");
		functional_options_list.add("Import Data (JSON)");
		functional_options_view.setAdapter(new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, functional_options_list));
		((BaseAdapter)functional_options_view.getAdapter()).notifyDataSetChanged();
	}
	
}