package actionscriptinfocollector;

import java.util.ArrayList;
import java.util.List;

import org.antlr.runtime.ParserRuleReturnScope;
import org.antlr.runtime.Token;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPositionCategoryException;
import org.eclipse.jface.text.IDocument;

public class ClassRecord extends TopLevelItemRecord
{
	protected TextItem mExtends;
	protected List<TextItem> mImplements;
	protected TextItem mName;
	protected List<FunctionRecord> mFunctions;
	protected List<PropertyLine> mProperties;
	protected List<IncludeItem> mIncludes;
	protected List<ImportRecord> mImports;
	protected List<UseNamespaceItem> mUseNamespaceRecords;
	protected List<DefaultNamespaceItem> mDefaultNamespaceRecords;
	protected List<StaticInitializerRecord> mStaticInitializers;
	private boolean mIsClass;
	protected List<PropertyLine> mNameSpaces;
	protected TextItem mBodyStart;
	protected TextItem mBodyEnd;
	
	public ClassRecord()
	{
		mExtends=null;
		mImplements=new ArrayList<TextItem>();
		mName=null;
		mIsClass=true;
		mFunctions=new ArrayList<FunctionRecord>();
		mProperties=new ArrayList<PropertyLine>();
		mIncludes=new ArrayList<IncludeItem>();
		mImports=new ArrayList<ImportRecord>();
		mNameSpaces=new ArrayList<PropertyLine>();
		mUseNamespaceRecords=new ArrayList<UseNamespaceItem>();
		mDefaultNamespaceRecords=new ArrayList<DefaultNamespaceItem>();
		mStaticInitializers=new ArrayList<StaticInitializerRecord>();
	}
	
	public void addFunctionRecord(FunctionRecord func)
	{
		mFunctions.add(func);
	}
	
	public void addProperty(PropertyLine prop)
	{
		mProperties.add(prop);
	}
	
	public TextItem getName() {
		return mName;
	}

	public void setName(ParserRuleReturnScope name) {
		mName = new TreeTextItem(name);
	}

	public TextItem getExtends() {
		return mExtends;
	}
	public void setExtends(ParserRuleReturnScope extends1) {
		mExtends = new TreeTextItem(extends1);
	}
	public List<TextItem> getImplements() {
		return mImplements;
	}
	public void addImplements(ParserRuleReturnScope implemented)
	{
		mImplements.add(new TreeTextItem(implemented));
	}
	
	public ParserTextHandler getImplementsHandler()
	{
		return new ParserTextHandler()
		{
			public void addItem(ParserRuleReturnScope tree)
			{
				addImplements(tree);
			}
		};
	}

	@Override
	public String toString() {
		StringBuffer buffer=new StringBuffer();
		buffer.append("Class "+getName());
		if (getExtends()!=null)
			buffer.append(" extends "+getExtends());
		if (getImplements().size()>0)
		{
			buffer.append(" implements ");
			for (TextItem className : getImplements()) {
				buffer.append(className);
				buffer.append(", ");
			}
		}
		buffer.append("\n");
		buffer.append("Start: "+getStartPos()+" End: "+getEndPos()+"\n");
		
		for (PropertyLine prop : getProperties()) {
			buffer.append(prop.toString());
			buffer.append("\n");
		}
		
		for (FunctionRecord func : getFunctions()) {
			buffer.append(func.toString());
			buffer.append("\n");
		}
		
		buffer.append("End Class "+getName()+"\n");
		return buffer.toString();
	}

	public List<FunctionRecord> getFunctions() {
		return mFunctions;
	}
	
	public List<PropertyLine> getProperties() {
		return mProperties;
	}
	
	public void setBodyStart(Token token)
	{
		mBodyStart=new SimpleTextItem(token);
	}

	public void setBodyEnd(Token token)
	{
		mBodyEnd=new SimpleTextItem(token);
	}

