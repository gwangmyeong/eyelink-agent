package com.m2u.eyelink.collector.rpc.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

import org.apache.thrift.TBase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import com.m2u.eyelink.collector.bo.AgentEventBo;
import com.m2u.eyelink.collector.cluster.route.ResponseEvent;
import com.m2u.eyelink.collector.dao.AgentEventDao;
import com.m2u.eyelink.collector.server.util.AgentEventMessageSerializer;
import com.m2u.eyelink.collector.util.AgentEventType;
import com.m2u.eyelink.context.HandshakePropertyType;
import com.m2u.eyelink.context.thrift.DeserializerFactory;
import com.m2u.eyelink.context.thrift.HeaderTBaseDeserializer;
import com.m2u.eyelink.context.thrift.TCommandEcho;
import com.m2u.eyelink.context.thrift.TCommandThreadDumpResponse;
import com.m2u.eyelink.context.thrift.TCommandTransfer;
import com.m2u.eyelink.context.thrift.TCommandTransferResponse;
import com.m2u.eyelink.context.thrift.TRouteResult;
import com.m2u.eyelink.rpc.server.ELAgentServer;
import com.m2u.eyelink.util.BytesUtils;

@RunWith(MockitoJUnitRunner.class)
public class AgentEventHandlerTest {
    // FIX guava 19.0.0 update error. MoreExecutors.sameThreadExecutor(); change final class
    @Spy
    private Executor executor = new DirectExecutor();

    @Mock
    private ELAgentServer elagentServer;

    @Mock
    private AgentEventDao agentEventDao;

    @Mock
    private AgentEventMessageSerializer agentEventMessageSerializer;

    @Mock
    private DeserializerFactory<HeaderTBaseDeserializer> deserializerFactory;

    @InjectMocks
    private AgentEventHandler agentEventHandler = new AgentEventHandler();

    private static final String TEST_AGENT_ID = "TEST_AGENT";
    private static final long TEST_START_TIMESTAMP = System.currentTimeMillis();
    private static final long TEST_EVENT_TIMESTAMP = TEST_START_TIMESTAMP + 10;
    private static final Map<Object, Object> TEST_CHANNEL_PROPERTIES = createTestChannelProperties();

    @Before
    public void setUp() {
        when(this.elagentServer.getChannelProperties()).thenReturn(TEST_CHANNEL_PROPERTIES);
    }

    @Test
    public void handler_should_handle_events_with_empty_message_body() throws Exception {
        // given
        final AgentEventType expectedEventType = AgentEventType.AGENT_CONNECTED;
        ArgumentCaptor<AgentEventBo> argCaptor = ArgumentCaptor.forClass(AgentEventBo.class);
        // when
        this.agentEventHandler.handleEvent(this.elagentServer, TEST_EVENT_TIMESTAMP, expectedEventType);
        verify(this.agentEventDao, times(1)).insert(argCaptor.capture());
        // then
        AgentEventBo actualAgentEventBo = argCaptor.getValue();
        assertEquals(TEST_AGENT_ID, actualAgentEventBo.getAgentId());
        assertEquals(TEST_START_TIMESTAMP, actualAgentEventBo.getStartTimestamp());
        assertEquals(TEST_EVENT_TIMESTAMP, actualAgentEventBo.getEventTimestamp());
        assertEquals(expectedEventType, actualAgentEventBo.getEventType());
        assertNull(actualAgentEventBo.getEventBody());
    }

