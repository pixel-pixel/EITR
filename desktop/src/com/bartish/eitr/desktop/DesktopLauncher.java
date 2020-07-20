package com.bartish.eitr.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.bartish.eitr.Main;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "closetrofobia";
		config.width = 540;
		config.height = 540;
		new LwjglApplication(new Main(), config);
	}
}
