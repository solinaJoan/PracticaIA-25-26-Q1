package IA;
import aima.search.framework.SuccessorFunction;
import aima.search.framework.Successor;
import java.util.ArrayList;
import java.util.List;
import aima.search.framework.SuccessorFunction;

public class PracticaSuccsesorFunction implements SuccessorFunction {

    public List getSuccessors(Object state) {
        ArrayList retval = new ArrayList();
        PracticaBoard board = (PracticaBoard) state;

        // Apliquem operadors i afegim a la llista
            // Fem una copia del actual per no modificar-lo
            PracticaBoard nouBoard = new PracticaBoard(board);
            // nouBoard.aplicar_operador();

            // Creem un sucsessor
            String action = "Descripcio operador";
            Successor s = new Successor(action, nouBoard);

            // L'afegim a la llista que retornem
            retval.add(s);

        return retval;
    }
}
