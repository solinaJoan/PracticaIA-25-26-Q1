package IA;

import aima.search.framework.SuccessorFunction;
import aima.search.framework.Successor;
import IA.PracticaBoard.*;
import java.util.*;

public class PracticaSuccessorFunction implements SuccessorFunction {

    /**
     * Genera tots els successors possibles aplicant els operadors
     */
    public List getSuccessors(Object state) {
        ArrayList retval = new ArrayList();
        PracticaBoard board = (PracticaBoard) state;

        // OPERADOR 1: AFEGIR una petició no assignada a un viatge existent
        retval.addAll(generarSuccessorsAfegirPeticio(board));

        // OPERADOR 2: TREURE una petició d'un viatge
        retval.addAll(generarSuccessorsTreurePeticio(board));

        // OPERADOR 3: MOURE una petició d'un viatge a un altre
        retval.addAll(generarSuccessorsMourePeticio(board));

        // OPERADOR 4: INTERCANVIAR dues peticions entre viatges
        retval.addAll(generarSuccessorsIntercanviarPeticions(board));

        // OPERADOR 5: CREAR un viatge nou amb una petició no assignada
        retval.addAll(generarSuccessorsCrearViatge(board));

        return retval;
    }


    // ============ OPERADOR 1: AFEGIR PETICIÓ A VIATGE EXISTENT ============

