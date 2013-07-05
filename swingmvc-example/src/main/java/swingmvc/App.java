/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package swingmvc;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import swingmvc.MainWindow.MainWindowModule;

/**
 *
 * @author elwood
 */
public class App {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        MainWindowModule module = new MainWindowModule();
        module.getView().setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        module.getView().setVisible(true);
        module.getView().addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                System.exit(0);
            }
        });
    }
}
