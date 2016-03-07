package actionscriptinfocollector;

import java.util.ArrayList;
import java.util.List;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.Token;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPositionCategoryException;
import org.eclipse.jface.text.IDocument;

public class ASCollector {
	
	private List<ImportRecord> mImports;
	private List<ClassRecord> mClassRecords;
	private List<FunctionRecord> mFunctionRecords;
	private List<PropertyLine> mPropertyRecords;
	protected List<UseNamespaceItem> mUseNamespaceRecords;
	protected List<DefaultNamespaceItem> mDefaultNamespaceRecords;
	protected List<PropertyLine> mDefinedNamespaceRecords;
//	private CommonTokenStream mRawTokens;
//	private IDocument mDoc;
	private List<IncludeItem> mIncludes;
	private TextItem mPackageOpen; //open brace of package
	private TextItem mPackageClose; //close brace of package
	private boolean mContainsConditionalMembers;
	private SourceItem mBounds;
	
//	private List<MetadataItem> mMetadataTags;
	public ASCollector()
	{
		mClassRecords=new ArrayList<ClassRecord>();
		mImports=new ArrayList<ImportRecord>();
//		mDoc=doc;
		mIncludes=new ArrayList<IncludeItem>();
		mUseNamespaceRecords=new ArrayList<UseNamespaceItem>();
		mDefaultNamespaceRecords=new ArrayList<DefaultNamespaceItem>();
//		mMetadataTags=new ArrayList<MetadataItem>();
		mDefinedNamespaceRecords=new ArrayList<PropertyLine>();
		mFunctionRecords=new ArrayList<FunctionRecord>();
		mPropertyRecords=new ArrayList<PropertyLine>();
		mPackageOpen=null;
		mPackageClose=null;
		mBounds=new SourceItem();
		mBounds.setEndPos(0);
		mBounds.setStartPos(Integer.MAX_VALUE);
	}
	
	private void updateBounds(SourceItem item)
	{
		if (item==null)
			return;
		
		if (item.getEarliestRelatedPos()<mBounds.getStartPos())
			mBounds.setStartPos(item.getEarliestRelatedPos());
		if (item.getPostEndPos()>mBounds.getEndPos())
			mBounds.setEndPos(item.getPostEndPos());
	}
	
//	private void checkBounds(TopLevelItemRecord item)
//	{
//		updateBounds(item.getComment());
//		updateBounds(item);
//	}
//	
	public static List<Exception> parse(IDocument doc, List<ASCollector> collectors)
	{
		String source=doc.get();
		source=Utilities.convertCarriageReturnsToLineFeeds(source);
		ANTLRStringStream stream=new ANTLRStringStream(source);
		ASCollectorLexer l2=new ASCollectorLexer(stream);
		CommonTokenStream rawTokens=new CommonTokenStream(l2);
		ASCollectorParser p2=new ASCollectorParser(collectors, rawTokens);
		
		try {
			p2.fileContents();
		} catch (RecognitionException e) {
			e.printStackTrace();
			return p2.getParseErrors();
		}
		
		if (p2.getParseErrors()!=null && p2.getParseErrors().size()>0)
			return p2.getParseErrors();
		if (collectors.size()>0)
			collectors.get(0).mContainsConditionalMembers=p2.containsConditionalMembers();
		return null;
	}
	
	public boolean containsConditionalMembers()
	{
		return mContainsConditionalMembers;
	}
	
	public void applyDocument(IDocument doc) throws BadLocationException, BadPositionCategoryException
	{
		if (mBounds.getStartPos()>doc.getLength())
			mBounds.setStartPos(0);
		mBounds.applyDocument(doc);
		for (SourceItem item : mImports) {
			item.applyDocument(doc);
		}
		
		for (SourceItem item : mIncludes) {
			item.applyDocument(doc);
		}
		
		for (ClassRecord cRec : mClassRecords) {
			cRec.applyDocument(doc);
		}

		for (FunctionRecord cRec : mFunctionRecords) {
			cRec.applyDocument(doc);
		}

		for (PropertyLine cRec : mPropertyRecords) {
			cRec.applyDocument(doc);
		}

		for (DefaultNamespaceItem cRec : mDefaultNamespaceRecords) {
			cRec.applyDocument(doc);
		}
		for (UseNamespaceItem cRec : mUseNamespaceRecords) {
			cRec.applyDocument(doc);
		}
		for (PropertyLine cRec : mDefinedNamespaceRecords) {
			cRec.applyDocument(doc);
		}
	}
	
	public void addClass(ClassRecord record)
	{
		updateBounds(record);
		mClassRecords.add(record);
	}

	public List<ClassRecord> getClassRecords()
	{
		return mClassRecords;
	}

	public void addFunctionRecord(FunctionRecord record)
	{
		updateBounds(record);
		mFunctionRecords.add(record);
	}

	public List<FunctionRecord> getFunctionRecords()
	{
		return mFunctionRecords;
	}

	public void addPropertyRecord(PropertyLine record)
	{
		updateBounds(record);
		mPropertyRecords.add(record);
	}

	public List<PropertyLine> getPropertyRecords()
	{
		return mPropertyRecords;
	}

	public void addImport(ImportRecord iRec)
	{
		updateBounds(iRec);
		mImports.add(iRec);
	}

	public List<ImportRecord> getImports() {
		return mImports;
	}
	
	public void addInclude(IncludeItem item)
	{
		updateBounds(item);
		mIncludes.add(item);
	}

//	public void addMetadataItem(MetadataItem item)
//	{
//		mMetadataTags.add(item);
//	}
//
	public List<IncludeItem> getIncludes()
	{
		return mIncludes;
	}

