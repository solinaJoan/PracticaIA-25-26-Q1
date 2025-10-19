package IA;

import aima.search.framework.HeuristicFunction;
import IA.PracticaBoard.*;
import java.util.*;

public class PracticaHeuristicFunction implements HeuristicFunction {

    /**
     * Calcula el valor heurístic de l'estat.
     *
     * IMPORTANT: Com que AIMA minimitza, hem de retornar l'INVERS del benefici.
     * És a dir: retornem -benefici per tal que minimitzar sigui equivalent a maximitzar.
     *
     * BENEFICI = Ingressos per peticions servides - Cost de kilòmetres recorreguts -
     *            Pèrdua per peticions no servides (assumint que es serviran demà)
     */
    public double getHeuristicValue(Object o) {
        PracticaBoard board = (PracticaBoard) o;

        double beneficiTotal = 0;

        // 1. INGRESSOS: Suma dels preus de les peticions servides
        double ingressos = 0;
        for (int i = 0; i < board.getNumCamions(); i++) {
            for (Viatge viatge : board.getViatgesPerCamio()[i]) {
                for (Peticio p : viatge.peticionsServides) {
                    ingressos += p.calcularPreu();
                }
            }
        }

        // 2. COST KILÒMETRES: Suma de les distàncies recorregudes
        double costKm = 0;
        for (int i = 0; i < board.getNumCamions(); i++) {
            for (Viatge viatge : board.getViatgesPerCamio()[i]) {
                costKm += viatge.calcularDistancia() * 
                          PracticaBoard.getCostPerKm();
            }
        }

        // 3. PÈRDUA PER PETICIONS NO SERVIDES
        // Assumim que les peticions no servides avui es serviran demà,
        // per tant perdrem la diferència entre el preu d'avui i el de demà
        double perdua = 0;
        for (Peticio p : board.getPeticionsNoAssignades()) {
            double preuAvui = p.calcularPreu();
            // Demà portarà un dia més pendent
            Peticio pDema = new Peticio(p.idGasolinera, p.idPeticioGasolinera,
                                        p.diesPendent + 1);
            double preuDema = pDema.calcularPreu();
            perdua += (preuAvui - preuDema);
        }

        // BENEFICI TOTAL = Ingressos - Costos - Pèrdues
        beneficiTotal = ingressos - costKm - perdua;

        // Retornem l'invers perquè AIMA minimitza
        return -beneficiTotal;
    }

    /**
     * Versió alternativa de la heurística que només considera ingressos i costos
     * (ignora les pèrdues futures)
     */
    public double getHeuristicValueSimple(Object o) {
        PracticaBoard board = (PracticaBoard) o;

        double ingressos = 0;
        double costKm = 0;

        for (int i = 0; i < board.getNumCamions(); i++) {
            for (Viatge viatge : board.getViatgesPerCamio()[i]) {
                for (Peticio p : viatge.peticionsServides) {
                    ingressos += p.calcularPreu();
                }
                costKm += viatge.calcularDistancia() * 
                          PracticaBoard.getCostPerKm();
            }
        }

        return -(ingressos - costKm);
    }

    /**
     * Versió que només prioritza maximitzar peticions servides
     * (útil per experimentar)
     */
    public double getHeuristicValuePeticions(Object o) {
        PracticaBoard board = (PracticaBoard) o;

        int peticionsServides = 0;
        for (int i = 0; i < board.getNumCamions(); i++) {
            for (Viatge viatge : board.getViatgesPerCamio()[i]) {
                peticionsServides += viatge.peticionsServides.size();
            }
        }

        return -peticionsServides;  // Maximitzem peticions
    }
}