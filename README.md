# 🖨️ Sistema de Controle da Impressora Elgin
---

## 📘 Sobre o Projeto

Este projeto é um programa de console em Java que se comunica com uma impressora térmica **Elgin i8/i9** usando a biblioteca `E1_Impressora01.dll`, por meio do JNA. O sistema pede login (com até 3 tentativas) e mostra um menu com 12 operações: configurar e abrir a conexão, imprimir texto, QR Code, código de barras e cupons SAT, abrir a gaveta, tocar o bip, avançar o papel e ver o status do papel. Foi desenvolvido por **Gustavo Mercês** na disciplina de **Programação Orientada a Objetos**.

---

## 🧰 Tecnologias Utilizadas

- **Java 17** (ou superior)
- **IntelliJ IDEA**
- **JNA 5.15.0** (comunicação com a DLL)
- **DLL E1_Impressora01** (SDK da Elgin)
- **Impressora Elgin *(i8 ou i9 — confira o modelo escrito na impressora do laboratório)***

---

## 📁 Estrutura do Projeto

```
/src
   Main.java           → Inicia o programa: cria os objetos, faz o login (3 tentativas) e abre o menu
   Usuario.java        → Guarda o login e a senha e confere se estão certos
   Impressora.java     → Guarda os dados da conexão e tem um método para cada operação da impressora
   Menu.java           → Mostra o menu em laço, lê os dados do teclado e chama a Impressora
   ImpressoraDLL.java  → Interface JNA que liga o Java à DLL da Elgin (fornecida, não alterada)
/Bibliotecas E1 Impressora → DLLs da Elgin (x64 e x86)
/libs Java                 → jna-5.15.0.jar
XMLSAT.xml                 → Cupom SAT de venda (exemplo)
CANC_SAT.xml               → Cupom SAT de cancelamento (exemplo)
```

### Como as classes se organizam

- **Encapsulamento:** os dados da conexão (`tipo`, `modelo`, `conexao`, `parametro`) e o `boolean conexaoAberta` são atributos privados da classe `Impressora`. Ninguém fora dela fala direto com a DLL.
- **Conexão obrigatória:** `conexaoAberta` começa `false` e só vira `true` quando `AbreConexaoImpressora` devolve 0. O método `verificarConexao()` é escrito uma vez só e usado em todas as opções de impressão; o `Menu` chama esse método **antes** de pedir qualquer dado, então ninguém digita à toa.
- **Retorno conferido:** todo comando da DLL tem o retorno verificado (0 = sucesso). Para `ImpressaoTexto`, só é erro se o retorno for negativo.

---

## ⚙️ Instalação e Execução

### 1. Baixar o projeto
1. Acesse o repositório no GitHub.
2. Clique em **Code → Download ZIP**.
3. Extraia o arquivo.

### 2. Abrir no IntelliJ IDEA
1. **File → Open** e escolha a pasta extraída.
2. Em **File → Project Structure → Modules**, marque a pasta `src` como **Sources**.
3. Em **File → Project Structure → Libraries → + → Java**, escolha `libs Java/jna-5.15.0.jar`.
4. Em **Run → Edit Configurations**, confira se o **Working directory** é a pasta principal do projeto (onde está a pasta `Bibliotecas E1 Impressora`). Sem isso a DLL não é encontrada.
5. Conecte a impressora no USB e ligue.
6. Abra `Main.java` e clique em ▶️ **Run**.

> Se aparecer `package com.sun.jna does not exist`, o JNA ainda não foi adicionado (passo 3).

---

## 🔐 Login

| Usuário | Senha |
|---|---|
| `admin` | `1234` |

São permitidas **3 tentativas**. A cada erro o programa mostra quantas tentativas restam; na terceira tentativa errada o acesso é bloqueado e o programa encerra.

---

## 📋 Funcionalidades do Menu

| Opção | O que faz | Comando da DLL |
|---|---|---|
| 1 | Configurar conexão (tipo, modelo, porta e parâmetro digitados pelo usuário) | — |
| 2 | Abrir conexão com a impressora | `AbreConexaoImpressora` |
| 3 | Fechar conexão | `FechaConexaoImpressora` |
| 4 | Imprimir texto (centralizado, em negrito, com quantidade de cópias) | `ImpressaoTexto` + `Corte` |
| 5 | Imprimir QR Code | `ImpressaoQRCode` |
| 6 | Imprimir código de barras CODE 128 | `ImpressaoCodigoBarras` |
| 7 | Imprimir cupom SAT de venda a partir do `XMLSAT.xml` | `ImprimeXMLSAT` |
| 8 | Imprimir cupom de cancelamento a partir do `CANC_SAT.xml` | `ImprimeXMLCancelamentoSAT` |
| 9 | Abrir a gaveta de dinheiro | `AbreGaveta` |
| 10 | Sinal sonoro (quantidade de bips escolhida pelo usuário) | `SinalSonoro` |
| 11 | Avançar papel (quantidade de linhas escolhida pelo usuário) | `AvancaPapel` |
| 12 | Ver status do papel (tem papel / acabando / sem papel) | `StatusImpressora` |
| 0 | Fecha a conexão (se estiver aberta) e sai | `FechaConexaoImpressora` |

Configuração padrão da conexão: tipo `1` (USB), modelo `i9`, conexão `USB`, parâmetro `0`.

---

## 🧪 Testes Realizados

| Teste | Resultado |
|---|---|
| Login com senha errada 3 vezes | Mostra as tentativas restantes e bloqueia o acesso ✅ |
| Login com usuário e senha corretos | Abre o menu ✅ |
| Digitar letras no lugar de número no menu | Avisa "Digite apenas números" e pergunta de novo ✅ |
| Escolher uma opção que não existe (ex.: 99) | Avisa "Opção inválida" e volta ao menu ✅ |
| Tentar imprimir com a conexão fechada | Avisa "Conexão fechada. Abra a conexão antes." sem pedir dados ✅ |
| Abrir conexão sem a DLL disponível | Mostra mensagem de erro e o programa continua ✅ |
| Abrir conexão com a impressora | *(preencher após o teste no laboratório)* |
| Imprimir texto / QR Code / código de barras | *(preencher)* |
| Imprimir XML SAT e cancelamento | *(preencher)* |
| Abrir gaveta / sinal sonoro / avançar papel / status | *(preencher)* |

---

## 📚 Documentação Adicional

- [Documentação oficial dos comandos da Elgin (E1_Impressora)](https://elgindevelopercommunity.github.io/group___m1.html)
- Manual da impressora Elgin i8 / i9

---

## 👤 Autor

- Gustavo Mercês
