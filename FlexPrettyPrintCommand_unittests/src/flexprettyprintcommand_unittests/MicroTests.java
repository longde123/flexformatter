package flexprettyprintcommand_unittests;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Properties;

import flexprettyprint.preferences.PrefPage;

import utilities.CommandLine;

import junit.framework.TestCase;

public class MicroTests extends TestCase {
	public void testFormat() throws IOException
	{
		File tempDir=new File(System.getProperty("java.io.tmpdir"));
		File outputDir=new File(tempDir, "output/micro");
		if (!outputDir.exists())
			outputDir.mkdirs();

		File expectedResultsDir=new File(".\\benchmarks\\microFormatTests\\expectedResults");
		File sourcesDir=new File(".\\benchmarks\\microFormatTests");

		File[] sourceFiles=sourcesDir.listFiles(new FileFilter()
		{
			public boolean accept(File pathname) {
				if (pathname.getName().endsWith(".properties"))
					return false;
				return true;
			}
		});
		
		for (File sourceFile : sourceFiles)
		{
			int dot=sourceFile.getName().indexOf('.');
			if (dot<0)
				continue;
			
			String sourceName=sourceFile.getName().substring(0, dot);
			String sourceExt=sourceFile.getName().substring(dot+1);
			File settingsFile=new File(sourceFile.getParentFile(), sourceFile.getName()+".properties");
			assertTrue(settingsFile.exists());
			
			Properties settings=new Properties();
			InputStream is=new FileInputStream(settingsFile);
			settings.load(is);
			is.close();
			
			File outputFile=new File(outputDir, sourceFile.getName());
			RegressionTest.copyFile(sourceFile, outputFile);
			CommandLine.FormatFile(outputFile, Charset.defaultCharset().name(), settings, 3, true);
		}	
		
		//now, run file comparisons
		File[] outputFiles=outputDir.listFiles();
		int successCount=0;
		for (File outputFile : outputFiles)
		{
			if (outputFile.isDirectory())
				continue;
			File benchmarkFile=new File(expectedResultsDir, outputFile.getName());
			assertTrue(benchmarkFile.exists());
			RegressionTest.compareFiles(outputFile, benchmarkFile);
			successCount++;
		}
		System.out.println("Files successfully formatted: "+successCount);
	}
}
