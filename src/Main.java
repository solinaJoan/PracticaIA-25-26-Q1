import IA.Gasolina.CentrosDistribucion;
import IA.Gasolina.Gasolineras;
import aima.search.framework.Problem;
import aima.search.framework.Search;
import aima.search.framework.SearchAgent;
import aima.search.informed.HillClimbingSearch;
import aima.search.informed.SimulatedAnnealingSearch;

import java.util.Properties;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import IA.PracticaBoard;
import IA.PracticaGoalTest;
import IA.PracticaHeuristicFunction;
import IA.PracticaSuccessorFunction;
import IA.PracticaSuccessorFunctionSA;
import IA.Viatge;

public class Main 
{
    private static final int NUM_REPETICIONS = 10;

    static private class ConjuntOperadors {
        private String nom;
        private boolean afegir, treure, moure, intercanviar, crear;

        public ConjuntOperadors(String nom, boolean afegir, boolean treure, 
                                boolean moure, boolean intercanviar, 
                                boolean crear) 
        {
            this.nom = nom;
            this.afegir = afegir;
            this.treure = treure;
            this.moure = moure;
            this.intercanviar = intercanviar;
            this.crear = crear;
        }
    }

    static private class ResultatExperiment {
        private double benefici;
        private int peticionsServides;
        private double kmRecorreguts;
        private int nodes_expandits;

        public ResultatExperiment(double benefici, int peticionsServides, 
                                  double kmRecorreguts, int nodes_expandits) 
        {
            this.benefici = benefici;
            this.peticionsServides = peticionsServides;
            this.kmRecorreguts = kmRecorreguts;
            this.nodes_expandits = nodes_expandits;
        }
    }

    public static void main(String[] args) 
    {
        if (args.length > 0) 
        {
            // Executar experiment específic des de línia de comandes
            int experiment = Integer.parseInt(args[0]);
            executarExperiment(experiment);
        } 
        else 
        {
            // Menú interactiu
            menuInteractiu();
        }
    }


    private static void menuInteractiu() 
    {
        java.util.Scanner scanner = new java.util.Scanner(System.in);

        boolean end = false;
        while (!end) 
        {
            System.out.println("\n" + "=".repeat(60));
            System.out.println("     PRÀCTICA DE CERCA LOCAL - IA");
            System.out.println("=".repeat(60));
            System.out.println("\nSelecciona l'experiment a executar:");
            System.out.println("  1. Comparació d'operadors (Hill Climbing)");
            System.out.println("  2. Comparació d'estratègies" +
                               " d'inicialització");
            System.out.println("  3. Optimització paràmetres Simulated " + 
                               "Annealing");
            System.out.println("  4. Escalabilitat (temps vs tamany)");
            System.out.println("  5. Reducció de centres a la meitat");
            System.out.println("  6. Efecte del cost per kilòmetre");
            System.out.println("  7. Variació de l'horari de feina");
            System.out.println("  8. Experiment ESPECIAL ");
            System.out.println("  9. Prova ràpida (debug)");
            System.out.println("  0. Sortir");
            System.out.print("\nOpció: ");

            int opcio = scanner.nextInt();

            if (opcio == 0) end = true;
            else if (opcio >= 1 && opcio <= 9) executarExperiment(opcio);
            else System.out.println("\nOpció no vàlida!");
        }

        scanner.close();
    }


