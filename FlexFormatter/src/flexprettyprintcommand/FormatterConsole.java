package flexprettyprintcommand;

import java.io.IOException;
import java.io.PrintStream;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IOConsole;
import org.eclipse.ui.console.IOConsoleOutputStream;

public class FormatterConsole extends IOConsole
{
	private static FormatterConsole mConsole;
	private static final String ConsoleName="FlexFormatter console";
	public static final int Type_Standard=1;
	public static final int Type_Error=2;
	public static final int Type_RecordingCommand=3;
	public static final int Type_PlayingCommand=4;
	public static final int Type_DebugInfo=5;
	public static final Color Red=new Color(Display.getDefault(), 255,0,0);
	public static final Color Green=new Color(Display.getDefault(), 0,128,0);
	public static final Color Blue=new Color(Display.getDefault(), 0,0,255);
	public static final Color Purple=new Color(Display.getDefault(), 128,0,128);
	public FormatterConsole()
	{
		super("Flex Formatter", null);
		mConsole=null;
	}
	
	public void write(Exception e)
	{
		write(e, Type_Error);
	}
	
	public void write(Exception e, int type)
	{
		ConsolePlugin.getDefault().getConsoleManager().showConsoleView(mConsole);
		
		IOConsoleOutputStream outputStream=mConsole.newOutputStream();
		configureStream(outputStream, type);
		try
		{
			PrintStream ps=new PrintStream(outputStream);
			e.printStackTrace(ps);
		}
		finally
		{
			try {
				outputStream.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	public void writeln(String data)
	{
		writeln(data, Type_Standard);
	}
	
	public void writeln(String data, int type)
	{
		write(data+"\n", type);
	}
	
	public void write(String data)
	{
		write(data, Type_Standard);
	}

	public void write(String data, int type)
	{
		try
		{
			IWorkbenchWindow[] allWindows=PlatformUI.getWorkbench().getWorkbenchWindows();
			for (IWorkbenchWindow window : allWindows) {
				IViewReference consoleView=window.getActivePage().findViewReference("org.eclipse.ui.console.ConsoleView");
				if (consoleView!=null)
				{
					if (!consoleView.isFastView())
					{
						ConsolePlugin.getDefault().getConsoleManager().showConsoleView(mConsole);
					}
					break;
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		IOConsoleOutputStream outputStream=mConsole.newOutputStream();
		configureStream(outputStream, type);
		try {
			outputStream.write(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally
		{
			try {
				outputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void configureStream(IOConsoleOutputStream outputStream, int type)
	{
		switch (type)
		{
		case Type_Error:
			outputStream.setColor(Red);
			break;
		case Type_PlayingCommand:
			outputStream.setColor(Blue);
			break;
		case Type_RecordingCommand:
			outputStream.setColor(Green);
			break;
		case Type_DebugInfo:
			outputStream.setColor(Purple);
			break;
		}
	}

	public static FormatterConsole getConsole()
	{
		if (mConsole==null)
		{
			IConsole[] consoles=ConsolePlugin.getDefault().getConsoleManager().getConsoles();
			for (IConsole console : consoles) {
				if (console.getName().equals(ConsoleName) && (console instanceof FormatterConsole))
				{
					mConsole=(FormatterConsole)console;
				}
			}
			
			if (mConsole==null)
			{
				mConsole=new FormatterConsole();
				ConsolePlugin.getDefault().getConsoleManager().addConsoles(new IConsole[]{mConsole});
			}
		}
		
		mConsole.setWaterMarks(10000, 100000);
		return mConsole;
	}
}
