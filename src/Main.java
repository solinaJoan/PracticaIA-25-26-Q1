
import IA.Gasolina.CentrosDistribucion;
import IA.Gasolina.Gasolineras;
import aima.search.framework.GraphSearch;
import aima.search.framework.Problem;
import aima.search.framework.Search;
import aima.search.framework.SearchAgent;
import aima.search.informed.AStarSearch;
import aima.search.informed.HillClimbingSearch;
import aima.search.informed.IterativeDeepeningAStarSearch;
import aima.search.informed.SimulatedAnnealingSearch;

import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import IA.PracticaBoard;
import IA.PracticaGoalTest;
import IA.PracticaHeuristicFunction;
import IA.PracticaSuccsesorFunction;


import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Selecciona l'experiment a executar (1-8):");
            int experiment = scanner.nextInt();

            switch (experiment) {
                case 1:
                    System.out.println("Executant experiment 1: Comparació d'operadors amb Hill Climbing");
                    experiment1();
                    break;

                case 2:
                    System.out.println("Executant experiment 2: Comparació d'estrategias d'inicializació");
                    experiment2();
                    break;

                case 3:
                    System.out.println("Executant experiment 3: Optimizació de paràmetres per Simulated Annealing");
                    experiment3();
                    break;

                case 4:
                    System.out.println("Executant experiment 4: Escalabilitat amb tamany del problema");
                    experiment4();
                    break;

                case 5:
                    System.out.println("Executant experiment 5: Reducció de Centres a la mitat");
                    experiment5();
                    break;

                case 6:
                    System.out.println("Executant experiment 6: Efecte del cost per kilòmetre");
                    experiment6();
                    break;

                case 7:
                    System.out.println("Executant experiment 7: Variació de l'horario de feina");
                    experiment7();
                    break;

                case 8:
                    System.out.println("Executant experiment 8: Especial amb semilla 1234");
                    experiment8();
                    break;

                default:
                    System.out.println("Experiment no vàlid. Ha de ser entre 1 i 8.");
            }
        }
    }

    private static void experiment1() {
        // Implementar comparación de operadores para Hill Climbing
        // Escenario: 10 Centres, 10 camiones, 100 gasolineres
        System.out.println("Comparant operadors per funció heuristica...");
        // Tu implementación aquí
    }

    private static void experiment2() {
        // Comparar estrategias de inicialización
        System.out.println("Comparant estrategies d'inicialització...");
        // Tu implementación aquí
    }

    private static void experiment3() {
        // Optimizar parámetros de Simulated Annealing
        System.out.println("Optimizant paràmetres de Simulated Annealing...");
        // Tu implementación aquí
    }

    private static void experiment4() {
        // Estudiar escalabilidad
        System.out.println("Analitzant escalabilitat amb tamany creixent...");
        // Proporción 10:100 (Centres:gasolineres)
        int[] tamanos = {10, 20, 30, 40, 50}; // Incrementando de 10 en 10
        for (int tam : tamanos) {
            System.out.println("Probando con " + tam + " Centres i " + (tam * 10) + " gasolineres");
            // Tu implementación aquí
        }
    }

    private static void experiment5() {
        // Reducir Centres a la mitad (5 Centres, 10 camiones, 100 gasolineres)
        System.out.println("Analitzant reducció de Centres a la mitat...");
        // Tu implementación aquí
    }

    private static void experiment6() {
        // Estudiar efecto del coste por kilómetro
        System.out.println("Analitzant efecte del cost per kilòmetre...");
        double[] costes = {2, 4, 8, 16, 32}; // Doblando el coste
        for (double coste : costes) {
            System.out.println("Probando con coste: " + coste + " por km");
            // Tu implementación aquí
        }
    }

    private static void experiment7() {
        // Variar horario de trabajo (±1 hora)
        System.out.println("Analitzant variació de l'horari de feina...");
        // Tu implementación aquí
    }

    private static void experiment8() {
        System.out.println("Executant experiment especial");
        // 10 Centres, 1 camió per centre, 100 gasolineres
        Gasolineras GS = new Gasolineras(10, 1234);
        CentrosDistribucion CD = new CentrosDistribucion(10, 1, 1234);
        long startTime = System.currentTimeMillis();

        try {
            Problem problem =  new Problem(new PracticaBoard(GS, CD),
                    new PracticaSuccsesorFunction(),
                    new PracticaGoalTest(),
                    new PracticaHeuristicFunction());

            Search search =  new HillClimbingSearch();
            SearchAgent agent = new SearchAgent(problem,search);

            System.out.println();
            printActions(agent.getActions());
            printInstrumentation(agent.getInstrumentation());
        } catch (Exception e) {
            e.printStackTrace();
        }

        long endTime = System.currentTimeMillis();
        long tiempoEjecucion = endTime - startTime;

        System.out.println("Benefici obtingut: [INSERTAR_RESULTADO]");
        System.out.println("Temps d'execució: " + tiempoEjecucion + " ms");
    }

    private static void printInstrumentation(Properties properties) {
        Iterator keys = properties.keySet().iterator();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            String property = properties.getProperty(key);
            System.out.println(key + " : " + property);
        }

    }

    private static void printActions(List actions) {
        for (int i = 0; i < actions.size(); i++) {
            String action = (String) actions.get(i);
            System.out.println(action);
        }
    }
}