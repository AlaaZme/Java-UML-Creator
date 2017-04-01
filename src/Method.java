import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Method extends Element

{
	public static String methodPattren="";
	private String modifier; // private , public , protected
	private String name;    // the method name
	private String sign; // +,-,#
	private String returnType;    // the method type
	private ArrayList<Element> vars; // the variables in the method ( )
	private String args;
	private boolean isCot;  // if its constructor
	private boolean isStatic; // if its static
	private boolean isAbstract ; // if its Abstract
	


	public Method(String modifier, String returnType, String name, String args, boolean isStatic, boolean isCot,boolean isAbstract) 
	{
		super();
		
		this.modifier = modifier==null?"":"public";
		this.name = name;
		this.args =args.replace(",", ";");
		this.returnType = returnType;
		if(!returnType.equals(""))
		{
			returnType = returnType.replace("[]", "\\[\\]");
			returnType = ":"+returnType;
		}

		this.isCot = isCot;
		this.isStatic = isStatic;
		this.isAbstract = isAbstract;
		
		
		if(modifier==null)
		{
			modifier="public";
		}
		
		switch(modifier)
		{
		
		case "public":
			sign="+";
			break;
			
		case "private":
			sign="-";
			break;
			default:
				sign="#";
				break;
		
		}
		
		
		ArrayList<String> v = new ArrayList<String>();
		v.add("Method");
		v.addAll(RegexUml.types);
		
		vars = (Variable.extractVariabls(new StringBuilder(this.args), v,true)).getList();
		args="";
		for(Element var : vars)
		{
			
			args+=var.toString()+",";
		}
		if(args.length()>0)
		{
			args = args.substring(0, args.length()-1);

		}
		this.args=args;

		
		
		
	}
	
	
	
	public static Result extractMethods(StringBuilder text,ArrayList<String> objectsNames)
	{

		
		ArrayList<IndexMatch> indexes = new ArrayList<IndexMatch>();
		StringBuilder textReduced = new StringBuilder();
		String result="";// the final result;
		
		
		String objs="";
		for(String obj : objectsNames)
		{
		objs+="|"+obj;	
		}
		ArrayList<Element> methods = new ArrayList<Element>();
		

			 methodPattren= "((public|private|protected|abstract)\\s+)?(static\\s+)?((\\w|<|>)*\\s+)([\\w_$]+)\\s*\\(([^(-)]*)\\)\\s*\\{(\\s*[^{}]*)*\\}";
			


		 Pattern pattern = null ;
		 Matcher matcher;
		 
		 try
		 {
			 pattern= Pattern.compile(methodPattren);

		 }
		 catch(Exception e)
		 
		 {
			// System.out.println(e.getMessage());
		 }
				 
			matcher = pattern.matcher(text);
			
			while(matcher.find())
			{
				// modifier -gr2-1
				// static   -gr3
				// type     -gr4
				// name     -gr6
				//args      -gr7
				//System.out.println(matcher.group(0));
				String modifier = matcher.group(2)!=null ? matcher.group(2).trim():null ;
				String statics =  matcher.group(3)!=null ? matcher.group(3).trim():null ;
				String type =  matcher.group(4)!=null ? matcher.group(4).trim():"" ;
				String name =  matcher.group(6)!=null ? matcher.group(6).trim():null ;
				String args= matcher.group(7)==null ? "":matcher.group(7).trim();
				
				boolean isCot =   statics==null && modifier == null   ? true : false;
				if(isCot)
				{
					modifier = type;
					type="";
				}
				boolean isStatic =   statics==null ? false : true;
				boolean isAbstract =   modifier!=null && modifier.equals("abstract") ? true : false;
				indexes.add(new IndexMatch(matcher.start(), matcher.end()));

				 Method method = new Method(modifier, type, name,args,isStatic,isCot,isAbstract);
		
				
				 methods.add(method);
				result+=(method.toString()+";");
				 

			}
			
			if(result.length()>0)
			{
				result = result.substring(0, result.length()-1);

			}
			textReduced =	RegexUml.reduceText(text, indexes);

		//	 System.out.println(result);
			 
//			 int start=0,end=0;
//			 for(IndexMatch indexMatch : indexes)
//			 {
//				 end= indexMatch.getStartIndex();
//				 textReduced.append(text.substring(start, end));
//				 start=indexMatch.getEndIndex();
//			 }
			 //System.out.println(textReduced);
			 
		return new Result(methods, textReduced);
		
	}

	@Override
	public String toString() 
	{
		return sign+name+"("+args+")"+returnType;
	}



	public String getModifier() {
		return modifier;
	}



	public void setModifier(String modifier) {
		this.modifier = modifier;
	}



	public String getName() {
		return name;
	}



	public void setName(String name) {
		this.name = name;
	}


	public String getSign() {
		return sign;
	}



	public void setSign(String sign) {
		this.sign = sign;
	}



	public String getReturnType() {
		return returnType;
	}



	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}



	public ArrayList<Element> getVars() {
		return vars;
	}



	public void setVars(ArrayList<Element> vars) {
		this.vars = vars;
	}



	public String getArgs() {
		return args;
	}



	public void setArgs(String args) {
		this.args = args;
	}



	public boolean isCot() {
		return isCot;
	}



	public void setCot(boolean isCot) {
		this.isCot = isCot;
	}



	public boolean isStatic() {
		return isStatic;
	}



	public void setStatic(boolean isStatic) {
		this.isStatic = isStatic;
	}



	public boolean isAbstract() {
		return isAbstract;
	}



	public void setAbstract(boolean isAbstract) {
		this.isAbstract = isAbstract;
	}
	
	
	
	


	
}
