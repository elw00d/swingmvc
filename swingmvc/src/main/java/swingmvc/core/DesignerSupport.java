package swingmvc.core;

/**
 * @author igor.kostromin
 *         04.07.13 19:03
 */
public final class DesignerSupport {
    /**
     * Returns true if current code is executed within NetBeans IDE designer context.
     */
    public static boolean isNetbeansDesignerAttached() {
        boolean designerAttached = false;
        StackTraceElement[] stackTraceItems = Thread.currentThread().getStackTrace();
        for (StackTraceElement item : stackTraceItems) {
            if (item.getClassName().contains("org.netbeans.modules.form.CreationFactory")) {
                designerAttached = true;
                break;
            }
        }
        return designerAttached;
    }
}
