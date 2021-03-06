/* Copyright (c) 2011 Danish Maritime Authority.
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
package net.maritimecloud.mms.server.connection.transport;

import net.maritimecloud.mms.server.ServerEventListener;
import net.maritimecloud.mms.server.security.MmsSecurityManager;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import static java.util.Objects.requireNonNull;

/**
 * The endpoint taking care of creating server transports using the JSR 356 Websocket API.
 *
 * @author Kasper Nielsen
 */
@ServerEndpoint(value = "/")
public class ServerTransportJsr356Endpoint {

    /** The connection is attached to, or null, if it is not attached to one. */
    private volatile ServerTransport transport;

    /** The security manager */
    private final MmsSecurityManager securityManager;

    /** A factory for creating server transport listeners. */
    private final ServerTransportListener transportListener;

    /** A listener of events */
    private final ServerEventListener eventListener;

    public ServerTransportJsr356Endpoint(MmsSecurityManager securityManager, ServerEventListener eventListener, ServerTransportListener transport) {
        this.securityManager = requireNonNull(securityManager);
        this.eventListener = requireNonNull(eventListener);
        this.transportListener = requireNonNull(transport);
    }

    @OnClose
    public void onClose(CloseReason closeReason) {
        transport.endpointOnClose(closeReason);
        transport = null;
    }

    @OnOpen
    public void onOpen(Session session) {
        // Set the maximum size of messages we want to receive from clients.
        session.setMaxBinaryMessageBufferSize(5 * 1024 * 1024);
        session.setMaxTextMessageBufferSize(5 * 1024 * 1024);

        transport = new ServerTransport(securityManager, session, transportListener, eventListener);
        transport.endpointOnOpen();
    }

    @OnMessage
    public void onTextMessage(String textMessage) {
        transport.endpointOnTextMessage(textMessage);
    }

    @OnMessage
    public void onBinaryMessage(byte[] binaryMessage) {
        transport.endpointOnBinaryMessage(binaryMessage);
    }
}
