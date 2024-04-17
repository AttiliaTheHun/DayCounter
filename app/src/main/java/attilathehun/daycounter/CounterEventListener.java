package attilathehun.daycounter;
 
import attilathehun.daycounter.Counter;
 
/**
 * Provides a communication channel to entities that need to be notified about changes in the Counter industry.
 */
public interface CounterEventListener {
    /**
     * Should be emitted when a notification is either added or removed from/onto a Counter.
     *
     * @param counter concerned Counter representation, NOT a reference!
     */
    public void onCounterNotificationStateChanged(Counter counter);
 
    /**
     * Should be emitted when a Counter is destroyed.
     *
     * @param counter concerned Counter representation, NOT a reference!
     */
    public void onCounterRemoved(Counter counter);
 
	/**
	 * Should be emitted when a Counter data has been edited.
	 *
	 * @param counter concerned Counter representation, NOT a reference!
	 */
    public void onCounterEdited(Counter counter);
 
}
 
 