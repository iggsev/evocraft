package com.evolution.sim;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

public class DesktopLauncher {
    public static void main(String[] arg) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        
        config.setTitle("Evolution Simulator");
        config.setWindowedMode(1280, 720);
        config.setForegroundFPS(60);
        config.setIdleFPS(30);
        config.setResizable(true);
        
        new Lwjgl3Application(new EvolutionSim(), config);
    }
}
