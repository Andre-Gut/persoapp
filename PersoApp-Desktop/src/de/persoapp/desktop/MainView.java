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
package de.persoapp.desktop;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.SplashScreen;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import de.persoapp.core.client.IEAC_Info;
import de.persoapp.core.client.IMainView;
import de.persoapp.core.client.PropertyResolver;
import de.persoapp.core.client.SecureHolder;
import de.persoapp.desktop.gui.frame.CANDialog;
import de.persoapp.desktop.gui.frame.MainFrame;
import de.persoapp.desktop.gui.frame.NewChangePinFrame;

/**
 * <p>
 * The MainView defines the current running instance of the GUI and is
 * a singleton to prevent the application from launched multiple times at once.
 * Also it acts as an endpoint of the communication channel between the core and
 * the gui, receiving events and data of user interaction and program
 * processing.
 * </p>
 * 
 * @author Ralf Wondratschek
 * @author Rico Klimsa - added javadoc comments.
 */

public class MainView implements IMainView {

	/**
	 * The currently used application logger.
	 */
	private final static Logger	LOGGER	= Logging.getLogger();

	/**
	 * The application instance.
	 */
	private static IMainView	instance;

	/**
	 * Shows the status of the PersoApp-Application and the result of
	 * the current operation.
	 */
	protected StatusIndicator	statusIndicator;
	
	/**
	 * The event listener.
	 */
	private EventListener		listener;
	
	/**
	 * The current displayed frame.
	 */
	protected volatile JFrame	currentFrame;

	/**
	 * The main frame, which shows all components.
	 */
	private MainFrame			mainFrame;
	
	/**
	 * The main dialog result, of inserting different data.
	 */
	private MainDialogResult	result;

	/**
	 * The frame to insert a pin in it.
	 */
	private NewChangePinFrame	changePinFrame;

	static {
		// install the configured Look-and-feel on the system
		installLAF();
	}

	/**
	 * Installs the configured Look-and-feel in the system.
	 */
	private static void installLAF() {
		try {
			UIManager.setLookAndFeel(Configuration.LOOK_AND_FEEL.getName());
		} catch (final Exception e) {
			LOGGER.log(Level.WARNING, "", e);
		}
	}

	/**
	 * Constructs a new instance of {@link MainView} and initializes it.
	 */
	protected MainView() {
		init();
	}

