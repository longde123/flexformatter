package flexprettyprintcommand_unittests;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Properties;

import junit.framework.TestCase;
import utilities.CommandLine;

public class RegressionTest extends TestCase
{
	public void testFormat() throws IOException
	{
		File tempDir=new File(System.getProperty("java.io.tmpdir"));
		File outputDir=new File(tempDir, "output");
		if (!outputDir.exists())
			outputDir.mkdir();

		File settingsDir=new File(".\\benchmarks\\settings");
		File expectedResultsDir=new File(".\\benchmarks\\expectedResults");
		File sourcesDir=new File(".\\benchmarks\\sources");
		
		File[] sourceFiles=sourcesDir.listFiles();
		for (File sourceFile : sourceFiles)
		{
			int dot=sourceFile.getName().indexOf('.');
			if (dot<0)
				continue;
			String sourceName=sourceFile.getName().substring(0, dot);
			String sourceExt=sourceFile.getName().substring(dot+1);
			File[] settingsFiles=settingsDir.listFiles();
			for (File propFile : settingsFiles)
			{
				if (!propFile.getName().endsWith(".properties"))
					continue;
				Properties settings=new Properties();
				InputStream is=new FileInputStream(propFile);
				settings.load(is);
				is.close();
				String settingsName=propFile.getName().substring(0, propFile.getName().indexOf('.'));
				
				String charsetName=Charset.defaultCharset().name();
				File outputFile=new File(outputDir, sourceName+"."+settingsName+".format."+sourceExt);
				copyFile(sourceFile, outputFile);
				CommandLine.FormatFile(outputFile, charsetName, settings, 4, true);
				
				outputFile=new File(outputDir, sourceName+"."+settingsName+".indent."+sourceExt);
				copyFile(sourceFile, outputFile);
				CommandLine.FormatFile(outputFile, charsetName, settings, 2, false);				
			}
		}
		
		File[] outputFiles=outputDir.listFiles();
		for (File outputFile : outputFiles)
		{
			if (outputFile.isDirectory())
				continue;
			File benchmarkFile=new File(expectedResultsDir, outputFile.getName());
			assertTrue(benchmarkFile.exists());
			compareFiles(outputFile, benchmarkFile);
		}
	}
	
	public static void compareFiles(File outputFile, File benchmarkFile) throws IOException
	{
		String outputString=readFile(outputFile);
		String expectedString=readFile(benchmarkFile);
		String[] outputLines=outputString.split("\n");
		String[] expectedLines=expectedString.split("\n");
		if (outputLines.length!=expectedLines.length)
		{
			assertTrue(outputLines.length==expectedLines.length);
		}
		for (int i=0;i<outputLines.length;i++)
		{
			if (!expectedLines[i].equals(outputLines[i]))
			{
				assertEquals(expectedLines[i], outputLines[i]);
			}
		}
	}

	public static String readFile(File f) throws IOException
	{
		BufferedReader br=new BufferedReader(new FileReader(f));
		StringBuffer result=new StringBuffer();
		while (true)
		{
			String line=br.readLine();
			if (line==null)
				break;
			result.append(line);
			result.append('\n');
		}
		return result.toString();
	}

	public static void copyFile(File source, File target) throws IOException
	{
		target.getParentFile().mkdirs();
		BufferedInputStream bis=new BufferedInputStream(new FileInputStream(source));
		BufferedOutputStream bos=new BufferedOutputStream(new FileOutputStream(target));
		byte[] bytes=new byte[1024];
		while (true)	
		{
			int bytesRead=bis.read(bytes);
			if (bytesRead<0)
				break;
			
			bos.write(bytes, 0, bytesRead);
		}
		
		bis.close();
		bos.close();
	}
}
