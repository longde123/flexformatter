package flexasrearrangecodecommand.handlers;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.Token;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPositionCategoryException;
import org.eclipse.jface.text.DefaultPositionUpdater;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IPositionUpdater;
import org.eclipse.ui.texteditor.MarkerAnnotation;
import org.eclipse.ui.texteditor.MarkerUtilities;

import actionscriptinfocollector.ASCollector;
import actionscriptinfocollector.ASCollectorLexer;
import actionscriptinfocollector.AntlrUtilities;
import actionscriptinfocollector.ClassRecord;
import actionscriptinfocollector.DeclRecord;
import actionscriptinfocollector.DefaultNamespaceItem;
import actionscriptinfocollector.FunctionRecord;
import actionscriptinfocollector.ImportRecord;
import actionscriptinfocollector.IncludeItem;
import actionscriptinfocollector.MetadataItem;
import actionscriptinfocollector.ObjectPositionHolder;
import actionscriptinfocollector.PropertyLine;
import actionscriptinfocollector.SourceItem;
import actionscriptinfocollector.StaticInitializerRecord;
import actionscriptinfocollector.TextItem;
import actionscriptinfocollector.TopLevelItemRecord;
import actionscriptinfocollector.UseNamespaceItem;
import actionscriptinfocollector.Utilities;
import flexasrearrangecodecommand.preferences.PreferenceConstants;
import flexprettyprint.handlers.ActionScriptFormatter;
import flexprettyprint.preferences.CommonPrefComposite;
import flexprettyprint.preferences.ISectionItem;
import flexprettyprint.preferences.MemberSelectionSpec;
import flexprettyprint.preferences.NewSectionHeaderItem;
import flexprettyprint.preferences.SectionHeader;
import flexprettyprint.preferences.SectionSpec;
import flexprettyprintcommand.Activator;

public class ASRearranger {
	
	private IPreferenceStore mPrefs;
	private List<Exception> mErrors;
	private String mInternalError;
	private boolean mIsSoftFailure=false;
	private SectionHeader mCopyrightHeader;
	private boolean mMadeChanges;
	
	public static final String SlashStar="/*";
	public static final String SlashSlash="//";
	public static final String StarSlash="*/";
	public static final String Section_Metatags="Metatags";
	public static final String SpanningSuffix="#span";
	
	public ASRearranger(IPreferenceStore store)
	{
		mPrefs=store;
	}
	
	private StringBuffer mAddedText=new StringBuffer();
	private StringBuffer mRemovedText=new StringBuffer();
	
	public boolean rearrangeCode(IDocument source, List<MarkerAnnotation> lineBasedAnnotations, boolean insideMXML)
	{
		mMadeChanges=false;
		mIsSoftFailure=false;
		mAddedText=new StringBuffer();
		mRemovedText=new StringBuffer();
		mCopyrightHeader=SectionHeader.load(mPrefs.getString(PreferenceConstants.ASRearr_CopyrightHeader));
		
		List<Map<String, Integer>> savedPositions=new ArrayList<Map<String, Integer>>();
		try
		{
			Document workingDoc=new Document(source.get());
			
			//now, process copyright, but only for .as files
			if (!insideMXML && mPrefs.getBoolean(PreferenceConstants.ASRearr_UseCopyright))
			{
				//find the first item
				//look to see if the first comment is a copyright comment
				//delete or add based on settings
				//update doc and added/removed text
				String docText=workingDoc.get();
				docText=Utilities.convertCarriageReturnsToLineFeeds(docText);
				ANTLRStringStream stream=new ANTLRStringStream(docText);
				ASCollectorLexer l2=new ASCollectorLexer(stream);
				List<String> preItems=new ArrayList<String>();
				while (true)
				{
					Token t=l2.nextToken();
					if (t==null)
						break;
					if (t.getType()==ASCollectorLexer.EOL || t.getType()==ASCollectorLexer.WHITESPACE || t.getType()==ASCollectorLexer.COMMENT_MULTILINE || t.getType()==ASCollectorLexer.COMMENT_SINGLELINE)
					{
						preItems.add(t.getText());
					}
					else
						break;
				}

				//if it has the current copyright data or contains the word 'copyright', then 
				//I'll consider it a copyright statement.
				List<HeaderInfo> headers=new ArrayList<HeaderInfo>();
				getHeadersFromArray(preItems.toArray(new String[]{}), workingDoc.getDefaultLineDelimiter(), headers);
				boolean hasCopyright=false;
				boolean exactMatch=false;
				boolean removed=false;
				if (headers.size()>0)
				{
					HeaderInfo header=headers.get(0);
					boolean[] isCopyrightFlags=isCopyright(header, mCopyrightHeader);
					hasCopyright=isCopyrightFlags[0];
					exactMatch=isCopyrightFlags[1];

					//if the copyright matches exactly or is a copyright and we are removing existing, then remove the text
					if (exactMatch || (hasCopyright && mPrefs.getBoolean(PreferenceConstants.ASRearr_RemoveExistingCopyrightHeaders)))
					{
						String removedText=workingDoc.get(0, header.getEndPos()-0);
						mRemovedText.append(removedText);
						workingDoc.replace(0, header.getEndPos()-0, ""); //remove the text
						removed=true;
					}
				}
					
				if (!hasCopyright || removed)
				{
					String insertData=mCopyrightHeader.generateHeader(workingDoc.getDefaultLineDelimiter(), "")+workingDoc.getDefaultLineDelimiter();
					
//					//only add in the extra blank lines afterward if we didn't just remove the the copyright
//					if (removed)
					{
						//remove extra carriage returns at start of doc
						String tempText=workingDoc.get();
						int deleteLength=0;
						for (;deleteLength<docText.length();deleteLength++)
						{
							char c=tempText.charAt(deleteLength);
							if (c!='\r' && c!='\n')
								break;
						}
						if (deleteLength>0)
							workingDoc.replace(0, deleteLength, "");
					}
//					if (!removed)
					{
						for (int i=0;i<mCopyrightHeader.getLinesBefore();i++)
							insertData+=workingDoc.getDefaultLineDelimiter();
					}
					workingDoc.replace(0, 0, insertData);
					mAddedText.append(insertData);
				}
			}

			
			List<ASCollector> collectors=new ArrayList<ASCollector>();
			mErrors=ASCollector.parse(workingDoc, collectors);
			if (mErrors!=null)
				return false;
			
			for (ASCollector collector : collectors) {
				if (collector.containsConditionalMembers())
				{
					mInternalError="This file contains conditionally-compiled member blocks, which cannot be rearranged at this time.";
					mIsSoftFailure=true;
					return false;
				}
				collector.applyDocument(workingDoc);
			}
			
			//check for copyright comment at start of file and remove that from the first token.  I've already
			//determined whether there is a copyright comment above, but I don't know if it's actually going to 
			//be associated with imports (or other things for a non-class file) where it could be a problem.
			//Not sure how to safely make this check other than to check items for pre-tokens that are at file start.
	
			IPositionUpdater updater=null;
			updater=new DefaultPositionUpdater(SourceItem.PositionCategory);
			workingDoc.addPositionUpdater(updater);
	
			for (MarkerAnnotation markerAnnotation : lineBasedAnnotations) {
				Map<String, Integer> attrMap=new HashMap<String, Integer>();
				savedPositions.add(attrMap);
				for (ASCollector collector : collectors) {
					boolean captured=collector.captureObject(workingDoc, markerAnnotation, markerAnnotation.getMarker().getAttribute(IMarker.LINE_NUMBER, 0)-1);
					if (captured)
					{
						try {
							attrMap.put(IMarker.LINE_NUMBER, markerAnnotation.getMarker().getAttribute(IMarker.LINE_NUMBER, 1));
							attrMap.put(IMarker.CHAR_START, markerAnnotation.getMarker().getAttribute(IMarker.CHAR_START, 0));
							attrMap.put(IMarker.CHAR_END, markerAnnotation.getMarker().getAttribute(IMarker.CHAR_END, 0));

							markerAnnotation.getMarker().setAttribute(IMarker.LINE_NUMBER, 1);
							markerAnnotation.getMarker().setAttribute(IMarker.CHAR_START, 0);
							markerAnnotation.getMarker().setAttribute(IMarker.CHAR_END, 0);
						} catch (CoreException e) {
							mInternalError="Error storing line markers";
						}
						break; //don't keep looking if we found inside one collector
					}
				}
			}
	
			final boolean moveImports=mPrefs.getBoolean(PreferenceConstants.ASRearr_MoveImportsOutsideClass);
			boolean modified=false;
			for (ASCollector collector : collectors)
			{
				if (collector.getEndPos()==collector.getStartPos())
					continue;
				
				List<ClassRecord> classes=collector.getClassRecords();

				//handle stuff outside of any classes
				modified|=reorderFunctionModifiers(collector.getFunctionRecords(), workingDoc);
				modified|=reorderPropertyModifiers(collector.getPropertyRecords(), workingDoc);

				//rearrange elements outside class
				List<ImportRecord> movedImports=new ArrayList<ImportRecord>();
				if (moveImports)
				{
					for (ClassRecord classRecord : classes)
					{
						movedImports.addAll(classRecord.getImports());
					}
				}

				TextItem packageOpen=collector.getPackageOpen();
				TextItem packageClose=collector.getPackageClose();
				for (ClassRecord classRecord : classes) {
					if (packageClose==null || packageClose.getStartPos()>classRecord.getStartPos())
						packageClose=classRecord.getName();
				}
				
				int startInsertPos=0;
				SourceItem endPosInDoc=null;
				
				if (packageOpen==null)
				{
					startInsertPos=collector.getStartPos();
				}
				else
				{
					startInsertPos=packageOpen.getEndPos();
				}
				
				if (packageClose==null)
				{
					endPosInDoc=null; //collector.getEndPos();
				}
				else
				{
					endPosInDoc=packageClose; //.getStartPos();
				}
				

				List<ImportRecord> outsideClassImports=new ArrayList<ImportRecord>();
				outsideClassImports.addAll(collector.getImports());
				outsideClassImports.addAll(movedImports);
				modified|=reorderElements(startInsertPos, endPosInDoc, "", collector.getFunctionRecords(), collector.getPropertyRecords(), outsideClassImports, collector.getIncludes(), collector.getDefinedNamespaces(), collector.getDefaultNamespaces(), collector.getUseNamespaces(), new ArrayList<StaticInitializerRecord>(), insideMXML, true, false || insideMXML, workingDoc);

				for (ClassRecord classRecord : classes)
				{
					modified|=reorderClassModifiers(classRecord, workingDoc);
					List<ImportRecord> imports=new ArrayList<ImportRecord>();
					if (!moveImports)
						imports.addAll(classRecord.getImports());
					
					//sort metatags for class
					List<MetadataItem> originalMetatags=new ArrayList<MetadataItem>();
					originalMetatags.addAll(classRecord.getMetadataItems());
					List<MetadataItem> metatags=new ArrayList<MetadataItem>();
					metatags.addAll(classRecord.getMetadataItems());
					if (metatags.size()>0)
					{
						int earliestPos=metatags.get(0).getPreStartPos();
						sortMetatags(metatags);
						boolean changes=false;
						for (int i=0;i<metatags.size();i++)
						{
							if (!(metatags.get(i)==originalMetatags.get(i)))
							{
								changes=true;
								break;
							}
						}
						
						//skip if no change in ordering
						if (changes)
						{
							List<ReorderHolder> capturedTags=new ArrayList<ReorderHolder>();
							boolean firstItem=true;
							for (MetadataItem metadataItem : metatags) {
								metadataItem.nailDownPositions();
								capturedTags.add(new ReorderHolder(metadataItem, workingDoc.get(metadataItem.getPreStartPos(), metadataItem.getPostEndPos()-metadataItem.getPreStartPos()), getIndentString(workingDoc, metadataItem.getPreStartPos()), firstItem && isAtFileStart(metadataItem.getPreStartPos(), workingDoc)));
								firstItem=false;
							}
							
							//delete the existing items
							for (ReorderHolder holder : capturedTags)
							{
								SourceItem sourceItem=holder.getItem();
								workingDoc.replace(sourceItem.getPreStartPos(), sourceItem.getPostEndPos()-sourceItem.getPreStartPos(), "");
							}
							
							int insertPos=insertItems(capturedTags, new HashMap<String, SectionSpec>(), workingDoc, earliestPos, insideMXML);
							clearExtraBlankLines(workingDoc, insertPos, classRecord.getStartPos());
							modified=true;
						}
					}					
					
					modified|=reorderElements(classRecord.getBodyStart().getEndPos(), classRecord.getBodyEnd(), classRecord.getName().getText(), classRecord.getFunctions(), classRecord.getProperties(), imports, classRecord.getIncludes(), classRecord.getNamespaces(), classRecord.getDefaultNamespaces(), classRecord.getUseNamespaces(), classRecord.getStaticInitializers(), insideMXML, classRecord.isClass(), true, workingDoc);
				}
			}
			
			if (isDocDifferent(workingDoc, source))
			{
				//now, check validation
				boolean validated=true;
				for (ASCollector collector : collectors) {
					if (!collector.validateText(workingDoc))
					{
						validated=false;
						break;
					}
				}
				
				if (validated)
				{
					if (ActionScriptFormatter.validateNonWhitespaceCharCounts(workingDoc.get()+mRemovedText.toString(), source.get()+mAddedText.toString()))
					{
						mMadeChanges=true;
						System.out.println(workingDoc.get());
						String working=workingDoc.get();
						if (!workingDoc.getDefaultLineDelimiter().equals(source.getLineDelimiter(0)))
						{
							working=working.replace(workingDoc.getDefaultLineDelimiter(), source.getLineDelimiter(0));
						}
						source.replace(0, source.getLength(), working);
	
						//update annotations
//						try
						{
							for (ASCollector collector : collectors) {
								List<ObjectPositionHolder> annotations=collector.getCapturedObjects();
								for (ObjectPositionHolder holder : annotations) {
									if (holder.getObject() instanceof MarkerAnnotation)
									{
										int startLine=source.getLineOfOffset(holder.getAssociatedItem().getStartPos());
										int linePos=source.getLineOffset(startLine+holder.getLinesOffset());
										String data=source.get();
										//TODO: using MarkerUtilities apparently doesn't do enough to flush the changes to the marker.  Or
										//maybe I'm working with a copy or something.  Not critical, but requires more investigation.
										//NOTE: line_number is 1-based.  Char positions are 0-based.
										IMarker marker=((MarkerAnnotation)holder.getObject()).getMarker();
										MarkerUtilities.setLineNumber(marker, startLine+holder.getLinesOffset()+1);
										MarkerUtilities.setCharStart(marker, linePos);
										MarkerUtilities.setCharEnd(marker, linePos);
	//									marker.setAttribute(IMarker.LINE_NUMBER, startLine+holder.getLinesOffset()+1);
	//									marker.setAttribute(IMarker.CHAR_START, linePos);
	//									marker.setAttribute(IMarker.CHAR_END, linePos);
									}
								}
							}
						}
//						catch (CoreException e)
//						{
//							e.printStackTrace();
//						}
						return true;
					}
					else
					{
						mInternalError="Internal error replacing text: new text doesn't match replaced text("+source.get()+")!=("+workingDoc.get()+")";
						return false;
					}
				}
				else
				{
					mInternalError="An internal error occurred rearranging text.  Aborting the operation to prevent scrambling your code.\nIf this error persists, please open a tracker on the SourceForge/flexformatter site and attach code that reproduces the problem.";
					return false;
				}
			}
		}
		catch (Exception e)
		{
			if (mErrors==null)
				mErrors=new ArrayList<Exception>();
			mErrors.add(e);
			e.printStackTrace();
			return false;
		}

		//put markers back where they were if the rearrange was *not* successful.
		try
		{
			for (int i=0; i<lineBasedAnnotations.size();i++)
			{
				Map<String, Integer> saved=savedPositions.get(i);
				if (saved!=null)
				{
					MarkerAnnotation markerAnnotation=lineBasedAnnotations.get(i);
					Integer val=saved.get(IMarker.LINE_NUMBER);
					if (val!=null)
						MarkerUtilities.setLineNumber(markerAnnotation.getMarker(), val.intValue());
//						markerAnnotation.getMarker().setAttribute(IMarker.LINE_NUMBER, val.intValue());
					val=saved.get(IMarker.CHAR_START);
					if (val!=null)
						MarkerUtilities.setCharStart(markerAnnotation.getMarker(), val.intValue());
//						markerAnnotation.getMarker().setAttribute(IMarker.CHAR_START, val.intValue());
					val=saved.get(IMarker.CHAR_END);
					if (val!=null)
						MarkerUtilities.setCharEnd(markerAnnotation.getMarker(), val.intValue());
//						markerAnnotation.getMarker().setAttribute(IMarker.CHAR_END, val.intValue());
				}
			}
			return true;  //we didn't do anything, but that's okay
		}
		catch (Exception e)
		{
			if (mErrors==null)
				mErrors=new ArrayList<Exception>();
			mErrors.add(e);
		}
		
		return false;
	}
	
	
	private static boolean[] isCopyright(HeaderInfo header, SectionHeader copyrightHeader)
	{
		boolean[] flags=new boolean[2];
		String contentText=convertContentLinesToString(header.getContentLines());
		flags[0]=contentText.toLowerCase().contains("copyright");
		String copyrightHeaderAsString=convertContentLinesToString(copyrightHeader.getText());
		flags[1]=contentText.equalsIgnoreCase(copyrightHeaderAsString); //seems like case insensitive is okay and will mask some unimportant differences
		if (flags[1])
			flags[0]=true;
		
		return flags;
	}


