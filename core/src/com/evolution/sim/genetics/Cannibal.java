package com.evolution.sim.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.evolution.sim.genetics.Genome;
import com.evolution.sim.world.WorldMap;

/**
 * Representa um predador que caça outras criaturas, incluindo da mesma espécie
 */
public class Cannibal extends Predator {
    // Atributos específicos dos canibais
    private float cannibalFactor;  // Quanto prefere atacar da mesma espécie vs. presas normais
    
    public Cannibal(float x, float y, WorldMap worldMap) {
        super(x, y, worldMap);
        
        // Configurações específicas para canibais
        this.maxSpeed = 65f;
        this.size = 7f;
        this.energy = 120f;
        this.maxEnergy = 180f;
        this.maxAge = 70f;  // Vida mais curta que predadores normais
        
        // Valores específicos
        this.cannibalFactor = 0.3f;  // 30% de preferência para canibalismo vs. caça normal
    }
    
    /**
     * Construtor para criar filhotes com genoma
     */
    public Cannibal(float x, float y, WorldMap worldMap, Genome genome) {
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
    @Override
    protected void applyGenome() {
        // Chamar implementação da classe pai
        super.applyGenome();
        
        // Aplicar traços específicos de canibais
        this.cannibalFactor = 0.3f * (1 + genome.getTraitValue("cannibalism", 0.5f) - 0.25f);
    }
    
    @Override
    protected Color getColor() {
        // Cor roxa escura para canibais
        return new Color(0.6f, 0.1f, 0.6f, 1.0f);
    }
    
    @Override
    public Creature reproduce(Creature partner) {
        // Verificar se pode reproduzir
        if (!canReproduce()) {
            return null;
        }
        
        // Consumir energia para reprodução
        energy *= 0.7f;
        
        // Criar novo genoma
        Genome childGenome;
        
        if (partner != null && partner instanceof Cannibal && partner.canReproduce()) {
            // Reprodução sexual - misturar genomas
            Cannibal cannibalPartner = (Cannibal) partner;
            cannibalPartner.energy *= 0.7f; // Parceiro também gasta energia
            
            childGenome = Genome.combine(this.genome, cannibalPartner.genome);
        } else {
            // Reprodução assexuada - clonar com mutações
            childGenome = genome.clone();
            childGenome.mutate();
        }
        
        // Criar filhote próximo à posição atual
        float offsetX = MathUtils.random(-20f, 20f);
        float offsetY = MathUtils.random(-20f, 20f);
        
        return new Cannibal(position.x + offsetX, position.y + offsetY, worldMap, childGenome);
    }
}
