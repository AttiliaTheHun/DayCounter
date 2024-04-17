package attilathehun.daycounter;

import android.animation.*;
import android.app.*;
import android.app.AlertDialog;
import android.content.*;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.media.*;
import android.net.*;
import android.net.Uri;
import android.os.*;
import android.text.*;
import android.text.style.*;
import android.util.*;
import android.view.*;
import android.view.View;
import android.view.View.*;
import android.view.animation.*;
import android.webkit.*;
import android.widget.*;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import androidx.annotation.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.*;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import java.io.*;
import java.text.*;
import java.util.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.*;
import org.json.*;
import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.content.ContextCompat;
import androidx.core.app.ActivityCompat;
import android.widget.PopupMenu;
import android.view.MenuItem;
import attilathehun.daycounter.Util;
import attilathehun.daycounter.Counter;
import attilathehun.daycounter.CounterManager;

public class MainActivity extends AppCompatActivity {
	
	private Toolbar _toolbar;
	private AppBarLayout _app_bar;
	private CoordinatorLayout _coordinator;
	private FloatingActionButton _fab;
	private String useless = "";
	private  static List<CounterEventListener> listeners = new ArrayList<CounterEventListener>();
	private HashMap<String, Object> dhdh = new HashMap<>();
	private  Counter temp = Counter.emptyCounter();
	private  final int REQUEST_CODE = 0;
	
	private ArrayList<HashMap<String, Object>> counters = new ArrayList<>();
	
	private LinearLayout linear2;
	private ListView listview1;
	
	private Intent intent = new Intent();
	private AlertDialog.Builder d;
	
