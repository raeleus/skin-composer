package com.ray3k.skincomposer.utils;

import java.io.File;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;
import javafx.stage.FileChooser;

/**
 * A utility class that summons JavaFX FileChooser from the Swing EDT.
 * (Or anywhere else for that matter.) JavaFX should be initialized prior to
 * using this class (e. g. by creating a JFXPanel instance). It is also
 * recommended to call Platform.setImplicitExit(false) after initialization
 * to ensure that JavaFX platform keeps running. Don't forget to call
 * Platform.exit() when shutting down the application, to ensure that
 * the JavaFX threads don't prevent JVM exit.
 * 
 * @author Sergey A. Tachenov
 */
public class SynchronousJFXFileChooser {
    private final Supplier<FileChooser> fileChooserFactory;

    /**
     * Constructs a new file chooser that will use the provided factory.
     * 
     * The factory is accessed from the JavaFX event thread, so it should either
     * be immutable or at least its state shouldn't be changed randomly while
     * one of the dialog-showing method calls is in progress.
     * 
     * The factory should create and set up the chooser, for example,
     * by setting extension filters. If there is no need to perform custom
     * initialization of the chooser, FileChooser::new could be passed as
     * a factory.
     * 
     * Alternatively, the method parameter supplied to the showDialog()
     * function can be used to provide custom initialization.
     * 
     * @param fileChooserFactory the function used to construct new choosers
     */
    public SynchronousJFXFileChooser(Supplier<FileChooser> fileChooserFactory) {
        this.fileChooserFactory = fileChooserFactory;
    }

    /**
     * Shows the FileChooser dialog by calling the provided method.
     * 
     * Waits for one second for the dialog-showing task to start in the JavaFX
     * event thread, then throws an IllegalStateException if it didn't start.
     * 
     * @see #showDialog(java.util.function.Function, long, java.util.concurrent.TimeUnit) 
     * @param <T> the return type of the method, usually File or List&lt;File&gt;
     * @param method a function calling one of the dialog-showing methods
     * @return whatever the method returns
     */
    public <T> T showDialog(Function<FileChooser, T> method) {
        return showDialog(method, 1, TimeUnit.SECONDS);
    }

    /**
     * Shows the FileChooser dialog by calling the provided method. The dialog 
     * is created by the factory supplied to the constructor, then it is shown
     * by calling the provided method on it, then the result is returned.
     * <p>
     * Everything happens in the right threads thanks to
     * {@link SynchronousJFXCaller}. The task performed in the JavaFX thread
     * consists of two steps: construct a chooser using the provided factory
     * and invoke the provided method on it. Any exception thrown during these
     * steps will be rethrown in the calling thread, which shouldn't
     * normally happen unless the factory throws an unchecked exception.
     * </p>
     * <p>
     * If the calling thread is interrupted during either the wait for
     * the task to start or for its result, then null is returned and
     * the Thread interrupted status is set.
     * </p>
     * @param <T> return type (usually File or List&lt;File&gt;)
     * @param method a function that calls the desired FileChooser method
     * @param timeout time to wait for Platform.runLater() to <em>start</em>
     * the dialog-showing task (once started, it is allowed to run as long
     * as needed)
     * @param unit the time unit of the timeout argument
     * @return whatever the method returns
     * @throws IllegalStateException if Platform.runLater() fails to start
     * the dialog-showing task within the given timeout
     */
    public <T> T showDialog(Function<FileChooser, T> method,
            long timeout, TimeUnit unit) {
        Callable<T> task = () -> {
            FileChooser chooser = fileChooserFactory.get();
            return method.apply(chooser);
        };
        SynchronousJFXCaller<T> caller = new SynchronousJFXCaller<>(task);
        try {
            return caller.call(timeout, unit);
        } catch (RuntimeException | Error ex) {
            throw ex;
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            return null;
        } catch (Exception ex) {
            throw new AssertionError("Got unexpected checked exception from"
                    + " SynchronousJFXCaller.call()", ex);
        }
    }

    /**
     * Shows a FileChooser using FileChooser.showOpenDialog().
     * 
     * @see #showDialog(java.util.function.Function, long, java.util.concurrent.TimeUnit) 
     * @return the return value of FileChooser.showOpenDialog()
     */
    public File showOpenDialog() {
        return showDialog(chooser -> chooser.showOpenDialog(null));
    }

    /**
     * Shows a FileChooser using FileChooser.showSaveDialog().
     * 
     * @see #showDialog(java.util.function.Function, long, java.util.concurrent.TimeUnit) 
     * @return the return value of FileChooser.showSaveDialog()
     */
    public File showSaveDialog() {
        return showDialog(chooser -> chooser.showSaveDialog(null));
    }

    /**
     * Shows a FileChooser using FileChooser.showOpenMultipleDialog().
     * 
     * @see #showDialog(java.util.function.Function, long, java.util.concurrent.TimeUnit) 
     * @return the return value of FileChooser.showOpenMultipleDialog()
     */
    public List<File> showOpenMultipleDialog() {
        return showDialog(chooser -> chooser.showOpenMultipleDialog(null));
    }

}