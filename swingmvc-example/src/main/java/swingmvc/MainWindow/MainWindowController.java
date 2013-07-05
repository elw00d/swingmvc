/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package swingmvc.MainWindow;

import swingmvc.PersonInfoTable.PersonVM;
import swingmvc.core.Controller;
import swingmvc.core.ViewModel;

/**
 *
 * @author elwood
 */
public class MainWindowController extends Controller<MainWindowJFrame, ViewModel> {

    @Override
    protected void onInitialized() {
        view.personTableModule2.setModel(new PersonVM("Igor", 23));
    }
    
}
