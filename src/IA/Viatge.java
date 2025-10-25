package IA;

import IA.Gasolina.*;
import java.util.*;

/**
 * Representa un viatge d'un camió
 */
public class Viatge 
{
    private static final int MAX_PETICIONS_VIATGE = 2;

    private final int idCamio;
    private final List<Peticio> peticionsServides; // 1 o 2 peticions


    public Viatge(int idCamio) 
    {
        this.idCamio = idCamio;
        this.peticionsServides = new ArrayList<>(MAX_PETICIONS_VIATGE);
    }


    public Viatge(Viatge altre) 
    {
        this.idCamio = altre.idCamio;
        this.peticionsServides = new ArrayList<>(altre.peticionsServides);
    }


    public void afegirPeticio(Peticio p) 
    {
        if (this.peticionsServides.size() < MAX_PETICIONS_VIATGE) 
        {
            this.peticionsServides.add(p);
        }
    }


    public boolean esPle() 
    {
        return this.peticionsServides.size() >= MAX_PETICIONS_VIATGE;
    }


    // Calcula la distància total del viatge
    public double calcularDistancia() 
    {
        // Coordenades del centre del camió
        Distribucion centro = PracticaBoard.getCentros().get(idCamio);
        int centroX = centro.getCoordX();
        int centroY = centro.getCoordY();

        double distanciaTotal = 0;

        if (this.peticionsServides.size() == 1) 
        {
            // Centre -> Gasolinera -> Centre
            int id_gasolinera = this.peticionsServides.get(0).getIdGasolinera();
            Gasolinera g = PracticaBoard.getGasolineras().get(id_gasolinera);
            distanciaTotal = 2 * Utils.distanciaManhattan(centroX, centroY, 
                                                          g.getCoordX(), 
                                                          g.getCoordY());
        } 
        else if (this.peticionsServides.size() == 2)
        {
            int id_g1 = this.peticionsServides.get(0).getIdGasolinera();
            Gasolinera g1 = PracticaBoard.getGasolineras().get(id_g1);

            int id_g2 = this.peticionsServides.get(1).getIdGasolinera();
            Gasolinera g2 = PracticaBoard.getGasolineras().get(id_g2);

            // Opció 1: Centre -> G1 -> G2 -> Centre
            double dist1 = Utils.distanciaManhattan(centroX, centroY, 
                                                    g1.getCoordX(), 
                                                    g1.getCoordY()) 
                         + Utils.distanciaManhattan(g1.getCoordX(), 
                                                    g1.getCoordY(), 
                                                    g2.getCoordX(), 
                                                    g2.getCoordY())
                         + Utils.distanciaManhattan(g2.getCoordX(), 
                                                    g2.getCoordY(), 
                                                    centroX, centroY);

            // Opció 2: Centre -> G2 -> G1 -> Centre
            double dist2 = Utils.distanciaManhattan(centroX, centroY, 
                                                    g2.getCoordX(), 
                                                    g2.getCoordY())
                         + Utils.distanciaManhattan(g2.getCoordX(), 
                                                    g2.getCoordY(), 
                                                    g1.getCoordX(), 
                                                    g1.getCoordY())
                         + Utils.distanciaManhattan(g1.getCoordX(), 
                                                    g1.getCoordY(), 
                                                    centroX, centroY);

            distanciaTotal = Math.min(dist1, dist2);
        }

        return distanciaTotal;
    }


    public int getIdCamio()
    {
        return idCamio;
    }


    public List<Peticio> getPeticionsServides()
    {
        return peticionsServides;
    }
}