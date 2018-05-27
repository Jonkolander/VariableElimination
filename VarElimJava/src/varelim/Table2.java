package varelim;

import java.util.ArrayList;

/**
 * @author Dennis den Hollander s4776658
 * @author Tom Kamp s4760921
 */
public class Table2 {

    private final Table TABLE;
    private ArrayList<Variable> headers = new ArrayList();

    public Table2(Table table) {
        this.TABLE = table;
        this.headers = table.getHeaders();
    }

    public Table getTableProp() {
        return this.TABLE;
    }

    public ArrayList<Variable> getHeaders() {
        return this.headers;
    }

    public ArrayList<ProbRow> getTable() {
        return this.TABLE.getTable();
    }

    public Table2 multiply(Table2 other) {
        ArrayList<ProbRow> newRows = new ArrayList();
        for (ProbRow row1 : this.getTable()) {
            for (ProbRow row2 : other.getTable()) {
                for (Variable thisHeader : this.headers) {
                    for (Variable otherHeader : other.headers) {
                        if (thisHeader.equals(otherHeader)) {
                            String thisVal = row1.getValues().get(this.headers.indexOf(thisHeader));
                            String otherVal = row2.getValues().get(other.headers.indexOf(otherHeader));
                            if (thisVal.equals(otherVal)) {
                                newRows.add(row1.multiply(row2, this.headers, other.headers));
                            }
                        }
                    }
                }
            }
        }
        ArrayList<Variable> newHeaders = newRows.get(0).getHeaders();
        return new Table2(new Table(newRows, newHeaders));
    }

    public Table2 eliminate(Variable variable) {
        ArrayList<ProbRow> newRows = new ArrayList();
        ArrayList<Variable> newHeaders = (ArrayList<Variable>) this.headers.clone();
        for (Variable header : this.headers) {
            if (header.equals(variable)) {
                int column = this.getHeaders().indexOf(header);
                newHeaders.remove(column);
                for (ProbRow row1 : this.getTable()) {
                    row1.getValues().remove(column);
                    newRows.add(row1);
                }
            }
        }
        return new Table2(new Table(newRows, newHeaders));
    }

    public Table2 merge() {
        ArrayList<ProbRow> tempVal = new ArrayList();
        ArrayList<ProbRow> newRows = new ArrayList();
        for (ProbRow row1 : this.getTable()) {
            for (ProbRow row2 : this.getTable()) {
                if (row1.getValues().equals(row2.getValues()) && !tempVal.contains(row1) && row1 != row2) {
                    tempVal.add(row1);
                    tempVal.add(row2);
                    newRows.add(row1.addRow(row2));
                }
            }
        }
        if (newRows.size() < 1) {
            return this;
        }
        return new Table2(new Table(newRows, this.headers));
    }
}
