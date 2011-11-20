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
package com.igormaznitsa.jcpreprocessor.directives;

import com.igormaznitsa.jcpreprocessor.containers.PreprocessingState;
import com.igormaznitsa.jcpreprocessor.context.PreprocessorContext;

public abstract class AbstractDirectiveHandler {

    public static final String DIRECTIVE_PREFIX = "//#";
    
    public static final AbstractDirectiveHandler[] DIRECTIVES = new AbstractDirectiveHandler[]{
        // Order makes sense !!!
        new LocalDirectiveHandler(),
        new IfDefinedDirectiveHandler(),
        new IfDirectiveHandler(),
        new WhileDirectiveHandler(),
        new BreakDirectiveHandler(),
        new ContinueDirectiveHandler(),
        new ElseDirectiveHandler(),
        new EndIfDirectiveHandler(),
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

    public static final AbstractDirectiveHandler[] GLOBAL_DIRECTIVES = new AbstractDirectiveHandler[]{
        new GlobalDirectiveHandler(),
        new GlobalElseDirectiveHandler(),
        new GlobalEndIfDirectiveHandler(),
        new GlobalIfDirectiveHandler(),
        new ExcludeIfDirectiveHandler()
    };

    public abstract String getName();

    public abstract String getReference();

    public String getFullName(){
        return DIRECTIVE_PREFIX+getName();
    }
    
    public DirectiveArgumentType getArgumentType() {
        return DirectiveArgumentType.NONE;
    }
    
    public abstract AfterProcessingBehaviour execute(String string, PreprocessingState state, PreprocessorContext context);

    public boolean executeOnlyWhenExecutionAllowed() {
        return true;
    }

    public boolean isGlobalPhaseAllowed() {
        return false;
    }

    public boolean isPreprocessingPhaseAllowed() {
        return true;
    }
}
