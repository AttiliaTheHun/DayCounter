package attilathehun.daycounter;

/**
* An interface to be implemented by classes, that need to refresh their data/output
* when today becomes yesterday
*/
public interface DateChangedListener {
        public void onDateChanged();
}