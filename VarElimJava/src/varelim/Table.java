package varelim;

import java.util.ArrayList;

/**
 * Class to represent the Table Object consisting of probability rows
 *
 * @author Marcel de Korte, Moira Berens, Djamari Oetringer, Abdullahi Ali, Leonieke van den Bulk
 */
public class Table {

    private final ArrayList<ProbRow> TABLE;
    private Variable node;
    private ArrayList<Variable> parents;
    private ArrayList<Variable> headers;

    /**
     * Constructor of the class.
     *
     * @param table made out of probability rows (ProbRows)
     * @param node belonging to the current probability table
     * @param parents belonging to the current probability table
     */
    public Table(ArrayList<ProbRow> table, Variable node, ArrayList<Variable> parents) {
        this.TABLE = table;
        this.node = node;
        this.parents = parents;
    }

    public Table(ArrayList<ProbRow> table, ArrayList<Variable> headers) {
        this.TABLE = table;
        this.headers = headers;
    }

    /**
     * Getter of the table made out of ProbRows
     *
     * @return table
     */
    public ArrayList<ProbRow> getTable() {
        return TABLE;
    }

    /**
     * Getter of the node that belongs to the probability table
     *
     * @return the node
     */
    public Variable getNode() {
        return node;
    }

    /**
     * Getter of the parents that belong to the probability table
     *
     * @return the parents
     */
    public ArrayList<Variable> getParents() {
        return parents;
    }

    public ArrayList<Variable> getHeaders() {
        return headers;
    }

    /**
     * Gets the i'th element from the ArrayList of ProbRows
     *
     * @param i index
     * @return i'th ProbRow in Table
     */
    public ProbRow get(int i) {
        return TABLE.get(i);
    }

    /**
     * Returns the size of the Table (amount of probability rows)
     *
     * @return size of Table
     */
    public int size() {
        return TABLE.size();
    }
}
