

# 💵 Conversor de Moedas – API REST em Java

Este é um projeto simples e funcional de **API REST em Java puro**, que permite realizar conversões de moedas em tempo real utilizando a [ExchangeRate-API](https://www.exchangerate-api.com/).

O sistema foi construído com foco em organização, clareza e aplicação de princípios de **código limpo** e **boas práticas**, incluindo o tratamento adequado de erros e modularização.

---

## 🚀 Funcionalidades

* Obter taxa de câmbio entre duas moedas.
* Converter um valor de uma moeda para outra.
* Listar todas as moedas suportadas pela API externa.

---

## 🛠 Tecnologias Utilizadas


* **HttpServer** (Java embutido)
* **HttpClient** (Java nativo para requisições HTTP)
* **Gson** (para manipulação de JSON)
* **ExchangeRate-API** (para dados de câmbio)

---

## 🌐 Endpoints Disponíveis

| Método | Rota      | Descrição                                                                                                                             |
| ------ | --------- | ------------------------------------------------------------------------------------------------------------------------------------- |
| GET    | `/taxa`   | Retorna a taxa de câmbio entre duas moedas e, se informado, o valor convertido. <br>Exemplo: `/taxa?origem=BRL&destino=USD&valor=100` |
| GET    | `/moedas` | Retorna a lista de moedas disponíveis para conversão.                                                                                 |

---

## ▶️ Como Executar

### 1. Clonar o Repositório

```bash
git clone https://github.com/keilanumm46/ConversorMoeda.git
cd conversor-moedas-java
```

### 2. Compilar e Rodar

```bash
javac Moeda/Main.java
java Moeda.Main
```

> O servidor será iniciado em: [http://localhost:8080](http://localhost:8080)

---

## 📌 Observações

* É necessário ter o Java instalado na máquina (Java 17 ou superior recomendado).
* A API externa exige uma chave válida (API Key). Verifique se a sua está ativa e atualizada.