	public void addUseNamespace(UseNamespaceItem ns)
	{
		updateBounds(ns);
		mUseNamespaceRecords.add(ns);
	}
	
	public List<UseNamespaceItem> getUseNamespaces()
	{
		return mUseNamespaceRecords;
	}
	
	public void addDefaultNamespace(DefaultNamespaceItem ns)
	{
		updateBounds(ns);
		mDefaultNamespaceRecords.add(ns);
	}
	
	public List<DefaultNamespaceItem> getDefaultNamespaces()
	{
		return mDefaultNamespaceRecords;
	}

	public void addDefinedNamespace(PropertyLine ns)
	{
		updateBounds(ns);
		mDefinedNamespaceRecords.add(ns);
	}
	
	public List<PropertyLine> getDefinedNamespaces()
	{
		return mDefinedNamespaceRecords;
	}
	
	public boolean validateText(IDocument doc) throws BadLocationException
	{
		for (SourceItem item : mClassRecords) {
			if (!item.validateText(doc))
				return false;
		}
		
		for (SourceItem item : mFunctionRecords) {
			if (!item.validateText(doc))
				return false;
		}
		
		for (SourceItem item : mPropertyRecords) {
			if (!item.validateText(doc))
				return false;
		}
		
		for (SourceItem item : mDefaultNamespaceRecords) {
			if (!item.validateText(doc))
				return false;
		}
		
		for (SourceItem item : mDefinedNamespaceRecords) {
			if (!item.validateText(doc))
				return false;
		}
		
		for (SourceItem item : mImports) {
			if (!item.validateText(doc))
				return false;
		}
		
		for (SourceItem item : mIncludes) {
			if (!item.validateText(doc))
				return false;
		}
		
		for (SourceItem item : mUseNamespaceRecords) {
			if (!item.validateText(doc))
				return false;
		}
		
		return true;
	}
	
	public boolean captureObject(IDocument doc, Object o, int lineNumber) throws BadLocationException
	{
		for (SourceItem item : mClassRecords) {
			if (item.captureObject(doc, o, lineNumber))
				return true;
		}
		
		for (SourceItem item : mFunctionRecords) {
			if (item.captureObject(doc, o, lineNumber))
				return true;
		}
		
		for (SourceItem item : mPropertyRecords) {
			if (item.captureObject(doc, o, lineNumber))
				return true;
		}
		
		for (SourceItem item : mDefaultNamespaceRecords) {
			if (item.captureObject(doc, o, lineNumber))
				return true;
		}
		
		for (SourceItem item : mDefinedNamespaceRecords) {
			if (item.captureObject(doc, o, lineNumber))
				return true;
		}
		
		for (SourceItem item : mImports) {
			if (item.captureObject(doc, o, lineNumber))
				return true;
		}
		
		for (SourceItem item : mIncludes) {
			if (item.captureObject(doc, o, lineNumber))
				return true;
		}
		
		for (SourceItem item : mUseNamespaceRecords) {
			if (item.captureObject(doc, o, lineNumber))
				return true;
		}
		
		return false;
	}
	
	public List<ObjectPositionHolder> getCapturedObjects()
	{
		List<ObjectPositionHolder> objects=new ArrayList<ObjectPositionHolder>();
		for (SourceItem item : mClassRecords) {
			objects.addAll(item.getAttachedObjects());
		}
		
		for (SourceItem item : mFunctionRecords) {
			objects.addAll(item.getAttachedObjects());
		}
		
		for (SourceItem item : mPropertyRecords) {
			objects.addAll(item.getAttachedObjects());
		}
		
		for (SourceItem item : mDefaultNamespaceRecords) {
			objects.addAll(item.getAttachedObjects());
		}
		
		for (SourceItem item : mDefinedNamespaceRecords) {
			objects.addAll(item.getAttachedObjects());
		}
		
		for (SourceItem item : mImports) {
			objects.addAll(item.getAttachedObjects());
		}
		
		for (SourceItem item : mIncludes) {
			objects.addAll(item.getAttachedObjects());
		}
		
		for (SourceItem item : mUseNamespaceRecords) {
			objects.addAll(item.getAttachedObjects());
		}
		
		return objects;
	}

	public void nailDownPositions() {
		
		mBounds.nailDownPositions();
		for (SourceItem item : mClassRecords) {
			item.nailDownPositions();
		}
		
		for (SourceItem item : mFunctionRecords) {
			item.nailDownPositions();
		}
		
		for (SourceItem item : mPropertyRecords) {
			item.nailDownPositions();
		}
		
		for (SourceItem item : mDefaultNamespaceRecords) {
			item.nailDownPositions();
		}
		
		for (SourceItem item : mDefinedNamespaceRecords) {
			item.nailDownPositions();
		}
		
		for (SourceItem item : mImports) {
			item.nailDownPositions();
		}
		
		for (SourceItem item : mIncludes) {
			item.nailDownPositions();
		}
		
		for (SourceItem item : mUseNamespaceRecords) {
			item.nailDownPositions();
		}
	}
	
	public void setPackageOpen(Token t)
	{
		mPackageOpen=new SimpleTextItem(t);
	}
	
	public TextItem getPackageOpen()
	{
		return mPackageOpen;
	}

	public void setPackageClose(Token t)
	{
		mPackageClose=new SimpleTextItem(t);
	}
	
	public TextItem getPackageClose()
	{
		return mPackageClose;
	}
	
	public int getStartPos()
	{
		return mBounds.getStartPos();
	}
	
	public int getEndPos()
	{
		return mBounds.getEndPos();
	}
}
