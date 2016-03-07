package actionscriptinfocollector;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPositionCategoryException;
import org.eclipse.jface.text.IDocument;

public class PropertyLine extends TopLevelItemRecord implements DeclHolder {
	private List<DeclRecord> mProperties;
	private boolean mIsNamespace;
	private boolean mIsConst;
	
	public PropertyLine()
	{
		mProperties=new ArrayList<DeclRecord>();
		mIsNamespace=false;
		mIsConst=false;
	}
	
	public void addDecl(DeclRecord decl)
	{
		mProperties.add(decl);
	}

	@Override
	public String toString() {
		StringBuffer buffer=new StringBuffer();
		for (DeclRecord decl:mProperties) {
			buffer.append(decl);
		}
		buffer.append("Start: "+getStartPos()+" End: "+getEndPos()+"\n");
		buffer.append(super.toString());
		return buffer.toString();
	}

	@Override
	public void applyDocument(IDocument doc) throws BadLocationException, BadPositionCategoryException {
		super.applyDocument(doc);
		for (SourceItem item : mProperties) {
			item.applyDocument(doc);
		}
	}
	
	@Override
	public void nailDownPositions()
	{
		for (SourceItem item : mProperties) {
			item.nailDownPositions();
		}
		super.nailDownPositions();
	}

	
	@Override
	public void resetPositions(int delta, IDocument doc) throws BadLocationException, BadPositionCategoryException {
		
		for (SourceItem item : mProperties) {
			item.resetPositions(delta, doc);
		}
		super.resetPositions(delta, doc);
	}

	public List<DeclRecord> getProperties() {
		return mProperties;
	}

	@Override
	public boolean validateText(IDocument doc) throws BadLocationException
	{
		for (SourceItem item : mProperties) {
			if (!item.validateText(doc))
				return false;
		}
		return super.validateText(doc);
	}

	public boolean isNamespace() {
		return mIsNamespace;
	}

	public void setIsNamespace(boolean isNamespace) {
		mIsNamespace = isNamespace;
	}

	public void setConst(boolean value)
	{
		mIsConst=value;
	}
	
	public boolean isConst()
	{
		return mIsConst;
	}
}
