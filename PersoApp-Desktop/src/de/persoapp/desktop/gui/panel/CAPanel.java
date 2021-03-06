/**
 * 
 * COPYRIGHT (C) 2010, 2011, 2012, 2013, 2014 AGETO Innovation GmbH
 * 
 * Authors Christian Kahlo, Ralf Wondratschek
 * 
 * All Rights Reserved.
 * 
 * Contact: PersoApp, http://www.persoapp.de
 * 
 * @version 1.0, 30.07.2013 13:50:47
 * 
 *          This file is part of PersoApp.
 * 
 *          PersoApp is free software: you can redistribute it and/or modify it
 *          under the terms of the GNU Lesser General Public License as
 *          published by the Free Software Foundation, either version 3 of the
 *          License, or (at your option) any later version.
 * 
 *          PersoApp is distributed in the hope that it will be useful, but
 *          WITHOUT ANY WARRANTY; without even the implied warranty of
 *          MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *          Lesser General Public License for more details.
 * 
 *          You should have received a copy of the GNU Lesser General Public
 *          License along with PersoApp. If not, see
 *          <http://www.gnu.org/licenses/>.
 * 
 *          Diese Datei ist Teil von PersoApp.
 * 
 *          PersoApp ist Freie Software: Sie können es unter den Bedingungen der
 *          GNU Lesser General Public License, wie von der Free Software
 *          Foundation, Version 3 der Lizenz oder (nach Ihrer Option) jeder
 *          späteren veröffentlichten Version, weiterverbreiten und/oder
 *          modifizieren.
 * 
 *          PersoApp wird in der Hoffnung, dass es nützlich sein wird, aber OHNE
 *          JEDE GEWÄHRLEISTUNG, bereitgestellt; sogar ohne die implizite
 *          Gewährleistung der MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN
 *          ZWECK. Siehe die GNU Lesser General Public License für weitere
 *          Details.
 * 
 *          Sie sollten eine Kopie der GNU Lesser General Public License
 *          zusammen mit diesem Programm erhalten haben. Wenn nicht, siehe
 *          <http://www.gnu.org/licenses/>.
 * 
 */
package de.persoapp.desktop.gui.panel;

import java.awt.BorderLayout;
import java.awt.event.MouseListener;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;

import de.persoapp.core.client.IEAC_Info;
import de.persoapp.core.client.PropertyResolver;
import de.persoapp.desktop.gui.MyTitledBorder;

/**
 * <p>
 * The CAPanel displays all informations about the currently used
 * certificates </br>(<em>ISO-7816</em> and <em>TR-03110</em>).
 * </p>
 * 
 * @author Christian Kahlo
 * @author Rico Klimsa - added javadoc comments.
 */
public class CAPanel extends JPanel {

	private static final long				serialVersionUID	= -1598003679367657336L;

	public static final String				BOLD				= "Bold";
	public static final String				NORMAL				= "Normal";

	/**
	 * The necessary {@link JTextPane} to display the Certificate
	 * Authority Informations.
	 */
	private JTextPane						textPane;

	/**
	  *Localized message bundle for user interaction.	 
	  */
	private final PropertyResolver.Bundle	textBundle;

	/**
	 * <p>
	 * Constructs and initializes a new instance of the CAPanel. Also
	 * initializes the defined attributes and draws the content.
	 * </p>
	 * <p>
	 * The constructed panel is double-buffered to achieve benefits by
	 * the extended use of memory.
	 * </p>
	 * <p>
	 * The {@link BorderLayout} is used for drawing.
	 * </p>
	 */
	public CAPanel() {
		super();

		textBundle = PropertyResolver.getBundle("text");
		this.setDoubleBuffered(true);
		this.setLayout(new BorderLayout());
		this.setBorder(new MyTitledBorder(textBundle.get("CAPanel_title")));

		drawContent();
	}

	/**
	 * Draws the content of the {@link CAPanel}.
	 */
	private void drawContent() {
		textPane = new JTextPane();
		textPane.setEditable(false);

		Style style = textPane.addStyle(BOLD, null);
		StyleConstants.setBold(style, true);
		style = textPane.addStyle(NORMAL, null);

		this.add(new JScrollPane(textPane, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED), BorderLayout.CENTER);
	}
	
	/**
	 * Adds the given text which is created according to the given style to the
	 * defined {@link #textPane}.
	 * 
	 * @param text
	 *            - The text, to add.
	 * @param style
	 *            - The style of the text.
	 */
	private void addText(final String text, final String style) {
		final Document doc = textPane.getDocument();
		try {
			doc.insertString(doc.getLength(), text, textPane.getStyle(style));
		} catch (final BadLocationException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Removes the text of the {@link CAPanel}.
	 */
	public void clear() {
		textPane.setText("");
	}

	/**
	 * Removes the content of the included textPane and inserts the following
	 * specific certificate informations from the given {@link IEAC_Info}.
	 * <p>
	 * <ul>
	 * <li>Issuer Name - The name of the certificate issuer.</li>
	 * <li>Issuer URL - The url of the certificate issuer.</li>
	 * </ul>
	 * </p>
	 * @param eacInfo
	 *            - The {@link IEAC_Info}, to fill.
	 */
	public void fillCertificate(final IEAC_Info eacInfo) {
		clear();
		addText(eacInfo.getIssuerName() + "\n", BOLD);
		addText(eacInfo.getIssuerURL(), NORMAL);
	}

	@Override
	public void addMouseListener(final MouseListener listener) {
		super.addMouseListener(listener);
		textPane.addMouseListener(listener);
	}
}
