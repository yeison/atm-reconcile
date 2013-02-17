package transaction;

import java.sql.Date;

public class AtmWithdrawalTransaction extends ReconcilableTransaction {

    public AtmWithdrawalTransaction(long id, String name, double amount, Date date){

        setId(id);
        setTransactionName(name);
        setAmount(amount);
        setDate(date);

        setType(TransactionType.ATM);

    }

}
