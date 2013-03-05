atm-reconcile
=============
This program takes csv files containing Cash Purchase and ATM Withdrawal 
transaction records.  Then it attempts to reconcile the cash transactions 
with appropriate atm transactions.  The output is a list of reconciliation 
transactions with their corresponding ATM parents.

requirements
=============
Java 1.6  
maven 3.0

execution
=============
To run the program, first build the jar using the maven 'package' goal from the project's base directory:

```
mvn package
```

The executable jar will be generated into the base directory as atm-reconcile.jar.  The jar may then be run as usual:

```
java -jar atm-reconcile.jar <input-file1.csv> ...
```

The program will process the input data and place the output into './output'.  
  
A list of input files may be provided to the runnable jar.   The names of the output files will correspond with those of the input files.

input
=============
The input folder contains a number of input test files that may be used.
