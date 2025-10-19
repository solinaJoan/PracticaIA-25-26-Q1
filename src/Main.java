import IA.Gasolina.CentrosDistribucion;
import IA.Gasolina.Gasolineras;
import aima.search.framework.Problem;
import aima.search.framework.Search;
import aima.search.framework.SearchAgent;
import aima.search.informed.HillClimbingSearch;
import aima.search.informed.SimulatedAnnealingSearch;

import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import IA.PracticaBoard;
import IA.PracticaGoalTest;
import IA.PracticaHeuristicFunction;
import IA.PracticaSuccessorFunction;
import IA.PracticaSuccessorFunctionSA;

public class Main {

    // Constants per experiments
    private static final int NUM_REPETICIONS = 10;

    public static void main(String[] args) {
        if (args.length > 0) {
            // Executar experiment específic des de línia de comandes
            int experiment = Integer.parseInt(args[0]);
            executarExperiment(experiment);
        } else {
            // Menú interactiu
            menuInteractiu();
        }
    }

    private static void menuInteractiu() {
        java.util.Scanner scanner = new java.util.Scanner(System.in);

        while (true) {
            System.out.println("\n" + "=".repeat(60));
            System.out.println("     PRÀCTICA DE CERCA LOCAL - IA");
            System.out.println("=".repeat(60));
            System.out.println("\nSelecciona l'experiment a executar:");
            System.out.println("  1. Comparació d'operadors (Hill Climbing)");
            System.out.println("  2. Comparació d'estratègies d'inicialització");
            System.out.println("  3. Optimització paràmetres Simulated Annealing");
            System.out.println("  4. Escalabilitat (temps vs tamany)");
            System.out.println("  5. Reducció de centres a la meitat");
            System.out.println("  6. Efecte del cost per kilòmetre");
            System.out.println("  7. Variació de l'horari de feina");
            System.out.println("  8. Experiment ESPECIAL ");
            System.out.println("  9. Prova ràpida (debug)");
            System.out.println("  0. Sortir");
            System.out.print("\nOpció: ");

            int opcio = scanner.nextInt();

            if (opcio == 0) {
                System.out.println("\nAdéu!");
                break;
            }

            if (opcio >= 1 && opcio <= 9) {
                executarExperiment(opcio);
            } else {
                System.out.println("\n❌ Opció no vàlida!");
            }
        }

        scanner.close();
    }

    private static void executarExperiment(int num) {
        System.out.println("\n" + "=".repeat(60));
        switch (num) {
            case 1:
                experiment1_ComparacioOperadors();
                break;
            case 2:
                experiment2_ComparacioInicialitzacio();
                break;
            case 3:
                experiment3_ParametresSA();
                break;
            case 4:
                experiment4_Escalabilitat();
                break;
            case 5:
                experiment5_ReduccioCentres();
                break;
            case 6:
                experiment6_CostKilometre();
                break;
            case 7:
                experiment7_VariacioHorari();
                break;
            case 8:
                experiment8_Especial();
                break;
            case 9:
                provaRapida();
                break;
            default:
                System.out.println("Experiment no implementat");
        }
        System.out.println("=".repeat(60));
    }

    // ============================================================================
    // EXPERIMENT 1: Comparació d'Operadors
    // ============================================================================
    private static void experiment1_ComparacioOperadors() {
        System.out.println("EXPERIMENT 1: Comparació d'Operadors amb Hill Climbing");
        System.out.println("Escenari: 10 centres, 1 camió/centre, 100 gasolineres");
        System.out.println();

        // Definim diferents conjunts d'operadors a provar
        ConjuntOperadors[] conjunts = {
                // Conjunt 1: Operadors bàsics (Afegir + Crear)
                new ConjuntOperadors("Bàsics (Afegir+Crear)", true, false, false, false, true),

                // Conjunt 2: Operadors de modificació (Afegir + Treure + Moure)
                new ConjuntOperadors("Modificació (Afegir+Treure+Moure)", true, true, true, false, false),

                // Conjunt 3: Tots els operadors
                new ConjuntOperadors("Tots", true, true, true, true, true),

                // Conjunt 4: Sense intercanvi (Afegir + Treure + Moure + Crear)
                new ConjuntOperadors("Sense Intercanvi", true, true, true, false, true),

                // Conjunt 5: Només moviments (Moure + Intercanviar)
                new ConjuntOperadors("Només Moviments", false, false, true, true, false)
        };

        System.out.println("Provant " + conjunts.length + " conjunts d'operadors diferents...\n");

        // Per cada conjunt d'operadors
        for (ConjuntOperadors conjunt : conjunts) {
            System.out.println("--- CONJUNT: " + conjunt.nom + " ---");

            double[] beneficis = new double[NUM_REPETICIONS];
            long[] temps = new long[NUM_REPETICIONS];

            for (int i = 0; i < NUM_REPETICIONS; i++) {
                Gasolineras gs = new Gasolineras(100, 1234 + i);
                CentrosDistribucion cd = new CentrosDistribucion(10, 1, 1234 + i);

                long startTime = System.currentTimeMillis();
                double benefici = executarHillClimbingAmbOperadors(gs, cd, 2, conjunt);
                long endTime = System.currentTimeMillis();

                beneficis[i] = benefici;
                temps[i] = endTime - startTime;
            }

            imprimirEstadistiques(conjunt.nom, beneficis, temps);
            System.out.println();
        }

        System.out.println("\n💡 ANÀLISI: Compara els resultats per determinar quin conjunt");
        System.out.println("   d'operadors dona millor benefici amb menys temps d'execució.");
    }

