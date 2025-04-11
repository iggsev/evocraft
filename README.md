# Evolution Simulator

Um simulador de ecossistema 2D com evolução natural onde criaturas competem, caçam, se adaptam e evoluem organicamente. Implementado em LibGDX para Android.

![Imagem de Demonstração](docs/images/preview.png)

## 🌟 Visão Geral

Este projeto é um simulador de vida artificial onde diferentes tipos de criaturas (presas, predadores e canibais) interagem em um ambiente customizável. Através de um processo de evolução por seleção natural, as criaturas se adaptam ao seu ambiente ao longo do tempo.

### Características Principais

- 🗺️ **Editor de Mundo**: Crie e modifique terrenos (água, florestas, montanhas, neve, etc.)
- 🧬 **Sistema Genético**: Criaturas herdam características dos pais com possíveis mutações
- 🦊 **Diferentes Espécies**: Presas, predadores e canibais com comportamentos únicos
- 🌱 **Evolução Natural**: As criaturas se adaptam ao ambiente com o passar do tempo
- 🌦️ **Sistema Climático**: Mudanças climáticas afetam o comportamento e sobrevivência das criaturas
- 📱 **Multiplataforma**: Funciona em Android e Desktop (via LibGDX)

## 🚀 Como Começar

### Pré-requisitos

- JDK 11 ou superior
- Android Studio (com SDK Android configurado)
- Git

### Configuração para Desenvolvimento

1. Clone o repositório
   ```bash
   git clone https://github.com/SEU_USUARIO/evolution-simulator.git
   cd evolution-simulator
   ```

2. Configure o Android SDK (se não configurado)
   - Crie um arquivo `local.properties` na raiz do projeto
   - Adicione: `sdk.dir=/caminho/para/seu/android/sdk`

3. Compile o projeto
   ```bash
   # Para usuários Mac/Linux
   chmod +x gradlew
   ./gradlew build
   
   # Para usuários Windows
   gradlew.bat build
   ```

### Executando no Android

```bash
./gradlew android:installDebug
```

### Executando no Desktop (para desenvolvimento)

```bash
./gradlew desktop:run
```

## 🧩 Estrutura do Projeto

```
evolution-simulator/
├── android/              # Código específico do Android
├── core/                 # Código principal do jogo
│   └── src/
│       └── com/evolution/sim/
│           ├── screens/  # Telas do jogo
│           ├── entities/ # Criaturas e entidades
│           ├── world/    # Sistema de terreno e mundo
│           ├── genetics/ # Sistema genético
│           └── ui/       # Interface de usuário
├── desktop/              # Configuração para desktop
├── assets/               # Recursos gráficos e sons
└── docs/                 # Documentação
```

## 🛠️ Tecnologias Utilizadas

- **LibGDX**: Framework de jogos multiplataforma
- **Java/Kotlin**: Linguagens de programação principais
- **Gradle**: Sistema de build
- **Box2D**: Física simples para movimentação das criaturas

## 🤝 Como Contribuir

Adoraríamos sua contribuição para o Evolution Simulator! Aqui estão algumas formas de ajudar:

1. **Reportar bugs**: Abra uma issue descrevendo o problema
2. **Sugerir melhorias**: Novas funcionalidades são bem-vindas
3. **Enviar Pull Requests**: Para correções ou novas funcionalidades

### Processo para contribuir

1. Faça um fork do repositório
2. Crie um branch para sua feature (`git checkout -b feature/nova-funcionalidade`)
3. Faça commit das suas mudanças (`git commit -m 'Adiciona nova funcionalidade'`)
4. Faça push para o branch (`git push origin feature/nova-funcionalidade`)
5. Abra um Pull Request

### Áreas que precisam de ajuda

- 🎨 **Gráficos**: Precisamos de sprites melhores para as criaturas e terrenos
- 🧠 **IA**: Algoritmos mais complexos para o comportamento das criaturas
- 📊 **Estatísticas**: Sistema para visualizar a evolução das espécies ao longo do tempo
- 🧪 **Simulações**: Implementação de diferentes cenários de simulação

## 🗺️ Roadmap

- [x] Editor de mapa básico
- [ ] Sistema de criaturas com comportamentos básicos
- [ ] Implementação do sistema genético
- [ ] Interface de usuário melhorada
- [ ] Sistema climático
- [ ] Estatísticas e gráficos da simulação
- [ ] Diferentes biomas com características únicas

## 📜 Licença

Este projeto está licenciado sob a Licença MIT - veja o arquivo [LICENSE](LICENSE) para detalhes.

## 👥 Time

- **[Seu Nome]** - Idealizador e Desenvolvedor Principal

## 🙏 Agradecimentos

- Inspirado por simuladores de vida artificial como "Spore" e "Species"
- Comunidade LibGDX pela excelente documentação
