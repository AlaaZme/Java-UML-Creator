
import javax.swing.JFrame;


 public class Core {
	// (\/\*[\s\S]*\*\/|\/\/[\S ]*)  for comments

	// inherits B-:>A
	// interface B--:>Interface
	// composition  o->

	static String str;
	public static void main(String[] args) 
		{
		JFrame j = new JFrame("Core");
		j.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		j.setSize(400, 400);
		ProgPanel pnl = new ProgPanel();
		j.add(pnl);
		j.setVisible(true);


		


	}

}