	public void applyDocument(IDocument doc) throws BadLocationException, BadPositionCategoryException
	{
		super.applyDocument(doc);
		for (SourceItem item : getProperties()) {
			item.applyDocument(doc);
		}
		
		for (SourceItem item : getFunctions()) {
			item.applyDocument(doc);
		}

		if (mName!=null)
			mName.applyDocument(doc);
		
		if (mExtends!=null)
			mExtends.applyDocument(doc);
		
		for (SourceItem item : getImplements()) {
			item.applyDocument(doc);
		}
		
		for (IncludeItem item : getIncludes()) {
			item.applyDocument(doc);
		}
		for (ImportRecord item : getImports()) {
			item.applyDocument(doc);
		}
		for (PropertyLine item : getNamespaces()) {
			item.applyDocument(doc);
		}
		for (DefaultNamespaceItem item : getDefaultNamespaces()) {
			item.applyDocument(doc);
		}
		for (UseNamespaceItem item : getUseNamespaces()) {
			item.applyDocument(doc);
		}
		for (SourceItem item : mStaticInitializers)
		{
			item.applyDocument(doc);
		}
		mBodyStart.applyDocument(doc);
		mBodyEnd.applyDocument(doc);
	}
	
	@Override
	public void nailDownPositions()
	{
		for (SourceItem item : getProperties()) {
			item.nailDownPositions();
		}
		mBodyStart.nailDownPositions();
		mBodyEnd.nailDownPositions();
		if (mName!=null)
			mName.nailDownPositions();
		if (mExtends!=null)
			mExtends.nailDownPositions();
		for (FunctionRecord rec : getFunctions()) {
			rec.nailDownPositions();
		}
		for (SourceItem item : getImplements()) {
			item.nailDownPositions();
		}
		for (IncludeItem item : getIncludes()) {
			item.nailDownPositions();
		}
		for (ImportRecord item : getImports()) {
			item.nailDownPositions();
		}
		for (PropertyLine item : getNamespaces()) {
			item.nailDownPositions();
		}
		for (DefaultNamespaceItem item : getDefaultNamespaces()) {
			item.nailDownPositions();
		}
		for (UseNamespaceItem item : getUseNamespaces()) {
			item.nailDownPositions();
		}
		for (SourceItem item : mStaticInitializers)
		{
			item.nailDownPositions();
		}
		super.nailDownPositions();
	}
	
	

	@Override
	public void resetPositions(int delta, IDocument doc) throws BadLocationException, BadPositionCategoryException {
		
		for (SourceItem item : getProperties()) {
			item.resetPositions(delta, doc);
		}
		mBodyStart.resetPositions(delta, doc);
		mBodyEnd.resetPositions(delta, doc);
		if (mName!=null)
			mName.resetPositions(delta, doc);
		if (mExtends!=null)
			mExtends.resetPositions(delta, doc);
		for (FunctionRecord rec : getFunctions()) {
			rec.resetPositions(delta, doc);
		}
		for (SourceItem item : getImplements()) {
			item.resetPositions(delta, doc);
		}
		for (IncludeItem item : getIncludes()) {
			item.resetPositions(delta, doc);
		}
		for (ImportRecord item : getImports()) {
			item.resetPositions(delta, doc);
		}
		for (PropertyLine item : getNamespaces()) {
			item.resetPositions(delta, doc);
		}
		for (DefaultNamespaceItem item : getDefaultNamespaces()) {
			item.resetPositions(delta, doc);
		}
		for (UseNamespaceItem item : getUseNamespaces()) {
			item.resetPositions(delta, doc);
		}
		for (SourceItem item : mStaticInitializers)
		{
			item.resetPositions(delta, doc);
		}
		super.resetPositions(delta, doc);
	}

	public void addInclude(IncludeItem item)
	{
		mIncludes.add(item); 
	}
	
	public void addImport(ImportRecord item)
	{
		mImports.add(item);
	}
	
	public boolean isClass()
	{
		return mIsClass;
	}
	
	public void setIsClass(boolean isClass)
	{
		mIsClass=isClass;
	}
	
	public void addNamespace(PropertyLine ns)
	{
		mNameSpaces.add(ns);
	}
	
	public List<PropertyLine> getNamespaces()
	{
		return mNameSpaces;
	}

	public List<IncludeItem> getIncludes() {
		return mIncludes;
	}

	public List<ImportRecord> getImports() {
		return mImports;
	}
	
	public TextItem getBodyStart()
	{
		return mBodyStart;
	}
	
	public void addUseNamespace(UseNamespaceItem ns)
	{
		mUseNamespaceRecords.add(ns);
	}
	
	public List<UseNamespaceItem> getUseNamespaces()
	{
		return mUseNamespaceRecords;
	}
	
	public void addDefaultNamespace(DefaultNamespaceItem ns)
	{
		mDefaultNamespaceRecords.add(ns);
	}
	
