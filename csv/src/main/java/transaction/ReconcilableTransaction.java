package transaction;

import exception.MalformedInputException;

import java.io.IOException;
import java.sql.Date;

/**
 * Defines the fields and methods used to manage a transaction object for reconciliation.
 * Contains Comparable implementation to allow sorting of ReconcilableTransaction
 * based on date and transaction id.
 */
public abstract class ReconcilableTransaction implements Comparable<ReconcilableTransaction> {

    /** Transaction id **/
    protected long id;

    /** A short descriptive name **/
    protected String transactionName;

    /** Date the transaction took place **/
    protected Date date;

    /** Transaction type; CASH or ATM **/
    protected TransactionType type;

    /** Transaction amount in dollars **/
    protected double amount;

    @Override
    public int compareTo(ReconcilableTransaction that){

        if(this.equals(that)){
            return 0;
        }

        if(!this.date.equals(that.date)){
            return this.date.compareTo(that.date);
        } else {
            return  (this.id > that.id) ?  1: -1;
        }

    }

    public boolean equals(ReconcilableTransaction that) {

        if(this.date.equals(that.date) && this.id == that.id){
            return true;
        }

        return false;
    }

    /**
     * Create a ReconcilableTransaction object from an input line.
     *
     * @param line input with expected format: id,name,amount,date,is_cash,is_atm
     * @return a ReconcilableTransaction object generated from input-line data
     * @throws IOException
     */
    public static ReconcilableTransaction createTransactionsFromInput(String line) throws MalformedInputException {

        try{
            String[] values = line.split(",");

            // Remove white spaces from fields
            Long    id              = new Long(values[0]);
            String  name            = values[1].replaceAll("\\s", "");
            Double  amount          = new Double(values[2].replaceAll("\\s", ""));
            String  dateString      = values[3].replaceAll("\\s", "");
            boolean isCash          = Boolean.valueOf(values[4].replaceAll("\\s", ""));
            boolean isAtm           = Boolean.valueOf(values[5].replaceAll("\\s", ""));

            Date date = Date.valueOf(dateString);

            if(isAtm && !isCash){
                return new AtmWithdrawalTransaction(id, name, amount, date);

            } else if (isCash){
                return new CashPurchaseTransaction(id, name, amount, date);

            } else {
                throw new MalformedInputException(String.format("Input type is ambiguous (atm/cash): %s", line));
            }
        } catch (ArrayIndexOutOfBoundsException e){
            throw new MalformedInputException(String.format("Input has malformed records/lines: %s", line));
        }

    }

    /* Getters and Setters */

    public long getId() {
        return id;
    }

    protected void setId(long id) {
        this.id = id;
    }

    public String getTransactionName() {
        return transactionName;
    }

    protected void setTransactionName(String transactionName) {
        this.transactionName = transactionName;
    }

    public Date getDate() {
        return date;
    }

    protected void setDate(Date date) {
        this.date = date;
    }

    public TransactionType getType() {
        return type;
    }

    protected void setType(TransactionType type) {
        this.type = type;
    }

    public double getAmount() {
        return amount;
    }

    protected void setAmount(double amount) {
        this.amount = amount;
    }
}
