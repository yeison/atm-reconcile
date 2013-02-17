package transaction;

import java.sql.Date;

public abstract class ReconcilableTransaction implements Comparable<ReconcilableTransaction> {

    protected long id;

    protected String transactionName;

    protected Date date;

    protected TransactionType type;

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

    /** Getters and Setters **/

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
