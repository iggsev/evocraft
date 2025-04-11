package com.evolution.sim;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

public class AndroidLauncher extends AndroidApplication {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        
        // Configurações específicas para Android
        config.useAccelerometer = false;
        config.useCompass = false;
        config.useWakelock = true; // Mantém a tela ligada enquanto o app estiver rodando
        
        initialize(new EvolutionSim(), config);
    }
}
