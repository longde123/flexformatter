package actionscriptinfocollector;

import java.util.ArrayList;
import java.util.List;

import org.antlr.runtime.ParserRuleReturnScope;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPositionCategoryException;
import org.eclipse.jface.text.IDocument;

public class UseNamespaceItem extends SourceItem implements ISourceElement {
	private List<TextItem> mNamespaces;
	
	public UseNamespaceItem()
	{
		mNamespaces=new ArrayList<TextItem>();
	}
	
	public void addNamespace(ParserRuleReturnScope t)
	{
		mNamespaces.add(new TreeTextItem(t));
	}
	
	public List<TextItem> getNamespaces()
	{
		return mNamespaces;
	}
	
	@Override
	public boolean validateText(IDocument doc) throws BadLocationException
	{
		for (SourceItem item : mNamespaces) {
			if (!item.validateText(doc))
				return false;
		}
		return super.validateText(doc);
	}

	@Override
	public void nailDownPositions()
	{
		for (SourceItem item : mNamespaces) {
			item.nailDownPositions();
		}
		super.nailDownPositions();
	}

	@Override
	public void resetPositions(int delta, IDocument doc) throws BadLocationException, BadPositionCategoryException {
		for (SourceItem item : mNamespaces) {
			item.resetPositions(delta, doc);
		}
		super.resetPositions(delta, doc);
	}

	@Override
	public void applyDocument(IDocument doc) throws BadLocationException, BadPositionCategoryException {
		for (SourceItem item : mNamespaces) {
			item.applyDocument(doc);
		}
		super.applyDocument(doc);
	}
	
	
}
