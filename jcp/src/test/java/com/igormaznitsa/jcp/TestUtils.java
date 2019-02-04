/*
 * Copyright 2019 Igor Maznitsa.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.igormaznitsa.jcp;

import java.io.File;
import java.nio.file.Path;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.junit.Assert;

public final class TestUtils {
    private TestUtils(){}
    
    @Nullable
    public static String normalizeNextLine(@Nullable final String text) {
        return text == null ? null : text.replace("\r\n", "\n");
    }
    
    public static void assertFilePath(@Nonnull final String message, @Nonnull final File expected, @Nonnull final File check) {
        final Path pathExpected = expected.toPath().normalize();
        final Path pathCheck = check.toPath().normalize();
        if (!pathExpected.equals(pathCheck)){
            Assert.fail("Expected path '"+pathExpected.toString()+"' but detected path '"+pathCheck.toString()+"'");
        }
    }
}
