/*
 * Copyright 2006-2010 the original author or authors.
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

package com.consol.citrus.config.xml;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.consol.citrus.actions.TraceVariablesAction;
import com.consol.citrus.testng.AbstractActionParserTest;

/**
 * @author Christoph Deppisch
 */
public class TraceVariablesActionParserTest extends AbstractActionParserTest<TraceVariablesAction> {

    @Test
    public void testTraceVariablesActionParser() {
        assertActionCount(2);
        assertActionClassAndName(TraceVariablesAction.class, "trace");
        
        TraceVariablesAction action = getNextTestActionFromTest();
        Assert.assertEquals(action.getVariableNames().size(), 0);
        
        action = getNextTestActionFromTest();
        Assert.assertEquals(action.getVariableNames().size(), 1);
        Assert.assertEquals(action.getVariableNames().get(0), "foo");
    }
}