/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package swingmvc.MainWindow;

import swingmvc.core.JFrameModule;
import swingmvc.core.ViewModel;

/**
 *
 * @author elwood
 */
public class MainWindowModule  extends JFrameModule<MainWindowJFrame, ViewModel, MainWindowController> {
    protected Class<MainWindowJFrame> viewClass() { return MainWindowJFrame.class; }
    protected Class<MainWindowController> controllerClass() { return MainWindowController.class; }
}