    // Classe auxiliar per definir conjunts d'operadors
    static class ConjuntOperadors {
        String nom;
        boolean afegir, treure, moure, intercanviar, crear;

        ConjuntOperadors(String nom, boolean afegir, boolean treure, boolean moure,
                         boolean intercanviar, boolean crear) {
            this.nom = nom;
            this.afegir = afegir;
            this.treure = treure;
            this.moure = moure;
            this.intercanviar = intercanviar;
            this.crear = crear;
        }
    }

    // Mètode auxiliar per executar HC amb un conjunt específic d'operadors
    private static double executarHillClimbingAmbOperadors(Gasolineras gs, CentrosDistribucion cd,
                                                           int estrategia, ConjuntOperadors conjunt) {
        try {
            Problem problem = new Problem(
                    new PracticaBoard(gs, cd, estrategia),
                    new PracticaSuccessorFunction(conjunt.afegir, conjunt.treure, conjunt.moure,
                            conjunt.intercanviar, conjunt.crear),
                    new PracticaGoalTest(),
                    new PracticaHeuristicFunction()
            );

            Search search = new HillClimbingSearch();
            SearchAgent agent = new SearchAgent(problem, search);

            PracticaBoard estatFinal = (PracticaBoard) search.getGoalState();
            PracticaHeuristicFunction heuristica = new PracticaHeuristicFunction();
            return -heuristica.getHeuristicValue(estatFinal);

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return 0;
        }
    }

    // ============================================================================
    // EXPERIMENT 2: Comparació d'Estratègies d'Inicialització
    // ============================================================================
    private static void experiment2_ComparacioInicialitzacio() {
        System.out.println("EXPERIMENT 2: Comparació d'Estratègies d'Inicialització");
        System.out.println("Escenari: 10 centres, 1 camió/centre, 100 gasolineres");
        System.out.println();

        // Estratègia 1: Solució buida
        System.out.println("--- ESTRATÈGIA 1: Solució Buida ---");
        double[] beneficis1 = new double[NUM_REPETICIONS];
        long[] temps1 = new long[NUM_REPETICIONS];

        for (int i = 0; i < NUM_REPETICIONS; i++) {
            Gasolineras gs = new Gasolineras(100, 1234 + i);
            CentrosDistribucion cd = new CentrosDistribucion(10, 1, 1234 + i);

            long start = System.currentTimeMillis();
            beneficis1[i] = executarHillClimbing(gs, cd, 1);
            temps1[i] = System.currentTimeMillis() - start;
        }
        imprimirEstadistiques("Buida", beneficis1, temps1);

        // Estratègia 2: Solució greedy
        System.out.println("\n--- ESTRATÈGIA 2: Solució Greedy ---");
        double[] beneficis2 = new double[NUM_REPETICIONS];
        long[] temps2 = new long[NUM_REPETICIONS];

        for (int i = 0; i < NUM_REPETICIONS; i++) {
            Gasolineras gs = new Gasolineras(100, 1234 + i);
            CentrosDistribucion cd = new CentrosDistribucion(10, 1, 1234 + i);

            long start = System.currentTimeMillis();
            beneficis2[i] = executarHillClimbing(gs, cd, 2);
            temps2[i] = System.currentTimeMillis() - start;
        }
        imprimirEstadistiques("Greedy", beneficis2, temps2);

        // Comparació
        System.out.println("\n--- COMPARACIÓ ---");
        double milloraBenefici = ((calcularMitjana(beneficis2) - calcularMitjana(beneficis1)) /
                Math.abs(calcularMitjana(beneficis1))) * 100;
        System.out.println("Millora Greedy vs Buida: " + String.format("%.2f", milloraBenefici) + "%");
    }

