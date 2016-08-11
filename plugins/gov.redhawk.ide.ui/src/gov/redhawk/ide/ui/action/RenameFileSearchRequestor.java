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
package gov.redhawk.ide.ui.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.ltk.core.refactoring.participants.RefactoringParticipant;
import org.eclipse.search.core.text.TextSearchEngine;
import org.eclipse.search.core.text.TextSearchMatchAccess;
import org.eclipse.search.core.text.TextSearchRequestor;
import org.eclipse.search.ui.text.FileTextSearchScope;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEditGroup;

/**
 * @since 10.1
 */
public class RenameFileSearchRequestor extends TextSearchRequestor {

	private final RefactoringParticipant participant;

	public RenameFileSearchRequestor(RefactoringParticipant participant) {
		this.participant = participant;
	}

	/** List of files that will be changed */
	private final List<IFile> affectedFiles = new ArrayList<IFile>();

	/** Map of all TextFileChange objects. These are used to update resource contents to the new name */
	private final HashMap<IFile, TextFileChange> changes = new HashMap<IFile, TextFileChange>();

	/** Id used to tack refactor operation */
	private String changeId;

	/** The new project name (Could be either dot(.) or forward-slash(/) delimited */
	private String newText;

	/** The old project name (Could be either dot(.) or forward-slash(/) delimited */
	private String oldText;

	/**
	 * The main mechanism for creating new TextChange objects to be applied for the refactor
	 * @param scope - FileTextSearchScope: used to determine which files to apply the pattern to
	 * @param changeId - String: ID used to used to track the change in the UI
	 * @param pattern - Pattern: used to precisely determine which text elements should be considered for refactoring
	 * @param oldText - String: text to be replaced
	 * @param newText - String: text that will be replacing any oldName elements that are detected by the pattern
	 * @param pm - ProgressMonitor
	 */
	public void createChange(FileTextSearchScope scope, String changeId, Pattern pattern, String newText, String oldText, IProgressMonitor pm) {
		this.changeId = changeId;
		this.newText = newText;
		this.oldText = oldText;
		TextSearchEngine.create().search(scope, this, pattern, pm);
	}

	public boolean acceptPatternMatch(final TextSearchMatchAccess matchAccess) throws CoreException {
		final IFile matchedFile = (IFile) matchAccess.getFile();

		if (!this.affectedFiles.contains(matchedFile)) {
			this.affectedFiles.add(matchedFile);
		}

		// Check if we have already recorded a change against this file
		TextFileChange change = changes.get(matchedFile);

		// If this is the fist time we have seen this file, make a new
		// TextChange object to track all updates to be applied to this file
		if (change == null) {
			final TextChange textChange = this.participant.getTextChange(matchedFile);
			if (textChange != null) {
				return false;
			}

			change = new TextFileChange(changeId, matchedFile);
			change.setEdit(new MultiTextEdit());
			changes.put(matchedFile, change);
		}

		String originalText = matchAccess.getFileContent(matchAccess.getMatchOffset(), matchAccess.getMatchLength());
		String replacementText = "";
		if (matchedFile.getName().endsWith(".spec") && originalText.contains("%dir")) {
			replacementText = buildDirectoryBlock(originalText);
		} else {
			replacementText = originalText.replace(this.oldText, this.newText);
		}

		final ReplaceEdit edit = new ReplaceEdit(matchAccess.getMatchOffset(), matchAccess.getMatchLength(), replacementText);
		change.addEdit(edit);
		change.addTextEditGroup(new TextEditGroup("Update type reference", edit));
		return true;
	}

	/**
	 * Builds a text block for each directory in the namespaced path. For example: A/B/Foo becomes <br>
	 * &ltprefix&gt/A</br>
	 * &ltprefix&gt/A/B</br>
	 * &ltprefix&gt/A/B/Foo</br>
	 * 
	 * @param originalText - The original text block from the file
	 * @return
	 */
	private String buildDirectoryBlock(String originalText) {
		StringBuilder builder = new StringBuilder();

		// Preserve the existing prefix, as this is specific to the project type
		String[] textArray = originalText.split("\n");
		String pathPrefix = textArray[textArray.length - 1];
		pathPrefix = pathPrefix.substring(0, pathPrefix.indexOf(this.oldText) - 1);

		// Add lines to the directory block for each folder in the path
		String[] newPathElements = this.newText.split("/");
		String pathSuffix = "";
		for (int i = 0; i < newPathElements.length; i++) {
			pathSuffix = pathSuffix + "/" + newPathElements[i];
			if (i == newPathElements.length - 1) {
				builder.append(pathPrefix + pathSuffix);
			} else {
				builder.append(pathPrefix + pathSuffix + "\n");
			}
		}

		// Return the formated block
		return builder.toString();
	}

	public List<IFile> getAffectedFiles() {
		return affectedFiles;
	}

	public HashMap<IFile, TextFileChange> getChanges() {
		return changes;
	}

}
