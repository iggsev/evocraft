package com.evolution.sim.world;

/**
 * Tipos básicos de terreno para o editor de mapa
 */
public enum TerrainType {
    GRASS("grass", 0.2f, 0.8f, 0.2f),
    DIRT("dirt", 0.6f, 0.4f, 0.2f),
    SAND("sand", 0.9f, 0.8f, 0.5f),
    STONE("stone", 0.5f, 0.5f, 0.5f),
    WATER("water", 0.1f, 0.3f, 0.7f),
    FOREST("forest", 0.0f, 0.5f, 0.0f),
    MOUNTAIN("mountain", 0.65f, 0.65f, 0.7f),
    SNOW("snow", 0.95f, 0.95f, 1.0f);
    
    private final String name;
    private final float r;
    private final float g;
    private final float b;
    
    TerrainType(String name, float r, float g, float b) {
        this.name = name;
        this.r = r;
        this.g = g;
        this.b = b;
    }
    
    public String getName() {
        return name;
    }
    
    public float getR() {
        return r;
    }
    
    public float getG() {
        return g;
    }
    
    public float getB() {
        return b;
    }
}
