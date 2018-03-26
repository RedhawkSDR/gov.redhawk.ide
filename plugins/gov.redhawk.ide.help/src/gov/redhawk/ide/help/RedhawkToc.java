/**
 * This file is protected by Copyright.
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 */
package gov.redhawk.ide.help;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.help.IToc;
import org.eclipse.help.ITopic;
import org.eclipse.help.IUAElement;

public class RedhawkToc implements IToc {

	private List<ITopic> topics;
	private ITopic topic;

	public RedhawkToc() {
	}

	@Override
	public boolean isEnabled(IEvaluationContext context) {
		return true;
	}

	@Override
	public IUAElement[] getChildren() {
		if (topics == null) {
			createTopics();
		}
		return getTopics();
	}

	@Override
	public String getHref() {
		return "/" + RedhawkHelpPlugin.PLUGIN_ID + "/html/index.html";
	}

	@Override
	public String getLabel() {
		return "REDHAWK";
	}

	@Override
	public ITopic[] getTopics() {
		if (topics == null) {
			createTopics();
		}
		return topics.toArray(new ITopic[topics.size()]);
	}

	@Override
	public ITopic getTopic(String href) {
		if (href == null) {
			if (topic == null) {
				topic = new ITopic() {

					@Override
					public String getHref() {
						return RedhawkToc.this.getHref();
					}

					@Override
					public String getLabel() {
						return RedhawkToc.this.getLabel();
					}

					@Override
					public ITopic[] getSubtopics() {
						return getTopics();
					}

					@Override
					public boolean isEnabled(IEvaluationContext context) {
						return RedhawkToc.this.isEnabled(context);
					}

					@Override
					public IUAElement[] getChildren() {
						return new IUAElement[0];
					}
				};
			}
			return topic;
		}
		return null;
	}

	private void createTopics() {
		topics = new ArrayList<>();
		topics.add(new RedhawkTopic("html/getting-started/index.html", "Getting Started"));
		topics.add(new RedhawkTopic("html/manual/index.html", "REDHAWK Manual"));
	}

}
