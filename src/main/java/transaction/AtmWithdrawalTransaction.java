package transaction;

import java.sql.Date;

/**
 * Holds data from an atm withdrawal record.  Manages the data during reconciliation.
 */
public class AtmWithdrawalTransaction extends ReconcilableTransaction {

    /**
     *
     * @param id transaction id number
     * @param name transaction name/description
     * @param amount dollar amount of the transaction
     * @param date date the transaction took place
     */
    public AtmWithdrawalTransaction(long id, String name, double amount, Date date){

        setId(id);
        setTransactionName(name);
        setAmount(amount);
        setDate(date);

        setType(TransactionType.ATM);

    }

}