	public List<Exception> getErrors() {
		return mErrors;
	}

	private boolean isDocDifferent(IDocument newDoc, IDocument originalDoc) throws BadLocationException
	{
		//compare lines of docs and return true if they are the same
//		try
//		{
			String[] newLines=newDoc.get().trim().split(newDoc.getLineDelimiter(0));
			String[] oldLines=originalDoc.get().trim().split(originalDoc.getLineDelimiter(0));
			if (newLines.length!=oldLines.length)
				return true;
			for (int i=0;i<newLines.length;i++)
			{
				if (newLines[i].compareTo(oldLines[i])!=0)
					return true;
			}
			
			return false;
//		}
//		catch (BadLocationException e)
//		{
//			Activator.logException(e, "");
//		}
//		return true;
	}
	
	public static Map<String, SectionSpec> readHeaderSpecs(String specData)
	{
		Map<String, SectionSpec> headers=new HashMap<String, SectionSpec>();
		try
		{
			Properties props=new Properties();
			InputStream is=new ByteArrayInputStream(specData.getBytes());
			props.load(is);
			headers.clear();
			for (Object obj : props.keySet()) {
				Object spec=props.get(obj);
				if (obj instanceof String && spec!=null && spec instanceof String)
				{
					SectionSpec s=SectionSpec.load((String)spec);
					headers.put((String)obj, s);
				}
			}
		}
		catch (Exception e)
		{
			Activator.logException(e, "");
		}
		return headers;
	}
	
	public static void updateBlankLinesMap(Map<String, Integer> blankLinesMap, String blankLinesData) {
		blankLinesMap.clear();
		String[] props=blankLinesData.split(PreferenceConstants.AS_Pref_Line_Separator);
		for (String line : props) {
			int eq=line.indexOf(PreferenceConstants.AS_Pref_Equals);
			if (eq>0)
			{
				String el=line.substring(0, eq);
				String value=line.substring(eq+1);
				try
				{
					int count=Integer.parseInt(value);
					blankLinesMap.put(el, count);
				}
				catch (NumberFormatException e)
				{
					Activator.logException(e, "");
				}
			}
		}
	}

