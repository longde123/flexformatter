package utilities;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.texteditor.MarkerAnnotation;

import flexasrearrangecodecommand.handlers.ASRearranger;
import flexasrearrangecodecommand.handlers.MXMLRearranger;
import flexasrearrangecodecommand.preferences.PreferenceConstants;
import flexprettyprint.handlers.ASPrettyPrinter;
import flexprettyprint.handlers.MXMLNamespaceCleaner;
import flexprettyprint.handlers.MXMLPrettyPrinter;
import flexprettyprint.preferences.Initializer;

public class CommandLine
{
	private static boolean mTestMode=false;
//	public static String UTF8="UTF8";
	
	/**
	 * @param args 
	 * 	[0]=full directory path or file path, required parameter
	 * 	[1]=location of property file with settings, default=empty property set 
	 * 	[2]=tabSize, default=3, must be >=1
	 * 	[3]=true->format, false->indent, default=false
	 *  [4]=java charset name, ex. UTF8.  Defaults to the default charset
	 *  [5]=true->change file, false->no output (test mode).  True is the default
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException
	{
		if (args.length==0)
		{
			showHelp();
			System.exit(1);
		}
		
		File topDir=null;
		if (args.length>=1)
		{
			topDir=new File(args[0]);
			if (!topDir.exists())
			{
				System.out.println("Error: File/Directory does not exist: "+args[0]);
				showHelp();
				System.exit(2);
			}
		}
		
		Properties settings=new Properties();
		if (args.length>=2)
		{
			File settingsFile=new File(args[1]);
			if (!settingsFile.exists())
			{
				System.out.println("Error: Settings file doesn't exist.");
				showHelp();
				System.exit(3);
			}

			InputStream is=new FileInputStream(settingsFile);
			settings.load(is);
			is.close();
		}
	
		int tabSize=3;
		if (args.length>=3)
		{
			String tabString=args[2];
			tabSize=Integer.parseInt(tabString);
			if (tabSize<1)
			{
				System.out.println("Tab size could not be converted to a positive integer.");
				showHelp();
				System.exit(4);
			}
		}
		
		boolean format=false;
		if (args.length>=4)
		{
			String formatString=args[3];
			format=Boolean.parseBoolean(formatString);
		}
		
		String charsetName=Charset.defaultCharset().name();
		if (args.length>=5)
		{
			charsetName=args[4].trim();
		}
		
		if (args.length>=6)
		{
			Boolean testMode=Boolean.parseBoolean(args[5]);
			mTestMode=testMode.booleanValue();
		}
		
		FormatAllFilesInDirectory(topDir, charsetName, settings, tabSize, format);
	}
	
	public static void showHelp()
	{
		System.out.println("Usage:");
		System.out.println("[0]=full file/directory path, required parameter");
		System.out.println("[1]=location of property file with settings, default=empty property set");
		System.out.println("[2]=tabSize, default=3");
		System.out.println("[3]=true->format, false->indent, default=false");
		System.out.println("[4]=java charset name, ex. UTF8.  Defaults to the default charset.");
	}
	
	public static void FormatAllFilesInDirectory(File directoryOrFile, String charsetName, Properties settings, int tabSize, boolean format)
	{
		List<File> allFiles=new ArrayList<File>();
		if (directoryOrFile.isFile())
			allFiles.add(directoryOrFile);
		else
			getRecursiveFiles(directoryOrFile, allFiles);
		List<File> successfullyFormatted=new ArrayList<File>();
		List<File> unSuccessfullyFormatted=new ArrayList<File>();
		
		for (File file : allFiles)
		{
			try
			{
				boolean success=FormatFile(file, charsetName, settings, tabSize, format);
				if (success)
				{
					successfullyFormatted.add(file);
				}
				else
				{
					unSuccessfullyFormatted.add(file);
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		
		if (mTestMode)
		{
			for (File f : successfullyFormatted) {
				System.out.println("Successful format: "+f.getAbsolutePath());
			}
		}
		
		for (File f : unSuccessfullyFormatted) {
			System.out.println("Failed to"+(format ? "format" : "indent")+": "+f.getAbsolutePath());
		}
	}
	
	@SuppressWarnings("deprecation")
	public static void migrateProperties(Properties props)
	{
		if (!props.containsKey(Initializer.Pref_AS_NewlineBeforeBindableFunction) && props.containsKey(Initializer.Pref_AS_NewlineAfterBindable))
		{
			props.put(Initializer.Pref_AS_NewlineBeforeBindableFunction, props.get(Initializer.Pref_AS_NewlineAfterBindable));
			props.put(Initializer.Pref_AS_NewlineBeforeBindableProperty, props.get(Initializer.Pref_AS_NewlineAfterBindable));
		}

		if (!props.containsKey(Initializer.Pref_AS_AdvancedSpacesInsideParensInParameterLists) && props.containsKey(Initializer.Pref_AS_AdvancedSpacesInsideParens))
		{
			props.put(Initializer.Pref_AS_AdvancedSpacesInsideParensInParameterLists, props.get(Initializer.Pref_AS_AdvancedSpacesInsideParens));
			props.put(Initializer.Pref_AS_AdvancedSpacesInsideParensInArgumentLists, props.get(Initializer.Pref_AS_AdvancedSpacesInsideParens));
			props.put(Initializer.Pref_AS_AdvancedSpacesInsideParensInOtherPlaces, props.get(Initializer.Pref_AS_AdvancedSpacesInsideParens));
		}
		
		if (!props.containsKey(Initializer.Pref_AS_AdvancedSpacesAfterColonsInDeclarations) && props.containsKey(Initializer.Pref_AS_AdvancedSpacesAfterColons))
		{
			props.put(Initializer.Pref_AS_AdvancedSpacesAfterColonsInDeclarations, props.get(Initializer.Pref_AS_AdvancedSpacesAfterColons));
			props.put(Initializer.Pref_AS_AdvancedSpacesAfterColonsInFunctionTypes, props.get(Initializer.Pref_AS_AdvancedSpacesAfterColons));
			props.put(Initializer.Pref_AS_AdvancedSpacesBeforeColonsInDeclarations, props.get(Initializer.Pref_AS_AdvancedSpacesBeforeColons));
			props.put(Initializer.Pref_AS_AdvancedSpacesBeforeColonsInFunctionTypes, props.get(Initializer.Pref_AS_AdvancedSpacesBeforeColons));
		}

		if (!props.containsKey(Initializer.Pref_AS_AddBracesToCases) && props.containsKey(Initializer.Pref_AS_EnsureSwitchCasesHaveBraces))
		{
			Object val=props.get(Initializer.Pref_AS_EnsureSwitchCasesHaveBraces);
			if (val instanceof String)
				props.put(Initializer.Pref_AS_AddBracesToCases, Integer.toString(((String)val).equalsIgnoreCase("true") ? ASPrettyPrinter.Braces_AddSmart : ASPrettyPrinter.Braces_NoModify));
		}

		if (!props.containsKey(Initializer.Pref_AS_AddBracesToConditionals) && props.containsKey(Initializer.Pref_AS_EnsureConditionalsHaveBraces))
		{
			Object val=props.get(Initializer.Pref_AS_EnsureConditionalsHaveBraces);
			if (val instanceof String)
				props.put(Initializer.Pref_AS_AddBracesToConditionals, Integer.toString(((String)val).equalsIgnoreCase("true") ? ASPrettyPrinter.Braces_AddIfMissing : ASPrettyPrinter.Braces_NoModify));
		}

		if (!props.containsKey(Initializer.Pref_AS_AddBracesToLoops) && props.containsKey(Initializer.Pref_AS_EnsureLoopsHaveBraces))
		{
			Object val=props.get(Initializer.Pref_AS_EnsureLoopsHaveBraces);
			if (val instanceof String)
				props.put(Initializer.Pref_AS_AddBracesToLoops, Integer.toString(((String)val).equalsIgnoreCase("true") ? ASPrettyPrinter.Braces_AddIfMissing : ASPrettyPrinter.Braces_NoModify));
		}

		if (!props.containsKey(Initializer.Pref_AS_BreakLinesBeforeArithmetic) && props.containsKey(Initializer.Pref_AS_BreakLinesBeforeComma))
		{
			props.put(Initializer.Pref_AS_BreakLinesBeforeArithmetic, props.get(Initializer.Pref_AS_BreakLinesBeforeComma));
			props.put(Initializer.Pref_AS_BreakLinesBeforeAssignment, props.get(Initializer.Pref_AS_BreakLinesBeforeComma));
			props.put(Initializer.Pref_AS_BreakLinesBeforeLogical, props.get(Initializer.Pref_AS_BreakLinesBeforeComma));
		}
		
	}
	
	public static PreferenceStore loadPrefStore(Properties settings)
	{
		CommandLine.migrateProperties(settings);

		PreferenceStore prefStore=new PreferenceStore();

		//use my preference initializer with a special constructor to set the default prefs so that a pref file that
		//doesn't contain all items will still use the default values
		Initializer init=new Initializer(prefStore);
		init.initializeDefaultPreferences();
		
		for (Map.Entry<Object, Object> entry : settings.entrySet())
		{
			String key=(String)entry.getKey();
			String value=(String)entry.getValue();
			prefStore.putValue(key, value);
		}
		return prefStore;
	}
	
	public static boolean FormatFile(File file, String charsetName, Properties settings, int tabSize, boolean format) throws IOException
	{
		PreferenceStore prefStore=loadPrefStore(settings);
		String platformCR=System.getProperty("line.separator");
		
		Set<String> asExtensions=new HashSet<String>();
		asExtensions.add("as");
		
		Set<String> mxmlExtensions=new HashSet<String>();
		mxmlExtensions.add("mxml");
		
		int lastDot=file.getName().lastIndexOf('.');
		if (lastDot>=0)
		{
			String extension=file.getName().substring(lastDot+1).toLowerCase();
			if (!(asExtensions.contains(extension) || mxmlExtensions.contains(extension)))
			{
				System.out.println("Skipping unknown file type: "+file.getAbsolutePath());
				return true;
			}
				
			String data=readFileContent(file, charsetName);
			if (data.indexOf(ASPrettyPrinter.mIgnoreFileProcessing)>=0)
			{
				System.out.println("Skipping file containing FlexFormatter ignore flag: "+file.getAbsolutePath());
				return false;
			}
			
			if (mxmlExtensions.contains(extension))
			{
				//reorder top-level elements in mxml file (under root tag)
				if (format && prefStore.getBoolean(PreferenceConstants.MXMLRearr_RearrangeWhileFormatting))
				{
					MXMLRearranger rearranger=new MXMLRearranger(prefStore);
					try
					{
						IDocument doc=new Document(data);
						boolean success=rearranger.rearrangeCode(doc, new ArrayList<MarkerAnnotation>());
						if (!success)
						{
							System.out.println("Failed to rearrange: "+file.getAbsolutePath());
							if (!rearranger.isSoftFailure())
								return false;
						}
						
						data=doc.get();
					}
					catch (Exception e)
					{
						e.printStackTrace();
						return false;
					}
				}

				//remove unused namespaces
				if (format && prefStore.getBoolean(Initializer.Pref_MXML_RemoveNamespacesAsPartOfFormat))
				{
					IDocument doc=new Document(data);
					MXMLNamespaceCleaner cleaner=new MXMLNamespaceCleaner(doc);
					try
					{
						boolean success=cleaner.removeExtra();
						if (!success)
						{
							System.out.println("******Failed to remove extra namespaces: "+file.getAbsolutePath());
							return false;
						}
						data=doc.get(); //update working data
					}
					catch (Exception e)
					{
						System.out.println("******Failed to remove extra namespaces (with exception): "+file.getAbsolutePath());
						e.printStackTrace();
						return false;
					}
				}
				
				//normal formatting
				MXMLPrettyPrinter printer=new MXMLPrettyPrinter(data);
				FormatUtility.configureMXMLPrinter(printer, prefStore, tabSize);
				printer.setTabSize(tabSize);
				printer.setDoFormat(format);
				try
				{
					String resultData=printer.print(0);
					if (resultData==null)
					{
						System.out.println("******Failed to format (bad parse or internal failure): "+file.getAbsolutePath());
						return false;
					}
					if (!mTestMode)
					{
						if (!platformCR.equals("\n"))
							resultData=resultData.replace("\n", platformCR);
						saveFileContent(resultData, file, charsetName);
					}
					
					System.out.println("    Successfully formatted: "+file.getAbsolutePath());
					return true;
				}
				catch (IOException e)
				{
					System.out.println("******Failed to format (with exception): "+file.getAbsolutePath());
					throw e;
				}
				catch (Exception e)
				{
					System.out.println("******Failed to format (with exception): "+file.getAbsolutePath());
					e.printStackTrace();
				}
			}
			else if (asExtensions.contains(extension))
			{
				if (format && prefStore.getBoolean(Initializer.Pref_AS_RearrangeAsPartOfFormat))
				{
					ASRearranger rearranger=new ASRearranger(prefStore);
					try
					{
						IDocument doc=new Document(data);
						boolean success=rearranger.rearrangeCode(doc, new ArrayList<MarkerAnnotation>(), false);
						if (!success)
						{
							System.out.println("Failed to rearrange: "+file.getAbsolutePath());
							if (!rearranger.isSoftFailure())
								return false;
						}
						
						data=doc.get();
					}
					catch (Exception e)
					{
						e.printStackTrace();
						return false;
					}
				}
				
				ASPrettyPrinter printer=new ASPrettyPrinter(true, data);
				FormatUtility.configureASPrinter(printer, prefStore, tabSize);
				printer.setTabSize(tabSize);
				printer.setDoFormat(format);
				try
				{
					String resultData=printer.print(0);
					if (resultData==null)
					{
						System.out.println("******Failed to format (bad parse or internal failure): "+file.getAbsolutePath());
						return false;
					}
					if (!mTestMode)
					{
						if (!platformCR.equals("\n"))
							resultData=resultData.replace("\n", platformCR);
						saveFileContent(resultData, file, charsetName);
					}
					
					System.out.println("    Successfully formatted: "+file.getAbsolutePath());
					return true;
				}
				catch (Exception e)
				{
					System.out.println("******Failed to format (with exception): "+file.getAbsolutePath());
					e.printStackTrace();
				}
			}
			else
			{
				System.out.println("Skipping unknown file type: "+file.getAbsolutePath());
				return true;
			}
		}
		else
		{
			System.out.println("Skipping file with no extension: "+file.getAbsolutePath());
			return true;
		}
		
		return false;
	}

	private static boolean saveFileContent(String data, File file, String charsetName)
	{
		try
		{
			BufferedWriter bw=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), charsetName));
			bw.write(data);
			bw.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally
		{}
		
		return false;
	}

	public static String readFileContent(File file, String charsetName)
	{
		try
		{
			BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(file), charsetName));
			StringBuffer buffer=new StringBuffer();
			while (true)
			{
				String line=br.readLine();
				if (line==null)
					break;
				buffer.append(line);
				buffer.append('\n');
			}
			return buffer.toString();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{}
		return null;
	}

	private static void getRecursiveFiles(File dir, List<File> results)
	{
		File[] allFiles=dir.listFiles();
		if (allFiles==null)
			return;
		for (File file : allFiles) {
			if (file.isFile())
			{
				results.add(file);
			}
			else if (file.isDirectory())
			{
				getRecursiveFiles(file, results);
			}
		}
	}
	
}
