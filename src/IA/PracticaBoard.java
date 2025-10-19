package IA;

import IA.Gasolina.*;
import java.util.*;

public class PracticaBoard 
{
    private static final int MAX_VIATGES_DIA = 5;
    private static final int VALOR_DIPOSIT = 1000;
    private static int MAX_KM_DIA = 640;
    private static double COST_PER_KM = 2.0;

    private static Gasolineras gasolineras;
    private static CentrosDistribucion centros;


    // REPRESENTACIO DE L'ESTAT


    // Cada camió té una llista de viatges assignats
    private List<Viatge>[] viatgesPerCamio;  

    // Peticions que encara no s'han assignat a cap camió
    private Set<Peticio> peticionsNoAssignades;


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
            for (int j = 0; j < peticions.size(); j++) 
            {
                int diesPendent = peticions.get(j);
                this.peticionsNoAssignades.add(new Peticio(i, j, diesPendent));
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


    public static void setCostPerKm(double cost) {
        COST_PER_KM = cost;
    }

    
    public static void setMaxKmDia(int km) {
        MAX_KM_DIA = km;
    }


    public static double getCostPerKm() {
        return COST_PER_KM;
    }


    public static int getMaxKmDia() {
        return MAX_KM_DIA;
    }


    public static int getMaxViatgesDia()
    {
        return MAX_VIATGES_DIA;
    }


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

                    // Busquem una segona petició que estigui a prop de la primera
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

            // Ràtio benefici/distància (prioritzem peticions amb molts dies pendents)
            double benefici = p.calcularPreu();
            double ratio = benefici / (distancia + 1);  // +1 per evitar divisió per 0

            if (ratio > millorRatio) {
                millorRatio = ratio;
                millor = p;
            }
        }

        return millor;
    }
}