	private boolean reorderElements(int insertLocation, SourceItem endOfRange, final String className, List<FunctionRecord> origFunctions, List<PropertyLine> origProperties, List<ImportRecord> imports, List<IncludeItem> includes, List<PropertyLine> namespaces, List<DefaultNamespaceItem> defaultNamespaces, List<UseNamespaceItem> useNamespaces, List<StaticInitializerRecord> initializers, boolean insideMXML, boolean isClass, boolean insideClass, Document workingDoc) throws BadLocationException, BadPositionCategoryException
	{
		IPreferenceStore store=mPrefs;
		if (!store.getBoolean(PreferenceConstants.ASRearr_UseElementOrder))
			return false;

		SectionHeader majorHeader=SectionHeader.load(store.getString(PreferenceConstants.ASRearr_MajorSectionHeader));
		SectionHeader minorHeader=SectionHeader.load(store.getString(PreferenceConstants.ASRearr_MinorSectionHeader));
		Map<Integer, SectionHeader> baseHeaderMap=new HashMap<Integer, SectionHeader>();
		baseHeaderMap.put(SectionSpec.MAJOR, majorHeader);
		baseHeaderMap.put(SectionSpec.MINOR, minorHeader);
		Map<String, SectionSpec> headerMap=readHeaderSpecs(mPrefs.getString(PreferenceConstants.ASRearr_SectionHeaders));
		boolean useSectionHeaders=mPrefs.getBoolean(PreferenceConstants.ASRearr_UseSectionHeaders);
		if (!useSectionHeaders || (!isClass) || (insideMXML && !mPrefs.getBoolean(PreferenceConstants.ASRearr_UseSectionHeadersInMXML)))
			headerMap.clear();
		
		String blankLinesData=mPrefs.getString(PreferenceConstants.ASRearr_BlankLinesBeforeElement);
		Map<String, Integer> blankLinesMap=new HashMap<String, Integer>();
		updateBlankLinesMap(blankLinesMap, blankLinesData);

		Map<Integer, String> visMap=new HashMap<Integer, String>();
		visMap.put(TopLevelItemRecord.ASDoc_Public, PreferenceConstants.AS_Mod_Public);
		visMap.put(TopLevelItemRecord.ASDoc_Protected, PreferenceConstants.AS_Mod_Protected);
		visMap.put(TopLevelItemRecord.ASDoc_Private, PreferenceConstants.AS_Mod_Private);
		visMap.put(TopLevelItemRecord.ASDoc_Internal, PreferenceConstants.AS_Mod_Internal);
		
		List<FunctionRecord> allFunctions=new ArrayList<FunctionRecord>();
		allFunctions.addAll(origFunctions);
		List<PropertyLine> allProperties=new ArrayList<PropertyLine>();
		allProperties.addAll(origProperties);
		
		List<SourceItem> sortedItems=new ArrayList<SourceItem>();
//		List<SourceItem> allMembers=new ArrayList<SourceItem>();
//		allMembers.addAll(allProperties);
//		allMembers.addAll(allFunctions);
		
		String[] orderItems=store.getString(PreferenceConstants.ASRearr_ElementOrder).split(PreferenceConstants.AS_Pref_Line_Separator);
		
		//build list of ISectionItems in case we need them.
		List<ISectionItem> sections=new ArrayList<ISectionItem>();
		for (String itemData : orderItems) {
			if (itemData.startsWith(PreferenceConstants.AS_Pref_MemberSpecPrefix))
			{
				MemberSelectionSpec spec=new MemberSelectionSpec();
				spec.initializeFromData(itemData.substring(1));
				sections.add(spec);
			}			
			else
			{
				sections.add(new CommonPrefComposite.ElementHolder(itemData));
			}
		}
		Set<String> usedSectionIDS=new HashSet<String>();

		//handle member selector prefiltering
		Map<Integer, List<PropertySortHolder>> prefilterMap=new HashMap<Integer, List<PropertySortHolder>>();
		{
			//grab prefilter selectors if there are any
			List<MemberSelectionSpec> prefilters=new ArrayList<MemberSelectionSpec>();
			for (ISectionItem section : sections)
			{
				if (section instanceof MemberSelectionSpec)
				{
					MemberSelectionSpec spec=(MemberSelectionSpec)section;
					if (spec.getPreselectPriority()>0)
						prefilters.add(spec);
				}
			}

			//sort by preselect priority
			Collections.sort(prefilters, new Comparator<MemberSelectionSpec>()
			{
				public int compare(MemberSelectionSpec o1, MemberSelectionSpec o2)
				{
					return o1.getPreselectPriority()-o2.getPreselectPriority();
				}
			});
			
			for (MemberSelectionSpec spec : prefilters) {
				List<PropertySortHolder> results=new ArrayList<PropertySortHolder>();
				prefilterMembers(allFunctions, allProperties, spec, className, results);
				prefilterMap.put(spec.getPreselectPriority(), results);
			}
		}		
		
		for (ISectionItem section : sections)
		{
			if (section instanceof MemberSelectionSpec)
			{
				List<PropertySortHolder> tempItems=null;
				MemberSelectionSpec spec=(MemberSelectionSpec)section;
				if (spec.getPreselectPriority()>0)
					tempItems=prefilterMap.get(spec.getPreselectPriority());
				if (tempItems==null)
				{
					tempItems=new ArrayList<PropertySortHolder>();
					prefilterMembers(allFunctions, allProperties, spec, className, tempItems);
				}
				
				List<SourceItem> newItems=new ArrayList<SourceItem>();
				for (PropertySortHolder holder : tempItems) {
					newItems.add(holder.getRawItem());
				}
				
				boolean addedHeader=addElementHeaderItem(section, sections, headerMap, newItems, usedSectionIDS, sortedItems);
				if (!addedHeader && tempItems.size()>0 && sortedItems.size()>0)
					sortedItems.add(new NewSectionHeaderItem(getCRCountForElement(tempItems.get(0).getRawItem(), spec.getBlankLinesBefore())));
//				if (!spec.isFunction())
					insertPropertyHeaders(tempItems, PreferenceConstants.ASRearr_UseSectionHeaders, spec.getPropertyHeaderStyle(), (spec.getSortFlags() & MemberSelectionSpec.Associate_Getters)!=0);
				
				for (PropertySortHolder holder : tempItems) {
					sortedItems.add(holder.getRawItem());
				}
			}
			else
			{
				if (section.getReferenceID().equals(PreferenceConstants.ASRearr_Element_Include))
				{
					sortIncludes(includes);
					boolean addedHeader=addElementHeaderItem(section, sections, headerMap, includes, usedSectionIDS, sortedItems);
					if (!addedHeader && includes.size()>0 && sortedItems.size()>0)
						sortedItems.add(new NewSectionHeaderItem(getCRCountForElement(includes.get(0), blankLinesMap, PreferenceConstants.ASRearr_Element_Include)));
					sortedItems.addAll(includes);
				}
				else if (section.getReferenceID().equals(PreferenceConstants.ASRearr_Element_Import))
				{
					sortImports(imports);
					boolean addedHeader=addElementHeaderItem(section, sections, headerMap, imports, usedSectionIDS, sortedItems);
					if (!addedHeader && imports.size()>0 && sortedItems.size()>0)
						sortedItems.add(new NewSectionHeaderItem(getCRCountForElement(imports.get(0), blankLinesMap, PreferenceConstants.ASRearr_Element_Import)));
					sortedItems.addAll(imports);
				}
				else if (section.getReferenceID().equals(PreferenceConstants.ASRearr_Element_DefaultNamespace))
				{
					boolean sort=store.getBoolean(PreferenceConstants.ASRearr_SortNamespaces);
					if (sort)
					{
						Collections.sort(defaultNamespaces, new Comparator<DefaultNamespaceItem>()
						{
							public int compare(DefaultNamespaceItem o1, DefaultNamespaceItem o2)
							{
								return o1.getNamespace().getText().compareTo(o2.getNamespace().getText());
							}
						});
					}
					boolean addedHeader=addElementHeaderItem(section, sections, headerMap, defaultNamespaces, usedSectionIDS, sortedItems);
					if (!addedHeader && defaultNamespaces.size()>0 && sortedItems.size()>0)
						sortedItems.add(new NewSectionHeaderItem(getCRCountForElement(defaultNamespaces.get(0), blankLinesMap, PreferenceConstants.ASRearr_Element_DefaultNamespace)));
					sortedItems.addAll(defaultNamespaces);
				}
				else if (section.getReferenceID().equals(PreferenceConstants.ASRearr_Element_DefineNamespace))
				{
					boolean sort=store.getBoolean(PreferenceConstants.ASRearr_SortNamespaces);
					if (sort)
					{
						sortNamespaces(namespaces);
					}
					boolean addedHeader=addElementHeaderItem(section, sections, headerMap, namespaces, usedSectionIDS, sortedItems);
					if (!addedHeader && namespaces.size()>0 && sortedItems.size()>0)
						sortedItems.add(new NewSectionHeaderItem(getCRCountForElement(namespaces.get(0), blankLinesMap, PreferenceConstants.ASRearr_Element_DefineNamespace)));
					sortedItems.addAll(namespaces);
				}
				else if (section.getReferenceID().equals(PreferenceConstants.ASRearr_Element_UseNamespace))
				{
					boolean sort=store.getBoolean(PreferenceConstants.ASRearr_SortNamespaces);
					if (sort)
					{
						Collections.sort(useNamespaces, new Comparator<UseNamespaceItem>()
						{
							public int compare(UseNamespaceItem o1, UseNamespaceItem o2)
							{
								return o1.getNamespaces().get(0).getText().compareTo(o2.getNamespaces().get(0).getText());
							}
						});
					}
					boolean addedHeader=addElementHeaderItem(section, sections, headerMap, useNamespaces, usedSectionIDS, sortedItems);
					if (!addedHeader && useNamespaces.size()>0 && sortedItems.size()>0)
						sortedItems.add(new NewSectionHeaderItem(getCRCountForElement(useNamespaces.get(0), blankLinesMap, PreferenceConstants.ASRearr_Element_UseNamespace)));
					sortedItems.addAll(useNamespaces);
				}
				else if (section.getReferenceID().equals(PreferenceConstants.ASRearr_Element_Constructor))
				{
					List<FunctionRecord> gatheredFunctions=new ArrayList<FunctionRecord>();
					for (int i = 0; i < allFunctions.size();)
					{
						FunctionRecord function = allFunctions.get(i);
						if ((function.getModifierFlags() & TopLevelItemRecord.ASDoc_Static)==0)
						{
							if (function.getName().getText().equals(className))
							{
								allFunctions.remove(i);
								gatheredFunctions.add(function); //should only be one
								continue;
							}
						}
						i++;
					}
					
					boolean addedHeader=addElementHeaderItem(section, sections, headerMap, gatheredFunctions, usedSectionIDS, sortedItems);
					if (!addedHeader && gatheredFunctions.size()>0 && sortedItems.size()>0)
						sortedItems.add(new NewSectionHeaderItem(getCRCountForElement(gatheredFunctions.get(0), blankLinesMap, PreferenceConstants.ASRearr_Element_Constructor)));
					sortedItems.addAll(gatheredFunctions);
				}
				else if (section.getReferenceID().equals(PreferenceConstants.ASRearr_Element_Function))
				{
					List<FunctionRecord> gatheredFunctions=new ArrayList<FunctionRecord>();
					for (int i = 0; i < allFunctions.size();)
					{
						FunctionRecord function = allFunctions.get(i);
						if ((function.getModifierFlags() & TopLevelItemRecord.ASDoc_Static)==0)
						{
							if (!function.getName().getText().equals(className))
							{
								//skip if this is a getter/setter and we are treating those as properties
								if (store.getBoolean(PreferenceConstants.ASRearr_TreatGettersAndSettersAsProperties) && (function.getType()==FunctionRecord.Type_Getter || function.getType()==FunctionRecord.Type_Setter))
								{
									i++;
									continue;
								}
								
								allFunctions.remove(i);
								gatheredFunctions.add(function);
								continue;
							}
						}
						i++;
					}
					sortFunctions(gatheredFunctions, store.getBoolean(PreferenceConstants.ASRearr_SortFunctions), store.getString(PreferenceConstants.ASRearr_FunctionVisibilityOrder), store.getBoolean(PreferenceConstants.ASRearr_UseFunctionVisibilityOrder));
					List<PropertySortHolder> outputProperties=new ArrayList<PropertySortHolder>();
					for (FunctionRecord func : gatheredFunctions) {
						outputProperties.add(new PropertySortHolder(func));
					}
					insertHeaderSpecs(outputProperties, headerMap, PreferenceConstants.ASRearr_Element_Function, visMap, PreferenceConstants.ASRearr_SortFunctions, PreferenceConstants.ASRearr_UseFunctionVisibilityOrder);
					List<SourceItem> newItems=new ArrayList<SourceItem>();
					for (PropertySortHolder holder : outputProperties) {
						newItems.add(holder.getRawItem());
					}
					boolean addedHeader=addElementHeaderItem(section, sections, headerMap, newItems, usedSectionIDS, sortedItems);
					if (!addedHeader && newItems.size()>0 && sortedItems.size()>0)
						sortedItems.add(new NewSectionHeaderItem(getCRCountForElement(newItems.get(0), blankLinesMap, PreferenceConstants.ASRearr_Element_Function)));
					sortedItems.addAll(newItems);
				}
				else if (section.getReferenceID().equals(PreferenceConstants.ASRearr_Element_StaticFunction))
				{
					List<FunctionRecord> gatheredFunctions=new ArrayList<FunctionRecord>();
					for (int i = 0; i < allFunctions.size();)
					{
						FunctionRecord function = allFunctions.get(i);
						if ((function.getModifierFlags() & TopLevelItemRecord.ASDoc_Static)!=0)
						{
							//skip if this is a getter/setter and we are treating those as properties
							if (store.getBoolean(PreferenceConstants.ASRearr_TreatGettersAndSettersAsStaticProperties) && (function.getType()==FunctionRecord.Type_Getter || function.getType()==FunctionRecord.Type_Setter))
							{
								i++;
								continue;
							}
							
							allFunctions.remove(i);
							gatheredFunctions.add(function);
							continue;
						}
						i++;
					}
					sortFunctions(gatheredFunctions, store.getBoolean(PreferenceConstants.ASRearr_SortStaticFunctions), store.getString(PreferenceConstants.ASRearr_StaticFunctionVisibilityOrder), store.getBoolean(PreferenceConstants.ASRearr_UseStaticFunctionVisibilityOrder));
					List<PropertySortHolder> outputProperties=new ArrayList<PropertySortHolder>();
					for (FunctionRecord func : gatheredFunctions) {
						outputProperties.add(new PropertySortHolder(func));
					}
					insertHeaderSpecs(outputProperties, headerMap, PreferenceConstants.ASRearr_Element_StaticFunction, visMap, PreferenceConstants.ASRearr_SortStaticFunctions, PreferenceConstants.ASRearr_UseStaticFunctionVisibilityOrder);
					
					List<SourceItem> newItems=new ArrayList<SourceItem>();
					for (PropertySortHolder holder : outputProperties) {
						newItems.add(holder.getRawItem());
					}
					boolean addedHeader=addElementHeaderItem(section, sections, headerMap, newItems, usedSectionIDS, sortedItems);
					if (!addedHeader && newItems.size()>0 && sortedItems.size()>0)
						sortedItems.add(new NewSectionHeaderItem(getCRCountForElement(newItems.get(0), blankLinesMap, PreferenceConstants.ASRearr_Element_StaticFunction)));
					sortedItems.addAll(newItems);
				}
				else if (section.getReferenceID().equals(PreferenceConstants.ASRearr_Element_Property))
				{
					List<PropertyLine> gatheredProperties=new ArrayList<PropertyLine>();
					for (int i = 0; i < allProperties.size();)
					{
						PropertyLine property = allProperties.get(i);
						if ((property.getModifierFlags() & TopLevelItemRecord.ASDoc_Static)==0)
						{
							allProperties.remove(i);
							gatheredProperties.add(property);
							continue;
						}
						i++;
					}
					
					//grab getters/setters if configured
					List<FunctionRecord> gettersAndSetters=new ArrayList<FunctionRecord>();
					if (store.getBoolean(PreferenceConstants.ASRearr_TreatGettersAndSettersAsProperties))
					{
						for (int i = 0; i < allFunctions.size();)
						{
							FunctionRecord function = allFunctions.get(i);
							if ((function.getModifierFlags() & TopLevelItemRecord.ASDoc_Static)==0)
							{
								if (function.getType()==FunctionRecord.Type_Getter || function.getType()==FunctionRecord.Type_Setter)
								{
									allFunctions.remove(i);
									gettersAndSetters.add(function);
									continue;
								}
							}
							i++;
						}
					}
					
					List<PropertySortHolder> outputProperties=new ArrayList<PropertySortHolder>();
					moveAssociatedProperties(gatheredProperties, gettersAndSetters, outputProperties, mPrefs.getBoolean(PreferenceConstants.ASRearr_TreatGettersAndSettersAsProperties), mPrefs.getBoolean(PreferenceConstants.ASRearr_SortGettersAndSettersWithProperties));
					sortProperties(outputProperties, store.getBoolean(PreferenceConstants.ASRearr_SortProperties), store.getString(PreferenceConstants.ASRearr_PropertyVisibilityOrder), store.getBoolean(PreferenceConstants.ASRearr_UsePropertyVisibilityOrder));
					insertHeaderSpecs(outputProperties, headerMap, PreferenceConstants.ASRearr_Element_Property, visMap, PreferenceConstants.ASRearr_SortProperties, PreferenceConstants.ASRearr_UsePropertyVisibilityOrder);
					insertPropertyHeaders(outputProperties, PreferenceConstants.ASRearr_UseSectionHeaders, mPrefs.getInt(PreferenceConstants.ASRearr_AddDefaultHeaderForProperties), mPrefs.getBoolean(PreferenceConstants.ASRearr_SortGettersAndSettersWithProperties) && mPrefs.getBoolean(PreferenceConstants.ASRearr_TreatGettersAndSettersAsProperties));
					List<SourceItem> newItems=new ArrayList<SourceItem>();
					for (PropertySortHolder holder : outputProperties) {
						newItems.add(holder.getRawItem());
					}
					boolean addedHeader=addElementHeaderItem(section, sections, headerMap, newItems, usedSectionIDS, sortedItems);
					if (!addedHeader && newItems.size()>0 && sortedItems.size()>0)
						sortedItems.add(new NewSectionHeaderItem(getCRCountForElement(newItems.get(0), blankLinesMap, PreferenceConstants.ASRearr_Element_Property)));
					sortedItems.addAll(newItems);
				}
				else if (section.getReferenceID().equals(PreferenceConstants.ASRearr_Element_StaticProperty))
				{
					List<PropertyLine> gatheredProperties=new ArrayList<PropertyLine>();
					for (int i = 0; i < allProperties.size();)
					{
						PropertyLine property = allProperties.get(i);
						if ((property.getModifierFlags() & TopLevelItemRecord.ASDoc_Static)!=0)
						{
							allProperties.remove(i);
							gatheredProperties.add(property);
							continue;
						}
						i++;
					}

					//grab getters/setters if configured
					List<FunctionRecord> gettersAndSetters=new ArrayList<FunctionRecord>();
					if (store.getBoolean(PreferenceConstants.ASRearr_TreatGettersAndSettersAsStaticProperties))
					{
						for (int i = 0; i < allFunctions.size();)
						{
							FunctionRecord function = allFunctions.get(i);
							if ((function.getModifierFlags() & TopLevelItemRecord.ASDoc_Static)!=0)
							{
								if (function.getType()==FunctionRecord.Type_Getter || function.getType()==FunctionRecord.Type_Setter)
								{
									allFunctions.remove(i);
									gettersAndSetters.add(function);
									continue;
								}
							}
							i++;
						}
					}
					
					List<PropertySortHolder> outputProperties=new ArrayList<PropertySortHolder>();
					moveAssociatedProperties(gatheredProperties, gettersAndSetters, outputProperties, mPrefs.getBoolean(PreferenceConstants.ASRearr_TreatGettersAndSettersAsStaticProperties), mPrefs.getBoolean(PreferenceConstants.ASRearr_SortGettersAndSettersWithStaticProperties));
					sortProperties(outputProperties, store.getBoolean(PreferenceConstants.ASRearr_SortStaticProperties), store.getString(PreferenceConstants.ASRearr_StaticPropertyVisibilityOrder), store.getBoolean(PreferenceConstants.ASRearr_UseStaticPropertyVisibilityOrder));
					insertHeaderSpecs(outputProperties, headerMap, PreferenceConstants.ASRearr_Element_StaticProperty, visMap, PreferenceConstants.ASRearr_SortStaticProperties, PreferenceConstants.ASRearr_UseStaticPropertyVisibilityOrder);
					insertPropertyHeaders(outputProperties, PreferenceConstants.ASRearr_UseSectionHeaders, mPrefs.getInt(PreferenceConstants.ASRearr_AddDefaultHeaderForStaticProperties), mPrefs.getBoolean(PreferenceConstants.ASRearr_SortGettersAndSettersWithStaticProperties) && mPrefs.getBoolean(PreferenceConstants.ASRearr_TreatGettersAndSettersAsStaticProperties));
					List<SourceItem> newItems=new ArrayList<SourceItem>();
					for (PropertySortHolder holder : outputProperties) {
						newItems.add(holder.getRawItem());
					}
					
					if (newItems.size()>0)
					{
						boolean addedHeader=addElementHeaderItem(section, sections, headerMap, newItems, usedSectionIDS, sortedItems);
						if (!addedHeader && sortedItems.size()>0)
							sortedItems.add(new NewSectionHeaderItem(getCRCountForElement(newItems.get(0), blankLinesMap, PreferenceConstants.ASRearr_Element_StaticProperty)));
						sortedItems.addAll(newItems);
					}
				}
				else if (section.getReferenceID().equals(PreferenceConstants.ASRearr_Element_StaticInitializer))
				{
					boolean addedHeader=addElementHeaderItem(section, sections, headerMap, initializers, usedSectionIDS, sortedItems);
					if (!addedHeader && initializers.size()>0 && sortedItems.size()>0)
						sortedItems.add(new NewSectionHeaderItem(getCRCountForElement(initializers.get(0), blankLinesMap, PreferenceConstants.ASRearr_Element_StaticInitializer)));
					sortedItems.addAll(initializers);
				}
			}
		}
		
//		//get current file ordering so that I can detect if everything is already in the correct order.  This sorts by position in file,
//		//which is the default comparator for these objects.
//		List<SourceItem> currentFileOrder=new ArrayList<SourceItem>();
//		currentFileOrder.addAll(sortedItems);
//		Collections.sort(currentFileOrder);
		
//		//compare the two lists to see if there are any changes
//		if (currentFileOrder.size()==sortedItems.size())
//		{
//			boolean difference=false;
//			for (int i=0;i<currentFileOrder.size();i++)
//			{
//				SourceItem currentItem=currentFileOrder.get(i);
//				SourceItem sortedItem=sortedItems.get(i);
//				if (currentItem!=sortedItem)
//				{
//					difference=true;
//					break;
//				}
//			}
//			if (!difference)
//				return false;
//		}
		
		{
			List<ReorderHolder> insertItems=new ArrayList<ReorderHolder>();
			
			//collect all the text
			boolean seenFirstCodeItem=false;
			for (int j = 0; j < sortedItems.size(); j++) {
				SourceItem sourceItem = sortedItems.get(j);
				if (sourceItem instanceof TopLevelItemRecord)
				{
					List<MetadataItem> metatags=new ArrayList<MetadataItem>();
					metatags.addAll(((TopLevelItemRecord)sourceItem).getMetadataItems());
					sortMetatags(metatags);
					boolean firstItem=true;
					for (MetadataItem metadataItem : metatags) {
						metadataItem.nailDownPositions();
						insertItems.add(new ReorderHolder(metadataItem, workingDoc.get(metadataItem.getPreStartPos(), metadataItem.getPostEndPos()-metadataItem.getPreStartPos()), getIndentString(workingDoc, metadataItem.getPreStartPos()), firstItem && !seenFirstCodeItem && isAtFileStart(metadataItem.getPreStartPos(), workingDoc)));
						firstItem=false;
					}
					
					//special check here to see if the copyright header exists and happens to be before an item
					//at the top of the file.  In that case, we don't want the copyright header to be moved
					//around with the item, so we remove the header from the SourceItem and adjust the starting insert location
					//to reflect the end of the copyright comment.  Remove the copyright header means the header won't
					//be removed below.
					if (sourceItem.getPreStartPos()==0) //only applies if the item is at the top of the file
					{
						//find the headers for the item
						List<HeaderInfo> outputHeaders=new ArrayList<ASRearranger.HeaderInfo>();
						getHeadersFromItem(sourceItem, workingDoc.getDefaultLineDelimiter(), outputHeaders);
						if (outputHeaders.size()>0)
						{
							//check the first header to see if it is a copyright
							HeaderInfo header=outputHeaders.get(0);
							if (isCopyright(header, mCopyrightHeader)[0])
							{
								//remove the tokens corresponding to the copyright header
								insertLocation=header.getEndPos();
								int startTrimPos=header.getStartIndex();
								int endTrimPos=header.getEndIndex();
								sourceItem.removePreTokens(startTrimPos, endTrimPos+1);

								if (outputHeaders.size()==1) //if there was only one header
								{
									//trim pre-tokens if they are all whitespace
									boolean foundNonWS=false;
									for (TextItem item: sourceItem.getPreTokens()) {
										if (AntlrUtilities.asTrim(item.getText()).length()>0)
										{
											foundNonWS=true;
											break;
										}
									}
									if (!foundNonWS)
										sourceItem.removePreTokens(0, sourceItem.getPreTokens().size());
								}
							}
							
						}
					}
				}

				sourceItem.nailDownPositions();
				sourceItem.validateText(workingDoc);
				ReorderHolder holder=null;
				if (sourceItem instanceof NewSectionHeaderItem)
				{
					//find next non section header and use its indent
					String indentString=" ";
					for (int k=j+1;k<sortedItems.size();k++)
					{
						if (!(sortedItems.get(k) instanceof NewSectionHeaderItem))
						{
							indentString=findIndentAmount(workingDoc, sortedItems.get(k).getPreStartPos());
							break;
						}
					}
					holder=new ReorderHolder(sourceItem, ((NewSectionHeaderItem)sourceItem).getText(baseHeaderMap, workingDoc.getDefaultLineDelimiter(), indentString), "", false);
				}
				else
				{
					String indentString=getIndentString(workingDoc, sourceItem.getPreStartPos());
					holder=new ReorderHolder(sourceItem, workingDoc.get(sourceItem.getPreStartPos(), sourceItem.getPostEndPos()-sourceItem.getPreStartPos()), indentString, !seenFirstCodeItem && isAtFileStart(sourceItem.getPreStartPos(), workingDoc));
					seenFirstCodeItem=true;
				}
				insertItems.add(holder);
			}
			
			//delete the existing items
			for (ReorderHolder holder : insertItems)
			{
				SourceItem sourceItem=holder.getItem();
				if (sourceItem instanceof NewSectionHeaderItem)
					continue;
				workingDoc.replace(sourceItem.getPreStartPos(), sourceItem.getPostEndPos()-sourceItem.getPreStartPos(), "");
			}
//			System.out.println(workingDoc.get());
			
			//start inserting at the supplied insert location
//			int insertPos=0;
//			if (insertLocation!=null)
//				insertPos=insertLocation.getStartPos()+1;
			int insertPos=insertLocation;
			
			String initialCR=workingDoc.getDefaultLineDelimiter();
			if (insertPos>0)
			{
//				String wholeDoc=workingDoc.get();
				workingDoc.replace(insertPos, 0, initialCR);
				insertPos+=initialCR.length();
			}
			
			insertPos=insertItems(insertItems, headerMap, workingDoc, insertPos, insideMXML);
			int endPos=workingDoc.getLength();
			if (endOfRange!=null)
				endPos=endOfRange.getStartPos();
			clearExtraBlankLines(workingDoc, insertPos, endPos);
		}
		
//		clearExtraBlankLines(workingDoc, 0, initialInsertPos);
//		creator.nailDownPositions();
		return true;
	}
	
