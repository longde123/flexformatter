package flexasdocgen.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

public class ASDocGenElementHandler extends AbstractHandler
{
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		ASDocGenHandler.addASDoc(true);
		return null;
	}
}
