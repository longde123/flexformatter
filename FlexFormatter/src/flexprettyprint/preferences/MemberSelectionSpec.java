package flexprettyprint.preferences;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;

import actionscriptinfocollector.AntlrUtilities;
import actionscriptinfocollector.DeclRecord;
import actionscriptinfocollector.FunctionRecord;
import actionscriptinfocollector.MetadataItem;
import actionscriptinfocollector.PropertyLine;
import actionscriptinfocollector.TextItem;
import actionscriptinfocollector.TopLevelItemRecord;
import flexasrearrangecodecommand.handlers.ASRearranger;
import flexasrearrangecodecommand.handlers.ASRearranger.PropertySortHolder;
import flexasrearrangecodecommand.preferences.PreferenceConstants;
import flexprettyprintcommand.Activator;

public class MemberSelectionSpec implements ISectionItem
{
	private int mUseFlags; //which fields are looked at
	private int mIncludeAttrs; //flags to include
	private int mExcludeAttrs; //flags to exclude
	private boolean mInvertNamespaces;
	private boolean mInvertNames;
	private boolean mInvertMetatags;
	private List<String> mNamespaces;
	private List<String> mNames;
	private List<String> mObjectTypes;
	private List<String> mFirstParameterNames;
	private List<String> mFirstParameterTypes;
	private List<String> mMetaTags;
//	private boolean mNoObjectType;
	
	private long mUniqueID;
	private String mPrintName;
	private boolean mIncludeGetters; //for properties
	private int mPropertyHeaderStyle; //for properties
	private boolean mIncludeProperties; //for getter/setter functions
	
	private int mPreselectPriority; //0 means don't preselect; 1 or higher is the order of selection
	private int mFunctionTypeFlags;
	private boolean mIsFunction; //other is field
	private int mSortFlags;
	private int mBlankLinesBefore;
	
	public static final int Use_Visibility=0x1;
	public static final int Use_Static=0x2;
	public static final int Use_Final=0x4;
	public static final int Use_Native=0x8;
	public static final int Use_Override=0x10;
	public static final int Use_Names=0x20;
	public static final int Use_Namespaces=0x40;
	public static final int Use_FunctionType=0x80;
	public static final int Use_ObjectType=0x100;
	public static final int Use_ParameterType=0x200;
	public static final int Use_ParameterName=0x400;
	public static final int Use_MetaTag=0x800;
	public static final int Use_Const=0x1000;
	
	public static final int FuncType_Getter=0x1;
	public static final int FuncType_Setter=0x2;
	public static final int FuncType_Constructor=0x4;
	public static final int FuncType_Other=0x8;

	public static final int Sort_On=0x1;
	public static final int Sort_CaseSensitive=0x2;
	public static final int Associate_Getters=0x4;
	public static final int Sort_ByType=0x80;
	
	public MemberSelectionSpec()
	{
		mObjectTypes=new ArrayList<String>();
		mFirstParameterNames=new ArrayList<String>();
		mFirstParameterTypes=new ArrayList<String>();
		mMetaTags=new ArrayList<String>();
		mNames=new ArrayList<String>();
		mNamespaces=new ArrayList<String>();
		mPrintName="";
		mIsFunction=true;
		mSortFlags=Sort_On;
		mUniqueID=System.currentTimeMillis();
		mIncludeGetters=false;
		mBlankLinesBefore=1;
		mPropertyHeaderStyle=PreferenceConstants.PropertyHeaders_None;
		mPreselectPriority=0;
	}
	
	public MemberSelectionSpec copy()
	{
		MemberSelectionSpec spec=new MemberSelectionSpec();
		spec.mExcludeAttrs=mExcludeAttrs;
		spec.mFunctionTypeFlags=mFunctionTypeFlags;
		spec.mIncludeAttrs=mIncludeAttrs;
		spec.mIncludeProperties=mIncludeProperties;
		spec.mInvertNames=mInvertNames;
		spec.mInvertMetatags=mInvertMetatags;
		spec.mInvertNamespaces=mInvertNamespaces;
		spec.mIsFunction=mIsFunction;
		spec.mNames=new ArrayList<String>();
		spec.mNames.addAll(mNames);
		spec.mNamespaces=new ArrayList<String>();
		spec.mNamespaces.addAll(mNamespaces);
		spec.mSortFlags=mSortFlags;
		spec.mUseFlags=mUseFlags;
		spec.mPrintName=mPrintName;
		spec.mUniqueID=mUniqueID;
		spec.mIncludeGetters=mIncludeGetters;
		spec.mBlankLinesBefore=mBlankLinesBefore;
		spec.mPropertyHeaderStyle=mPropertyHeaderStyle;
		spec.mPreselectPriority=mPreselectPriority;
		spec.mObjectTypes=new ArrayList<String>();
		spec.mObjectTypes.addAll(mObjectTypes);
		spec.mFirstParameterNames=new ArrayList<String>();
		spec.mFirstParameterNames.addAll(mFirstParameterNames);
		spec.mFirstParameterTypes=new ArrayList<String>();
		spec.mFirstParameterTypes.addAll(mFirstParameterTypes);
		spec.mMetaTags=new ArrayList<String>();
		spec.mMetaTags.addAll(mMetaTags);
		return spec;
	}
	