	private void prefilterMembers(List<FunctionRecord> allFunctions, List<PropertyLine> allProperties, MemberSelectionSpec spec,
			String className, List<PropertySortHolder> results)
	{
		//grab all functions that match
		List<FunctionRecord> gettersAndSetters=new ArrayList<FunctionRecord>();
		for (int i = 0; i < allFunctions.size();)
		{
			FunctionRecord function = allFunctions.get(i);
			if (spec.matches(function, className))
			{
				allFunctions.remove(i);
				if (!spec.isFunction())
					gettersAndSetters.add(function); //just in case we need them
				else
					results.add(new PropertySortHolder(function));
				continue;
			}
			i++;
		}

		//grab all properties that match
		List<PropertyLine> capturedProperties=new ArrayList<PropertyLine>();
		propertyLoop: for (int i = 0; i < allProperties.size();)
		{
			PropertyLine property = allProperties.get(i);
			if (spec.matches(property, className))
			{
				allProperties.remove(i);
				capturedProperties.add(property);
				continue;
			}
			else if (spec.isFunction() && spec.isIncludeAssociatedProperty())
			{
				//see if it matches any of the functions that have been selected
				Set<String> output=new HashSet<String>();
				for (DeclRecord decl : property.getProperties()) {
					addPossibleImplicitPropertyNames(decl.getName().getText(), output);
				}
				for (PropertySortHolder holder : results) {
					if (holder.getTopLevelItem() instanceof FunctionRecord)
					{
						FunctionRecord fr=(FunctionRecord)holder.getTopLevelItem();
						if ((fr.getType()==FunctionRecord.Type_Getter || fr.getType()==FunctionRecord.Type_Setter) && output.contains(fr.getName().getText()))
						{
							allProperties.remove(i);
							PropertySortHolder associatedPropery=new PropertySortHolder(property);
							
							//whether we change the override name will determine whether the property names sort with the getter functions or just alphabetically.
							if ((spec.getSortFlags() & MemberSelectionSpec.Associate_Getters)!=0)
								associatedPropery.setOverrideSortName(fr.getName().getText());
							if ((spec.getSortFlags() & MemberSelectionSpec.Associate_Getters)!=0)
								associatedPropery.setOverridePosition(fr.getStartPos()-2); //make sure before function(s)
							results.add(associatedPropery);
							continue propertyLoop;
						}
					}
				}
			}
			i++;
		}

		//for properties, handle the implicit property case
		List<PropertySortHolder> implicitProperties=new ArrayList<PropertySortHolder>();
		if (spec.isFunction())
		{
			//put back to being sorted by file location
			Collections.sort(results, new Comparator<PropertySortHolder>()
			{
				public int compare(PropertySortHolder o1, PropertySortHolder o2)
				{
					return o1.getFilePosition()-o2.getFilePosition();
				}
			});			
		}
		else
		{
			//spec is a property, so we need to find implicit functions
//			boolean sort=((spec.getSortFlags() & MemberSelectionSpec.Sort_On)!=0); // && spec.isIncludeGetters() && ((spec.getSortFlags() & MemberSelectionSpec.Associate_Getters)!=0);
			
			if (spec.isIncludeGetters())
			{
				//first, find additional properties that correspond to getter/setter functions that were grabbed directly by the selector
				Map<String, FunctionRecord> grabbedFunctions=new HashMap<String, FunctionRecord>();
				for (FunctionRecord func : gettersAndSetters) {
					grabbedFunctions.put(func.getName().getText(), func);
				}
				
				for (int i = allProperties.size()-1;i>=0;i--)
				{
					PropertyLine property = allProperties.get(i);
					List<String> output=new ArrayList<String>();
					for (DeclRecord decl : property.getProperties()) {
						addPossibleImplicitPropertyNames(decl.getName().getText(), output);
					}
					
					for (String possibleFunctionName : output)
					{
						if (grabbedFunctions.containsKey(possibleFunctionName))
						{
							allProperties.remove(i);
							capturedProperties.add(property);
							break;
						}
					}
				}
				
				//then attempt to find and associate all of the properties 
				moveAssociatedProperties(capturedProperties, gettersAndSetters, implicitProperties, spec.isIncludeGetters(), (spec.getSortFlags() & MemberSelectionSpec.Associate_Getters)!=0);

				//find other implicit properties that match the implicit properties 
				//selected (for example, if we already have the getter, but not the setter for a particular name).
				//This can happen if the getter is public (grabbed), but the setter is private (not selected).
				List<PropertySortHolder> addedFunctions=new ArrayList<PropertySortHolder>();
				for (int i=allFunctions.size()-1;i>=0;i--)
				{
					for (PropertySortHolder holder : implicitProperties) {
						if (holder.getRawItem() instanceof FunctionRecord)
						{
							if (holder.getSortName().equals(allFunctions.get(i).getName().getText()))
							{
								FunctionRecord rec=allFunctions.remove(i);
								PropertySortHolder newFunc=new PropertySortHolder(rec);
								
								//set the position of this guy if we are associating getters
								//so that they will sort properly.  We use the same position as the
								//other implicit property so that they will sort in the correct order
								//if only position sorting is used.
								if ((spec.getSortFlags() & MemberSelectionSpec.Associate_Getters)!=0)
								{
									int offset=0;
									if (rec.getType()==FunctionRecord.Type_Getter)
										offset=(-1); //if getter, other item is setter
									else
										offset=1; //if setter, other item is getter and we want to be beyond it
									newFunc.setOverridePosition(holder.getFilePosition()+offset);
								}
								addedFunctions.add(newFunc);
							}
						}
					}
				}
				implicitProperties.addAll(addedFunctions);
				
				//find other implicit properties that match the properties selected
				for (int i=allFunctions.size()-1;i>=0;i--)
				{
					PropertySortHolder holder=findMatchingImplicitProperty(implicitProperties, allFunctions.get(i), (spec.getSortFlags() & MemberSelectionSpec.Associate_Getters)!=0);
					if (holder!=null)
					{
						allFunctions.remove(i);
						implicitProperties.add(holder);
					}
				}
			}
			else
			{
				//just add them all to the list
				for (PropertyLine propertyLine : capturedProperties) {
					implicitProperties.add(new PropertySortHolder(propertyLine));
				}
				for (FunctionRecord func : gettersAndSetters) {
					implicitProperties.add(new PropertySortHolder(func));
				}
			}
			
			
			//put back to being sorted by file location
			Collections.sort(implicitProperties, new Comparator<PropertySortHolder>()
			{
				public int compare(PropertySortHolder o1, PropertySortHolder o2)
				{
					return o1.getFilePosition()-o2.getFilePosition();
				}
			});
			
			results.clear();
			results.addAll(implicitProperties);
		}
		
		spec.sortItems(results);
	}


	private void trimPreBlankLines(SourceItem nextItem)
	{
		//find the pretokens before this item (or its first metatag)
		SourceItem firstItem=nextItem;
		List<TextItem> preTokens=nextItem.getPreTokens();
		if (nextItem instanceof TopLevelItemRecord)
		{
			List<MetadataItem> metaTags=((TopLevelItemRecord)nextItem).getMetadataItems();
			if (metaTags.size()>0)
			{
				preTokens=metaTags.get(0).getPreTokens();
				firstItem=metaTags.get(0);
			}
		}
		
		//delete up until the last newline found
		int lastCR=(-1);
		for (int i=0;i<preTokens.size();i++)
		{
			TextItem preItem=preTokens.get(i);
			if (AntlrUtilities.asTrim(preItem.getText()).length()>0) //kick out if we hit any data; we don't want to delete any non-WS
				break;
			
			if (preItem.getText().contains("\n"))
				lastCR=i;
		}
		if (lastCR>=0)
			firstItem.removePreTokens(0, lastCR+1);
	}
	
	private int getCRCountForElement(SourceItem nextItem, int desiredBlankLinesBefore)
	{
		if (nextItem==null)
			return desiredBlankLinesBefore;

		//because of the way items are grabbed, the number of carriage returns in the pre-tokens IS 
		//the number of blank lines
		SourceItem firstItem=nextItem;
		int existingCount=0;
		List<TextItem> preTokens=nextItem.getPreTokens();
		if (nextItem instanceof TopLevelItemRecord)
		{
			List<MetadataItem> metaTags=((TopLevelItemRecord)nextItem).getMetadataItems();
			if (metaTags.size()>0)
			{
				preTokens=metaTags.get(0).getPreTokens();
				firstItem=metaTags.get(0);
			}
		}
		for (TextItem preItem : preTokens) {
			if (AntlrUtilities.asTrim(preItem.getText()).length()>0) //kick out if we hit any data
				break;
			if (preItem.getText().contains("\n"))
				existingCount++;
		}
		
		if (existingCount>desiredBlankLinesBefore)
		{
//			boolean firstDelete=true;
			int numToDelete=existingCount-desiredBlankLinesBefore;
			for (int i=0;i<preTokens.size();i++)
			{
				TextItem preItem=preTokens.get(i);
				if (AntlrUtilities.asTrim(preItem.getText()).length()>0) //kick out if we hit any data; we shouldn't but it's better to be safe
					break;
				if (preItem.getText().contains("\n"))
				{
//					if (firstDelete)
//						firstDelete=false;
//					else
//					{
					numToDelete--;
						if (numToDelete<=0)
						{
							firstItem.removePreTokens(0, i+1);
							break;
						}
//					}
				}
			}
			return 0;
		}
		
		return Math.max(0, desiredBlankLinesBefore-existingCount);
	}


	private int getCRCountForElement(SourceItem nextItem, Map<String, Integer> blankLinesMap, String elementID)
	{
		int desiredCount=0;
		Integer count=blankLinesMap.get(elementID);
		if (count==null)
			desiredCount=1;
		else
			desiredCount=count.intValue();
		return getCRCountForElement(nextItem, desiredCount);
	}


	private void insertPropertyHeaders(List<PropertySortHolder> properties, String useHeadersKey, int headerStyle, boolean associateHeaders)
	{
		if (mPrefs.getBoolean(useHeadersKey) && headerStyle!=PreferenceConstants.PropertyHeaders_None)
		{
			boolean alwaysAddHeaders=headerStyle==PreferenceConstants.PropertyHeaders_All;
			boolean smartHeaders=(headerStyle==PreferenceConstants.PropertyHeaders_AssociatedConditional && associateHeaders);
			boolean associatedOnly=(headerStyle==PreferenceConstants.PropertyHeaders_AssociatedOnly && associateHeaders);

			boolean addHeadersMode=false;
			mainLoop: for (int i=0;i<properties.size();i++)
			{
				PropertySortHolder holder=properties.get(i);
				if ((i==0 || holder.getTopLevelItem()==null) && smartHeaders)
				{
					//this is a section header.  If we're in smart mode, then we want to determine the header
					//mode for the current section.
					addHeadersMode=false; //turn mode back off because we need to check the next group
					
					for (int k=i+1;k+1<properties.size();k++)
					{
						PropertySortHolder test1=properties.get(k);
						PropertySortHolder test2=properties.get(k+1);
						if (test1.getTopLevelItem()==null || test2.getTopLevelItem()==null)
							break;
						if (test1.getSortName().equals(test2.getSortName()))
						{
							addHeadersMode=true;
							break;
						}
					}
				}
				if (holder.getTopLevelItem()!=null)
				{
					boolean needToAddPropertyHeader=false;
					if (addHeadersMode || alwaysAddHeaders)
						needToAddPropertyHeader=true;
					
					String sortName=holder.getSortName();
					
					if (!needToAddPropertyHeader && i+1<properties.size() && associatedOnly)
					{
						PropertySortHolder nextHolder=properties.get(i+1);
						if (sortName.equals(nextHolder.getSortName()))
						{
							needToAddPropertyHeader=true;
						}
					}
					
					if (needToAddPropertyHeader)
					{
						trimPreBlankLines(holder.getRawItem());
						properties.add(i, new PropertySortHolder(new NewSectionHeaderItem(new SectionSpec("", SectionSpec.MINOR, new String[]{" "+holder.getSortName()}, true))));
					}
					
					i++; //to move past the item we just added
					
					//skip past associated properties because we never want to put a header in the middle of them
					while (i<properties.size() && properties.get(i).getTopLevelItem()!=null && properties.get(i).getSortName().equals(sortName))
					{
						i++;
						if (i>=properties.size())
							break mainLoop;
					}
					i--; //back up to previous element to account for loop auto-increment
				}
			}
		}
	}


