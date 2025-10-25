package IA;

import IA.Gasolina.*;
import java.util.*;

<<<<<<< HEAD
public class PracticaBoard {

    // ============ CONSTANTS DEL PROBLEMA (ara modificables) ============
    private static int MAX_KM_DIA = 640;  // 8h * 80km/h
    private static final int MAX_VIATGES_DIA = 5;
    private static final double CAPACITAT_CAMI = 2.0;  // Pot omplir 2 depòsits
    private static final int VALOR_DEPOSIT = 1000;
=======
public class PracticaBoard 
{
    private static final int MAX_VIATGES_DIA = 5;
    private static final int VALOR_DIPOSIT = 1000;
    private static int MAX_KM_DIA = 640;
>>>>>>> ea54be6591a350c59b721bc86bbb347cd0f78251
    private static double COST_PER_KM = 2.0;

    private static Gasolineras gasolineras;
    private static CentrosDistribucion centros;


    // REPRESENTACIO DE L'ESTAT


    // Cada camió té una llista de viatges assignats
    private List<Viatge>[] viatgesPerCamio;  

    // Peticions que encara no s'han assignat a cap camió
    private Set<Peticio> peticionsNoAssignades;


<<<<<<< HEAD
    // ============ CLASSES INTERNES ============

    /**
     * Representa una petició d'una gasolinera
     */
    public static class Peticio {

        /**
         - idGasolinera ==  Índex de la gasolinera
         - idPeticioGasolinera == Índex de la peticio en la benzinera.
         Necessari quan una mateixa benzinera té més d'una petició amb els
         mateixos dies pendents.
         - diesPendent == dies que porta pendent
         */

        public final int idGasolinera;
        public final int idPeticioGasolinera;
        public final int diesPendent;

        public Peticio(int idGasolinera, int idPeticioGasolinera,
                       int diesPendent) {
            this.idGasolinera = idGasolinera;
            this.idPeticioGasolinera = idPeticioGasolinera;
            this.diesPendent = diesPendent;
        }

        // Calcula el preu que cobrarem per aquesta petició
        public double calcularPreu() {
            double percentatge = 102.0;
            if (diesPendent > 0) percentatge = 100 - Math.pow(2, diesPendent);
            if (percentatge < 0) percentatge = 0;
            return VALOR_DEPOSIT * percentatge / 100.0;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Peticio)) return false;
            Peticio p = (Peticio) o;
            return idGasolinera == p.idGasolinera &&
                    idPeticioGasolinera == p.idPeticioGasolinera &&
                    diesPendent == p.diesPendent;
        }

        @Override
        public int hashCode() {
            return Objects.hash(idGasolinera, idPeticioGasolinera, diesPendent);
        }
    }

    /**
     * Representa un viatge d'un camió
     */
    public static class Viatge {
        public final int idCamio;
        public final List<Peticio> peticionsServides;  // 1 o 2 peticions

        public Viatge(int idCamio) {
            this.idCamio = idCamio;
            this.peticionsServides = new ArrayList<>(2);
        }

        public Viatge(Viatge altre) {
            this.idCamio = altre.idCamio;
            this.peticionsServides = new ArrayList<>(altre.peticionsServides);
        }

        public void afegirPeticio(Peticio p) {
            if (peticionsServides.size() < 2) {
                peticionsServides.add(p);
            }
        }

        public boolean esPle() {
            return peticionsServides.size() >= 2;
        }

        // Calcula la distància total del viatge
        public double calcularDistancia() {
            if (peticionsServides.isEmpty()) return 0;

            // Coordenades del centre del camió
            Distribucion centro = centros.get(idCamio);
            int centroX = centro.getCoordX();
            int centroY = centro.getCoordY();

            double distanciaTotal = 0;

            if (peticionsServides.size() == 1) {
                // Centre -> Gasolinera -> Centre
                Gasolinera g = gasolineras.get(peticionsServides.get(0).idGasolinera);
                distanciaTotal = 2 * distanciaManhattan(centroX, centroY, g.getCoordX(), g.getCoordY());
            } else {
                // Centre -> G1 -> G2 -> Centre (o el contrari, escollim el millor)
                Gasolinera g1 = gasolineras.get(peticionsServides.get(0).idGasolinera);
                Gasolinera g2 = gasolineras.get(peticionsServides.get(1).idGasolinera);

                // Opció 1: Centre -> G1 -> G2 -> Centre
                double dist1 = distanciaManhattan(centroX, centroY, g1.getCoordX(), g1.getCoordY())
                        + distanciaManhattan(g1.getCoordX(), g1.getCoordY(), g2.getCoordX(), g2.getCoordY())
                        + distanciaManhattan(g2.getCoordX(), g2.getCoordY(), centroX, centroY);

                // Opció 2: Centre -> G2 -> G1 -> Centre
                double dist2 = distanciaManhattan(centroX, centroY, g2.getCoordX(), g2.getCoordY())
                        + distanciaManhattan(g2.getCoordX(), g2.getCoordY(), g1.getCoordX(), g1.getCoordY())
                        + distanciaManhattan(g1.getCoordX(), g1.getCoordY(), centroX, centroY);

                distanciaTotal = Math.min(dist1, dist2);
            }

            return distanciaTotal;
        }
    }


    // ============ CONSTRUCTORS ============

