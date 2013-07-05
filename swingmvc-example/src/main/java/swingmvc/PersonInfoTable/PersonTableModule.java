/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package swingmvc.PersonInfoTable;

import swingmvc.core.JPanelModule;

/**
 *
 * @author elwood
 */
public class PersonTableModule extends JPanelModule<PersonTableJPanel, PersonVM, PersonTableController> {
    protected Class<PersonTableJPanel> viewClass() { return PersonTableJPanel.class; }
    protected Class<PersonTableController> controllerClass() { return PersonTableController.class; }
}
