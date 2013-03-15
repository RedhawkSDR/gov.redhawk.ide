package org.jacorb.naming;

/*
 *        JacORB - a free Java ORB
 *
 *   Copyright (C) 1997-2004 Gerald Brose.
 *
 *   This library is free software; you can redistribute it and/or
 *   modify it under the terms of the GNU Library General Public
 *   License as published by the Free Software Foundation; either
 *   version 2 of the License, or (at your option) any later version.
 *
 *   This library is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *   Library General Public License for more details.
 *
 *   You should have received a copy of the GNU Library General Public
 *   License along with this library; if not, write to the Free
 *   Software Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

/**   
 * Implementation of the  "BindingIterator" interface
 * @author Gerald Brose
 * @version $Id: BindingIteratorImpl.java,v 1.7 2004/05/06 12:39:59 nicolas Exp $
 */

import org.omg.CosNaming.Binding;

public class BindingIteratorImpl extends org.omg.CosNaming.BindingIteratorPOA {
	Binding[] bindings;
	int iterator_pos = 0;

	public BindingIteratorImpl(final Binding[] b) {
		this.bindings = b;
		if (b.length > 0) {
			this.iterator_pos = 0;
		}
	}

	public void destroy() {
		this.bindings = null;
		try {
			finalize();
		} catch (final Throwable t) {
		}
	}

	public boolean next_n(final int how_many, final org.omg.CosNaming.BindingListHolder bl) {
		final int diff = this.bindings.length - this.iterator_pos;
		if (diff > 0) {
			Binding[] bndgs = null;
			if (how_many <= diff) {
				bndgs = new Binding[how_many];
				System.arraycopy(this.bindings, this.iterator_pos, bndgs, 0, how_many);
				this.iterator_pos += how_many;
			} else {
				bndgs = new Binding[diff];
				System.arraycopy(this.bindings, this.iterator_pos, bndgs, 0, diff);
				this.iterator_pos = this.bindings.length;
			}
			bl.value = bndgs;
			return true;
		} else {
			bl.value = new org.omg.CosNaming.Binding[0];
			return false;
		}
	}

	public boolean next_one(final org.omg.CosNaming.BindingHolder b) {
		if (this.iterator_pos < this.bindings.length) {
			b.value = this.bindings[this.iterator_pos++];
			return true;
		} else {
			b.value = new Binding(new org.omg.CosNaming.NameComponent[0], org.omg.CosNaming.BindingType.nobject);
			return false;
		}
	}
}
