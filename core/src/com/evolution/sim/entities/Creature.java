package com.evolution.sim.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.evolution.sim.genetics.Genome;
import com.evolution.sim.world.TerrainType;
import com.evolution.sim.world.WorldMap;

/**
 * Classe base para todas as criaturas do simulador
 */
public abstract class Creature {
    // Posição e movimento
    protected Vector2 position;
    protected Vector2 velocity;
    protected float maxSpeed;
    protected float rotation;
    
    // Atributos básicos
    protected float size;
    protected float energy;
    protected float maxEnergy;
    protected float age;
    protected float maxAge;
    protected boolean alive;
    
    // Genética
    protected Genome genome;
    
    // Referência ao mundo
    protected WorldMap worldMap;
    
    public Creature(float x, float y, WorldMap worldMap) {
        this.position = new Vector2(x, y);
        this.velocity = new Vector2(0, 0);
        this.worldMap = worldMap;
        this.alive = true;
        
        // Valores padrão, devem ser sobrescritos nas subclasses
        this.maxSpeed = 50f;
        this.size = 5f;
        this.energy = 100f;
        this.maxEnergy = 100f;
        this.age = 0f;
        this.maxAge = 100f;
        this.rotation = MathUtils.random(360f);
    }
    
    /**
     * Atualiza a criatura a cada frame
     * @param delta Tempo desde o último frame em segundos
     */
    public void update(float delta) {
        if (!alive) return;
        
        // Envelhecer
        age += delta;
        if (age >= maxAge) {
            die();
            return;
        }
        
        // Consumir energia com o tempo
        energy -= delta * getBasicEnergyConsumption();
        if (energy <= 0) {
            die();
            return;
        }
        
        // Comportamento específico da subclasse
        behavior(delta);
        
        // Aplicar movimento
        position.add(velocity.x * delta, velocity.y * delta);
        
        // Verificar limites do mundo
        checkWorldBounds();
        
        // Verificar interações com o terreno
        handleTerrainInteraction();
    }
    
    /**
     * Comportamento específico da criatura
     * @param delta Tempo desde o último frame
     */
    protected abstract void behavior(float delta);
    
    /**
     * Desenha a criatura
     * @param batch SpriteBatch para desenho
     */
    public void render(SpriteBatch batch) {
        if (!alive) return;
        
        // As subclasses podem implementar renderização customizada
        // Esta é apenas uma implementação básica
    }
    
    /**
     * Desenha a criatura usando ShapeRenderer
     * @param shapeRenderer ShapeRenderer para desenho de formas
     */
    public void renderShape(ShapeRenderer shapeRenderer) {
        if (!alive) return;
        
        // Desenhar corpo
        Color color = getColor();
        shapeRenderer.setColor(color);
        shapeRenderer.circle(position.x, position.y, size);
        
        // Desenhar direção
        float dirX = position.x + MathUtils.cosDeg(rotation) * size * 1.5f;
        float dirY = position.y + MathUtils.sinDeg(rotation) * size * 1.5f;
        shapeRenderer.line(position.x, position.y, dirX, dirY);
    }
    
    /**
     * Obtém a cor da criatura
     * @return Color para renderização
     */
    protected abstract Color getColor();
    
    /**
     * Calcula o consumo básico de energia por segundo
     * @return Taxa de consumo de energia
     */
    protected float getBasicEnergyConsumption() {
        // Fórmula básica: criaturas maiores consomem mais energia
        return 0.5f + (size * 0.1f);
    }
    
    /**
     * Movimento em direção a um alvo
     * @param target Posição alvo
     * @param delta Tempo desde o último frame
     */
    protected void moveToward(Vector2 target, float delta) {
        // Calcular direção
        Vector2 direction = new Vector2(target).sub(position).nor();
        
        // Calcular rotação desejada
        float targetRotation = direction.angleDeg();
        
        // Girar gradualmente para a direção
        rotation = MathUtils.lerpAngleDeg(rotation, targetRotation, delta * 2f);
        
        // Mover na direção atual
        velocity.x = MathUtils.cosDeg(rotation) * maxSpeed;
        velocity.y = MathUtils.sinDeg(rotation) * maxSpeed;
    }
    
