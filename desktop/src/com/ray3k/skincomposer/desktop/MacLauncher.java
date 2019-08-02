package com.ray3k.skincomposer.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.ray3k.skincomposer.Main;
import com.ray3k.skincomposer.utils.Utils;

import javax.swing.*;
import java.io.FileWriter;
import java.io.PrintWriter;

public class MacLauncher extends Launcher {
    public static void main(String[] args) {
        var config = new Lwjgl3ApplicationConfiguration();
        config.setResizable(true);
        config.useVsync(true);
        config.setWindowedMode(800, 800);
        var desktopLauncher = new MacLauncher();
        config.setWindowListener(desktopLauncher);
        config.setTitle("Skin Composer - New Project*");
        config.setWindowSizeLimits(675, 400, -1, -1);
        config.setWindowIcon("logo-16.png", "logo-32.png", "logo-48.png", "logo.png");
        var main = new Main(args);
        main.setDesktopWorker(desktopLauncher);
        if (!Utils.isWindows()) {
            desktopLauncher.closeSplashScreen();
        }

        try {
            new Lwjgl3Application(main, config);
        } catch (Exception e) {
            e.printStackTrace();
            
            try {
                var fw = new FileWriter(Gdx.files.external(".skincomposer/temp/java-stacktrace.txt").file(), true);
                PrintWriter pw = new PrintWriter(fw);
                e.printStackTrace(pw);
                pw.close();
                fw.close();
                int choice = JOptionPane.showConfirmDialog(null, "Exception occurred. See error log?", "Skin Composer Exception!", JOptionPane.YES_NO_OPTION);
                if (choice == 0) {
                    Utils.openFileExplorer(Gdx.files.external(".skincomposer/temp/java-stacktrace.txt"));
                }
            } catch (Exception ex) {

            }
        }
    }
}
