package IA;

import IA.Gasolina.*;
import java.util.*;

public class PracticaBoard {
    private static final int MAX_KM_DIA = 640;
    private static final int MAX_VIATGES_DIA = 5;
    private static final int VALOR_DEPOSIT = 1000;
    private static final double COST_PER_KM = 2.0;

    private static Gasolineras gasolineras;
    private static CentrosDistribucion centros;

    /**
     * Representa una petició d'una gasolinera
     */
    public static class Peticio {
        public final int idGasolinera; // Índex de la gasolinera
        public final int diesPendent; // Dies que porta pendent

        public Peticio(int idGasolinera, int diesPendent) {
            this.idGasolinera = idGasolinera;
            this.diesPendent = diesPendent;
        }

        public double calcularPreu() {
            double percentatge = 100 - 2 * diesPendent;
            if (percentatge < 0)
                percentatge = 0;
            return VALOR_DEPOSIT * percentatge / 100.0;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (!(o instanceof Peticio))
                return false;
            Peticio p = (Peticio) o;
            return idGasolinera == p.idGasolinera && diesPendent == p.diesPendent;
        }

        @Override
        public int hashCode() {
            return Objects.hash(idGasolinera, diesPendent);
        }
    }

    /**
     * Representa un viatge d'un camió
     */
    public static class Viatge {
        public final int idCamio;
        public final List<Peticio> peticionsServides; // 1 o 2 peticions

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
            if (peticionsServides.isEmpty())
                return 0;

            // Coordenades del centre del camió
            Distribucion centro = centros.get(idCamio);
            int centroX = centro.getCoordX();
            int centroY = centro.getCoordY();

            double distanciaTotal = 0;

            if (peticionsServides.size() == 1) {
                // Centre -> Gasolinera -> Centre
                Gasolinera g = gasolineras.get(peticionsServides.get(0).idGasolinera);
                distanciaTotal = 2 * distanciaManhattan(centroX, centroY,
                        g.getCoordX(), g.getCoordY());
            } else {
                // Centre -> G1 -> G2 -> Centre (o el contrari, escollim el
                // millor)
                Gasolinera g1 = gasolineras.get(peticionsServides.get(0).idGasolinera);
                Gasolinera g2 = gasolineras.get(peticionsServides.get(1).idGasolinera);

                // Opció 1: Centre -> G1 -> G2 -> Centre
                double dist1 = distanciaManhattan(centroX, centroY,
                        g1.getCoordX(), g1.getCoordY())
                        + distanciaManhattan(g1.getCoordX(), g1.getCoordY(),
                                g2.getCoordX(), g2.getCoordY())
                        + distanciaManhattan(g2.getCoordX(), g2.getCoordY(),
                                centroX, centroY);

                // Opció 2: Centre -> G2 -> G1 -> Centre
                double dist2 = distanciaManhattan(centroX, centroY, g2.getCoordX(), g2.getCoordY())
                        + distanciaManhattan(g2.getCoordX(), g2.getCoordY(), g1.getCoordX(), g1.getCoordY())
                        + distanciaManhattan(g1.getCoordX(), g1.getCoordY(), centroX, centroY);

                distanciaTotal = Math.min(dist1, dist2);
            }