    // ============================================================================
    // EXPERIMENT 3: Optimització Paràmetres Simulated Annealing
    // ============================================================================
    private static void experiment3_ParametresSA() {
        System.out.println("EXPERIMENT 3: Optimització Paràmetres Simulated Annealing");
        System.out.println("Escenari: 10 centres, 1 camió/centre, 100 gasolineres");
        System.out.println();

        // Provar diferents combinacions de paràmetres
        int[] iteracions = {1000, 5000, 10000};
        int[] ks = {5, 25, 125};
        double[] lambdas = {0.01, 0.001, 0.0001};

        System.out.println("Provant combinacions de paràmetres...");
        System.out.println("(Això pot trigar una estona)\n");

        double millorBenefici = Double.NEGATIVE_INFINITY;
        int millorIter = 0, millorSteps = 0, millorK = 0;
        double millorLambda = 0;

        // Només provem algunes combinacions representatives
        for (int iter : iteracions) {
            for (int k : ks) {
                for (double lambda : lambdas) {
                    int steps = iter / 10;  // 10% de les iteracions

                    double beneficiMig = 0;
                    for (int rep = 0; rep < 3; rep++) {  // Només 3 repeticions per anar ràpid
                        Gasolineras gs = new Gasolineras(100, 1234 + rep);
                        CentrosDistribucion cd = new CentrosDistribucion(10, 1, 1234 + rep);

                        double benefici = executarSimulatedAnnealing(gs, cd, 2, iter, steps, k, lambda);
                        beneficiMig += benefici;
                    }
                    beneficiMig /= 3;

                    System.out.println(String.format("Iter=%d, k=%d, λ=%.4f → Benefici=%.2f €",
                            iter, k, lambda, beneficiMig));

                    if (beneficiMig > millorBenefici) {
                        millorBenefici = beneficiMig;
                        millorIter = iter;
                        millorSteps = steps;
                        millorK = k;
                        millorLambda = lambda;
                    }
                }
            }
        }

        System.out.println("\n--- MILLORS PARÀMETRES ---");
        System.out.println("Iteracions: " + millorIter);
        System.out.println("Steps/Temp: " + millorSteps);
        System.out.println("k: " + millorK);
        System.out.println("λ: " + millorLambda);
        System.out.println("Benefici: " + String.format("%.2f", millorBenefici) + " €");
    }

    // ============================================================================
    // EXPERIMENT 4: Escalabilitat
    // ============================================================================
    private static void experiment4_Escalabilitat() {
        System.out.println("EXPERIMENT 4: Escalabilitat (proporció 10:100)");
        System.out.println();

        int[] tamanys = {10, 20, 30, 40, 50};  // Centres

        System.out.println("Centres\tGasolineres\tTemps Mig (ms)\tBenefici Mig (€)");
        System.out.println("-".repeat(60));

        for (int centres : tamanys) {
            int gasolineres = centres * 10;

            double[] beneficis = new double[NUM_REPETICIONS];
            long[] temps = new long[NUM_REPETICIONS];

            for (int i = 0; i < NUM_REPETICIONS; i++) {
                Gasolineras gs = new Gasolineras(gasolineres, 1234 + i);
                CentrosDistribucion cd = new CentrosDistribucion(centres, 1, 1234 + i);

                long start = System.currentTimeMillis();
                beneficis[i] = executarHillClimbing(gs, cd, 2);
                temps[i] = System.currentTimeMillis() - start;
            }

            System.out.println(String.format("%d\t%d\t\t%.0f\t\t%.2f",
                    centres, gasolineres,
                    calcularMitjana(temps),
                    calcularMitjana(beneficis)));
        }
    }

