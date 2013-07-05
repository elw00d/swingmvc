package swingmvc.core;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;

/**
 * Base class for modules that use {@link JFrame} as view.
 * @author elwood
 */
public abstract class JFrameModule <TView extends JFrame, TViewModel extends ViewModel, TController extends Controller<TView, TViewModel>> {
   
    private TView view;
    private TController controller;
    
    /**
     * Pairs of class and arg object in one array
     * @param argsWithTypes 
     */
    private void ctorCore(Object... argsWithTypes) {
        try {
            Class<TView> viewClazz = viewClass();
            TView view;
            if ( argsWithTypes.length == 0) {
                view = viewClazz.newInstance();
            } else {
                Class[] classes = new Class[argsWithTypes.length / 2];
                Object[] args = new Object[argsWithTypes.length / 2];
                for (int i = 0; i < argsWithTypes.length; i+= 2 ) {
                    classes[i / 2] = (Class) argsWithTypes[i];
                    args[i / 2] = argsWithTypes[i + 1];
                }
                view = viewClazz.getDeclaredConstructor(classes).newInstance(args);
            }
            //
            if (!DesignerSupport.isNetbeansDesignerAttached()) {
                controller = controllerClass().newInstance();
                controller.initialize(view);
            }
            //
            this.view = view;
        } catch (InstantiationException ex) {
            throw new RuntimeException (ex);
        } catch (InvocationTargetException ex) {
            throw new RuntimeException (ex);
        } catch (NoSuchMethodException ex) {
            throw new RuntimeException (ex);
        } catch (IllegalAccessException ex) {
            throw new RuntimeException (ex);
        }
    }

    /**
     * You should implement this to provide the real view clazz object in runtime.
     */
    protected abstract Class<TView> viewClass();

    /**
     * You should implement this to provide the real controller clazz object in runtime.
     */
    protected abstract Class<TController> controllerClass();

    public JFrameModule() throws HeadlessException {
        ctorCore();
    }
    
    /**
     * @return Returns underlying view.
     */
    public TView getView() {
        return view;
    }
    
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
