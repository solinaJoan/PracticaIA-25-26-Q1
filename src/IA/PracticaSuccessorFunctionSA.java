package IA;
import aima.search.framework.SuccessorFunction;
import aima.search.framework.Successor;
import IA.PracticaBoard.*;
import java.util.*;

public class PracticaSuccessorFunctionSA implements SuccessorFunction {

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
                for (int k = 0; k < board.getViatgesPerCamio()[i].get(j).getPeticionsServides().size(); k++) {
                    peticions.add(new int[]{i, j, k});
                }
            }
        }

        if (peticions.isEmpty()) return null;

        int[] pos = peticions.get(random.nextInt(peticions.size()));

        PracticaBoard nouBoard = new PracticaBoard(board);
        Peticio p = nouBoard.getViatgesPerCamio()[pos[0]].get(pos[1])
                .getPeticionsServides().remove(pos[2]);
        nouBoard.getPeticionsNoAssignades().add(p);

        if (nouBoard.getViatgesPerCamio()[pos[0]].get(pos[1]).getPeticionsServides().isEmpty()) {
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
                for (int k = 0; k < board.getViatgesPerCamio()[i].get(j).getPeticionsServides().size(); k++) {
                    peticions.add(new int[]{i, j, k});
                }
            }
        }

        if (peticions.size() < 2) return null;

        int[] pos1 = peticions.get(random.nextInt(peticions.size()));
        int[] pos2 = peticions.get(random.nextInt(peticions.size()));

        PracticaBoard nouBoard = new PracticaBoard(board);
        Peticio p1 = nouBoard.getViatgesPerCamio()[pos1[0]].get(pos1[1]).getPeticionsServides().get(pos1[2]);
        Peticio p2 = nouBoard.getViatgesPerCamio()[pos2[0]].get(pos2[1]).getPeticionsServides().get(pos2[2]);

        nouBoard.getViatgesPerCamio()[pos1[0]].get(pos1[1]).getPeticionsServides().set(pos1[2], p2);
        nouBoard.getViatgesPerCamio()[pos2[0]].get(pos2[1]).getPeticionsServides().set(pos2[2], p1);

        return nouBoard;
    }

    private PracticaBoard aplicarCrearViatge(PracticaBoard board) {
        if (board.getPeticionsNoAssignades().isEmpty()) return null;

        // Busquem camions que puguin fer més viatges
        List<Integer> camionsDisponibles = new ArrayList<>();
        for (int i = 0; i < board.getNumCamions(); i++) {
            if (board.getViatgesPerCamio()[i].size() < PracticaBoard.getMaxViatgesDia()) {
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
}