	public List<DefaultNamespaceItem> getDefaultNamespaces()
	{
		return mDefaultNamespaceRecords;
	}
	
	@Override
	public boolean validateText(IDocument doc) throws BadLocationException
	{
		if (mBodyStart!=null && !mBodyStart.validateText(doc))
			return false;
		
		if (mExtends!=null && !mExtends.validateText(doc))
			return false;
		
		if (mName!=null && !mName.validateText(doc))
			return false;
		
		for (SourceItem item : mProperties) {
			if (!item.validateText(doc))
				return false;
		}
		for (SourceItem item : mDefaultNamespaceRecords) {
			if (!item.validateText(doc))
				return false;
		}
		for (SourceItem item : mFunctions) {
			if (!item.validateText(doc))
				return false;
		}
		for (SourceItem item : mImplements) {
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
		for (SourceItem item : mMetadataItems) {
			if (!item.validateText(doc))
				return false;
		}
		for (SourceItem item : mModifiers) {
			if (!item.validateText(doc))
				return false;
		}
		for (SourceItem item : mUseNamespaceRecords) {
			if (!item.validateText(doc))
				return false;
		}
		for (SourceItem item : mNameSpaces) {
			if (!item.validateText(doc))
				return false;
		}
		for (SourceItem item : mStaticInitializers) {
			if (!item.validateText(doc))
				return false;
		}
		return super.validateText(doc);
	}

	@Override
	public boolean captureObject(IDocument doc, Object o, int lineNumber) throws BadLocationException
	{
		for (SourceItem item : mProperties) {
			if (item.captureObject(doc, o, lineNumber))
				return true;
		}
		for (SourceItem item : mDefaultNamespaceRecords) {
			if (item.captureObject(doc, o, lineNumber))
				return true;
		}
		for (SourceItem item : mFunctions) {
			if (item.captureObject(doc, o, lineNumber))
				return true;
		}
		for (SourceItem item : mImplements) {
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
		for (SourceItem item : mMetadataItems) {
			if (item.captureObject(doc, o, lineNumber))
				return true;
		}
		for (SourceItem item : mModifiers) {
			if (item.captureObject(doc, o, lineNumber))
				return true;
		}
		for (SourceItem item : mUseNamespaceRecords) {
			if (item.captureObject(doc, o, lineNumber))
				return true;
		}
		for (SourceItem item : mNameSpaces) {
			if (item.captureObject(doc, o, lineNumber))
				return true;
		}
		for (SourceItem item : mStaticInitializers) {
			if (item.captureObject(doc, o, lineNumber))
				return true;
		}
		
		return super.captureObject(doc, o, lineNumber);
	}

	@Override
	public List<ObjectPositionHolder> getAttachedObjects()
	{
		List<ObjectPositionHolder> objects=new ArrayList<ObjectPositionHolder>();
		for (SourceItem item : mProperties) {
			objects.addAll(item.getAttachedObjects());
		}
		for (SourceItem item : mDefaultNamespaceRecords) {
			objects.addAll(item.getAttachedObjects());
		}
		for (SourceItem item : mFunctions) {
			objects.addAll(item.getAttachedObjects());
		}
		for (SourceItem item : mImplements) {
			objects.addAll(item.getAttachedObjects());
		}
		for (SourceItem item : mImports) {
			objects.addAll(item.getAttachedObjects());
		}
		for (SourceItem item : mIncludes) {
			objects.addAll(item.getAttachedObjects());
		}
		for (SourceItem item : mMetadataItems) {
			objects.addAll(item.getAttachedObjects());
		}
		for (SourceItem item : mModifiers) {
			objects.addAll(item.getAttachedObjects());
		}
		for (SourceItem item : mUseNamespaceRecords) {
			objects.addAll(item.getAttachedObjects());
		}
		for (SourceItem item : mNameSpaces) {
			objects.addAll(item.getAttachedObjects());
		}
		for (SourceItem item : mStaticInitializers) {
			objects.addAll(item.getAttachedObjects());
		}
		
		objects.addAll(super.getAttachedObjects());
		return objects;
	}

	public TextItem getBodyEnd()
	{
		return mBodyEnd;
	}

	public void addStaticInitializer(StaticInitializerRecord item)
	{
		mStaticInitializers.add(item);
	}

	public List<StaticInitializerRecord> getStaticInitializers() {
		return mStaticInitializers;
	}
	
}

