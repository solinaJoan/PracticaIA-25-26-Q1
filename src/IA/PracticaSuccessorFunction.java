package IA;

import aima.search.framework.SuccessorFunction;
import aima.search.framework.Successor;
import java.util.*;

public class PracticaSuccessorFunction implements SuccessorFunction 
{
    // Configuració dels operadors a utilitzar
    private boolean usarAfegir;
    private boolean usarTreure;
    private boolean usarMoure;
    private boolean usarIntercanviar;
    private boolean usarCrear;


    // Configuració dels operadors a utilitzar
    private boolean usarAfegir = true;
    private boolean usarTreure = true;
    private boolean usarMoure = true;
    private boolean usarIntercanviar = true;
    private boolean usarCrear = true;

    /**
     * Constructor per defecte: utilitza tots els operadors
<<<<<<< HEAD
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
=======
>>>>>>> ea54be6591a350c59b721bc86bbb347cd0f78251
     */
    public PracticaSuccessorFunction() 
    {
        this.usarAfegir = true;
        this.usarTreure = true;
        this.usarMoure = true;
        this.usarIntercanviar = true;
        this.usarCrear = true;
    }


    /**
     * Constructor configurable: permet escollir quins operadors utilitzar
     */
    public PracticaSuccessorFunction(boolean afegir, boolean treure, 
                                     boolean moure, boolean intercanviar, 
                                     boolean crear) 
    {
        this.usarAfegir = afegir;
        this.usarTreure = treure;
        this.usarMoure = moure;
        this.usarIntercanviar = intercanviar;
        this.usarCrear = crear;
    }


    /**
     * Genera tots els successors possibles aplicant els operadors configurats
     */
    public List<Successor> getSuccessors(Object state) 
    {
        List<Successor> retval = new ArrayList<>();
        PracticaBoard board = (PracticaBoard) state;

<<<<<<< HEAD
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
=======
        if (usarAfegir) 
        {
            retval.addAll(afegirPeticio(board));
        }

        if (usarTreure) 
        {
            retval.addAll(treurePeticio(board));
        }

        if (usarMoure) 
        {
            retval.addAll(mourePeticio(board));
        }

        if (usarIntercanviar) 
        {
            retval.addAll(intercanviarPeticions(board));
        }

        if (usarCrear) 
        {
            retval.addAll(crearViatge(board));
>>>>>>> ea54be6591a350c59b721bc86bbb347cd0f78251
        }

        return retval;
    }


    public String getDescripcio() 
    {
        List<String> operadors = new ArrayList<>();
        if (usarAfegir) operadors.add("Afegir");
        if (usarTreure) operadors.add("Treure");
        if (usarMoure) operadors.add("Moure");
        if (usarIntercanviar) operadors.add("Intercanviar");
        if (usarCrear) operadors.add("Crear");
        return String.join("+", operadors);
    }


    // OPERADORS PRIVATS


    /**
     * Afegeix una petició no assignada a un viatge que encara no està ple
     */
    private List<Successor> afegirPeticio(PracticaBoard board) {
        List<Successor> successors = new ArrayList<>();

<<<<<<< HEAD
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
=======
        for (int idCamio = 0; idCamio < board.getNumCamions(); idCamio++) 
        {
            List<Viatge> viatges = board.getViatgesPerCamio()[idCamio];

            for (int idxViatge = 0; idxViatge < viatges.size(); idxViatge++) 
            {
                Viatge viatge = viatges.get(idxViatge);
                
                if (!viatge.esPle()) 
                {
                    for (Peticio p : board.getPeticionsNoAssignades()) 
                    {
                        PracticaBoard nouBoard = new PracticaBoard(board);

                        nouBoard.getViatgesPerCamio()[idCamio]
                                .get(idxViatge)
                                .afegirPeticio(p);
                        nouBoard.getPeticionsNoAssignades().remove(p);

                        if (nouBoard.compleixRestriccions(idCamio)) 
                        {
                            String action = String.format(
                                "AfegirPeticio: Camió %d, Viatge %d, " +
                                "Gasolinera %d", idCamio, idxViatge, 
                                p.getIdGasolinera());
>>>>>>> ea54be6591a350c59b721bc86bbb347cd0f78251
                            successors.add(new Successor(action, nouBoard));
                        }
                    }
                }
            }
        }

        return successors;
    }


<<<<<<< HEAD
    // ============ OPERADOR 2: TREURE PETICIÓ DE VIATGE ============

