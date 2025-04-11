package com.evolution.sim.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.evolution.sim.genetics.Genome;
import com.evolution.sim.world.TerrainType;
import com.evolution.sim.world.WorldMap;

import java.util.List;

/**
 * Representa uma criatura presa que se alimenta de vegetação
 */
public class Prey extends Creature {
    // Atributos específicos das presas
    private float plantDetectionRange;
    private float predatorDetectionRange;
    private float reproductionCooldown;
    private float reproductionTimer;
    
    public Prey(float x, float y, WorldMap worldMap) {
        super(x, y, worldMap);
        
        // Configurações específicas para presas
        this.maxSpeed = 60f;
        this.size = 4f;
        this.energy = 50f;
        this.maxEnergy = 80f;
        this.maxAge = 60f;
        
        // Valores de detecção
        this.plantDetectionRange = 100f;
        this.predatorDetectionRange = 150f;
        this.reproductionCooldown = 10f;
        this.reproductionTimer = 0f;
    }
    
    /**
     * Construtor para criar filhotes com genoma
     */
    public Prey(float x, float y, WorldMap worldMap, Genome genome) {
        this(x, y, worldMap);
        this.genome = genome;
        
        // Aplicar modificações genéticas
        if (genome != null) {
            applyGenome();
        }
    }
    
    /**
     * Aplica os efeitos do genoma nos atributos
     */
    private void applyGenome() {
        // Implementação básica - será expandida no futuro
        this.size = size * (1 + genome.getTraitValue("size", 0.2f) - 0.1f);
        this.maxSpeed = maxSpeed * (1 + genome.getTraitValue("speed", 0.3f) - 0.15f);
        this.maxEnergy = maxEnergy * (1 + genome.getTraitValue("energy", 0.2f) - 0.1f);
        this.plantDetectionRange = plantDetectionRange * (1 + genome.getTraitValue("perception", 0.4f) - 0.2f);
        this.predatorDetectionRange = predatorDetectionRange * (1 + genome.getTraitValue("perception", 0.3f) - 0.15f);
    }
    
    @Override
    protected void behavior(float delta) {
        // Atualizar timer de reprodução
        reproductionTimer -= delta;
        
        // Lógica de comportamento da presa
        Vector2 nearestPredator = findNearestPredator();
        
        // Se houver um predador próximo, fugir
        if (nearestPredator != null) {
            fleeFromPredator(nearestPredator, delta);
        } else {
            // Se não houver predador, procurar comida
            if (energy < maxEnergy * 0.7f) {
                seekFood(delta);
            } else {
                // Se estiver com energia suficiente, comportamento de procura por parceiros ou exploração
                moveRandomly(delta);
            }
        }
    }
    
    /**
     * Procura pelo predador mais próximo
     * @return Posição do predador ou null se não houver predador no alcance
     */
    private Vector2 findNearestPredator() {
        // Implementação simples - seria substituída por uma verificação real das criaturas no ambiente
        // Na implementação real, você teria uma lista de predadores para verificar
        return null;
    }
    
    /**
     * Foge de um predador
     * @param predatorPos Posição do predador
     * @param delta Tempo desde o último frame
     */
    private void fleeFromPredator(Vector2 predatorPos, float delta) {
        // Calcular direção oposta ao predador
        Vector2 fleeDirection = new Vector2(position).sub(predatorPos).nor();
        
        // Rotação desejada para a fuga
        float targetRotation = fleeDirection.angleDeg();
        
        // Ajustar rotação rapidamente (fuga é mais rápida que movimento normal)
        rotation = MathUtils.lerpAngleDeg(rotation, targetRotation, delta * 4f);
        
        // Mover na direção oposta ao predador usando velocidade máxima
        velocity.x = MathUtils.cosDeg(rotation) * maxSpeed * 1.2f; // Velocidade de fuga aumentada
        velocity.y = MathUtils.sinDeg(rotation) * maxSpeed * 1.2f;
        
        // Consumir mais energia ao fugir
        energy -= delta * 0.5f;
    }
    