	@Override
	protected void onCreate(Bundle _savedInstanceState) {
		super.onCreate(_savedInstanceState);
		setContentView(R.layout.main);
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
		_fab = findViewById(R.id._fab);
		
		linear2 = findViewById(R.id.linear2);
		listview1 = findViewById(R.id.listview1);
		d = new AlertDialog.Builder(this);
		
		listview1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> _param1, View _param2, int _param3, long _param4) {
				final int _position = _param3;
				final AlertDialog counterDialog = new AlertDialog.Builder(MainActivity.this).create();
				View inflate = getLayoutInflater().inflate(R.layout.counter_dialog, null);
				counterDialog.setView(inflate);
				TextView close_view = (TextView) inflate.findViewById(R.id.close_view);
				close_view.setText(getResources().getString(R.string.close_dialog));
				TextView textview1 = (TextView) inflate.findViewById(R.id.textview1);
				textview1.setText(Util.getDaysRemaining(CounterManager.getInstance().getCounters().get(_position), getApplicationContext()));
				close_view.setOnClickListener(new OnClickListener() { 
					public void onClick(View view) { counterDialog.dismiss();
						 } 
				});
				counterDialog.show();
				
			}
		});
		
		listview1.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> _param1, View _param2, int _param3, long _param4) {
				final int _position = _param3;
				View view = listview1.getChildAt(_position - listview1.getFirstVisiblePosition());
				Util.log(view.toString());
				PopupMenu popupMenu = new PopupMenu(MainActivity.this, view);
				popupMenu.getMenuInflater().inflate(R.xml.list_item_menu, popupMenu.getMenu());
				popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
					 @Override
					public boolean onMenuItemClick(MenuItem menuItem) {
						final int id = menuItem.getItemId();
						
						switch(id) {
							case R.id.edit:
							temp = CounterManager.getInstance().getCounters().get(_position);
							if (temp == null || temp.isEmpty()) {
								throw new IllegalArgumentException("The counter cannot be null");
							}
							intent.putExtra("edit", "true");
							intent.putExtra("id", temp.getId());
							intent.putExtra("name", temp.getName());
							intent.putExtra("targetDay", temp.getDay());
							intent.putExtra("targetMonth", temp.getMonth());
							intent.putExtra("targetYear", temp.getYear() - temp.getTargetAge());
							intent.putExtra("targetAge", temp.getTargetAge());
							intent.putExtra("position", _position);
							intent.setClass(getApplicationContext(), CreateCounterActivity.class);
							temp = null;
							startActivity(intent);
							break;
							case R.id.duplicate:
							temp = CounterManager.getInstance().getCounters().get(_position);
							CounterManager.getInstance().duplicate(temp);
							temp = null;
							_refresh();
							break;
							case R.id.delete:
							String counterId = counters.get(_position).get("id").toString();
							CounterManager.getInstance().deleteCounter(counterId);
							_refresh();
							
							break;
						}
						return true;
					}
				});
				
				popupMenu.show();
				
				return true;
			}
		});
		
		_fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				intent.setClass(getApplicationContext(), CreateCounterActivity.class);
				startActivity(intent);
				finish();
			}
		});
	}
	
	private void initializeLogic() {
		getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		_init();
		
		if (Util.DEBUG) {
			_checkPermissions();
		}
		if (!CounterManager.getInstance().counterExists() && !(getIntent().getStringExtra("ABORTED") != null && getIntent().getStringExtra("ABORTED").equals("true"))) {
			intent.setClass(getApplicationContext(), CreateCounterActivity.class);
			startActivity(intent);
			finish();
		}
		
		if (counters.size() > 0) {
			listview1.smoothScrollToPosition((int)(getIntent().getIntExtra("position", 0) ));
		}
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 0, 0, Util.getContext().getResources().getString(R.string.about));
		menu.add(0, 1, 1, Util.getContext().getResources().getString(R.string.settings));
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		final int _id = item.getItemId();
		final String _title = (String) item.getTitle();
		switch (_id){
			case 0:
			final AlertDialog aboutdialog = new AlertDialog.Builder(MainActivity.this).create();
			View inflate = getLayoutInflater().inflate(R.layout.about_dialog, null);
			aboutdialog.setView(inflate);
			TextView close_view = (TextView) inflate.findViewById(R.id.close_view);
			TextView textview2 = (TextView) inflate.findViewById(R.id.textview2);
			TextView textview3 = (TextView) inflate.findViewById(R.id.textview3);
			textview2.setText(getString(R.string.about_dialog_application_description)); 
			android.text.util.Linkify.addLinks(textview2, android.text.util.Linkify.ALL);
			textview2.setLinkTextColor(Color.parseColor("#CDDC39"));
			textview2.setLinksClickable(true);
			textview3.setText(getString(R.string.about_dialog_version_description));
			close_view.setText(getString(R.string.close_dialog));
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
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		Util.clearContextIfEquals(this);
	}
	
	@Override
	public void onBackPressed() {
		Util.clearContextIfEquals(this);
	}
	public void _init() {
		if (Util.DEBUG && true) {
			
			
		}
		// Force Sketchware to include the Gson library
		useless = new Gson().toJson(counters);
		Util.setContextIfNull(getApplicationContext());
		Util.startService(this);
		Util.registerProviders();
		_refresh();
	}
	public void _checkPermissions() {
		if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
				|| ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
			ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
		}
	}
	 @Override
	public void onRequestPermissionsResult(int requestCode,
	                                       
	String[] permissions,
	  
	 int[] grantResults)
	    {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == REQUEST_CODE) {
			if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				SketchwareUtil.showMessage(getApplicationContext(), getString(R.string.permission_granted));
			} else {
				SketchwareUtil.showMessage(getApplicationContext(), getString(R.string.storage_permission_required));
			}
		}
	}
	
	
	public void _refresh() {
		Util.setContextIfNull(getApplicationContext());
		counters = CounterManager.getInstance().getCountersData();
		listview1.setAdapter(new Listview1Adapter(counters));
		((BaseAdapter)listview1.getAdapter()).notifyDataSetChanged();
		/*
}

public class Listview1Adapter extends BaseAdapter {

ArrayList<HashMap<String, Object>> _data;

public Listview1Adapter(ArrayList<HashMap<String, Object>> _arr) {
_data = _arr;
}

@Override
public int getCount() {
return _data.size();
}

@Override
public HashMap<String, Object> getItem(int _index) {
return _data.get(_index);
}

@Override
public long getItemId(int _index) {
return _index;
}

@Override
public View getView(final int _position, View _v, ViewGroup _container) {
LayoutInflater _inflater = getLayoutInflater();
View _view = _v;
if (_view == null) {
_view = _inflater.inflate(R.layout.counter_list_item, null);
}

final LinearLayout linear1 = _view.findViewById(R.id.linear1);
final LinearLayout linear2 = _view.findViewById(R.id.linear2);
final LinearLayout linear3 = _view.findViewById(R.id.linear3);
final TextView name_view = _view.findViewById(R.id.name_view);
final Switch notification_indicator = _view.findViewById(R.id.notification_indicator);
final TextView date_view = _view.findViewById(R.id.date_view);
final ImageView delete_button = _view.findViewById(R.id.delete_button);

*/
	}
	public class Listview1Adapter extends BaseAdapter {
		
		ArrayList<HashMap<String, Object>> _data;
		
		public Listview1Adapter(ArrayList<HashMap<String, Object>> _arr) {
			_data = _arr;
		}
		
		@Override
		public int getCount() {
			return _data.size();
		}
		
		@Override
		public HashMap<String, Object> getItem(int _index) {
			return _data.get(_index);
		}
		
		@Override
		public long getItemId(int _index) {
			return _index;
		}
		
		@Override
		public View getView(final int _position, View _v, ViewGroup _container) {
			LayoutInflater _inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View _view = _v;
			_view = _inflater.inflate(R.layout.counter_list_item, null);
			final LinearLayout linear1 = _view.findViewById(R.id.linear1);
						final LinearLayout linear2 = _view.findViewById(R.id.linear2);
						final LinearLayout linear3 = _view.findViewById(R.id.linear3);
						final TextView name_view = _view.findViewById(R.id.name_view);
						final Switch notification_indicator = _view.findViewById(R.id.notification_indicator);
						final TextView date_view = _view.findViewById(R.id.date_view);
						final ImageView delete_button = _view.findViewById(R.id.delete_button);
						
			try {
				name_view.setText(counters.get((int)_position).get("name").toString());
				date_view.setText(counters.get((int)_position).get("date_string").toString());
				notification_indicator.setText(getResources().getString(R.string.show_notification_label));
				notification_indicator.setChecked(Boolean.parseBoolean(counters.get(_position).get("has_notification").toString()));
				delete_button.setOnLongClickListener(new View.OnLongClickListener() {
					@Override
					public boolean onLongClick(View _view) {
						SketchwareUtil.showMessage(getApplicationContext(), getResources().getString(R.string.delete_counter_label));
						return true;
					}
				});
				delete_button.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View _view) {
						String id = counters.get(_position).get("id").toString();
						CounterManager.getInstance().deleteCounter(id);
						_refresh();
					}
				});
				notification_indicator.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton cb, boolean isChecked) {
						String id = counters.get(_position).get("id").toString();
						if (isChecked) {
							CounterManager.getInstance().addNotification(id);
							Util.startService(getApplicationContext());
						}
						else {
							CounterManager.getInstance().removeNotification(id);
						}
						counters = CounterManager.getInstance().getCountersData();
						notification_indicator.setChecked(Boolean.parseBoolean(counters.get(_position).get("has_notification").toString()));
					}});
			} catch (Exception e) { Util.log(e.getMessage());}
			
			return _view;
		}
	}
}