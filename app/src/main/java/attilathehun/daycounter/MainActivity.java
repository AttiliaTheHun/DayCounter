package attilathehun.daycounter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.*;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import java.util.HashMap;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.content.Intent;
import android.net.Uri;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.AdapterView;
import android.view.View;
import com.google.gson.Gson;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.DialogFragment;
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
				
			}
		});
		
		listview1.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> _param1, View _param2, int _param3, long _param4) {
				final int _position = _param3;
				
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
		_init();
		
		if (!CounterManager.getInstance().counterExists()) {
			intent.setClass(getApplicationContext(), CreateCounterActivity.class);
			startActivity(intent);
			finish();
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
	public void _init() {
		Util.setContext(getApplicationContext());
		Util.startServiceIfNotRunning();
		_refresh();
		useless = new Gson().toJson(counters);
		//Util.log(useless);
	}
	public static void addListener(CounterEventListener listener) {
		MainActivity.listeners.add(listener);
	}
	
	private static void notifyCounterNotificationStateChanged(Counter counter) {
		for (CounterEventListener listener : MainActivity.listeners) {
			listener.onCounterNotificationStateChanged(counter);
		}
	}
	private static void notifyCounterRemoved(Counter counter) {
		for (CounterEventListener listener : MainActivity.listeners) {
			listener.onCounterRemoved(counter);
		}
	}
	
	
	public void _refresh() {
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
LayoutInflater _inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
				notification_indicator.setText(Util.getContext().getResources().getString(R.string.show_notification_label));
				notification_indicator.setChecked(Boolean.parseBoolean(counters.get(_position).get("has_notification").toString()));
				delete_button.setOnLongClickListener(new View.OnLongClickListener() {
					@Override
					public boolean onLongClick(View _view) {
						SketchwareUtil.showMessage(getApplicationContext(), "getApplicationContext().getResource().getString(R.string.delete_counter_label);");
						return true;
					}
				});
				delete_button.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View _view) {
						String id = counters.get(_position).get("id").toString();
						MainActivity.notifyCounterRemoved(CounterManager.getInstance().getCounterOfId(id));
						CounterManager.getInstance().deleteCounter(id);
						Util.refreshWidgets();
						_refresh();
					}
				});
				notification_indicator.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton cb, boolean isChecked) {
						String id = counters.get(_position).get("id").toString();
						if (isChecked) {
							CounterManager.getInstance().addNotification(id);
						}
						else {
							CounterManager.getInstance().removeNotification(id);
						}
						MainActivity.notifyCounterNotificationStateChanged(CounterManager.getInstance().getCounterOfId(id));
						_refresh();
						notification_indicator.setChecked(Boolean.parseBoolean(counters.get(_position).get("has_notification").toString()));
					}});
			} catch (Exception e) { Util.log(e.getMessage());}
			
			return _view;
		}
	}
}