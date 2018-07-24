package net.theinterweb.Chatterbox.Utils;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.Utilities;

public class CEditorPane extends JEditorPane{
	private static final long serialVersionUID = 1L;

    static ImageIcon SMILE_IMG=createImage("smile");
    static ImageIcon FROWN_IMG=createImage("frown");
	
	public void append(String s){
		this.setText(this.getText() + s);
	}
	
	public void initListener() {
        getDocument().addDocumentListener(new DocumentListener(){
            public void insertUpdate(DocumentEvent event) {
                final DocumentEvent e=event;
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        if (e.getDocument() instanceof StyledDocument) {
                            try {
                                StyledDocument doc=(StyledDocument)e.getDocument();
                                int start= Utilities.getRowStart(CEditorPane.this,Math.max(0,e.getOffset()-1));
                                int end=Utilities.getWordStart(CEditorPane.this,e.getOffset()+e.getLength());
                                String text=doc.getText(start, end-start);

                                int i=text.indexOf(":)");
                                while(i>=0) {
                                    final SimpleAttributeSet attrs=new SimpleAttributeSet(
                                       doc.getCharacterElement(start+i).getAttributes());
                                    if (StyleConstants.getIcon(attrs)==null) {
                                        StyleConstants.setIcon(attrs, SMILE_IMG);
                                        doc.remove(start+i, 2);
                                        doc.insertString(start+i,":)", attrs);
                                    }
                                    i=text.indexOf(":)", i+2);
                                }
                                
                                i=text.indexOf(":(");
                                while(i>=0) {
                                    final SimpleAttributeSet attrs=new SimpleAttributeSet(
                                       doc.getCharacterElement(start+i).getAttributes());
                                    if (StyleConstants.getIcon(attrs)==null) {
                                        StyleConstants.setIcon(attrs, FROWN_IMG);
                                        doc.remove(start+i, 2);
                                        doc.insertString(start+i,":(", attrs);
                                    }
                                    i=text.indexOf(":(", i+2);
                                }
                            } catch (BadLocationException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                });
            }
            public void removeUpdate(DocumentEvent e) {
            }
            public void changedUpdate(DocumentEvent e) {
            }
        });
    }

    static ImageIcon createImage(String t) {
    	if (t.equals("smile")) {
	        BufferedImage res=new BufferedImage(17, 17, BufferedImage.TYPE_INT_ARGB);
	        Graphics g=res.getGraphics();
	        ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	        g.setColor(Color.yellow);
	        g.fillOval(0,0,16,16);
	
	        g.setColor(Color.black);
	        g.drawOval(0,0,16,16);
	
	        g.drawLine(4,5, 6,5);
	        g.drawLine(4,6, 6,6);
	
	        g.drawLine(11,5, 9,5);
	        g.drawLine(11,6, 9,6);
	
	        g.drawLine(4,10, 8,12);
	        g.drawLine(8,12, 12,10);
	        g.dispose();
	
	        return new ImageIcon(res);
    	} else if (t.equals("frown")) {
    		BufferedImage res=new BufferedImage(17, 17, BufferedImage.TYPE_INT_ARGB);
 	        Graphics g=res.getGraphics();
 	        ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
 	        // Create the background for the face
 	        g.setColor(Color.yellow);
 	        g.fillOval(0,0,16,16);
 	        // Create the outline for the face
 	        g.setColor(Color.black);
 	        g.drawOval(0,0,16,16);
 	        // Create the left eye
 	        g.drawLine(6,5, 4,5);
 	        g.drawLine(6,6, 4,6);
 	        // Create the left eye
 	        g.drawLine(11,5, 9,5);
 	        g.drawLine(11,6, 9,6);
 	        // Create the mouth
 	        g.drawLine(4,12, 8,10);
 	        g.drawLine(8,10, 12,12);
 	        g.dispose();
 	
 	        return new ImageIcon(res);
    	}
    	return null;
    }
}
