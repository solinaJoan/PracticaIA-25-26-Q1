package IA;

import aima.search.framework.SuccessorFunction;
import aima.search.framework.Successor;
import IA.PracticaBoard.*;
import java.util.*;

public class PracticaSuccessorFunction implements SuccessorFunction {

    // Configuració dels operadors a utilitzar
    private boolean usarAfegir = true;
    private boolean usarTreure = true;
    private boolean usarMoure = true;
    private boolean usarIntercanviar = true;
    private boolean usarCrear = true;

    /**
     * Constructor per defecte: utilitza tots els operadors
     */
    public PracticaSuccessorFunction() {
        // Tots activats per defecte
    }

    /**
     * Constructor configurable: permet escollir quins operadors utilitzar
     */
    public PracticaSuccessorFunction(boolean afegir, boolean treure, 
                                     boolean moure, boolean intercanviar, 
                                     boolean crear) {
        this.usarAfegir = afegir;
        this.usarTreure = treure;
        this.usarMoure = moure;
        this.usarIntercanviar = intercanviar;
        this.usarCrear = crear;
    }

    /**
     * Genera tots els successors possibles aplicant els operadors configurats
     */
    public List getSuccessors(Object state) {
        ArrayList retval = new ArrayList();
        PracticaBoard board = (PracticaBoard) state;

        if (usarAfegir) {
            retval.addAll(generarSuccessorsAfegirPeticio(board));
        }

        if (usarTreure) {
            retval.addAll(generarSuccessorsTreurePeticio(board));
        }

        if (usarMoure) {
            retval.addAll(generarSuccessorsMourePeticio(board));
        }

        if (usarIntercanviar) {
            retval.addAll(generarSuccessorsIntercanviarPeticions(board));
        }

        if (usarCrear) {
            retval.addAll(generarSuccessorsCrearViatge(board));
        }

        return retval;
    }


    // ============ OPERADOR 1: AFEGIR PETICIÓ A VIATGE EXISTENT ============

    /**
     * Afegeix una petició no assignada a un viatge que encara no està ple
     */
    private List<Successor> generarSuccessorsAfegirPeticio(PracticaBoard board) {
        List<Successor> successors = new ArrayList<>();

        for (int idCamio = 0; idCamio < board.getNumCamions(); idCamio++) {
            List<Viatge> viatges = board.getViatgesPerCamio()[idCamio];

            for (int idxViatge = 0; idxViatge < viatges.size(); idxViatge++) {
                Viatge viatge = viatges.get(idxViatge);

                if (!viatge.esPle()) {
                    for (Peticio p : board.getPeticionsNoAssignades()) {
                        PracticaBoard nouBoard = new PracticaBoard(board);

                        nouBoard.getViatgesPerCamio()[idCamio].get(idxViatge).afegirPeticio(p);
                        nouBoard.getPeticionsNoAssignades().remove(p);

                        if (nouBoard.compleixRestriccions(idCamio)) {
                            String action = String.format("AfegirPeticio: Camió %d, Viatge %d, Gasolinera %d",
                                    idCamio, idxViatge, p.idGasolinera);
                            successors.add(new Successor(action, nouBoard));
                        }
                    }
                }
            }
        }

        return successors;
    }


    // ============ OPERADOR 2: TREURE PETICIÓ DE VIATGE ============

    private List<Successor> generarSuccessorsTreurePeticio(PracticaBoard board) {
        List<Successor> successors = new ArrayList<>();

        for (int idCamio = 0; idCamio < board.getNumCamions(); idCamio++) {
            List<Viatge> viatges = board.getViatgesPerCamio()[idCamio];

            for (int idxViatge = 0; idxViatge < viatges.size(); idxViatge++) {
                Viatge viatge = viatges.get(idxViatge);

                for (int idxPeticio = 0; idxPeticio < viatge.peticionsServides.size(); idxPeticio++) {
                    PracticaBoard nouBoard = new PracticaBoard(board);

                    Peticio p = nouBoard.getViatgesPerCamio()[idCamio].get(idxViatge)
                            .peticionsServides.remove(idxPeticio);
                    nouBoard.getPeticionsNoAssignades().add(p);

                    if (nouBoard.getViatgesPerCamio()[idCamio].get(idxViatge).peticionsServides.isEmpty()) {
                        nouBoard.getViatgesPerCamio()[idCamio].remove(idxViatge);
                    }

                    String action = String.format("TreurePeticio: Camió %d, Viatge %d, Gasolinera %d",
                            idCamio, idxViatge, p.idGasolinera);
                    successors.add(new Successor(action, nouBoard));
                }
            }
        }

        return successors;
    }


    // ============ OPERADOR 3: MOURE PETICIÓ ENTRE VIATGES ============

