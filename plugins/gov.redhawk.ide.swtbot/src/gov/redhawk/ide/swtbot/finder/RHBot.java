/*******************************************************************************
 * This file is protected by Copyright. 
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package gov.redhawk.ide.swtbot.finder;

import static org.eclipse.swtbot.swt.finder.matchers.WidgetMatcherFactory.allOf;
import static org.eclipse.swtbot.swt.finder.matchers.WidgetMatcherFactory.widgetOfType;
import static org.eclipse.swtbot.swt.finder.matchers.WidgetMatcherFactory.withMnemonic;

import gov.redhawk.ide.swtbot.finder.widgets.RHBotFormText;
import gov.redhawk.ide.swtbot.finder.widgets.RHBotSection;

import org.eclipse.swt.widgets.Widget;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.finders.ControlFinder;
import org.eclipse.swtbot.swt.finder.finders.Finder;
import org.eclipse.swtbot.swt.finder.finders.MenuFinder;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.Section;
import org.hamcrest.Matcher;

/**
 * An extension of {@link SWTBot}
 */
public class RHBot extends SWTBot {
	
	/**
	 * Creates a new {@link RHBot} from an existing {@link SWTBot}.
	 * @param bot The existing bot
	 */
	public RHBot(SWTBot bot) {
		super(bot.getFinder());
	}

	/**
	 * @see SWTBot#SWTBot()
	 */
	public RHBot() {
		super();
	}

	/**
	 * @see SWTBot#SWTBot(Widget)
	 */
	public RHBot(Widget parent) {
		super(parent);
	}

	/**
	 * @see SWTBot#SWTBot(ControlFinder, MenuFinder)
	 */
	public RHBot(ControlFinder controlFinder, MenuFinder menuFinder) {
		super(controlFinder, menuFinder);
	}

	/**
	 * @see SWTBot#SWTBot(Finder)
	 */
	public RHBot(Finder finder) {
		super(finder);
	}
	
	/**
	 * @param mnemonicText the mnemonicText on the widget.
	 * @return a {@link RHBotSection} with the specified <code>mnemonicText</code>.
	 * @throws org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException if the widget is not found or is disposed.
	 */
	public RHBotSection section(String mnemonicText) {
		return section(mnemonicText, 0);
	}

	/**
	 * @param mnemonicText the mnemonicText on the widget.
	 * @param index the index of the widget.
	 * @return a {@link RHBotSection} with the specified <code>mnemonicText</code>.
	 * @throws org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException if the widget is not found or is disposed.
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	public RHBotSection section(String mnemonicText, int index) {
		Matcher matcher = allOf(widgetOfType(Section.class), withMnemonic(mnemonicText));
		return new RHBotSection((Section) widget(matcher, index), matcher);
	}

	/**
	 * @return a {@link RHBotFormText}
	 */
	public RHBotFormText formText() {
		return formText(0);
	}

	/**
	 * @param index the index of the widget.
	 * @return a {@link RHBotFormText}
	 */
	public RHBotFormText formText(int index) {
		@SuppressWarnings("unchecked")
		Matcher<FormText> matcher = allOf(widgetOfType(FormText.class));
		return new RHBotFormText(widget(matcher, index), matcher);
	}
}
