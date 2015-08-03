/*
 * Copyright 2006-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.consol.citrus.http.socket.endpoint;

import com.consol.citrus.endpoint.AbstractEndpointConfiguration;
import com.consol.citrus.http.socket.message.WebSocketMessageConverter;

/**
 * Abstract endpoint configuration implementation provides basic endpoint properties for web socket endpoints.
 * @author Martin Maher
 * @since 2.3
 */
public abstract class AbstractWebSocketEndpointConfiguration extends AbstractEndpointConfiguration implements WebSocketEndpointConfiguration {
    /** Web socket server endpoint uri */
    private String endpointUri;

    /** The message converter */
    private WebSocketMessageConverter messageConverter = new WebSocketMessageConverter();

    @Override
    public WebSocketMessageConverter getMessageConverter() {
        return messageConverter;
    }

    @Override
    public void setMessageConverter(WebSocketMessageConverter messageConverter) {
        this.messageConverter = messageConverter;
    }

    @Override
    public String getEndpointUri() {
        return endpointUri;
    }

    @Override
    public void setEndpointUri(String endpointUri) {
        this.endpointUri = endpointUri;
    }
}
