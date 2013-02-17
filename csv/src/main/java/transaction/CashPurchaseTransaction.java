package transaction;

import java.sql.Date;

public class CashPurchaseTransaction extends ReconcilableTransaction{

    public CashPurchaseTransaction(long id, String name, double amount, Date date){

        setId(id);
        setTransactionName(name);
        setAmount(amount);
        setDate(date);

        setType(TransactionType.CASH);

    }

}
