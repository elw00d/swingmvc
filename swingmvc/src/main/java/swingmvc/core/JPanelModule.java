package swingmvc.core;

import javax.swing.*;
import java.awt.*;

/**
 * Base class for modules that use {@link JPanel} as view.
 * @author elwood
 */
public abstract class JPanelModule<TView extends JPanel, TViewModel extends ViewModel, TController extends Controller<TView, TViewModel>> extends JPanel {

    public JPanelModule(LayoutManager layout, boolean isDoubleBuffered) {
        super(layout, isDoubleBuffered);
        ctorCore();
    }

    public JPanelModule(LayoutManager layout) {
        super(layout);
        ctorCore();
    }

    public JPanelModule(boolean isDoubleBuffered) {
        super(isDoubleBuffered);
        ctorCore();
    }

    public JPanelModule() {
        ctorCore();
    }

    private void ctorCore() {
        try {
            Class<TView> viewClazz= viewClass();
            TView view = viewClazz.newInstance();

            if (!DesignerSupport.isNetbeansDesignerAttached() ) {
                controller = controllerClass().newInstance();
                controller.initialize(view);
            }

            // add wrapped view as child
            this.setLayout(new BorderLayout());
            this.add(view, BorderLayout.CENTER);

            this.view = view;
        } catch (InstantiationException ex) {
            throw new RuntimeException (ex);
        } catch (IllegalAccessException ex) {
            throw new RuntimeException (ex);
        }
    }

    private TView view;
    private TController controller;

    /**
     * You should implement this to provide the real view clazz object in runtime.
     */
    protected abstract Class<TView> viewClass();

    /**
     * You should implement this to provide the real controller clazz object in runtime.
     */
    protected abstract Class<TController> controllerClass();
    
    /**
     * @return Underlying controller.
     */
    public TController getController() {
        return controller;
    }
    
    public TViewModel getModel() {
        return controller.getModel();
    }
    
    public void setModel( TViewModel model) {
        controller.setModel(model);
    }
}