    private List<Successor> generarSuccessorsTreurePeticio(PracticaBoard board) {
=======
    private List<Successor> treurePeticio(PracticaBoard board) {
>>>>>>> ea54be6591a350c59b721bc86bbb347cd0f78251
        List<Successor> successors = new ArrayList<>();

        for (int idCamio = 0; idCamio < board.getNumCamions(); idCamio++) 
        {
            List<Viatge> viatges = board.getViatgesPerCamio()[idCamio];

            for (int idxViatge = 0; idxViatge < viatges.size(); idxViatge++) 
            {
                Viatge viatge = viatges.get(idxViatge);

                int num_pet_serv = viatge.getPeticionsServides().size();
                for (int i = 0; i < num_pet_serv; i++) 
                {
                    PracticaBoard nouBoard = new PracticaBoard(board);

                    Peticio p = nouBoard.getViatgesPerCamio()[idCamio]
                                        .get(idxViatge)
                                        .getPeticionsServides().remove(i);
                    nouBoard.getPeticionsNoAssignades().add(p);

<<<<<<< HEAD
                    if (nouBoard.getViatgesPerCamio()[idCamio].get(idxViatge).peticionsServides.isEmpty()) {
                        nouBoard.getViatgesPerCamio()[idCamio].remove(idxViatge);
=======
                    if (nouBoard.getViatgesPerCamio()[idCamio]
                                .get(idxViatge).getPeticionsServides()
                                .isEmpty()) 
                    {
                        nouBoard.getViatgesPerCamio()[idCamio]
                                .remove(idxViatge);
>>>>>>> ea54be6591a350c59b721bc86bbb347cd0f78251
                    }

                    String action = String.format(
                        "TreurePeticio: Camió %d, Viatge %d, Gasolinera %d",
                        idCamio, idxViatge, p.getIdGasolinera());
                    successors.add(new Successor(action, nouBoard));
                }
            }
        }

        return successors;
    }


<<<<<<< HEAD
    // ============ OPERADOR 3: MOURE PETICIÓ ENTRE VIATGES ============

    private List<Successor> generarSuccessorsMourePeticio(PracticaBoard board) {
        List<Successor> successors = new ArrayList<>();

        for (int idCamioOrigen = 0; idCamioOrigen < board.getNumCamions(); idCamioOrigen++) {
            for (int idCamioDesti = 0; idCamioDesti < board.getNumCamions(); idCamioDesti++) {
=======
    private List<Successor> mourePeticio(PracticaBoard board) 
    {
        List<Successor> successors = new ArrayList<>();

        for (int i = 0; i < board.getNumCamions(); i++) 
        {
            for (int j = 0; j < board.getNumCamions(); j++) 
            {
                List<Viatge> viatgesOrigen = board.getViatgesPerCamio()[i];
                List<Viatge> viatgesDesti = board.getViatgesPerCamio()[j];
>>>>>>> ea54be6591a350c59b721bc86bbb347cd0f78251

                for (int k = 0; k < viatgesOrigen.size(); k++) 
                {
                    Viatge viatgeOrigen = viatgesOrigen.get(k);

<<<<<<< HEAD
                for (int idxViatgeOrigen = 0; idxViatgeOrigen < viatgesOrigen.size(); idxViatgeOrigen++) {
                    Viatge viatgeOrigen = viatgesOrigen.get(idxViatgeOrigen);

                    for (int idxViatgeDesti = 0; idxViatgeDesti < viatgesDesti.size(); idxViatgeDesti++) {
                        if (idCamioOrigen == idCamioDesti && idxViatgeOrigen == idxViatgeDesti) {
=======
                    for (int l = 0; l < viatgesDesti.size(); l++) 
                    {
                        if (i == j && k == l) 
                        {
>>>>>>> ea54be6591a350c59b721bc86bbb347cd0f78251
                            continue;
                        }

                        Viatge viatgeDesti = viatgesDesti.get(l);

<<<<<<< HEAD
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
=======
                        if (viatgeDesti.esPle()) 
                        {
                            continue;
                        }
                        
                        int num_pet_serv = viatgeOrigen.getPeticionsServides()
                                                       .size();
                        for (int m = 0; m < num_pet_serv; m++) 
                        {
                            PracticaBoard nouBoard = new PracticaBoard(board);

                            Peticio p = nouBoard.getViatgesPerCamio()[i]
                                                .get(k)
                                                .getPeticionsServides()
                                                .remove(m);

                            nouBoard.getViatgesPerCamio()[j]
                                    .get(l)
                                    .afegirPeticio(p);

                            if (nouBoard.getViatgesPerCamio()[i].get(k)
                                        .getPeticionsServides().isEmpty()) 
                            {
                                nouBoard.getViatgesPerCamio()[i].remove(k);
                            }

                            if (nouBoard.compleixRestriccions(i) &&
                                nouBoard.compleixRestriccions(j)) 
                            {
                                String action = String.format(
                                    "MourePeticio: Gasolinera %d de Camió %d" + 
                                    "a Camió %d", p.getIdGasolinera(), i, j);
>>>>>>> ea54be6591a350c59b721bc86bbb347cd0f78251
                                successors.add(new Successor(action, nouBoard));
                            }
                        }
                    }
                }
            }
        }

        return successors;
    }


<<<<<<< HEAD
    // ============ OPERADOR 4: INTERCANVIAR PETICIONS ============

    private List<Successor> generarSuccessorsIntercanviarPeticions(PracticaBoard board) {
        List<Successor> successors = new ArrayList<>();

        for (int idCamio1 = 0; idCamio1 < board.getNumCamions(); idCamio1++) {
            for (int idCamio2 = 0; idCamio2 < board.getNumCamions(); idCamio2++) {
=======
    private List<Successor> intercanviarPeticions(PracticaBoard board) 
    {
        List<Successor> successors = new ArrayList<>();

        for (int i = 0; i < board.getNumCamions(); i++) 
        {
            for (int j = 0; j < board.getNumCamions(); j++) 
            {
                List<Viatge> viatges1 = board.getViatgesPerCamio()[i];
                List<Viatge> viatges2 = board.getViatgesPerCamio()[j];
>>>>>>> ea54be6591a350c59b721bc86bbb347cd0f78251

                for (int k = 0; k < viatges1.size(); k++) 
                {
                    Viatge viatge1 = viatges1.get(k);

<<<<<<< HEAD
                for (int idxViatge1 = 0; idxViatge1 < viatges1.size(); idxViatge1++) {
                    Viatge viatge1 = viatges1.get(idxViatge1);

                    for (int idxViatge2 = 0; idxViatge2 < viatges2.size(); idxViatge2++) {
                        if (idCamio1 == idCamio2 && idxViatge1 == idxViatge2) {
=======
                    for (int l = 0; l < viatges2.size(); l++) 
                    {
                        if (i == j && k == l) 
                        {
>>>>>>> ea54be6591a350c59b721bc86bbb347cd0f78251
                            continue;
                        }

                        Viatge viatge2 = viatges2.get(l);

<<<<<<< HEAD
                        for (int idxP1 = 0; idxP1 < viatge1.peticionsServides.size(); idxP1++) {
                            for (int idxP2 = 0; idxP2 < viatge2.peticionsServides.size(); idxP2++) {
                                PracticaBoard nouBoard = new PracticaBoard(board);

                                Peticio p1 = nouBoard.getViatgesPerCamio()[idCamio1]
                                        .get(idxViatge1).peticionsServides.get(idxP1);
                                Peticio p2 = nouBoard.getViatgesPerCamio()[idCamio2]
                                        .get(idxViatge2).peticionsServides.get(idxP2);
=======
                        int num_pet_serv_v1 = viatge1.getPeticionsServides()
                                                     .size();
                        for (int m = 0; m < num_pet_serv_v1; m++) 
                        {
                            int num_pet_serv_v2 = viatge2.getPeticionsServides()
                                                         .size();
                            for (int n = 0; n < num_pet_serv_v2; n++) 
                            {
                                PracticaBoard nouBoard = new PracticaBoard(board);

                                Peticio p1 = nouBoard.getViatgesPerCamio()[i]
                                                     .get(k)
                                                     .getPeticionsServides()
                                                     .get(m);
                                Peticio p2 = nouBoard.getViatgesPerCamio()[j]
                                                     .get(l)
                                                     .getPeticionsServides()
                                                     .get(n);
>>>>>>> ea54be6591a350c59b721bc86bbb347cd0f78251

                                nouBoard.getViatgesPerCamio()[i]
                                        .get(k)
                                        .getPeticionsServides()
                                        .set(m, p2);
                                nouBoard.getViatgesPerCamio()[j]
                                        .get(l)
                                        .getPeticionsServides()
                                        .set(n, p1);

<<<<<<< HEAD
                                if (nouBoard.compleixRestriccions(idCamio1) &&
                                        nouBoard.compleixRestriccions(idCamio2)) {
                                    String action = String.format("IntercanviarPeticions: G%d (C%d) <-> G%d (C%d)",
                                            p1.idGasolinera, idCamio1,
                                            p2.idGasolinera, idCamio2);
                                    successors.add(new Successor(action, nouBoard));
=======
                                if (nouBoard.compleixRestriccions(i) &&
                                    nouBoard.compleixRestriccions(j)) 
                                {
                                    String action = String.format(
                                        "IntercanviarPeticions: G%d (C%d) " +
                                        "<-> G%d (C%d)", p1.getIdGasolinera(), 
                                        i, p2.getIdGasolinera(), j);
                                    successors.add(new Successor(action, 
                                                                 nouBoard));
>>>>>>> ea54be6591a350c59b721bc86bbb347cd0f78251
                                }
                            }
                        }
                    }
                }
            }
        }

        return successors;
    }


<<<<<<< HEAD
    // ============ OPERADOR 5: CREAR VIATGE NOU ============

    private List<Successor> generarSuccessorsCrearViatge(PracticaBoard board) {
        List<Successor> successors = new ArrayList<>();

        for (int idCamio = 0; idCamio < board.getNumCamions(); idCamio++) {
            if (board.getViatgesPerCamio()[idCamio].size() < PracticaBoard.getMaxViatgesDia()) {

                for (Peticio p : board.getPeticionsNoAssignades()) {
=======
    private List<Successor> crearViatge(PracticaBoard board) 
    {
        List<Successor> successors = new ArrayList<>();

        for (int idCamio = 0; idCamio < board.getNumCamions(); idCamio++) 
        {
            if (board.getViatgesPerCamio()[idCamio].size() < 
                PracticaBoard.getMaxViatgesDia()) 
            {

                for (Peticio p : board.getPeticionsNoAssignades()) 
                {
>>>>>>> ea54be6591a350c59b721bc86bbb347cd0f78251
                    PracticaBoard nouBoard = new PracticaBoard(board);

                    Viatge nouViatge = new Viatge(idCamio);
                    nouViatge.afegirPeticio(p);

                    nouBoard.getViatgesPerCamio()[idCamio].add(nouViatge);
                    nouBoard.getPeticionsNoAssignades().remove(p);

<<<<<<< HEAD
                    if (nouBoard.compleixRestriccions(idCamio)) {
                        String action = String.format("CrearViatge: Camió %d, Gasolinera %d",
                                idCamio, p.idGasolinera);
=======
                    if (nouBoard.compleixRestriccions(idCamio)) 
                    {
                        String action = String.format(
                            "CrearViatge: Camió %d, Gasolinera %d",
                            idCamio, p.getIdGasolinera());
>>>>>>> ea54be6591a350c59b721bc86bbb347cd0f78251
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