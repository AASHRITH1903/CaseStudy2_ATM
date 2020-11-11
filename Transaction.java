import java.io.Serializable;

public class Transaction implements Serializable{

    private String mode ;
    private double change;

    Transaction(String mode ,double change){
        //mode is (deposit or Withdraw or Transfer "from or to")
        this.mode = mode;
        //Change can be negative also depending upon balance increase or decrease
        this.change = change;
    }

    public String getMode(){
        return mode;
    }
    public double getChange(){
        return change;
    }
    
}
