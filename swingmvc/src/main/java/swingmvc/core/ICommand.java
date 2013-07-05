package swingmvc.core;

/**
 * Abstract executable command interface.
 * @author elwood
 */
public interface ICommand {
    /**
     * Returns true if command can be executed, otherwise returns false.
     */
    boolean canExecute();
    
    /**
     * Performs command execution.
     */
    void execute();
}
