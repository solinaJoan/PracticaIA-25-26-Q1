package IA;

import IA.Gasolina.CentrosDistribucion;
import IA.Gasolina.Gasolineras;

public class PracticaBoard {
    // Un estat és un array de centres. Cada centre té una coordenadaX i coordenadaY, i un
    // array de 10 viatges. Per cada viatge es guarda només les coordenades de la gasolinera

    public static class Centres {
        private Viatges[] centres = new Viatges[10];
        private int centreX;
        private int centreY;

        public static class Viatges {
            private int gasolineraX;
            private int gasolineraY;
        }
    }

    private Centres[] centresArray;

    public PracticaBoard(Gasolineras GS, CentrosDistribucion CD) {

    }


    public PracticaBoard(PracticaBoard board) {

    }
}
