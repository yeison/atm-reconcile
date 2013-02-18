package transaction;

import java.sql.Date;

/**
 * Holds data and manages a Cash Purchase Transaction.
 */
public class CashPurchaseTransaction extends ReconcilableTransaction{

    /**
     *
     * @param id transaction id number
     * @param name transaction name/description
     * @param amount dollar amount of the transaction
     * @param date date the transaction took place
     */
    public CashPurchaseTransaction(long id, String name, double amount, Date date){

        setId(id);
        setTransactionName(name);
        setAmount(amount);
        setDate(date);

        setType(TransactionType.CASH);

    }

}
