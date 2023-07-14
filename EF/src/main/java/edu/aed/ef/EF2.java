/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.aed.ef;

/*
Este programa implementa el algoritmo de detección de puntos muertos en transacciones utilizando un grafo de espera. Permite ejecutar comandos de lectura (read), escritura (write) y finalización (end) de transacciones. Cuando se detecta un punto muerto, interrumpe la transacción más reciente y coloca sus pasos al final de la entrada.
El programa utiliza un mecanismo de bloqueo binario para implementar el grafo de espera. Cuando una transacción accede a un registro, se pone un candado en ese registro y ninguna otra transacción puede acceder al mismo registro hasta que la transacción actual termine.
*/
import java.util.*;

public class EF2 {
    private static class Transaction {
        String name;
        List<String> operations;

        public Transaction(String name, List<String> operations) {
            this.name = name;
            this.operations = operations;
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        List<Transaction> transactions = new ArrayList<>();
        Set<String> lockedResources = new HashSet<>();
        Map<String, Set<String>> waitGraph = new HashMap<>();

        String input;
        while (!(input = scanner.nextLine()).equals("end")) {
            String[] tokens = input.split(",");
            String transactionName = tokens[0];
            String operation = tokens[1];

            if (operation.equals("end")) {
                Transaction currentTransaction = findTransactionByName(transactions, transactionName);
                if (currentTransaction != null) {
                    transactions.remove(currentTransaction);
                    releaseResources(currentTransaction, lockedResources, waitGraph);
                }
            } else if (operation.equals("read")) {
                String resource = tokens[2];
                Transaction currentTransaction = findTransactionByName(transactions, transactionName);
                if (currentTransaction != null) {
                    if (lockedResources.contains(resource)) {
                        // Wait for the resource to be released
                        addEdge(waitGraph, transactionName, findTransactionByResource(lockedResources, resource));
                        currentTransaction.operations.add(input);
                    } else {
                        // Acquire the resource
                        lockedResources.add(resource);
                        currentTransaction.operations.add(input);
                    }
                }
            } else if (operation.equals("write")) {
                String resource = tokens[2];
                Transaction currentTransaction = findTransactionByName(transactions, transactionName);
                if (currentTransaction != null) {
                    if (lockedResources.contains(resource)) {
                        // Wait for the resource to be released
                        addEdge(waitGraph, transactionName, findTransactionByResource(lockedResources, resource));
                        currentTransaction.operations.add(input);
                    } else {
                        // Acquire the resource
                        lockedResources.add(resource);
                        currentTransaction.operations.add(input);
                    }
                }
            } else {
                System.out.println("Invalid operation: " + operation);
            }
        }

        // Detect deadlock
        List<String> deadlockCycle = findDeadlockCycle(waitGraph);
        if (deadlockCycle.isEmpty()) {
            System.out.println("No deadlock detected");
        } else {
            System.out.println("Deadlock detected. Transaction " + deadlockCycle.get(deadlockCycle.size() - 1) + " interrupted.");
        }
    }

    private static Transaction findTransactionByName(List<Transaction> transactions, String name) {
        for (Transaction transaction : transactions) {
            if (transaction.name.equals(name)) {
                return transaction;
            }
        }
        return null;
    }

    private static Transaction findTransactionByResource(Set<String> lockedResources, String resource) {
        for (String transactionName : lockedResources) {
            if (transactionName.startsWith("T") && transactionName.substring(1).equals(resource)) {
                return new Transaction(transactionName, new ArrayList<>());
            }
        }
        return null;
    }

    private static void addEdge(Map<String, Set<String>> waitGraph, String source, Transaction destination) {
        if (!waitGraph.containsKey(source)) {
            waitGraph.put(source, new HashSet<>());
        }
        if (destination != null) {
            waitGraph.get(source).add(destination.name);
        }
    }

    private static void releaseResources(Transaction transaction, Set<String> lockedResources, Map<String, Set<String>> waitGraph) {
        for (String operation : transaction.operations) {
            String[] tokens = operation.split(",");
            String resource = tokens[2];
            lockedResources.remove(resource);
            removeEdge(waitGraph, transaction.name, findTransactionByResource(lockedResources, resource));
        }
    }

    private static void removeEdge(Map<String, Set<String>> waitGraph, String source, Transaction destination) {
        if (waitGraph.containsKey(source) && destination != null) {
            waitGraph.get(source).remove(destination.name);
        }
    }

    private static List<String> findDeadlockCycle(Map<String, Set<String>> waitGraph) {
        Set<String> visited = new HashSet<>();
        Set<String> recursionStack = new HashSet<>();
        List<String> cycle = new ArrayList<>();

        for (String transaction : waitGraph.keySet()) {
            if (isCyclic(transaction, visited, recursionStack, waitGraph, cycle)) {
                break;
            }
        }

        return cycle;
    }

    private static boolean isCyclic(String transaction, Set<String> visited, Set<String> recursionStack, Map<String, Set<String>> waitGraph, List<String> cycle) {
        if (recursionStack.contains(transaction)) {
            cycle.add(transaction);
            return true;
        }

        if (!visited.contains(transaction)) {
            visited.add(transaction);
            recursionStack.add(transaction);

            if (waitGraph.containsKey(transaction)) {
                for (String neighbor : waitGraph.get(transaction)) {
                    if (isCyclic(neighbor, visited, recursionStack, waitGraph, cycle)) {
                        cycle.add(transaction);
                        return true;
                    }
                }
            }
        }

        recursionStack.remove(transaction);
        return false;
    }
}