    // ============================================================================
    // EXPERIMENT 5: Reducció de Centres
    // ============================================================================
    private static void experiment5_ReduccioCentres() {
        System.out.println("EXPERIMENT 5: Reducció de Centres");
        System.out.println();

        // Escenari original: 10 centres, 1 camió/centre
        System.out.println("--- ESCENARI ORIGINAL: 10 centres, 1 camió/centre ---");
        double[] beneficis1 = new double[NUM_REPETICIONS];
        int[] peticionsServides1 = new int[NUM_REPETICIONS];

        for (int i = 0; i < NUM_REPETICIONS; i++) {
            Gasolineras gs = new Gasolineras(100, 1234 + i);
            CentrosDistribucion cd = new CentrosDistribucion(10, 1, 1234 + i);

            ResultatExperiment res = executarHillClimbingAmbStats(gs, cd, 2);
            beneficis1[i] = res.benefici;
            peticionsServides1[i] = res.peticionsServides;
        }

        System.out.println("Benefici mig: " + String.format("%.2f", calcularMitjana(beneficis1)) + " €");
        System.out.println("Peticions servides mig: " + String.format("%.1f", calcularMitjana(peticionsServides1)));

        // Escenari reduït: 5 centres, 2 camions/centre
        System.out.println("\n--- ESCENARI REDUÏT: 5 centres, 2 camions/centre ---");
        double[] beneficis2 = new double[NUM_REPETICIONS];
        int[] peticionsServides2 = new int[NUM_REPETICIONS];

        for (int i = 0; i < NUM_REPETICIONS; i++) {
            Gasolineras gs = new Gasolineras(100, 1234 + i);
            CentrosDistribucion cd = new CentrosDistribucion(5, 2, 1234 + i);

            ResultatExperiment res = executarHillClimbingAmbStats(gs, cd, 2);
            beneficis2[i] = res.benefici;
            peticionsServides2[i] = res.peticionsServides;
        }

        System.out.println("Benefici mig: " + String.format("%.2f", calcularMitjana(beneficis2)) + " €");
        System.out.println("Peticions servides mig: " + String.format("%.1f", calcularMitjana(peticionsServides2)));

        // Comparació
        System.out.println("\n--- COMPARACIÓ ---");
        double diferencia = calcularMitjana(beneficis2) - calcularMitjana(beneficis1);
        double difPeticions = calcularMitjana(peticionsServides2) - calcularMitjana(peticionsServides1);
        System.out.println("Diferència de benefici: " + String.format("%.2f", diferencia) + " €");
        System.out.println("Diferència de peticions: " + String.format("%.1f", difPeticions));
    }

    // ============================================================================
    // EXPERIMENT 6: Efecte del Cost per Kilòmetre
    // ============================================================================
    private static void experiment6_CostKilometre() {
        System.out.println("EXPERIMENT 6: Efecte del Cost per Kilòmetre");
        System.out.println();

        double[] costs = {2, 4, 8, 16, 32};

        System.out.println("Cost/km (€)\tBenefici Mig (€)\tPeticions Servides");
        System.out.println("-".repeat(60));

        for (double cost : costs) {
            // Modifiquem temporalment la constant
            PracticaBoard.setCostPerKm(cost);

            double beneficiMig = 0;
            int peticionsServidesTotal = 0;

            for (int i = 0; i < NUM_REPETICIONS; i++) {
                Gasolineras gs = new Gasolineras(100, 1234 + i);
                CentrosDistribucion cd = new CentrosDistribucion(10, 1, 1234 + i);

                ResultatExperiment res = executarHillClimbingAmbStats(gs, cd, 2);
                beneficiMig += res.benefici;
                peticionsServidesTotal += res.peticionsServides;
            }
            beneficiMig /= NUM_REPETICIONS;
            double peticionsServidesMedia = peticionsServidesTotal / (double) NUM_REPETICIONS;

            System.out.println(String.format("%.0f\t\t%.2f\t\t%.1f",
                    cost, beneficiMig, peticionsServidesMedia));
        }

        // Restaurem el valor per defecte
        PracticaBoard.setCostPerKm(2.0);
    }

    // ============================================================================
    // EXPERIMENT 7: Variació de l'Horari de Feina
    // ============================================================================
    private static void experiment7_VariacioHorari() {
        System.out.println("EXPERIMENT 7: Variació de l'Horari de Feina");
        System.out.println();

        int[] hores = {7, 8, 9};  // -1h, normal, +1h
        int[] kmsMax = {560, 640, 720};  // 7h, 8h, 9h a 80km/h

        System.out.println("Hores\tKm Màx\tBenefici Mig (€)");
        System.out.println("-".repeat(40));

        for (int i = 0; i < hores.length; i++) {
            // Modifiquem temporalment la constant
            PracticaBoard.setMaxKmDia(kmsMax[i]);

            double beneficiMig = 0;
            for (int rep = 0; rep < NUM_REPETICIONS; rep++) {
                Gasolineras gs = new Gasolineras(100, 1234 + rep);
                CentrosDistribucion cd = new CentrosDistribucion(10, 1, 1234 + rep);
                beneficiMig += executarHillClimbing(gs, cd, 2);
            }
            beneficiMig /= NUM_REPETICIONS;

            System.out.println(String.format("%d\t%d\t%.2f", hores[i], kmsMax[i], beneficiMig));
        }

        // Restaurem el valor per defecte
        PracticaBoard.setMaxKmDia(640);
    }