	public int getUseFlags() {
		return mUseFlags;
	}
	
	@Override
	public String toString() {
		if (mPrintName.length()>0)
			return mPrintName;
		
		StringBuffer buffer=new StringBuffer();
		if ((mUseFlags & Use_Visibility)!=0)
		{
			if ((mIncludeAttrs & TopLevelItemRecord.ASDoc_Public)!=0)
				addItem(buffer, "public", "/");
			if ((mIncludeAttrs & TopLevelItemRecord.ASDoc_Protected)!=0)
				addItem(buffer, "protected", "/");
			if ((mIncludeAttrs & TopLevelItemRecord.ASDoc_Private)!=0)
				addItem(buffer, "private", "/");
			if ((mIncludeAttrs & TopLevelItemRecord.ASDoc_Internal)!=0)
				addItem(buffer, "internal", "/");
			if ((mExcludeAttrs & TopLevelItemRecord.ASDoc_Public)!=0)
				addItem(buffer, "!public", "/");
			if ((mExcludeAttrs & TopLevelItemRecord.ASDoc_Protected)!=0)
				addItem(buffer, "!protected", "/");
			if ((mExcludeAttrs & TopLevelItemRecord.ASDoc_Private)!=0)
				addItem(buffer, "!private", "/");
			if ((mExcludeAttrs & TopLevelItemRecord.ASDoc_Internal)!=0)
				addItem(buffer, "!internal", "/");
		}
		
		addItem(buffer, "", " ");
		
		//add other modifiers
		if ((mUseFlags & Use_Final)!=0)
		{
			if ((mIncludeAttrs & TopLevelItemRecord.ASDoc_Final)!=0)
				addItem(buffer, "final", "/");
			if ((mExcludeAttrs & TopLevelItemRecord.ASDoc_Final)!=0)
				addItem(buffer, "!final", "/");
		}
		if ((mUseFlags & Use_Const)!=0)
		{
			if ((mIncludeAttrs & TopLevelItemRecord.ASDoc_Const)!=0)
				addItem(buffer, "const", "/");
			if ((mExcludeAttrs & TopLevelItemRecord.ASDoc_Const)!=0)
				addItem(buffer, "!const", "/");
		}
		if ((mUseFlags & Use_Static)!=0)
		{
			if ((mIncludeAttrs & TopLevelItemRecord.ASDoc_Static)!=0)
				addItem(buffer, "static", "/");
			if ((mExcludeAttrs & TopLevelItemRecord.ASDoc_Static)!=0)
				addItem(buffer, "!static", "/");
		}
		if ((mUseFlags & Use_Override)!=0)
		{
			if ((mIncludeAttrs & TopLevelItemRecord.ASDoc_Override)!=0)
				addItem(buffer, "override", "/");
			if ((mExcludeAttrs & TopLevelItemRecord.ASDoc_Override)!=0)
				addItem(buffer, "!override", "/");
		}
		if ((mUseFlags & Use_Native)!=0)
		{
			if ((mIncludeAttrs & TopLevelItemRecord.ASDoc_Native)!=0)
				addItem(buffer, "native", "/");
			if ((mExcludeAttrs & TopLevelItemRecord.ASDoc_Native)!=0)
				addItem(buffer, "!native", "/");
		}
		
		addItem(buffer, isFunction() ? "Functions" : "Fields", " ");
		
		if ((mUseFlags & Use_FunctionType)!=0)
		{
			buffer.append(" (");
			if ((mFunctionTypeFlags & FuncType_Constructor)!=0)
				addItem(buffer, "constructor", "/");
			if ((mFunctionTypeFlags & FuncType_Getter)!=0)
				addItem(buffer, "get", "/");
			if ((mFunctionTypeFlags & FuncType_Setter)!=0)
				addItem(buffer, "set", "/");
			if ((mFunctionTypeFlags & FuncType_Other)!=0)
				addItem(buffer, "normal", "/");
			buffer.append(")");
		}
		
		//names and namespaces
		if ((mUseFlags & Use_Names)!=0)
		{
			addItem(buffer, "name=", " ");
			String prefix=(mInvertNames ? "!" : "");
			for (String name : mNames) {
				addItem(buffer, prefix+name, ",");
			}
		}
		
		if ((mUseFlags & Use_Namespaces)!=0)
		{
			addItem(buffer, "namespace=", " ");
			if ((mExcludeAttrs & TopLevelItemRecord.ASDoc_Namespace)!=0)
				addItem(buffer, "<none>", " ");
			else
			{
				String prefix=(mInvertNamespaces ? "!" : "");
				for (String name : mNamespaces) {
					
					addItem(buffer, prefix+name, ",");
				}
			}
		}
		
		if ((mUseFlags & Use_ObjectType)!=0)
		{
			addItem(buffer, "objectType=", " ");
			for (String name : mObjectTypes) {
				addItem(buffer, name, ",");
			}
		}
		
		if ((mUseFlags & Use_ParameterType)!=0)
		{
			addItem(buffer, "parameterType=", " ");
			for (String name : mFirstParameterTypes) {
				addItem(buffer, name, ",");
			}
		}
		
		if ((mUseFlags & Use_ParameterName)!=0)
		{
			addItem(buffer, "parameterName=", " ");
			for (String name : mFirstParameterNames) {
				addItem(buffer, name, ",");
			}
		}
		
		if ((mUseFlags & Use_MetaTag)!=0)
		{
			addItem(buffer, "metatag=", " ");
			for (String name : mMetaTags) {
				String prefix=(mInvertMetatags ? "!" : "");
				addItem(buffer, prefix+name, ",");
			}
		}

		if ((mSortFlags & Sort_On)!=0)
		{
			addItem(buffer, "Sort", " ");
			if ((mSortFlags & Sort_CaseSensitive)!=0)
			{
				addItem(buffer, "Case-Sensitive", " ");
			}
			if ((mSortFlags & Sort_ByType)!=0)
			{
				addItem(buffer, "Group-by-type", " ");
			}
		}
		
		if (!mIsFunction && mIncludeGetters)
			addItem(buffer, "(Include Getters)", " ");
		if (mIsFunction && mIncludeProperties)
			addItem(buffer, "(Include Properties)", " ");

		if ((mSortFlags & Associate_Getters)!=0 && mIncludeGetters && !mIsFunction)
		{
			addItem(buffer, "Associate Getters", " ");
		}
		if ((mSortFlags & Associate_Getters)!=0 && mIncludeProperties && mIsFunction)
		{
			addItem(buffer, "Associate Getters", " ");
		}
		
		if (mPreselectPriority>0)
		{
			addItem(buffer, "(Priority="+mPreselectPriority+")", " ");
		}
		
		return buffer.toString();
	}
	
