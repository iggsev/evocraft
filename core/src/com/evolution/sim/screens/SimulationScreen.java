package com.evolution.sim.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.evolution.sim.EvolutionSim;
import com.evolution.sim.entities.Cannibal;
import com.evolution.sim.entities.Creature;
import com.evolution.sim.entities.Predator;
import com.evolution.sim.entities.Prey;
import com.evolution.sim.world.TerrainType;
import com.evolution.sim.world.WorldMap;

/**
 * Tela principal da simulação de evolução
 */
public class SimulationScreen implements Screen, GestureDetector.GestureListener {
    private final EvolutionSim game;
    private OrthographicCamera camera;
    private WorldMap worldMap;
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;
    private Stage uiStage;
    
    // Criaturas na simulação
    private Array<Creature> creatures;
    
    // Estatísticas
    private int preyCount;
    private int predatorCount;
    private int cannibalCount;
    
    // Controle de câmera
    private float zoom = 1.0f;
    
    // Constantes
    private static final int TILE_SIZE = 32;
    private static final int INITIAL_PREY = 20;
    private static final int INITIAL_PREDATORS = 8;
    private static final int INITIAL_CANNIBALS = 3;
    
    // Textures
    private Texture[] terrainTextures;
    
    // Estado da simulação
    private boolean paused = false;
    private float simulationSpeed = 1.0f;
    
    public SimulationScreen(EvolutionSim game) {
        this.game = game;
        
        // Inicializar câmera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        
        // Carregar ou criar mapa
        loadOrCreateWorld();
        
        // Inicializar criaturas
        creatures = new Array<>();
        
        // Inicializar interface de usuário
        createUI();
        
        // Inicializar renderizador de formas
        shapeRenderer = new ShapeRenderer();
        
        // Inicializar fonte
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        
        // Criar texturas para os terrenos
        createTerrainTextures();
        
        // Configurar detector de gestos
        Gdx.input.setInputProcessor(new GestureDetector(this));
        
        // Povoar o mundo com criaturas iniciais
        populateWorld();
    }
    
    private void loadOrCreateWorld() {
        // Tentar carregar um mapa existente
        worldMap = new WorldMap(50, 50);
        try {
            worldMap.loadFromFile("worldmap.json");
            Gdx.app.log("Simulation", "Mapa carregado com sucesso");
        } catch (Exception e) {
            Gdx.app.log("Simulation", "Não foi possível carregar o mapa. Criando um novo...");
            
            // Gerar um mapa aleatório simples
            generateRandomMap();
        }
        
        // Posicionar a câmera no centro do mapa
        camera.position.set(worldMap.getWidth() * TILE_SIZE / 2f, worldMap.getHeight() * TILE_SIZE / 2f, 0);
    }
    
    private void generateRandomMap() {
        // Preencher com grama primeiro
        worldMap.fillWith(TerrainType.GRASS);
        
        // Adicionar terrenos variados
        addRandomTerrainPatches(TerrainType.WATER, 0.1f, 5, 10);
        addRandomTerrainPatches(TerrainType.FOREST, 0.2f, 3, 8);
        addRandomTerrainPatches(TerrainType.MOUNTAIN, 0.05f, 2, 5);
        addRandomTerrainPatches(TerrainType.DIRT, 0.15f, 2, 6);
        addRandomTerrainPatches(TerrainType.SAND, 0.08f, 3, 7);
        addRandomTerrainPatches(TerrainType.SNOW, 0.05f, 1, 3);
        
        // Salvar o mapa gerado
        worldMap.saveToFile("worldmap.json");
    }
    
    private void addRandomTerrainPatches(TerrainType terrainType, float coverage, int minSize, int maxSize) {
        int mapWidth = worldMap.getWidth();
        int mapHeight = worldMap.getHeight();
        
        // Calcular número de manchas
        int totalTiles = mapWidth * mapHeight;
        int targetTileCount = (int)(totalTiles * coverage);
        int currentTileCount = 0;
        
        while (currentTileCount < targetTileCount) {
            // Posição aleatória
            int centerX = MathUtils.random(0, mapWidth - 1);
            int centerY = MathUtils.random(0, mapHeight - 1);
            
            // Tamanho aleatório para a mancha
            int patchSize = MathUtils.random(minSize, maxSize);
            
            // Criar mancha
            for (int y = -patchSize; y <= patchSize; y++) {
                for (int x = -patchSize; x <= patchSize; x++) {
                    // Calcular distância ao centro (forma circular)
                    float distance = (float) Math.sqrt(x*x + y*y);
                    
                    // Verificar se está dentro do raio e dentro dos limites do mapa
                    if (distance <= patchSize && MathUtils.random() < (1f - distance/patchSize)) {
                        int tileX = centerX + x;
                        int tileY = centerY + y;
                        
                        if (tileX >= 0 && tileX < mapWidth && tileY >= 0 && tileY < mapHeight) {
                            worldMap.setTerrainAt(tileX, tileY, terrainType);
                            currentTileCount++;
                        }
                    }
                }
            }
        }
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
        
        // Criar tabela para conter elementos de UI
        Table table = new Table();
        table.setFillParent(true);
        table.top().right().pad(10);
        
        // Adicionar rótulos para estatísticas
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);
        
