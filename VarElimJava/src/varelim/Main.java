package varelim;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Main class to read in a network, add queries and observed variables, and eliminate variables.
 *
 * @author Marcel de Korte, Moira Berens, Djamari Oetringer, Abdullahi Ali, Leonieke van den Bulk
 * 
 * EDITED BY:
 * 
 * @author Dennis den Hollander s4776658
 * @author Tom Kamp s4760921
 */
public class Main {
    // The network to be read in ( format from http://www.bnlearn.com/bnrepository/ )

    private final static String NETWORK_NAME = "earthquake.bif";
    // private final static String NETWORK_NAME = "cancer.bif";

    public static void main(String[] args) throws IOException {

        // Read in the network
        Networkreader reader;
        BufferedWriter writer;

        // Creates a log file on the users' desktop
        File logFile = new File(System.getProperty("user.home"), "/Desktop/log.txt");
        writer = new BufferedWriter(new FileWriter(logFile));
        reader = new Networkreader(NETWORK_NAME, writer);

        // Get the variables and probabilities of the network
        ArrayList<Variable> Vs = reader.getVs();
        ArrayList<Table> Ps = reader.getPs();
        
        // Print variables and probabilities
        reader.printNetwork(Vs, Ps);
        
        //Configure Table2
        ArrayList<Table2> Ps2 = new ArrayList();
        for (Table t : Ps) {
            ArrayList<Variable> headers = t.getParents();
            headers.add(t.getNode());
            Ps2.add(new Table2(new Table(t.getTable(), headers)));
        }

        // Ask user for query and heuristic
        reader.askForQuery();

        // Turn this on if you want to experiment with different heuristics 
        // for bonus points (you need to implement the heuristics yourself)
        //reader.askForHeuristic();
        //String heuristic = reader.getHeuristic();
        Variable Q = reader.getQueriedVariable();

        // Ask user for observed variables 
        reader.askForObservedVariables();
        ArrayList<Variable> O = reader.getObservedVariables();

        // Print the query and observed variables
        reader.printQueryAndObserved(Q, O);
        
        /**
         * Algorithm implementation for variable elimination
         *
         * @author Dennis den Hollander | s4776658
         * @author Tom Kamp | s4760921
         */
        Table2 result = variableElimination(Vs, Ps2, O, Q, writer, reader);
        System.out.println("\nEnd result:");
        writer.write("End result:");
        writer.newLine();
        reader.printTable(result.getTableProp());
        writer.close();
    }

    public static Table2 variableElimination(ArrayList<Variable> Vs, ArrayList<Table2> Ps, ArrayList<Variable> O, Variable Q, BufferedWriter writer, Networkreader reader) throws IOException {

        ArrayList<Variable> copyVs = (ArrayList<Variable>) Vs.clone();
        ArrayList<Table2> copyPs = (ArrayList<Table2>) Ps.clone();
        copyVs.remove(Vs.indexOf(Q));

        writer.write("Elimination order and factor multiplication:");
        writer.newLine();

        /**
         * Check if variables are observed and if so, 
         * filter them out of all tables with coherence
         */
        for (Variable X : Vs) {
            if (X.getObserved()) {
                Ps = reduceObserved(X, Ps);
            }
        }

        /**
         * Now for the remaining variables, eliminate all in an specific order 
         * (we now assume the order of the Variables-list)
         */
        for (Variable X : copyVs) {
            System.out.println();
            writer.write("Variable to be removed: " + X.getName());
            writer.newLine();

            ArrayList<Table2> tablesMultiply = new ArrayList();

            System.out.println("Variable to be removed: " + X.getName());

            for (Table2 factor : copyPs) {
                for (Variable header : factor.getHeaders()) {
                    if (header.getName().equals(X.getName()) && !tablesMultiply.contains(factor)) {
                        tablesMultiply.add(factor);
                    }
                }
            }

            /**
             * Multiply all tables that have a relationship with variable X
             */
            System.out.println("Tables involved with variable " + X.getName());
            writer.write("Tables involved with variable " + X.getName() + ":");
            writer.newLine();
            for (Table2 t : tablesMultiply) {
                writer.newLine();
                reader.printTable(t.getTableProp());

            }

            Table2 result = tablesMultiply.get(0);
            tablesMultiply.remove(tablesMultiply.indexOf(result));

            copyPs.remove(copyPs.indexOf(result));
            if (tablesMultiply.size() > 0) {
                for (Table2 t : tablesMultiply) {
                    result = t.multiply(result);
                }
            }

            result = result.eliminate(X);
            if (result.getTable().size() > 2) {

                result = result.merge();
            }
            for (Table2 t : tablesMultiply) {

                copyPs.remove(copyPs.indexOf(t));
            }
            copyPs.add(result);
            Vs.remove(Vs.indexOf(X));

            System.out.println("Result of multiplication:");
            writer.newLine();
            writer.write("Result of multiplication:");
            writer.newLine();
            reader.printTable(result.getTableProp());
            writer.newLine();
        }
        Table2 query = tableMultiply(copyPs);
        ArrayList<ProbRow> newRows = new ArrayList();
        for (ProbRow row : query.getTable()) {

            ProbRow newRow = new ProbRow((row.getProb() / sumProbTable(query)), row.getValues());
            newRows.add(newRow);
        }
        return new Table2(new Table(newRows, query.getHeaders()));
    }

    /**
     * Reduces possibly given observed variables from all tables
     * @param v
     * @param Fs
     * @return 
     */
    public static ArrayList<Table2> reduceObserved(Variable v, ArrayList<Table2> Fs) {
        for (Table2 factor : Fs) {
            ArrayList<ProbRow> copyTable = (ArrayList<ProbRow>) factor.getTable().clone();
            for (ProbRow row : copyTable) {
                for (Variable header : factor.getHeaders()) {
                    if (header.getName().equals(v.getName())) {
                        String thisVal = v.getValue();
                        String otherVal = row.getValues().get(factor.getHeaders().indexOf(v));
                        if (!(thisVal.equals(otherVal))) {
                            factor.getTable().remove(row);
                        }
                    }
                }
            }
        }
        return Fs;
    }

    /**
     * Multiplies multiple tables together
     * @param list
     * @return 
     */
    public static Table2 tableMultiply(ArrayList<Table2> list) {
        Table2 query = list.get(0);
        list.remove(list.indexOf(query));
        if (list.size() > 0) {
            for (Table2 table : list) {
                query = table.multiply(query);
            }
        }
        return query;
    }

    /**
     * Calculates the sum of all probability values in a table
     * Is used for normalizing!
     * @param table
     * @return 
     */
    public static double sumProbTable(Table2 table) {
        double total = 0;
        for (ProbRow row : table.getTable()) {
            total += row.getProb();
        }
        return total;
    }

}
