/**
 * Copyright 2013-2014 Linagora, Université Joseph Fourier
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.roboconf.core.model.parsing;

import net.roboconf.core.internal.utils.Utils;

/**
 * The base definition for all the blocks.
 * @author Vincent Zurczak - Linagora
 */
public abstract class AbstractBlock {

	public static final int PROPERTY = 0;
	public static final int IMPORT = 1;
	public static final int FACET = 2;
	public static final int COMPONENT = 3;
	public static final int COMMENT = 4;
	public static final int BLANK = 5;
	public static final int INSTANCEOF = 6;

	private String inlineComment;
	private int line;
	private final FileDefinition declaringFile;


	/**
	 * Constructor.
	 * @param declaringFile not null
	 */
	public AbstractBlock( FileDefinition declaringFile ) {
		if( declaringFile == null )
			throw new IllegalArgumentException( "Declaring file cannot be null." );

		this.declaringFile = declaringFile;
	}

	/**
	 * @return the in-line comment (always starts with a sharp '#' symbol)
	 */
	public String getInlineComment() {
		return this.inlineComment;
	}

	/**
	 * @param inlineComment the inlineComment to set
	 */
	public void setInlineComment( String inlineComment ) {
		this.inlineComment = Utils.isEmptyOrWhitespaces( inlineComment ) ? null : inlineComment;
	}

	/**
	 * @return the line
	 */
	public int getLine() {
		return this.line;
	}

	/**
	 * @param line the line to set
	 */
	public void setLine( int line ) {
		this.line = line;
	}

	/**
	 * @return the block type
	 */
	public abstract int getInstructionType();

	/**
	 * @return the declaringFile
	 */
	public FileDefinition getDeclaringFile() {
		return this.declaringFile;
	}
}