	/*
	 * return true if position is the first non-whitespace in the document 
	 */
	private boolean isAtFileStart(int preStartPos, Document workingDoc)
	{
		String text=workingDoc.get().substring(0, preStartPos);
		if (AntlrUtilities.asTrim(text).length()==0)
			return true;
		return false;
	}


	private PropertySortHolder findMatchingImplicitProperty(List<PropertySortHolder> sourceProperties, FunctionRecord func, boolean sortWithImplicit)
	{
		if (func.getType()==FunctionRecord.Type_Getter || func.getType()==FunctionRecord.Type_Setter)
		{
			String[] prefixes=getPropertyPrefixes();
			String funcName=func.getName().getText();
			for (PropertySortHolder holder : sourceProperties) {
				if (holder.getRawItem() instanceof PropertyLine)
				{
					PropertyLine propLine=(PropertyLine)holder.getRawItem();
					if ((func.getModifierFlags() & TopLevelItemRecord.ASDoc_Static)!=(propLine.getModifierFlags() & TopLevelItemRecord.ASDoc_Static))
						continue;
					String firstVarName=null;
					for (DeclRecord var : propLine.getProperties())
					{
						String varName=var.getName().getText();
						if (firstVarName==null)
							firstVarName=varName;
						for (String prefix : prefixes)
						{
							if (varName.startsWith(prefix))
							{
								if (varName.substring(prefix.length()).equals(funcName))
								{
									//we've found a matching location.
									PropertySortHolder newHolder=new PropertySortHolder(func);
									
//									boolean sortWithImplicit=true;
									if (sortWithImplicit)
									{
										//NB: this assumes that getter/setter have the same visibility
										
										//set the sort name of the variable to match the function.  For sorting purposes,
										//we want to use the name without the underscore.
										holder.setOverrideSortName(func.getName().getText());
										
										//set the sort position of the *function* to be just after the var; we are adding
										//implicit getters/setters *to* the properties.  We alter the offset
										//so that getter/setter will sort in the correct order.
										int offset=0;
										if (func.getType()==FunctionRecord.Type_Getter)
											offset=1;
										else
											offset=2;
										newHolder.setOverridePosition(propLine.getStartPos()+offset);
										
										//set the type of the function to match the variable.  For sorting purposes, the
										//type of the variable makes the most sense.
										newHolder.setOverrideSortType(MemberSelectionSpec.getTypeString(propLine));
										
										//if we haven't seen an associated getter/setter, update the property flags to match the function visibility
										//However, if we have seen a getter/setter, then update the function with that same
										//visibility.  In that case, the property should have been previously updated, so we copy
										//from it.
										if (holder.getOverrideVisibility()<0)
											holder.setOverrideVisibility(func.getModifierFlags());
										else
											newHolder.setOverrideVisibility(holder.getOverrideVisibility());
									}
									else
									{
//										//I'm not sure what I was going for here, so I'm commenting out
//										holder.setOverrideSortName(func.getName().getText());
////										newHolder.setOverrideSortName(varName);
//										newHolder.setOverrideVisibility(propLine.getModifierFlags());
									}
									return newHolder;
								}
							}
						}
					}
				}
			}
		}
		
		return null;
	}
	
	//move getter/setter functions from the gettersAndSetters list to the allProps list if they match any properties in gatheredProperties.
	private void moveAssociatedProperties(
			List<PropertyLine> gatheredProperties,
			List<FunctionRecord> gettersAndSetters,
			List<PropertySortHolder> outputProperties,
			boolean findAssociatedGetters,
			boolean sortGettersWithProperties)
	{
		//just stick all the properties in the output list
		for (PropertyLine prop : gatheredProperties) {
			outputProperties.add(new PropertySortHolder(prop));
		}
		
		if (findAssociatedGetters)
		{
			List<PropertySortHolder> originalProps=new ArrayList<PropertySortHolder>();
			originalProps.addAll(outputProperties);
			for (int i=gettersAndSetters.size()-1;i>=0;i--)
			{
				FunctionRecord func=gettersAndSetters.get(i);
				PropertySortHolder holder=findMatchingImplicitProperty(originalProps, func, sortGettersWithProperties);
				if (holder!=null)
					outputProperties.add(holder);
				else
					outputProperties.add(new PropertySortHolder(func)); //not associated with a physical property
			}
		}
		else
		{
			for (FunctionRecord func : gettersAndSetters) {
				outputProperties.add(new PropertySortHolder(func));
			}
		}
		
		//sort allProps based on file location
		Collections.sort(outputProperties, new Comparator<PropertySortHolder>()
		{
			public int compare(PropertySortHolder o1, PropertySortHolder o2)
			{
				return o1.getFilePosition()-o2.getFilePosition();
			}
		});
	}


//	public static void addGettersAndSetters(List<TopLevelItemRecord> newItems, List<PropertySortHolder> implicitProperties)
//	{
//		//sort the getter/setter methods with setters first for ease of insertion below, to enforce the
//		//order of property, getter, setter
//		Collections.sort(implicitProperties, new Comparator<PropertySortHolder>()
//		{
//			public int compare(PropertySortHolder o1, PropertySortHolder o2)
//			{
//				FunctionRecord f1=o1.mItem;
//				FunctionRecord f2=o2.mItem;
//				//return setters sorted first
//				if (f1.getType()==FunctionRecord.Type_Setter && f2.getType()==FunctionRecord.Type_Getter)
//					return -1;
//				if (f2.getType()==FunctionRecord.Type_Setter && f1.getType()==FunctionRecord.Type_Getter)
//					return 1;
//				
//				//we don't care if they're the same
//				return 0;
//			}
//		});
//
//		//now, insert them into the property list
//		for (PropertySortHolder holder : implicitProperties) {
//			for (int i=0;i<newItems.size();i++)
//			{
//				SourceItem item=newItems.get(i);
//				if (item instanceof PropertyLine)
//				{
//					String varName=((PropertyLine)item).getProperties().get(0).getName().getText();
//					if (varName.equals(holder.mAssociatedProperty))
//					{
//						newItems.add(i+1, holder.mItem);
//						break;
//					}
//				}
//			}
//		}
//	}
	
	private void addPossibleImplicitPropertyNames(String propName, Collection<String> output)
	{
		for (String prefix : getPropertyPrefixes())
		{
			if (propName.startsWith(prefix))
				output.add(propName.substring(prefix.length()));
		}
	}

//	public static void addPossiblePhysicalPropertyNames(String propName, Object associatedObject, Map<String, Object> output)
//	{
//		for (String prefix : getPropertyPrefixes()) {
//			output.put(prefix+propName, associatedObject);
//		}
//	}

	public static String[] getPropertyPrefixes()
	{
		return new String[]{"_", "__"};
	}

//	private void insertGetterSetterWithProperty(List<SourceItem> items, List<FunctionRecord> funcs)
//	{
//		if (funcs.size()==0)
//			return;
//		
//		//find getters/setters that are associated with properties.  
//		//Then sort in the ones that aren't associated.
//		//Then add in the 
//		List<PropertySortHolder> wrappedProps=new ArrayList<PropertySortHolder>();
//		String[] prefixes=getPropertyPrefixes();
//		main: for (FunctionRecord func : funcs) {
//			String funcName=func.getName().getText();
//			for (int i = 0; i < items.size(); i++) {
//				SourceItem	item=items.get(i);
//				if (item instanceof PropertyLine)
//				{
//					PropertyLine propLine=(PropertyLine)item;
//					for (DeclRecord var : propLine.getProperties())
//					{
//						for (String prefix : prefixes)
//						{
//							String varName=var.getName().getText();
//							if (varName.startsWith(prefix))
//							{
//								if (varName.substring(prefix.length()).equals(funcName))
//								{
//									wrappedProps.add(new PropertySortHolder(func, varName));
////									okay, we've found a matching location.  We need to either put the getter/setter
////									after this index.  We assume that setters are processed first so that we can always
////									just insert after the property and end up with 1. property 2. getter 3. setter
////									items.add(i+1, func);
//									continue main;
//								}
//							}
//						}
//					}
//				}
//			}
//			
//			wrappedProps.add(new PropertySortHolder(func, "")); //not associated with a property
//		}
//		
//		for (SourceItem prop : items) {
//			wrappedProps.add(new PropertySortHolder(prop, ""));
//		}
//		
//		Collections.sort(wrappedProps, new Comparator<PropertySortHolder>()
//		{
//			public int compare(PropertySortHolder o1, PropertySortHolder o2)
//			{
//				if (o1.mItem instanceof PropertyLine && o2.mItem instanceof PropertyLine)
//					return (((PropertyLine)o1.mItem).getProperties().get(0).getName().getText()).compareTo(((PropertyLine)o2.mItem).getProperties().get(0).getName().getText());
//				
//				if (o1.mItem instanceof FunctionRecord && o2.mItem instanceof FunctionRecord)
//				{
//					String f1=((FunctionRecord)o1.mItem).getName().getText();
//					String f2=((FunctionRecord)o2.mItem).getName().getText();
//					if (!f1.equals(f2)) //if the names aren't the same, then just sort by name
//						return f1.compareTo(f2);
//					//otherwise sort getter first
//					else if (((FunctionRecord)o1.mItem).getType()==FunctionRecord.Type_Getter)
//						return -1;
//					else
//						return 1;
//				}
//				
//				if (o1.mItem instanceof PropertyLine)
//				{
//					String propName=((PropertyLine)o1.mItem).getProperties().get(0).getName().getText();
//					if (propName.equals(o2.mAssociatedProperty))
//					{
//						return -1;
//					}
//					
//					String funcName=((FunctionRecord)o2.mItem).getName().getText();
//					return propName.compareTo(funcName);
//				}
//				else
//				{
//					String propName=((PropertyLine)o2.mItem).getProperties().get(0).getName().getText();
//					if (propName.equals(o1.mAssociatedProperty))
//					{
//						return 1;
//					}
//					
//					String funcName=((FunctionRecord)o1.mItem).getName().getText();
//					return funcName.compareTo(propName);
//				}
//			}
//		});
//		
//		items.clear();
//		for (PropertySortHolder holder : wrappedProps) {
//			items.add(holder.mItem);
//		}
//			
////		//if we fell through to here, then we want to insert alphabetically, but still getter before setter
////		for (int i = 0; i < items.size(); i++) {
////			SourceItem	item=items.get(i);
////			if (item instanceof PropertyLine)
////			{
////				//if we found the property that we are after
////				if (((PropertyLine)item).getProperties().get(0).getName().getText().compareTo(func.getName().getText())<0)
////				{
////					
////				}
////			}
////		}		
//	}


	private boolean addElementHeaderItem(ISectionItem section, List<ISectionItem> allSections, Map<String, SectionSpec> headerMap, List<? extends SourceItem> itemsInSection, Set<String> usedSectionIDS, List<SourceItem> outputItemList)
	{
		if (itemsInSection.size()==0)
			return false;
		
		boolean addedHeader=false;
		String[] ids=new String[]{section.getReferenceID()+SpanningSuffix, section.getReferenceID()};
		for (String secID : ids) {
			SectionSpec header=getSectionSpec(secID, allSections, headerMap);
			if (header!=null && header.isUseHeader() && !usedSectionIDS.contains(header.getID()))
			{
				trimPreBlankLines(itemsInSection.get(0));
				usedSectionIDS.add(header.getID());
				outputItemList.add(new NewSectionHeaderItem(header));
				addedHeader=true;
			}
		}
		
		return addedHeader;
	}

	public static SectionSpec getSectionSpec(String sectionID, List<ISectionItem> sections, Map<String, SectionSpec> specMap)
	{
		if (!sectionID.endsWith(SpanningSuffix))
			return specMap.get(sectionID);
		
		for (int i=0;i<sections.size();i++)
		{
			ISectionItem data=sections.get(i);
			String secID=data.getReferenceID()+SpanningSuffix;
			
			//get the section spec for this item
			SectionSpec tempSpec=specMap.get(secID);
			
			if (tempSpec==null)
			{
				if (secID.equals(sectionID))
					return null;
				continue;
			}

			//if it matches the one of interest, then this is the one we want
			if (tempSpec.getID().equals(sectionID))
			{
				return tempSpec;
			}
			
			String endSection=tempSpec.getEndSpanSectionID()+SpanningSuffix;
			if (endSection.equals(tempSpec.getID()))
				continue;
			
			while (i+1<sections.size())
			{
				i++;
				data=sections.get(i);
				secID=data.getReferenceID()+SpanningSuffix;
				
				//if we found the section we're looking for, then use the spanning section spec
				if (secID.equals(sectionID))
				{
					return tempSpec;
				}
				
				//if we found the end section of this span, then quit searching.  The loop
				//iterator should be in the correct position.
				if (secID.equals(endSection))
					break;
			}
		}

		return null;
	}
	
	private void insertHeaderSpecs(List<PropertySortHolder> items, Map<String, SectionSpec> headerMap, String rootElement, Map<Integer, String> visibilities, String doSortKey, String useVisibilityKey)
	{
//		boolean sortProperties=mPrefs.getBoolean(doSortKey);
		boolean useVisOrder=mPrefs.getBoolean(useVisibilityKey);
		if (/*sortProperties &&*/ useVisOrder)
		{
			Map<Integer, SectionSpec> headers=new HashMap<Integer, SectionSpec>();
			for (Integer vis : visibilities.keySet()) {
				SectionSpec spec=headerMap.get(rootElement+"#"+visibilities.get(vis));
				if (spec!=null && spec.isUseHeader())
					headers.put(vis, spec);	
			}
			
			for (int i = 0; i < items.size(); i++) {
				PropertySortHolder rec=(PropertySortHolder)items.get(i);
				
				//if no more visibility items left, then kick out
				if (headers.size()==0)
					break;
				
				Set<Integer> tempSet=new HashSet<Integer>();
				tempSet.addAll(headers.keySet());
				for (Integer vis : tempSet) {
					if ((rec.getSortVisibility() & vis)!=0)
					{
						trimPreBlankLines(rec.getRawItem());
						items.add(i, new PropertySortHolder(new NewSectionHeaderItem(headers.get(vis))));
						headers.remove(vis);
						break;
					}
				}
			}
		}
	}
	
