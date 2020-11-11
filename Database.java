import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.System;
import java.sql.*;


// Driver path   -------->   -classpath ".;JDBC/sqlite-jdbc-3.7.2.jar"
//Driver path ----> For Non - Windows  ".:JDBC/sqlite-jdbc-3.7.2.jar"
public class Database{
    
    private String db_name = "atm.db";
    private String url = "jdbc:sqlite:db/"+db_name; // url of the database


    private Connection connect() {
        Connection conn = null;
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection(url);
        } catch (Exception e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return conn;
    }

     public int count_tables(){
         int n = 0;
         try(   
                Connection c = this.connect();
                ResultSet rs = c.getMetaData().getTables(null, null, null, new String[]{"TABLE"});
         ){
            
             while(rs.next()){
                    n++;
             }
            }catch(Exception e){
                System.err.println( e.getClass().getName() + " : " + e.getMessage() );
            }
            return n;
     }

     public void setupDatabase(){

         deleteAllTables();
         
         try(Connection c = this.connect();
            Statement s = c.createStatement();
         ){

             s.executeUpdate("create table if not exists accounts (accountNo int not null , securedPin text not null , salt text not null , account blob not null)");
            
         }catch(Exception e){
            System.out.println(e.getClass().getName() +" : "+e.getMessage());
         }
     }

     public void deleteAllTables(){	     
      try(Connection c = this.connect();	
            Statement s = c.createStatement();
      ){	
         s.executeUpdate("drop table if exists 'accounts' ");	         
         	
      }catch(Exception e){	
         System.out.println(e.getClass().getName() +" : "+e.getMessage());	
      }	        
    }
    
    
    public void addAccount(int accountNo , int pin , Account acc) throws IOException {

        byte[] data = this.toBytes(acc);
        //Encrypting the PIN before storing it in Database , here "securedPin" is Encrypted PIN .
        String salt = Encryption.getSalt(30);
        String securedPin = Encryption.generateSecurePassword(String.valueOf(pin), salt);

        try(Connection c = this.connect();
            PreparedStatement ps = c.prepareStatement("insert into accounts values (?,?,?,?)");  
        ){

            ps.setInt(1, accountNo);
            ps.setString(2, securedPin);
            ps.setString(3, salt);
            ps.setBytes(4, data);
            ps.executeUpdate();
 
        }catch(Exception e){
          System.out.println(e.getClass().getName() +" : "+e.getMessage());
       }

    }


    public Account getAccount(int accountNo , int pin){

        try(Connection c = this.connect();
            PreparedStatement ps = c.prepareStatement("select * from accounts where accountNo = ?");  
        ){

            ps.setInt(1, accountNo);
            
            ResultSet rs = ps.executeQuery();

            while(rs.next()){

                String securedPin = rs.getString("securedPin");
                String salt = rs.getString("salt");

                //verfying the PIN using Encryption.verifyUserPassword() method . 
                
                boolean isPinCorrect = Encryption.verifyUserPassword(String.valueOf(pin), securedPin, salt);
                if(isPinCorrect){
                    byte[] data = rs.getBytes("account");;
                    Account acc = this.toAccountObject(data);
                    return acc;
                }
            }
            return null;
        }catch(Exception e){
          System.out.println(e.getClass().getName() +" : "+e.getMessage());
          return null;
       }

    }

    //" Overloaded Method for Transferring Money "

    public Account getAccount(int accountNo ){

        try(Connection c = this.connect();
            PreparedStatement ps = c.prepareStatement("select * from accounts where accountNo = ?");  
        ){

            ps.setInt(1, accountNo);
            
            ResultSet rs = ps.executeQuery();

            while(rs.next()){
                byte[] data = rs.getBytes("account");;
                Account acc = this.toAccountObject(data);
                return acc;
            }
            return null;
        }catch(Exception e){
          System.out.println(e.getClass().getName() +" : "+e.getMessage());
          return null;
       }

    }

    public void updateAccount(Account acc) throws IOException{

        int accountNo = acc.getAccountNo();
        byte[] data = this.toBytes(acc);

        try(Connection c = this.connect();
            PreparedStatement ps = c.prepareStatement("update accounts set account = ?  where accountNo = ?");  
        ){

            ps.setBytes(1, data);
            ps.setInt(2, accountNo);
            
            ps.executeUpdate();

        }catch(Exception e){
          System.out.println(e.getClass().getName() +" : "+e.getMessage());
       }

    }

    // TO convert "Account" Object to "byte Stream" to store as blob in Database

    public byte[] toBytes(Account a) throws IOException{
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(a);
        return bos.toByteArray();

    }

    //To Convert Back to "Account" Object from Byte array

    public Account toAccountObject(byte[] data) throws IOException , ClassNotFoundException{
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        ObjectInputStream ois = new ObjectInputStream(bis);
        return (Account)ois.readObject();

    }


  


        
}