    /**
     * Procura e se move em direção a comida (plantas)
     * @param delta Tempo desde o último frame
     */
    private void seekFood(float delta) {
        // Verificar terreno atual para alimentação
        int tileX = (int) (position.x / 32);
        int tileY = (int) (position.y / 32);
        
        TerrainType currentTerrain = worldMap.getTerrainAt(tileX, tileY);
        
        // Se estiver em um terreno com plantas (grama ou floresta), se alimentar
        if (currentTerrain == TerrainType.GRASS || currentTerrain == TerrainType.FOREST) {
            // Alimentar-se
            addEnergy(delta * 10f);
            
            // Movimento mais lento enquanto se alimenta
            velocity.scl(0.3f);
        } else {
            // Procurar por terrenos com plantas
            Vector2 foodTarget = findNearestFood();
            
            if (foodTarget != null) {
                moveToward(foodTarget, delta);
            } else {
                moveRandomly(delta);
            }
        }
    }
    
    /**
     * Procura pelo terreno com comida mais próximo
     * @return Posição do terreno com comida ou null se não houver no alcance
     */
    private Vector2 findNearestFood() {
        // Verificar em uma área ao redor da criatura por terrenos com plantas
        int tileSize = 32;
        int currentTileX = (int) (position.x / tileSize);
        int currentTileY = (int) (position.y / tileSize);
        
        // Raio de busca em tiles
        int searchRadius = (int) (plantDetectionRange / tileSize);
        
        // Melhor posição encontrada
        Vector2 bestPosition = null;
        float bestDistance = Float.MAX_VALUE;
        
        // Procurar em uma área quadrada
        for (int x = currentTileX - searchRadius; x <= currentTileX + searchRadius; x++) {
            for (int y = currentTileY - searchRadius; y <= currentTileY + searchRadius; y++) {
                // Verificar se o terreno é adequado para alimentação
                TerrainType terrainType = worldMap.getTerrainAt(x, y);
                
                if (terrainType == TerrainType.GRASS || terrainType == TerrainType.FOREST) {
                    // Calcular posição central do tile
                    Vector2 tileCenter = new Vector2(x * tileSize + tileSize/2, y * tileSize + tileSize/2);
                    
                    // Calcular distância
                    float distance = position.dst(tileCenter);
                    
                    // Se for o mais próximo até agora e estiver dentro do alcance
                    if (distance < bestDistance && distance <= plantDetectionRange) {
                        bestDistance = distance;
                        bestPosition = tileCenter;
                    }
                }
            }
        }
        
        return bestPosition;
    }
    
    @Override
    protected Color getColor() {
        // Cor verde-azulada para presas
        return new Color(0.2f, 0.8f, 0.6f, 1.0f);
    }
    
    @Override
    public Creature reproduce(Creature partner) {
        // Verificar se pode reproduzir
        if (!canReproduce() || reproductionTimer > 0) {
            return null;
        }
        
        // Consumir energia para reprodução
        energy *= 0.6f;
        
        // Configurar timer de cooldown
        reproductionTimer = reproductionCooldown;
        
        // Criar novo genoma
        Genome childGenome;
        
        if (partner != null && partner instanceof Prey && partner.canReproduce()) {
            // Reprodução sexual - misturar genomas
            Prey preyPartner = (Prey) partner;
            preyPartner.energy *= 0.6f; // Parceiro também gasta energia
            
            childGenome = Genome.combine(this.genome, preyPartner.genome);
        } else {
            // Reprodução assexuada - clonar com mutações
            childGenome = genome.clone();
            childGenome.mutate();
        }
        
        // Criar filhote próximo à posição atual
        float offsetX = MathUtils.random(-20f, 20f);
        float offsetY = MathUtils.random(-20f, 20f);
        
        return new Prey(position.x + offsetX, position.y + offsetY, worldMap, childGenome);
    }