	private void getHeadersFromArray(String[] preItems, String lineDelim, List<HeaderInfo> outputHeaders)
	{
		outputHeaders.clear();
		int lastCommentEnd=(-1);
		int startCommentIndex=(-1);
		int endCommentIndex=(-1);
		String prefix="";
		boolean seenNextCR=false;
		String repeatChars="";
		for (int i = 0; i < preItems.length; i++) {
			String line = preItems[i];
			//token can't cross lines.  // comment will have a line delimiter on the end
			if ((line.endsWith(lineDelim) && line.startsWith(SlashSlash)) || 
				(line.indexOf(lineDelim)<0 && line.startsWith(SlashStar) && line.endsWith(StarSlash) && isCRNextNonWhitespace(i+1, preItems, lineDelim)))
			{
				if (startCommentIndex<0)
				{
					//make sure string looks like a start line
					String[] repeatChar=new String[]{""};
					if (!isHeaderBoundaryLine(line, repeatChar))
						continue;
					
					if (stringContainsContent(line))
						continue;
					startCommentIndex=i;
					prefix=line.substring(0, 2);
					repeatChars=repeatChar[0];
					seenNextCR=false;
				}
				else
				{
					seenNextCR=false;
					endCommentIndex=i;
					if (isHeaderBoundaryLine(line, new String[]{repeatChars}))
					{
						endCommentIndex=i;
						if (endCommentIndex-startCommentIndex+1>=3)
						{
							//found a comment
							List<String> contentLines=new ArrayList<String>();
							int startPos=0;
							
							//walk backward and include whitespace items previous to the comment.  However,
							//we want to leave at least one CR if we have a // header.  A /* header will always
							//have at least one CR following it.
							int lastCRIndex=(-1);
							int originalStart=startCommentIndex;
							while (startCommentIndex-1>lastCommentEnd && startCommentIndex>0)
							{
								String text=preItems[startCommentIndex-1];
								if (AntlrUtilities.asTrim(text).length()==0)
								{
									startCommentIndex--;
									if (text.indexOf(lineDelim)>=0)
										lastCRIndex=startCommentIndex;
								}
								else
									break;
							}
//							if (prefix.equals("//") && lastCRIndex>=0)
//								startCommentIndex=lastCRIndex+1;
							
							for (int k=0;k<startCommentIndex;k++)
							{
								startPos+=preItems[k].length();
							}
							int endPos=startPos;
							for (int k=startCommentIndex;k<=endCommentIndex;k++)
							{
								if (k>=originalStart) //to handle the additional leading whitespace we've grabbed
									contentLines.add(preItems[k]);
								endPos+=preItems[k].length();
							}
							if (prefix.equals(SlashStar)) //for /* comments, we need to grab the next carriage return
							{
								while (true)
								{
									if (endCommentIndex+1<preItems.length)
									{
										String data=preItems[endCommentIndex+1];
										
										//if the next token is all whitespace, then grab this token as well 
										if (AntlrUtilities.asTrim(data).length()==0)
										{
											endCommentIndex++;
											endPos+=data.length();											
										}
										else
											break; //otherwise, we're done
										
										//if this is a CR, then quit
										if (data.indexOf(lineDelim)>=0)
											break;
									}
									else
										break;
								}
								i=endCommentIndex;
							}
							lastCommentEnd=endCommentIndex;
							String[] contentData=getContentFromHeader(contentLines.toArray(new String[]{}));
//							HeaderInfo header=new HeaderInfo(contentData, countCharsToLine(lines, lineDelim.length(), startCommentLine)+1, countCharsToLine(lines, lineDelim.length(), endCommentLine+1));
							HeaderInfo header=new HeaderInfo(contentData, startCommentIndex, endCommentIndex, startPos, endPos);
							outputHeaders.add(header);
						}
						
						//clear this comment and start looking for next one
						startCommentIndex=(-1);
					}
				}
			}
			else if (startCommentIndex>=0)
			{
				//if we are in the middle of a header, then we need to determine if there is a blank line here that 
				//means that we don't actually have a section comment.
				
				//if we have non-whitespace text, then this may be a metatag or similar, which would break up the 
				//current comment.  Or it could even be code.
				if (AntlrUtilities.asTrim(line).length()!=0)
				{
					startCommentIndex=(-1);
				}
				
				//whitespace is okay, as long as it doesn't contain carriage returns
				if (AntlrUtilities.asTrim(line).length()==0 && line.indexOf(lineDelim)<0)
					continue;

				//if we are matching a /* pattern, but we haven't seen the line-ending CR yet.
				if (prefix.equals(SlashStar) && !seenNextCR)
				{
					//if we find one line delimiter, but NOT a second one then we're okay.  This is only true
					//if the token contains only whitespace because the next non-whitespace token needs to be another
					// /* comment for the pattern to hold
					int delimPos=line.indexOf(lineDelim);
					if (AntlrUtilities.asTrim(line).length()==0 && delimPos>=0 && line.indexOf(lineDelim, delimPos+lineDelim.length())<0)
					{
						seenNextCR=true; //we've seen the CR
						continue;
					}

					startCommentIndex=(-1);
					continue;
				}

				//if we see a line delimiter, then there is an extra blank line and therefore, we do not have
				//a header comment.
				if (line.indexOf(lineDelim)>=0)
				{
					startCommentIndex=(-1);
				}
				
			}
		}
	}
	
	private void getHeadersFromItem(SourceItem item, String lineDelim, List<HeaderInfo> outputHeaders)
	{
		List<TextItem> preTokens=item.getPreTokens();
		outputHeaders.clear();
		List<String> tokens=new ArrayList<String>();
		for (TextItem token : preTokens) {
			tokens.add(token.getText());
		}
		getHeadersFromArray(tokens.toArray(new String[]{}), lineDelim, outputHeaders);
	}

	//return true if there is a carriage return before the next non-whitespace character
	private boolean isCRNextNonWhitespace(int startIndex, String[] preTokens, String lineDelim)
	{
		for (int i=startIndex;i<preTokens.length;i++)
		{
			String text=preTokens[i];
			int delimIndex=text.indexOf(lineDelim); //if there is a carriage return
			if (delimIndex>=0)
			{
				//now, see if there is a non-whitespace char before the carriage return
				for (int k=0;k<delimIndex;k++)
				{
					char c=text.charAt(k);
					if (!AntlrUtilities.isASWhitespace(c))
						return false;
				}
				return true;
			}
			else if (AntlrUtilities.asTrim(text).length()>0)
				break;
		}
		return false;
	}

	private String[] getContentFromHeader(String[] lines)
	{
		List<String> output=new ArrayList<String>();
		for (int i=1;i+1<lines.length;i++) //skip 1st and last lines
		{
			String tempLine=AntlrUtilities.asTrim(lines[i]);
			if (tempLine.endsWith(StarSlash))
				tempLine=tempLine.substring(0, tempLine.length()-2);
			if (tempLine.startsWith(SlashSlash) || tempLine.startsWith(SlashStar))
				tempLine=tempLine.substring(2);
			if (AntlrUtilities.asTrim(tempLine).length()==0)
				output.add("");
			else
				output.add(tempLine);
		}
		return output.toArray(new String[]{});
	}
	
	private boolean isHeaderBoundaryLine(String line, String[] repeatChar) {
		String testLine=line.substring(2);
		if (line.endsWith(StarSlash))
			testLine=testLine.substring(0, testLine.length()-2);
		testLine=AntlrUtilities.asTrim(testLine);
		if (testLine.length()==0)
			return false;
		
		//assign a minimum number of repeat characters for considering this a boundary line.
		if (testLine.length()<10)
			return false;
		
		//use the repeat char passed in if there is one
		char testChar=testLine.charAt(0);
		if (repeatChar[0].length()==0)
			repeatChar[0]=""+testChar;
		else
			testChar=repeatChar[0].charAt(0);
			
		for (int k=0;k<testLine.length();k++)
		{
			if (testLine.charAt(k)!=testChar)
				return false;
		}
		return true;
	}


	private boolean stringContainsContent(String data)
	{
		//check for alphanumerics on line.  If we find any, then don't treat this as the start line of a comment
		for (int k=0;k<data.length();k++)
		{
			char c=data.charAt(k);
			if (Character.isLetterOrDigit(c))
			{
				return true;
			}
		}
		
		return false;
	}

	private int insertItems(List<ReorderHolder> insertItems, Map<String, SectionSpec> headerMap, Document workingDoc, int insertPos, boolean insideMXML) throws BadLocationException, BadPositionCategoryException
	{
		if (mPrefs.getBoolean(PreferenceConstants.ASRearr_UseSectionHeaders))
		{
			boolean removeAllExisting=mPrefs.getBoolean(PreferenceConstants.ASRearr_RemoveAllExistingHeaders);
//			int i=(insideClass ? 0 : 1);
			ReorderHolder copyrightItem=null;
			boolean firstRealTokenSeen=false;
			for (int i=0; i < insertItems.size(); i++) //start at '1' because we don't want to delete a leading copyright block
			{ 
				ReorderHolder reorderHolder = insertItems.get(i);
				if (!(reorderHolder.getItem() instanceof NewSectionHeaderItem))
				{
					boolean tempFirstTokenSeen=firstRealTokenSeen;
					firstRealTokenSeen=true;
//					//also, skip metadata items because we're not doing anything with them for headers.
//					if (reorderHolder.getItem() instanceof MetadataItem)
//						continue;
					
					//remove all previously existing headers if the flag is set.
					List<HeaderInfo> headers=new ArrayList<HeaderInfo>();
//					getHeadersFromText(reorderHolder.getText(), workingDoc.getDefaultLineDelimiter(), headers);
					getHeadersFromItem(reorderHolder.getItem(), workingDoc.getDefaultLineDelimiter(), headers);
					String trimmedText="";
					String removedText="";
					int previousStartPos=(-1);
					
					SourceItem implicitTestItem=reorderHolder.getItem();
					if (implicitTestItem instanceof MetadataItem)
					{
						//walk forward to the next propertyline or function and then compare against that name
						for (int z=i+1;z<insertItems.size();z++)
						{
							SourceItem testItem=insertItems.get(z).getItem();
							if (testItem instanceof FunctionRecord || testItem instanceof PropertyLine)
							{
								implicitTestItem=testItem;
								break;
							}
						}
					}
					
					
					for (int k=headers.size()-1;k>=0;k--)
					{
						HeaderInfo header=headers.get(k);
						
						//assume that a copyright-type comment at the start of the file is a copyright comment.  Unless this
						//is a block inside an mxml file.
						boolean removeThisItem=false;
						if (!insideMXML && !tempFirstTokenSeen && k==0 && reorderHolder.isAtFileStart() && isCopyright(header, mCopyrightHeader)[0])
						{
							//we've identified that this is a copyright item.  If we're not using copyrights, then
							//we want to preserve this one and readd it at the beginning.
							removeThisItem=true;
							if (mPrefs.getBoolean(PreferenceConstants.ASRearr_UseCopyright) && mPrefs.getBoolean(PreferenceConstants.ASRearr_RemoveExistingCopyrightHeaders))
							{
								copyrightItem=new ReorderHolder(new NewSectionHeaderItem(mCopyrightHeader), mCopyrightHeader.generateHeader(workingDoc.getDefaultLineDelimiter(), ""), "", false);
							}
							else
							{
								copyrightItem=new ReorderHolder(new NewSectionHeaderItem(new SectionHeader(SectionHeader.AS_Section_Style_SlashSlash, 40, 0, "-", new String[]{}, 1)), reorderHolder.getText().substring(0, header.getEndPos()), "", false);
							}
						}
						
						//if the item is a property and matches the header contents
						boolean isImplicitPropertyHeader=false;
						if (implicitTestItem!=null)
						{
							if (implicitTestItem instanceof PropertyLine)
							{
								String varName=((PropertyLine)implicitTestItem).getProperties().get(0).getName().getText();
								if (matchesVarName(AntlrUtilities.asTrim(convertContentLinesToString(getContentFromHeader(header.getContentLines()))), varName))
								{
									isImplicitPropertyHeader=true;
								}
							}
							else if (implicitTestItem instanceof FunctionRecord)
							{
								String varName=((FunctionRecord)implicitTestItem).getName().getText();
								if (AntlrUtilities.asTrim(convertContentLinesToString(getContentFromHeader(header.getContentLines()))).equals(varName))
								{
									isImplicitPropertyHeader=true;
								}
							}
						}
						
						if (removeThisItem || isImplicitPropertyHeader || (removeAllExisting && !(reorderHolder.getItem() instanceof MetadataItem)) || contentLinesMatchAHeader(header.getContentLines(), headerMap))
						{
							int startTrimPos=header.getStartIndex();
							int endTrimPos=header.getEndIndex();
							reorderHolder.getItem().removePreTokens(startTrimPos, endTrimPos+1);
							
							//removed text is appended for each removal, because we need everything removed
							removedText+=reorderHolder.getText().substring(header.getStartPos(), header.getEndPos());
							
							//trimmed text is what's left after the item has been removed.
							if (previousStartPos<0)
								trimmedText=reorderHolder.getText().substring(header.getEndPos())+trimmedText;
							else
								trimmedText=reorderHolder.getText().substring(header.getEndPos(), previousStartPos)+trimmedText;
							previousStartPos=header.getStartPos();
						}
					}
					if (previousStartPos>=0)
						trimmedText=reorderHolder.getText().substring(0, previousStartPos)+trimmedText;
					else
						trimmedText=reorderHolder.getText();
					
					reorderHolder=new ReorderHolder(reorderHolder.getItem(), trimmedText, reorderHolder.getIndent(), reorderHolder.isAtFileStart());
					mRemovedText.append(removedText);
					insertItems.set(i, reorderHolder); //replace item in list
				}
			}
			
			//stick the copyright at the start of the file so that it will be in the right order with other possible headers
			if (copyrightItem!=null)
				insertItems.add(0, copyrightItem);
		}
		
		for (int k = 0; k < insertItems.size(); k++) {
			ReorderHolder reorderHolder = insertItems.get(k);
			String preData=""; //workingDoc.getDefaultLineDelimiter();
			preData+=reorderHolder.getIndent();
			String postData=workingDoc.getDefaultLineDelimiter();
			
			//don't add the default line delimeter if the item text ended with a line delimiter.  This
			//happens for line comments.  I don't know of any other cases right now.
			for (int i=reorderHolder.getText().length()-1;i>=0;i--)
			{
				char ch=reorderHolder.getText().charAt(i);
				if (!AntlrUtilities.isASWhitespace(ch))
					break;
				if (ch=='\n' || ch=='\r')
				{
					postData="";
					break;
				}
			}
			
			for (int i=0;i<reorderHolder.getItem().getBlanksAfter();i++)
			{
				postData+=workingDoc.getDefaultLineDelimiter();
			}
			
			//if this is added stuff, then add it to our running string containing new data so that the
			//char counts will match up
			if (reorderHolder.getItem() instanceof NewSectionHeaderItem)
			{
				//determine if the next item non-header contains a header.  If so, then throw away this one, assuming
				//that we should keep the old one
				NewSectionHeaderItem sectionItem=(NewSectionHeaderItem)reorderHolder.getItem();
				if (!sectionItem.isCopyright() && !sectionItem.isWhitespace())
				{
					//find the next non section header
					int j=k+1;
					for (;j<insertItems.size();j++)
					{
						if (!(insertItems.get(j).getItem() instanceof NewSectionHeaderItem))
							break;
					}
					
					if (j<insertItems.size())
					{
						//see if this item has headers associated with it.  If so, then skip the current header item (i.e. don't
						//inserted it into the output stream.  This has the known side effect of possibly skipping 2
						//new header items in some cases.
						List<HeaderInfo> headers=new ArrayList<HeaderInfo>();
						getHeadersFromItem(insertItems.get(j).getItem(), workingDoc.getDefaultLineDelimiter(), headers);
						if (headers.size()>0)
							continue;
					}
				}
				
				//remove the carriage return from the postData if whitespace is the whole purpose of this section item
				if (sectionItem.isWhitespace())
					postData="";
				else
				{
					//if there is a next item and it's a header, then don't add the blank line; the next item
					//will either contain only whitespace or leading whitespace
					if (k+1<insertItems.size() && (insertItems.get(k+1).getItem() instanceof NewSectionHeaderItem))
					{}
					else //or add the blank line after this header (if the next item isn't a blank line)
					{
						boolean addNewlineAfter=true;
						//see if there is at least one newline in the leading whitespace of the next item.  If 
						//so, then we don't need to add another.
						if (k+1<insertItems.size())
						{
							ReorderHolder holder=insertItems.get(k+1);
							String text=holder.getText();
							for (int z=0;z<text.length();z++)
							{
								char c=text.charAt(z);
								if (!AntlrUtilities.isASWhitespace(c))
									break;
								if (c=='\r' || c=='\n')
								{
									addNewlineAfter=false;
									break;
								}
							}
						}
						if (addNewlineAfter)
							postData+=workingDoc.getDefaultLineDelimiter(); //create a blank line after header by default
					}
				}

				//skip if we're at the start of the 'file' and it's just whitespace
				if (insertPos==0 && sectionItem.isWhitespace())
					continue;
				
				mAddedText.append(reorderHolder.getText());
			}
			
			String textToInsert=preData+reorderHolder.getText()+postData;
			workingDoc.replace(insertPos, 0, textToInsert);
			String updatedDoc=workingDoc.get();
			reorderHolder.getItem().resetPositions(insertPos+preData.length()-reorderHolder.getItem().getNailedDownStartPos(), workingDoc);
			reorderHolder.getItem().validateText(workingDoc);
			insertPos+=textToInsert.length();
			System.out.println(workingDoc.get());
		}
		
		return insertPos;
	}

