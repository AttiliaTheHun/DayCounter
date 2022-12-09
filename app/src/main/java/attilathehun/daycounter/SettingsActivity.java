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
import androidx.core.*;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.DialogFragment;

public class SettingsActivity extends AppCompatActivity {
	
	private Toolbar _toolbar;
	private AppBarLayout _app_bar;
	private CoordinatorLayout _coordinator;
	private  static final int PICKFILE_RESULT_CODE = 8788;
	
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
					if (_position == 1) {
						if (CounterManager.getInstance().exportBytesDefault()) {
							SketchwareUtil.showMessage(getApplicationContext(), String.format(Util.getContext().getResources().getString(R.string.exported_to), CounterManager.DEFAULT_PATH));
							
						}
					}
					else {
						if (_position == 2) {
							if (CounterManager.getInstance().exportJSONDefault()) {
								SketchwareUtil.showMessage(getApplicationContext(), String.format(Util.getContext().getResources().getString(R.string.exported_to_json), CounterManager.DEFAULT_PATH));
							}
						}
						else {
							if (_position == 3) {
								Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
								chooseFile.setType("*/*");
								chooseFile = Intent.createChooser(chooseFile, "Choose a file");
								startActivityForResult(chooseFile, PICKFILE_RESULT_CODE);
							}
							else {
								if (_position == 4) {
									Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
									chooseFile.setType("*/*");
									chooseFile = Intent.createChooser(chooseFile, "Choose a file");
									startActivityForResult(chooseFile, PICKFILE_RESULT_CODE);
								}
								else {
									
								}
							}
						}
					}
				}
			}
		});
		
		functional_options_view.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> _param1, View _param2, int _param3, long _param4) {
				final int _position = _param3;
				if (_position == 0) {
					SketchwareUtil.showMessage(getApplicationContext(), Util.getContext().getResources().getString(R.string.clear_data_description));
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
					SketchwareUtil.showMessage(getApplicationContext(), Util.getContext().getResources().getString(R.string.start_service_description));
				}
				else {
					if (_position == 1) {
						SketchwareUtil.showMessage(getApplicationContext(), Util.getContext().getResources().getString(R.string.view_log_description));
					}
					else {
						if (_position == 2) {
							SketchwareUtil.showMessage(getApplicationContext(), Util.getContext().getResources().getString(R.string.clear_log_description));
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
		setTitle(Util.getContext().getResources().getString(R.string.settings));
		_initListsAndViews();
		//private static final int CHOOSE_FILE_REQUESTCODE = 8777;
		//private static final int PICKFILE_RESULT_CODE = 8778;
	}
	
	@Override
	protected void onActivityResult(int _requestCode, int _resultCode, Intent _data) {
		super.onActivityResult(_requestCode, _resultCode, _data);
		Uri uri = _data.getData();
		String path = UriUtils.getPathFromUri(this, uri);
		if (path.toString().endsWith(".json")) {
				if (CounterManager.getInstance().importJSON(path)) {
						SketchwareUtil.showMessage(getApplicationContext(), Util.getContext().getResources().getString(R.string.import_successful));
				}
		} else if (CounterManager.getInstance().importBytes(path)) {
				SketchwareUtil.showMessage(getApplicationContext(), Util.getContext().getResources().getString(R.string.import_successful));
		}
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
	public void _initListsAndViews() {
		developer_options_list.add(Util.getContext().getResources().getString(R.string.start_service));
		developer_options_list.add(Util.getContext().getResources().getString(R.string.clear_log));
		developer_options_list.add(Util.getContext().getResources().getString(R.string.view_log));
		developer_options_view.setAdapter(new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, developer_options_list));
		((BaseAdapter)developer_options_view.getAdapter()).notifyDataSetChanged();
		functional_options_list.add(Util.getContext().getResources().getString(R.string.clear_data));
		functional_options_list.add(Util.getContext().getResources().getString(R.string.export_data_bytes));
		functional_options_list.add(Util.getContext().getResources().getString(R.string.export_data_json));
		functional_options_list.add(Util.getContext().getResources().getString(R.string.import_data_bytes));
		functional_options_list.add(Util.getContext().getResources().getString(R.string.import_data_json));
		functional_options_view.setAdapter(new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, functional_options_list));
		((BaseAdapter)functional_options_view.getAdapter()).notifyDataSetChanged();
		textview3.setText(Util.getContext().getResources().getString(R.string.developer_settings));
		textview1.setText(Util.getContext().getResources().getString(R.string.functional_settings));
	}
	
}