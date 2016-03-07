package actionscriptinfocollector;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.antlr.runtime.ParserRuleReturnScope;
import org.antlr.runtime.Token;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPositionCategoryException;
import org.eclipse.jface.text.IDocument;

public class MetadataItem extends SourceItem implements ISourceElement {
	private List<TextItem> mArgs;
	private TextItem mBindingType;
	public MetadataItem()
	{
		mArgs=new ArrayList<TextItem>();
		mBindingType=null;
	}
	
	public void addArg(ParserRuleReturnScope t)
	{
		mArgs.add(new TreeTextItem(t));
	}
	
	public void setBindingType(Token t)
	{
		mBindingType=new SimpleTextItem(t);
	}
	
	public TextItem getType()
	{
		return mBindingType;
	}
	
	

	@Override
	public void applyDocument(IDocument doc) throws BadLocationException, BadPositionCategoryException {
		if (mBindingType!=null)
			mBindingType.applyDocument(doc);
		for (TextItem item : mArgs) {
			item.applyDocument(doc);
		}
		super.applyDocument(doc);
	}

	@Override
	public void nailDownPositions() {
		if (mBindingType!=null)
			mBindingType.nailDownPositions();
		for (TextItem item : mArgs) {
			item.nailDownPositions();
		}
		super.nailDownPositions();
	}

	@Override
	public void resetPositions(int delta, IDocument doc) throws BadLocationException, BadPositionCategoryException {
		
		if (mBindingType!=null)
			mBindingType.resetPositions(delta, doc);
		for (TextItem item : mArgs) {
			item.resetPositions(delta, doc);
		}
		super.resetPositions(delta, doc);
	}

	@Override
	public boolean validateText(IDocument doc) throws BadLocationException
	{
		for (SourceItem item : mArgs) {
			if (!item.validateText(doc))
				return false;
		}
		
		if (mBindingType!=null && !mBindingType.validateText(doc))
			return false;
		
		return super.validateText(doc);
	}

	public List<TextItem> getArgs() {
		return mArgs;
	}

	
	
//	private static Set<String> mAssociatedTags;
//	static
//	{
//		mAssociatedTags=new HashSet<String>();
//		mAssociatedTags.add("ArrayElementType");
//		mAssociatedTags.add("Bindable");
//		mAssociatedTags.add("Deprecated");
//		mAssociatedTags.add("Effect");
//		mAssociatedTags.add("Embed");
//		mAssociatedTags.add("Event");
//		mAssociatedTags.add("ArrayElementType");
//		mAssociatedTags.add("ArrayElementType");
//		mAssociatedTags.add("ArrayElementType");
//		mAssociatedTags.add("ArrayElementType");
//		mAssociatedTags.add("ArrayElementType");
//		mAssociatedTags.add("ArrayElementType");
//		mAssociatedTags.add("ArrayElementType");
//		mAssociatedTags.add("ArrayElementType");
//		mAssociatedTags.add("ArrayElementType");
//		mAssociatedTags.add("ArrayElementType");
//	}
//	
//	public static boolean isTagAssociatedWithNextElement(String tagName)
//	{
//		
//	}
	
	
}