	private boolean matchesVarName(String headerString, String varName)
	{
		String[] prefixes=getPropertyPrefixes();
		for (String prefix : prefixes) {
			if (varName.startsWith(prefix))
			{
				if (varName.substring(prefix.length()).equals(headerString))
					return true;
			}
		}
		return false;
	}


	private static boolean contentLinesMatchAHeader(String[] contentLines, Map<String, SectionSpec> headerMap)
	{
		String contentAsString=convertContentLinesToString(contentLines);
		
		for (SectionSpec spec : headerMap.values()) {
			String[] testHeaderLines=spec.getText();
			
			//I want to ignore whitespace/newlines because I think that will typically not matter.  I just don't want to
			//lose typed characters.
			String testHeaderAsString=convertContentLinesToString(testHeaderLines);
			if (contentAsString.equalsIgnoreCase(testHeaderAsString)) //seems like case insensitive is okay and will mask some unimportant differences
				return true;
		}
		return false;
	}
	
	private static String convertContentLinesToString(String[] contentLines)
	{
		StringBuffer buffer=new StringBuffer();
		for (String line : contentLines) {
			buffer.append(AntlrUtilities.asTrim(line));
		}
		return buffer.toString();
	}

	private void sortNamespaces(List<PropertyLine> namespaces)
	{
		Collections.sort(namespaces, new Comparator<PropertyLine>()
		{
			public int compare(PropertyLine o1, PropertyLine o2)
			{
				return o1.getProperties().get(0).getName().getText().compareTo(o2.getProperties().get(0).getName().getText());
			}
		});
	}

	private void clearExtraBlankLines(Document workingDoc, int startPos, int endPos) throws BadLocationException
	{
		//strip out extra blank lines (more than 1 in a row) so that so much whitespace isn't left lying around after the
		//package open brace or at the end of the class
		int startLine=workingDoc.getLineOfOffset(startPos);
		if (endPos>workingDoc.getLength())
			endPos=workingDoc.getLength();
		int endLine=workingDoc.getLineOfOffset(endPos);
		for (int i=startLine;i<workingDoc.getNumberOfLines()-1;)
		{
			int lineOffset=workingDoc.getLineOffset(i);
			int nextLineOffset=workingDoc.getLineOffset(i+1);
			String data=workingDoc.get(lineOffset, workingDoc.getLineLength(i));
			if (AntlrUtilities.asTrim(data).length()==0)
			{
				//delete line; decrement endLine since we've removed a line
				workingDoc.replace(lineOffset, nextLineOffset-lineOffset, "");
				endLine--;
			}
			else
			{
				//there was content, so skip to next line
				i++;
				if (i>=endLine) //kick out if we're beyond our specified boundary
					break;
			}
		}
	}
	
	private String findIndentAmount(IDocument doc, int startPos) throws BadLocationException
	{
		//walk forward until I find the next non-whitespace char, then get the indent based on that char
		for (;startPos<doc.getLength();startPos++)
		{
			char ch=doc.getChar(startPos);
			if (!AntlrUtilities.isASWhitespace(ch))
				break;
		}
		
		return getIndentString(doc, startPos);
	}

	private String getIndentString(IDocument doc, int startPos) throws BadLocationException
	{
		int lineStart=doc.getLineOffset(doc.getLineOfOffset(startPos));
		String indent=doc.get(lineStart, startPos-lineStart);
		StringBuffer buffer=new StringBuffer();
		for (int i = 0; i < indent.length(); i++) {
			char c=indent.charAt(i);
			if (AntlrUtilities.isASWhitespace(c))
				buffer.append(c);
			else
				break; //kick out if we hit a non-whitespace character.  This might happen if we have another element (such as a metatag) earlier on the line
		}
		
		return buffer.toString();
	}

	private void sortMetatags(List<MetadataItem> metatags)
	{
		String leftoverGroup="<>";
		Map<String, List<MetadataItem>> groupMap=new HashMap<String, List<MetadataItem>>();
		List<String> itemOrder=new ArrayList<String>();
		if (mPrefs.getBoolean(PreferenceConstants.ASRearr_UseMetatagOrder))
		{
			String orderData=mPrefs.getString(PreferenceConstants.ASRearr_MetatagOrder);
			String[] order=orderData.split(PreferenceConstants.AS_Pref_Line_Separator);
			itemOrder.addAll(Arrays.asList(order));
			Set<String> orderHash=new HashSet<String>();
			orderHash.addAll(itemOrder);
			itemOrder.add(leftoverGroup);
			
			for (MetadataItem item : metatags)
			{
				String type=item.getType().getText();
				if (!orderHash.contains(type))
					type=leftoverGroup;
				List<MetadataItem> group=groupMap.get(type);
				if (group==null)
				{
					group=new ArrayList<MetadataItem>();
					groupMap.put(type, group);
				}
				group.add(item);
			}
		}
		else
		{
			List<MetadataItem> results=new ArrayList<MetadataItem>();
			results.addAll(metatags);
			groupMap.put(leftoverGroup, results);
			itemOrder.add(leftoverGroup);
		}
		
		boolean sort=mPrefs.getBoolean(PreferenceConstants.ASRearr_SortMetatags);
		if (sort)
		{
			for (List<MetadataItem> metadataList : groupMap.values())
			{
				Collections.sort(metadataList, new Comparator<MetadataItem>()
				{
					public int compare(MetadataItem o1, MetadataItem o2)
					{
						int typeComp=o1.getType().getText().compareTo(o2.getType().getText());
						if (typeComp==0)
						{
							for (int i=0;i<o1.getArgs().size() && i<o2.getArgs().size();i++)
							{
								TextItem i1=o1.getArgs().get(i);
								TextItem i2=o2.getArgs().get(i);
								typeComp=i1.getText().compareTo(i2.getText());
								if (typeComp!=0)
									return typeComp;
							}
							
							//if the same args, then the one with more args sorts last
							typeComp=o1.getArgs().size()-o2.getArgs().size();
						}
						return typeComp;
					}
				});
			}
		}
		
		//now, copy the results back into the input array, possibly adding separators as specified
		metatags.clear();
		for (String item : itemOrder)
		{
			List<MetadataItem> group=groupMap.get(item);
			if (group!=null)
			{
				metatags.addAll(group);
			}
		}
		
	}

	private void sortIncludes(List<IncludeItem> includes)
	{
		boolean sort=mPrefs.getBoolean(PreferenceConstants.ASRearr_SortIncludes);
		if (sort)
		{
			Collections.sort(includes, new Comparator<IncludeItem>()
			{
				public int compare(IncludeItem o1, IncludeItem o2)
				{
					return o1.getIncludeFile().compareTo(o2.getIncludeFile());
				}
			});
		}
		else
		{
			Collections.sort(includes);
		}
	}

	private void sortImports(List<ImportRecord> imports)
	{
		String leftoverGroup="<>";
		Map<String, List<ImportRecord>> groupMap=new HashMap<String, List<ImportRecord>>();
		List<String> itemOrder=new ArrayList<String>();
		boolean groupImports=mPrefs.getBoolean(PreferenceConstants.ASRearr_UseImportOrder);
		if (groupImports)
		{
			String orderData=mPrefs.getString(PreferenceConstants.ASRearr_ImportOrder);
			String[] order=orderData.split(PreferenceConstants.AS_Pref_Line_Separator);
			itemOrder.addAll(Arrays.asList(order));
			itemOrder.add(leftoverGroup);
			
			//TODO: walk through imports in list and stick them in bucket for the longest prefix match.  This is an exhaustive search,
			//but with small 'n'
			for (ImportRecord imp : imports) {
				String longestPrefix="";
				imp.trimLeadingLines();
				for (String prefix : itemOrder) {
					if (prefix.equals(PreferenceConstants.ASRearr_ImportSeparator))
						continue;
					if (imp.getType().getText().startsWith(prefix))
					{
						if (prefix.length()>longestPrefix.length())
							longestPrefix=prefix;
					}
				}
				if (longestPrefix.length()==0)
					longestPrefix=leftoverGroup;
				List<ImportRecord> impList=groupMap.get(longestPrefix);
				if (impList==null)
				{
					impList=new ArrayList<ImportRecord>();
					groupMap.put(longestPrefix, impList);
				}
				impList.add(imp);
			}
		}
		else
		{
			List<ImportRecord> results=new ArrayList<ImportRecord>();
			results.addAll(imports);
			groupMap.put(leftoverGroup, results);
			itemOrder.add(leftoverGroup);
		}
		
		boolean sort=mPrefs.getBoolean(PreferenceConstants.ASRearr_SortImports);
		if (sort)
		{
			for (List<ImportRecord> importList : groupMap.values())
			{
				Collections.sort(importList, new Comparator<ImportRecord>()
				{
					public int compare(ImportRecord o1, ImportRecord o2)
					{
						return o1.getType().getText().compareTo(o2.getType().getText());
					}
				});
			}
		}
		
		imports.clear();
		//now, copy the results back into the input array, possibly adding separators as specified
		for (String prefix : itemOrder)
		{
			if (prefix.equals(PreferenceConstants.ASRearr_ImportSeparator))
			{
//				if (results.size()>0) && !(results.get(results.size()-1) instanceof ImportSeparator))
				if (imports.size()>0)
				{
					ImportRecord rec=imports.get(imports.size()-1);
					rec.setBlanksAfter(1);
				}
			}
			else
			{
				List<ImportRecord> group=groupMap.get(prefix);
				if (group!=null)
				{
					for (ImportRecord importRecord : group) {
						imports.add(importRecord);
						
						//always clear out the blanks because we are controlling the spacing if we are using the sort table
						if (sort)
							importRecord.setBlanksBefore(0);
						importRecord.setBlanksAfter(0); //it seems like we should always clear after regardless (this may change)
					}
				}
			}
		}

		//don't add carriage return afterward since the next segment probably has its own leading whitespace
//		if (imports.size()>0)
//		{
//			imports.get(imports.size()-1).setBlanksAfter(1);
//		}
	}

