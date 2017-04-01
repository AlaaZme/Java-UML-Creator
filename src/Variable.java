import java.io.UnsupportedEncodingException;
import java.lang.reflect.Modifier;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Variable extends Element
{
	public static String variablePattren;
	public static final String NAME_PATTERN="([a-zA-Z0-9$_]+)\\s*([\\[\\s\\d\\w\\]]*)?(\\s*=\\s*[^,;]*)?";
	private String modifier; // private , public , protected , object
	private String sign; // +,-,#
	private String type; // the type of the var
	private String name;    // the var name
	private boolean isStatic; // if its static
	private boolean isFinal ; // if its Abstract

	
	public Variable(String modifier, String type, String name)
	{

		this.modifier = modifier;
		
		this.name = name;
		this.type = type.replace("[]", "\\[\\]");
		

		if(isFinal)
		{
			this.name = name.toUpperCase();
		}

		this.isStatic = isStatic;
		this.isFinal = isFinal;
		
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
	}


	public String getModifier() {
		return modifier;
	}


	public String getType() {
		return type;
	}


	public String getName() {
		return name;
	}

	public String getSign () {
		return sign;
	}

	public boolean isStatic() {
		return isStatic;
	}


	public boolean isFinal() {
		return isFinal;
	}


	public void setModifier(String modifier) {
		this.modifier = modifier;
	}


	public void setType(String type) {
		this.type = type;
	}


	public void setName(String name) {
		this.name = name;
	}


	public void setStatic(boolean isStatic) {
		this.isStatic = isStatic;
	}


	public void setFinal(boolean isFinal) {
		this.isFinal = isFinal;
	}


	public static Result extractVariabls(StringBuilder text,ArrayList<String> objectsNames,boolean isInMethod)
	{
		ArrayList<IndexMatch> indexes = new ArrayList<IndexMatch>();
		StringBuilder textReduced = new StringBuilder();
		 Pattern pattern=null ;
		 Matcher matcher=null;
		 String result="";// the final result;
			
			
			String objs="";
			for(String obj : objectsNames)
			{
			objs+="|"+obj;	
			}
			ArrayList<Element> vars = new ArrayList<Element>();
			
			
			
			 try
			 {
				 
				 variablePattren="(private\\s+|public\\s+|protected\\s+)?(static\\s+final\\s+|final\\s+static\\s+|static\\s+|final\\s+)?(byte|short|int|long|float|double|boolean|char|String"+objs+")(?!\\s+\\w+[()])\\s*((\\[\\s*\\d*\\s*\\]\\s*)*)\\s*([a-zA-Z0-9$_*,=]?\\s*[^;{}]*\\s*)\\s*;?";
				 if(!isInMethod)
					 variablePattren= variablePattren.substring(0,variablePattren.length()-1);
			 }
			 catch(Exception e)
			 {
				// System.out.println(e.getMessage());
				 return null;
			 }
			 



			 pattern= Pattern.compile(variablePattren);
				matcher = pattern.matcher(text);
				while(matcher.find())
				{
				//	 System.out.println(matcher.group(0));
					/*
					Modifier -gr1
					statics   -gr2
					Type      -gr3
					[]        -gr4
					name      -gr6

					*/
				//	System.out.println(matcher.group(0));
				//	System.out.println( matcher.group(1));
					String modifire = matcher.group(1)!=null ? matcher.group(1).trim():null ;
					String statics =  matcher.group(2)!=null ? matcher.group(2).trim():null ;
					String type =  matcher.group(3)!=null ? matcher.group(3).trim():null ;
					String bracketsBefore= matcher.group(4)==null ? "":matcher.group(4).trim();

					String fullName = matcher.group(6).trim();
					//new regex will be applied on this
					
					 Pattern patternOfName= Pattern.compile(NAME_PATTERN); ;
					 Matcher matcherOfName = patternOfName.matcher(fullName.trim());
					
						indexes.add(new IndexMatch(matcher.start(), matcher.end()));

					 while(matcherOfName.find())
						{
					//	 System.out.println(matcherOfName.group(0));
						 /*
					Names   - gr1
					[]      - gr2
					=val    -gr3

						   */
						 String name = matcherOfName.group(1)!=null ? matcherOfName.group(1).trim():"" ;
						 String bracketsAfter =  matcherOfName.group(2)!=null ? matcherOfName.group(2).trim():"" ;
						 String value = matcherOfName.group(3)!=null ? matcherOfName.group(3).trim():"" ;


						// System.out.println(name);

						 
						 
							
						 Variable var = new Variable(modifire, type+bracketsBefore+bracketsAfter, name);
						 if(statics!=null)
						 {
							 String[] arr =statics.split("\\s");
							 if(arr.length==2)
							 {
								 var.setFinal(true);
								 var.setStatic(true);
							 }
							 else if(arr[0].trim().equals("static"))
							 {
								 var.setStatic(true);
							 }
							 else
							 {
								 var.setFinal(true);
							 }
						 }
						
						 vars.add(var);
						result+=var.toString()+";";
						 
						 
						}
						
					
					
					
					// end of new regex
				
				}
				
				try {
					if(result.length()>0)
					{
						result = result.substring(0, result.length()-1);
						//System.out.println(result);

					}
				//	String url =" http://www.nomnoml.com/#view/" + "[myclass|"+URLEncoder.encode(result, "UTF-8")+"]";
				//	System.out.println(url);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				textReduced =	RegexUml.reduceText(text, indexes);

				 
//				 int start=0,end=0;
//				 for(IndexMatch indexMatch : indexes)
//				 {
//					 end= indexMatch.getStartIndex();
//					 textReduced.append(text.substring(start, end));
//					 start=indexMatch.getEndIndex();
//				 }
				
			
			return new Result(vars, textReduced);
	}
	
	
	
	@Override
	public String toString() {
	
		return sign+name+":"+type;
		
	}

}
