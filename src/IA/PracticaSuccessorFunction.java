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


    /**
     * Constructor per defecte: utilitza tots els operadors
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

                        if (nouBoard.compleixRestriccions(idCamio)) {
                            String action = String.format(
                                "AfegirPeticio: Camió %d, Viatge %d, " +
                                "Gasolinera %d", idCamio, idxViatge, 
                                p.getIdGasolinera());
                            successors.add(new Successor(action, nouBoard));
                        }
                    }
                }
            }
        }

        return successors;
    }


    private List<Successor> treurePeticio(PracticaBoard board) {
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

                    if (nouBoard.getViatgesPerCamio()[idCamio]
                                .get(idxViatge).getPeticionsServides()
                                .isEmpty()) 
                    {
                        nouBoard.getViatgesPerCamio()[idCamio]
                                .remove(idxViatge);
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


    private List<Successor> mourePeticio(PracticaBoard board) 
    {
        List<Successor> successors = new ArrayList<>();

        for (int i = 0; i < board.getNumCamions(); i++) 
        {
            for (int j = 0; j < board.getNumCamions(); j++) 
            {
                List<Viatge> viatgesOrigen = board.getViatgesPerCamio()[i];
                List<Viatge> viatgesDesti = board.getViatgesPerCamio()[j];

                for (int k = 0; k < viatgesOrigen.size(); k++) 
                {
                    Viatge viatgeOrigen = viatgesOrigen.get(k);

                    for (int l = 0; l < viatgesDesti.size(); l++) 
                    {
                        if (i == j && k == l) 
                        {
                            continue;
                        }

                        Viatge viatgeDesti = viatgesDesti.get(l);

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
                                successors.add(new Successor(action, nouBoard));
                            }
                        }
                    }
                }
            }
        }

        return successors;
    }


    private List<Successor> intercanviarPeticions(PracticaBoard board) 
    {
        List<Successor> successors = new ArrayList<>();

        for (int i = 0; i < board.getNumCamions(); i++) 
        {
            for (int j = 0; j < board.getNumCamions(); j++) 
            {
                List<Viatge> viatges1 = board.getViatgesPerCamio()[i];
                List<Viatge> viatges2 = board.getViatgesPerCamio()[j];

                for (int k = 0; k < viatges1.size(); k++) 
                {
                    Viatge viatge1 = viatges1.get(k);

                    for (int l = 0; l < viatges2.size(); l++) 
                    {
                        if (i == j && k == l) 
                        {
                            continue;
                        }

                        Viatge viatge2 = viatges2.get(l);

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

                                nouBoard.getViatgesPerCamio()[i]
                                        .get(k)
                                        .getPeticionsServides()
                                        .set(m, p2);
                                nouBoard.getViatgesPerCamio()[j]
                                        .get(l)
                                        .getPeticionsServides()
                                        .set(n, p1);

                                if (nouBoard.compleixRestriccions(i) &&
                                    nouBoard.compleixRestriccions(j)) 
                                {
                                    String action = String.format(
                                        "IntercanviarPeticions: G%d (C%d) " +
                                        "<-> G%d (C%d)", p1.getIdGasolinera(), 
                                        i, p2.getIdGasolinera(), j);
                                    successors.add(new Successor(action, 
                                                                 nouBoard));
                                }
                            }
                        }
                    }
                }
            }
        }

        return successors;
    }


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
                    PracticaBoard nouBoard = new PracticaBoard(board);

                    Viatge nouViatge = new Viatge(idCamio);
                    nouViatge.afegirPeticio(p);

                    nouBoard.getViatgesPerCamio()[idCamio].add(nouViatge);
                    nouBoard.getPeticionsNoAssignades().remove(p);

                    if (nouBoard.compleixRestriccions(idCamio)) 
                    {
                        String action = String.format(
                            "CrearViatge: Camió %d, Gasolinera %d",
                            idCamio, p.getIdGasolinera());
                        successors.add(new Successor(action, nouBoard));
                    }
                }
            }
        }

        return successors;
    }
}