=======
>>>>>>> ea54be6591a350c59b721bc86bbb347cd0f78251
    /**
     * Constructor per generar estat inicial
     */
    @SuppressWarnings("unchecked")
    public PracticaBoard(Gasolineras gs, CentrosDistribucion cd, 
                         int estrategiaInicial) 
    {
        // Guardem les dades estàtiques
        gasolineras = gs;
        centros = cd;

        int numCamions = cd.size();
        this.viatgesPerCamio = new ArrayList[numCamions];
        for (int i = 0; i < numCamions; i++) 
        {
            this.viatgesPerCamio[i] = new ArrayList<>();
        }

        // Inicialitzem el conjunt de peticions no assignades
        this.peticionsNoAssignades = new HashSet<>();
        for (int i = 0; i < gs.size(); i++) 
        {
            Gasolinera g = gs.get(i);
            ArrayList<Integer> peticions = g.getPeticiones();
<<<<<<< HEAD
            for (int j = 0; j < peticions.size(); j++) {
                int diesPendent = peticions.get(j);
                peticionsNoAssignades.add(new Peticio(i, j, diesPendent));
=======
            for (int j = 0; j < peticions.size(); j++) 
            {
                int diesPendent = peticions.get(j);
                this.peticionsNoAssignades.add(new Peticio(i, j, diesPendent));
>>>>>>> ea54be6591a350c59b721bc86bbb347cd0f78251
            }
        }

        // Genera solució inicial segons estratègia
        switch (estrategiaInicial) 
        {
            case 1:
                generarSolucioInicial_Buida();
                break;
            case 2:
                generarSolucioInicial_Greedy();
                break;
            default:
                generarSolucioInicial_Buida();
        }
    }

    /**
     * Constructor còpia (per generar successors)
     */
    @SuppressWarnings("unchecked")
    public PracticaBoard(PracticaBoard altre) {
        this.viatgesPerCamio = new ArrayList[altre.viatgesPerCamio.length];
        for (int i = 0; i < altre.viatgesPerCamio.length; i++) 
        {
            this.viatgesPerCamio[i] = new ArrayList<>();
            for (Viatge v : altre.viatgesPerCamio[i]) 
            {
                this.viatgesPerCamio[i].add(new Viatge(v));
            }
        }
        this.peticionsNoAssignades = new HashSet<>(altre.peticionsNoAssignades);
    }


    /**
     * Comprova si un camió compleix les restriccions
     */
    public boolean compleixRestriccions(int idCamio) {
        if (viatgesPerCamio[idCamio].size() > MAX_VIATGES_DIA) {
            return false;
        }

        double kmTotal = 0;
        for (Viatge v : viatgesPerCamio[idCamio]) {
            kmTotal += v.calcularDistancia();
        }

        return kmTotal <= MAX_KM_DIA;
    }

    /**
     * Comprova si tot l'estat compleix les restriccions
     */
    public boolean compleixRestriccions() {
        for (int i = 0; i < viatgesPerCamio.length; i++) {
            if (!compleixRestriccions(i)) {
                return false;
            }
        }
        return true;
    }

<<<<<<< HEAD
    // ============ SETTERS PER EXPERIMENTS 6 i 7 ============

    /**
     * Permet modificar el cost per kilòmetre (Experiment 6)
     */
=======

    public List<Viatge>[] getViatgesPerCamio() {
        return viatgesPerCamio;
    }

    public Set<Peticio> getPeticionsNoAssignades() {
        return peticionsNoAssignades;
    }

    public int getNumCamions() {
        return viatgesPerCamio.length;
    }

    public static Gasolineras getGasolineras() {
        return gasolineras;
    }

    public static CentrosDistribucion getCentros() {
        return centros;
    }


>>>>>>> ea54be6591a350c59b721bc86bbb347cd0f78251
    public static void setCostPerKm(double cost) {
        COST_PER_KM = cost;
    }

<<<<<<< HEAD
    /**
     * Permet modificar els kilòmetres màxims per dia (Experiment 7)
     */
=======
    
>>>>>>> ea54be6591a350c59b721bc86bbb347cd0f78251
    public static void setMaxKmDia(int km) {
        MAX_KM_DIA = km;
    }

<<<<<<< HEAD
    /**
     * Obté el cost per kilòmetre actual
     */
=======

>>>>>>> ea54be6591a350c59b721bc86bbb347cd0f78251
    public static double getCostPerKm() {
        return COST_PER_KM;
    }

<<<<<<< HEAD
    /**
     * Obté els kilòmetres màxims per dia actuals
     */
=======

>>>>>>> ea54be6591a350c59b721bc86bbb347cd0f78251
    public static int getMaxKmDia() {
        return MAX_KM_DIA;
    }

<<<<<<< HEAD
=======

>>>>>>> ea54be6591a350c59b721bc86bbb347cd0f78251
    public static int getMaxViatgesDia()
    {
        return MAX_VIATGES_DIA;
    }
<<<<<<< HEAD
=======


    public static int getValorDeposit()
    {
        return VALOR_DIPOSIT;
    }


    // OPERACIONS PRIVADES


    // Estratègies per crear la solució inicial.

    /**
     * Estratègia 1: Solució buida (cap petició assignada)
     */
    private void generarSolucioInicial_Buida() 
    {
        // No fem res, deixem tots els viatges buits
    }


    /**
     * Estratègia 2: Solució greedy (assigna peticions amb millor ràtio 
     *               benefici/distància)
     */
    private void generarSolucioInicial_Greedy() {
        // Per cada camió, intentem assignar viatges de manera greedy
        for (int idCamio = 0; idCamio < viatgesPerCamio.length; idCamio++) 
        {
            Distribucion centro = centros.get(idCamio);

            for (int numViatge = 0; numViatge < MAX_VIATGES_DIA; numViatge++) 
            {
                Viatge viatge = new Viatge(idCamio);

                // Busquem les 2 millors peticions per aquest viatge
                Peticio millor1 = trobarMillorPeticio(centro, null);
                if (millor1 != null) {
                    viatge.afegirPeticio(millor1);
                    peticionsNoAssignades.remove(millor1);

                    // Busquem una segona petició que estigui a prop de la 
                    // primera
                    Peticio millor2 = trobarMillorPeticio(centro, millor1);
                    if (millor2 != null) {
                        viatge.afegirPeticio(millor2);
                        peticionsNoAssignades.remove(millor2);
                    }
                }

                if (!viatge.getPeticionsServides().isEmpty()) {
                    viatgesPerCamio[idCamio].add(viatge);
                } else {
                    break;  // No hi ha més peticions a assignar
                }
            }
        }
    }


    private Peticio trobarMillorPeticio(Distribucion centro, 
                                        Peticio peticioPrèvia) 
    {
        Peticio millor = null;
        double millorRatio = Double.NEGATIVE_INFINITY;

        for (Peticio p : peticionsNoAssignades) 
        {
            Gasolinera g = gasolineras.get(p.getIdGasolinera());
            double distancia;

            if (peticioPrèvia == null) 
            {
                // Primera petició del viatge: distància des del centre
                distancia = Utils.distanciaManhattan(centro.getCoordX(), 
                                                     centro.getCoordY(),
                                                     g.getCoordX(), 
                                                     g.getCoordY());
            } 
            else 
            {
                // Segona petició: distància des de la primera gasolinera
                int id_gasolinera = peticioPrèvia.getIdGasolinera();
                Gasolinera gPrevia = gasolineras.get(id_gasolinera);
                distancia = Utils.distanciaManhattan(gPrevia.getCoordX(), 
                                                     gPrevia.getCoordY(),
                                                     g.getCoordX(), 
                                                     g.getCoordY());
            }

            // Ràtio benefici/distància (prioritzem peticions amb molts dies 
            // pendents)
            double benefici = p.calcularPreu();
            double ratio = benefici / (distancia + 1);  // +1 per evitar 
                                                        // divisió per 0

            if (ratio > millorRatio) {
                millorRatio = ratio;
                millor = p;
            }
        }

        return millor;
    }
>>>>>>> ea54be6591a350c59b721bc86bbb347cd0f78251
}