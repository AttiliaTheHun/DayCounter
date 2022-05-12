package attilathehun.daycounter;

import java.io.IOException;
import java.io.File;
import java.io.FileWriter;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.Intent;
import androidx.core.content.FileProvider;

import android.net.Uri;
import android.app.Activity;

import android.os.Build;

import attilathehun.daycounter.FileUtil;
import attilathehun.daycounter.ServiceLauncher;
import attilathehun.daycounter.NotificationService;

/**
* A collection of methods that are crucial for other classes yet belong in none of them
*/
public class Util {
        
	private static Context context = null;
	private static SharedPreferences file;
		
		/**
		* @return stringified path of the log file
		*/
	private static String getLogPath() {
	        return FileUtil.getExternalStorageDir() + "/DayCounterLog.txt";
	}
	
	/**
	* Logs a message inside the log file
	*/
	public static void log(String message) {
		appendFile(getLogPath(), message + "\n");
	}
	
	/**
	* Empties the log file
	*/
	public static void clearLog() {
		FileUtil.writeFile(Util.getLogPath(), "");
	}
	
	/**
	* Attemps to open the log file in some kind of file viewing app the user has installed,
	* crashes on my friend's phone though
	* @param Context context to use for the action
	*/
	public static void viewLog(Context context) {
		try {
	    String authority = "attilathehun.daycounter.fileprovider";
		Intent intent = new Intent();
		File logFile = new File(Util.getLogPath());
		Uri uri =  FileProvider.getUriForFile(context, authority, logFile);
		intent.setAction(Intent.ACTION_VIEW);
		intent.setDataAndType(uri, "text/plain");
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		//Util.log("viewLog(): " + uri.toString());
		context.startActivity(intent);
		} catch (Exception e) {
		  Util.log(e.toString());
		}
	}
	
	/**
	* A version of Util#viewLog(context) that uses the default context
	*/
	public static void viewLog() {
		Util.viewLog(Util.getContext());
	}
	
	/**
	* Creates a new file on the specified path
	* I think I stole this from Sketchware's FileUtil.java
	* @param path path the create the file at
	*/
    private static void createNewFile(String path) {
        int lastSep = path.lastIndexOf(File.separator);
        if (lastSep > 0) {
            String dirPath = path.substring(0, lastSep);
            FileUtil.makeDir(dirPath);
        }

        File file = new File(path);

        try {
            if (!file.exists())
                file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
	/**
	* Attaches a String to the file's content
	* I think I stole this from Sketchware' FileUtil.java
	* @param path path of the file
	* @str the text to attach
	*/
    private static void appendFile(String path, String str) {
        createNewFile(path);
        FileWriter fileWriter = null;

        try {
            fileWriter = new FileWriter(new File(path), true);
            fileWriter.write(str);
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileWriter != null)
                    fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
	
	 /**
	 * Sets the default context, allowing the use of contextless methods
	 * @param context the context object to use as default
	 */
    public static void setContext(Context context) {
		Util.context = context;
    }
	
	/**
	* A version of Util#setContext(context) to use when expecting to have a default
	* context set or if we have set default a superior one
	*/
	public static void setContextIfNull(Context context) {
		if(Util.getContext() == null) {
			Util.setContext(context);
		}	
	}
	
	/**
	* Returns the default context
	* @return default context
	*/
	public static Context getContext() {
		return Util.context;
	}
		
		/**
		* Reads cataloged data from the data file, returning them as integer array
		* @return array with the saved data in order I had in my mind when first creating this app
		*/
    public static int[] getData() {
		try{
	      file = Util.getContext().getSharedPreferences("data", Activity.MODE_PRIVATE);
          int targetDay = file.getInt("targetDay", 0);
          int targetMonth = file.getInt("targetMonth", 0);      
	      int targetYear = file.getInt("targetYear", 0);
		  int targetAge = file.getInt("targetAge", 0);
		  int[] data = {targetDay, targetMonth, targetYear, targetAge};
		  return data;
		}catch(Exception e){
		    Util.log(e.toString());
		    throw new RuntimeException(e.getMessage());
		}
	}
	
	/**
	* Reads a specific String from the data file
	* @param key key for the target value
	* @return value for the target key
	*/
	  public static String getData(String key) {
	      try {
	        file = Util.getContext().getSharedPreferences("data", Activity.MODE_PRIVATE);  
	          String value = file.getString(key, "");
	          return value;
	          
	      } catch (Exception e) {
	        Util.log(e.toString());
	        throw new RuntimeException(e.getMessage());
	      }     
	  }
	
	/**
	* Clears the cataloged data ftom the data file
	*/
    public static void resetData() {
	  file.edit().remove("counterExists").commit();
      file.edit().remove("targetDay").commit();
      file.edit().remove("targetMonth").commit();
      file.edit().remove("targetYear").commit();
      file.edit().remove("targetAge").commit();
    }
	
	/**
	* Starts the notification service, hopefully resulting in the notification appearing
	* @param context a context to use
	*/
	public static void startService(Context context) {
		 Util.log("startService()");
		 Util.log("enableNotification: " + Util.getData("enableNotification"));
		 if(!Util.getData("enableNotification").equals("true")) {
		     Util.log("Prevented starting service due to user preferences");
		     return;
		 }
	     Intent intent = new Intent(context, NotificationService.class);
		 if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			    Util.log("Starting as a foreground service");
                context.startForegroundService(intent);
         } else {
                context.startService(intent);
         }		
	}
	
	/**
	* A contextless version of Util#startService(context) which uses the default context
	*/
	public static void startService() {
		Util.startService(Util.getContext());
	}
	
}