    @Test
    public void handler_should_handle_serialization_of_messages_appropriately() throws Exception {
        // given
        final AgentEventType expectedEventType = AgentEventType.OTHER;
        final String expectedMessageBody = "test event message";
        final byte[] expectedMessageBodyBytes = BytesUtils.toBytes(expectedMessageBody);
        ArgumentCaptor<AgentEventBo> argCaptor = ArgumentCaptor.forClass(AgentEventBo.class);
        when(this.agentEventMessageSerializer.serialize(expectedEventType, expectedMessageBody)).thenReturn(
                expectedMessageBodyBytes);
        // when
        this.agentEventHandler.handleEvent(this.elagentServer, TEST_EVENT_TIMESTAMP, expectedEventType,
                expectedMessageBody);
        verify(this.agentEventDao, times(1)).insert(argCaptor.capture());
        // then
        AgentEventBo actualAgentEventBo = argCaptor.getValue();
        assertEquals(TEST_AGENT_ID, actualAgentEventBo.getAgentId());
        assertEquals(TEST_START_TIMESTAMP, actualAgentEventBo.getStartTimestamp());
        assertEquals(TEST_EVENT_TIMESTAMP, actualAgentEventBo.getEventTimestamp());
        assertEquals(expectedEventType, actualAgentEventBo.getEventType());
        assertEquals(expectedMessageBodyBytes, actualAgentEventBo.getEventBody());
    }

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void handler_should_handle_serialization_of_request_events() throws Exception {
        // given
        final AgentEventType expectedEventType = AgentEventType.USER_THREAD_DUMP;
        final TCommandThreadDumpResponse expectedThreadDumpResponse = new TCommandThreadDumpResponse();
        final byte[] expectedThreadDumpResponseBody = new byte[0];

        final TCommandTransfer tCommandTransfer = new TCommandTransfer();
        tCommandTransfer.setAgentId(TEST_AGENT_ID);
        tCommandTransfer.setStartTime(TEST_START_TIMESTAMP);

        final TCommandTransferResponse tCommandTransferResponse = new TCommandTransferResponse();
        tCommandTransferResponse.setRouteResult(TRouteResult.OK);
        tCommandTransferResponse.setPayload(expectedThreadDumpResponseBody);

        final ResponseEvent responseEvent = new ResponseEvent(tCommandTransfer, null, 0, tCommandTransferResponse);

        ArgumentCaptor<AgentEventBo> argCaptor = ArgumentCaptor.forClass(AgentEventBo.class);
        HeaderTBaseDeserializer deserializer = mock(HeaderTBaseDeserializer.class);
        when(this.deserializerFactory.createDeserializer()).thenReturn(deserializer);
        when(deserializer.deserialize(expectedThreadDumpResponseBody)).thenReturn((TBase)expectedThreadDumpResponse);
        // when
        this.agentEventHandler.handleResponseEvent(responseEvent, TEST_EVENT_TIMESTAMP);
        // then
        verify(this.agentEventDao, atLeast(1)).insert(argCaptor.capture());
        AgentEventBo actualAgentEventBo = argCaptor.getValue();
        assertEquals(TEST_AGENT_ID, actualAgentEventBo.getAgentId());
        assertEquals(TEST_START_TIMESTAMP, actualAgentEventBo.getStartTimestamp());
        assertEquals(TEST_EVENT_TIMESTAMP, actualAgentEventBo.getEventTimestamp());
        assertEquals(expectedEventType, actualAgentEventBo.getEventType());
        assertArrayEquals(expectedThreadDumpResponseBody, actualAgentEventBo.getEventBody());
    }

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void handler_should_ignore_request_events_with_unsupported_message_types() throws Exception {
        // given
        final TCommandEcho mismatchingResponse = new TCommandEcho();
        final byte[] mismatchingResponseBody = new byte[0];

        final TCommandTransfer tCommandTransfer = new TCommandTransfer();
        tCommandTransfer.setAgentId(TEST_AGENT_ID);
        tCommandTransfer.setStartTime(TEST_START_TIMESTAMP);

        final TCommandTransferResponse tCommandTransferResponse = new TCommandTransferResponse();
        tCommandTransferResponse.setRouteResult(TRouteResult.OK);
        tCommandTransferResponse.setPayload(mismatchingResponseBody);

        final ResponseEvent responseEvent = new ResponseEvent(tCommandTransfer, null, 0, tCommandTransferResponse);

        ArgumentCaptor<AgentEventBo> argCaptor = ArgumentCaptor.forClass(AgentEventBo.class);
        HeaderTBaseDeserializer deserializer = mock(HeaderTBaseDeserializer.class);
        when(this.deserializerFactory.createDeserializer()).thenReturn(deserializer);
        when(deserializer.deserialize(mismatchingResponseBody)).thenReturn((TBase)mismatchingResponse);
        // when
        this.agentEventHandler.handleResponseEvent(responseEvent, TEST_EVENT_TIMESTAMP);
        // then
        verify(this.agentEventDao, never()).insert(argCaptor.capture());
    }

    private static Map<Object, Object> createTestChannelProperties() {
        return createChannelProperties(TEST_AGENT_ID, TEST_START_TIMESTAMP);
    }

    private static Map<Object, Object> createChannelProperties(String agentId, long startTimestamp) {
        Map<Object, Object> map = new HashMap<>();
        map.put(HandshakePropertyType.AGENT_ID.getName(), agentId);
        map.put(HandshakePropertyType.START_TIMESTAMP.getName(), startTimestamp);
        return map;
    }

}