    private List<Successor> generarSuccessorsMourePeticio(PracticaBoard board) {
        List<Successor> successors = new ArrayList<>();

        for (int idCamioOrigen = 0; idCamioOrigen < board.getNumCamions(); idCamioOrigen++) {
            for (int idCamioDesti = 0; idCamioDesti < board.getNumCamions(); idCamioDesti++) {

                List<Viatge> viatgesOrigen = board.getViatgesPerCamio()[idCamioOrigen];
                List<Viatge> viatgesDesti = board.getViatgesPerCamio()[idCamioDesti];

                for (int idxViatgeOrigen = 0; idxViatgeOrigen < viatgesOrigen.size(); idxViatgeOrigen++) {
                    Viatge viatgeOrigen = viatgesOrigen.get(idxViatgeOrigen);

                    for (int idxViatgeDesti = 0; idxViatgeDesti < viatgesDesti.size(); idxViatgeDesti++) {
                        if (idCamioOrigen == idCamioDesti && idxViatgeOrigen == idxViatgeDesti) {
                            continue;
                        }

                        Viatge viatgeDesti = viatgesDesti.get(idxViatgeDesti);

                        if (viatgeDesti.esPle()) {
                            continue;
                        }

                        for (int idxPeticio = 0; idxPeticio < viatgeOrigen.peticionsServides.size(); idxPeticio++) {
                            PracticaBoard nouBoard = new PracticaBoard(board);

                            Peticio p = nouBoard.getViatgesPerCamio()[idCamioOrigen]
                                    .get(idxViatgeOrigen).peticionsServides.remove(idxPeticio);

                            nouBoard.getViatgesPerCamio()[idCamioDesti]
                                    .get(idxViatgeDesti).afegirPeticio(p);

                            if (nouBoard.getViatgesPerCamio()[idCamioOrigen].get(idxViatgeOrigen)
                                    .peticionsServides.isEmpty()) {
                                nouBoard.getViatgesPerCamio()[idCamioOrigen].remove(idxViatgeOrigen);
                            }

                            if (nouBoard.compleixRestriccions(idCamioOrigen) &&
                                    nouBoard.compleixRestriccions(idCamioDesti)) {
                                String action = String.format("MourePeticio: Gasolinera %d de Camió %d a Camió %d",
                                        p.idGasolinera, idCamioOrigen, idCamioDesti);
                                successors.add(new Successor(action, nouBoard));
                            }
                        }
                    }
                }
            }
        }

        return successors;
    }


    // ============ OPERADOR 4: INTERCANVIAR PETICIONS ============

    private List<Successor> generarSuccessorsIntercanviarPeticions(PracticaBoard board) {
        List<Successor> successors = new ArrayList<>();

        for (int idCamio1 = 0; idCamio1 < board.getNumCamions(); idCamio1++) {
            for (int idCamio2 = 0; idCamio2 < board.getNumCamions(); idCamio2++) {

                List<Viatge> viatges1 = board.getViatgesPerCamio()[idCamio1];
                List<Viatge> viatges2 = board.getViatgesPerCamio()[idCamio2];

                for (int idxViatge1 = 0; idxViatge1 < viatges1.size(); idxViatge1++) {
                    Viatge viatge1 = viatges1.get(idxViatge1);

                    for (int idxViatge2 = 0; idxViatge2 < viatges2.size(); idxViatge2++) {
                        if (idCamio1 == idCamio2 && idxViatge1 == idxViatge2) {
                            continue;
                        }

                        Viatge viatge2 = viatges2.get(idxViatge2);

                        for (int idxP1 = 0; idxP1 < viatge1.peticionsServides.size(); idxP1++) {
                            for (int idxP2 = 0; idxP2 < viatge2.peticionsServides.size(); idxP2++) {
                                PracticaBoard nouBoard = new PracticaBoard(board);

                                Peticio p1 = nouBoard.getViatgesPerCamio()[idCamio1]
                                        .get(idxViatge1).peticionsServides.get(idxP1);
                                Peticio p2 = nouBoard.getViatgesPerCamio()[idCamio2]
                                        .get(idxViatge2).peticionsServides.get(idxP2);

                                nouBoard.getViatgesPerCamio()[idCamio1]
                                        .get(idxViatge1).peticionsServides.set(idxP1, p2);
                                nouBoard.getViatgesPerCamio()[idCamio2]
                                        .get(idxViatge2).peticionsServides.set(idxP2, p1);

                                if (nouBoard.compleixRestriccions(idCamio1) &&
                                        nouBoard.compleixRestriccions(idCamio2)) {
                                    String action = String.format("IntercanviarPeticions: G%d (C%d) <-> G%d (C%d)",
                                            p1.idGasolinera, idCamio1,
                                            p2.idGasolinera, idCamio2);
                                    successors.add(new Successor(action, nouBoard));
                                }
                            }
                        }
                    }
                }
            }
        }

        return successors;
    }


    // ============ OPERADOR 5: CREAR VIATGE NOU ============

    private List<Successor> generarSuccessorsCrearViatge(PracticaBoard board) {
        List<Successor> successors = new ArrayList<>();

        for (int idCamio = 0; idCamio < board.getNumCamions(); idCamio++) {
            if (board.getViatgesPerCamio()[idCamio].size() < PracticaBoard.getMaxViatgesDia()) {

                for (Peticio p : board.getPeticionsNoAssignades()) {
                    PracticaBoard nouBoard = new PracticaBoard(board);

                    Viatge nouViatge = new Viatge(idCamio);
                    nouViatge.afegirPeticio(p);

                    nouBoard.getViatgesPerCamio()[idCamio].add(nouViatge);
                    nouBoard.getPeticionsNoAssignades().remove(p);

                    if (nouBoard.compleixRestriccions(idCamio)) {
                        String action = String.format("CrearViatge: Camió %d, Gasolinera %d",
                                idCamio, p.idGasolinera);
                        successors.add(new Successor(action, nouBoard));
                    }
                }
            }
        }

        return successors;
    }

    // ============ GETTERS ============

    public String getDescripcio() {
        List<String> operadors = new ArrayList<>();
        if (usarAfegir) operadors.add("Afegir");
        if (usarTreure) operadors.add("Treure");
        if (usarMoure) operadors.add("Moure");
        if (usarIntercanviar) operadors.add("Intercanviar");
        if (usarCrear) operadors.add("Crear");
        return String.join("+", operadors);
    }
}