package execute;

import exception.MalformedInputException;
import transaction.*;

import java.io.*;
import java.util.*;

/**
 * This is the entry point for the csv-based processing.  It reads the
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

        // Provide some help/usage information
        if(args.length == 0 || args[0].equals("-h")){
            printHelpMessage();
        }

        // LinkedList will perform better than ArrayList for our needs
        LinkedList<ReconcilableTransaction> transactions = null;

        for(String fileName : args){

            try {
                transactions = generateTransactionsFromFile(fileName);

                // Perform reconciliation on the records
                LinkedList<ReconciliationTransaction> reconciliations
                            = ReconciliationTransaction.reconcileTransactionList(transactions);

                // Output to csv file
                outputToCsv(reconciliations, fileName);

            } catch (IOException e){
                // Alert of input-file error and continue to next file
                System.err.println(String.format("\nError: Unable to process file: %s", fileName));
                e.printStackTrace();
                continue;
            }

        }

    }

    /**
     * Given a file name, this generates the appropriate CASH/ATM transaction objects from the
     * records contained in that file.
     *
     * @param fileName the name of a file containing transaction records of the correct format.
     * @return a list of the records loaded into transaction objects.
     * @throws IOException
     */
    private static LinkedList<ReconcilableTransaction> generateTransactionsFromFile(String fileName)
            throws IOException {

        LinkedList<ReconcilableTransaction> transactions = new LinkedList<ReconcilableTransaction>();
        BufferedReader bufferedReader = null;

        // Try here just so we can add the finally and close the reader
        try {

            File file = new File(fileName);
            bufferedReader = new BufferedReader(new FileReader(file));

            String line;
            assert bufferedReader != null;

            // Skip the first line (header)
            bufferedReader.readLine();

            // Generate ReconcilableTransaction objects from csv records
            while ((line = bufferedReader.readLine()) != null) {
                transactions.add(ReconcilableTransaction.createTransactionsFromInput(line));
            }

        } finally {
            bufferedReader.close();
        }

        return transactions;
    }

    private static void outputToCsv(LinkedList<ReconciliationTransaction> reconciliations, String fileName)
            throws IOException {

        String outputDirectory = "output";
        // Get filename without path
        File file = new File(fileName);
        fileName = file.getName();

        BufferedWriter bufferedWriter = null;
        try {

            new File(outputDirectory).mkdir();

            FileWriter fileWriter = new FileWriter(outputDirectory + fileName.replaceAll(".csv", "") + ".reconciled.csv");
            bufferedWriter = new BufferedWriter(fileWriter);

            bufferedWriter.write("id, name, parent, amount\n");

            for(ReconciliationTransaction reconciliation: reconciliations){
                bufferedWriter.write(reconciliation.toString() + "\n");
            }

            bufferedWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            bufferedWriter.close();
        }

    }

    private static void printHelpMessage(){
        System.out.println("" +
                "\nThis program takes csv files containing Cash Purchase and ATM Withdrawal " +
                "\ntransaction records.  Then it attempts to reconcile the cash transactions " +
                "\nwith appropriate atm transactions.  The output is a list of reconciliation " +
                "\ntransactions with their corresponding ATM parents." +
                "\n" +
                "\nProvide the names of properly formatted csv files as arguments.  " +
                "\nOutput will be generated and placed in ./output" +
                "\n\n\tUsage: " +
                "\n\t\tjava -jar atm-reconcile.jar <input-file1.csv> ...\n");
        System.exit(0);
    }


}
