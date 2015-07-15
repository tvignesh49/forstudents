package rcword;

import java.util.Vector;
import javax.swing.text.AttributeSet;
import javax.swing.text.Document;
import javax.swing.*;
import javax.swing.text.ElementIterator;
import javax.swing.text.Element;
import javax.swing.text.BadLocationException;

/**
 * A class to represent a portion of styled text. Each chunk of text is stored
 * with its own set of character attributes.
 */
public class StyledText
{
	/** Holds the copied text chunks */
	private Vector clipText = new Vector(0);

	/** Holds the copied character attributes mapping to clipText */
	private Vector clipAttr = new Vector(0);

	/** Construct a new empty <code>StyledText</code> object */
	public StyledText()
	{
	}

	/**
	 * Construct a new <code>StyledText</code> object and load it with
	 * an already selected portion of text from a
	 * <code>AText</code>
	 *
	 * @param src AText that has selected the text portion to be taken
	 *
	 * @see AText
	 */

	public StyledText(JTextPane src) throws BadLocationException
	{
		Document doc = src.getDocument();
		int selStart = src.getSelectionStart();
		int selEnd = src.getSelectionEnd();
		int eStart;
		int eEnd;
		String eText;
		ElementIterator eli = new ElementIterator(doc);
		Element elem = eli.first();
		while(elem != null)
		{
			eStart = elem.getStartOffset();
			eEnd = elem.getEndOffset();
			if(elem.getName().equalsIgnoreCase("content"))
			{
				if((eEnd >= selStart) && (eStart <= selEnd))
				{
					clipAttr.addElement(elem.getAttributes());
					if(eStart < selStart)
					{
						if(eEnd > selEnd)
						{
							//both ends of element outside selection
							clipText.addElement(src.getText(selStart, selEnd - selStart));
						}
						else
						{
							//only first part of element outside selection
							clipText.addElement(src.getText(selStart, eEnd - selStart));
						}
					}
					else if(eEnd > selEnd)
					{
						//only last part of element outside selection
						clipText.addElement(src.getText(eStart, selEnd - eStart));
					}
					else
					{
						//whole element inside selection
						clipText.addElement(src.getText(eStart, eEnd - eStart));
					}
				}
			}
			elem = eli.next();
		}
	}

	/**
	 * Insert this <code>StyledText</code> into a <code>Document</code>
	 *
	 * @param doc the document to insert this StyledText into
	 * @param insertPos the position within the document to insert at
	 * @see javax.swing.text.Document
	 */
	public void insert(Document doc, int insertPos) throws BadLocationException
	{
		int i;
		int contentSize = size();
		String text;
		for(i=0;i<contentSize;i++)
		{
			text = getCharactersAt(i);
			doc.insertString(insertPos, text, getCharacterAttributes(i));
			insertPos += text.length();
		}
	}

	/** Get the number of text chunks in this <code>StyledText</code> object */
	public int size()
	{
		return clipText.size();
	}

	/**
	 * Get the attributes of a certain chunk of styled text
	 *
	 * @param chunkPos - the number of the chunk to get the attributes for
	 * @return the attributes for respective character position
	 */
	public AttributeSet getCharacterAttributes(int chunkNo)
	{
		return (AttributeSet) clipAttr.elementAt(chunkNo);
	}

	/**
	 * Get the characters of a certain chunk of styled text
	 *
	 * @param chunkNo - the number of the chunk to get the characters for
	 * @return the characters for respective chunk as String
	 */
	public String getCharactersAt(int chunkNo)
	{
		return (String) clipText.elementAt(chunkNo);
	}

	/**
	 * Get a String containing all chunks of text contained in this object
	 *
	 * @return string of all chunks in this object
	 */
	public String toString()
	{
		String text = "";
		int i;
		for(i=0;i<clipText.size();i++)
		{
			text += (String) clipText.elementAt(i);
		}
		return text;
	}

	/**
	 * Add a chunk of styled text
	 *
	 * @param  text  - the text to add
	 * @param  ca    - the character attributes for 'text'
	 */
	public void add(String text, AttributeSet ca)
	{
		clipText.addElement(text);
		clipAttr.addElement(ca);
	}

	/** Clear all contents of this <code>StyledText</code> object */
	public void clear()
	{
		clipText.clear();
		clipAttr.clear();
	}
}
