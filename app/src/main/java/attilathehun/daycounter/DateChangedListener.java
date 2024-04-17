package attilathehun.daycounter;
 
import androidx.annotation.NonNull;
 
import android.content.Context;
 
/**
 * Provides a communication channel to entities that need to be notified when date changes.
 */
public interface DateChangedListener {
    /**
     * Guess what?
     *
     * @param context the context (not null)
     */
    public void onDateChanged(@NonNull Context context);
}