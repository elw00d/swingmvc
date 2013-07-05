package swingmvc.core;

import binding.BindingGroup;
import binding.IPropertyChangedListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Base class for all UI controllers.
 * Connects view and model and manages them.
 * Supports commands and data change notifications.
 * @author elwood
 */
public class Controller<TView, TViewModel extends ViewModel> implements IPropertyChangedListener {
    protected TView view;
    private TViewModel model;
    private BindingGroup bindings;
    
    private boolean initialized = false;
    
    private Map<String, List<Pair<JButton, ActionListener>>> buttonsBound = new HashMap<String, List<Pair<JButton, ActionListener>>>();
    private Map<String, MethodCommand> commands = new HashMap<String, MethodCommand> () ;
    
    /**
     * Called internally from modules.
     */
    void initialize(TView view) {
        this.view = view;
        initializeCommands();
        initializeBindings();
        subscribeToClose();
        initialized = true;
        onInitialized();
    }

    private class CloseListener extends WindowAdapter {
        private boolean unloaded = false;
        // WINDOW_CLOSED event is raised only if Window's default close operation is set to DISPOSE_ON_CLOSE
        public void windowClosed( WindowEvent e ) {
            // this event may be raised twice because of bug in SWT (fixed in java 8 last versions only)
            // so we use flag to avoid duplicate call of unload event
            // for additional information see http://www.mail-archive.com/swing-dev@openjdk.java.net/msg02625.html
            if (unloaded) return;
            try {
                e.getWindow().removeWindowListener( this );
                onUnload();
            } finally {
                unloaded = true;
            }
        }
    }

    /**
     * Called when view (if it is a Window) or its root parent (Window) is closed.
     * Note that windows with default close operation = DO_NOTHING_ON_CLOSE, HIDE_ON_CLOSE and EXIT_ON_CLOSE will not
     * raise WindowClosed event, and unload method won't be called.
     */
    protected void onUnload() {
    }

    private void subscribeToClose() {
        final CloseListener closeListener = new CloseListener();
        if (view instanceof Window ) {
            ((Window) view ).addWindowListener(closeListener );
        } else {
            // setup the hierarchy listener that will wait the moment of attaching view to Window
            ((JComponent) view).addHierarchyListener( new HierarchyListener() {
                public void hierarchyChanged( HierarchyEvent e ) {
                    //
                    if (e.getChangeFlags() == HierarchyEvent.PARENT_CHANGED) {
                        Window root;
                        if (e.getChangedParent() instanceof Window) {
                            root = (Window) e.getChangedParent();
                        } else {
                            root = SwingUtilities.windowForComponent(e.getChangedParent());
                        }
                        if (null != root) {
                            root.addWindowListener(closeListener);
                            e.getComponent().removeHierarchyListener(this);
                        }
                    }
                }
            } );
        }
    }

    private void initializeBindings() {
        bindings = new BindingGroup(  );
        setupBindings( bindings );
        if (model != null) {
            bindings.setSource( model );
            bindings.bind();
        }
    }
    
    /**
     * Called when view is created and controller was initialized
     * (view is available as field, commands are loaded).
     */
    protected void onInitialized() {
    }

    /**
     * Allows set data binding rules to view model instance.
     * If view model instance will change, Controller will rebind it automatically.
     */
    protected void setupBindings( BindingGroup bindings) {
    }

    /**
     * Called when someone uses {@link #setModel(ViewModel)} method.
     * @param oldModel Old model instance or null if there was no model
     * @param model New model instance
     */
    protected void onModelChanged( TViewModel oldModel, TViewModel model) {
    }
    
    /**
     * Called when model signals about property change.
     * @param propertyName Name of affected property
     */
    protected void onModelPropertyChanged(String propertyName) {
    }
    
    /**
     * Determines command availability status.
     * Called before command executing and
     * when someone calls {@link #refreshCanExecuteCommand(String)}
     * 
     * @param commandName Name of command to check
     * @return true if command can be executed, otherwise false
     */
    protected boolean canExecuteCommand( String commandName ) {
        // return false by default
        return false;
    }
    
    /**
     * Updates all commands availability status.
     * Automatically sets buttons enabled property for buttons bound to commands.
     */
    public void refreshCanExecuteCommands() {
        if (!initialized) throw new IllegalArgumentException( "Controller is not initialized yet" );
        for (String commandName : commands.keySet() ) {
            refreshCanExecuteCommand(commandName);
        }
    }
    
    /**
     * Updates command availability status.
     * Automatically sets buttons enabled property for buttons bound to command.
     * @param commandName Name of command
     */
    public void refreshCanExecuteCommand( String commandName ) {
        if (!initialized) throw new IllegalArgumentException( "Controller is not initialized yet" );
        if (buttonsBound.containsKey(commandName)) {
            List<Pair<JButton, ActionListener>> buttons = buttonsBound.get(commandName);
            boolean canExecute = canExecuteCommand(commandName);
            for (Pair<JButton, ActionListener> buttonPair : buttons)
                buttonPair.first.setEnabled(canExecute);
        }
    }
    
