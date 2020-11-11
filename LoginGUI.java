import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

class LoginGUI extends JFrame implements ActionListener{


	private JLabel heading , l1 , l2;
	private JTextField accountNo,pin;
	private JButton reset,submit;
	private Database db ; 
	private boolean isLoginSuccessful ;
	private Account currentUser ; 

	LoginGUI(){

		Container c=getContentPane();
		heading=new JLabel(" Login ");
		l1=new JLabel("Account Number:");
		l2=new JLabel("PIN:");
		accountNo=new JTextField(20);
		pin=new JPasswordField(20);
		submit=new JButton("Submit");
		reset=new JButton("Reset");
		c.setLayout(new FlowLayout(FlowLayout.CENTER));

		heading.setFont(new Font("courier new",Font.BOLD,30));

		// heading.setBounds(50, 50, 100, 50);
		// l1.setBounds(50, 100, 100, 50);
		// accountNo.setBounds(50, 150, 100, 50);
		// l2.setBounds(50, 200, 100, 50);
		// pin.setBounds(50, 250,  100, 50);
		// submit.setBounds(50, 350, 100, 50);
		// reset.setBounds(150, 350, 100, 50);
	
		c.add(heading);
		c.add(l1);
		c.add(accountNo);
		c.add(l2);
		c.add(pin);
		c.add(submit);
		c.add(reset);
		setSize(240,300);
		setVisible(true);
		submit.addActionListener(this);
		reset.addActionListener(this);
		db = new Database();
		isLoginSuccessful = false;
		currentUser = null;
	}

	public void actionPerformed(ActionEvent ae){

		
		String str=ae.getActionCommand();

		if(str.equals("Submit")){


			try{

				int temp1 =Integer.parseInt(accountNo.getText());
				int temp2 = Integer.parseInt(pin.getText());

				currentUser = db.getAccount(temp1 , temp2);

            	//verifying the PIN is done in Database.getAccount() method, if PIN or accountNo is incorrect null is returned . 

            	if(currentUser != null){
					JOptionPane.showMessageDialog(null,"\nAuthenticated Succesfully.\n");
                	isLoginSuccessful = true;
            	}else{
					JOptionPane.showMessageDialog(null , "\nIncorrect AccountNo or PIN or Account Doesn't Exist . Please Try Again.\n");
                	isLoginSuccessful = false;
            	}

			}catch(NumberFormatException e){
				JOptionPane.showMessageDialog(null,"\nPlease Enter Valid Credentials.\n");
				isLoginSuccessful = false;
			}

		}else if(str.equals("Reset")){

			accountNo.setText("");
			pin.setText("");
		}
	}

	public boolean getIsLoginSuccessful(){
		return isLoginSuccessful;
	}

	public Account getUserAccount(){
		return currentUser;
	}

}

