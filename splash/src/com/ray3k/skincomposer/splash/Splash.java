package com.ray3k.skincomposer.splash;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class Splash {
    public static void main(String[] args) throws IOException {
        var file = new File(System.getProperty("user.dir") + "/" + "SkinComposer.jar");
        var process =  Runtime.getRuntime().exec("java -jar \"" + file.getName() + "\"", null, file.getParentFile());
        BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
    
        String line;
        while ((line = in.readLine()) != null) {
            if (line.equals("close-splash")) break;
        }
        
        in.close();
        SplashScreen splash = SplashScreen.getSplashScreen();
        if (splash != null) {
            splash.close();
        }
    }
}