	private void addItem(StringBuffer buffer, String text, String separator)
	{
		if (buffer.length()>0 && !AntlrUtilities.isASWhitespace(buffer.charAt(buffer.length()-1)) && buffer.charAt(buffer.length()-1)!='=' && buffer.charAt(buffer.length()-1)!=',' && buffer.charAt(buffer.length()-1)!='(')
			buffer.append(separator);
		buffer.append(text);
	}

	public void setUseFlags(int useFlags) {
		mUseFlags = useFlags;
	}

	public int getIncludeAttrs() {
		return mIncludeAttrs;
	}

	public void setIncludeAttrs(int includeAttrs) {
		mIncludeAttrs = includeAttrs;
	}

	public int getExcludeAttrs() {
		return mExcludeAttrs;
	}

	public void setExcludeAttrs(int excludeAttrs) {
		mExcludeAttrs = excludeAttrs;
	}

	public boolean isInvertNamespaces() {
		return mInvertNamespaces;
	}

	public void setInvertNamespaces(boolean invertNamespaces) {
		mInvertNamespaces = invertNamespaces;
	}
	
	public boolean isInvertMetatags() {
		return mInvertMetatags;
	}

	public void setInvertMetatags(boolean invertMetatags) {
		mInvertMetatags = invertMetatags;
	}

	public boolean isInvertNames() {
		return mInvertNames;
	}

	public void setInvertNames(boolean invertNames) {
		mInvertNames = invertNames;
	}

	public List<String> getNamespaces() {
		return mNamespaces;
	}

	public void setNamespaces(List<String> namespaces) {
		mNamespaces = namespaces;
	}

	public List<String> getNames() {
		return mNames;
	}

	public void setNames(List<String> names) {
		mNames = names;
	}

	public List<String> getObjectTypes() {
		return mObjectTypes;
	}

	public void setObjectTypes(List<String> types) {
		mObjectTypes=types;
	}

	public List<String> getFirstArgNames() {
		return mFirstParameterNames;
	}

	public void setFirstArgNames(List<String> names) {
		mFirstParameterNames= names;
	}

	public List<String> getMetatags() {
		return mMetaTags;
	}

	public void setMetatags(List<String> tags) {
		mMetaTags= tags;
	}

	public List<String> getFirstArgTypes() {
		return mFirstParameterTypes;
	}

	public void setFirstArgTypes(List<String> types) {
		mFirstParameterTypes=types;
	}

	public int getFunctionTypeFlags() {
		return mFunctionTypeFlags;
	}

	public void setFunctionTypeFlags(int functionTypeFlags) {
		mFunctionTypeFlags = functionTypeFlags;
	}

	public boolean isFunction() {
		return mIsFunction;
	}

	public void setFunction(boolean isFunction) {
		mIsFunction = isFunction;
	}

	public int getSortFlags() {
		return mSortFlags;
	}

