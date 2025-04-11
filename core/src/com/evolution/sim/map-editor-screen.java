package com.evolution.sim.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.evolution.sim.EvolutionSim;
import com.evolution.sim.world.TerrainType;
import com.evolution.sim.world.WorldMap;

public class MapEditorScreen implements Screen, GestureDetector.GestureListener {
    private final EvolutionSim game;
    private OrthographicCamera camera;
    private WorldMap worldMap;
    private Stage uiStage;
    private ShapeRenderer shapeRenderer;
    
    // Estado do editor
    private TerrainType selectedTerrain = TerrainType.GRASS;
    
    // Constantes
    private static final int TILE_SIZE = 32;
    private static final int MAP_WIDTH = 50;
    private static final int MAP_HEIGHT = 50;
    
    // Controle da câmera
    private float zoom = 1.0f;
    private Texture[] terrainTextures;
    
    public MapEditorScreen(EvolutionSim game) {
        this.game = game;
        
        // Inicializar câmera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(MAP_WIDTH * TILE_SIZE / 2f, MAP_HEIGHT * TILE_SIZE / 2f, 0);
        
        // Inicializar mapa
        worldMap = new WorldMap(MAP_WIDTH, MAP_HEIGHT);
        
        // Inicializar entrada de toque
        Gdx.input.setInputProcessor(new GestureDetector(this));
        
        // Inicializar interface de usuário
        createUI();
        
        // Inicializar renderer
        shapeRenderer = new ShapeRenderer();
        
        // Criar texturas para os terrenos
        createTerrainTextures();
    }
    
    private void createTerrainTextures() {
        terrainTextures = new Texture[TerrainType.values().length];
        
        for (int i = 0; i < TerrainType.values().length; i++) {
            TerrainType type = TerrainType.values()[i];
            
            // Criar um pixmap para cada tipo de terreno
            Pixmap pixmap = new Pixmap(TILE_SIZE, TILE_SIZE, Pixmap.Format.RGBA8888);
            pixmap.setColor(type.getR(), type.getG(), type.getB(), 1);
            pixmap.fill();
            
            // Converter pixmap para textura
            terrainTextures[i] = new Texture(pixmap);
            pixmap.dispose();
        }
    }
    