	/**
	 * Initializes the {@link SplashScreen} in an extra {@link Thread} and
	 * retrieves necessary properties to display informations about the
	 * PersoApp-DesktopClient on the SplashScreen.
	 * <p>
	 * The SplashScreen isn't locked to the loading process of the
	 * underlying application. He just waits 3000 milliseconds before
	 * he vanishes.
	 * </p>
	 */
	protected void init() {
		final PropertyResolver.Bundle text_mainBundle = PropertyResolver.getBundle("text");
		final PropertyResolver.Bundle text_coreBundle = PropertyResolver.getBundle("text_core");

		statusIndicator = new StatusIndicator();
		statusIndicator.setDefaultTitle(text_mainBundle.get("window_title"));

		final SplashScreen ss = SplashScreen.getSplashScreen();
		if (ss != null && ss.isVisible()) {
			final URL splashURL = Utils.getResourceUrl("splash_white_324x204.png");
			if (splashURL != null) {
				try {
					ss.setImageURL(splashURL);
					final Graphics2D splashGraphics = ss.createGraphics();

					new Thread(new Runnable() {
						@Override
						public void run() {
							splashGraphics.setBackground(Color.WHITE);
							splashGraphics.setColor(Color.BLACK);
							splashGraphics.setFont(new Font("Arial", Font.TRUETYPE_FONT | Font.PLAIN, 14));

							splashGraphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
									RenderingHints.VALUE_INTERPOLATION_BILINEAR);
							splashGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
									RenderingHints.VALUE_ANTIALIAS_ON);
							splashGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
									RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
							splashGraphics.setRenderingHint(RenderingHints.KEY_RENDERING,
									RenderingHints.VALUE_RENDER_QUALITY);

							final int width = (int) ss.getSize().getWidth(), height = (int) ss.getSize().getHeight();

							final BufferedImage img = Configuration.LOGO;
							if (img != null) {
								splashGraphics.drawImage(img, (width - img.getWidth()) / 2,
										(height - img.getHeight()) / 2, null);
							}

							String msg = text_mainBundle.get("window_title");
							splashGraphics.drawString(msg, 4, 16);

							final String buildVersion = PropertyResolver.getProperty("version", "buildVersion");
							final String buildRevision = PropertyResolver.getProperty("version", "buildRevision");
							final String buildNo = PropertyResolver.getProperty("version", "buildNo");
							final String buildDate = PropertyResolver.getProperty("version", "buildDate");

							msg = buildVersion + "." + buildRevision + "." + buildNo + " " + buildDate;
							splashGraphics.drawString(msg, 4, 32);

							msg = "persoapp@trust.cased.de";
							splashGraphics.drawString(msg, 4, 195);

							final FontMetrics metrics = splashGraphics.getFontMetrics();
							msg = "(c) 2013 www.persoapp.de";
							splashGraphics.drawString(msg, width - metrics.stringWidth(msg) - 4, 195);

							msg = text_coreBundle.get("Main_starting");
							splashGraphics.drawString(msg, (width - metrics.stringWidth(msg)) / 2, 160);

							ss.update();

							try {
								Thread.sleep(3000);
							} catch (final InterruptedException e) {
							}

							try {
								ss.close();
							} catch (final Exception e) {
								// already closed
							}
						}
					}).start();
				} catch (final Exception e) {
					e.printStackTrace();
				}
			} else {
				ss.close();
			}
		}
	}

	/**
	 * Returns the instance of the {@link MainView}.
	 * 
	 * @return Returns the instance of the {@link MainView}.
	 */
	public static IMainView getInstance() {
		if (instance == null) {
			try {
				instance = Configuration.MAINVIEW_CLASS.newInstance();
			} catch (final InstantiationException e) {
				e.printStackTrace();
			} catch (final IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return instance;
	}

	/**
	 * Returns the {@link MainFrame} of the {@link MainView}.
	 * 
	 * @return Returns the currently used {@link MainFrame} or a newly created
	 *         one. The returned frame is going to be displayed.
	 */
	public MainFrame getMainFrame() {
		if (this.mainFrame == null) {
			this.mainFrame = new MainFrame();
			this.result = null;
		}
		
		currentFrame = this.mainFrame;
		return this.mainFrame;
	}

	/**
	 * Returns the {@link NewChangePinFrame} of the {@link MainView}.
	 * 
	 * @return Returns the currently used {@link NewChangePinFrame} or a newly
	 *         created one. The returned frame is going to be displayed.
	 */
	public NewChangePinFrame getChangePinFrame() {
		if (this.changePinFrame == null) {
			this.changePinFrame = new NewChangePinFrame();
		}

		currentFrame = this.changePinFrame;
		return this.changePinFrame;
	}

	@Override
	public void showMainDialog(final IEAC_Info eacInfo, final int MODE) {
		this.result = null;

		final MainFrame mf = getMainFrame();
		mf.init(eacInfo);

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				mf.setAlwaysOnTop(true);
				mf.setVisible(true);
				mf.toFront();
				mf.setPinEnabled(!(MODE == IMainView.MODE_NONE || MODE == IMainView.MODE_CHATONLY));
			}
		});
	}

	@Override
	public MainDialogResult getMainDialogResult() {
		while (this.result == null) {
			try {
				synchronized (this) {
					this.wait();
				}
			} catch (final InterruptedException e) {
			}
		}

		return this.result;
	}

	/**
	 * Sets the {@link MainDialogResult}. After the result is set, the Thread of
	 * the current {@link MainView} is notified.
	 * 
	 * @param chat
	 *            - The marked fields of the personal data which is going to be
	 *            read out and send to the service provider.
	 * @param pin
	 *            - The inserted pin.
	 * @param approved
	 *            - <strong>true</strong> if the inserted data is correct,
	 *            otherwise <strong>false</strong>.
	 */
	public void setResult(final long chat, final byte[] pin, final boolean approved) {
		this.result = new MainDialogResult(chat, pin, approved);
		synchronized (this) {
			this.notify();
		}
	}

	@Override
	public void showProgress(final String message, final int amount, final boolean enabled) {
		this.getMainFrame().showProgress(message, amount, enabled);
	}
	

	@Override
	public boolean showQuestion(final String title, final String message) {
		return JOptionPane.OK_OPTION == JOptionPane.showConfirmDialog(currentFrame, message, title,
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
	}

	@Override
	public void showError(final String title, final String message) {
		JOptionPane.showMessageDialog(currentFrame, message, title, JOptionPane.ERROR_MESSAGE);
	}


	@Override
	public void closeDialogs() {
		if (mainFrame != null) {
			mainFrame.setVisible(false);
		}

		if (changePinFrame != null) {
			changePinFrame.setVisible(false);
		}
	}

	@Override
	public void shutdown() {
		closeDialogs();

		if (statusIndicator != null) {
			statusIndicator.close();
		}
	}

	@Override
	public void showChangePinDialog() {
		if (mainFrame != null) {
			mainFrame.setVisible(false);
		}

		getChangePinFrame().setPanelState(NewChangePinFrame.STATE_CHOOSE);
		getChangePinFrame().setVisible(true);
	}

	@Override
	public void showMessage(final String info, final int type) {
		statusIndicator.displayMessage(null, info, type);
	}

	@Override
	public Object triggerEvent(final int event, final Object... eventData) {
		synchronized (listener) {
			return listener.handleEvent(event, eventData);
		}
	}

	@Override
	public void setEventLister(final EventListener listener) {
		this.listener = listener;
	}

	@Override
	public SecureHolder showCANDialog(final String msg) {
		return CANDialog.show(currentFrame, PropertyResolver.getBundle("text").get("MainView_can_title"), msg);
	}
}
