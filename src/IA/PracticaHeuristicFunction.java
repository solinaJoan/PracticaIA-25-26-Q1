package IA;

import aima.search.framework.HeuristicFunction;

<<<<<<< HEAD
public class PracticaHeuristicFunction implements HeuristicFunction {

=======

public class PracticaHeuristicFunction implements HeuristicFunction 
{
>>>>>>> ea54be6591a350c59b721bc86bbb347cd0f78251
    /**
     * Calcula el valor heurístic de l'estat.
     *
     * Com que AIMA minimitza, retornem -benefici per tal que minimitzar sigui 
     * equivalent a maximitzar.
     *
     * BENEFICI = Ingressos per peticions servides 
     *            - Cost de kilòmetres recorreguts 
     *            - Pèrdua per peticions no servides (assumint que es serveixen 
     *              demà)
     */
    public double getHeuristicValue(Object o) 
    {
        PracticaBoard board = (PracticaBoard) o;

        double beneficiTotal = 0;

        // 1. INGRESSOS: Suma dels preus de les peticions servides
        double ingressos = 0;
        for (int i = 0; i < board.getNumCamions(); i++) 
        {
            for (Viatge viatge : board.getViatgesPerCamio()[i]) 
            {
                for (Peticio p : viatge.getPeticionsServides()) {
                    ingressos += p.calcularPreu();
                }
            }
        }

        // 2. COST KILÒMETRES: Suma de les distàncies recorregudes
        double costKm = 0;
<<<<<<< HEAD
        for (int i = 0; i < board.getNumCamions(); i++) {
            for (Viatge viatge : board.getViatgesPerCamio()[i]) {
                costKm += viatge.calcularDistancia() *
                        PracticaBoard.getCostPerKm();
=======
        for (int i = 0; i < board.getNumCamions(); i++) 
        {
            for (Viatge viatge : board.getViatgesPerCamio()[i]) 
            {
                costKm += viatge.calcularDistancia() * 
                          PracticaBoard.getCostPerKm();
>>>>>>> ea54be6591a350c59b721bc86bbb347cd0f78251
            }
        }

        // 3. PÈRDUA PER PETICIONS NO SERVIDES
        // Assumim que les peticions no servides avui es serviran demà,
        // per tant perdrem la diferència entre el preu d'avui i el de demà
        double perdua = 0;
        for (Peticio p : board.getPeticionsNoAssignades()) 
        {
            double preuAvui = p.calcularPreu();
<<<<<<< HEAD
            // Demà portarà un dia més pendent
            Peticio pDema = new Peticio(p.idGasolinera, p.idPeticioGasolinera,
                    p.diesPendent + 1);
=======
            Peticio pDema = new Peticio(p.getIdGasolinera(), 
                                        p.getIdPeticioGasolinera(), 
                                        p.getDiesPendents() + 1);
>>>>>>> ea54be6591a350c59b721bc86bbb347cd0f78251
            double preuDema = pDema.calcularPreu();
            perdua += (preuAvui - preuDema);
        }

        // BENEFICI TOTAL = Ingressos - Costos - Pèrdues
        beneficiTotal = ingressos - costKm - perdua;

        // Retornem l'invers perquè AIMA minimitza
        return -beneficiTotal;
    }


    /**
     * Versió alternativa de la heurística que només considera ingressos i 
     * costos (ignora les pèrdues futures).
     */
    public double getHeuristicValueSimple(Object o) 
    {
        PracticaBoard board = (PracticaBoard) o;

        double ingressos = 0;
        double costKm = 0;

        for (int i = 0; i < board.getNumCamions(); i++) 
        {
            for (Viatge viatge : board.getViatgesPerCamio()[i]) 
            {
                for (Peticio p : viatge.getPeticionsServides()) 
                {
                    ingressos += p.calcularPreu();
                }
<<<<<<< HEAD
                costKm += viatge.calcularDistancia() *
                        PracticaBoard.getCostPerKm();
=======
                costKm += viatge.calcularDistancia() * 
                          PracticaBoard.getCostPerKm();
>>>>>>> ea54be6591a350c59b721bc86bbb347cd0f78251
            }
        }

        return -(ingressos - costKm);
    }


    /**
     * Versió que només prioritza maximitzar peticions servides
     */
    public double getHeuristicValuePeticions(Object o) 
    {
        PracticaBoard board = (PracticaBoard) o;

        int peticionsServides = 0;
        for (int i = 0; i < board.getNumCamions(); i++) 
        {
            for (Viatge viatge : board.getViatgesPerCamio()[i]) 
            {
                peticionsServides += viatge.getPeticionsServides().size();
            }
        }

        return -peticionsServides;
    }
}