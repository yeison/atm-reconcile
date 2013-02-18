atm-reconcile
=============
This program takes csv files containing Cash Purchase and ATM Withdrawal 
transaction records.  Then it attempts to reconcile the cash transactions 
with appropriate atm transactions.  The output is a list of reconciliation 
transactions with their corresponding ATM parents.

execution
=============
To run the program, first build the jar using the maven 'package' goal:

`mvn package`