    /**
     * Returns view model instance linked to.
     */
    public TViewModel getModel() {
        return model;
    }

    /**
     * Replaces the current view model instance by the specified one.
     * Calls {@link #onModelChanged(ViewModel, ViewModel)} method after operation.
     * Automatically calls {@link #refreshCanExecuteCommands()} also.
     * 
     * @param model Reference to new view model instance.
     */
    public void setModel( TViewModel model) {
        if (null == model) throw new IllegalArgumentException("model is null");
        
        if (this.model != model) {
            TViewModel oldModel = this.model;
            
            if (oldModel != null) {
                oldModel.removePropertyChangedListener(this);
                if (initialized) {
                    bindings.unbind();
                }
            }
            this.model = model;
            model.addPropertyChangedListener(this);
            if (initialized) {
                bindings.setSource( model );
                bindings.bind();
            }
            
            onModelChanged(oldModel, model);

            if (initialized) {
                refreshCanExecuteCommands();
            }
        }
    }

    /**
     * Connects the button to specified command.
     * After this command canExecute will be synchronized with button's enabled property.
     * You can bind several buttons to one command.
     * @param button 
     * @param commandName 
     */
    protected void bindButtonToCommand( JButton button, final String commandName ) {
        if (!initialized) throw new IllegalArgumentException( "Commands are not initialized yet" );
        if (!commands.containsKey(commandName)) throw new RuntimeException( String.format( "Command %s not found", commandName ));
        final ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getCommandByName(commandName).execute();
            }
        };
        button.addActionListener(actionListener);
        //
        if (!buttonsBound.containsKey(commandName))
            buttonsBound.put(commandName, new ArrayList<Pair<JButton, ActionListener>>());
        buttonsBound.get(commandName).add(new Pair<JButton, ActionListener>(button, actionListener));
        //
        refreshCanExecuteCommand(commandName);
    }
    
    /**
     * Removes connection between command and button.
     * @param button
     * @param commandName 
     */
    protected void unbindButtonFromCommand( JButton button, String commandName ) {
        if (!initialized) throw new IllegalArgumentException( "Commands are not initialized yet" );
        List<Pair<JButton, ActionListener>> buttons = buttonsBound.get(commandName);
        if (null == buttons) throw new RuntimeException( String.format("Command %s is not bound to any button", commandName));
        Pair<JButton, ActionListener> pairToDelete = null;
        for (Pair<JButton, ActionListener> buttonPair: buttons) {
            if (buttonPair.first == button) {
                pairToDelete = buttonPair;
                break;
            }
        }
        if (null == pairToDelete) throw new RuntimeException(String.format("Command %s is not bound to specified button", commandName));
        button.removeActionListener(pairToDelete.second);
        buttons.remove(pairToDelete);
        if (buttons.isEmpty())
            buttonsBound.remove(commandName);
    }
    
    /**
     * Returns command for specified name. Can be used from another controllers.
     * @throws IllegalArgumentException If commandName is null or empty
     * @throws RuntimeException If specified command not found
     */
    public ICommand getCommandByName( String commandName ) {
        if (!initialized) throw new IllegalArgumentException( "Commands are not initialized yet" );
        if ( null == commandName || commandName.isEmpty()) throw new IllegalArgumentException( "commandName is null or empty");
        ICommand cmd = commands.get( commandName );
        if ( null == cmd ) throw new RuntimeException( String.format( "Command %s not found", commandName) );
        return cmd;
    }

    @Override
    public void propertyChanged(String propertyName) {
        onModelPropertyChanged(propertyName );
    }
    
    private static class Pair<F, S> {
        F first;
        S second;

        Pair(F first, S second) {
            this.first = first;
            this.second = second;
        }
                
    }
    
    /**
     * Scans current class for methods annotated with {@link Command} attribute
     * and converts them to ICommand instances.
     */
    private void initializeCommands() {
        Method[] methods = this.getClass().getDeclaredMethods();
        for ( Method method : methods) {
            Command attribute = method.getAnnotation(Command.class);
            if (null != attribute) {
                String name = attribute.value();
                if ( null == name || name.isEmpty() )
                    throw new RuntimeException( String.format( "commandName on method %s is null or empty", method.getName()) );
                method.setAccessible(true);
                commands.put(name, new MethodCommand(name, method));
            }
        }
    }
    
    private class MethodCommand implements ICommand {

        String name;
        Method method;
        
        public MethodCommand( String name, Method method) {
            this.name = name;
            this.method = method;
        }
        
        @Override
        public boolean canExecute() {
            return Controller.this.canExecuteCommand(name);
        }

        @Override
        public void execute() {
            if ( canExecute() ) {
                try {                    
                    method.invoke(Controller.this);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException (e);
                } catch (IllegalArgumentException e) {
                    throw new RuntimeException (e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException (e);
                }
            }
        }
        
    }
}
