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
import com.igormaznitsa.jcpreprocessor.utils.PreprocessorUtils;

/**
 * The class implements //#assert directive handler 
 * 
 * @author Igor Maznitsa (igor.maznitsa@igormaznitsa.com)
 */
public class AssertDirectiveHandler extends AbstractDirectiveHandler {

    @Override
    public String getName() {
        return "assert";
    }

    @Override
    public DirectiveArgumentType getArgumentType() {
        return DirectiveArgumentType.TAIL;
    }

    @Override
    public String getReference() {
        return "asserts some info to the console, it supports macroses";
    }

    @Override
    public AfterDirectiveProcessingBehaviour execute(final String string, final PreprocessorContext configurator, final PreprocessingState state) {
        configurator.logInfo(PreprocessorUtils.processMacroses(string.trim(), configurator, state));
        return AfterDirectiveProcessingBehaviour.PROCESSED;
    }
}
