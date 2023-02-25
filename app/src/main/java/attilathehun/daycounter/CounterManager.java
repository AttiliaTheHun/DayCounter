package attilathehun.daycounter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.FileNotFoundException;
import java.io.ObjectStreamException;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Intent;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import attilathehun.daycounter.Util;
import attilathehun.daycounter.Counter;
import attilathehun.daycounter.CounterEventListener;

/**
 * This class manages a collection of the Counter objects, that represent the actual counters. It is the central class through all access or manipulation to/of the
 * counters data is done.
 */
public class CounterManager {

    public static final String DEFAULT_EXPORT_PATH = "/storage/emulated/0/counters";
    public static final String SAVE_FILE_PATH = Util.getContext().getFilesDir() + "/data";
    public static final boolean AUTOMATIC_BACKUP = Util.DEBUG;

    private static CounterManager instance = new CounterManager();
    private ArrayList<Counter> counters = new ArrayList<Counter>();

    // Let this be a singleton!
    private CounterManager() {
        load();
    }

    /**
     * The official way to get to the manager instance.
     *
     * @return the manager reference
     */
    public static CounterManager getInstance() {
        return instance;
    }

    /**
     * Sends the CounterEventListener#onCounterRemoved() event to all registered listeners.
     *
     * @param counter the removed counter data
     */
    private static void notifyCounterRemoved(Counter counter) {
        counter.inject(Util.getContext());
        for (CounterEventListener listener : Counter.getEventListeners()) {
            listener.onCounterRemoved(counter);
        }
    }

    /**
     * Official way to add a new Counter to the collection with a parameter check. The counter is represented as data and turned into a Counter instance
     * by the method itself.
     *
     * @param name        display name of the Counter
     * @param targetDay   Counter target day
     * @param targetMonth Counter target month
     * @param targetYear  Counter target year
     * @param targetAge   Counter target age
     * @return true if successful, false if counter has not been created
     */
    //TODO: validate input, check for impossible dates
    public boolean addCounter(String name, int targetDay, int targetMonth, int targetYear, int targetAge) {
        // The id must be unique, but whole time millis are needlessly large to store, so it should be sufficient to join it with some randoms
        String id = String.valueOf(System.currentTimeMillis());
        id = String.valueOf(Util.random(0, 15)) + id.substring(id.length() - 5, id.length());
        // Performs a parameter check of its own and possibly returns null
        Counter temp = Counter.create(id, name, targetDay, targetMonth, targetYear, targetAge);

        if (temp == null) {
            return false;
        }

        counters.add(temp);
        save();
        return true;
    }

    /**
     * Official way of removing Counters.
     *
     * @param id target Counter id
     */
    public void deleteCounter(String id) {
        for (int i = 0; i < counters.size(); i++) {
            if (counters.get(i).getId().equals(id)) {
                Counter counter = counters.get(i);
                counters.remove(i);
                CounterManager.notifyCounterRemoved(counter);
                break;
            }
        }
        save();
    }

    /**
     * Saves the current state of the Counters collection to the app's private storage. The data is saved as a serialized byte stream.
     * It is important to call this method after every data manipulation to prevent data loss due to unexpected interruptions such as device turn offs.
     */
    public void save() {
        try {
            Util.log(new Gson().toJson(counters));
            File dataFile = new File(SAVE_FILE_PATH);
            dataFile.createNewFile();
            FileOutputStream dataFileOutputStream = new FileOutputStream(dataFile, false);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(dataFileOutputStream);
            objectOutputStream.writeObject(counters);
            objectOutputStream.close();
            dataFileOutputStream.close();
            Util.log("Saved the data");
            if (counters.size() != 0) {
                this.backup();
            }
        } catch (Exception e) {
            Util.log("Can not save the data: " + e.toString());
        }
    }