    /**
     * Afegeix una petició no assignada a un viatge que encara no està ple
     */
    private List<Successor> generarSuccessorsAfegirPeticio(PracticaBoard board) {
        List<Successor> successors = new ArrayList<>();

        // Per cada camió
        for (int idCamio = 0; idCamio < board.getNumCamions(); idCamio++) {
            List<Viatge> viatges = board.getViatgesPerCamio()[idCamio];

            // Per cada viatge que no estigui ple
            for (int idxViatge = 0; idxViatge < viatges.size(); idxViatge++) {
                Viatge viatge = viatges.get(idxViatge);

                if (!viatge.esPle()) {
                    // Per cada petició no assignada
                    for (Peticio p : board.getPeticionsNoAssignades()) {
                        PracticaBoard nouBoard = new PracticaBoard(board);

                        // Afegim la petició al viatge
                        nouBoard.getViatgesPerCamio()[idCamio].get(idxViatge).afegirPeticio(p);
                        nouBoard.getPeticionsNoAssignades().remove(p);

                        // Només afegim si compleix les restriccions
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

    /**
     * Treu una petició d'un viatge i la torna a no assignada
     */
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

                    // Si el viatge queda buit, l'eliminem
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

    /**
     * Mou una petició d'un viatge a un altre viatge diferent
     */
    private List<Successor> generarSuccessorsMourePeticio(PracticaBoard board) {
        List<Successor> successors = new ArrayList<>();

        // Per cada parell de camions (pot ser el mateix o diferent)
        for (int idCamioOrigen = 0; idCamioOrigen < board.getNumCamions(); idCamioOrigen++) {
            for (int idCamioDesti = 0; idCamioDesti < board.getNumCamions(); idCamioDesti++) {

                List<Viatge> viatgesOrigen = board.getViatgesPerCamio()[idCamioOrigen];
                List<Viatge> viatgesDesti = board.getViatgesPerCamio()[idCamioDesti];

                // Per cada viatge origen
                for (int idxViatgeOrigen = 0; idxViatgeOrigen < viatgesOrigen.size(); idxViatgeOrigen++) {
                    Viatge viatgeOrigen = viatgesOrigen.get(idxViatgeOrigen);

                    // Per cada viatge destí
                    for (int idxViatgeDesti = 0; idxViatgeDesti < viatgesDesti.size(); idxViatgeDesti++) {
                        // No podem moure a nosaltres mateixos
                        if (idCamioOrigen == idCamioDesti && idxViatgeOrigen == idxViatgeDesti) {
                            continue;
                        }

                        Viatge viatgeDesti = viatgesDesti.get(idxViatgeDesti);

                        // Si el viatge destí està ple, no podem afegir-hi res
                        if (viatgeDesti.esPle()) {
                            continue;
                        }

                        // Per cada petició del viatge origen
                        for (int idxPeticio = 0; idxPeticio < viatgeOrigen.peticionsServides.size(); idxPeticio++) {
                            PracticaBoard nouBoard = new PracticaBoard(board);

                            // Treiem la petició del viatge origen
                            Peticio p = nouBoard.getViatgesPerCamio()[idCamioOrigen]
                                    .get(idxViatgeOrigen).peticionsServides.remove(idxPeticio);

                            // L'afegim al viatge destí
                            nouBoard.getViatgesPerCamio()[idCamioDesti]
                                    .get(idxViatgeDesti).afegirPeticio(p);

                            // Si el viatge origen queda buit, l'eliminem
                            if (nouBoard.getViatgesPerCamio()[idCamioOrigen].get(idxViatgeOrigen)
                                    .peticionsServides.isEmpty()) {
                                nouBoard.getViatgesPerCamio()[idCamioOrigen].remove(idxViatgeOrigen);
                            }

                            // Només afegim si compleix les restriccions
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

    /**
     * Intercanvia dues peticions entre dos viatges diferents
     */
    private List<Successor> generarSuccessorsIntercanviarPeticions(PracticaBoard board) {
        List<Successor> successors = new ArrayList<>();

        // Per cada parell de camions
        for (int idCamio1 = 0; idCamio1 < board.getNumCamions(); idCamio1++) {
            for (int idCamio2 = 0; idCamio2 < board.getNumCamions(); idCamio2++) {

                List<Viatge> viatges1 = board.getViatgesPerCamio()[idCamio1];
                List<Viatge> viatges2 = board.getViatgesPerCamio()[idCamio2];

                // Per cada viatge del camió 1
                for (int idxViatge1 = 0; idxViatge1 < viatges1.size(); idxViatge1++) {
                    Viatge viatge1 = viatges1.get(idxViatge1);

                    // Per cada viatge del camió 2
                    for (int idxViatge2 = 0; idxViatge2 < viatges2.size(); idxViatge2++) {
                        // No intercanviem amb nosaltres mateixos
                        if (idCamio1 == idCamio2 && idxViatge1 == idxViatge2) {
                            continue;
                        }

                        Viatge viatge2 = viatges2.get(idxViatge2);

                        // Per cada petició del viatge 1
                        for (int idxP1 = 0; idxP1 < viatge1.peticionsServides.size(); idxP1++) {
                            // Per cada petició del viatge 2
                            for (int idxP2 = 0; idxP2 < viatge2.peticionsServides.size(); idxP2++) {
                                PracticaBoard nouBoard = new PracticaBoard(board);

                                // Intercanviem les peticions
                                Peticio p1 = nouBoard.getViatgesPerCamio()[idCamio1]
                                        .get(idxViatge1).peticionsServides.get(idxP1);
                                Peticio p2 = nouBoard.getViatgesPerCamio()[idCamio2]
                                        .get(idxViatge2).peticionsServides.get(idxP2);

                                nouBoard.getViatgesPerCamio()[idCamio1]
                                        .get(idxViatge1).peticionsServides.set(idxP1, p2);
                                nouBoard.getViatgesPerCamio()[idCamio2]
                                        .get(idxViatge2).peticionsServides.set(idxP2, p1);

                                // Només afegim si compleix les restriccions
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

    /**
     * Crea un viatge nou amb una petició no assignada
     */
    private List<Successor> generarSuccessorsCrearViatge(PracticaBoard board) {
        List<Successor> successors = new ArrayList<>();

        // Per cada camió
        for (int idCamio = 0; idCamio < board.getNumCamions(); idCamio++) {
            // Només si encara pot fer més viatges
            if (board.getViatgesPerCamio()[idCamio].size() < 5) {

                // Per cada petició no assignada
                for (Peticio p : board.getPeticionsNoAssignades()) {
                    PracticaBoard nouBoard = new PracticaBoard(board);

                    // Creem un viatge nou
                    Viatge nouViatge = new Viatge(idCamio);
                    nouViatge.afegirPeticio(p);

                    // L'afegim al camió
                    nouBoard.getViatgesPerCamio()[idCamio].add(nouViatge);
                    nouBoard.getPeticionsNoAssignades().remove(p);

                    // Només afegim si compleix les restriccions
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
}


/**
 * VERSIÓ ALTERNATIVA PER SIMULATED ANNEALING
 *
 * En aquesta versió només generem UN successor aleatori
 *//*
class PracticaSuccessorFunctionSA implements SuccessorFunction {

    private Random random = new Random();

    public List getSuccessors(Object state) {
        ArrayList retval = new ArrayList();
        PracticaBoard board = (PracticaBoard) state;

        // Escollim un operador aleatori
        int operador = random.nextInt(5);

        PracticaBoard nouBoard = null;
        String action = "";

        switch (operador) {
            case 0:
                nouBoard = aplicarAfegirPeticio(board);
                action = "AfegirPeticio (aleatori)";
                break;
            case 1:
                nouBoard = aplicarTreurePeticio(board);
                action = "TreurePeticio (aleatori)";
                break;
            case 2:
                nouBoard = aplicarMourePeticio(board);
                action = "MourePeticio (aleatori)";
                break;
            case 3:
                nouBoard = aplicarIntercanviarPeticions(board);
                action = "IntercanviarPeticions (aleatori)";
                break;
            case 4:
                nouBoard = aplicarCrearViatge(board);
                action = "CrearViatge (aleatori)";
                break;
        }

        if (nouBoard != null && nouBoard.compleixRestriccions()) {
            retval.add(new Successor(action, nouBoard));
        }

        return retval;
    }

    private PracticaBoard aplicarAfegirPeticio(PracticaBoard board) {
        if (board.getPeticionsNoAssignades().isEmpty()) return null;

        // Busquem un viatge que no estigui ple
        List<int[]> viatgesNoPlens = new ArrayList<>();
        for (int i = 0; i < board.getNumCamions(); i++) {
            for (int j = 0; j < board.getViatgesPerCamio()[i].size(); j++) {
                if (!board.getViatgesPerCamio()[i].get(j).esPle()) {
                    viatgesNoPlens.add(new int[]{i, j});
                }
            }
        }

        if (viatgesNoPlens.isEmpty()) return null;

        // Escollim un viatge aleatori
        int[] viatge = viatgesNoPlens.get(random.nextInt(viatgesNoPlens.size()));

        // Escollim una petició aleatòria
        List<Peticio> peticions = new ArrayList<>(board.getPeticionsNoAssignades());
        Peticio p = peticions.get(random.nextInt(peticions.size()));

        PracticaBoard nouBoard = new PracticaBoard(board);
        nouBoard.getViatgesPerCamio()[viatge[0]].get(viatge[1]).afegirPeticio(p);
        nouBoard.getPeticionsNoAssignades().remove(p);

        return nouBoard;
    }

    private PracticaBoard aplicarTreurePeticio(PracticaBoard board) {
        // Busquem totes les peticions assignades
        List<int[]> peticions = new ArrayList<>(); // [idCamio, idxViatge, idxPeticio]
        for (int i = 0; i < board.getNumCamions(); i++) {
            for (int j = 0; j < board.getViatgesPerCamio()[i].size(); j++) {
                for (int k = 0; k < board.getViatgesPerCamio()[i].get(j).peticionsServides.size(); k++) {
                    peticions.add(new int[]{i, j, k});
                }
            }
        }

        if (peticions.isEmpty()) return null;

        int[] pos = peticions.get(random.nextInt(peticions.size()));

        PracticaBoard nouBoard = new PracticaBoard(board);
        Peticio p = nouBoard.getViatgesPerCamio()[pos[0]].get(pos[1])
                .peticionsServides.remove(pos[2]);
        nouBoard.getPeticionsNoAssignades().add(p);

        if (nouBoard.getViatgesPerCamio()[pos[0]].get(pos[1]).peticionsServides.isEmpty()) {
            nouBoard.getViatgesPerCamio()[pos[0]].remove(pos[1]);
        }

        return nouBoard;
    }

    private PracticaBoard aplicarMourePeticio(PracticaBoard board) {
        // Similar a aplicarTreurePeticio + aplicarAfegirPeticio
        // Implementació simplificada
        PracticaBoard temp = aplicarTreurePeticio(board);
        if (temp == null) return null;
        return aplicarAfegirPeticio(temp);
    }

    private PracticaBoard aplicarIntercanviarPeticions(PracticaBoard board) {
        List<int[]> peticions = new ArrayList<>();
        for (int i = 0; i < board.getNumCamions(); i++) {
            for (int j = 0; j < board.getViatgesPerCamio()[i].size(); j++) {
                for (int k = 0; k < board.getViatgesPerCamio()[i].get(j).peticionsServides.size(); k++) {
                    peticions.add(new int[]{i, j, k});
                }
            }
        }

        if (peticions.size() < 2) return null;

        int[] pos1 = peticions.get(random.nextInt(peticions.size()));
        int[] pos2 = peticions.get(random.nextInt(peticions.size()));

        PracticaBoard nouBoard = new PracticaBoard(board);
        Peticio p1 = nouBoard.getViatgesPerCamio()[pos1[0]].get(pos1[1]).peticionsServides.get(pos1[2]);
        Peticio p2 = nouBoard.getViatgesPerCamio()[pos2[0]].get(pos2[1]).peticionsServides.get(pos2[2]);

        nouBoard.getViatgesPerCamio()[pos1[0]].get(pos1[1]).peticionsServides.set(pos1[2], p2);
        nouBoard.getViatgesPerCamio()[pos2[0]].get(pos2[1]).peticionsServides.set(pos2[2], p1);

        return nouBoard;
    }

    private PracticaBoard aplicarCrearViatge(PracticaBoard board) {
        if (board.getPeticionsNoAssignades().isEmpty()) return null;

        // Busquem camions que puguin fer més viatges
        List<Integer> camionsDisponibles = new ArrayList<>();
        for (int i = 0; i < board.getNumCamions(); i++) {
            if (board.getViatgesPerCamio()[i].size() < 5) {
                camionsDisponibles.add(i);
            }
        }

        if (camionsDisponibles.isEmpty()) return null;

        int idCamio = camionsDisponibles.get(random.nextInt(camionsDisponibles.size()));
        List<Peticio> peticions = new ArrayList<>(board.getPeticionsNoAssignades());
        Peticio p = peticions.get(random.nextInt(peticions.size()));

        PracticaBoard nouBoard = new PracticaBoard(board);
        Viatge nouViatge = new Viatge(idCamio);
        nouViatge.afegirPeticio(p);
        nouBoard.getViatgesPerCamio()[idCamio].add(nouViatge);
        nouBoard.getPeticionsNoAssignades().remove(p);

        return nouBoard;
    }
}*/