    /**
     * Movimento aleatório
     * @param delta Tempo desde o último frame
     */
    protected void moveRandomly(float delta) {
        // Mudar direção ocasionalmente
        if (MathUtils.random() < delta * 0.1f) {
            rotation += MathUtils.random(-30f, 30f);
        }
        
        // Mover na direção atual
        velocity.x = MathUtils.cosDeg(rotation) * maxSpeed * 0.5f;
        velocity.y = MathUtils.sinDeg(rotation) * maxSpeed * 0.5f;
    }
    
    /**
     * Verifica se a criatura está dentro dos limites do mundo
     */
    protected void checkWorldBounds() {
        // Obter dimensões do mapa em pixels
        float mapWidth = worldMap.getWidth() * 32f;  // 32 é o tamanho do tile
        float mapHeight = worldMap.getHeight() * 32f;
        
        // Limitar posição
        if (position.x < size) {
            position.x = size;
            velocity.x *= -0.5f;
            rotation = 180 - rotation;
        } else if (position.x > mapWidth - size) {
            position.x = mapWidth - size;
            velocity.x *= -0.5f;
            rotation = 180 - rotation;
        }
        
        if (position.y < size) {
            position.y = size;
            velocity.y *= -0.5f;
            rotation = 360 - rotation;
        } else if (position.y > mapHeight - size) {
            position.y = mapHeight - size;
            velocity.y *= -0.5f;
            rotation = 360 - rotation;
        }
    }
    
    /**
     * Lida com interações entre a criatura e o terreno
     */
    protected void handleTerrainInteraction() {
        // Converter posição em coordenadas de tile
        int tileX = (int) (position.x / 32);
        int tileY = (int) (position.y / 32);
        
        // Obter tipo de terreno atual
        TerrainType terrain = worldMap.getTerrainAt(tileX, tileY);
        
        // Aplicar efeitos do terreno
        switch (terrain) {
            case WATER:
                // Reduzir velocidade na água, exceto para criaturas aquáticas
                velocity.scl(0.9f);
                break;
            case MOUNTAIN:
                // Movimento mais lento em montanhas
                velocity.scl(0.7f);
                break;
            case SNOW:
                // Perda adicional de energia no frio
                energy -= 0.05f;
                velocity.scl(0.8f);
                break;
            case FOREST:
                // Floresta pode oferecer proteção/camuflagem
                break;
            default:
                // Terrenos normais não têm efeito especial
                break;
        }
    }
    
    /**
     * Verifica colisão com outra criatura
     * @param other Outra criatura para verificar colisão
     * @return true se houver colisão
     */
    public boolean isCollidingWith(Creature other) {
        // Verificar distância entre as criaturas
        float distance = position.dst(other.position);
        float minDistance = size + other.size;
        
        return distance < minDistance;
    }
    
    /**
     * Calcula a distância para outra criatura
     * @param other Outra criatura
     * @return Distância em pixels
     */
    public float distanceTo(Creature other) {
        return position.dst(other.position);
    }
    
    /**
     * Adiciona energia à criatura (alimentação)
     * @param amount Quantidade de energia
     */
    public void addEnergy(float amount) {
        energy = Math.min(energy + amount, maxEnergy);
    }
    
    /**
     * Mata a criatura
     */
    public void die() {
        alive = false;
    }
    
    /**
     * Verifica se a criatura está pronta para reproduzir
     * @return true se pode se reproduzir
     */
    public boolean canReproduce() {
        // Precisa ter pelo menos 70% da energia total
        return alive && energy > maxEnergy * 0.7f;
    }
    
    /**
     * Cria um descendente (reprodução)
     * @param partner Parceiro para reprodução (pode ser null para reprodução assexuada)
     * @return Nova criatura
     */
    public abstract Creature reproduce(Creature partner);
    
    // Getters e setters
    
    public Vector2 getPosition() {
        return position;
    }
    
    public float getSize() {
        return size;
    }
    
    public float getEnergy() {
        return energy;
    }
    
    public boolean isAlive() {
        return alive;
    }
}