    /**
     * Loads the serialized data into memory. Returns if no save file is found.
     * It should be sufficient to call this method only once, at startup.
     */
    public void load() {
        try {
            File file = new File(SAVE_FILE_PATH);
            if (!file.exists()) {
                return;
            }
            FileInputStream dataFileInputStream = new FileInputStream(file);
            ObjectInputStream objectInputStream = new ObjectInputStream(dataFileInputStream);
            ArrayList<Counter> list = (ArrayList<Counter>) objectInputStream.readObject();
            objectInputStream.close();
            dataFileInputStream.close();
            counters = list;
            Util.log("Loaded list of size " + counters.size());
            //Util.log(new Gson().toJson(counters));

            //A safeguard to prevent data loss when updating (DEBUG mode only)
        } catch (ObjectStreamException o) {
            Util.log(o.getMessage());
            this.restore();
            Util.log("Restored list of size " + counters.size());
        } catch (Exception e) {
            Util.log(e.toString());
            return;
        }
    }

    /**
     * Creates a specially structured ArrayList of the counters. This format is used when displaying data in a ListView.
     *
     * @return ListView compliant collection of counters data
     */
    public ArrayList<HashMap<String, Object>> getCountersData() {
        ArrayList<HashMap<String, Object>> result = new ArrayList<HashMap<String, Object>>();
        for (Counter counter : counters) {
            HashMap<String, Object> temp = new HashMap<String, Object>();
            temp.put("id", counter.getId());
            temp.put("name", counter.getName());
            temp.put("date_string", counter.getDateString());
            temp.put("has_notification", String.valueOf(counter.hasNotification()));
            result.add(temp);
        }
        return result;
    }

    /**
     * Checks if at least one counter exists.
     *
     * @return true if at least one counter exists, false otherwise
     */
    public boolean counterExists() {
        return counters.size() > 0;
    }

    /**
     * Returns a list of those counters that are meant to have an active notification.
     *
     * @return a list of counters with notification
     */
    public ArrayList<Counter> getNotificationCounters() {
        ArrayList<Counter> result = new ArrayList<Counter>();
        for (Counter counter : counters) {
            if (counter.hasNotification()) {
                result.add(counter);
            }
        }
        return result;
    }

    /**
     * Returns a list of those counters that are bound to a widget.
     *
     * @return list of counters bound to a widget
     */
    public ArrayList<Counter> getWidgetCounters() {
        ArrayList<Counter> result = new ArrayList<Counter>();
        for (Counter counter : counters) {
            if (counter.hasWidget()) {
                result.add(counter);
            }
        }
        return result;
    }

    /**
     * Searches for the counter that is bound to the widget of given id.
     *
     * @param widgetId target widget id
     * @return Counter that is bound to target widget or null if not found
     */
    public Counter getWidgetCounterForId(int widgetId) {
        for (Counter counter : counters) {
            if (counter.getWidgetId() == widgetId) {
                return counter;
            }
        }
        return null;
    }

    /**
     * Returns the counters collection in the current state.
     *
     * @return the counter collection
     */
    public ArrayList<Counter> getCounters() {
        return counters;
    }

    /**
     * Wipes the counter collection. Stops the notification service and updates all widgets.
     */
    public void clearCounters() {
        Util.stopService(Util.getContext());
      /*  for (Counter c : counters) {
            c.removeNotification();
        }*/
        this.counters.clear();
        save();
        Util.refreshWidgets(Util.getContext());
        //Util.getContext().stopService(new Intent(Util.getContext(), NotificationService.class));
        Util.log("Cleared the data");
    }

    /**
     * Sets the notification status of the counter with the corresponding id to true.
     *
     * @param id target counter id
     */
    public void addNotification(String id) {
        for (int i = 0; i < counters.size(); i++) {
            if (counters.get(i).getId().equals(id)) {
                counters.get(i).addNotification();
                break;
            }
        }
        save();
    }

