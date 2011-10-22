package com.igormaznitsa.jcpreprocessor.directives;

import com.igormaznitsa.jcpreprocessor.cfg.PreprocessorContext;
import java.io.IOException;

public abstract class AbstractDirectiveHandler {
    public static final AbstractDirectiveHandler [] DIRECTIVES = new AbstractDirectiveHandler [] {
      new LocalDirectiveHandler(),
      new AssertDirectiveHandler(),
      new WhileDirectiveHandler(),
      new BreakDirectiveHandler(),
      new OutDirDirectiveHandler(),
      new OutEnabledDirectiveHandler(),
      new OutNameDirectiveHandler(),
      new OutDisabledDirectiveHandler(),
      new CommentNextLineDirectiveHandler(),
      new ContinueDirectiveHandler(),
      new DefineDirectiveHandler(),
      new ElseDirectiveHandler(),
      new EndIfDirectiveHandler(),
      new EndDirectiveHandler(),
      new ExitIfDirectiveHandler(),
      new ExitDirectiveHandler(),
      new FlushDirectiveHandler(),
      new IfDefinedDirectiveHandler(),
      new IfDirectiveHandler(),
      new IncludeDirectiveHandler(),
      new ActionDirectiveHandler(),
    };
    
    public abstract String getName();
    public abstract boolean hasExpression();
    public abstract DirectiveBehaviour execute(String string, ParameterContainer state, PreprocessorContext configurator) throws IOException;
    
    public boolean processOnlyIfProcessingEnabled(){
        return true;
    }
}
