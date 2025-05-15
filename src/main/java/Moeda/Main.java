package Moeda;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.Set;

public class Main {
    private static final String API_KEY = "";
    private static final String BASE_URL = "https://v6.exchangerate-api.com/v6/";
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final Gson gson = new Gson();

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/taxa", exchange -> {
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");

            String query = exchange.getRequestURI().getQuery();
            String moedaOrigem = "";
            String moedaDestino = "";
            String valorStr = null;
            if (query != null) {
                for (String param : query.split("&")) {
                    String[] pair = param.split("=");
                    if (pair.length == 2) {
                        if (pair[0].equals("origem")) moedaOrigem = pair[1];
                        if (pair[0].equals("destino")) moedaDestino = pair[1];
                        if (pair[0].equals("valor")) valorStr = pair[1];
                    }
                }
            }

            try {
                double taxa = obterTaxaCambio(moedaOrigem, moedaDestino);
                StringBuilder resposta = new StringBuilder("{\"taxa\": " + taxa);
                if (valorStr != null) {
                    double valor = Double.parseDouble(valorStr);
                    double valorConvertido = converter(moedaOrigem, moedaDestino, valor);
                    resposta.append(", \"valorConvertido\": ").append(valorConvertido);
                }
                resposta.append("}");
                exchange.sendResponseHeaders(200, resposta.length());
                OutputStream os = exchange.getResponseBody();
                os.write(resposta.toString().getBytes());
                os.close();
            } catch (NumberFormatException e) {
                String erro = "{\"erro\": \"Valor inválido: " + valorStr + "\"}";
                exchange.sendResponseHeaders(400, erro.length());
                exchange.getResponseBody().write(erro.getBytes());
                exchange.getResponseBody().close();
            } catch (Exception e) {
                String erro = "{\"erro\": \"Falha ao buscar taxa: " + e.getMessage() + "\"}";
                exchange.sendResponseHeaders(500, erro.length());
                exchange.getResponseBody().write(erro.getBytes());
                exchange.getResponseBody().close();
            }
        });

        server.createContext("/moedas", exchange -> {
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");

            try {
                Set<String> listaMoedas = obterMoedasDisponiveis("USD");
                String dadosJson = gson.toJson(listaMoedas);
                exchange.sendResponseHeaders(200, dadosJson.length());
                exchange.getResponseBody().write(dadosJson.getBytes());
                exchange.getResponseBody().close();
            } catch (Exception e) {
                String erro = "{\"erro\": \"Falha ao buscar moedas: " + e.getMessage() + "\"}";
                exchange.sendResponseHeaders(500, erro.length());
                exchange.getResponseBody().write(erro.getBytes());
                exchange.getResponseBody().close();
            }
        });

        server.start();
        System.out.println("Servidor rodando em http://localhost:8080");
    }

    private static double obterTaxaCambio(String moedaOrigem, String moedaDestino) throws IOException, InterruptedException {
        String url = BASE_URL + API_KEY + "/latest/" + moedaOrigem;
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Falha na API de câmbio");
        }

        Map<String, Object> dadosJson = gson.fromJson(response.body(), new TypeToken<Map<String, Object>>(){}.getType());
        Map<String, Double> taxas = (Map<String, Double>) dadosJson.get("conversion_rates");

        if (!taxas.containsKey(moedaDestino)) {
            throw new RuntimeException("Moeda não suportada: " + moedaDestino);
        }

        return taxas.get(moedaDestino);
    }

    private static Set<String> obterMoedasDisponiveis(String moedaOrigem) throws IOException, InterruptedException {
        String url = BASE_URL + API_KEY + "/latest/" + moedaOrigem;
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Falha na API de câmbio");
        }

        Map<String, Object> dadosJson = gson.fromJson(response.body(), new TypeToken<Map<String, Object>>(){}.getType());
        Map<String, Double> taxas = (Map<String, Double>) dadosJson.get("conversion_rates");

        return taxas.keySet();
    }

    private static double converter(String origem, String destino, double valor) throws IOException, InterruptedException {
        double taxa = obterTaxaCambio(origem, destino);
        return valor * taxa;
    }
}
