import java.awt.Graphics;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class ProgPanel extends JPanel implements ActionListener {
	private JLabel lblChooseFile;
	private JButton btnChooseFile,btnProcess;
	private JTextField txtResult;
	public ProgPanel()
	{
		setLayout(new GridLayout(4,1,2,2));

		btnChooseFile = new JButton("Choose File");
		btnProcess= new JButton("Process Files");
		lblChooseFile = new JLabel("Path:");
		txtResult = new JTextField();
		this.add(btnChooseFile);
		this.add(lblChooseFile);
		this.add(btnProcess);
		this.add(txtResult);
		btnChooseFile.addActionListener(this);
		btnProcess.addActionListener(this);
		lblChooseFile.setText("");



		
	}
	protected void paintComponent(Graphics g) // method that sets the background color and handles draw on the screen
	{
		super.paintComponent(g);
	}
	
	public void actionPerformed(ActionEvent arg0) 
	{
		if(arg0.getSource()==btnChooseFile)
		{
			JFileChooser j = new JFileChooser();
			j.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int opt = j.showOpenDialog(this);
			if(JFileChooser.OPEN_DIALOG==opt)
			{
				lblChooseFile.setText(j.getSelectedFile()+"");
				JOptionPane.showMessageDialog(null,"Link copied to clipboard");
				

			}

		}
		else
		{
			if(!lblChooseFile.getText().equals(""))
			{
				txtResult.setText(RegexUml.readInputFiles(lblChooseFile.getText())); 

			}
			else
			{
				JOptionPane.showMessageDialog(null,	"Choose a Directory");

			}
		}
	}
	

	
}
