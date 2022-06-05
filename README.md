# NextEconomy

[![Codacy Badge](https://app.codacy.com/project/badge/Grade/41ceccfd3fa241f3a9741f6996f44ccd)](https://www.codacy.com/gh/NextPlugins/NextEconomy/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=NextPlugins/NextEconomy&amp;utm_campaign=Badge_Grade)

Um sistema completo de economia para servidores de Minecraft, com várias opções configuráveis, com pódio em chat/menu,
NPCs, ArmorStands e Hologramas. Informações salvas em MySQL ou SQLite (opção a escolha do utilizador), além de haver uma
robusta [API](https://github.com/NextPlugins/NextEconomy/tree/main/src/main/java/com/nextplugins/economy/api) para
programadores. Veja mais [aqui](https://imgur.com/gallery/xDfx9pp).

Veja a reação da comunidade sobre o
plugin [aqui](https://gamersboard.com.br/topic/75000-nexteconomy-o-mais-completo-plugin-de-economia-da-comunidade/)

# Estatísticas de uso

<p align="center">
    <a href="https://bstats.org/plugin/bukkit/NextEconomy" title="Estatísticas do NextEconomy">
        <img src="https://bstats.org/signatures/bukkit/NextEconomy.svg"  alt=""/>
    </a>
</p>


## Comandos

|Comando         |Descrição                      |Permissão                    |
|----------------|-------------------------------|-----------------------------|
|/coins ou /money |Abrir o menu do sistema ou mostrar a mensagem de ajuda.|Nenhuma    |
|/coins enviar    |Envie uma quantia de money para outra pessoa.|`nexteconomy.command.pay`|
|/coins top       |Veja o ranking dos jogadores mais ricos do server.|Nenhuma         |
|/coins toggle    |Ative/Desative o recebimento de coins.|`nexteconomy.togglecoins`   |
|/coins vincular  |Vincular sua conta com o discord.|Nenhuma                          |
|/coins desvincular  |Desvincular sua conta do discord.|Nenhuma                       |
|/bolsa           |Veja informações do sistema de bolsa.|Nenhuma                      |
|/setbolsa        |Definir o valor da bolsa.|`nexteconomy.setpurse`                   |
|/cheque          |Veja os comandos disponíveis do sistema.|`nexteconomy.command.check`|
|/cheque criar    |Crie um cheque com uma certa quantia de dinheiro.|`nexteconomy.command.check`|
|/coins ajuda     |Veja os comandos disponíveis do sistema.|Nenhuma                   |
|/coins add       |Adicione uma quantia de money para alguém.|`nexteconomy.command.add`|
|/coins set       |Altere a quantia de money de alguém.|`nexteconomy.command.set`     |
|/coins remove    |Remova uma quantia de money de alguém.|`nexteconomy.command.remove`|
|/coins reset     |Zere o saldo de money de algum jogador.|`nexteconomy.command.reset`|
|/coins npc       |Veja a ajuda para o sistema de NPCs.|`nexteconomy.command.npc.help`|
|/coins npc add   |Adicione uma localização de spawn de NPC.|`nexteconomy.command.npc.add`|
|/coins npc remove|Remova uma localização de spawn de NPC.|`nexteconomy.command.npc.remove`|
|/nexteconomy ou /ne |Comandos administrativos.|`nexteconomy.admin`                      |

## Download

Você pode encontrar o plugin pronto para baixar [**aqui**](https://github.com/NextPlugins/NextEconomy/releases), ou se
você quiser, pode optar por clonar o repositório e dar build no plugin com suas alterações.

## Configuração

O plugin conta com vários arquivos de configuração, que pode ser facilmente manipulado por qualquer pessoa, além de você
poder moldar ao seu modo.

## Permissões adicionais

- nexteconomy.bypass ~ Poder enviar dinheiro a um jogador que desativou o recebimento de coins.

## Placeholders

### PlaceholderAPI

- "%nexteconomy_amount%" ~ quantia de dinheiro;
- "%nexteconomy_purse%" ~ valor da bolsa -> Ex.: (28%);
- "%nexteconomy_purse_only_value%" ~ valor da bolsa apenas numero -> Ex.: (28);
- "%nexteconomy_purse_with_icon%" ~ valor da bolsa -> Ex.: (48% ↗ em alta);
- "%nexteconomy_tycoon%" ~ retornará tag rico ou magnata caso o jogador esteja no top ranking.
- "%nexteconomy_tycoon_name%" ~ retornará o nome do jogador magnata (o primeiro no ranking).

OBS: Caso utilize FeatherBoard, as placeholders serão {placeholderapi_nexteconomy_...}, exemplo:
{placeholderapi_nexteconomy_amount}

### LegendChat, nChat ou UltimateChat

- "{tycoon}" ~ tag magnata ou rico.

## Dependências

### Obrigatórias

- [Vault](https://github.com/MilkBowl/VaultAPI) - para suporte a todos os plugins que exigem uma economia (Spawners,
  Maquinas, Lojas, etc.).

### Opcionais

- [Citizens](https://dev.bukkit.org/projects/citizens) - para o sistema de NPCs;
- [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/) - para ter suporte a placeholders;
- [HolographicDisplays](https://dev.bukkit.org/projects/holographic-displays) - para hologramas (utilizado no sistema de
  ranking por NPC).

## Informações de desenvolvimento

### Tecnologias usadas

- [caffeine](https://github.com/ben-manes/caffeine) - Uma biblioteca de cache de alto desempenho (Async & Sync);
- [Lombok](https://projectlombok.org/) - Gera getters, setters e outros métodos útils durante a compilação por
  anotações.

### APIs e Frameworks

- [command-framework](https://github.com/SaiintBrisson/command-framework) - Framework para criação e gestão de comandos;
- [inventory-api](https://github.com/HenryFabio/inventory-api) - API para criação e a gestão de inventários
  customizados;
- [sql-provider](https://github.com/henryfabio/sql-provider) - Provê a conexão com o banco de dados;
- [configuration-injector](https://github.com/HenryFabio/configuration-injector) - Injetar valores de configurações
  automaticamente;
- [Item-NBT-API](https://github.com/tr7zw/Item-NBT-API) - Adicione tags NBT customizadas para itens/tiles/entities sem
  NMS.
