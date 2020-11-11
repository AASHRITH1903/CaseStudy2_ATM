import java.io.Serializable;
import java.util.LinkedList;
import java.util.Queue;

public class Account implements Serializable{

    private Queue<Transaction> history;
    private String ifsc;
    private int accountNo;
    private int pin;
    private double balance;

    Account(int accountNo ,int pin , String ifsc){
        this.accountNo = accountNo;
        this.pin = pin;
        this.ifsc = ifsc;
        balance = 0.0;
        history = new LinkedList<Transaction>();
    }

    public int getAccountNo(){
        return accountNo;
    }

    public String getifsc(){
        return ifsc;
    }

    public int getPin(){
        return pin;
    }
    public void setPin(int pin){
        this.pin = pin;
    }

    

    public double getBalance(){
        return balance;
    } 
    public void setBalance(double bal){
        balance = bal;
    }

    public Queue<Transaction> getHistory(){
        return history;
    }
    public void addToHistory(Transaction t){
        if(history.size() < 3){
            history.add(t);
        }else{
            history.poll();
            history.add(t);
        }
    }

    
}
