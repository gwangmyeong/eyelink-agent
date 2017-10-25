package com.m2u.eyelink.collector.handler;

import java.util.List;

import org.apache.thrift.TBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.m2u.eyelink.collector.bo.SpanBo;
import com.m2u.eyelink.collector.bo.SpanEventBo;
import com.m2u.eyelink.collector.bo.SpanFactory;
import com.m2u.eyelink.collector.dao.ApplicationTraceIndexDao;
import com.m2u.eyelink.collector.dao.HostApplicationMapDao;
import com.m2u.eyelink.collector.dao.TraceDao;
import com.m2u.eyelink.collector.dao.TraceDetailDao;
import com.m2u.eyelink.common.service.ServiceTypeRegistryService;
import com.m2u.eyelink.context.thrift.TSpan;
import com.m2u.eyelink.trace.ServiceType;
import com.m2u.eyelink.util.CollectionUtils;

@Service
public class SpanHandler implements SimpleHandler {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	@Qualifier("elasticSearchTraceDaoFactory")
	private TraceDao traceDao;

	@Autowired
	private TraceDetailDao traceDetailDao;

	@Autowired
	private ApplicationTraceIndexDao applicationTraceIndexDao;

	@Autowired
	private StatisticsHandler statisticsHandler;

	@Autowired
	private HostApplicationMapDao hostApplicationMapDao;

	@Autowired
	private ServiceTypeRegistryService registry;

	@Autowired
	private SpanFactory spanFactory;

	public void handleSimple(TBase<?, ?> tbase) {

		if (!(tbase instanceof TSpan)) {
			throw new IllegalArgumentException("unexpected tbase:" + tbase + " expected:" + this.getClass().getName());
		}

		try {
			final TSpan tSpan = (TSpan) tbase;
			if (logger.isDebugEnabled()) {
				logger.debug("Received SPAN={}", tSpan);
			}

			final SpanBo spanBo = spanFactory.buildSpanBo(tSpan);

			traceDao.insert(spanBo);
			traceDetailDao.insert(spanBo);
			applicationTraceIndexDao.insert(tSpan);

			// insert statistics info for server map
			insertAcceptorHost(spanBo);
			insertSpanStat(spanBo);
			insertSpanEventStat(spanBo);
		} catch (Exception e) {
			logger.warn("Span handle error. Caused:{}. Span:{}", e.getMessage(), tbase, e);
		}
	}

	private void insertSpanStat(SpanBo span) {
		final ServiceType applicationServiceType = getApplicationServiceType(span);
		final ServiceType spanServiceType = registry.findServiceType(span.getServiceType());

		final boolean isError = span.getErrCode() != 0;
		int bugCheck = 0;
		if (span.getParentSpanId() == -1) {
			if (spanServiceType.isQueue()) {
				// FIXME bsh delete updateCaller, updateCallee
				// create virtual queue node
				statisticsHandler.updateCaller(span.getAcceptorHost(), spanServiceType, span.getRemoteAddr(),
						span.getApplicationId(), applicationServiceType, span.getEndPoint(), span.getElapsed(),
						isError);

				statisticsHandler.updateCallee(span.getApplicationId(), applicationServiceType, span.getAcceptorHost(),
						spanServiceType, span.getAgentId(), span.getElapsed(), isError);

				
				// bsh, insert applicationMapData
				statisticsHandler.putApplicationMap(span.getTranscationFullId(), ServiceType.USER.getName(), ServiceType.USER, span.getAgentId(),
						span.getApplicationId(), applicationServiceType, span.getAgentId(), span.getStartTime(),
						span.getElapsed(), isError);

			} else {
				// create virtual user
				statisticsHandler.updateCaller(span.getApplicationId(), ServiceType.USER, span.getAgentId(),
						span.getApplicationId(), applicationServiceType, span.getAgentId(), span.getElapsed(), isError);

				// update the span information of the current node (self)
				statisticsHandler.updateCallee(span.getApplicationId(), applicationServiceType, span.getApplicationId(),
						ServiceType.USER, span.getAgentId(), span.getElapsed(), isError);

				// bsh, insert applicationMapData
				statisticsHandler.putApplicationMap(span.getTranscationFullId(), ServiceType.USER.getName(), ServiceType.USER, span.getAgentId(),
						span.getApplicationId(), applicationServiceType, span.getAgentId(), span.getStartTime(),
						span.getElapsed(), isError);
			}
			bugCheck++;
		}

		// save statistics info only when parentApplicationContext exists
		// when drawing server map based on statistics info, you must know the
		// application name of the previous node.
		if (span.getParentApplicationId() != null) {
			String parentApplicationName = span.getParentApplicationId();
			logger.debug("Received parent application name. {}", parentApplicationName);

			ServiceType parentApplicationType = registry.findServiceType(span.getParentApplicationServiceType());

			// create virtual queue node if current' span's service type is a queue AND :
			// 1. parent node's application service type is not a queue (it may have come
			// from a queue that is traced)
			// 2. current node's application service type is not a queue (current node may
			// be a queue that is traced)
			if (spanServiceType.isQueue()) {
				if (!applicationServiceType.isQueue() && !parentApplicationType.isQueue()) {
					// emulate virtual queue node's accept Span and record it's acceptor host
					hostApplicationMapDao.insert(span.getRemoteAddr(), span.getAcceptorHost(),
							spanServiceType.getCode(), parentApplicationName, parentApplicationType.getCode());
					// emulate virtual queue node's send SpanEvent
					statisticsHandler.updateCaller(span.getAcceptorHost(), spanServiceType, span.getRemoteAddr(),
							span.getApplicationId(), applicationServiceType, span.getEndPoint(), span.getElapsed(),
							isError);

					parentApplicationName = span.getAcceptorHost();
					parentApplicationType = spanServiceType;
				}
			}

			// FIXME bsh delete updateCaller, updateCallee
			statisticsHandler.updateCallee(span.getApplicationId(), applicationServiceType, parentApplicationName,
					parentApplicationType, span.getAgentId(), span.getElapsed(), isError);

			// bsh, insert applicationMapData
			statisticsHandler.putApplicationMap(span.getTranscationFullId(), parentApplicationName, parentApplicationType, span.getAgentId(),
					span.getApplicationId(), applicationServiceType, span.getAgentId(), span.getStartTime(),
					span.getElapsed(), isError);

			bugCheck++;
		}

		// record the response time of the current node (self).
		// blow code may be conflict of idea above callee key.
		// it is odd to record reversely, because of already recording the caller data
		// at previous node.
		// the data may be different due to timeout or network error.

		statisticsHandler.updateResponseTime(span.getApplicationId(), applicationServiceType, span.getAgentId(),
				span.getElapsed(), isError);

		if (bugCheck != 1) {
			logger.warn("ambiguous span found(bug). span:{}", span);
		}
	}

