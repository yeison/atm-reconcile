package transaction;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Stack;

/**
 * Maintains a reconciliation event record.  The record holds parent ATM transaction
 * if one exists, as well as the reconciliation amount. Once the reconciliation record
 * has been constructed, it's meant to be immutable (all setters are private).
 *
 */
public class ReconciliationTransaction {

    private CashPurchaseTransaction cashPurchaseTransaction;

    private AtmWithdrawalTransaction atmWithdrawalTransaction;

    private AtmWithdrawalTransaction parent;

    private double amount;


    /**
     * Construct a reconciliation event by providing a Cash transaction and an ATM transaction
     * that should be reconciled with one another.
     *
     * @param cashPurchase a cash purchase transaction to be reconciled.
     * @param atmWithdrawal an atm withdrawal transaction to be reconciled.
     */
    public ReconciliationTransaction(CashPurchaseTransaction cashPurchase, AtmWithdrawalTransaction atmWithdrawal){

        // Reconcile the transactions
        setCashPurchaseTransaction(cashPurchase);
        setAtmWithdrawalTransaction(atmWithdrawal);
        doReconciliationEvent();

    }

    /**
     * A reconciliation event with no ATM transaction available.
     * @param cashPurchase a cash purchase transaction with no corresponding atm transaction.
     */
    public ReconciliationTransaction(CashPurchaseTransaction cashPurchase){

        // No ATM to reconcile with
        setCashPurchaseTransaction(cashPurchase);
        setAmount(cashPurchaseTransaction.getAmount());
        setParent(null);

    }

    /**
     *  Reconcile the transactions that comprise this reconciliation object.
     */
    private void doReconciliationEvent() {

        double cashAmount = cashPurchaseTransaction.amount;
        double atmAmount = atmWithdrawalTransaction.amount;

        // Should never be 0 at this point, but assertions here can only help
        assert cashAmount != 0;
        assert atmAmount  != 0;

        if(cashAmount <= atmAmount){
            setParent(atmWithdrawalTransaction);
            setAmount(cashAmount);

            // Update remaining amounts on transactions
            atmWithdrawalTransaction.setAmount(atmAmount - cashAmount);
            cashPurchaseTransaction.setAmount(0);

        } else if (cashAmount > atmAmount){
            setParent(atmWithdrawalTransaction);
            setAmount(atmAmount);

            // Update remaining amounts on transactions
            atmWithdrawalTransaction.setAmount(0);
            cashPurchaseTransaction.setAmount(cashAmount - atmAmount);

        }

    }

    /**
     * Accordingly reconcile a list of Cash and ATM transactions with one another.
     * ReconciliationTransaction objects will be created from the reconciliation events, and returned
     * as a new list.
     *
     * This is where the magic happens.  The list passed in is first sorted, then split into two stacks: atm and cash.
     * The transactions at the top of the stacks are the most recent transactions respectively.  One transaction
     * is taken from each stack, and the two transactions are reconciled.  If the transaction has not been consumed,
     * it remains at the top of the stack.  Otherwise, if the transaction is consumed, the transaction is removed.
     *
     * @param transactions A mixed list of ATM and Cash ReconcilableTransactions to be reconciled.
     * @return A list of ReconciliationTransactions, which are essentially records of the reconciliation events.
     */
    public static LinkedList<ReconciliationTransaction> reconcileTransactionList(LinkedList<ReconcilableTransaction> transactions) {

        Stack<AtmWithdrawalTransaction> atmStack = new Stack();
        Stack<CashPurchaseTransaction> cashStack = new Stack();
        LinkedList<ReconciliationTransaction> reconciliations = new LinkedList<ReconciliationTransaction>();

        // Sort by ReconcilableTransaction.compareTo
        Collections.sort(transactions);

        // After sorting, the older transactions should be placed at the bottom of the respective stacks
        for (ReconcilableTransaction transaction: transactions){

            if(transaction.getType() == TransactionType.ATM){

                atmStack.push((AtmWithdrawalTransaction) transaction);

            } else {
                // If this is not CASH type, something has gone wrong
                assert transaction.getType() == TransactionType.CASH;

                cashStack.push((CashPurchaseTransaction) transaction);

            }

        }

        while (!atmStack.isEmpty() && !cashStack.isEmpty()){

            ReconciliationTransaction reconciliation = null;

            // Retrieve the most recent cash transaction
            CashPurchaseTransaction cashTransaction  = cashStack.peek();
            // Retrieve the most recent atm transaction
            AtmWithdrawalTransaction atmTransaction  = atmStack.peek();

            // Only reconcile if most recent cash transaction is more recent than the most recent atm transaction
            if(cashTransaction.compareTo(atmTransaction) > 0 ){

                // Create reconciliation object and perform reconciliation processing
                reconciliation =  new ReconciliationTransaction(cashTransaction, atmTransaction);

                // Update with post-reconciliation transaction amounts
                cashTransaction = reconciliation.getCashPurchaseTransaction();
                atmTransaction  = reconciliation.getAtmWithdrawalTransaction();

                // This is where the magic happens
                // Pop consumed transactions from the top of their respective stacks
                // NOTE: If this application were multi-threaded, these stacks would have to be thread-safe
                if(cashTransaction.getAmount() == 0){
                    cashStack.pop();
                }
                if(atmTransaction.getAmount() == 0){
                    atmStack.pop();
                }

                reconciliations.add(reconciliation);

            } else {
                // We pop the atm if newest atm transaction is more recent than newest cash transaction
                // If we were receiving Real-Time data, the implementation here would be more complex
                atmStack.pop();
                continue;
            }

        }

        // If any cash transactions remain, create reconciliation record with no parent
        while (!cashStack.isEmpty()){
            CashPurchaseTransaction cashTransaction  = cashStack.pop();
            ReconciliationTransaction reconciliation = new ReconciliationTransaction(cashTransaction);

            reconciliations.add(reconciliation);

        }

        return reconciliations;

    }

    @Override
    public String toString(){

        if(parent == null){
            return String.format("%d, %s, null, %.0f",
                cashPurchaseTransaction.getId(), cashPurchaseTransaction.getTransactionName(), amount);
        } else {
            return String.format("%d, %s, %d, %.0f",
                cashPurchaseTransaction.getId(), cashPurchaseTransaction.getTransactionName(), parent.getId(), amount);
        }

    }

    /* Getters and Setters */

    /**
     * Retrieve the cash transaction from this reconciliation event.
     */
    public CashPurchaseTransaction getCashPurchaseTransaction() {
        return cashPurchaseTransaction;
    }

    private void setCashPurchaseTransaction(CashPurchaseTransaction cashPurchaseTransaction) {
        this.cashPurchaseTransaction = cashPurchaseTransaction;
    }

    /**
     * Retrieve the atm transaction from this reconciliation event.
     */
    public AtmWithdrawalTransaction getAtmWithdrawalTransaction() {
        return atmWithdrawalTransaction;
    }

    private void setAtmWithdrawalTransaction(AtmWithdrawalTransaction atmWithdrawalTransaction) {
        this.atmWithdrawalTransaction = atmWithdrawalTransaction;
    }

    /**
     * If this reconciliation event has a parent ATM transaction, retrieve this parent.
     */
    public AtmWithdrawalTransaction getParent() {
        return parent;
    }

    private void setParent(AtmWithdrawalTransaction parent) {
        this.parent = parent;
    }

    /**
     * Retrieve the dollar amount of this reconciliation event.
     */
    public double getAmount() {
        return amount;
    }

    private void setAmount(double amount) {
        this.amount = amount;
    }
}
