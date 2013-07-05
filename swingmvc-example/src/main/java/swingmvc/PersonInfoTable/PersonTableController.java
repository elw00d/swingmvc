package swingmvc.PersonInfoTable;

import binding.BindingMode;
import binding.UpdateSourceTrigger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import swingmvc.actiondialog.ActionDialogModule;
import swingmvc.core.Command;
import swingmvc.core.Controller;

/**
 *
 * @author elwood
 */
public class PersonTableController extends Controller<PersonTableJPanel, PersonVM> {
    
    private PersonVM model;
    
    protected void onInitialized() {
        bindButtonToCommand(view.buttonSave, "save");
        bindButtonToCommand(view.buttonClear, "clear");
    }
    
    protected void onUnload() {
        JOptionPane.showMessageDialog(view, "Unloaded.");
    }
    
    @Command("save")
    private void save() {
        ActionDialogModule dlg = new ActionDialogModule((JFrame) SwingUtilities.windowForComponent(view), true);
        dlg.getView().setModal(true);
        dlg.getView().setVisible(true);
    }
    
    @Command("clear")
    private void clear() {
        model.setName(null);
        model.setAge(null);
    }

    @Override
    protected boolean canExecuteCommand(String commandName) {
        if ("save".equals(commandName)) {
            return model != null && model.getName() != null && !model.getName().isEmpty() && model.getAge() != null && model.getAge() > 10;
        } else if ("clear".equals(commandName)) {
            return true;
        }
        return super.canExecuteCommand(commandName);
    }

    @Override
    protected void onModelChanged(PersonVM oldModel, PersonVM newModel) {
        model = newModel;
    }

    @Override
    protected void setupBindings(binding.BindingGroup bindings) {
        bindings.add(view.textfieldName, "text", "name");
        bindings.add(view.textfieldAge, "text", "age", BindingMode.Default, UpdateSourceTrigger.PropertyChanged);
    }
    
    @Override
    protected void onModelPropertyChanged(String propertyName) {
        if ("name".equals(propertyName) || "age".equals(propertyName)) {
            refreshCanExecuteCommand("save");
        }
    }
}
