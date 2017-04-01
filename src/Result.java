import java.awt.List;
import java.util.ArrayList;


public class Result {

	private ArrayList<Element> list;
	private StringBuilder stringBuilder;
	public Result(ArrayList<Element> list, StringBuilder stringBuilder) {
		super();
		this.list = list;
		this.stringBuilder = stringBuilder;
	}
	public ArrayList<Element> getList() {
		return list;
	}
	public StringBuilder getStringBuilder() {
		return stringBuilder;
	}
	public void setList(ArrayList<Element> list) {
		this.list = list;
	}
	public void setStringBuilder(StringBuilder stringBuilder) {
		this.stringBuilder = stringBuilder;
	}

	
}
