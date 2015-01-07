package gov.redhawk.ide.swtbot;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.results.BoolResult;
import org.eclipse.swtbot.swt.finder.results.VoidResult;
import org.eclipse.swtbot.swt.finder.utils.MessageFormat;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;

/**
 * Correctly implements {@link SWTBotMenu#click()}. See https://bugs.eclipse.org/bugs/show_bug.cgi?id=339217.
 */
public class SWTBotRadioMenu extends SWTBotMenu {

	/**
	 * Creates a new SWTBotRadioMenu for a {@link SWTBotMenu} handling a menu of style {@link SWT#RADIO}.
	 *
	 * @param menu The SWTBot for the radio menu item
	 */
	public SWTBotRadioMenu(SWTBotMenu menu) {
		this(menu.widget);
	}

	/**
	 * Creates a new SWTBotRadioMenu for a {@link MenuItem} of style {@link SWT#RADIO}.
	 *
	 * @param w The radio menu item
	 * @throws WidgetNotFoundException
	 * @throws IllegalArgumentException If the menu isn't radio style
	 */
	public SWTBotRadioMenu(MenuItem w) throws WidgetNotFoundException {
		super(w);
		boolean isRadioStyle = syncExec(new BoolResult() {
			@Override
			public Boolean run() {
				return (widget.getStyle() & SWT.RADIO) != 0;
			}
		});
		if (!isRadioStyle) {
			throw new IllegalArgumentException("Not a radio menu item widget");
		}
	}

	/**
	 * Clicks on the radio menu item
	 *
	 * @see org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu#click()
	 */
	@Override
	public SWTBotMenu click() {
		log.debug(MessageFormat.format("Clicking on {0}", this)); //$NON-NLS-1$
		waitForEnabled();
		syncExec(new VoidResult() {
			public void run() {
				widget.setSelection(true);
			}
		});
		notify(SWT.Selection);
		log.debug(MessageFormat.format("Clicked on {0}", this)); //$NON-NLS-1$
		return this;
	}

}
