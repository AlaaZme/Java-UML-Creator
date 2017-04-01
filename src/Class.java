import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Class extends Element {

	public static final String CLASS_PATTERN = "\\s*(public|private)?\\s*(final|abstract)?\\s*class\\s+([\\w$_]*)\\s*(extends\\s*([\\w]*))?\\s*(implements\\s*([\\w,]*))?";
	public static final String EXTENDS = "-:>";
	public static final String IMPLEMENTS = "--:>";
	public static final String COMPOSITION = "o->";
	public static final String USES = "-->";

	private String modifier;
	private String type = "";
	private boolean isFinal;
	private boolean isAbstract;
	private String name;
	private String extend;
	private ArrayList<String> implement;
	private ArrayList<Element> variables;
	private ArrayList<Element> methods;
	private ArrayList<Element> interfaces;
	//private ArrayList<Element> classes;
	private String sign; // +,-,#    

	public Class(String modifier, boolean isFinal, boolean isAbstract, String name, String extend, String implement,
			ArrayList<Element> variables, ArrayList<Element> methods) {
		super();
		this.modifier = modifier;
		this.isFinal = isFinal;
		this.isAbstract = isAbstract;
		this.name = name;
		this.variables = variables;
		this.methods = methods;
		this.extend = extend;

		if (isAbstract) {
			type = "<abstract>";
		} else if (isFinal) {
			type = "<final>";

		}

		if (modifier == null) {
			modifier = "public";
		}

		switch (modifier) {

		case "public":
			sign = "+";
			break;

		case "private":
			sign = "-";
			break;
		default:
			sign = "#";
			break;

		}
		this.implement = parseImplements(implement);

	}

	private ArrayList<String> parseImplements(String text) {
		ArrayList<String> items = new ArrayList<>();
		if (text.length() > 0) {
			String[] seperated = text.split(",");
			for (int i = 0; i < seperated.length; i++) {
				items.add("[" + sign + name + "]" + IMPLEMENTS + "[" + seperated[i] + "]");
			}
		}

		return items;
	}

	private String parseComposition(ArrayList<String> types) {
		String result = "";
		ArrayList<String> temp = new ArrayList<>();
		for (Element element : variables) {
			Variable variable = (Variable) element;
			if (types.contains(variable.getType())) {
				// remove the +
				String str = "\n[" + sign + name + "]" + COMPOSITION + "[+" + variable.getType() + "]";
				if (!temp.contains(str)) {
					temp.add(str);
					result += str + "\n";
				}

			}

		}
		return result;

	}

/*	private String parseUses(ArrayList<Element> types)
		{
		
			return null;
		}*/
	public static Result extractClasses(StringBuilder text, ArrayList<String> objectsNames) {

		Result resultMethods = Method.extractMethods(text, objectsNames);
		//System.out.println(resultMethods.getStringBuilder());

		Result resultVars = Variable.extractVariabls(resultMethods.getStringBuilder(), objectsNames, false);
	//	System.out.println(resultVars.getStringBuilder());

		ArrayList<IndexMatch> indexes = new ArrayList<IndexMatch>();
		StringBuilder textReduced = new StringBuilder();
		String result = "";// the final result;
		ArrayList<Element> clase = new ArrayList<Element>();
		Pattern pattern = null;
		Matcher matcher;

		try {
			pattern = Pattern.compile(CLASS_PATTERN);

		} catch (Exception e)

		{
			System.out.println(e.getMessage());
		}

		// System.out.println(resultVars.getStringBuilder());

		matcher = pattern.matcher(resultVars.getStringBuilder());

		while (matcher.find()) {

			// modifier -gr1
			// final|abstract -gr2
			// name -gr3
			// extends class -gr5
			// implements interface -gr7
			// System.out.println(matcher.group(0));
			String modifier = matcher.group(1) != null ? matcher.group(1).trim() : null;
			String iFinal = matcher.group(2) != null ? matcher.group(2).trim() : null;
			String name = matcher.group(3) != null ? matcher.group(3).trim() : null;
			String extend = matcher.group(5) != null ? matcher.group(5).trim() : "";
			String implement = matcher.group(7) != null ? matcher.group(7).trim() : "";

			boolean isFinal = iFinal != null && iFinal.equals("final") ? true : false;
			boolean isAbstract = iFinal != null && iFinal.equals("abstract") ? true : false;

			indexes.add(new IndexMatch(matcher.start(), matcher.end()));

			Class class1 = new Class(modifier, isFinal, isAbstract, name, extend, implement, resultVars.getList(),
					resultMethods.getList());
			clase.add(class1);
			// System.out.println(class1.toString());
		}
		textReduced = RegexUml.reduceText(text, indexes);

		//
		return new Result(clase, textReduced);
	}

	public String toString() {
		String varsText = "";
		String methodsText = "";
		String fullClass = "";

		for (Element var : variables) {
			varsText += ((Variable) var).toString() + ";";

		}

		if (varsText.length() > 0) {
			varsText = varsText.substring(0, varsText.length() - 1);
			varsText += "|";
			// System.out.println(result);

		}
		String use ="";

		for (Element method : methods) {
			Method met=((Method) method);
			methodsText += met.toString() + ";";
			// check uses
			for(Element t : met.getVars())
			{
				
				Variable var = (Variable)t;
				if(RegexUml.types.contains((String)var.getType()))
				{
					 use += "["+sign + name+"]"+USES+"["+var.getSign()+var.getType()+"]\n";
				}
				//if(var.getType().equals(anObject))
				
			}
		}
		if (methodsText.length() > 0) {
			methodsText = methodsText.substring(0, methodsText.length() - 1);
		}

		String result = "[" + type + "" + sign + name + "|" + varsText + methodsText + "]";
		;
		if (!extend.equals("")) {
			// remove the + later
			result += "\n" + "[" + sign + name + "]" + EXTENDS + "[" + "+" + extend + "]";
		}
		for (String interfacee : implement) {
			result += "\n" + interfacee;

		}
		result += parseComposition(RegexUml.types);
		result += "\n";
		result += use;
		result += "\n";

		String url = null;
		try {
			url = URLEncoder.encode(result, "UTF-8");
			System.out.println(url);
		//	 JButton button = new JButton();
			//    button.setText("<HTML>Click the <FONT color=\"#000099\"><U>link</U></FONT>"
			//        + " to go to the Java website.</HTML>");
			// url = "http://www.nomnoml.com/#view/"+URLEncoder.encode(result,
			// "UTF-8");

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return url;
		// return result;

	}

	public String getModifier() {
		return type==modifier ? "":modifier;

	}

	public void setModifier(String modifier) {
		this.modifier = modifier;
	}

	public String getType() {
		return type==null ? "":type;

	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isFinal() {
		return isFinal;
	}

	public void setFinal(boolean isFinal) {
		this.isFinal = isFinal;
	}

	public boolean isAbstract() {
		return isAbstract;
	}

	public void setAbstract(boolean isAbstract) {
		this.isAbstract = isAbstract;
	}

	public String getName() {
		return name==null ? "":name;

	}

	public void setName(String name) {
		this.name = name;
		
	}

	public String getExtend() {
		return extend==null ? "":extend;

	}

	public void setExtend(String extend) {
		this.extend = extend;
	}

	public ArrayList<String> getImplement() {
		return implement;
	}

	public void setImplement(ArrayList<String> implement) {
		this.implement = implement;
	}

	public ArrayList<Element> getVariables() {
		return variables;
	}

	public void setVariables(ArrayList<Element> variables) {
		this.variables = variables;
	}

	public ArrayList<Element> getMethods() {
		return methods;
	}

	public void setMethods(ArrayList<Element> methods) {
		this.methods = methods;
	}

	public ArrayList<Element> getInterfaces() {
		return interfaces;
	}

	public void setInterfaces(ArrayList<Element> interfaces) {
		this.interfaces = interfaces;
	}

	public String getSign() {
		return sign==null ? "":sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

}