	public void setSortFlags(int sortFlags) {
		mSortFlags = sortFlags;
	}

	public boolean matches(TopLevelItemRecord record, String className)
	{
		if (mIsFunction && !(record instanceof FunctionRecord))
			return false;
		
		if (!mIsFunction)
		{ 
			if (record instanceof FunctionRecord)
			{
				FunctionRecord func=(FunctionRecord)record;
				if (mIncludeGetters && (func.getType()==FunctionRecord.Type_Getter || func.getType()==FunctionRecord.Type_Setter))
				{
					//then we keep going to see if this item matches
				}
				else
					return false;
			}
		}
		
		if ((mUseFlags & Use_Visibility)!=0)
		{
			boolean matches=false;
			if ((TopLevelItemRecord.ASDoc_Private & mIncludeAttrs)!=0 && (record.getModifierFlags() & TopLevelItemRecord.ASDoc_Private)!=0)
			{
				matches=true;
			}
			else if ((TopLevelItemRecord.ASDoc_Public & mIncludeAttrs)!=0 && (record.getModifierFlags() & TopLevelItemRecord.ASDoc_Public)!=0)
			{
				matches=true;
			}
			else if ((TopLevelItemRecord.ASDoc_Protected & mIncludeAttrs)!=0 && (record.getModifierFlags() & TopLevelItemRecord.ASDoc_Protected)!=0)
			{
				matches=true;
			}
			else if ((TopLevelItemRecord.ASDoc_Internal & mIncludeAttrs)!=0 && (record.getModifierFlags() & TopLevelItemRecord.ASDoc_Internal)!=0)
			{
				matches=true;
			}
			if (!matches)
				return false;
		}
		
		if ((mUseFlags & Use_Final)!=0)
		{
			boolean matches=false;
			if ((TopLevelItemRecord.ASDoc_Final & mIncludeAttrs)!=0 && (record.getModifierFlags() & TopLevelItemRecord.ASDoc_Final)!=0)
			{
				matches=true;
			}
			else if ((TopLevelItemRecord.ASDoc_Final & mExcludeAttrs)!=0 && (record.getModifierFlags() & TopLevelItemRecord.ASDoc_Final)==0)
			{
				matches=true;
			}
			if (!matches)
				return false;
		}		
		
		if ((mUseFlags & Use_FunctionType)!=0 && (record instanceof FunctionRecord))
		{
			boolean matches=false;
			FunctionRecord func=(FunctionRecord)record;
			if ((func.getType()==FunctionRecord.Type_Getter) && (mFunctionTypeFlags & FuncType_Getter)!=0)
			{
				matches=true;
			}
			else if ((func.getType()==FunctionRecord.Type_Setter) && (mFunctionTypeFlags & FuncType_Setter)!=0)
			{
				matches=true;
			}
			else if (func.getType()==FunctionRecord.Type_Normal)
			{
				if (func.getName().getText().equals(className) && (mFunctionTypeFlags & FuncType_Constructor)!=0)
				{
					matches=true;
				}
				else if (!func.getName().getText().equals(className) && (mFunctionTypeFlags & FuncType_Other)!=0)
				{
					matches=true;
				} 
			}
			if (!matches)
				return false;
		}		

		if ((mUseFlags & Use_ObjectType)!=0)
		{
			boolean matches=false;
			List<String> typeNames=new ArrayList<String>();
			if (record instanceof FunctionRecord)
			{
				TextItem returnType=((FunctionRecord)record).getReturnType();
				if (returnType!=null)
					typeNames.add(returnType.getText());
			}
			else if (record instanceof PropertyLine)
			{
				for (DeclRecord decl:((PropertyLine)record).getProperties())
				{
					if (decl.getType()!=null)
						typeNames.add(decl.getType().getText());
				}
			}
			
			outerloop: for (String typeRegex : mObjectTypes) {
				Pattern p=Pattern.compile(typeRegex);
				for (String itemName : typeNames) {
					if (p.matcher(itemName).matches())
					{
						//if we found a match, then we set our match to true or false depending on whether we are including/excluding the name
						matches=true;
						break outerloop;
					}
				}
			}
			
			if (!matches)
				return false;
		}
		
		if ((mUseFlags & Use_ParameterName)!=0 && (record instanceof FunctionRecord))
		{
			boolean matches=false;
			List<DeclRecord> parms=((FunctionRecord)record).getParameters();
			if (parms.size()==1)
			{
				TextItem nameItem=parms.get(0).getName();
				if (nameItem!=null)
				{
					String parmName=nameItem.getText();
					for (String typeRegex : mFirstParameterNames)
					{
						Pattern p=Pattern.compile(typeRegex);
						if (p.matcher(parmName).matches())
						{
							//if we found a match, then we set our match to true or false depending on whether we are including/excluding the name
							matches=true;
							break;
						}
					}
				}
			}
			
			if (!matches)
				return false;
		}
		
		if ((mUseFlags & Use_ParameterType)!=0 && (record instanceof FunctionRecord))
		{
			boolean matches=false;
			List<DeclRecord> parms=((FunctionRecord)record).getParameters();
			if (parms.size()==1)
			{
				TextItem typeItem=parms.get(0).getType();
				if (typeItem!=null)
				{
					String parmType=typeItem.getText();
					for (String typeRegex : mFirstParameterTypes)
					{
						Pattern p=Pattern.compile(typeRegex);
						if (p.matcher(parmType).matches())
						{
							//if we found a match, then we set our match to true or false depending on whether we are including/excluding the name
							matches=true;
							break;
						}
					}
				}
			}
			
			if (!matches)
				return false;
		}
		
		if ((mUseFlags & Use_Names)!=0)
		{
			boolean matches=false;
			List<String> names=new ArrayList<String>();
			if (record instanceof FunctionRecord)
				names.add(((FunctionRecord)record).getName().getText());
			else if (record instanceof PropertyLine)
			{
				for (DeclRecord decl:((PropertyLine)record).getProperties())
				{
					names.add(decl.getName().getText());
				}
			}
			matches=mInvertNames;
			outerloop: for (String nameRegex : mNames) {
				Pattern p=Pattern.compile(nameRegex);
				for (String itemName : names) {
					if (p.matcher(itemName).matches())
					{
						//if we found a match, then we set our match to true or false depending on whether we are including/excluding the name
						matches=(!mInvertNames);
						break outerloop;
					}
				}
			}
			
			if (!matches)
				return false;
		}		
		
		if ((mUseFlags & Use_MetaTag)!=0)
		{
			boolean matches=false;
			List<String> tags=new ArrayList<String>();
			for (MetadataItem md : record.getMetadataItems()) {
				String mdType=md.getType().getText();
				tags.add(mdType);
			}
			matches=mInvertMetatags;
			outerloop: for (String tagRegex : mMetaTags) {
				Pattern p=Pattern.compile(tagRegex);
				for (String itemName : tags) {
					if (p.matcher(itemName).matches())
					{
						//if we found a match, then we set our match to true or false depending on whether we are including/excluding the name
						matches=(!mInvertMetatags);
						break outerloop;
					}
				}
			}
			
			if (!matches)
				return false;
		}		
		
		if ((mUseFlags & Use_Namespaces)!=0)
		{
			boolean matches=false;
			
			String ns=getNamespace(record);
			
			//if namespace=<none> specified, then check that first
			if ((mExcludeAttrs & TopLevelItemRecord.ASDoc_Namespace)!=0)
			{
				//if we are only including items with no namespaces, then set the flag accordingly
				matches=(ns==null);
			}
			else
			{
				//otherwise, we continue to check the other cases
				//if no namespace 
				if (ns==null)
				{
					//if we match anything but the specified namespaces, and no namespace is therefore allowed
					if (mInvertNamespaces)
					{
						matches=true;	
					}
				}
				else if  (ns!=null)
				{
					//if we have a namespace, and it matches part of the spec
					matches=mInvertNamespaces;
					outerloop: for (String nsRegex : mNamespaces)
					{
						Pattern p=Pattern.compile(nsRegex);
						if (p.matcher(ns).matches())
						{
							//if we found a match, then we set our match to true or false depending on whether we are including/excluding the name
							matches=(!mInvertNamespaces);
							break outerloop;
						}
					}
				}
			}
			
			if (!matches)
				return false;
		}		
		
		if ((mUseFlags & Use_Override)!=0)
		{
			boolean matches=false;
			if ((TopLevelItemRecord.ASDoc_Override & mIncludeAttrs)!=0 && (record.getModifierFlags() & TopLevelItemRecord.ASDoc_Override)!=0)
			{
				matches=true;
			}
			else if ((TopLevelItemRecord.ASDoc_Override & mExcludeAttrs)!=0 && (record.getModifierFlags() & TopLevelItemRecord.ASDoc_Override)==0)
			{
				matches=true;
			}
			if (!matches)
				return false;
		}		
		
		if ((mUseFlags & Use_Static)!=0)
		{
			boolean matches=false;
			if ((TopLevelItemRecord.ASDoc_Static & mIncludeAttrs)!=0 && (record.getModifierFlags() & TopLevelItemRecord.ASDoc_Static)!=0)
			{
				matches=true;
			}
			else if ((TopLevelItemRecord.ASDoc_Static & mExcludeAttrs)!=0 && (record.getModifierFlags() & TopLevelItemRecord.ASDoc_Static)==0)
			{
				matches=true;
			}
			if (!matches)
				return false;
		}		
		
		if ((mUseFlags & Use_Const)!=0 && (record instanceof PropertyLine))
		{
			boolean matches=false;
			if ((TopLevelItemRecord.ASDoc_Const & mIncludeAttrs)!=0 && ((PropertyLine)record).isConst())
			{
				matches=true;
			}
			else if ((TopLevelItemRecord.ASDoc_Const & mExcludeAttrs)!=0 && !((PropertyLine)record).isConst())
			{
				matches=true;
			}
			if (!matches)
				return false;
		}		
		
		if ((mUseFlags & Use_Native)!=0)
		{
			boolean matches=false;
			if ((TopLevelItemRecord.ASDoc_Native & mIncludeAttrs)!=0 && (record.getModifierFlags() & TopLevelItemRecord.ASDoc_Native)!=0)
			{
				matches=true;
			}
			else if ((TopLevelItemRecord.ASDoc_Native & mExcludeAttrs)!=0 && (record.getModifierFlags() & TopLevelItemRecord.ASDoc_Native)==0)
			{
				matches=true;
			}
			if (!matches)
				return false;
		}		
		
		//if not filtered out, then this member matches
		return true;
	}

