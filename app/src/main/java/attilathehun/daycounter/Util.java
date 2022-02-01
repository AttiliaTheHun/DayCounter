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

public class Util {
        
	private static Context context = null;
	private static SharedPreferences file;
		
		
	private static String getLogPath() {
	        return FileUtil.getExternalStorageDir() + "/DayCounterLog.txt";
	}
	
	public static void log(String message) {
		appendFile(getLogPath(), message + "\n");
	}
	
	public static void clearLog() {
		FileUtil.writeFile(Util.getLogPath(), "");
	}
	
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
	
	public static void viewLog() {
		Util.viewLog(Util.getContext());
	}
	
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
	
    public static void setContext(Context context) {
		Util.context = context;
    }
	
	
	public static void setContextIfNull(Context context) {
		if(Util.getContext() == null) {
			Util.setContext(context);
		}	
	}
	
	public static Context getContext() {
		return Util.context;
	}
		
		
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
	
    public static void resetData() {
	  file.edit().remove("counterExists").commit();
      file.edit().remove("targetDay").commit();
      file.edit().remove("targetMonth").commit();
      file.edit().remove("targetYear").commit();
      file.edit().remove("targetAge").commit();
    }
	
	
	public static void startService(Context context) {
		 Util.log("startService()");
	     Intent intent = new Intent(context, NotificationService.class);
		 if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			    Util.log("Starting as a foreground service");
                context.startForegroundService(intent);
         } else {
                context.startService(intent);
         }		
	}
	
	public static void startService() {
		Util.startService(Util.getContext());
	}
	
}