    private static void executarExperiment(int num) 
    {
        System.out.println("\n" + "=".repeat(60));
        switch (num) 
        {
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


    private static void experiment1_ComparacioOperadors() 
    {
        System.out.println("EXPERIMENT 1: Comparació d'Operadors amb Hill " +
                           "Climbing");
        System.out.println("Escenari: 10 centres, 1 camió/centre, 100 " +
                           "benzineres");
        System.out.println();

        // Definim diferents conjunts d'operadors a provar
        ConjuntOperadors[] conjunts = 
        {
            // Conjunt 1: Operadors bàsics (Afegir + Crear)
            new ConjuntOperadors("Bàsics (Afegir+Crear)", 
                                 true, false, false, false, true),

            // Conjunt 2: Operadors de modificació (Afegir + Treure + Moure)
            new ConjuntOperadors("Modificació (Afegir+Treure+Moure)", 
                                 true, true, true, false, false),

            // Conjunt 3: Tots els operadors
            new ConjuntOperadors("Tots", true, true, true, true, true),

            // Conjunt 4: Sense intercanvi (Afegir + Treure + Moure + Crear)
            new ConjuntOperadors("Sense Intercanvi", 
                                 true, true, true, false, true),

            // Conjunt 5: Només moviments (Moure + Intercanviar)
            new ConjuntOperadors("Només Moviments", 
                                 false, false, true, true, false)
        };

        System.out.println("Provant " + conjunts.length + 
                           " conjunts d'operadors diferents...\n");

        double[] mitjanesBenefici = new double[conjunts.length];
        double[] tempsMig = new double[conjunts.length];
        int[] nodes_expandits_mig = new int[conjunts.length];


        // Per cada conjunt d'operadors
        for (int idx = 0; idx < conjunts.length; idx++) {
            ConjuntOperadors conjunt = conjunts[idx];
            System.out.println("--- CONJUNT: " + conjunt.nom + " ---");

            double[] beneficis = new double[NUM_REPETICIONS];
            long[] temps = new long[NUM_REPETICIONS];
            int[] nodes_expandits = new int[NUM_REPETICIONS];

            BufferedWriter writer = inicialitzarFitxerExperiment(
                                        "experiment-1-comp-operadors-" + 
                                        conjunt.nom,
                                        1
                                    );

            for (int i = 0; i < NUM_REPETICIONS; i++) {
                Gasolineras gs = new Gasolineras(100, 1234 + i);
                CentrosDistribucion cd = new CentrosDistribucion(10, 1, 
                                                                 1234 + i);

                long startTime = System.currentTimeMillis();
                ResultatExperiment res = executarHillClimbing(gs, cd, conjunt, 
                                                              2);
                long endTime = System.currentTimeMillis();

                beneficis[i] = res.benefici;
                temps[i] = endTime - startTime;
                nodes_expandits[i] = res.nodes_expandits;

                guardarResultat(writer, i + 1, res, temps[i], 
                                PracticaBoard.getCostPerKm(), 
                                PracticaBoard.getMaxKmDia());
            }

            if (writer != null) {
                try { writer.close(); } 
                catch (IOException e) { e.printStackTrace(); }
            }

            imprimirEstadistiques(conjunt.nom, beneficis, temps, nodes_expandits);
            System.out.println();

            // Guardem mitjana i temps mig
            mitjanesBenefici[idx] = calcularMitjana(beneficis);
            tempsMig[idx] = calcularMitjana(temps);
            nodes_expandits_mig[idx] = (int) calcularMitjana(nodes_expandits);
        }
        System.out.println("\nCalculant eficiència (Operadors que expandeixen" +
                           " mes nodes i, en cas d'empat, \ns'usa la formula" +
                           " Benefici mig / Temps mig) per cada conjunt" + 
                           " d'operadors...\n\n\n");

        double millorEficiència = Double.NEGATIVE_INFINITY;
        int max_nodes_expandits = 0;
        String millorConjunt = "";

        for (int idx = 0; idx < conjunts.length; idx++) {
            double eficiència = mitjanesBenefici[idx] / tempsMig[idx];
            System.out.println("Operadors: " + conjunts[idx].nom + 
                               " → Eficiència: \n\t\t(nodes expandits) " + 
                               String.format("%d", nodes_expandits_mig[idx]) + 
                               " ; \n\t\t(Benefici mig / Temps mig) " + 
                               String.format("%.5f", eficiència) + "\n\n");

            if (nodes_expandits_mig[idx] > max_nodes_expandits) 
            {
                max_nodes_expandits = nodes_expandits_mig[idx];
                millorConjunt = conjunts[idx].nom;
                millorEficiència = eficiència;
            }
            else if (nodes_expandits_mig[idx] == max_nodes_expandits &&
                     eficiència > millorEficiència)
            {
                millorConjunt = conjunts[idx].nom;
                millorEficiència = eficiència;
            }
        }

        System.out.println("\nEl conjunt '" + millorConjunt + 
                           "' mostra un alt benefici en relació al nombre de" +
                           " nodes expandits (aprofita mes el Hill Climbing)" +
                           " \ni al temps d'execució, resultant en la millor" +
                           " eficiència entre els conjunts provats.");
    }

    
    private static void experiment2_ComparacioInicialitzacio() {
        System.out.println("EXPERIMENT 2: Comparació d'Estratègies " +
                           "d'Inicialització");
        System.out.println("Escenari: 10 centres, 1 camió/centre, 100 " +
                           "gasolineres");
        System.out.println();

        ConjuntOperadors conjunt = new ConjuntOperadors("Tots", true, true, 
                                                        true, true, true);

        // Estratègia 1: Solució buida
        System.out.println("--- ESTRATÈGIA 1: Solució Buida ---");
        double[] beneficis1 = new double[NUM_REPETICIONS];
        long[] temps1 = new long[NUM_REPETICIONS];
        int[] nodes_expandits1 = new int[NUM_REPETICIONS];

        BufferedWriter writer = inicialitzarFitxerExperiment(
                                        "experiment-2-comp-estrategies-sol-" +
                                        "inicial-est-1-sol-buida",
                                        2
                                    );

        for (int i = 0; i < NUM_REPETICIONS; i++) {
            Gasolineras gs = new Gasolineras(100, 1234 + i);
            CentrosDistribucion cd = new CentrosDistribucion(10, 1, 1234 + i);

            long start = System.currentTimeMillis();
            ResultatExperiment res = executarHillClimbing(gs, cd, conjunt, 1);
            beneficis1[i] = res.benefici;
            temps1[i] = System.currentTimeMillis() - start;
            nodes_expandits1[i] = res.nodes_expandits;

            guardarResultat(writer, i + 1, res, temps1[i], 
                            PracticaBoard.getCostPerKm(), 
                            PracticaBoard.getMaxKmDia());
        }
        imprimirEstadistiques("Buida", beneficis1, temps1, nodes_expandits1);

        // Estratègia 2: Solució greedy
        System.out.println("\n--- ESTRATÈGIA 2: Solució Greedy ---");
        double[] beneficis2 = new double[NUM_REPETICIONS];
        long[] temps2 = new long[NUM_REPETICIONS];
        int[] nodes_expandits2 = new int[NUM_REPETICIONS];

        writer = inicialitzarFitxerExperiment(
                        "experiment-2-comp-estrategies-sol-" +
                        "inicial-est-2-sol-greedy",
                        2
                    );

        for (int i = 0; i < NUM_REPETICIONS; i++) {
            Gasolineras gs = new Gasolineras(100, 1234 + i);
            CentrosDistribucion cd = new CentrosDistribucion(10, 1, 1234 + i);

            long start = System.currentTimeMillis();
            ResultatExperiment res = executarHillClimbing(gs, cd, conjunt, 2);
            beneficis2[i] = res.benefici;
            temps2[i] = System.currentTimeMillis() - start;
            nodes_expandits2[i] = res.nodes_expandits;

            guardarResultat(writer, i + 1, res, temps2[i], 
                            PracticaBoard.getCostPerKm(),
                            PracticaBoard.getMaxKmDia());
        }

        if (writer != null) {
            try { writer.close(); } 
            catch (IOException e) { e.printStackTrace(); }
        }

        imprimirEstadistiques("Greedy", beneficis2, temps2, nodes_expandits2);

        // Comparació
        System.out.println("\n--- COMPARACIÓ ---");
        double milloraBenefici = ((calcularMitjana(beneficis2) - calcularMitjana(beneficis1)) /
                Math.abs(calcularMitjana(beneficis1))) * 100;
        System.out.println("Millora Greedy vs Buida: " + String.format("%.2f", milloraBenefici) + "%");
    }

   
    private static void experiment3_ParametresSA() {
        System.out.println("EXPERIMENT 3: Optimització Paràmetres Simulated Annealing");
        System.out.println("Escenari: 10 centres, 1 camió/centre, 100 gasolineres");
        System.out.println();

        ConjuntOperadors conjunt = new ConjuntOperadors("Només Moviments", 
                                                        false, false, true, 
                                                        true, false);

        // Provar diferents combinacions de paràmetres
        int[] iteracions = { 1000, 5000, 10000 };
        int[] ks = { 5, 25, 125 };
        double[] lambdas = { 0.01, 0.001, 0.0001 };

        System.out.println("Provant combinacions de paràmetres...");
        System.out.println("(Això pot trigar una estona)\n");

        double millorBenefici = Double.NEGATIVE_INFINITY;
        int millorIter = 0, millorSteps = 0, millorK = 0;
        double millorLambda = 0;

        // Només provem algunes combinacions representatives
        
        for (int iter : iteracions) 
        {
            for (int k : ks) 
            {
                for (double lambda : lambdas) 
                {
                    int steps = iter / 10;

                    double beneficiMig = 0;

                    BufferedWriter writer = inicialitzarFitxerExperimentSA(
                                        "experiment-3-comp-param-sa-" +
                                        iter + "-" + k + "-" + lambda,
                                        3
                                    );

                    for (int rep = 0; rep < NUM_REPETICIONS; rep++) 
                    {
                        Gasolineras gs = new Gasolineras(100, 1234 + rep);
                        CentrosDistribucion cd = new CentrosDistribucion(10, 1, 
                                                        1234 + rep);

                        double benefici = executarSimulatedAnnealing(
                                              gs, cd, iter, steps, 
                                              k, lambda, conjunt
                                          );

                        try 
                        {
                            writer.write((rep + 1) + " || " + 
                                         String.format("%.2f", benefici) + 
                                         " || " +
                                         iter + " || " +
                                         k + " || " +
                                         lambda + "\n");
                            writer.flush();
                        } 
                        catch (IOException e) 
                        {
                            System.out.println("❌ Error escrivint resultat: " + e.getMessage());
                        }
                        
                        beneficiMig += benefici;
                    }

                    if (writer != null) {
                        try { writer.close(); } 
                        catch (IOException e) { e.printStackTrace(); }
                    }

                    beneficiMig /= NUM_REPETICIONS;

                    System.out.printf("Iter=%d, k=%d, λ=%.4f → " +
                                      "Benefici=%.2f €%n", iter, k, lambda, 
                                      beneficiMig);

                    if (beneficiMig > millorBenefici) 
                    {
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

   
    private static void experiment4_Escalabilitat() {
        System.out.println("EXPERIMENT 4: Escalabilitat (proporció 10:100)");
        System.out.println();

        ConjuntOperadors conjunt = new ConjuntOperadors("Només Moviments", 
                                                        false, false, true, 
                                                        true, false);

        int[] tamanys = { 10, 20, 30, 40, 50 }; // Centres

        System.out.println("Centres\tGasolineres\tBenefici HC\tBenefici SA");
        System.out.println("-".repeat(60));

        for (int centres : tamanys) {
            int gasolineres = centres * 10;

            double[] beneficis = new double[NUM_REPETICIONS];
            long[] temps = new long[NUM_REPETICIONS];

            BufferedWriter writer = inicialitzarFitxerExperiment(
                                        "experiment-4-escalabilitat-hc-" +
                                        centres + "-centres-" + centres*10 + 
                                        "-benzineres",
                                        4
                                    );

            for (int i = 0; i < NUM_REPETICIONS; i++) {
                Gasolineras gs = new Gasolineras(gasolineres, 1234 + i);
                CentrosDistribucion cd = new CentrosDistribucion(centres, 1, 1234 + i);

                long start = System.currentTimeMillis();
                ResultatExperiment res = executarHillClimbing(gs, cd, 
                                                              conjunt, 2);
                beneficis[i] = res.benefici;
                temps[i] = System.currentTimeMillis() - start;
                guardarResultat(writer, i + 1, res, temps[i], 
                                PracticaBoard.getCostPerKm(), 
                                PracticaBoard.getMaxKmDia());
            }

            int iter = 10000;
            int k = 25;
            double lambda = 0.001;

            writer = inicialitzarFitxerExperimentSA(
                                        "experiment-4-escalabilitat-sa-" +
                                        centres + "-centres-" + centres*10 + 
                                        "-benzineres",
                                        4
                                    );

            double beneficiMig = 0;

            for (int rep = 0; rep < NUM_REPETICIONS; rep++) 
            {
                Gasolineras gs = new Gasolineras(gasolineres, 1234 + rep);
                CentrosDistribucion cd = new CentrosDistribucion(centres, 1, 
                                                1234 + rep);

                double benefici = executarSimulatedAnnealing(
                                      gs, cd, iter, iter / 10, 
                                      k, lambda, conjunt
                                  );

                try 
                {
                    writer.write((rep + 1) + " || " + 
                                 String.format("%.2f", benefici) + 
                                 " || " +
                                 iter + " || " +
                                 k + " || " +
                                 lambda + "\n");
                    writer.flush();
                } 
                catch (IOException e) 
                {
                    System.out.println("❌ Error escrivint resultat: " + e.getMessage());
                }
                
                beneficiMig += benefici;
            }
            beneficiMig /= NUM_REPETICIONS;


            if (writer != null) {
                try { writer.close(); } 
                catch (IOException e) { e.printStackTrace(); }
            }

            System.out.printf("%d\t%d\t\t(Benefici HC) %.2f\t\t(Benefici SA) " +
                              "%.2f%n", centres, gasolineres, 
                              calcularMitjana(beneficis), beneficiMig
                             );
        }
    }

    
    private static void experiment5_ReduccioCentres() {
        System.out.println("EXPERIMENT 5: Reducció de Centres");
        System.out.println();

        ConjuntOperadors conjunt = new ConjuntOperadors("Només Moviments", 
                                                        false, false, true, 
                                                        true, false);

        // Escenari original: 10 centres, 1 camió/centre
        System.out.println("--- ESCENARI ORIGINAL: 10 centres, 1 camió/centre ---");
        double[] beneficis1 = new double[NUM_REPETICIONS];
        int[] peticionsServides1 = new int[NUM_REPETICIONS];

        BufferedWriter writer = inicialitzarFitxerExperiment(
                                    "experiment-5-reduccio-centres-original", 5
                                );

        for (int i = 0; i < NUM_REPETICIONS; i++) {
            Gasolineras gs = new Gasolineras(100, 1234 + i);
            CentrosDistribucion cd = new CentrosDistribucion(10, 1, 1234 + i);

            long start = System.currentTimeMillis();
            ResultatExperiment res = executarHillClimbing(gs, cd, conjunt, 2);
            long temps = System.currentTimeMillis() - start;
            beneficis1[i] = res.benefici;
            peticionsServides1[i] = res.peticionsServides;
            guardarResultat(writer, i + 1, res, temps, 
                            PracticaBoard.getCostPerKm(),
                            PracticaBoard.getMaxKmDia());
        }

        System.out.println("Benefici mig: " + String.format("%.2f", calcularMitjana(beneficis1)) + " €");
        System.out.println("Peticions servides mig: " + String.format("%.1f", calcularMitjana(peticionsServides1)));

        // Escenari reduït: 5 centres, 2 camions/centre
        System.out.println("\n--- ESCENARI REDUÏT: 5 centres, 2 camions/centre ---");
        double[] beneficis2 = new double[NUM_REPETICIONS];
        int[] peticionsServides2 = new int[NUM_REPETICIONS];

        writer = inicialitzarFitxerExperiment(
                    "experiment-5-reduccio-centres-reduit", 5
                 );

        for (int i = 0; i < NUM_REPETICIONS; i++) {
            Gasolineras gs = new Gasolineras(100, 1234 + i);
            CentrosDistribucion cd = new CentrosDistribucion(5, 2, 1234 + i);

            long start = System.currentTimeMillis();
            ResultatExperiment res = executarHillClimbing(gs, cd, conjunt, 2);
            long temps = System.currentTimeMillis() - start;
            guardarResultat(writer, i + 1, res, temps, 
                            PracticaBoard.getCostPerKm(),
                            PracticaBoard.getMaxKmDia());
            beneficis2[i] = res.benefici;
            peticionsServides2[i] = res.peticionsServides;
        }

        if (writer != null) {
            try { writer.close(); } 
            catch (IOException e) { e.printStackTrace(); }
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

   
    private static void experiment6_CostKilometre() {
        System.out.println("EXPERIMENT 6: Efecte del Cost per Kilòmetre");
        System.out.println();
        ConjuntOperadors conjunt = new ConjuntOperadors("Només Moviments", 
                                                        false, false, true, 
                                                        true, false);

        double[] costs = { 2, 4, 8, 16, 32 };

        System.out.println("Cost/km (€)\tBenefici Mig (€)\tPeticions Servides");
        System.out.println("-".repeat(60));

        for (double cost : costs) {
            // Modifiquem temporalment la constant
            PracticaBoard.setCostPerKm(cost);

            double beneficiMig = 0;
            int peticionsServidesTotal = 0;

            BufferedWriter writer = inicialitzarFitxerExperiment(
                                        "experiment-6-cost-km-" + cost, 6
                                    );

            for (int i = 0; i < NUM_REPETICIONS; i++) {
                Gasolineras gs = new Gasolineras(100, 1234 + i);
                CentrosDistribucion cd = new CentrosDistribucion(10, 1, 1234 + i);

                long start = System.currentTimeMillis();
                ResultatExperiment res = executarHillClimbing(gs, cd, conjunt, 
                                                              2);
                long temps = System.currentTimeMillis() - start;
                beneficiMig += res.benefici;
                peticionsServidesTotal += res.peticionsServides;
                guardarResultat(writer, i + 1, res, temps, 
                                PracticaBoard.getCostPerKm(),
                                PracticaBoard.getMaxKmDia());
            }

            if (writer != null) {
                try { writer.close(); } 
                catch (IOException e) { e.printStackTrace(); }
            }

            beneficiMig /= NUM_REPETICIONS;
            double peticionsServidesMedia = peticionsServidesTotal / (double) NUM_REPETICIONS;

            System.out.printf("%.0f\t\t%.2f\t\t%.1f%n",
                    cost, beneficiMig, peticionsServidesMedia);
        }

        // Restaurem el valor per defecte
        PracticaBoard.setCostPerKm(2.0);
    }

    
    private static void experiment7_VariacioHorari() {
        System.out.println("EXPERIMENT 7: Variació de l'Horari de Feina");
        System.out.println();

        ConjuntOperadors conjunt = new ConjuntOperadors("Només Moviments", 
                                                        false, false, true, 
                                                        true, false);

        int[] hores = { 7, 8, 9 }; // -1h, normal, +1h
        int[] kmsMax = { 560, 640, 720 }; // 7h, 8h, 9h a 80km/h

        System.out.println("Hores\tKm Màx\tBenefici Mig (€)");
        System.out.println("-".repeat(40));

        for (int i = 0; i < hores.length; i++) {
            // Modifiquem temporalment la constant
            PracticaBoard.setMaxKmDia(kmsMax[i]);

            BufferedWriter writer = inicialitzarFitxerExperiment(
                                        "experiment-7-max-km-dia-" + kmsMax[i], 
                                        7
                                    );

            double beneficiMig = 0;
            for (int rep = 0; rep < NUM_REPETICIONS; rep++) {
                Gasolineras gs = new Gasolineras(100, 1234 + rep);
                CentrosDistribucion cd = new CentrosDistribucion(10, 1, 1234 + rep);

                long start = System.currentTimeMillis();
                ResultatExperiment res = executarHillClimbing(gs, cd, 
                                                              conjunt, 2);
                long temps = System.currentTimeMillis() - start;
                beneficiMig += res.benefici;
                guardarResultat(writer, i + 1, res, temps, 
                                PracticaBoard.getCostPerKm(),
                                PracticaBoard.getMaxKmDia());
            }

            if (writer != null) {
                try { writer.close(); } 
                catch (IOException e) { e.printStackTrace(); }
            }

            beneficiMig /= NUM_REPETICIONS;

            System.out.printf("%d\t%d\t%.2f%n", hores[i], kmsMax[i], beneficiMig);
        }

        // Restaurem el valor per defecte
        PracticaBoard.setMaxKmDia(640);
    }

    
    private static void experiment8_Especial() {
        System.out.println("EXPERIMENT 8: ESPECIAL amb llavor 1234");
        System.out.println("Escenari: 10 centres, 1 camió/centre," +
                " 100 benzineres");
        System.out.println();

        Gasolineras gs = new Gasolineras(100, 1234);
        CentrosDistribucion cd = new CentrosDistribucion(10, 1, 1234);

        System.out.println("Executant Hill Climbing...");
        long startTime = System.currentTimeMillis();

        try {
            Problem problem = new Problem(
                    new PracticaBoard(gs, cd, 2), // Greedy
                    new PracticaSuccessorFunction(true, true, true, false, true),
                    new PracticaGoalTest(),
                    new PracticaHeuristicFunction());

            Search search = new HillClimbingSearch();
            SearchAgent agent = new SearchAgent(problem, search);

            long endTime = System.currentTimeMillis();
            long temps = endTime - startTime;

            // Calcular benefici
            PracticaBoard estatFinal = (PracticaBoard) search.getGoalState();
            PracticaHeuristicFunction heuristica = new PracticaHeuristicFunction();

            // L'heurística simple ens dona el benefici que volem
            // (ingressos - costs_km)
            double benefici = -heuristica.getHeuristicValueSimple(estatFinal);

            System.out.println("\n" + "=".repeat(60));
            System.out.println("✅ RESULTATS EXPERIMENT ESPECIAL");
            System.out.println("=".repeat(60));
            System.out.println("Benefici obtingut: " +
                    String.format("%.2f", benefici) + " €");
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


    private static void provaRapida() {
        System.out.println("PROVA RÀPIDA - Debug");
        System.out.println();
        

        Gasolineras gs = new Gasolineras(10, 1234);
        CentrosDistribucion cd = new CentrosDistribucion(3, 1, 1234);

        System.out.println("Gasolineres: " + gs.size());
        System.out.println("Centres: " + cd.size());

        // Comptar peticions
        int totalPeticions = 0;
        for (IA.Gasolina.Gasolinera g : gs) {
            totalPeticions += g.getPeticiones().size();
        }
        System.out.println("Peticions totals: " + totalPeticions);

        System.out.println("\nExecutant Hill Climbing...");
        ConjuntOperadors conjunt = new ConjuntOperadors("Només Moviments", 
                                                        false, false, true, 
                                                        true, false);
        double benefici = executarHillClimbing(gs, cd, conjunt, 2).benefici;
        System.out.println("Benefici: " + String.format("%.2f", benefici) + " €");

        System.out.println("\nExecutant Simulated Annealing...");
        double beneficiSA = executarSimulatedAnnealing(gs, cd, 1000, 100, 25, 
                                                       0.001, conjunt);
        System.out.println("Benefici SA: " + String.format("%.2f", beneficiSA) + " €");
    }

    
    // Funcions auxiliars

    private static ResultatExperiment executarHillClimbing(
        Gasolineras gs, CentrosDistribucion cd, 
        ConjuntOperadors conjunt, int estrategia
    ) {
        try {
            Problem problem = new Problem(
                    new PracticaBoard(gs, cd, estrategia),
                    new PracticaSuccessorFunction(conjunt.afegir, 
                                                  conjunt.treure, 
                                                  conjunt.moure, 
                                                  conjunt.intercanviar, 
                                                  conjunt.crear),
                    new PracticaGoalTest(),
                    new PracticaHeuristicFunction());

            Search search = new HillClimbingSearch();
            SearchAgent agent = new SearchAgent(problem, search);

            // Obtenir estat final i calcular benefici
            PracticaBoard estatFinal = (PracticaBoard) search.getGoalState();
            PracticaHeuristicFunction heur = new PracticaHeuristicFunction();
            double benefici = -heur.getHeuristicValueSimple(estatFinal);
            
            // Comptar peticions servides i km recorreguts
            int peticionsServides = 0;
            double kmRecorreguts = 0;

            for (int i = 0; i < estatFinal.getNumCamions(); i++) {
                for (Viatge v : estatFinal.getViatgesPerCamio()[i]) {
                    peticionsServides += v.getPeticionsServides().size();
                    kmRecorreguts += v.calcularDistancia();
                }
            }

            int nodes_expandits = Integer.parseInt(
                                    agent.getInstrumentation()
                                         .getProperty("nodesExpanded")
                                  );

            return new ResultatExperiment(benefici, peticionsServides, 
                                          kmRecorreguts, nodes_expandits);
        } 
        catch (Exception e) 
        {
            System.out.println("Error: " + e.getMessage());
            return new ResultatExperiment(0, 0, 0, 0);
        }
    }

    private static double executarSimulatedAnnealing(
        Gasolineras gs, CentrosDistribucion cd,
        int iter, int steps, int k, double lambda, ConjuntOperadors conjunt
    ) {
        try {
            Problem problem = new Problem(
                new PracticaBoard(gs, cd, 2),
                new PracticaSuccessorFunctionSA(conjunt.afegir, 
                                                conjunt.treure, 
                                                conjunt.moure, 
                                                conjunt.intercanviar, 
                                                conjunt.crear),
                new PracticaGoalTest(),
                new PracticaHeuristicFunction()
            );

            Search search = new SimulatedAnnealingSearch(iter, steps, k, 
                                                         lambda);
            SearchAgent agent = new SearchAgent(problem, search);

            PracticaBoard estatFinal = (PracticaBoard) search.getGoalState();
            PracticaHeuristicFunction heur = new PracticaHeuristicFunction();
            return -heur.getHeuristicValueSimple(estatFinal);

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return 0;
        }
    }

    private static void imprimirEstadistiques(String nom, double[] beneficis, 
                                              long[] temps, 
                                              int[] nodes_expandits) 
    {
        System.out.println("\n--- Estadístiques: " + nom + " ---");
        System.out.println("Benefici mig: " + String.format("%.2f", 
                           calcularMitjana(beneficis)) + " €");
        System.out.println("Desviació est: " + String.format("%.2f", 
                           calcularDesviacio(beneficis)) + " €");
        System.out.println("Mínim: " + 
                           String.format("%.2f", calcularMin(beneficis)) + 
                           " €");
        System.out.println("Màxim: " + 
                           String.format("%.2f", calcularMax(beneficis)) + 
                           " €");
        System.out.println("Nodes expandits promig: " + String.format("%.0f", 
                           calcularMitjana(nodes_expandits)));

        if (temps != null) {
            System.out.println("Temps mig: " + 
                               String.format("%.0f", calcularMitjana(temps)) + 
                               " ms");
        }
    }

    private static double calcularMitjana(double[] dades) 
    {
        double suma = 0;
        for (double d : dades)
            suma += d;
        return suma / dades.length;
    }

    private static double calcularMitjana(int[] dades) 
    {
        double suma = 0;
        for (int d : dades)
            suma += d;
        return suma / dades.length;
    }

    private static double calcularMitjana(long[] dades) 
    {
        double suma = 0;
        for (long d : dades)
            suma += d;
        return suma / dades.length;
    }

    private static double calcularDesviacio(double[] dades) 
    {
        double mitjana = calcularMitjana(dades);
        double sumQuadrats = 0;
        for (double d : dades) 
        {
            sumQuadrats += Math.pow(d - mitjana, 2);
        }
        return Math.sqrt(sumQuadrats / dades.length);
    }

    private static double calcularMin(double[] dades) 
    {
        double min = dades[0];
        for (double d : dades)
            if (d < min)
                min = d;
        return min;
    }

    private static double calcularMax(double[] dades) 
    {
        double max = dades[0];
        for (double d : dades)
            if (d > max)
                max = d;
        return max;
    }

    private static void printInstrumentation(Properties properties) 
    {
        for (Object o : properties.keySet()) 
        {
            String key = (String) o;
            String property = properties.getProperty(key);
            System.out.println(key + " : " + property);
        }
    }

    private static BufferedWriter inicialitzarFitxerExperiment(String nomExperiment, int num_exp) {
        String nomFitxer = nomExperiment.replaceAll("\\s+", "-")
                                        .toLowerCase() + ".txt";

        String directori = "./resultats/experiment-" + 
                           Integer.toString(num_exp);
        java.nio.file.Path pathDir = java.nio.file.Paths.get(directori);

        try {

            if (!java.nio.file.Files.exists(pathDir)) {
                java.nio.file.Files.createDirectories(pathDir);
            }

            // Ruta completa del fitxer
            java.nio.file.Path pathFitxer = pathDir.resolve(nomFitxer);

            // Si ja existeix, l’esborrem
            java.nio.file.Files.deleteIfExists(pathFitxer);

            // Obrim el fitxer per escriure (en mode append = true)
            BufferedWriter writer = new BufferedWriter(new FileWriter(pathFitxer.toFile(), true));

            // Escriure capçalera
            writer.write("Execucio || Benefici (€) || Temps (ms)|| " + 
                         "PeticionsServides || KmRecorreguts || " +
                         "Cost per Km || Max Km dia || NodesExpandits\n");
            writer.flush();

            return writer;
        } catch (IOException e) {
            System.out.println("❌ Error creant el fitxer: " + e.getMessage());
            return null;
        }
    }

    private static BufferedWriter inicialitzarFitxerExperimentSA(String nomExperiment, int num_exp) {
        String nomFitxer = nomExperiment.replaceAll("\\s+", "-")
                                        .toLowerCase() + ".txt";

        String directori = "./resultats/experiment-" + 
                           Integer.toString(num_exp);
        java.nio.file.Path pathDir = java.nio.file.Paths.get(directori);

        try {

            if (!java.nio.file.Files.exists(pathDir)) {
                java.nio.file.Files.createDirectories(pathDir);
            }

            // Ruta completa del fitxer
            java.nio.file.Path pathFitxer = pathDir.resolve(nomFitxer);

            // Si ja existeix, l’esborrem
            java.nio.file.Files.deleteIfExists(pathFitxer);

            // Obrim el fitxer per escriure (en mode append = true)
            BufferedWriter writer = new BufferedWriter(new FileWriter(pathFitxer.toFile(), true));

            // Escriure capçalera
            writer.write("Execucio || Benefici (€) || Iteracions || " + 
                         " k || Lambda\n");
            writer.flush();

            return writer;
        } catch (IOException e) {
            System.out.println("❌ Error creant el fitxer: " + e.getMessage());
            return null;
        }
    }

    private static void guardarResultat(BufferedWriter writer, int execucio,    
                                        ResultatExperiment res, long temps,
                                        double cost_km, int max_km_dia) 
    {
        try 
        {
            writer.write(execucio + " || " + 
                         String.format("%.2f", res.benefici) + " || " +
                         temps + " || " +
                         res.peticionsServides + " || " +
                         String.format("%.2f", res.kmRecorreguts) + " || " +
                         String.format("%.0f", cost_km) + " || " +
                         String.format("%d", max_km_dia) + " || " +
                         res.nodes_expandits + "\n");
            writer.flush();
        } 
        catch (IOException e) 
        {
            System.out.println("❌ Error escrivint resultat: " + e.getMessage());
        }
    }
}