	private String getNamespace(TopLevelItemRecord record)
	{
		Set<String> normalMods=new HashSet<String>();
		normalMods.addAll(Arrays.asList(new String[]{"private", "protected", "public", "dynamic", "static", "final", "override", "native"}));
		Set<TextItem> mods=record.getModifiers();
		for (TextItem textItem : mods) {
			String mod=textItem.getText();
			if (!normalMods.contains(mod))
				return mod;
		}
		return null;
	}
	
	public void sortItems(List<PropertySortHolder> items)
	{
		if ((mSortFlags & Sort_On)==0)
			return;
		
//		//perform my sorting
//		if ((mSortFlags & Associate_Getters)==0)
//		{
//			for (PropertySortHolder holder : implicitProperties) {
//				items.add(holder.mItem);				
//			}
//			implicitProperties.clear();
//		}
		
		Collections.sort(items, new Comparator<PropertySortHolder>()
		{
			public int compare(PropertySortHolder o1, PropertySortHolder o2)
			{
				String type1=o1.getSortType();
				String type2=o2.getSortType();
				
				//first, order by type if that option is turned on.  
				boolean byType=((mSortFlags & Sort_ByType)!=0);
				if (byType)
				{
					if (!type1.equals(type2))
					{
						return type1.compareTo(type2);
					}
				}
				
				//the types are the same (or aren't being used), so now sort by name
				
				String name1=o1.getSortName();
				String name2=o2.getSortName();
				if ((mSortFlags & Sort_CaseSensitive)==0)
				{
					name1=name1.toLowerCase();
					name2=name2.toLowerCase();
				}

				if (!name1.equals(name2))
				{
					return name1.compareTo(name2);
				}
				
				//handle the 2-function case first
				if (o1.getRawItem() instanceof FunctionRecord && o2.getRawItem() instanceof FunctionRecord)
				{
					return ASRearranger.orderTwoFunctions((FunctionRecord)o1.getRawItem(), (FunctionRecord)o2.getRawItem());
				}
				
				//is this is a property selector, then order the possibly associated property/getter/setter
//				if (!MemberSelectionSpec.this.isFunction())
				{
					if (o1.getRawItem() instanceof FunctionRecord && o2.getRawItem() instanceof PropertyLine)
						return 1;
					if (o2.getRawItem() instanceof FunctionRecord && o1.getRawItem() instanceof PropertyLine)
						return -1;
//					if (o1.getRawItem() instanceof FunctionRecord && o2.getRawItem() instanceof FunctionRecord)
//					{
//						FunctionRecord f1=(FunctionRecord)o1.getRawItem();
//						FunctionRecord f2=(FunctionRecord)o2.getRawItem();
//						if (f1.getType()==FunctionRecord.Type_Getter && f2.getType()==FunctionRecord.Type_Setter)
//							return -1;
//						else
//							return 1;
//					}
				}
				
				
				return 0;
			}
		});
		
//		//insert the implicit properties back in
//		ASRearranger.addGettersAndSetters(items, implicitProperties);
	}
	
