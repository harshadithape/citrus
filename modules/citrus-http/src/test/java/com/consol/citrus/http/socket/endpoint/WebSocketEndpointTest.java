/*
 * Copyright 2006-2015 the original author or authors.
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

package com.consol.citrus.http.socket.endpoint;

import com.consol.citrus.exceptions.ActionTimeoutException;
import com.consol.citrus.http.socket.handler.CitrusWebSocketHandler;
import com.consol.citrus.http.socket.message.WebSocketMessage;
import com.consol.citrus.message.DefaultMessage;
import com.consol.citrus.message.Message;
import com.consol.citrus.testng.AbstractTestNGUnitTest;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.springframework.web.socket.*;
import org.testng.Assert;
import org.testng.annotations.Test;

import static org.easymock.EasyMock.*;

public class WebSocketEndpointTest extends AbstractTestNGUnitTest {

    private WebSocketSession session = EasyMock.createMock(WebSocketSession.class);
    private WebSocketSession session2 = EasyMock.createMock(WebSocketSession.class);
    private WebSocketSession session3 = EasyMock.createMock(WebSocketSession.class);

    @Test
    public void testWebSocketEndpoint() throws Exception {
        WebSocketServerEndpointConfiguration endpointConfiguration = new WebSocketServerEndpointConfiguration();
        WebSocketEndpoint webSocketEndpoint = new WebSocketEndpoint(endpointConfiguration);
        String endpointUri = "/test";

        CitrusWebSocketHandler handler = new CitrusWebSocketHandler();
        endpointConfiguration.setHandler(handler);

        final String requestBody = "<TestRequest><Message>Hello World!</Message></TestRequest>";

        final Message responseMessage = new DefaultMessage("<TestResponse><Message>Hello World!</Message></TestResponse>");

        endpointConfiguration.setEndpointUri(endpointUri);

        reset(session);

        expect(session.getId()).andReturn("test-socket-1").atLeastOnce();
        expect(session.isOpen()).andReturn(true).once();

        session.sendMessage(anyObject(org.springframework.web.socket.WebSocketMessage.class));
        expectLastCall().andAnswer(new IAnswer<Void>() {
            @Override
            public Void answer() throws Throwable {
                org.springframework.web.socket.WebSocketMessage request = (org.springframework.web.socket.WebSocketMessage) getCurrentArguments()[0];

                Assert.assertTrue(TextMessage.class.isInstance(request));
                Assert.assertEquals(((TextMessage)request).getPayload(), responseMessage.getPayload(String.class));
                Assert.assertTrue(request.isLast());
                return null;
            }
        }).once();

        replay(session);

        handler.afterConnectionEstablished(session);
        handler.handleMessage(session, new TextMessage(requestBody));

        WebSocketMessage requestMessage = (WebSocketMessage) webSocketEndpoint.createConsumer().receive(context);
        Assert.assertEquals(requestMessage.getPayload(), requestBody);
        Assert.assertTrue(requestMessage.isLast());

        webSocketEndpoint.createProducer().send(responseMessage, context);

        verify(session);
    }

    @Test
    public void testWebSocketEndpointMultipleSessions() throws Exception {
        WebSocketServerEndpointConfiguration endpointConfiguration = new WebSocketServerEndpointConfiguration();
        WebSocketEndpoint webSocketEndpoint = new WebSocketEndpoint(endpointConfiguration);
        String endpointUri = "/test";

        CitrusWebSocketHandler handler = new CitrusWebSocketHandler();
        endpointConfiguration.setHandler(handler);

        final String requestBody = "<TestRequest><Message>Hello World!</Message></TestRequest>";

        final Message responseMessage = new DefaultMessage("<TestResponse><Message>Hello World!</Message></TestResponse>");

        endpointConfiguration.setEndpointUri(endpointUri);

        reset(session, session2, session3);

        expect(session.getId()).andReturn("test-socket-1").atLeastOnce();
        expect(session2.getId()).andReturn("test-socket-2").atLeastOnce();
        expect(session3.getId()).andReturn("test-socket-3").atLeastOnce();
        expect(session.isOpen()).andReturn(true).once();
        expect(session2.isOpen()).andReturn(true).once();

        session.sendMessage(anyObject(org.springframework.web.socket.WebSocketMessage.class));
        expectLastCall().andAnswer(new IAnswer<Void>() {
            @Override
            public Void answer() throws Throwable {
                org.springframework.web.socket.WebSocketMessage request = (org.springframework.web.socket.WebSocketMessage) getCurrentArguments()[0];

                Assert.assertTrue(TextMessage.class.isInstance(request));
                Assert.assertEquals(((TextMessage)request).getPayload(), responseMessage.getPayload(String.class));
                Assert.assertTrue(request.isLast());
                return null;
            }
        }).once();

        session2.sendMessage(anyObject(org.springframework.web.socket.WebSocketMessage.class));
        expectLastCall().andAnswer(new IAnswer<Void>() {
            @Override
            public Void answer() throws Throwable {
                org.springframework.web.socket.WebSocketMessage request = (org.springframework.web.socket.WebSocketMessage) getCurrentArguments()[0];

                Assert.assertTrue(TextMessage.class.isInstance(request));
                Assert.assertEquals(((TextMessage)request).getPayload(), responseMessage.getPayload(String.class));
                Assert.assertTrue(request.isLast());
                return null;
            }
        }).once();

        replay(session, session2, session3);

        handler.afterConnectionEstablished(session);
        handler.afterConnectionEstablished(session2);
        handler.afterConnectionEstablished(session3);
        handler.afterConnectionClosed(session3, CloseStatus.NORMAL);

        handler.handleMessage(session, new TextMessage(requestBody));

        WebSocketMessage requestMessage = (WebSocketMessage) webSocketEndpoint.createConsumer().receive(context);
        Assert.assertEquals(requestMessage.getPayload(), requestBody);
        Assert.assertTrue(requestMessage.isLast());

        webSocketEndpoint.createProducer().send(responseMessage, context);

        verify(session, session2, session3);
    }

    @Test
    public void testWebSocketEndpointTimeout() throws Exception {
        WebSocketServerEndpointConfiguration endpointConfiguration = new WebSocketServerEndpointConfiguration();
        WebSocketEndpoint webSocketEndpoint = new WebSocketEndpoint(endpointConfiguration);
        String endpointUri = "/test";

        CitrusWebSocketHandler handler = new CitrusWebSocketHandler();
        endpointConfiguration.setHandler(handler);
        endpointConfiguration.setEndpointUri(endpointUri);
        endpointConfiguration.setTimeout(1000L);

        reset(session);
        expect(session.getId()).andReturn("test-socket-1").atLeastOnce();
        replay(session);

        handler.afterConnectionEstablished(session);

        try {
            webSocketEndpoint.createConsumer().receive(context, endpointConfiguration.getTimeout());
            Assert.fail("Missing timeout exception on web socket endpoint");
        } catch (ActionTimeoutException e) {
            Assert.assertTrue(e.getMessage().contains(endpointUri));
        }

        verify(session);
    }
}