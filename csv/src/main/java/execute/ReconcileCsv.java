package execute;

import transaction.*;

import java.io.*;
import java.util.*;

/**
 * This is the entry point for the csv-based record loading.  It reads the
 * transaction records from a csv file; creates the appropriate ReconcilableTransaction
 * objects from the csv data.  Then it uses these objects to perform the reconciliations.
 */
public class ReconcileCsv {

    /**
     * Performs reconciliation on csv files containing properly-formatted transaction records.
     *
     * @param args a list of csv files to process
     */
    public static void main(String[] args){

        // LinkedList will perform better than ArrayList for our needs
        LinkedList<ReconcilableTransaction> transactions;

        for(String fileName : args){

            try {
                transactions = generateTransactionsFromInput(fileName);
            } catch (IOException e){
                // Alert of input-file error and continue to next file
                System.err.println(String.format("Unable to process file: %s", fileName));
                e.printStackTrace();
                continue;
            }

            // Perform reconciliation on the records
            LinkedList<ReconciliationTransaction> reconciliations
                        = ReconciliationTransaction.reconcileTransactionList(transactions);

            for(ReconciliationTransaction reconciliation: reconciliations){
                System.out.println(reconciliation);
            }

        }

    }


    /**
     *
     * @param fileName
     * @return
     * @throws IOException
     */
    private static LinkedList<ReconcilableTransaction> generateTransactionsFromInput(String fileName) throws IOException{

        LinkedList<ReconcilableTransaction> transactions = new LinkedList<ReconcilableTransaction>();

        File file = new File(fileName);
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));

        String line;
        assert bufferedReader != null;

        // Skip the first line (header)
        bufferedReader.readLine();

        // Generate ReconcilableTransaction objects from csv records
        while ((line = bufferedReader.readLine()) != null) {

            transactions.add(ReconcilableTransaction.createFromInput(line));

        }

        bufferedReader.close();

        return transactions;
    }


}
