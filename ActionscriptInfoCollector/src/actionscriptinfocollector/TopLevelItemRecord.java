package actionscriptinfocollector;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.antlr.runtime.Token;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPositionCategoryException;
import org.eclipse.jface.text.IDocument;

public class TopLevelItemRecord extends SourceItem implements ISourceElement
{
	public static final int ASDoc_Public=0x1;
	public static final int ASDoc_Private=0x2;
	public static final int ASDoc_Protected=0x4;
	public static final int ASDoc_Internal=0x8;
	public static final int ASDoc_Dynamic=0x10;
	public static final int ASDoc_Final=0x20;
	public static final int ASDoc_Static=0x40;
	public static final int ASDoc_Native=0x80;
	public static final int ASDoc_Override=0x100;
	public static final int ASDoc_Namespace=0x200;
	public static final int ASDoc_Const=0x400; //added for convenience, even though it's not really a modifier
	
	protected Set<TextItem> mModifiers;
	protected ASDocComment mComment;
	protected int mModifierFlags;
	protected List<MetadataItem> mMetadataItems;
	
	public TopLevelItemRecord()
	{
		mModifiers=new HashSet<TextItem>();
		mModifierFlags=0;
		mMetadataItems=new ArrayList<MetadataItem>();
	}
	
	
	public TextItem addModifier(Token modifier)
	{
		TextItem mod=new SimpleTextItem(modifier);
		mModifiers.add(mod);
		if (mod.getText().equals("public"))
			mModifierFlags|=ASDoc_Public;
		else if (mod.getText().equals("private"))
			mModifierFlags|=ASDoc_Private;
		else if (mod.getText().equals("protected"))
			mModifierFlags|=ASDoc_Protected;
		else if (mod.getText().equals("override"))
			mModifierFlags|=ASDoc_Override;
		else if (mod.getText().equals("dynamic"))
			mModifierFlags|=ASDoc_Dynamic;
		else if (mod.getText().equals("final"))
			mModifierFlags|=ASDoc_Final;
		else if (mod.getText().equals("internal"))
			mModifierFlags|=ASDoc_Internal;
		else if (mod.getText().equals("native"))
			mModifierFlags|=ASDoc_Native;
		else if (mod.getText().equals("static"))
			mModifierFlags|=ASDoc_Static;
//		else if (mod.getText().equals("const")) //NOT a modifier, so don't include in modifier flags
		return mod;
	}

	public Set<TextItem> getModifiers() {
		return mModifiers;
	}


	public ASDocComment getComment() {
		return mComment;
	}


	public void setComment(ASDocComment comment) {
		mComment = comment;
	}


	@Override
	public String toString() {
		StringBuffer buffer=new StringBuffer();
		if (getModifiers().size()>0)
		{
			buffer.append("modifiers(");
			for (TextItem mod : getModifiers()) {
				buffer.append(mod+", ");
			}
			buffer.append(") ");
		}
		if (mComment!=null)
		{
			buffer.append(mComment);
		}
		return buffer.toString();
	}


	@Override
	public void applyDocument(IDocument doc) throws BadLocationException, BadPositionCategoryException {
		super.applyDocument(doc);
		
		if (mComment!=null)
			mComment.applyDocument(doc);	
		for (SourceItem item : getModifiers()) {
			item.applyDocument(doc);
		}
		
		for (MetadataItem mItem : mMetadataItems) {
			mItem.applyDocument(doc);
		}
		
	}


	public int getModifierFlags() {
		if ((mModifierFlags & 0xf)==0)
			mModifierFlags|=ASDoc_Internal;
		return mModifierFlags;
	}


	@Override
	public void addPreTokens(List<Token> hiddenTokens)
	{
		super.addPreTokens(hiddenTokens);
		setComment(AntlrUtilities.findCommentReverse(hiddenTokens));
	}

	public void addMetadataItems(List<MetadataItem> items)
	{
		mMetadataItems.addAll(items);
	}

	public List<MetadataItem> getMetadataItems()
	{
		return mMetadataItems;
	}


	@Override
	public void nailDownPositions()
	{
		if (mComment!=null)
			mComment.nailDownPositions();
		for (TextItem item : mModifiers) {
			item.nailDownPositions();
		}
		for (MetadataItem item : mMetadataItems) {
			item.nailDownPositions();
		}
		super.nailDownPositions();
	}

	public void resetPositions(int delta, IDocument doc) throws BadLocationException, BadPositionCategoryException
	{
		if (mComment!=null)
			mComment.resetPositions(delta, doc);
		for (TextItem mod : mModifiers) {
			mod.resetPositions(delta, doc);
		}
//		for (MetadataItem item : mMetadataItems) {
//			item.resetPositions(delta, doc);
//		}
		super.resetPositions(delta, doc);
	}


	@Override
	public boolean validateText(IDocument doc) throws BadLocationException
	{
		for (SourceItem item : getMetadataItems()) {
			if (!item.validateText(doc))
				return false;
//			if (item.getStartPos()<getStartPos())
//				return false;
		}
		
		for (SourceItem item : getModifiers()) {
			if (!item.validateText(doc))
				return false;
			if (item.getStartPos()<getStartPos())
				return false;
		}
		
		if (mComment!=null && !mComment.validateText(doc))
			return false;
		
		return super.validateText(doc);
	}


	@Override
	public boolean captureObject(IDocument doc, Object o, int lineNumber) throws BadLocationException
	{
		for (MetadataItem metaItem : mMetadataItems) {
			if (metaItem.captureObject(doc, o, lineNumber))
				return true;
		}
		
		return super.captureObject(doc, o, lineNumber);
	}


	@Override
	public List<ObjectPositionHolder> getAttachedObjects() {
		
		List<ObjectPositionHolder> objects=new ArrayList<ObjectPositionHolder>();
		for (MetadataItem metaItem : mMetadataItems) {
			objects.addAll(metaItem.getAttachedObjects());
		}
		objects.addAll(super.getAttachedObjects());
		return objects;
	}
	
	public int getEarliestRelatedPos()
	{
		//if we have any metadata items, then return the beginning of the first one of those.  Otherwise
		//just return the beginning of our stuff.
		if (mMetadataItems.size()>0)
			return mMetadataItems.get(0).getPreStartPos();
		return getPreStartPos();
	}
}
