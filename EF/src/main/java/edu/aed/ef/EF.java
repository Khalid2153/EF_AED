/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package edu.aed.ef;

import java.util.ArrayList;
import java.util.List;

public class EF {

    private static final int INF = Integer.MAX_VALUE / 2;

    /**
     * Encuentra la ruta óptima en un grafo
     */
    public static List<Integer> encontrarRutaOptima(int[][] grafo) {
        // Crea una matriz para almacenar la distancia entre cada pareja de vértices
        int[][] matrizRecorrido = new int[grafo.length][grafo.length];

        // Obtiene los vértices impares del grafo
        List<Integer> verticesImpares = obtenerVerticesImpares(grafo);

        // Para cada vértice impar `v`, completa las distancias entre `v` y los otros vértices impares
        for (int v : verticesImpares) {
            for (int u : verticesImpares) {
                if (grafo[v][u] != 0 && grafo[v][u] != INF) {
                    // Calcula la distancia entre `v` y `u`
                    int distancia = grafo[v][u];

                    // Si la distancia es menor que la distancia actual entre `v` y `u`, actualiza la distancia
                    if (distancia < matrizRecorrido[v][u]) {
                        matrizRecorrido[v][u] = distancia;
                        matrizRecorrido[u][v] = distancia;
                    }
                }
            }
        }

        // Utiliza el algoritmo de camino euleriano para encontrar la ruta óptima
        List<Integer> rutaOptima = new ArrayList<>();
        euleriano(grafo, matrizRecorrido, verticesImpares.get(0), rutaOptima);

        return rutaOptima;
    }

    /**
     * Algoritmo de camino euleriano para encontrar la ruta óptima
     */
    private static void euleriano(int[][] grafo, int[][] matrizRecorrido, int actual, List<Integer> rutaOptima) {
        // Para cada vértice `u` adyacente al vértice actual, llama al método `euleriano()` con el vértice `u` como parámetro
        for (int u : grafo[actual]) {
            if (grafo[actual][u] != 0 && grafo[actual][u] != INF) {
                grafo[actual][u] = 0;
                grafo[u][actual] = 0;
                euleriano(grafo, matrizRecorrido, u, rutaOptima);
            }
        }

        // Agrega el vértice actual a la ruta óptima
        rutaOptima.add(actual);
    }

    /**
     * Obtiene los vértices impares de un grafo
     */
    private static List<Integer> obtenerVerticesImpares(int[][] grafo) {
        // Crea una lista para almacenar los vértices impares
        List<Integer> verticesImpares = new ArrayList<>();

        // Para cada vértice `v` en el grafo, cuente el número de aristas que conectan `v` a otros vértices
        for (int i = 0; i < grafo.length; i++) {
            int grado = 0;
            for (int j = 0; j < grafo.length; j++) {
                if (grafo[i][j] != 0 && grafo[i][j] != INF) {
                    grado++;
                }
            }

            // Si el número de aristas que conectan `v` a otros vértices es impar, agregue `v` a la lista de vértices impares
            if (grado % 2 != 0) {
                verticesImpares.add(i);
            }
        }

        return verticesImpares;
    }
}