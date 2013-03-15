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
package gov.redhawk.ide.debug.internal;


import org.omg.CORBA.Object;
import org.omg.CosNaming.BindingIteratorHolder;
import org.omg.CosNaming.BindingListHolder;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContext;
import org.omg.CosNaming.NamingContextExtOperations;
import org.omg.CosNaming.NamingContextExtPOA;
import org.omg.CosNaming.NamingContextExtPackage.InvalidAddress;
import org.omg.CosNaming.NamingContextPackage.AlreadyBound;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.InvalidName;
import org.omg.CosNaming.NamingContextPackage.NotEmpty;
import org.omg.CosNaming.NamingContextPackage.NotFound;

public class NamingContextExtPOATie extends NamingContextExtPOA {

	private final NamingContextExtOperations impl;
	private org.omg.PortableServer.POA poa;

	public NamingContextExtPOATie(final NamingContextExtOperations delegate) {
		this.impl = delegate;
	}

	public NamingContextExtPOATie(final NamingContextExtOperations delegate, final org.omg.PortableServer.POA poa) {
		this.impl = delegate;
		this.poa = poa;
	}

	@Override
	public org.omg.PortableServer.POA _default_POA() { //SUPPRESS CHECKSTYLE Method Name
		if (this.poa != null) {
			return this.poa;
		} else {
			return super._default_POA();
		}
	}

	public void bind(final NameComponent[] n, final Object obj) throws NotFound, CannotProceed, InvalidName, AlreadyBound {
		this.impl.bind(n, obj);
	}

	public String to_string(final NameComponent[] n) throws InvalidName {
		return this.impl.to_string(n);
	}

	public NameComponent[] to_name(final String sn) throws InvalidName {
		return this.impl.to_name(sn);
	}

	public String to_url(final String addr, final String sn) throws InvalidAddress, InvalidName {
		return this.impl.to_url(addr, sn);
	}

	public void bind_context(final NameComponent[] n, final NamingContext nc) throws NotFound, CannotProceed, InvalidName, AlreadyBound {
		this.impl.bind_context(n, nc);
	}

	public Object resolve_str(final String sn) throws NotFound, CannotProceed, InvalidName {
		return this.impl.resolve_str(sn);
	}

	public void rebind(final NameComponent[] n, final Object obj) throws NotFound, CannotProceed, InvalidName {
		this.impl.rebind(n, obj);
	}

	public void rebind_context(final NameComponent[] n, final NamingContext nc) throws NotFound, CannotProceed, InvalidName {
		this.impl.rebind_context(n, nc);
	}

	public Object resolve(final NameComponent[] n) throws NotFound, CannotProceed, InvalidName {
		return this.impl.resolve(n);
	}

	public void unbind(final NameComponent[] n) throws NotFound, CannotProceed, InvalidName {
		this.impl.unbind(n);
	}

	public void list(final int howMany, final BindingListHolder bl, final BindingIteratorHolder bi) {
		this.impl.list(howMany, bl, bi);
	}

	public NamingContext new_context() {
		return this.impl.new_context();
	}

	public NamingContext bind_new_context(final NameComponent[] n) throws NotFound, AlreadyBound, CannotProceed, InvalidName {
		return this.impl.bind_new_context(n);
	}

	public void destroy() throws NotEmpty {
		this.impl.destroy();
	}

} // class NamingContextExtPOATie
