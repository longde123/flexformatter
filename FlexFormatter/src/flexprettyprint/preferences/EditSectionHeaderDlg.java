package flexprettyprint.preferences;

import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import flexasrearrangecodecommand.handlers.ASRearranger;

public class EditSectionHeaderDlg extends TitleAreaDialog {
	private SectionSpec mHeader;
	private Button mUseSection;
	private Button mMajor;
	private Button mMinor;
	private Text mSectionText;
	private Text mSample;
	private Map<Integer, SectionHeader> mTemplates;
	
	private String mCurrentElement;
	private List<ISectionItem> mElementOrder;
	private Combo mSpanCombo;
	
	private boolean mIsSpanning;
	
	public EditSectionHeaderDlg(Shell parent, SectionSpec header, Map<Integer, SectionHeader> templates, String current, List<ISectionItem> elementIDOrder)
	{
		super(parent);
		mHeader=header;
		mTemplates=templates;
		mElementOrder=elementIDOrder;
		mCurrentElement=current;
		mIsSpanning=mCurrentElement.endsWith(ASRearranger.SpanningSuffix);
		setShellStyle(getShellStyle()|SWT.RESIZE);
	}
	
	@Override
	protected Control createDialogArea(Composite parent)
	{
		if (mIsSpanning)
		{
			getShell().setText("Spanning section header");
			setMessage("A spanning section header is one that applies when *any* of the sections in the span contains an item.\nThere can be both a regular header and a spanning header associated with the same element type.", IMessageProvider.INFORMATION);
		}
		else
		{
			getShell().setText("Section header");
			setMessage("A section header is one that applies when there is at least one item of the element type.\nThere can be both a regular header and a spanning header associated with the same element type.", IMessageProvider.INFORMATION);
		}
		Composite comp=new Composite(parent, SWT.None);
		comp.setLayout(new GridLayout());
		comp.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		if (!mCurrentElement.equals(mHeader.getID()))
		{
			Label l=new Label(comp, SWT.None);
			String parentItem="";
			for (ISectionItem item : mElementOrder) {
				if (mHeader.getID().startsWith(item.getReferenceID()))
				{
					parentItem=item.getPrintString();
					break;
				}
			}
			l.setText("This header is configured in an earlier code element ("+parentItem+").  Editing can only occur there.");
		}
		
		mUseSection=new Button(comp, SWT.CHECK);
		mUseSection.setText("Use a header for this code section");
		mUseSection.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				enableWidgets();
			}
		});
		
		Composite templateComp=new Composite(comp, SWT.None);
		templateComp.setLayout(new GridLayout(2, true));
		
		mMajor=new Button(templateComp, SWT.RADIO);
		mMajor.setText("Use major header");
		mMajor.setToolTipText("The Major header attributes are configured on the 'Sections' tab");
		mMajor.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateSample();
			}
		});
		
		mMinor=new Button(templateComp, SWT.RADIO);
		mMinor.setText("Use minor header");
		mMinor.setToolTipText("The Minor header attributes are configured on the 'Sections' tab");
		mMinor.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateSample();
			}
		});
		
		Label l=new Label(comp, SWT.None);
		l.setText("Enter the content of the section tag in the text area");
		mSectionText=new Text(comp, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		GridData gd=new GridData(GridData.FILL_BOTH);
		gd.heightHint=mSectionText.getLineHeight()*3; //make the text area a couple of lines high to make it clear that multiple lines can be added.
		mSectionText.setLayoutData(gd);
		
		mUseSection.setSelection(mHeader.isUseHeader());
		mMajor.setSelection(mHeader.getSectionType()==SectionSpec.MAJOR);
		mMinor.setSelection(mHeader.getSectionType()==SectionSpec.MINOR);
		StringBuffer buffer=new StringBuffer();
		String[] lines=mHeader.getText();
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];
			buffer.append(line);
			if (i+1<lines.length)
				buffer.append('\n');
		}
		mSectionText.setText(buffer.toString());
		mSectionText.addModifyListener(new ModifyListener()
		{
			public void modifyText(ModifyEvent e) {
				updateSample();
			}
		});
		
		
		mSample=new Text(comp, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.READ_ONLY);
		gd=new GridData(GridData.FILL_HORIZONTAL);
		FontData[] fonts=Display.getDefault().getFontList("Courier", false);
		if (fonts.length>0)
		{
			Font font=new Font(Display.getCurrent(), fonts[0]);
			mSample.setFont(font);
			gd.heightHint=mSample.getLineHeight()*7;
		}
		mSample.setLayoutData(gd);
		
		Composite c=new Composite(comp, SWT.None);
		c.setLayout(new GridLayout(2, false));
		c.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		l=new Label(c, SWT.None);
		l.setText("This spanning section includes all code elements down to: ");
		mSpanCombo=new Combo(c, SWT.READ_ONLY | SWT.BORDER);
		mSpanCombo.setToolTipText("By default, a header is only defined for 1 element type.  However, if you want to define a 'Members'\nsection and have it apply to all properties and functions, then you would adjust the combo selection to point to the last\n element type you want to include.");
		boolean seenCurrent=false;
		
		String spanID=mCurrentElement;
		if (mIsSpanning)
			spanID=spanID.substring(0, spanID.length()-ASRearranger.SpanningSuffix.length());
		for (ISectionItem element : mElementOrder) {
			//we only want to add the ones after tbe current element.
			String persistString=element.getReferenceID();
			if (seenCurrent)
			{
				String printString=element.toString();
				mSpanCombo.add(printString);
				mSpanCombo.setData(printString, persistString);
				mSpanCombo.setData(persistString, printString);
			}
			if (!seenCurrent)
				seenCurrent=(persistString.equals(spanID));
		}
		l.setVisible(mIsSpanning);
		mSpanCombo.setVisible(mIsSpanning);
		
		if (mSpanCombo.getItemCount()>0)
		{
			int index=0;
			if (mHeader.getEndSpanSectionID()!=null)
			{
				String printString=(String)mSpanCombo.getData(mHeader.getEndSpanSectionID());
				if (printString!=null)
					index=mSpanCombo.indexOf(printString);
			}
			if (index>=0)
				mSpanCombo.select(index);
			else
				mSpanCombo.select(0);
		}
		
		enableWidgets();
		updateSample();
		return comp;
	}
	
	private void enableWidgets()
	{
		boolean enableAll=mCurrentElement.equals(mHeader.getID());
		mUseSection.setEnabled(enableAll);
		mMajor.setEnabled(mUseSection.getSelection() && enableAll);
		mMinor.setEnabled(mUseSection.getSelection() && enableAll);
		mSectionText.setEnabled(mUseSection.getSelection() && enableAll);
		mSample.setEnabled(mUseSection.getSelection() && enableAll);
		mSpanCombo.setEnabled(mUseSection.getSelection() && enableAll && (mSpanCombo.getItemCount()>0));
	}
	
	private void updateSample()
	{
		mSample.setText(generatHeaderSpec().generateHeader(mTemplates, mSample.getLineDelimiter(), ""));
//		SectionHeader oldHeader=mTemplates.get(mMajor.getSelection() ? SectionSpec.MAJOR : SectionSpec.MINOR);
//		if (oldHeader==null)
//			return;
//		
//		String[] lines=mSectionText.getText().split(mSectionText.getLineDelimiter());
//		SectionHeader header=new SectionHeader(oldHeader.getStyle(), oldHeader.getWidth(), oldHeader.getExtraInternalLines(), lines);
//		String[] output=header.getCommentLines();
//		StringBuffer buffer=new StringBuffer();
//		for (int i = 0; i < output.length; i++) {
//			String line = output[i];
//			buffer.append(line);
//			if (i+1<output.length)
//				buffer.append(mSample.getLineDelimiter());
//		}
//		mSample.setText(buffer.toString());
	}
	
	@Override
	protected void okPressed() {
//		String text=mSectionText.getText();
//		String[] lines=text.split(mSectionText.getLineDelimiter());
//		mHeader=new SectionSpec(mHeader.getID(), mMajor.getSelection() ? SectionSpec.MAJOR : SectionSpec.MINOR, lines, mUseSection.getSelection());
		mHeader=generatHeaderSpec();
		super.okPressed();
	}
	
	protected SectionSpec generatHeaderSpec()
	{
		String text=mSectionText.getText();
		String[] lines=text.split(mSectionText.getLineDelimiter());
		SectionSpec spec=new SectionSpec(mHeader.getID(), mMajor.getSelection() ? SectionSpec.MAJOR : SectionSpec.MINOR, lines, mUseSection.getSelection());
		if (mIsSpanning)
			spec.setEndSpanSectionID((String)mSpanCombo.getData(mSpanCombo.getText()));
		else
			spec.setEndSpanSectionID(spec.getID());
		return spec;
	}

	public SectionSpec getHeaderSpec()
	{
		return mHeader;
	}
}
