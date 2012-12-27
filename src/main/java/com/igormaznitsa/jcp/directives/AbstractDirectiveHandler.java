/*
 * Copyright 2011 Igor Maznitsa (http://www.igormaznitsa.com)
 *
 * This library is free software; you can redistribute it and/or modify
 * it under the terms of version 3 of the GNU Lesser General Public
 * License as published by the Free Software Foundation.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307  USA
 */
package com.igormaznitsa.jcp.directives;

import com.igormaznitsa.jcp.context.PreprocessorContext;

/**
 * The class is the abstract parent for all classes process preprocessor directives 
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public abstract class AbstractDirectiveHandler {

    /**
     * The common preprocessor prefix for all directives
     */
    public static final String DIRECTIVE_PREFIX = "//#";
    
    /**
     * The array contains all directives of the preprocessor
     */
    public static final AbstractDirectiveHandler[] DIRECTIVES = new AbstractDirectiveHandler[]{
        // Order makes sense !!!
        new LocalDirectiveHandler(),
        new IfDefinedDirectiveHandler(),
        new IfDirectiveHandler(),
        new ElseDirectiveHandler(),
        new EndIfDirectiveHandler(),
        new WhileDirectiveHandler(),
        new BreakDirectiveHandler(),
        new ContinueDirectiveHandler(),
        new EndDirectiveHandler(),
        new ExitIfDirectiveHandler(),
        new ExitDirectiveHandler(),
        new AssertDirectiveHandler(),
        new OutDirDirectiveHandler(),
        new OutEnabledDirectiveHandler(),
        new OutNameDirectiveHandler(),
        new OutDisabledDirectiveHandler(),
        new CommentNextLineDirectiveHandler(),
        new DefineDirectiveHandler(),
        new FlushDirectiveHandler(),
        new IncludeDirectiveHandler(),
        new ActionDirectiveHandler(),
        new PostfixDirectiveHandler(),
        new PrefixDirectiveHandler(),
        new GlobalDirectiveHandler(),
        new GlobalElseDirectiveHandler(),
        new GlobalEndIfDirectiveHandler(),
        new GlobalIfDirectiveHandler(),
        new ExcludeIfDirectiveHandler()
    };

    /**
     * The array contains preprocessor directives active only during the global preprocessing phase
     */
    public static final AbstractDirectiveHandler[] GLOBAL_DIRECTIVES = new AbstractDirectiveHandler[]{
        new GlobalDirectiveHandler(),
        new GlobalElseDirectiveHandler(),
        new GlobalEndIfDirectiveHandler(),
        new GlobalIfDirectiveHandler(),
        new ExcludeIfDirectiveHandler()
    };

    /**
     * Get the name of the directive without prefix
     * @return the directive name, must not be null
     */
    public abstract String getName();

    /**
     * Get the directive reference, it will be printed for a help request
     * @return the directive reference as a String, must not be null
     */
    public abstract String getReference();

    /**
     * Get the directive name with prefix
     * @return the full directive name (it including prefix)
     */
    public String getFullName(){
        return DIRECTIVE_PREFIX+getName();
    }
    
    /**
     * Get the argument type needed by the directive
     * @return the argument type needed by the directive, it can't be null
     */
    public DirectiveArgumentType getArgumentType() {
        return DirectiveArgumentType.NONE;
    }
    
    /**
     * Execute directive
     * @param string the tail string from the string where the directive has been met, must not be null but can be empty one
     * @param context the preprocessor context, it can be null
     * @return the needed preprocessor behavior, must not be null
     */
    public abstract AfterDirectiveProcessingBehaviour execute(String string, PreprocessorContext context);

    /**
     * Shows that the directive can be executed only when the preprocessing n active state i.e. if it is in active block //#if..//#endif of //#while
     * @return true if the directive can be executed only if it is in active block, else the directive will be called in any case
     */
    public boolean executeOnlyWhenExecutionAllowed() {
        return true;
    }

    /**
     * Shows that the directive can be executed during a global preprocessing phase
     * @return true if the directive allows the global directive phase, false if the directive must be ignored during that phase
     */
    public boolean isGlobalPhaseAllowed() {
        return false;
    }

    /**
     * Shows that the directive can be executed during the second preprocessing phase 
     * @return true uf the directive can be executed during the second preprocessing phase else false if the directive must be ignored
     */
    public boolean isPreprocessingPhaseAllowed() {
        return true;
    }
}
