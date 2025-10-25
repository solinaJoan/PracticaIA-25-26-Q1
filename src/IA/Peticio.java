package IA;

import java.util.*;

/**
 * Representa una petició d'una gasolinera
 */
public class Peticio 
{
    /**
     * - idGasolinera == Índex de la gasolinera
     * - idPeticioGasolinera == Índex de la peticio en la benzinera.
     *   Necessari quan una mateixa benzinera té més d'una petició amb els
     *   mateixos dies pendents.
     * - diesPendent == dies que porta pendent
     */

    private final int idGasolinera;
    private final int idPeticioGasolinera;
    private final int diesPendent;


    public Peticio(int idGasolinera, int idPeticioGasolinera,
                   int diesPendent) 
    {
        this.idGasolinera = idGasolinera;
        this.idPeticioGasolinera = idPeticioGasolinera;
        this.diesPendent = diesPendent;
    }


    public double calcularPreu() 
    {
        double percentatge = 102.0;
        if (diesPendent > 0) percentatge = 100 - Math.pow(2, diesPendent);
        if (percentatge < 0) percentatge = 0;
        return PracticaBoard.getValorDeposit() * percentatge / 100.0;
    }


    public int getIdGasolinera()
    {
        return idGasolinera;
    }

    
    public int getIdPeticioGasolinera()
    {
        return idPeticioGasolinera;
    }


    public int getDiesPendents()
    {
        return diesPendent;
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