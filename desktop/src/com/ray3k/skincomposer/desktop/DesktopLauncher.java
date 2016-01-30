package com.ray3k.skincomposer.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.ray3k.skincomposer.Main;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
                config.allowSoftwareMode = true;
                config.resizable = true;
                config.vSyncEnabled = true;
                config.width = 800;
                config.height = 800;
		new LwjglApplication(new Main(), config);
	}
}
