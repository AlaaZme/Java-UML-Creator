
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUml
{
	static ArrayList<String> types = new ArrayList<String>();

public static String  readInputFiles(String pathOfDir)
{
	String url = "Error";
	FileReader fileReader;
	 BufferedReader reader = null ;
	  StringBuilder sb = new StringBuilder();
	  StringBuilder finalResult = new StringBuilder();
	//System.out.println("Please enter class name with full path");
//	Scanner scan = new Scanner(System.in);
	//String path = scan.next();
	  String path = pathOfDir;
	try {
	File f = new File(path);
	
	
	String filename="";
	for(File file : f.listFiles())
	{
		filename=file.getName();
		if(filename.endsWith(".java"))
		{
			filename=filename.substring(0, filename.indexOf('.'));
			types.add(filename);
			System.out.println(filename);
		}

	}
	
	
	


		int i=0;
		for(String type:types)
		{i++;

			fileReader = new FileReader(f.getAbsolutePath()+"\\"+type+".java");
			   reader = new BufferedReader(fileReader);
			    String line = reader.readLine();
			    
			    while (line != null)
			    {
			        sb.append(line);
			        sb.append(System.lineSeparator());
			        line = reader.readLine();
			    }
				//sb=RegexUml.extractBrackets(sb);
			    sb=RegexUml.extractBrackets2(sb);

				Result result = Class.extractClasses(sb, types);
				if(result.getList().size() > 0)
				{
				finalResult.append(result.getList().get(0));
				sb=new StringBuilder();					

				}

				
		}
		
			if(types.size()>0)
			{
				url = "http://www.nomnoml.com/#view/"+finalResult.toString();
				System.out.println(url);
				   StringSelection selection = new StringSelection(url);
				    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				    clipboard.setContents(selection, selection);
			}
			else
			{
				url="No Java files.";
			}





	} 
	 catch (Exception e)
	{
			System.out.println(e.getMessage());
	}
	
	finally
	{
		try {
			reader.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
		System.out.println(e.getMessage());
		}

	}
	return  url;
}


	public static final String COMMENTS_PATERN="(\\/\\*([^*]|[\\r\\n]|(\\*+([^*\\/]|[\\r\\n])))*\\*+\\/)|(\\/\\/.*)|(\\/\\*)";

	StringBuilder editedText=new StringBuilder();

	public static StringBuilder extractBrackets2(StringBuilder text)
	{
		int counter=0;
		StringBuilder textReduced = new StringBuilder();
		String[] lines = RegexUml.extractComments(text).toString().split("\n");
		int index=-1;
		for(String line : lines)
		{
			counter++;
		//line=line.trim();
		/*	if(line.contains("try"))
			{
				System.out.println("yess");
			}
			*/
			String newLine="";
			boolean inString=false;
			for (int i = 0; i < line.length(); i++) 
			{
				
				char curretnChar=line.charAt(i);
				if(curretnChar=='{')
				{
					if(index <1)// the method brackets or class
					{
						newLine+="{";

					}
					if(inString==false)
					index++;


				}
				else if(curretnChar=='}')
				{
					if(inString==false)
					index--;

					if(index <1)// the method brackets or class
					{
						newLine+="}";

					}	

				}
				else if(curretnChar=='"')
				{
					if((i-1 >=0 && line.charAt(i-1)!='\\') || i==0)
					{
						inString=!inString;
					}
				}
				else
				{
					if(index<1)
					newLine+=curretnChar;
				}

			}
			textReduced.append(newLine);

		}
	//	System.out.println(textReduced);
		return textReduced;
	}


	public static StringBuilder extractComments(StringBuilder text)
	{
		ArrayList<IndexMatch> indexes = new ArrayList<IndexMatch>();
		StringBuilder textReduced = new StringBuilder();
		Pattern pattern = null ;
		Matcher matcher= null ;
		pattern= Pattern.compile(COMMENTS_PATERN);


		matcher = pattern.matcher(text);

		while(matcher.find())
		{
			indexes.add(new IndexMatch(matcher.start(), matcher.end()));

		}

		textReduced =	reduceText(text, indexes);

		return textReduced;


	}


	public static StringBuilder reduceText(StringBuilder source,ArrayList<IndexMatch> indexes)
	{
		StringBuilder textReduced = new StringBuilder();

		int start=0,end=0;
		for(IndexMatch indexMatch : indexes)
		{
			end= indexMatch.getStartIndex();
			textReduced.append(source.substring(start, end));
			start=indexMatch.getEndIndex();
		}
		textReduced.append(source.substring(start, source.length()));

		return textReduced;
	}







}