	public static String getTypeString(TopLevelItemRecord rec)
	{
		String type1="";
		if (rec instanceof PropertyLine)
		{
			//We can have a case here with no type
			if (((PropertyLine)rec).getProperties().size()>0)
			{
				DeclRecord firstDecl=((PropertyLine)rec).getProperties().get(0);
				if (firstDecl.getType()!=null)
				{
					type1=firstDecl.getType().getText();
				}
			}
		}
		else if (rec instanceof FunctionRecord)
		{
			TextItem returnType=((FunctionRecord)rec).getReturnType();
			if (returnType!=null)
				type1=returnType.getText();
		}
		
		return type1;
	}
	
	public static final String Prop_SortFlags="SortFlags";
	public static final String Prop_ExcludeAttrs="ExcludeAttrs";
	public static final String Prop_FunctionFlags="FunctionFlags";
	public static final String Prop_IncludeAttrs="IncludeAttrs";
	public static final String Prop_InvertNames="InvertNames";
	public static final String Prop_InvertNamespaces="InvertNamespaces";
	public static final String Prop_InvertMetatags="InvertMetatags";
	public static final String Prop_IsFunction="IsFunction";
	public static final String Prop_Names="Names";
	public static final String Prop_Metatags="Metatags";
	public static final String Prop_ObjectTypes="ObjectTypes";
	public static final String Prop_FirstParameterNames="FirstParmNames";
	public static final String Prop_FirstParameterTypes="FirstParmTypes";
	public static final String Prop_Namespaces="Namespaces";
	public static final String Prop_UseFlags="UseFlags";
	public static final String Prop_Description="Description";
	public static final String Prop_ID="ID";
	public static final String Prop_Getters="Getters";
	public static final String Prop_IncludeProperties="IncludeProperties";
	public static final String Prop_BlankLinesBefore="LinesBefore";
	public static final String Prop_PropHeaderConfig="PropertyHeaderConfig";
	public static final String Prop_PreSelectPriority="PreselectPriority";
	public String persist()
	{
		Properties props=new Properties();
		props.setProperty(Prop_SortFlags, Integer.toString(mSortFlags));
		props.setProperty(Prop_Description, mPrintName);
		props.setProperty(Prop_ExcludeAttrs, Integer.toString(mExcludeAttrs));
		props.setProperty(Prop_FunctionFlags, Integer.toString(mFunctionTypeFlags));
		props.setProperty(Prop_IncludeAttrs, Integer.toString(mIncludeAttrs));
		props.setProperty(Prop_InvertNames, Boolean.toString(mInvertNames));
		props.setProperty(Prop_InvertNamespaces, Boolean.toString(mInvertNamespaces));
		props.setProperty(Prop_InvertMetatags, Boolean.toString(mInvertMetatags));
		props.setProperty(Prop_IsFunction, Boolean.toString(mIsFunction));
		props.setProperty(Prop_UseFlags, Integer.toString(mUseFlags));
		props.setProperty(Prop_ID, Long.toString(mUniqueID));
		props.setProperty(Prop_Getters, Boolean.toString(mIncludeGetters));
		props.setProperty(Prop_IncludeProperties, Boolean.toString(mIncludeProperties));
		props.setProperty(Prop_BlankLinesBefore, Integer.toString(mBlankLinesBefore));
		props.setProperty(Prop_PropHeaderConfig, Integer.toString(mPropertyHeaderStyle));
		props.setProperty(Prop_PreSelectPriority, Integer.toString(mPreselectPriority));
		
		props.setProperty(Prop_Names, getString(getNames(), true));
		props.setProperty(Prop_Metatags, getString(getMetatags(), true));
		props.setProperty(Prop_Namespaces, getString(mNamespaces, true));
		props.setProperty(Prop_ObjectTypes, getString(mObjectTypes, true));
		props.setProperty(Prop_FirstParameterTypes, getString(mFirstParameterTypes, true));
		props.setProperty(Prop_FirstParameterNames, getString(mFirstParameterNames, true));
		
		try {
			ByteArrayOutputStream os=new ByteArrayOutputStream();
			props.store(os, "");
			String returnVal=new String(os.toByteArray());
			returnVal=returnVal.replace(PreferenceConstants.AS_Pref_Line_Separator, ""); //remove commas if there are any so that it won't corrupt the enclosing stream.
			return returnVal;
		} catch (IOException e) {
			e.printStackTrace(); //shouldn't ever fail
		}
		return "";
	}
	
