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

import java.util.Vector;

import org.omg.CORBA.INTERNAL;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextPackage.InvalidName;

/**
 * A convenience class for names and converting
 * between Names and their string representation
 *
 * @author Gerald Brose, FU Berlin
 * @version $Id: Name.java,v 1.14 2006/06/14 11:56:28 alphonse.bendt Exp $
 */

public class Name implements java.io.Serializable {
	private final NameComponent[] fullName;
	private final NameComponent baseName;

	/** context part of this Name */
	private NameComponent[] ctxName;

	public Name() {
		this.fullName = null;
		this.baseName = null;
		this.ctxName = null;
	}

	/**
	 *	create a name from an array of NameComponents
	 */

	public Name(final NameComponent[] n) throws InvalidName {
		if (n == null || n.length == 0) {
			throw new InvalidName();
		}

		this.fullName = n;
		this.baseName = n[n.length - 1];
		if (n.length > 1) {
			this.ctxName = new NameComponent[n.length - 1];
			for (int i = 0; i < n.length - 1; i++) {
				this.ctxName[i] = n[i];
			}
		} else {
			this.ctxName = null;
		}
	}

	/**
	 *	create a name from a stringified name
	 */

	public Name(final String string_name) throws org.omg.CosNaming.NamingContextPackage.InvalidName {
		this(Name.toName(string_name));
	}

	/**
	 *	create a name from a singleNameComponent
	 */

	public Name(final org.omg.CosNaming.NameComponent n) throws org.omg.CosNaming.NamingContextPackage.InvalidName {
		if (n == null) {
			throw new org.omg.CosNaming.NamingContextPackage.InvalidName();
		}
		this.baseName = n;
		this.fullName = new org.omg.CosNaming.NameComponent[1];
		this.fullName[0] = n;
		this.ctxName = null;
	}

	/**
	 *	@return a NameComponent object representing the unstructured
	 *	base name of this structured name
	 */

	public org.omg.CosNaming.NameComponent baseNameComponent() {
		return this.baseName;
	}

	public String kind() {
		return this.baseName.kind;
	}

	/**
	 *	@return this name as an array of org.omg.CosNaming.NameComponent,
	 *	neccessary for a number of operations on naming context
	 */

	public org.omg.CosNaming.NameComponent[] components() {
		return this.fullName;
	}

	/**
	 *	@return a Name object representing the name of the enclosing context
	 */

	public Name ctxName() {
		// null if no further context
		if (this.ctxName != null) {
			try {
				return new Name(this.ctxName);
			} catch (final org.omg.CosNaming.NamingContextPackage.InvalidName e) {
				throw new INTERNAL(e.toString());
			}
		}
		return null;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Name)) {
			return false;
		}
		return (toString().equals(obj.toString()));
	}

	public Name fullName() throws org.omg.CosNaming.NamingContextPackage.InvalidName {
		return new Name(this.fullName);
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	/**
	 * @return  the string representation of this name
	 */

	@Override
	public String toString() {
		try {
			return Name.toString(this.fullName);
		} catch (final InvalidName in) {
			return "<invalid>";
		}
	}

	/**
	 * @return a single NameComponent, parsed from sn
	 */

	private static org.omg.CosNaming.NameComponent getComponent(final String sn) throws org.omg.CosNaming.NamingContextPackage.InvalidName {
		char ch;
		final int len = sn.length();
		boolean inKind = false;
		final StringBuffer id = new StringBuffer();
		final StringBuffer kind = new StringBuffer();

		for (int i = 0; i < len; i++) {
			ch = sn.charAt(i);

			if (ch == '\\') {
				// Escaped character

				i++;
				if (i >= len) {
					throw new InvalidName();
				}
				ch = sn.charAt(i);
			} else if (ch == '.') {
				// id/kind separator character

				if (inKind) {
					throw new InvalidName();
				}
				inKind = true;
				continue;
			}
			if (inKind) {
				kind.append(ch);
			} else {
				id.append(ch);
			}
		}

		return (new org.omg.CosNaming.NameComponent(id.toString(), kind.toString()));
	}

	/**
	 *
	 * @return an a array of NameComponents
	 * @throws org.omg.CosNaming.NamingContextPackage.InvalidName
	 */

	public static org.omg.CosNaming.NameComponent[] toName(final String sn) throws org.omg.CosNaming.NamingContextPackage.InvalidName {
		if (sn == null || sn.length() == 0 || sn.startsWith("/")) {
			throw new InvalidName();
		}

		final Vector v = new Vector();

		int start = 0;
		int i = 0;
		for (; i < sn.length(); i++) {
			if (sn.charAt(i) == '/' && sn.charAt(i - 1) != '\\') {
				if (i - start == 0) {
					throw new InvalidName();
				}
				v.addElement(Name.getComponent(sn.substring(start, i)));
				start = i + 1;
			}
		}
		if (start < i) {
			v.addElement(Name.getComponent(sn.substring(start, i)));
		}

		final org.omg.CosNaming.NameComponent[] result = new org.omg.CosNaming.NameComponent[v.size()];

		for (int j = 0; j < result.length; j++) {
			result[j] = (org.omg.CosNaming.NameComponent) v.elementAt(j);
		}
		return result;
	}

	/**
	 * @return the string representation of this NameComponent array
	 */

	public static String toString(final org.omg.CosNaming.NameComponent[] n) throws org.omg.CosNaming.NamingContextPackage.InvalidName {
		if (n == null || n.length == 0) {
			throw new org.omg.CosNaming.NamingContextPackage.InvalidName();
		}

		final StringBuffer b = new StringBuffer();
		for (int i = 0; i < n.length; i++) {
			if (i > 0) {
				b.append("/");
			}

			if (n[i].id.length() > 0) {
				b.append(Name.escape(n[i].id));
			}

			if (n[i].kind.length() > 0 || n[i].id.length() == 0) {
				b.append(".");
			}

			if (n[i].kind.length() > 0) {
				b.append(Name.escape(n[i].kind));
			}
		}
		return b.toString();
	}

	/**
	 * escape any occurrence of "/", "." and "\"
	 */

	private static String escape(final String s) {
		final StringBuffer sb = new StringBuffer(s);
		for (int i = 0; i < sb.length(); i++) {
			if (sb.charAt(i) == '/' || sb.charAt(i) == '\\' || sb.charAt(i) == '.') {
				sb.insert(i, '\\');
				i++;
			}
		}
		return sb.toString();
	}

}