	private void insertSpanEventStat(SpanBo span) {

		final List<SpanEventBo> spanEventList = span.getSpanEventBoList();
		if (CollectionUtils.isEmpty(spanEventList)) {
			return;
		}

		final ServiceType applicationServiceType = getApplicationServiceType(span);

		logger.debug("handle spanEvent size:{}", spanEventList.size());
		// TODO need to batch update later.
		for (SpanEventBo spanEvent : spanEventList) {
			final ServiceType spanEventType = registry.findServiceType(spanEvent.getServiceType());
			if (!spanEventType.isRecordStatistics()) {
				continue;
			}

			// if terminal update statistics
			final int elapsed = spanEvent.getEndElapsed();
			final boolean hasException = spanEvent.hasException();

			// FIXME bsh delete updateCaller, updateCallee
			/*
			 * save information to draw a server map based on statistics
			 */
			// save the information of caller (the spanevent that called span)
			statisticsHandler.updateCaller(span.getApplicationId(), applicationServiceType, span.getAgentId(),
					spanEvent.getDestinationId(), spanEventType, spanEvent.getEndPoint(), elapsed, hasException);

			// save the information of callee (the span that spanevent called)
			statisticsHandler.updateCallee(spanEvent.getDestinationId(), spanEventType, span.getApplicationId(),
					applicationServiceType, span.getEndPoint(), elapsed, hasException);

			// bsh, insert applicationMapData
			statisticsHandler.putApplicationMap(span.getTranscationFullId(), span.getApplicationId(), applicationServiceType, span.getAgentId(),
					spanEvent.getDestinationId(), spanEventType, spanEvent.getEndPoint(), span.getStartTime(), elapsed, hasException);

		}
	}

	private void insertAcceptorHost(SpanBo span) {
		// save host application map
		// acceptor host is set at profiler module only when the span is not the kind of
		// root span
		final String acceptorHost = span.getAcceptorHost();
		if (acceptorHost == null) {
			return;
		}
		final String spanApplicationName = span.getApplicationId();
		final short applicationServiceTypeCode = getApplicationServiceType(span).getCode();

		final String parentApplicationName = span.getParentApplicationId();
		final short parentServiceType = span.getParentApplicationServiceType();

		final ServiceType spanServiceType = registry.findServiceType(span.getServiceType());
		if (spanServiceType.isQueue()) {
			hostApplicationMapDao.insert(span.getEndPoint(), spanApplicationName, applicationServiceTypeCode,
					parentApplicationName, parentServiceType);
		} else {
			hostApplicationMapDao.insert(acceptorHost, spanApplicationName, applicationServiceTypeCode,
					parentApplicationName, parentServiceType);
		}
	}

	private ServiceType getApplicationServiceType(SpanBo span) {
		// Check if applicationServiceType is set. If not, use span's service type.
		final short applicationServiceTypeCode = span.getApplicationServiceType();
		return registry.findServiceType(applicationServiceTypeCode);
	}
}