	public void initializeFromData(String data)
	{
		Properties props=new Properties();
		try {
			props.load(new ByteArrayInputStream(data.getBytes())); //new StringReader(data));
		} catch (IOException e) {
			e.printStackTrace(); //should never fail
		}
		
		mSortFlags=0;
		String x=props.getProperty(Prop_SortFlags, "0");
		try
		{
			mSortFlags=Integer.parseInt(x);
		}
		catch (NumberFormatException e)
		{
			Activator.logException(e, "Failure reading member selection spec (sort flags)");
		}
		
		mExcludeAttrs=0;
		x=props.getProperty(Prop_ExcludeAttrs, "0");
		try
		{
			mExcludeAttrs=Integer.parseInt(x);
		}
		catch (NumberFormatException e)
		{
			Activator.logException(e, "Failure reading member selection spec (exclude attrs)");
		}
		
		mFunctionTypeFlags=0;
		x=props.getProperty(Prop_FunctionFlags, "0");
		try
		{
			mFunctionTypeFlags=Integer.parseInt(x);
		}
		catch (NumberFormatException e)
		{
			Activator.logException(e, "Failure reading member selection spec (function flags)");
		}
		
		mBlankLinesBefore=1;
		x=props.getProperty(Prop_BlankLinesBefore, "1");
		try
		{
			mBlankLinesBefore=Integer.parseInt(x);
		}
		catch (NumberFormatException e)
		{
			Activator.logException(e, "Failure reading member selection spec (blank lines before)");
		}
		
		mPropertyHeaderStyle=PreferenceConstants.PropertyHeaders_None;
		x=props.getProperty(Prop_PropHeaderConfig, Integer.toString(PreferenceConstants.PropertyHeaders_None));
		try
		{
			mPropertyHeaderStyle=Integer.parseInt(x);
		}
		catch (NumberFormatException e)
		{
			Activator.logException(e, "Failure reading member selection spec (property header style)");
		}
		
		mPreselectPriority=0;
		x=props.getProperty(Prop_PreSelectPriority, "0");
		try
		{
			mPreselectPriority=Integer.parseInt(x);
		}
		catch (NumberFormatException e)
		{
			Activator.logException(e, "Failure reading member selection spec (pre select priority)");
		}
		
		mIncludeAttrs=0;
		x=props.getProperty(Prop_IncludeAttrs, "0");
		try
		{
			mIncludeAttrs=Integer.parseInt(x);
		}
		catch (NumberFormatException e)
		{
			Activator.logException(e, "Failure reading member selection spec (include attrs)");
		}
		
		mUseFlags=0;
		x=props.getProperty(Prop_UseFlags, "0");
		try
		{
			mUseFlags=Integer.parseInt(x);
		}
		catch (NumberFormatException e)
		{
			Activator.logException(e, "Failure reading member selection spec (use flags)");
		}
		
		mUniqueID=0;
		x=props.getProperty(Prop_ID, "0");
		try
		{
			long val=Long.parseLong(x);
			if (val>0)
				mUniqueID=val; //otherwise leave as the current time
		}
		catch (NumberFormatException e)
		{
			Activator.logException(e, "Failure reading member selection spec (id)");
		}
		
		mPrintName=props.getProperty(Prop_Description, "");
		
		mInvertNames=Boolean.parseBoolean(props.getProperty(Prop_InvertNames, Boolean.toString(Boolean.FALSE)));
		mInvertNamespaces=Boolean.parseBoolean(props.getProperty(Prop_InvertNamespaces, Boolean.toString(Boolean.FALSE)));
		mInvertMetatags=Boolean.parseBoolean(props.getProperty(Prop_InvertMetatags, Boolean.toString(Boolean.FALSE)));
		
		mIsFunction=Boolean.parseBoolean(props.getProperty(Prop_IsFunction, Boolean.toString(Boolean.FALSE)));
		mIncludeGetters=Boolean.parseBoolean(props.getProperty(Prop_Getters, Boolean.toString(Boolean.FALSE)));
		mIncludeProperties=Boolean.parseBoolean(props.getProperty(Prop_IncludeProperties, Boolean.toString(Boolean.FALSE)));
		
		mNames=getItems(props.getProperty(Prop_Names, ""), true);
		mNamespaces=getItems(props.getProperty(Prop_Namespaces, ""), true);
		mMetaTags=getItems(props.getProperty(Prop_Metatags, ""), true);
		mObjectTypes=getItems(props.getProperty(Prop_ObjectTypes, ""), true);
		mFirstParameterNames=getItems(props.getProperty(Prop_FirstParameterNames, ""), true);
		mFirstParameterTypes=getItems(props.getProperty(Prop_FirstParameterTypes, ""), true);
	}
	