    // ============================================================================
    // EXPERIMENT 8: ESPECIAL (punt extra!)
    // ============================================================================
    private static void experiment8_Especial() {
        System.out.println("EXPERIMENT 8: ESPECIAL amb semilla 1234");
        System.out.println("Escenari: 10 centres, 1 camió/centre, 100 gasolineres");
        System.out.println();

        Gasolineras gs = new Gasolineras(100, 1234);
        CentrosDistribucion cd = new CentrosDistribucion(10, 1, 1234);

        System.out.println("Executant Hill Climbing...");
        long startTime = System.currentTimeMillis();

        try {
            Problem problem = new Problem(
                    new PracticaBoard(gs, cd, 2),  // Greedy
                    new PracticaSuccessorFunction(),
                    new PracticaGoalTest(),
                    new PracticaHeuristicFunction()
            );

            Search search = new HillClimbingSearch();
            SearchAgent agent = new SearchAgent(problem, search);

            long endTime = System.currentTimeMillis();
            long temps = endTime - startTime;

            // Calcular benefici
            PracticaBoard estatFinal = (PracticaBoard) search.getGoalState();
            PracticaHeuristicFunction heuristica = new PracticaHeuristicFunction();
            double benefici = -heuristica.getHeuristicValue(estatFinal);

            System.out.println("\n" + "=".repeat(60));
            System.out.println("✅ RESULTATS EXPERIMENT ESPECIAL");
            System.out.println("=".repeat(60));
            System.out.println("Benefici obtingut: " + String.format("%.2f", benefici) + " €");
            System.out.println("Temps d'execució: " + temps + " ms");
            System.out.println("=".repeat(60));



            // Instrumentació
            System.out.println("\nInstrumentació:");
            printInstrumentation(agent.getInstrumentation());

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ============================================================================
    // PROVA RÀPIDA (per debug)
    // ============================================================================
    private static void provaRapida() {
        System.out.println("PROVA RÀPIDA - Debug");
        System.out.println();

        Gasolineras gs = new Gasolineras(10, 1234);
        CentrosDistribucion cd = new CentrosDistribucion(3, 1, 1234);

        System.out.println("Gasolineres: " + gs.size());
        System.out.println("Centres: " + cd.size());

        // Comptar peticions
        int totalPeticions = 0;
        for (int i = 0; i < gs.size(); i++) {
            totalPeticions += gs.get(i).getPeticiones().size();
        }
        System.out.println("Peticions totals: " + totalPeticions);

        System.out.println("\nExecutant Hill Climbing...");
        double benefici = executarHillClimbing(gs, cd, 2);
        System.out.println("Benefici: " + String.format("%.2f", benefici) + " €");

        System.out.println("\nExecutant Simulated Annealing...");
        double beneficiSA = executarSimulatedAnnealing(gs, cd, 2, 1000, 100, 25, 0.001);
        System.out.println("Benefici SA: " + String.format("%.2f", beneficiSA) + " €");
    }

    // ============================================================================
    // FUNCIONS AUXILIARS
    // ============================================================================

    // Classe per retornar múltiples valors
    static class ResultatExperiment {
        double benefici;
        int peticionsServides;
        double kmRecorreguts;

        ResultatExperiment(double benefici, int peticionsServides, double kmRecorreguts) {
            this.benefici = benefici;
            this.peticionsServides = peticionsServides;
            this.kmRecorreguts = kmRecorreguts;
        }
    }

    private static ResultatExperiment executarHillClimbingAmbStats(Gasolineras gs, CentrosDistribucion cd, int estrategia) {
        try {
            Problem problem = new Problem(
                    new PracticaBoard(gs, cd, estrategia),
                    new PracticaSuccessorFunction(),
                    new PracticaGoalTest(),
                    new PracticaHeuristicFunction()
            );

            Search search = new HillClimbingSearch();
            SearchAgent agent = new SearchAgent(problem, search);

            // Obtenir estat final i calcular estadístiques
            PracticaBoard estatFinal = (PracticaBoard) search.getGoalState();
            PracticaHeuristicFunction heuristica = new PracticaHeuristicFunction();
            double benefici = -heuristica.getHeuristicValue(estatFinal);

            // Comptar peticions servides i km recorreguts
            int peticionsServides = 0;
            double kmRecorreguts = 0;

            for (int i = 0; i < estatFinal.getNumCamions(); i++) {
                for (PracticaBoard.Viatge v : estatFinal.getViatgesPerCamio()[i]) {
                    peticionsServides += v.peticionsServides.size();
                    kmRecorreguts += v.calcularDistancia();
                }
            }

            return new ResultatExperiment(benefici, peticionsServides, kmRecorreguts);

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return new ResultatExperiment(0, 0, 0);
        }
    }

    private static double executarHillClimbing(Gasolineras gs, CentrosDistribucion cd, int estrategia) {
        try {
            Problem problem = new Problem(
                    new PracticaBoard(gs, cd, estrategia),
                    new PracticaSuccessorFunction(),
                    new PracticaGoalTest(),
                    new PracticaHeuristicFunction()
            );

            Search search = new HillClimbingSearch();
            SearchAgent agent = new SearchAgent(problem, search);

            // Obtenir estat final i calcular benefici
            PracticaBoard estatFinal = (PracticaBoard) search.getGoalState();
            PracticaHeuristicFunction heuristica = new PracticaHeuristicFunction();
            return -heuristica.getHeuristicValue(estatFinal);  // Negem perquè està en negatiu

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return 0;
        }
    }

    private static double executarSimulatedAnnealing(Gasolineras gs, CentrosDistribucion cd,
                                                     int estrategia, int iter, int steps,
                                                     int k, double lambda) {
        try {
            Problem problem = new Problem(
                    new PracticaBoard(gs, cd, estrategia),
                    new PracticaSuccessorFunctionSA(),  // ✅ Utilitzem la versió SA!
                    new PracticaGoalTest(),
                    new PracticaHeuristicFunction()
            );

            Search search = new SimulatedAnnealingSearch(iter, steps, k, lambda);
            SearchAgent agent = new SearchAgent(problem, search);

            PracticaBoard estatFinal = (PracticaBoard) search.getGoalState();
            PracticaHeuristicFunction heuristica = new PracticaHeuristicFunction();
            return -heuristica.getHeuristicValue(estatFinal);

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return 0;
        }
    }

    private static void imprimirEstadistiques(String nom, double[] beneficis, long[] temps) {
        System.out.println("\n--- Estadístiques: " + nom + " ---");
        System.out.println("Benefici mig: " + String.format("%.2f", calcularMitjana(beneficis)) + " €");
        System.out.println("Desviació est: " + String.format("%.2f", calcularDesviacio(beneficis)) + " €");
        System.out.println("Mínim: " + String.format("%.2f", calcularMin(beneficis)) + " €");
        System.out.println("Màxim: " + String.format("%.2f", calcularMax(beneficis)) + " €");

        if (temps != null) {
            System.out.println("Temps mig: " + String.format("%.0f", calcularMitjana(temps)) + " ms");
        }
    }

    private static double calcularMitjana(double[] dades) {
        double suma = 0;
        for (double d : dades) suma += d;
        return suma / dades.length;
    }

    private static double calcularMitjana(int[] dades) {
        double suma = 0;
        for (int d : dades) suma += d;
        return suma / dades.length;
    }

    private static double calcularMitjana(long[] dades) {
        double suma = 0;
        for (long d : dades) suma += d;
        return suma / dades.length;
    }

    private static double calcularDesviacio(double[] dades) {
        double mitjana = calcularMitjana(dades);
        double sumQuadrats = 0;
        for (double d : dades) {
            sumQuadrats += Math.pow(d - mitjana, 2);
        }
        return Math.sqrt(sumQuadrats / dades.length);
    }

    private static double calcularMin(double[] dades) {
        double min = dades[0];
        for (double d : dades) if (d < min) min = d;
        return min;
    }

    private static double calcularMax(double[] dades) {
        double max = dades[0];
        for (double d : dades) if (d > max) max = d;
        return max;
    }

    private static void printInstrumentation(Properties properties) {
        Iterator keys = properties.keySet().iterator();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            String property = properties.getProperty(key);
            System.out.println(key + " : " + property);
        }
    }
}