	private Map<String, Integer> getVisOrderMap(String visibilityOrder)
	{
		Map<String, Integer> visOrder=new HashMap<String, Integer>();
		String[] lines=visibilityOrder.split(PreferenceConstants.AS_Pref_Line_Separator);
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];
			if (line.length()>0)
			{
				String[] tags=line.split(PreferenceConstants.AS_Pref_Tag_Separator);
				if (tags.length>=2)
				{
					visOrder.put(tags[0], Boolean.valueOf(tags[1]) ? Integer.valueOf(i) : Integer.MAX_VALUE);
				}
			}
			
		}
		return visOrder;
	}
	
	private int getVisibilityPriority(TopLevelItemRecord item, Map<String, Integer> orderMap)
	{
		return getVisibilityPriority(item.getModifierFlags(), orderMap);
	}	
	
	private int getVisibilityPriority(int modFlags, Map<String, Integer> orderMap)
	{
		String vis=null;
		if ((modFlags & TopLevelItemRecord.ASDoc_Public)!=0)
			vis=PreferenceConstants.AS_Mod_Public;
		else if ((modFlags & TopLevelItemRecord.ASDoc_Protected)!=0)
			vis=PreferenceConstants.AS_Mod_Protected;
		else if ((modFlags & TopLevelItemRecord.ASDoc_Private)!=0)
			vis=PreferenceConstants.AS_Mod_Private;
		else if ((modFlags & TopLevelItemRecord.ASDoc_Internal)!=0)
			vis=PreferenceConstants.AS_Mod_Internal;
		
		if (vis==null)
			return Integer.MAX_VALUE;
		
		Integer priority=orderMap.get(vis);
		if (priority==null)
			return Integer.MAX_VALUE;
		return priority.intValue();
	}

	private void sortFunctions(List<FunctionRecord> functions, final boolean sort, String visibilityOrder, final boolean useVisibilityOrder)
	{
		final Map<String, Integer> visOrder=getVisOrderMap(visibilityOrder);
		Collections.sort(functions, new Comparator<FunctionRecord>()
		{
			public int compare(FunctionRecord o1, FunctionRecord o2)
			{
				int p1=0,p2=0;
				if (useVisibilityOrder)
				{
					p1=getVisibilityPriority(o1, visOrder);
					p2=getVisibilityPriority(o2, visOrder);
				}
				
				if (p1==p2 && sort)
				{
					int compare=o1.getName().getText().compareTo(o2.getName().getText());
					if (compare==0)
					{
						compare=orderTwoFunctions(o1, o2);
					}
					return compare;
				}
				return p1-p2;
			}
		});
	}
	
	public static int orderTwoFunctions(FunctionRecord o1, FunctionRecord o2)
	{
		if (/*o1.getParameters().size()==0 && o2.getParameters().size()==1 && */o1.getType()==FunctionRecord.Type_Getter && o2.getType()==FunctionRecord.Type_Setter)
			return -1;
		else if (/*o1.getParameters().size()==1 && o2.getParameters().size()==0 && */o1.getType()==FunctionRecord.Type_Setter && o2.getType()==FunctionRecord.Type_Getter)
			return 1;
		
		int parmSize1=o1.getParameters().size();
		if (/*parmSize1==1 && */o1.getType()==FunctionRecord.Type_Setter)
			parmSize1=0;
		int parmSize2=o2.getParameters().size();
		if (/*parmSize2==1 && */o2.getType()==FunctionRecord.Type_Setter)
			parmSize2=0;
		int compare=parmSize1-parmSize2;
		return compare;
	}

	private void sortProperties(List<PropertySortHolder> properties, final boolean sortByName, String visibilityOrder, final boolean useVisibilityOrder)
	{
		final Map<String, Integer> visOrder=getVisOrderMap(visibilityOrder);
		Collections.sort(properties, new Comparator<PropertySortHolder>()
		{
			public int compare(PropertySortHolder o1, PropertySortHolder o2)
			{
				int p1=0,p2=0;
				if (useVisibilityOrder)
				{
					p1=getVisibilityPriority(o1.getSortVisibility(), visOrder);
					p2=getVisibilityPriority(o2.getSortVisibility(), visOrder);
				}
				
				if (p1==p2 && sortByName)
				{
					if (!o1.getSortName().equals(o2.getSortName()))
						return o1.getSortName().compareTo(o2.getSortName());
					
					if (o1.mItem instanceof FunctionRecord && o2.mItem instanceof PropertyLine)
						return 1;
					if (o2.mItem instanceof FunctionRecord && o1.mItem instanceof PropertyLine)
						return -1;
					if (o1.mItem instanceof FunctionRecord && o2.mItem instanceof FunctionRecord)
					{
						FunctionRecord f1=(FunctionRecord)o1.mItem;
						FunctionRecord f2=(FunctionRecord)o2.mItem;
						if (f1.getType()==FunctionRecord.Type_Getter && f2.getType()==FunctionRecord.Type_Setter)
							return -1;
						else
							return 1;
					}
						
					return 0;
				}
				return p1-p2;
			}
		});
	}

	private boolean reorderClassModifiers(ClassRecord classRecord, IDocument document) throws BadLocationException, BadPositionCategoryException
	{
		boolean modified=reorderModifiers(classRecord, document, getOrdering(PreferenceConstants.ASRearr_Class));

		modified|=reorderFunctionModifiers(classRecord.getFunctions(), document);

		modified|=reorderPropertyModifiers(classRecord.getProperties(), document);
		
		return modified;
	}
	
	private boolean reorderFunctionModifiers(List<FunctionRecord> functions, IDocument document) throws BadLocationException, BadPositionCategoryException
	{
		boolean modified=false;
		List<String> ordering=getOrdering(PreferenceConstants.ASRearr_Function);
		for (FunctionRecord functionRecord : functions) {
			modified|=reorderModifiers(functionRecord, document, ordering);
//			classRecord.validateText(document);
		}
		
		return modified;
	}
	
	private boolean reorderPropertyModifiers(List<PropertyLine> properties, IDocument document) throws BadLocationException, BadPositionCategoryException
	{
		boolean modified=false;
		List<String> ordering=getOrdering(PreferenceConstants.ASRearr_Property);
		for (PropertyLine propRecord : properties) {
			modified|=reorderModifiers(propRecord, document, ordering);
		}
		return modified;
	}

	private List<String> getOrdering(String elementType)
	{
		if (!mPrefs.getBoolean(PreferenceConstants.ASRearr_UseModifierOrder_Root+elementType))
			return null;
		
		String order=mPrefs.getString(PreferenceConstants.ASRearr_ModifierOrder_Root+elementType);
		if (mPrefs.getBoolean(PreferenceConstants.ASRearr_UseSameModifierOrderForAllElements))
		{
			if (!mPrefs.getBoolean(PreferenceConstants.ASRearr_UseModifierOrder_Root+PreferenceConstants.ASRearr_Function))
				return null;
			order=mPrefs.getString(PreferenceConstants.ASRearr_ModifierOrder_Root+PreferenceConstants.ASRearr_Function);
		}
		if (order.length()==0)
			return null;
		
		String[] items=order.split(",");
		List<String> results=new ArrayList<String>();
		for (String item : items) {
			if (item.length()>0)
				results.add(item);
		}
		return results;
	}

	private boolean reorderModifiers(TopLevelItemRecord record, IDocument document, List<String> ordering) throws BadLocationException, BadPositionCategoryException
	{
		if (ordering==null)
			return false;
		
		Set<TextItem> mods=record.getModifiers();
		
		if (mods.size()<=1)
			return false;
		
		List<TextItem> currentOrder=new ArrayList<TextItem>();
		currentOrder.addAll(mods);
		Collections.sort(currentOrder); //sort by position
		
		List<TextItem> desiredOrder=new ArrayList<TextItem>();
		desiredOrder.addAll(currentOrder);
		sortByOrdering(desiredOrder, ordering);
		
		//optimize to do nothing if the order is already correct
		boolean differenceFound=false;
		for (int i=0;i<currentOrder.size();i++)
		{
			TextItem t0=currentOrder.get(i);
			TextItem t1=desiredOrder.get(i);
			if (t0!=t1)
			{
				differenceFound=true;
				break;
			}
		}
		
		if (!differenceFound)
			return false;
		
		//since these items are all on the same line, I'm just going to grab the text, delete the data, and readd it to the document
		int start=currentOrder.get(0).getStartPos();
//		int end=currentOrder.get(currentOrder.size()-1).getEndPos();
		int workingPos=start;
		
		Map<TextItem, String> textToMove=new HashMap<TextItem, String>();

		//find new first item and old first item so that I can move the pretokens to the new item
		for (TextItem textItem : currentOrder) {
			int startPos=textItem.getPreStartPos();
			int endPos=textItem.getEndPos();
			String text=document.get(startPos, endPos-startPos);
			textToMove.put(textItem, text);
			textItem.nailDownPositions();
			document.replace(startPos, endPos-startPos, ""); //delete this segment
		}

		for (int i=0;i<desiredOrder.size();i++)
		{
			TextItem newItem=desiredOrder.get(i);
			//				String insertText=document.get(newItem.getStartPos(), newItem.getEndPos()-newItem.getStartPos()+1);
			String insertText=textToMove.get(newItem);
			if (insertText==null)
			{
				//TODO: show error message
				return true; //just quit, since I'm in a bad state
			}

			{
				if ((workingPos-1>=0) && !AntlrUtilities.isASWhitespace(document.getChar(workingPos-1))&&(!AntlrUtilities.isASWhitespace(insertText.charAt(0))))
				{
					document.replace(workingPos, 0, " ");
					workingPos++;
				}
			}

			document.replace(workingPos, 0, insertText);
			newItem.resetPositions(workingPos-newItem.getNailedDownStartPos(), document);
			workingPos+=insertText.length();
		}

		record.nailDownPositions();
		record.setStartPos(start);
		record.applyDocument(document);
		
		return true;
	}

	private void sortByOrdering(List<TextItem> mods, List<String> ordering)
	{
		final Map<String, Integer> orderMap=new HashMap<String, Integer>();
		for (int i=0;i<ordering.size();i++)
		{
			orderMap.put(ordering.get(i), i);
		}
		
		Collections.sort(mods, new Comparator<TextItem>()
		{
			public int compare(TextItem o1, TextItem o2)
			{
				Integer int1=orderMap.get(o1.getText());
				Integer int2=orderMap.get(o2.getText());
				if (int1==null)
					int1=orderMap.get(PreferenceConstants.NamespaceGeneric);
				if (int2==null)
					int2=orderMap.get(PreferenceConstants.NamespaceGeneric);
				if (int1!=null && int2!=null)
				{
					int i1=int1.intValue();
					int i2=int2.intValue();
					if (i1!=i2)
						return i1-i2;
				}
				
				return o1.getText().compareTo(o2.getText());
			}
		});
		
	}
	
	static class ReorderHolder
	{
		private SourceItem mItem;
		private String mText;
		private String mIndent;
		private boolean mAtFileStart;
		public ReorderHolder(SourceItem item, String text, String indentString, boolean atStartOfFile)
		{
			mItem=item;
			mText=text;
			mIndent=indentString;
			mAtFileStart=atStartOfFile;
		}
		public SourceItem getItem() {
			return mItem;
		}
		public String getText() {
			return mText;
		}
		public String getIndent() {
			return mIndent;
		}
		public boolean isAtFileStart()
		{
			return mAtFileStart;
		}
		
	}

	public String getInternalError() {
		return mInternalError;
	}

	public boolean isSoftFailure() {
		return mIsSoftFailure;
	}
	
	public class HeaderInfo
	{
		private String[] mContentLines;
		private int mStartIndex;
		private int mEndIndex;
		private int mStartPos;
		private int mEndPos;
		
		public HeaderInfo(String[] content, int start, int end, int startPos, int endPos)
		{
			mContentLines=content;
			mStartIndex=start;
			mEndIndex=end;
			mStartPos=startPos;
			mEndPos=endPos;
		}
		public String[] getContentLines() {
			return mContentLines;
		}
		public int getStartIndex() {
			return mStartIndex;
		}
		public int getEndIndex() {
			return mEndIndex;
		}
		public int getStartPos() {
			return mStartPos;
		}
		public int getEndPos() {
			return mEndPos;
		}
	}

	public String getAddedText() {
		return mAddedText.toString();
	}

	public String getRemovedText() {
		return mRemovedText.toString();
	}
	
	public static class PropertySortHolder
	{
		private int mOverrideVisibility=(-1);
		private String mOverrideSortName=null;
		private String mOverrideSortType=null;
		private int mOverridePosition=(-1); //to allow associating of getters/setters with properties
		private SourceItem mItem;
		public PropertySortHolder(SourceItem item)
		{
			mItem=item;
		}
		public int getSortVisibility()
		{
			if (mOverrideVisibility>=0)
				return mOverrideVisibility;
			return getTopLevelItem().getModifierFlags();
		}
		
		public TopLevelItemRecord getTopLevelItem()
		{
			if (mItem instanceof TopLevelItemRecord)
				return (TopLevelItemRecord)mItem;
			return null;
		}
		
		public SourceItem getRawItem()
		{
			return mItem;
		}
		
		public String getSortName()
		{
			if (mOverrideSortName!=null)
				return mOverrideSortName;
			if (mItem instanceof FunctionRecord)
				return ((FunctionRecord)mItem).getName().getText();
			if (mItem instanceof PropertyLine)
				return ((PropertyLine)mItem).getProperties().get(0).getName().getText();
			return "";
		}
		public int getOverrideVisibility() {
			return mOverrideVisibility;
		}
		public void setOverrideVisibility(int overrideVisibility) {
			mOverrideVisibility = overrideVisibility;
		}
		public String getOverrideSortName() {
			return mOverrideSortName;
		}
		public void setOverrideSortName(String overrideSortName) {
			mOverrideSortName = overrideSortName;
		}
		
		public String getOverrideSortType() {
			return mOverrideSortType;
		}
		public void setOverrideSortType(String overrideSortType) {
			mOverrideSortType = overrideSortType;
		}
		public String getSortType()
		{
			if (mOverrideSortType!=null)
				return mOverrideSortType;
			if (mItem instanceof FunctionRecord || mItem instanceof PropertyLine)
				return MemberSelectionSpec.getTypeString((TopLevelItemRecord)mItem);
			return "";
		}
		public int getFilePosition()
		{
			if (mOverridePosition>=0)
				return mOverridePosition;
			else
				return mItem.getStartPos();
		}
		public int getOverridePosition() {
			return mOverridePosition;
		}
		public void setOverridePosition(int overridePosition) {
			mOverridePosition = overridePosition;
		}
	}

	public boolean hasChanges() {
		return mMadeChanges;
	}
	
//	public static class FunctionSortHolder implements ISortholder
//	{
//		public FunctionRecord mItem;
//		public PropertyLine mAssociatedProperty;
//		public int mOverrideVisibility=(-1);
//		public FunctionSortHolder(FunctionRecord item)
//		{
//			mItem=item;
//		}
//		public String getSortName() {
//			return mItem.getName().getText();
//		}
//		public int getSortVisibility() {
//			if (mOverrideVisibility>=0)
//				return mOverrideVisibility;
//			return mItem.getModifierFlags();
//		}
//		public PropertyLine getAssociatedProperty() {
//			return mAssociatedProperty;
//		}
//		public void setAssociatedProperty(PropertyLine associatedProperty) {
//			mAssociatedProperty = associatedProperty;
//		}
//		
//	}
//
//	public static class PropertySortHolder implements ISortholder
//	{
//		public PropertyLine mItem;
//		public List<FunctionRecord> mAssociatedGetters;
//		public PropertySortHolder(PropertyLine item)
//		{
//			mItem=item;
//			mAssociatedGetters=new ArrayList<FunctionRecord>();
//		}
//		public void addImplicitProperty(FunctionRecord func)
//		{
//			mAssociatedGetters.add(func);
//		}
//		public List<FunctionRecord> getImplicitProperties()
//		{
//			return mAssociatedGetters;
//		}
//		public String getSortName() {
//			return mItem.getName().getText();
//		}
//		public int getSortVisibility() {
//			return mItem.getModifierFlags();
//		}
//	}

}