    /**
     * Sets the notification status of the counter with the corresponding id to false.
     *
     * @param id target counter id
     */
    public void removeNotification(String id) {
        for (int i = 0; i < counters.size(); i++) {
            if (counters.get(i).getId().equals(id)) {
                counters.get(i).removeNotification();
                break;
            }
        }
        save();
    }

    /**
     * Binds a physical homescreen widget to a counter whose data it will display.
     *
     * @param counterId target counter id
     * @param widgetId  target widget id
     */
    public void bindWidget(String counterId, int widgetId) {
        for (int i = 0; i < counters.size(); i++) {
            if (counters.get(i).getId().equals(counterId)) {
                Util.log("widget bound - " + "counterId: " + counterId + " widgetId: " + widgetId);
                counters.get(i).bindWidget(widgetId);
                break;
            }
        }
        save();
    }

    /**
     * Unbinds a physical homescreen widget from a counter.
     * Use cases: widget deleted, widget rebound, wanting to troll the user
     *
     * @param counterId target counter id
     */
    public void unbindWidget(String counterId) {
        for (int i = 0; i < counters.size(); i++) {
            if (counters.get(i).getId().equals(counterId)) {
                counters.get(i).unbindWidget();
                break;
            }
        }
        save();
    }

    /**
     * Voids widget status of the counter that is currently bound to the widget of given id.
     *
     * @param widgetId target widget id
     */
    public void unbindWidgetOfId(int widgetId) {
        for (int i = 0; i < counters.size(); i++) {
            if (counters.get(i).getWidgetId() == widgetId) {
                counters.get(i).unbindWidget();
                Util.log("widget unbound -  counterId: " + counters.get(i).getId());
                break;
            }
        }
        save();
    }

    /**
     * Finds the counter that is linked to the given id.
     *
     * @param counterId target counter id
     * @return corresponding counter representation, NOT a reference! or null if not found
     */
    public Counter getCounterOfId(String counterId) {
        for (int i = 0; i < counters.size(); i++) {
            if (counters.get(i).getId().equals(counterId)) {
                return counters.get(i);
            }
        }
        return null;
    }

    /**
     * Saves the serialized counter collection into a file at the specified path.
     *
     * @param path path of the export file
     * @return true if successful
     */
    public boolean exportBytes(String path) {
        try {
            if (counters.size() == 0) {
                SketchwareUtil.showMessage(Util.getContext(), Util.getContext().getString(R.string.no_counter_available));
                return false;
            }
            File dataFile = new File(SAVE_FILE_PATH);
            File exportFile = new File(path);
            exportFile.createNewFile();
            FileInputStream in = new FileInputStream(dataFile);
            FileOutputStream out = new FileOutputStream(exportFile, false);
            byte[] bytes = new byte[(int) dataFile.length()];
            in.read(bytes);
            out.write(bytes);
            in.close();
            out.close();
            Util.log("Exported to " + path);
            return true;
        } catch (Exception e) {
            SketchwareUtil.showMessage(Util.getContext(), e.getMessage());
            Util.log(e.toString());
            return false;
        }

    }

    /**
     * Saves the serialized counter collection into a file at the default path.
     *
     * @return true if success
     */
    public boolean exportBytesDefault() {
        return exportBytes(DEFAULT_EXPORT_PATH);
    }

    /**
     * Saves the counter collection into a file at the specified path in JSON format.
     *
     * @param path path of the export file
     * @return true if successful
     */
    public boolean exportJSON(String path) {
        try {
            if (counters.size() == 0) {
                SketchwareUtil.showMessage(Util.getContext(), Util.getContext().getString(R.string.no_counter_available));
                return false;
            }
            final String jsonData = new Gson().toJson(counters);
            File exportFile = new File(path);
            exportFile.createNewFile();
            FileOutputStream out = new FileOutputStream(exportFile, false);
            out.write(jsonData.getBytes("UTF-8"));
            out.close();
            Util.log("Exported to " + path);
            return true;
        } catch (Exception e) {
            SketchwareUtil.showMessage(Util.getContext(), e.getMessage());
            Util.log(e.toString());
            return false;
        }
    }

