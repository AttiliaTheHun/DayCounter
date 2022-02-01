package attilathehun.daycounter;

import java.util.Calendar;

import android.app.Application;
import android.app.Activity;
import android.content.Context;


import attilathehun.daycounter.MainActivity;
import attilathehun.daycounter.FileUtil;
import attilathehun.daycounter.Util;

public class Counter{
	
	private static final int[] DAYS_IN_MONTHS = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    private static final int DAYS_IN_YEAR = 365;
    private static Calendar calendar = Calendar.getInstance();
	private static int targetDay = 0, targetMonth = 0, targetYear = 0;
	
	
	public static int getDaysRemaining(){	 
		Util.log("getDaysRemaining()");  
		   initData();
	       int daysRemaining = 0;
		   final int currentDayOfYear = getCurrentDayOfYear();
		   final int targetDayOfYear = calculateTargetDayOfYear();
		   final int dayDifference = currentDayOfYear - targetDayOfYear;
		   int yearsRemaining = targetYear - getCurrentYear();
		   if(dayDifference > 0){
			   daysRemaining = DAYS_IN_YEAR - currentDayOfYear + targetDayOfYear;
			   yearsRemaining -= 1;
		   }else{
		       daysRemaining = targetDayOfYear - currentDayOfYear;
		   }
		   daysRemaining += getDaysInYears(yearsRemaining);
		   return daysRemaining;
		   
    }
		
	private static int getDaysInYears(int years){
		return years * DAYS_IN_YEAR;
	}
	
	private static int getCurrentYear(){
		return calendar.get(Calendar.YEAR);
	}
	
	private static int getCurrentDayOfYear(){
		return calendar.get(Calendar.DAY_OF_YEAR);
    }
	
	private static int calculateTargetDayOfYear(){
		int dayNumber = targetDay;
		for(int monthNumber = 0; monthNumber < targetMonth; monthNumber++){
			dayNumber += DAYS_IN_MONTHS[monthNumber];
		}
		return dayNumber;
  }
	
	
	private static void initData() {
		int[] data = Util.getData();
		targetDay = data[0];
		targetMonth = data[1];
		targetYear = data[2];
	}
	
	public static void refresh() {
	    Util.log("Refreshed the calendar");
	    Counter.calendar = Calendar.getInstance();
	}
	
}