    private void createUI() {
        uiStage = new Stage(new ScreenViewport());
        
        // Criar uma tabela para os botões na parte inferior
        Table table = new Table();
        table.setFillParent(true);
        table.bottom().padBottom(20);
        
        // Criar botões para cada tipo de terreno
        TerrainType[] terrainTypes = TerrainType.values();
        for (TerrainType terrainType : terrainTypes) {
            // Criar textura para o botão
            Pixmap pixmap = new Pixmap(50, 50, Pixmap.Format.RGBA8888);
            pixmap.setColor(terrainType.getR(), terrainType.getG(), terrainType.getB(), 1);
            pixmap.fill();
            
            Texture buttonTexture = new Texture(pixmap);
            TextureRegion buttonRegion = new TextureRegion(buttonTexture);
            TextureRegionDrawable buttonDrawable = new TextureRegionDrawable(buttonRegion);
            
            // Criar botão
            ImageButton button = new ImageButton(buttonDrawable);
            
            // Adicionar ouvinte para o botão
            final TerrainType type = terrainType;
            button.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    selectedTerrain = type;
                    Gdx.app.log("MapEditor", "Terreno selecionado: " + type);
                }
            });
            
            // Adicionar botão à tabela
            table.add(button).size(50, 50).pad(5);
            
            pixmap.dispose();
        }
        
        // Adicionar tabela ao estágio
        uiStage.addActor(table);
        
        // Combinar os processadores de entrada
        Gdx.input.setInputProcessor(new GestureDetector(this));
    }
    
    @Override
    public void render(float delta) {
        // Limpar tela
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        // Atualizar câmera
        camera.update();
        
        // Calcular área visível
        int startX = Math.max(0, (int)((camera.position.x - camera.viewportWidth/2 * camera.zoom) / TILE_SIZE));
        int startY = Math.max(0, (int)((camera.position.y - camera.viewportHeight/2 * camera.zoom) / TILE_SIZE));
        int endX = Math.min(worldMap.getWidth() - 1, (int)((camera.position.x + camera.viewportWidth/2 * camera.zoom) / TILE_SIZE) + 1);
        int endY = Math.min(worldMap.getHeight() - 1, (int)((camera.position.y + camera.viewportHeight/2 * camera.zoom) / TILE_SIZE) + 1);
        
        // Renderizar mapa
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        
        for (int x = startX; x <= endX; x++) {
            for (int y = startY; y <= endY; y++) {
                TerrainType terrain = worldMap.getTerrainAt(x, y);
                int index = terrain.ordinal();
                
                if (index >= 0 && index < terrainTextures.length) {
                    game.batch.draw(
                        terrainTextures[index],
                        x * TILE_SIZE,
                        y * TILE_SIZE,
                        TILE_SIZE,
                        TILE_SIZE
                    );
                }
            }
        }
        
        game.batch.end();
        
        // Renderizar grade
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeType.Line);
        shapeRenderer.setColor(0.3f, 0.3f, 0.3f, 0.5f);
        
        for (int x = startX; x <= endX; x++) {
            shapeRenderer.line(x * TILE_SIZE, startY * TILE_SIZE, x * TILE_SIZE, (endY + 1) * TILE_SIZE);
        }
        
        for (int y = startY; y <= endY; y++) {
            shapeRenderer.line(startX * TILE_SIZE, y * TILE_SIZE, (endX + 1) * TILE_SIZE, y * TILE_SIZE);
        }
        
        shapeRenderer.end();
        
        // Renderizar UI
        uiStage.act(delta);
        uiStage.draw();
    }
    
    @Override
    public void resize(int width, int height) {
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.update();
        
        uiStage.getViewport().update(width, height, true);
    }
    
    @Override
    public void pause() {
        // Salvar mapa automaticamente
        worldMap.saveToFile("worldmap.json");
    }
    
    @Override
    public void resume() {
        // Carregar mapa se existir
        try {
            worldMap.loadFromFile("worldmap.json");
        } catch (Exception e) {
            Gdx.app.log("MapEditor", "Nenhum mapa salvo encontrado");
        }
    }
    
    @Override
    public void show() {
        // Nada a fazer
    }
    
    @Override
    public void hide() {
        // Nada a fazer
    }
    
    @Override
    public void dispose() {
        shapeRenderer.dispose();
        uiStage.dispose();
        
        for (Texture texture : terrainTextures) {
            texture.dispose();
        }
    }
    
    // Manipulação de entrada
    @Override
    public boolean tap(float x, float y, int count, int button) {
        // Converter coordenadas de tela para coordenadas de mundo
        Vector3 worldCoords = camera.unproject(new Vector3(x, y, 0));
        
        // Converter para coordenadas de tile
        int tileX = (int)(worldCoords.x / TILE_SIZE);
        int tileY = (int)(worldCoords.y / TILE_SIZE);
        
        // Verificar se está dentro dos limites do mapa
        if (tileX >= 0 && tileX < worldMap.getWidth() && tileY >= 0 && tileY < worldMap.getHeight()) {
            // Modificar o terreno
            worldMap.setTerrainAt(tileX, tileY, selectedTerrain);
            return true;
        }
        
        return false;
    }
    
    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        // Mover a câmera
        camera.translate(-deltaX * camera.zoom, deltaY * camera.zoom);
        
        // Limitar posição da câmera
        float halfWidth = camera.viewportWidth * camera.zoom / 2;
        float halfHeight = camera.viewportHeight * camera.zoom / 2;
        
        camera.position.x = Math.max(halfWidth, Math.min(camera.position.x, worldMap.getWidth() * TILE_SIZE - halfWidth));
        camera.position.y = Math.max(halfHeight, Math.min(camera.position.y, worldMap.getHeight() * TILE_SIZE - halfHeight));
        
        return true;
    }
    
    @Override
    public boolean zoom(float initialDistance, float distance) {
        // Aplicar zoom
        float newZoom = zoom * initialDistance / distance;
        
        // Limitar zoom
        newZoom = Math.max(0.2f, Math.min(newZoom, 3.0f));
        
        // Aplicar à câmera
        camera.zoom = newZoom;
        zoom = newZoom;
        
        return true;
    }
    
    // Métodos não utilizados da interface GestureListener
    @Override public boolean touchDown(float x, float y, int pointer, int button) { return false; }
    @Override public boolean fling(float velocityX, float velocityY, int button) { return false; }
    @Override public boolean panStop(float x, float y, int pointer, int button) { return false; }
    @Override public boolean longPress(float x, float y) { return false; }
    @Override public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) { return false; }
    @Override public void pinchStop() { }
}
