package actionscriptinfocollector;

import java.util.ArrayList;
import java.util.List;

import org.antlr.runtime.ParserRuleReturnScope;
import org.antlr.runtime.Token;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPositionCategoryException;
import org.eclipse.jface.text.IDocument;

public class FunctionRecord extends TopLevelItemRecord implements DeclHolder
{
	public static final int Type_Normal=1;
	public static final int Type_Getter=2;
	public static final int Type_Setter=3;
	protected List<DeclRecord> mParms;
	protected TextItem mReturnType;
	protected int mType;
	protected TextItem mName;
	protected List<String> mThrowsExceptions;
	
	public FunctionRecord()
	{
		mType=Type_Normal;
		mParms=new ArrayList<DeclRecord>();
		mName=null;
		mThrowsExceptions=new ArrayList<String>();
	}

	
	public TextItem getName() {
		return mName;
	}

	public void setName(Token t) {
		mName = new SimpleTextItem(t);
	}

	public void setName(ParserRuleReturnScope t) {
		mName = new TreeTextItem(t);
	}

	public TextItem getReturnType() {
		return mReturnType;
	}

	public void setReturnType(ParserRuleReturnScope returnType) {
		mReturnType = new TreeTextItem(returnType);
	}

	public int getType() {
		return mType;
	}

	public void setType(int type) {
		mType = type;
	}

	public List<DeclRecord> getParameters() {
		return mParms;
	}
	
//	public void addArg(ParserRuleReturnScope name, ParserRuleReturnScope type)
//	{
//		DeclRecord parm=new DeclRecord(name, type);
//		mParms.add(parm);
//	}


	@Override
	public String toString()
	{
		StringBuffer buffer=new StringBuffer();
		buffer.append("Function "+getName()+" ->"+getReturnType());
		buffer.append("Start: "+getStartPos()+" End: "+getEndPos()+"\n");
		buffer.append(super.toString());
		switch (getType())
		{
		case Type_Getter:
			buffer.append("(Get)");
			break;
		case Type_Setter:
			buffer.append("(Set)");
			break;
		}
		
		if (getParameters().size()>0)
		{
			buffer.append(" (");
			for (DeclRecord parm : getParameters()) {
				buffer.append(parm.getName());
				buffer.append(":");
				buffer.append(parm.getType());
				buffer.append(", ");
			}
			
			buffer.append(")");
		}
		return buffer.toString();
	}


	public void addDecl(DeclRecord decl) {
		mParms.add(decl);
	}


	@Override
	public void applyDocument(IDocument doc) throws BadLocationException, BadPositionCategoryException {
		super.applyDocument(doc);
		
		if (mName!=null)
			mName.applyDocument(doc);
		
		if (mReturnType!=null)
			mReturnType.applyDocument(doc);
		
		for (SourceItem item : getParameters()) {
			item.applyDocument(doc);
		}
	}


	@Override
	public void nailDownPositions()
	{
		if (mName!=null)
			mName.nailDownPositions();
		
		if (mReturnType!=null)
			mReturnType.nailDownPositions();
		
		for (SourceItem item : getParameters()) {
			item.nailDownPositions();
		}
		
		super.nailDownPositions();
	}


	@Override
	public void resetPositions(int delta, IDocument doc) throws BadLocationException, BadPositionCategoryException {
		if (mName!=null)
			mName.resetPositions(delta, doc);
		
		if (mReturnType!=null)
			mReturnType.resetPositions(delta, doc);
		
		for (SourceItem item : getParameters()) {
			item.resetPositions(delta, doc);
		}

		super.resetPositions(delta, doc);
	}


	public void addThrowsExpression(ParserRuleReturnScope throwsExpression)
	{
		String throwsText=AntlrUtilities.getTreeText(throwsExpression);
		if (throwsText.startsWith("new"))
			throwsText=AntlrUtilities.asTrim(throwsText.substring(3));
		
		int paren=throwsText.indexOf('(');
		if (paren>0)
			throwsText=throwsText.substring(0, paren);
		
		mThrowsExceptions.add(AntlrUtilities.asTrim(throwsText));
	}


	public List<String> getThrowsExceptions() {
		return mThrowsExceptions;
	}

	@Override
	public boolean validateText(IDocument doc) throws BadLocationException
	{
		if (mReturnType!=null && !mReturnType.validateText(doc))
			return false;
		
		if (mName!=null && !mName.validateText(doc))
			return false;
		
		for (SourceItem item : mParms) {
			if (!item.validateText(doc))
				return false;
		}
		return super.validateText(doc);
	}
	
}

