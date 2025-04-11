package com.evolution.sim;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evolution.sim.screens.MapEditorScreen;

public class EvolutionSim extends Game {
    public SpriteBatch batch;
    
    @Override
    public void create() {
        batch = new SpriteBatch();
        setScreen(new MapEditorScreen(this));
    }
    
    @Override
    public void render() {
        super.render();
    }
    
    @Override
    public void dispose() {
        batch.dispose();
        getScreen().dispose();
    }
}
