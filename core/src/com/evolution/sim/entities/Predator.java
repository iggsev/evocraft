package com.evolution.sim.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.evolution.sim.genetics.Genome;
import com.evolution.sim.world.WorldMap;

import java.util.List;

/**
 * Representa um predador que caça presas
 */
public class Predator extends Creature {
    // Atributos específicos dos predadores
    private float preyDetectionRange;
    private float attackRange;
    private float attackStrength;
    private float reproductionCooldown;
    private float reproductionTimer;
    private float huntingCooldown;
    private float huntingTimer;
    
    public Predator(float x, float y, WorldMap worldMap) {
        super(x, y, worldMap);
        
        // Configurações específicas para predadores
        this.maxSpeed = 70f;
        this.size = 6f;
        this.energy = 100f;
        this.maxEnergy = 150f;
        this.maxAge = 80f;
        
        // Valores de predador
        this.preyDetectionRange = 200f;
        this.attackRange = 10f;
        this.attackStrength = 30f;
        this.reproductionCooldown = 15f;
        this.reproductionTimer = 0f;
        this.huntingCooldown = 2f;
        this.huntingTimer = 0f;
    }
    
    /**
     * Construtor para criar filhotes com genoma
     */
    public Predator(float x, float y, WorldMap worldMap, Genome genome) {
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
        this.preyDetectionRange = preyDetectionRange * (1 + genome.getTraitValue("perception", 0.4f) - 0.2f);
        this.attackStrength = attackStrength * (1 + genome.getTraitValue("strength", 0.3f) - 0.15f);
    }
    
    @Override
    protected void behavior(float delta) {
        // Atualizar timers
        reproductionTimer -= delta;
        huntingTimer -= delta;
        
        // Lógica de comportamento do predador
        if (energy < maxEnergy * 0.4f) {
            // Se estiver com pouca energia, priorizar a caça
            huntPrey(delta);
        } else if (energy > maxEnergy * 0.8f && reproductionTimer <= 0) {
            // Se estiver com energia alta e puder reproduzir, procurar parceiros
            // Na implementação real, procuraria por outros predadores
            moveRandomly(delta);
        } else {
            // Comportamento normal - alternar entre caça e exploração
            if (MathUtils.randomBoolean(0.7f)) {
                huntPrey(delta);
            } else {
                moveRandomly(delta);
            }
        }
    }
    
    /**
     * Comportamento de caça - procurar e perseguir presas
     * @param delta Tempo desde o último frame
     */
    private void huntPrey(float delta) {
        // Na implementação real, você teria uma referência às presas no mundo
        // Por enquanto, apenas simularemos o comportamento
        
        // Simulando a detecção de uma presa
        Vector2 preyPosition = findNearestPrey();
        
        if (preyPosition != null) {
            // Perseguir a presa
            moveToward(preyPosition, delta);
            
            // Verificar se está no alcance de ataque
            float distanceToPrey = position.dst(preyPosition);
            
            if (distanceToPrey <= attackRange && huntingTimer <= 0) {
                // Atacar a presa
                attack();
                
                // Reiniciar o cooldown de ataque
                huntingTimer = huntingCooldown;
            }
        } else {
            // Se não houver presas visíveis, explorar
            moveRandomly(delta);
        }
    }
    
    /**
     * Procura pela presa mais próxima
     * @return Posição da presa ou null se não houver presa no alcance
     */
    private Vector2 findNearestPrey() {
        // Implementação simples - seria substituída por uma verificação real das presas no ambiente
        // Na implementação real, você teria uma lista de presas para verificar
        return null;
    }
    
    /**
     * Executa um ataque contra uma presa
     */
    private void attack() {
        // Na implementação real, você aplicaria dano à presa e possivelmente a mataria
        // Se a presa morresse, o predador obteria energia dela
        
        // Simulação - apenas adicionar energia como se tivesse comido uma presa
        addEnergy(30f);
    }
    
    @Override
    protected Color getColor() {
        // Cor avermelhada para predadores
        return new Color(0.8f, 0.2f, 0.2f, 1.0f);
    }
    
    @Override
    public Creature reproduce(Creature partner) {
        // Verificar se pode reproduzir
        if (!canReproduce() || reproductionTimer > 0) {
            return null;
        }
        
        // Consumir energia para reprodução
        energy *= 0.7f;
        
        // Configurar timer de cooldown
        reproductionTimer = reproductionCooldown;
        
        // Criar novo genoma
        Genome childGenome;
        
        if (partner != null && partner instanceof Predator && partner.canReproduce()) {
            // Reprodução sexual - misturar genomas
            Predator predatorPartner = (Predator) partner;
            predatorPartner.energy *= 0.7f; // Parceiro também gasta energia
            
            childGenome = Genome.combine(this.genome, predatorPartner.genome);
        } else {
            // Reprodução assexuada - clonar com mutações
            childGenome = genome.clone();
            childGenome.mutate();
        }
        
        // Criar filhote próximo à posição atual
        float offsetX = MathUtils.random(-20f, 20f);
        float offsetY = MathUtils.random(-20f, 20f);
        
        return new Predator(position.x + offsetX, position.y + offsetY, worldMap, childGenome);
    }
}
