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

    public abstract boolean hasExpression();

    public abstract String getReference();

    public String getFullName(){
        return DIRECTIVE_PREFIX+getName();
    }
    
    public String getExpressionType() {
        return null;
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
