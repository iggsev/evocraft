package com.evolution.sim.genetics;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

import java.util.HashMap;
import java.util.Map;

/**
 * Representa o genoma de uma criatura, contendo todos os seus traços genéticos
 */
public class Genome {
    // Mapa de traços genéticos
    private Map<String, Float> traits;
    
    // Constantes
    private static final float MUTATION_CHANCE = 0.2f;
    private static final float MUTATION_AMOUNT = 0.2f;
    
    /**
     * Cria um novo genoma com traços aleatórios
     */
    public Genome() {
        traits = new HashMap<>();
        randomizeTraits();
    }
    
    /**
     * Cria um genoma com traços específicos
     * @param traits Mapa de traços
     */
    public Genome(Map<String, Float> traits) {
        this.traits = new HashMap<>(traits);
    }
    
    /**
     * Inicializa traços com valores aleatórios
     */
    private void randomizeTraits() {
        // Traços básicos para todos os tipos de criaturas
        traits.put("size", MathUtils.random(0f, 1f));
        traits.put("speed", MathUtils.random(0f, 1f));
        traits.put("energy", MathUtils.random(0f, 1f));
        traits.put("perception", MathUtils.random(0f, 1f));
        traits.put("strength", MathUtils.random(0f, 1f));
        traits.put("reproduction", MathUtils.random(0f, 1f));
        traits.put("adaption", MathUtils.random(0f, 1f));
    }
    
    /**
     * Obtém o valor de um traço
     * @param traitName Nome do traço
     * @param defaultValue Valor padrão caso o traço não exista
     * @return Valor do traço
     */
    public float getTraitValue(String traitName, float defaultValue) {
        return traits.getOrDefault(traitName, defaultValue);
    }
    
    /**
     * Define o valor de um traço
     * @param traitName Nome do traço
     * @param value Valor do traço (entre 0 e 1)
     */
    public void setTraitValue(String traitName, float value) {
        // Garantir que o valor esteja entre 0 e 1
        value = Math.max(0f, Math.min(1f, value));
        traits.put(traitName, value);
    }
    
    /**
     * Aplica mutações aleatórias aos traços
     */
    public void mutate() {
        for (String traitName : traits.keySet()) {
            // Chance de mutação para cada traço
            if (MathUtils.random() < MUTATION_CHANCE) {
                float currentValue = traits.get(traitName);
                float mutation = MathUtils.random(-MUTATION_AMOUNT, MUTATION_AMOUNT);
                setTraitValue(traitName, currentValue + mutation);
            }
        }
    }
    
    /**
     * Cria um clone deste genoma
     * @return Novo genoma idêntico
     */
    public Genome clone() {
        return new Genome(new HashMap<>(traits));
    }
    
    /**
     * Combina dois genomas para criar um filho
     * @param parent1 Primeiro genoma parental
     * @param parent2 Segundo genoma parental
     * @return Novo genoma combinado
     */
    public static Genome combine(Genome parent1, Genome parent2) {
        // Verificar se ambos os pais são válidos
        if (parent1 == null || parent2 == null) {
            return new Genome();
        }
        
        // Criar novo mapa de traços
        Map<String, Float> childTraits = new HashMap<>();
        
        // Combinar traços dos pais
        for (String traitName : parent1.traits.keySet()) {
            // Se ambos os pais têm o traço
            if (parent2.traits.containsKey(traitName)) {
                float value1 = parent1.traits.get(traitName);
                float value2 = parent2.traits.get(traitName);
                
                // Diferentes estratégias de combinação:
                float combinedValue;
                
                // 1. Média simples
                if (MathUtils.randomBoolean(0.5f)) {
                    combinedValue = (value1 + value2) / 2f;
                } 
                // 2. Favor a um dos pais
                else {
                    combinedValue = MathUtils.randomBoolean() ? value1 : value2;
                }
                
                childTraits.put(traitName, combinedValue);
            } 
            // Se apenas o primeiro pai tem o traço
            else {
                childTraits.put(traitName, parent1.traits.get(traitName));
            }
        }
        
        // Adicionar traços exclusivos do segundo pai
        for (String traitName : parent2.traits.keySet()) {
            if (!parent1.traits.containsKey(traitName)) {
                childTraits.put(traitName, parent2.traits.get(traitName));
            }
        }
        
        // Criar novo genoma
        Genome childGenome = new Genome(childTraits);
        
        // Chance de mutação após combinação
        if (MathUtils.random() < MUTATION_CHANCE * 2) {
            childGenome.mutate();
        }
        
        return childGenome;
    }
    
    /**
     * Calcula compatibilidade genética entre dois genomas
     * @param other Outro genoma
     * @return Valor entre 0 (incompatível) e 1 (compatível)
     */
    public float calculateCompatibility(Genome other) {
        if (other == null) return 0f;
        
        float totalDifference = 0f;
        int traitCount = 0;
        
        // Comparar traços em comum
        for (String traitName : traits.keySet()) {
            if (other.traits.containsKey(traitName)) {
                float value1 = traits.get(traitName);
                float value2 = other.traits.get(traitName);
                
                totalDifference += Math.abs(value1 - value2);
                traitCount++;
            }
        }
        
        // Se não houver traços em comum
        if (traitCount == 0) return 0f;
        
        // Calcular diferença média e convertê-la em compatibilidade
        float averageDifference = totalDifference / traitCount;
        return 1f - averageDifference;
    }
    
    /**
     * Retorna uma representação em string do genoma
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("Genome{");
        
        for (Map.Entry<String, Float> entry : traits.entrySet()) {
            builder.append(entry.getKey())
                   .append("=")
                   .append(String.format("%.2f", entry.getValue()))
                   .append(", ");
        }
        
        // Remover a última vírgula e espaço
        if (!traits.isEmpty()) {
            builder.delete(builder.length() - 2, builder.length());
        }
        
        builder.append("}");
        return builder.toString();
    }
}
