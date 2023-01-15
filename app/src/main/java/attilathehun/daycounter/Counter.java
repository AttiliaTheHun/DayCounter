package attilathehun.daycounter;
 
import java.util.Calendar;
import java.util.List;
import java.util.ArrayList;
import java.io.Serializable;
import java.time.Year;
 
import android.content.Context;
 
import attilathehun.daycounter.Util;
import attilathehun.daycounter.CounterEventListener;
 
/**
 * This class is responsible for the counting process. Instances of this class represent the actual counters.
 */
public class Counter implements Serializable {
 
    private static List<CounterEventListener> listeners = new ArrayList<CounterEventListener>();
 
    private static final int[] DAYS_IN_MONTHS = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
 
    private static final int FEBRUARY = 1;
 
    private static final int DAYS_IN_YEAR = 365;
 
    private static Calendar calendar = Calendar.getInstance();
 
    private int targetDay = 0, targetMonth = 0, targetYear = 0, targetAge = 0;
 
    private String id;
 
    private String name = "N/A";
 
    private boolean hasNotification = false;
 
    private int widgetId = -1; //No bound widget by default
 
    private transient Context context = null;
 
    /**
     * Prefered Counter constructor.
     *
     * @param id          id of the Counter
     * @param name        display name of the Counter
     * @param targetDay   Counter target day
     * @param targetMonth Counter target month
     * @param targetYear  Counter target year
     * @param targetAge   Counter target age
     */
    private Counter(String id, String name, int targetDay, int targetMonth, int targetYear, int targetAge) {
 
        this.id = id;
 
        this.name = name;
 
        this.targetDay = targetDay;
 
        this.targetMonth = targetMonth;
 
        this.targetYear = targetYear;
 
        this.targetAge = targetAge;
 
    }
 
    /**
     * Nameless Counter constructor, such Counters are displaying the default name.
     *
     * @param id          id of the Counter
     * @param name        display name of the Counter
     * @param targetDay   Counter target day
     * @param targetMonth Counter target month
     * @param targetYear  Counter target year
     * @param targetAge   Counter target age
     */
    private Counter(String id, int targetDay, int targetMonth, int targetYear, int targetAge) {
 
        this.id = id;
 
        this.targetDay = targetDay;
 
        this.targetMonth = targetMonth;
 
        this.targetYear = targetYear;
 
        this.targetAge = targetAge;
 
    }
 
    /**
     * A propertyless Counter could cause severe crashes, thus we forbid it. Do not use!
     */
    private Counter() {
    }
 
    /**
     * Official way of getting Counter instances, that deals with nameless Counters and performs parameter-checking.
     *
     * @param id          id of the Counter
     * @param name        display name of the Counter
     * @param targetDay   Counter target day
     * @param targetMonth Counter target month
     * @param targetYear  Counter target year
     * @param targetAge   Counter target age
     * @return A safe-to-use Counter object, null if the parameters are invalid.
     */
    public static Counter create(String id, String name, int targetDay, int targetMonth, int targetYear, int targetAge) {
 
        if (id == null || targetDay < 0 || targetMonth < 0 || targetYear < 0 || targetAge < 0) {
 
            return null;
        }
 
        if (name.equals("")) {
 
            return new Counter(id, targetDay, targetMonth, targetYear, targetAge);
        }
 
        return new Counter(id, name, targetDay, targetMonth, targetYear, targetAge);
    }
 
    /**
     * Returns the number of days remaining until the date of this particular counter. It is advised to use the Util#getDaysRemaining() wrapper, which supports translation.
     * Note that the number may be negative.
     *
     * @return number of the remaining days of the Counter
     */
    public int getDaysRemaining() {
 
        int daysRemaining = 0;
 
        final int currentDayOfYear = getCurrentDayOfYear();
 
        final int targetDayOfYear = calculateTargetDayOfYear();
 
        final int dayDifference = currentDayOfYear - targetDayOfYear;
 
        int yearsRemaining = targetYear - getCurrentYear();
 
        if (dayDifference > 0) {
 
            daysRemaining = DAYS_IN_YEAR - currentDayOfYear + targetDayOfYear;
 
            yearsRemaining -= 1;
 
        } else {
 
            daysRemaining = targetDayOfYear - currentDayOfYear;
 
        }
 
        daysRemaining += getDaysInYears(yearsRemaining);
 
        daysRemaining += getLeapYears();
 
        //We want today to be counted, because it is never too late! :)
 
        return daysRemaining + 1;
 
    }
 