	public static final String Internal_Separator_Escaped="\\|\\|\\|";
	public static final String Internal_Separator="|||";
	public static List<String> getItems(String data, boolean persisted)
	{
		List<String> names=new ArrayList<String>();
		String[] items=data.split(persisted ? Internal_Separator_Escaped : ",");
		for (String item : items) {
			if (item.length()>0)
				names.add(item);
		}
		return names;
	}
	
	
	public static String getString(List<String> items, boolean persist)
	{
		StringBuffer buffer=new StringBuffer();
		boolean first=true;
		for (String name : items) {
			if (!first)
			{
				if (persist)
					buffer.append(Internal_Separator);
				else
					buffer.append(',');
			}
			first=false;
			buffer.append(name);
		}
		return buffer.toString(); 
	}

	public String getPrintString() {
		return toString();
	}
	
	public String getReferenceID()
	{
		return Long.toString(mUniqueID);
	}

	public boolean isIncludeGetters()
	{
		return mIncludeGetters;
	}

	public void setIncludeGetters(boolean selection) {
		mIncludeGetters=selection;
	}

	public int getBlankLinesBefore() {
		return mBlankLinesBefore;
	}

	public void setBlankLinesBefore(int blankLinesBefore) {
		mBlankLinesBefore = blankLinesBefore;
	}

	public int getPropertyHeaderStyle() {
		return mPropertyHeaderStyle;
	}

	public void setPropertyHeaderStyle(int propertyHeaderStyle) {
		mPropertyHeaderStyle = propertyHeaderStyle;
	}

	public int getPreselectPriority() {
		return mPreselectPriority;
	}

	public void setPreselectPriority(int preselectPriority) {
		mPreselectPriority = preselectPriority;
	}

	public boolean isIncludeAssociatedProperty() {
		return mIncludeProperties;
	}

	public void setIncludeAssociatedProperty(boolean include)
	{
		mIncludeProperties=include;
	}
}
