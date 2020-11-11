import java.io.IOException;
import java.util.Queue;
import java.util.Scanner;

import javax.swing.JOptionPane;

public class Atm {

    private Scanner sc = new Scanner(System.in);

    private double cashAvailable;
    private double withDrawLimit;

    private Account currentUser;

    private Database db;

    Atm()throws IOException{
        cashAvailable = 100000;
        withDrawLimit = 10000;
        db = new Database();
        // db.setupDatabase();
        // Account acc = new Account(12345 , 12354 , "IFSC1");
        // acc.setBalance(2000);
        // db.addAccount(12345, 12354, acc);
        // Account acc2 = new Account(55555 , 66666 , "IFSC2");
        // acc2.setBalance(9000);
        // db.addAccount(55555, 66666, acc2);
    }

    public void run() throws IOException{
        try{
            System.out.println("****** WELCOME *******");
            System.out.println("1.Login \n2.Switch off");
            int choice = Integer.parseInt(sc.nextLine());
            switch (choice) {
                case 1:
                    boolean successful = authenticate();
                    if (successful) {
                        serveUser();
                        run();
                    } else {
                        run();
                    }
                break;
                case 2:
                    // terminates the program here
                break;
                default:
                    System.out.println("\nInvalid Option. Please Try again .\n");
                    run();
                break;
            }
            
        }catch(NumberFormatException e){
            System.out.println("\nEnter a valid choice . Please Try again .\n");
            run();
        }
    }

    private boolean authenticate() {


        LoginGUI l=new LoginGUI();

        System.out.println("\nPress Enter to continue... ");
        String submit = sc.nextLine();

        if(submit.equals("")){

            if(l.getIsLoginSuccessful()){
                this.currentUser = l.getUserAccount();
                return true;
            }else{
                return false;
            }

        }else{
            JOptionPane.showMessageDialog(null, "Login cancelled by the user . ");
            return false;

        }

    

        
        
    }



    private void serveUser() throws IOException {

        try{

            System.out.println("1.WithDraw \n2.Deposit \n3.View Balance \n4.Transfer Money to Another Account \n5.Show mini-statement \n6.logout");
            int choice = Integer.parseInt(sc.nextLine());
            switch (choice) {
                case 1:
                    makeWithDraw();
                    serveUser();
                    break;
                case 2:
                    makeDeposit();
                    serveUser();
                    break;
                case 3:
                    showBalance();
                    serveUser();
                    break;
                case 4:
                    transferMoney();
                    serveUser();
                    break;
                case 5:
                    showMiniStatement();
                    serveUser();
                    break;
                case 6:
                    logOut();
                    break;
                
                default:
                    System.out.println("\nInvalid option . Please Try again .\n");
                    serveUser();
                    break;
            }
        }catch(NumberFormatException e){
            System.out.println("\nEnter a valid choice . Please Try again .\n");
            serveUser();
        }
    }

    private void showBalance() {
        JOptionPane.showMessageDialog(null,"\nYour Account Balance is : Rs " + currentUser.getBalance()+"\n");
        System.out.println();
    }

    private void makeDeposit() {

        try{
            System.out.println("\nEnter the amount that you want to Deposit : ");
            double deposit = Double.parseDouble(sc.nextLine());
            currentUser.setBalance(currentUser.getBalance() + deposit);
            cashAvailable += deposit;
            System.out.printf("\nMessage : Rs %f has been Deposited into your Account \"%d\" \n\n", deposit, currentUser.getAccountNo());
            Transaction t = new Transaction("Deposited into your Account .", deposit);
            currentUser.addToHistory(t);
        }catch(NumberFormatException e){
            System.out.println("\nEnter Amount in numbers. Please Try again .\n");
        }
    }

    private void makeWithDraw() {

        try{
            System.out.println("\nEnter the Amount to WithDarw : ");
            double wd = Double.parseDouble(sc.nextLine());

            if (wd > withDrawLimit) {
                System.out.println("\nWithDraw Failed. Daily Limit Exceeded.\n");
            } else if (wd > cashAvailable) {
                System.out.println("\nWithDraw Failed. Available Cash Limit Exceeded.\n");
            } else if (wd > currentUser.getBalance()) {
                System.out.println("\nWithDraw Failed. Account Balance Exceeded.\n");
            } else {
                System.out.println("\nWithDraw Successful.\n");
                currentUser.setBalance(currentUser.getBalance() - wd);
                cashAvailable -= wd;
                System.out.println("\nCollect your Cash.\n");
                Transaction t = new Transaction("Withdrawn from your Account . ", -wd);
                currentUser.addToHistory(t);
            }
        }catch(NumberFormatException e){
            System.out.println("\nEnter Amount in numbers. Please Try again .\n");
        }
    }

    private void transferMoney()throws IOException{

        System.out.println("\nAccount number of the Target Account : ");
        int targetAccountNo = 0;

        try{
            targetAccountNo = Integer.parseInt(sc.nextLine());
        }catch(NumberFormatException e){
            System.out.println("\nEnter a valid Account number .Transcaction cancelled . Please Try again .\n");
            return;
        }

        Account targetAccount = db.getAccount(targetAccountNo);
        if(targetAccount != null && targetAccountNo != currentUser.getAccountNo()){
            System.out.println("\nEnter IFSC code : ");
            String ifsc = sc.nextLine();
            if(targetAccount.getifsc().equals(ifsc)){
                    System.out.println("\nEnter Amount to Transfer : ");
                    double amount = 0;
                    try{
                        amount = Double.parseDouble(sc.nextLine());
                    }catch(NumberFormatException e){
                        System.out.println("\nEnter a valid amount in numbers . Transaction cancelled . Please Try again .\n");
                        return;
                    } 
                    if(currentUser.getBalance() >= amount){
                        currentUser.setBalance(currentUser.getBalance() - amount);
                        targetAccount.setBalance(targetAccount.getBalance() + amount);
                        System.out.printf("\nTransferrd money Rs.%f from \"%d\" to \"%d\" \n\n" , amount , currentUser.getAccountNo() , targetAccount.getAccountNo());
                        Transaction t1 = new Transaction("Transferred to Account number \""+targetAccount.getAccountNo()+"\"", -amount);
                        Transaction t2 = new Transaction("Transferred from Account number \""+currentUser.getAccountNo()+"\"", amount);
                        currentUser.addToHistory(t1);
                        targetAccount.addToHistory(t2);
                        db.updateAccount(targetAccount);

                    }else{
                        System.out.println("\nEntered Amount is Greater than your Account Balance . Transaction Cancelled . Please Try Again .\n");
                    }
                    
            }else{
                System.out.println("\nIncorrect IFSC code .Transaction cancelled . Please Try again .\n");
                return;
            }
        }else{
            System.out.println("\nIncorrect AccountNo or Account Doesn't Exist .Transaction cancelled . Please Try Again.\n");
            return;
        }

    
    }

    //To show Last "3 Transactions"  
    private void showMiniStatement(){
        Queue<Transaction> history = currentUser.getHistory();

        String miniStatement = "";
        miniStatement += "\nMini Statement (Last 3 Transactions) :  \n";
        
        for(Transaction t : history){
            miniStatement += "\n"+t.getChange() + "  " + t.getMode() + "\n";
        }

        JOptionPane.showMessageDialog(null, miniStatement);

        System.out.println(); //Just to add space 
        
    }

    private void logOut() throws IOException {

        db.updateAccount(currentUser);
        currentUser = null;
        JOptionPane.showMessageDialog(null, "\nLogged Out .\n");
        System.out.println();

    }

}