            return distanciaTotal;
        }
    }

    // Representació de l'estat

    // Cada camió té una llista de viatges assignats
    private List<Viatge>[] viatgesPerCamio;

    // Peticions que encara no s'han assignat a cap camió
    private Set<Peticio> peticionsNoAssignades;

    // Constructors

    /**
     * Constructor per generar estat inicial
     */
    public PracticaBoard(Gasolineras gs, CentrosDistribucion cd,
            int estrategiaInicial) {
        // Guardem les dades estàtiques
        gasolineras = gs;
        centros = cd;

        int numCamions = cd.size();
        viatgesPerCamio = new ArrayList[numCamions];
        for (int i = 0; i < numCamions; i++) {
            viatgesPerCamio[i] = new ArrayList<>();
        }

        // Inicialitzem el conjunt de peticions no assignades
        peticionsNoAssignades = new HashSet<>();
        for (int i = 0; i < gs.size(); i++) {
            Gasolinera g = gs.get(i);
            for (int j = 0; j < g.getPeticiones().size(); j++) {
                peticionsNoAssignades.add(new Peticio(i, g.getPeticiones().get(j)));
            }
        }

        // Genera solució inicial segons estratègia
        switch (estrategiaInicial) {
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
    public PracticaBoard(PracticaBoard altre) {
        this.viatgesPerCamio = new ArrayList[altre.viatgesPerCamio.length];
        for (int i = 0; i < altre.viatgesPerCamio.length; i++) {
            this.viatgesPerCamio[i] = new ArrayList<>();
            for (Viatge v : altre.viatgesPerCamio[i]) {
                this.viatgesPerCamio[i].add(new Viatge(v));
            }
        }
        this.peticionsNoAssignades = new HashSet<>(altre.peticionsNoAssignades);
    }

    // Estratègies per generar la solució inicial.

    /**
     * ESTRATÈGIA 1: Solució buida (cap petició assignada)
     */
    private void generarSolucioInicial_Buida() {
        // No fem res, deixem tots els viatges buits
    }

    /**
     * ESTRATÈGIA 2: Solució greedy (assigna peticions amb millor ràtio
     * benefici/distància)
     */
    private void generarSolucioInicial_Greedy() {
        // Per cada camió, intentem assignar viatges de manera greedy
        for (int idCamio = 0; idCamio < viatgesPerCamio.length; idCamio++) {
            Distribucion centro = centros.get(idCamio);

            // Fem fins a MAX_VIATGES_DIA viatges
            for (int numViatge = 0; numViatge < MAX_VIATGES_DIA; numViatge++) {
                Viatge viatge = new Viatge(idCamio);

                // Busquem les 2 millors peticions per aquest viatge
                Peticio millor1 = trobarMillorPeticio(centro, null);
                if (millor1 != null) {
                    viatge.afegirPeticio(millor1);
                    peticionsNoAssignades.remove(millor1);

                    // Busquem una segona petició que estigui a prop de la primera
                    Peticio millor2 = trobarMillorPeticio(centro, millor1);
                    if (millor2 != null) {
                        viatge.afegirPeticio(millor2);
                        peticionsNoAssignades.remove(millor2);
                    }
                }

                if (!viatge.peticionsServides.isEmpty()) {
                    viatgesPerCamio[idCamio].add(viatge);
                } else {
                    break; // No hi ha més peticions a assignar
                }
            }
        }
    }

    private Peticio trobarMillorPeticio(Distribucion centro,
            Peticio peticioPrèvia) {
        Peticio millor = null;
        double millorRatio = Double.NEGATIVE_INFINITY;

        for (Peticio p : peticionsNoAssignades) {
            Gasolinera g = gasolineras.get(p.idGasolinera);
            double distancia;

            if (peticioPrèvia == null) {
                // Primera petició del viatge: distància des del centre
                distancia = distanciaManhattan(centro.getCoordX(), centro.getCoordY(),
                        g.getCoordX(), g.getCoordY());
            } else {
                // Segona petició: distància des de la primera gasolinera
                Gasolinera gPrevia = gasolineras.get(peticioPrèvia.idGasolinera);
                distancia = distanciaManhattan(gPrevia.getCoordX(), gPrevia.getCoordY(),
                        g.getCoordX(), g.getCoordY());
            }

            // Ràtio benefici/distància (prioritzem peticions amb molts dies pendents)
            double benefici = p.calcularPreu();
            double ratio = benefici / (distancia + 1); // +1 per evitar divisió per 0

            if (ratio > millorRatio) {
                millorRatio = ratio;
                millor = p;
            }
        }

        return millor;
    }

    // ============ UTILITATS ============

    public static double distanciaManhattan(int x1, int y1, int x2, int y2) {
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }

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
}