package swingmvc.core;

import binding.INotifyPropertyChanged;
import binding.IPropertyChangedListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Base class for view models.
 * @author elwood
 */
public class ViewModel implements INotifyPropertyChanged {
    
    protected void raisePropertyChange( String propName) {
        for ( IPropertyChangedListener listener : listeners ) {
            listener.propertyChanged( propName );
        }
    }

    private List<IPropertyChangedListener> listeners = new ArrayList<IPropertyChangedListener>(  );

    public void addPropertyChangedListener( IPropertyChangedListener listener ) {
        listeners.add( listener );
    }

    public void removePropertyChangedListener( IPropertyChangedListener listener ) {
        listeners.remove( listener );
    }

}
