package com.evolution.sim.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter.OutputType;

/**
 * Representa o mapa do mundo com sua grade de terrenos
 */
public class WorldMap {
    private int width;
    private int height;
    private TerrainType[][] terrain;
    
    /**
     * Cria um novo mapa vazio
     */
    public WorldMap(int width, int height) {
        this.width = width;
        this.height = height;
        this.terrain = new TerrainType[width][height];
        
        // Inicializar terreno padrão
        fillWith(TerrainType.GRASS);
    }
    
    /**
     * Preenche todo o mapa com um tipo de terreno
     */
    public void fillWith(TerrainType terrainType) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                terrain[x][y] = terrainType;
            }
        }
    }
    
    /**
     * Obtém o tipo de terreno em uma posição
     */
    public TerrainType getTerrainAt(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return TerrainType.WATER; // Fora dos limites é água
        }
        return terrain[x][y];
    }
    
    /**
     * Define o tipo de terreno em uma posição
     */
    public void setTerrainAt(int x, int y, TerrainType type) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return;
        }
        terrain[x][y] = type;
    }
    
    /**
     * Salva o mapa em um arquivo
     */
    public void saveToFile(String filename) {
        try {
            // Criar objeto para salvar
            MapData data = new MapData();
            data.width = width;
            data.height = height;
            
            // Converter enum para strings
            data.terrainData = new String[width][height];
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    data.terrainData[x][y] = terrain[x][y].name();
                }
            }
            
            // Serializar para JSON
            Json json = new Json();
            json.setOutputType(OutputType.json);
            String jsonText = json.toJson(data);
            
            // Salvar no arquivo
            FileHandle file = Gdx.files.local(filename);
            file.writeString(jsonText, false);
            
            Gdx.app.log("WorldMap", "Mapa salvo em " + filename);
        } catch (Exception e) {
            Gdx.app.error("WorldMap", "Erro ao salvar o mapa: " + e.getMessage());
        }
    }
    
    /**
     * Carrega o mapa de um arquivo
     */
    public void loadFromFile(String filename) {
        try {
            FileHandle file = Gdx.files.local(filename);
            if (!file.exists()) {
                Gdx.app.error("WorldMap", "Arquivo não encontrado: " + filename);
                return;
            }
            
            // Ler JSON
            String jsonText = file.readString();
            Json json = new Json();
            MapData data = json.fromJson(MapData.class, jsonText);
            
            // Atualizar dimensões
            this.width = data.width;
            this.height = data.height;
            this.terrain = new TerrainType[width][height];
            
            // Converter strings para enum
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    this.terrain[x][y] = TerrainType.valueOf(data.terrainData[x][y]);
                }
            }
            
            Gdx.app.log("WorldMap", "Mapa carregado de " + filename);
        } catch (Exception e) {
            Gdx.app.error("WorldMap", "Erro ao carregar o mapa: " + e.getMessage());
        }
    }
    
    /**
     * Classe auxiliar para serialização
     */
    private static class MapData {
        public int width;
        public int height;
        public String[][] terrainData;
    }
    
    // Getters
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }
}
