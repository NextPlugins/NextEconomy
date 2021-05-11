# NextEconomy

[![Codacy Badge](https://app.codacy.com/project/badge/Grade/41ceccfd3fa241f3a9741f6996f44ccd)](https://www.codacy.com/gh/NextPlugins/NextEconomy/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=NextPlugins/NextEconomy&amp;utm_campaign=Badge_Grade)

Um sistema simples e completo de uma economia principal para servidores de Minecraft, quase que 100% configurável, com pódio em chat/menu e NPCs, suporte ao PlaceholderAPI, informações salvas em banco de dados SQL e com uma robusta [API](https://github.com/NextPlugins/NextEconomy/tree/main/src/main/java/com/nextplugins/economy/api) ([WIKI](https://docs.eikefs.xyz/)) para desenvolvedores. [Prints in-game](https://imgur.com/gallery/RHlD6dA).

## Comandos
|Comando         |Descrição                      |Permissão                    |
|----------------|-------------------------------|-----------------------------|
|/money           |Abrir o menu do sistema.|Nenhuma    |
|/money enviar    |Envie uma quantia de money para outra pessoa.|`nexteconomy.command.pay`|
|/money top       |Veja o ranking dos jogadores mais ricos do server.|`nexteconomy.command.top`|
|/money ajuda     |Veja os comandos disponíveis do sistema.|`nexteconomy.command.help`|
|/money add       |Adicione uma quantia de money para alguém.|`nexteconomy.command.add`|
|/money set       |Altere a quantia de money de alguém.|`nexteconomy.command.set`|
|/money remove    |Remova uma quantia de money de alguém.|`nexteconomy.command.remove`|
|/money reset     |Zere o saldo de money de algum jogador.|`nexteconomy.command.reset`|
|/money npc       |Veja a ajuda para o sistema de NPCs.|`nexteconomy.command.npc.help`|
|/money npc add   |Adicione uma localização de spawn de NPC.|`nexteconomy.command.npc.add`|
|/money npc remove|Remova uma localização de spawn de NPC.|`nexteconomy.command.npc.remove`|
|/cheque          |Veja os comandos disponíveis do sistema.|`nexteconomy.command.check`|
|/cheque criar    |Crie um cheque com uma certa quantia de dinheiro.|`nexteconomy.command.check.create`|

## Download

Você pode encontrar o plugin pronto para baixar [**aqui**](https://github.com/NextPlugins/NextEconomy/releases), ou se você quiser, pode optar por clonar o repositório e dar build no plugin com suas alterações.

## Configuração

O plugin conta com vários arquivos de configuração, em que se pode configurar quase tudo que você quiser.

## Placeholders

### PlaceholderAPI
-   "{placeholderapi_nexteconomy_amount}" ~ quantia de dinheiro
-   "{placeholderapi_nexteconomy_purse}" ~ valor da bolsa -> Ex.: (28%)
-   "{placeholderapi_nexteconomy_purse_only_value}" ~ valor da bolsa apenas numero -> Ex.: (28)
-   "{placeholderapi_nexteconomy_purse_with_icon}" ~ valor da bolsa -> Ex.: (48% ↗ em alta)
-   "{placeholderapi_nexteconomy_tycoon}" ~ caso o jogador seja o mais rico, irá retornar a tag magnata 

LegendChat: "{tycoon}" ~ tag magnata

## Dependências
O NextEconomy necessita do [Citizens](https://dev.bukkit.org/projects/citizens) para o sistema de NPCs, [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/) para suporte a placeholders, [HolographicDisplays](https://dev.bukkit.org/projects/holographic-displays) para hologramas (utilizado no sistema de ranking por NPC)! OBS: todas essas são dependências opicionais.
As dependências de desenvolvimento serão baixadas automáticamente quando o plugin for habilitado pela primeira vez.

### Tecnologias usadas
-   [caffeine](https://github.com/ben-manes/caffeine) - Uma biblioteca de cache de alto desempenho (Async & Sync).
-   [PDM](https://github.com/knightzmc/pdm) - Faz o download de dependências de desenvolvimento durante o carregamento do servidor.
-   [Lombok](https://projectlombok.org/) - Gera getters, setters e outros métodos útils durante a compilação por meio de anotações.

**APIs e Frameworks**

-   [command-framework](https://github.com/SaiintBrisson/command-framework) - Framework para criação e gerenciamento de comandos.
-   [inventory-api](https://github.com/HenryFabio/inventory-api) - API para criação e o gerenciamento de inventários customizados.
-   [sql-provider](https://github.com/henryfabio/sql-provider) - Provê a conexão com o banco de dados.
-   [configuration-injector](https://github.com/HenryFabio/configuration-injector) - Injetar valores de configurações automaticamente.
-   [Item-NBT-API](https://github.com/tr7zw/Item-NBT-API) - Adicione tags NBT customizadas para itens/tiles/entities sem NMS.
