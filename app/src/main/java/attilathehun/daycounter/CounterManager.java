package attilathehun.daycounter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Intent;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import attilathehun.daycounter.Util;
import attilathehun.daycounter.Counter;

/**
 * This class manages a collection of the Counter objects, that represent the actual counters. It is the central class through all manipulation of the
 * counters data is done.
 */
public class CounterManager {

    public static final String DEFAULT_EXPORT_PATH = "/storage/emulated/0/counters";
    public static String SAVE_FILE_PATH = Util.getContext().getFilesDir() + "/data";

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
     * @return true if successful, false if no counter has been created
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
            File dataFile = new File(SAVE_FILE_PATH);
            dataFile.createNewFile();
            FileOutputStream dataFileOutputStream = new FileOutputStream(dataFile, false);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(dataFileOutputStream);
            objectOutputStream.writeObject(counters);
            objectOutputStream.close();
            dataFileOutputStream.close();
            Util.log("Saved the data");
        } catch (Exception e) {
            Util.log("Can not save the data: " + e.getMessage());
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

        } catch (Exception e) {
            Util.log(e.getMessage());
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
     * Returns true if there is a Counter in the collection and false if there is not.
     *
     * @return true if at least one counter exists, false otherwise
     */
    public boolean counterExists() {
        return counters.size() > 0;
    }

    /**
     * Returns a list of those counters that are meant to have active notification.
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
     * Wipes the counter collection.
     */
    public void clearCounters() {
        Util.stopService(Util.getContext());
        this.counters.clear();
        save();
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
     * Use cases: widget deleted, wanting to troll the user
     *
     * @param counterId target counter id
     * @param widgetId  target widget id
     */
    public void unbindWidget(String counterId, int widgetId) {
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

    public boolean exportBytes(String path) {
        try {
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
            Util.log(e.getMessage());
            return false;
        }

    }

    public boolean exportBytesDefault() {
        return exportBytes(DEFAULT_EXPORT_PATH);
    }

    public boolean exportJSON(String path) {
        try {
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
            Util.log(e.getMessage());
            return false;
        }
    }

    public boolean exportJSONDefault() {
        return exportJSON(DEFAULT_EXPORT_PATH + ".json");
    }

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
            Util.log(e.getMessage());
            return false;
        }
    }

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
            Util.log(e.getMessage());
            return false;
        }
    }

}