    /**
     * Calculates the number of leap years between current date and target date of the Counter, because for every leap year we need to add a day to the total day count.
     *
     * @return number of leap years in the Counter's range
     */
    private int getLeapYears() {
 
        int leapYears = 0;
 
        int firstLeapYear = -1;
 
        final int currentYear = calendar.get(Calendar.YEAR);
 
        boolean lowerInclusive = false, upperInclusive = false;
 
        // If current year is leap but it is past february, the extra day is not in the Counter's range, because the range starts afterwards.
 
        if (Year.isLeap(currentYear) && calendar.get(Calendar.MONTH) > FEBRUARY) {
 
            lowerInclusive = true;
 
        }
 
        // If the target year is leap but target month is not later than february, the range of the Counter ends before it reaches the extra day for it is the last day of february.
 
        if (Year.isLeap(targetYear) && targetMonth > FEBRUARY) {
 
            upperInclusive = true;
 
        }
 
        // Leap year is every fourth year, so we need to find the first leap year in the interval so we can work with it later.
 
        for (int i = 0; i < 4; i++) {
 
            if (Year.isLeap(currentYear + i)) {
 
                firstLeapYear = currentYear + i;
                break;
            }
        }
 
        // If there is no leap year in the interval, return 0.
 
        if (firstLeapYear == -1) {
 
            return 0;
        }
 
        int firstYear = firstLeapYear, lastYear = targetYear;
 
        // If current year is leap but it is past the extra day, we skip to the next leap year in the range.
 
        if (firstYear == currentYear && !lowerInclusive) {
 
            firstYear += 4;
        }
 
        // If target year is leap but the extra day is not within the range, we must not count this year.
 
        if (lastYear == targetYear && !upperInclusive) {
 
            lastYear -= 1;
        }
 
        // Now every fourth year in the interval between the first leap year of the range and the target year is leap.
 
        leapYears = Math.abs(lastYear - firstYear) / 4;
 
        // However it is not every fourth, it is rather the first, the fifth, the ninth ... . Thus there might be a leap year that is not followed by three ordinary years
 
        // and so the formula above ommited it. We check if there is a quadruplet began at the end of the interval.
 
        if (Math.abs(lastYear - firstYear) % 4 > 0) {
 
            leapYears++;
        }
 
        return (leapYears > 0) ? leapYears : 0;
    }
 
    /**
     * Returns the total number of days in the given number of years. Note that this method works with the fixed number of days of 365 so you need to user #getLeapYears()
     * to fill in the count.
     *
     * @param int number of the years
     * @return number of the days
     */
    private static int getDaysInYears(int years) {
 
        return years * DAYS_IN_YEAR;
    }
 
    /**
     * Wrapper for Calendar#get() for specific field.
     *
     * @return current year as integer
     */
    private static int getCurrentYear() {
 
        return calendar.get(Calendar.YEAR);
    }
 
    /**
     * Wrapper for Calendar#get() for specific field.
     *
     * @return position of the current day in the year
     */
    private static int getCurrentDayOfYear() {
 
        // This gets dealt with in #getLeapYears()
 
        if (Year.isLeap(calendar.get(Calendar.YEAR)) && calendar.get(Calendar.MONTH) > FEBRUARY) {
 
            return calendar.get(Calendar.DAY_OF_YEAR) - 1;
        }
 
        return calendar.get(Calendar.DAY_OF_YEAR);
    }
 
    /**
     * As Counter#getCurrentDayOfYear() returns position of the current day within the current year, this calculates the position of the target day in the target year.
     *
     * @return position of targetDay in the year
     */
    private int calculateTargetDayOfYear() {
 
        int dayNumber = targetDay;
 
        for (int monthNumber = 0; monthNumber < targetMonth; monthNumber++) {
 
            dayNumber += DAYS_IN_MONTHS[monthNumber];
        }
 
        // Check for target year being a leap year and add the extra day eventually
 
        if (Year.isLeap(targetYear) && targetMonth > FEBRUARY) {
 
            dayNumber++;
        }
 
        return dayNumber;
    }
 
    /**
     * The Calendar instance is static and works with the date it was created at. This method renews the instance to use the current date.
     */
    public static void refresh() {
 
        Counter.calendar = Calendar.getInstance();
    }
 
    /**
     * Stringifies the data of the Counter in a predefined format DD/MM/YYYY (age).
     *
     * @return String of the target date and data of the Counter
     */
    public String getDateString() {
 
        return (targetDay + 1) + "/" + (targetMonth + 1) + "/" + targetYear + " (" + targetAge + ")";
    }
 
    public String getId() {
        return id;
    }
 
    public String getName() {
        return name;
    }
 
    /**
     * Changes the notification status of the counter to true and fires CounterEventListerner#onCounterNotificationStateChanged().
     */
    public void addNotification() {
 
        this.hasNotification = true;
        Counter.notifyCounterNotificationStateChanged(this);
    }
 
    /**
     * Changes the notification status of the counter to false and fires CounterEventListerner#onCounterNotificationStateChanged().
     */
    public void removeNotification() {
 
        this.hasNotification = false;
        Counter.notifyCounterNotificationStateChanged(this);
 
    }
 
    public boolean hasNotification() {
 
        return this.hasNotification;
    }
 
    public int getWidgetId() {
 
        return this.widgetId;
    }
 
    public boolean hasWidget() {
 
        return this.widgetId != -1;
    }
 
    public void bindWidget(int widgetId) {
 
        this.widgetId = widgetId;
    }
 
    public void unbindWidget() {
 
        this.widgetId = -1;
    }
 
 
    /**
     * Sends the CounterEventListener#onCounterNotificationStateChanged() event to all registered listeners.
     *
     * @param counter counter whose notification state has been changed
     */
    private static void notifyCounterNotificationStateChanged(Counter counter) {
        for (CounterEventListener listener : Counter.listeners) {
            listener.onCounterNotificationStateChanged(counter);
        }
    }
 
 
    /**
     * This method registers a listener for coutner events.
     *
     * @param listener the entity to be registred
     */
    public static void addEventListener(CounterEventListener listener) {
        Counter.listeners.add(listener);
    }
 
    /**
     * Availabilifies the listener list for classes with custom counter events.
     *
     * @return list of the listeners
     */
    public static List<CounterEventListener> getEventListeners() {
        return Counter.listeners;
    }
 
    public void inject(Context context) {
        this.context = context;
    }
 
    public Context withdraw() {
        return this.context;
    }
 
}
 