    /**
     * Saves the counter collection into a file at the defaultpath in JSON format.
     *
     * @return true if successful
     */
    public boolean exportJSONDefault() {
        return exportJSON(DEFAULT_EXPORT_PATH + ".json");
    }

    /**
     * Loads the serialized counter collection from a file at the specified path.
     *
     * @param path path of the import file
     * @return true if successful
     */
    public boolean importBytes(String path) {
        try {
            File file = new File(path);
            if (!file.exists()) {
                throw new FileNotFoundException("File not found: " + path);
            }
            FileInputStream dataFileInputStream = new FileInputStream(file);
            ObjectInputStream objectInputStream = new ObjectInputStream(dataFileInputStream);
            ArrayList<Counter> list = (ArrayList<Counter>) objectInputStream.readObject();
            objectInputStream.close();
            dataFileInputStream.close();
            counters = list;
            Util.log("Imported list of size " + counters.size() + " from " + path);
            save();
            return true;
        } catch (Exception e) {
            SketchwareUtil.showMessage(Util.getContext(), e.getMessage());
            Util.log(e.toString());
            return false;
        }
    }

    /**
     * Loads the counter collection from a file at the specified path in JSON format.
     *
     * @param path path of the import file
     * @return true if successful
     */
    public boolean importJSON(String path) {
        try {
            File importFile = new File(path);
            FileInputStream in = new FileInputStream(importFile);
            byte[] bytes = new byte[(int) importFile.length()];
            in.read(bytes);
            String input = new String(bytes);
            in.close();
            counters = new Gson().fromJson(input, new TypeToken<ArrayList<Counter>>() {
            }.getType());
            Util.log("Imported list of size " + counters.size() + " from " + path);
            save();
            return true;
        } catch (Exception e) {
            SketchwareUtil.showMessage(Util.getContext(), e.getMessage());
            Util.log(e.toString());
            return false;
        }
    }

    /**
     * Creates a new counter with identical user-given data.
     *
     * @param counter source counter data
     */
    public void duplicate(Counter counter) {
        this.addCounter(counter.getName(), counter.getDay(), counter.getMonth(), counter.getYear(), counter.getTargetAge());
        Util.log("Counter duplicated originalId - " + counter.getId() + " newId - " + this.counters.get(this.counters.size() - 1).getId());
    }

    /**
     * Modifies a counter's user-given data. Counter is represented as the data.
     *
     * @param id          id of the counter
     * @param name        new display name of the Counter
     * @param targetDay   new Counter target day
     * @param targetMonth new Counter target month
     * @param targetYear  new Counter target year
     * @param targetAge   new Counter target age
     * @return true if successful
     */
    public boolean editCounter(String id, String name, int targetDay, int targetMonth, int targetYear, int targetAge) {
        for (int i = 0; i < counters.size(); i++) {
            if (counters.get(i).getId().equals(id)) {
                final boolean result = counters.get(i).edit(Util.getContext(), name, targetDay, targetMonth, targetYear, targetAge);
                if (result) {
                    save();
                }
                return result;
            }
        }
        return false;
    }

    /**
     * Creates a side save file in the apps private storage in the JSON format that can be loaded when the Counter class has been updated to prevent
     * data loss. (DEBUG mode only)
     */
    private void backup() {
        if (AUTOMATIC_BACKUP) {
            this.exportJSON(CounterManager.SAVE_FILE_PATH + ".json");
        }
    }

    /**
     * Restores the side save file in the apps private storage in the JSON format. (DEBUG mode only)
     */
    private void restore() {
        if (AUTOMATIC_BACKUP) {
            this.importJSON(CounterManager.SAVE_FILE_PATH + ".json");
        }
    }

}