package transaction;


public class ReconciliationTransaction {

    private CashPurchaseTransaction cashPurchaseTransaction;

    private AtmWithdrawalTransaction atmWithdrawalTransaction;

    private AtmWithdrawalTransaction parent;

    private double amount;


    public ReconciliationTransaction(CashPurchaseTransaction cashPurchase, AtmWithdrawalTransaction atmWithdrawal){

        // Reconcile the transactions
        setCashPurchaseTransaction(cashPurchase);
        setAtmWithdrawalTransaction(atmWithdrawal);
        reconcile();

    }

    public ReconciliationTransaction(CashPurchaseTransaction cashPurchase){

        // No ATM to reconcile with
        setCashPurchaseTransaction(cashPurchase);
        setAmount(cashPurchaseTransaction.getAmount());
        setParent(null);

    }

    private void reconcile() {

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

    @Override
    public String toString(){

        if(parent == null){
            return String.format("%d,%s,null,%.0f",
                cashPurchaseTransaction.getId(), cashPurchaseTransaction.getTransactionName(), amount);
        } else {
            return String.format("%d,%s,%d,%.0f",
                cashPurchaseTransaction.getId(), cashPurchaseTransaction.getTransactionName(), parent.getId(), amount);
        }

    }

    /** Getters and Setters **/
    public CashPurchaseTransaction getCashPurchaseTransaction() {
        return cashPurchaseTransaction;
    }

    private void setCashPurchaseTransaction(CashPurchaseTransaction cashPurchaseTransaction) {
        this.cashPurchaseTransaction = cashPurchaseTransaction;
    }

    public AtmWithdrawalTransaction getAtmWithdrawalTransaction() {
        return atmWithdrawalTransaction;
    }

    private void setAtmWithdrawalTransaction(AtmWithdrawalTransaction atmWithdrawalTransaction) {
        this.atmWithdrawalTransaction = atmWithdrawalTransaction;
    }

    public AtmWithdrawalTransaction getParent() {
        return parent;
    }

    private void setParent(AtmWithdrawalTransaction parent) {
        this.parent = parent;
    }

    public double getAmount() {
        return amount;
    }

    private void setAmount(double amount) {
        this.amount = amount;
    }
}