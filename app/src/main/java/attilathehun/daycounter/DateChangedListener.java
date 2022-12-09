package attilathehun.daycounter;
 
/**
* Communication channel between classes that are aware of date changing and classes that need to be aware of date changing.
*/
public interface DateChangedListener {
        public void onDateChanged();
}