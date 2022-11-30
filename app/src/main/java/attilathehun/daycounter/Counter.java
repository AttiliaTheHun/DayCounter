package attilathehun.daycounter;

import java.util.Calendar;

import android.app.Application;
import android.app.Activity;
import android.content.Context;


import attilathehun.daycounter.MainActivity;
import attilathehun.daycounter.FileUtil;
import attilathehun.daycounter.Util;


/**
* This class is responsible for the actual counting process
*/
public class Counter{
	
	private static final int[] DAYS_IN_MONTHS = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    private static final int DAYS_IN_YEAR = 365;
    private static Calendar calendar = Calendar.getInstance();
	private static int targetDay = 0, targetMonth = 0, targetYear = 0;
	
	/**
	* Returns the number of days remaining until the date.
	* The date is retrieved from the data file ocassionally, but the method stores them
	* in memory to make it easier for me to code.
	* Note that the number may be negative.
	* @return number of the remaining days
	*/
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
		   //for some reason I don't mean to bother with it gives out a count decreased by 1
		   return daysRemaining + 1;
		   
    }
		
		/**
		* Returns the total number of days in the given number of years
		* @param int number of the years
		* @return number of the days
		*/
	private static int getDaysInYears(int years){
		return years * DAYS_IN_YEAR;
	}
	
	/**
	* Wrapper for Calendar#get() with field filled in
	* @return current year as integer
	*/
	private static int getCurrentYear(){
		return calendar.get(Calendar.YEAR);
	}
	
	/**
	* Wrapper for Calendar#get() with field filled in
	* @return position of the current day in the year
	*/
	private static int getCurrentDayOfYear(){
		return calendar.get(Calendar.DAY_OF_YEAR);
    }

  /**	
  * As Counter#getCurrentDayOfYear() returns position of a day within a year,
  * this calculates the position for our targetDay
  * @return position of targetDay in the year
  */
	private static int calculateTargetDayOfYear(){
		int dayNumber = targetDay;
		for(int monthNumber = 0; monthNumber < targetMonth; monthNumber++){
			dayNumber += DAYS_IN_MONTHS[monthNumber];
		}
		return dayNumber;
  }
	
	/**
	* Loads date date from the data file and stores them inside the memory
	*/
	private static void initData() {
		int[] data = Util.getData();
		targetDay = data[0];
		targetMonth = data[1];
		targetYear = data[2];
	}
	
	/**
	* Renews the Calendar instance, for the physical date may change whilst the app is running
	* and then the Calendar would still return output for the date it was crrated on, resulting in bad
	* things happenning
	*/
	public static void refresh() {
	    Util.log("Refreshed the calendar and data");
	    Counter.calendar = Calendar.getInstance();
	    initData();
	}
	
}