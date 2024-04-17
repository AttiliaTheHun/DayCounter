package attilathehun.daycounter;
 
import androidx.annotation.NonNull;
 
import android.content.Context;
 
/**
 * Provides a communication channel to entities that need to be notified when system locale (language) changes.
 */
public interface LocaleChangedListener {
    
    /**
     * Should be emitted when the system locale changes so the UI can be refreshed with the current locale strings.
     *
     * @param context the context (not null)
     */
    public void onLocaleChanged(@NonNull Context context);
}
 