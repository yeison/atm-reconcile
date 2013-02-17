package execute;

import transaction.*;

import java.io.*;
import java.sql.Date;
import java.util.*;

public class ReconcileCsv {

    public static void main(String[] args){

        Stack<AtmWithdrawalTransaction> atmStack = new Stack();
        Stack<CashPurchaseTransaction> cashStack = new Stack();
        // LinkedList will perform better than ArrayList for our needs
        LinkedList<ReconcilableTransaction> transactions = new LinkedList<ReconcilableTransaction>();


        BufferedReader bufferedReader = null;
        try {
            File file = null;
            try {
                file = new File(args[0]);
            } catch (ArrayIndexOutOfBoundsException e){
                throw new FileNotFoundException("Please provide a filename as the first argument.");
            }


            bufferedReader = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();

        }

        String line;
        assert bufferedReader != null;

        try {

            // Skip the first line (header)
            bufferedReader.readLine();

            while ((line = bufferedReader.readLine()) != null) {

                transactions.add(parseLine(line));

            }

            bufferedReader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        // Sort based on ReconcilableTransaction comparable
        Collections.sort(transactions);

        // The older transactions should be at the bottom of the respective stacks
        for (ReconcilableTransaction transaction: transactions){

            if(transaction.getType() == TransactionType.ATM){

                atmStack.push((AtmWithdrawalTransaction) transaction);

            } else {
                // If this is not CASH type, something has gone wrong
                assert transaction.getType() == TransactionType.CASH;

                cashStack.push((CashPurchaseTransaction) transaction);

            }

        }


        LinkedList<ReconciliationTransaction> reconciliations = new LinkedList<ReconciliationTransaction>();

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
                System.out.println("Where Here");
                // We break if newest atm transaction is more recent than newest cash transaction
                break;
            }

        }

        while (!cashStack.isEmpty()){
            CashPurchaseTransaction cashTransaction  = cashStack.pop();
            ReconciliationTransaction reconciliation = new ReconciliationTransaction(cashTransaction);

            reconciliations.add(reconciliation);

        }


        for(ReconciliationTransaction reconciliation: reconciliations){
            System.out.println(reconciliation);
        }

    }

    private static ReconcilableTransaction parseLine(String line) throws IOException {
        // Expected File Format:
        // id, name,amount,date,is_cash,is_atm
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
            throw new IOException(String.format("Input type is ambiguous (atm/cash): %s", line));
        }

    }


}
