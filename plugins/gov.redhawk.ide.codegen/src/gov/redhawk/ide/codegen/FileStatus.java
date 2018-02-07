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
package gov.redhawk.ide.codegen;

/**
 * Gives information about a file that code generation will perform an action on. A {@link FileStatus} is {@link #equals(Object)} to
 * another instance if they describe the same file (regardless of how they describe it).
 * @since 10.0
 */
public class FileStatus {
	public static enum Action {
		ADDING,
		REMOVING,
		REGEN
	}

	public static enum State {
		MODIFIED,
		MATCHES
	}

	public static enum Type {
		SYSTEM,
		USER
	}

	private final String filename;
	private final Action desiredAction;
	private final Type type;
	private final boolean doItDefault;
	private boolean doIt;
	private final State state;

	public FileStatus(String filename, Action desiredAction, State state, Type type) {
		this.filename = filename;
		this.desiredAction = desiredAction;
		this.type = type;
		this.state = state;
		switch (desiredAction) {
		case ADDING:
			this.doIt = true;
			break;
		case REMOVING:
			if (state == State.MODIFIED) {
				if (type == Type.USER) {
					this.doIt = false;
				} else {
					this.doIt = false;
				}
			} else {
				if (type == Type.USER) {
					this.doIt = true;
				} else {
					this.doIt = true;
				}
			}
			break;
		case REGEN:
			if (state == State.MODIFIED) {
				if (type == Type.USER) {
					this.doIt = false;
				} else {
					this.doIt = false;
				}
			} else {
				if (type == Type.USER) {
					this.doIt = true;
				} else {
					this.doIt = true;
				}
			}
			break;
		default:
			break;
		}
		this.doItDefault = doIt;
	}

	public void setToDefault() {
		this.doIt = this.doItDefault;
	}

	public boolean getDoItDefault() {
		return doItDefault;
	}

	public State getState() {
		return state;
	}

	public boolean isDoIt() {
		return doIt;
	}

	public void setDoIt(boolean doIt) {
		this.doIt = doIt;
	}

	public String getFilename() {
		return filename;
	}

	public Action getDesiredAction() {
		return desiredAction;
	}

	public Type getType() {
		return type;
	}

	@Override
	public int hashCode() {
		return 31 * 17 + filename.hashCode();
	}

	@Override
	public String toString() {
		return filename;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof FileStatus) {
			return filename.equals(((FileStatus) obj).filename);
		}
		return false;
	}
}