        // Adicionar tabela ao estágio
        uiStage.addActor(table);
    }
    
    private void populateWorld() {
        // Limpar lista de criaturas
        creatures.clear();
        
        // Adicionar presas iniciais
        for (int i = 0; i < INITIAL_PREY; i++) {
            addRandomCreature(CreatureType.PREY);
        }
        
        // Adicionar predadores iniciais
        for (int i = 0; i < INITIAL_PREDATORS; i++) {
            addRandomCreature(CreatureType.PREDATOR);
        }
        
        // Adicionar canibais iniciais
        for (int i = 0; i < INITIAL_CANNIBALS; i++) {
            addRandomCreature(CreatureType.CANNIBAL);
        }
        
        // Atualizar contadores
        updateCreatureCounts();
    }
    
    private enum CreatureType {
        PREY, PREDATOR, CANNIBAL
    }
    
    private void addRandomCreature(CreatureType type) {
        // Encontrar posição válida (não na água)
        int tileX, tileY;
        TerrainType terrain;
        
        do {
            tileX = MathUtils.random(0, worldMap.getWidth() - 1);
            tileY = MathUtils.random(0, worldMap.getHeight() - 1);
            terrain = worldMap.getTerrainAt(tileX, tileY);
        } while (terrain == TerrainType.WATER);
        
        // Converter para posição em pixels
        float posX = tileX * TILE_SIZE + TILE_SIZE/2f;
        float posY = tileY * TILE_SIZE + TILE_SIZE/2f;
        
        // Criar criatura baseada no tipo
        Creature creature = null;
        switch (type) {
            case PREY:
                creature = new Prey(posX, posY, worldMap);
                break;
            case PREDATOR:
                creature = new Predator(posX, posY, worldMap);
                break;
            case CANNIBAL:
                creature = new Cannibal(posX, posY, worldMap);
                break;
        }
        
        // Adicionar à lista
        if (creature != null) {
            creatures.add(creature);
        }
    }
    
    private void updateCreatureCounts() {
        preyCount = 0;
        predatorCount = 0;
        cannibalCount = 0;
        
        for (Creature creature : creatures) {
            if (creature.isAlive()) {
                if (creature instanceof Prey) {
                    preyCount++;
                } else if (creature instanceof Cannibal) {
                    cannibalCount++;
                } else if (creature instanceof Predator) {
                    predatorCount++;
                }
            }
        }
    }
    
    @Override
    public void render(float delta) {
        // Limpar tela
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        // Processar entrada
        handleInput();
        
        // Atualizar câmera
        camera.update();
        
        // Atualizar simulação se não estiver pausada
        if (!paused) {
            updateSimulation(delta * simulationSpeed);
        }
        
        // Renderizar terreno
        renderTerrain();
        
        // Renderizar criaturas
        renderCreatures();
        
        // Renderizar UI
        renderUI(delta);
    }
    
    private void handleInput() {
        // Pausar/continuar simulação
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            paused = !paused;
        }
        
        // Controlar velocidade da simulação
        if (Gdx.input.isKeyJustPressed(Input.Keys.PLUS) || Gdx.input.isKeyJustPressed(Input.Keys.EQUALS)) {
            simulationSpeed = Math.min(simulationSpeed * 1.5f, 10.0f);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.MINUS)) {
            simulationSpeed = Math.max(simulationSpeed * 0.75f, 0.1f);
        }
        
        // Adicionar novas criaturas
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            addRandomCreature(CreatureType.PREY);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            addRandomCreature(CreatureType.PREDATOR);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
            addRandomCreature(CreatureType.CANNIBAL);
        }
        
        // Voltar ao editor de mapa
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new MapEditorScreen(game));
        }
    }
    
    private void updateSimulation(float delta) {
        // Atualizar cada criatura
        for (int i = 0; i < creatures.size; i++) {
            Creature creature = creatures.get(i);
            
            if (creature.isAlive()) {
                creature.update(delta);
            }
        }
        
        // Verificar interações entre criaturas
        processCreatureInteractions();
        
        // Remover criaturas mortas e adicionar novos nascimentos
        processReproduction();
        
        // Manter população mínima
        maintainMinimumPopulation();
        
        // Atualizar contadores
        updateCreatureCounts();
    }
    
    private void processCreatureInteractions() {
        // Este é um sistema simples de interação para demonstração
        // Uma implementação completa usaria um sistema de colisão mais eficiente
        
        for (int i = 0; i < creatures.size; i++) {
            Creature creature1 = creatures.get(i);
            
            if (!creature1.isAlive()) continue;
            
            for (int j = i + 1; j < creatures.size; j++) {
                Creature creature2 = creatures.get(j);
                
                if (!creature2.isAlive()) continue;
                
                // Verificar colisão
                if (creature1.isCollidingWith(creature2)) {
                    // Processar interação baseada nos tipos
                    if (creature1 instanceof Predator && creature2 instanceof Prey) {
                        // Predador come presa
                        processPredation((Predator)creature1, (Prey)creature2);
                    } 
                    else if (creature1 instanceof Prey && creature2 instanceof Predator) {
                        // Predador come presa
                        processPredation((Predator)creature2, (Prey)creature1);
                    }
                    else if (creature1 instanceof Cannibal && creature2 instanceof Predator) {
                        // Canibal come predador
                        processPredation((Predator)creature1, creature2);
                    }
                    else if (creature1 instanceof Predator && creature2 instanceof Cannibal) {
                        // Canibal come predador
                        processPredation((Predator)creature2, creature1);
                    }
                }
            }
        }
    }
    
    private void processPredation(Predator predator, Creature prey) {
        // Chance de sucesso baseada em força vs. velocidade
        float preySpeed = prey instanceof Prey ? ((Prey)prey).getSpeed() : 0;
        float predatorStrength = predator.getStrength();
        
        // Fórmula simples para chance de sucesso
        float successChance = 0.6f + (predatorStrength * 0.4f) - (preySpeed * 0.3f);
        
        if (MathUtils.random() < successChance) {
            // Predação bem-sucedida
            float energyGain = prey.getSize() * 15f; // Energia baseada no tamanho
            predator.addEnergy(energyGain);
            prey.die();
        }
    }
    
    private void processReproduction() {
        // Coletar novas criaturas para adicionar após o loop
        Array<Creature> newCreatures = new Array<>();
        
        // Remover criaturas mortas e processar reprodução
        for (int i = creatures.size - 1; i >= 0; i--) {
            Creature creature = creatures.get(i);
            
            // Remover mortos
            if (!creature.isAlive()) {
                creatures.removeIndex(i);
                continue;
            }
            
            // Chance de reprodução para criaturas com energia suficiente
            if (creature.canReproduce() && MathUtils.random() < 0.01f) {
                // Encontrar parceiro (se for da mesma espécie)
                Creature partner = findReproductionPartner(creature);
                
                // Criar novo descendente
                Creature child = creature.reproduce(partner);
                
                if (child != null) {
                    newCreatures.add(child);
                }
            }
        }
        
        // Adicionar novos nascimentos
        creatures.addAll(newCreatures);
    }
    
    private Creature findReproductionPartner(Creature creature) {
        // Proximidade máxima para reprodução
        float maxDistance = 100f;
        
        for (Creature other : creatures) {
            // Verificar se é da mesma espécie, está vivo e não é o mesmo
            if (other != creature && other.isAlive() && 
                other.getClass() == creature.getClass() && 
                other.canReproduce()) {
                
                // Verificar distância
                float distance = creature.distanceTo(other);
                if (distance < maxDistance) {
                    return other;
                }
            }
        }
        
        return null; // Nenhum parceiro encontrado
    }
    
    private void maintainMinimumPopulation() {
        // Manter população mínima de cada tipo
        if (preyCount < 5) {
            for (int i = 0; i < 3; i++) {
                addRandomCreature(CreatureType.PREY);
            }
        }
        
        if (predatorCount < 2) {
            addRandomCreature(CreatureType.PREDATOR);
        }
        
        if (cannibalCount < 1) {
            addRandomCreature(CreatureType.CANNIBAL);
        }
    }
    
    private void renderTerrain() {
        // Configurar batch com matriz da câmera
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        
        // Calcular área visível
        int startX = Math.max(0, (int)((camera.position.x - camera.viewportWidth/2 * camera.zoom) / TILE_SIZE));
        int startY = Math.max(0, (int)((camera.position.y - camera.viewportHeight/2 * camera.zoom) / TILE_SIZE));
        int endX = Math.min(worldMap.getWidth() - 1, (int)((camera.position.x + camera.viewportWidth/2 * camera.zoom) / TILE_SIZE) + 1);
        int endY = Math.min(worldMap.getHeight() - 1, (int)((camera.position.y + camera.viewportHeight/2 * camera.zoom) / TILE_SIZE) + 1);
        
        // Renderizar terreno visível
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
    }
    
    private void renderCreatures() {
        // Configurar renderizador de formas
        shapeRenderer.setProjectionMatrix(camera.combined);
        
        // Renderizar corpos das criaturas
        shapeRenderer.begin(ShapeType.Filled);
        for (Creature creature : creatures) {
            if (creature.isAlive()) {
                creature.renderShape(shapeRenderer);
            }
        }
        shapeRenderer.end();
        
        // Renderizar contornos
        shapeRenderer.begin(ShapeType.Line);
        shapeRenderer.setColor(Color.WHITE);
        for (Creature creature : creatures) {
            if (creature.isAlive()) {
                Vector2 pos = creature.getPosition();
                float size = creature.getSize();
                shapeRenderer.circle(pos.x, pos.y, size + 0.5f);
            }
        }
        shapeRenderer.end();
    }
    
    private void renderUI(float delta) {
        // Atualizar o palco da UI
        uiStage.act(delta);
        
        // Renderizar estatísticas na tela
        game.batch.begin();
        
        // Informações da simulação
        String status = paused ? "PAUSADO" : "EM EXECUÇÃO";
        String speedText = "Velocidade: " + String.format("%.1f", simulationSpeed) + "x";
        String statsText = "Presas: " + preyCount + " | Predadores: " + predatorCount + " | Canibais: " + cannibalCount;
        String controlsText = "ESPAÇO: Pausar | +/-: Velocidade | 1/2/3: Adicionar criaturas | ESC: Editor de mapa";
        
        font.draw(game.batch, status, 10, Gdx.graphics.getHeight() - 10);
        font.draw(game.batch, speedText, 10, Gdx.graphics.getHeight() - 30);
        font.draw(game.batch, statsText, 10, Gdx.graphics.getHeight() - 50);
        font.draw(game.batch, controlsText, 10, 20);
        
        game.batch.end();
        
        // Renderizar elementos da interface
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
        paused = true;
    }
    
    @Override
    public void resume() {
        // Manter pausado se já estava pausado antes
    }
    
    @Override
    public void show() {
        // Configuração ao mostrar a tela
    }
    
    @Override
    public void hide() {
        // Limpeza ao esconder a tela
    }
    
    @Override
    public void dispose() {
        shapeRenderer.dispose();
        font.dispose();
        uiStage.dispose();
        
        for (Texture texture : terrainTextures) {
            texture.dispose();
        }
    }
    
    // Implementação da interface GestureListener para controle de câmera
    
    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        // Mover a câmera com o movimento de pan
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
        // Aplicar zoom com pinça
        float newZoom = zoom * initialDistance / distance;
        
        // Limitar zoom
        newZoom = Math.max(0.2f, Math.min(newZoom, 3.0f));
        
        // Aplicar à câmera
        camera.zoom = newZoom;
        zoom = newZoom;
        
        return true;
    }
    
    @Override
    public boolean tap(float x, float y, int count, int button) {
        // Converter coordenadas de tela para coordenadas de mundo
        Vector3 worldCoords = camera.unproject(new Vector3(x, y, 0));
        
        // Mostrar informações sobre o terreno clicado
        int tileX = (int)(worldCoords.x / TILE_SIZE);
        int tileY = (int)(worldCoords.y / TILE_SIZE);
        
        if (tileX >= 0 && tileX < worldMap.getWidth() && tileY >= 0 && tileY < worldMap.getHeight()) {
            TerrainType terrain = worldMap.getTerrainAt(tileX, tileY);
            Gdx.app.log("Simulation", "Terreno: " + terrain.getName() + " em (" + tileX + ", " + tileY + ")");
        }
        
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