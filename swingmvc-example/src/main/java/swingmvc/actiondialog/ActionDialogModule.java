/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package swingmvc.actiondialog;

import java.awt.Frame;

import swingmvc.core.JDialogModule;
import swingmvc.core.ViewModel;

/**
 *
 * @author elwood
 */
public class ActionDialogModule extends JDialogModule<ActionDialog, ViewModel, ActionDialogController> {

    public ActionDialogModule(Frame owner, boolean modal) {
        super(owner, modal);
    }

    protected Class<ActionDialog> viewClass() { return ActionDialog.class; }
    protected Class<ActionDialogController> controllerClass() { return ActionDialogController.class; }
    
}
