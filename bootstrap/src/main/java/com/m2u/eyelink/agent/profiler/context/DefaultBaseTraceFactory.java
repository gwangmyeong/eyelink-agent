package com.m2u.eyelink.agent.profiler.context;

import com.m2u.eyelink.agent.profiler.context.id.AsyncIdGenerator;
import com.m2u.eyelink.agent.profiler.context.id.AtomicIdGenerator;
import com.m2u.eyelink.agent.profiler.context.id.IdGenerator;
import com.m2u.eyelink.agent.profiler.context.id.TraceIdFactory;
import com.m2u.eyelink.agent.profiler.context.recorder.RecorderFactory;
import com.m2u.eyelink.annotations.InterfaceAudience;
import com.m2u.eyelink.context.AsyncState;
import com.m2u.eyelink.context.AsyncStorage;
import com.m2u.eyelink.context.AsyncTrace;
import com.m2u.eyelink.context.AsyncTraceId;
import com.m2u.eyelink.context.BaseTraceFactory;
import com.m2u.eyelink.context.DisableTrace;
import com.m2u.eyelink.context.ListenableAsyncState;
import com.m2u.eyelink.context.Sampler;
import com.m2u.eyelink.context.Storage;
import com.m2u.eyelink.context.StorageFactory;
import com.m2u.eyelink.context.Trace;
import com.m2u.eyelink.context.TraceId;

public class DefaultBaseTraceFactory implements BaseTraceFactory {

    private final CallStackFactory callStackFactory;

    private final StorageFactory storageFactory;
    private final Sampler sampler;

    private final IdGenerator idGenerator;
    private final AsyncIdGenerator asyncIdGenerator;

    private final TraceIdFactory traceIdFactory;
    private final SpanFactory spanFactory;
    private final RecorderFactory recorderFactory;


    public DefaultBaseTraceFactory(CallStackFactory callStackFactory, StorageFactory storageFactory, Sampler sampler, TraceIdFactory traceIdFactory, IdGenerator idGenerator, AsyncIdGenerator asyncIdGenerator,
                                   SpanFactory spanFactory, RecorderFactory recorderFactory) {
        if (callStackFactory == null) {
            throw new NullPointerException("callStackFactory must not be null");
        }
        if (storageFactory == null) {
            throw new NullPointerException("storageFactory must not be null");
        }
        if (sampler == null) {
            throw new NullPointerException("sampler must not be null");
        }
        if (idGenerator == null) {
            throw new NullPointerException("idGenerator must not be null");
        }
        if (traceIdFactory == null) {
            throw new NullPointerException("traceIdFactory must not be null");
        }
        if (asyncIdGenerator == null) {
            throw new NullPointerException("asyncIdGenerator must not be null");
        }
        if (spanFactory == null) {
            throw new NullPointerException("spanFactory must not be null");
        }
        if (recorderFactory == null) {
            throw new NullPointerException("recorderFactory must not be null");
        }


        this.callStackFactory = callStackFactory;
        this.storageFactory = storageFactory;
        this.sampler = sampler;
        this.traceIdFactory = traceIdFactory;
        this.idGenerator = idGenerator;
        this.asyncIdGenerator = asyncIdGenerator;

        this.spanFactory = spanFactory;
        this.recorderFactory = recorderFactory;
    }


    // continue to trace the request that has been determined to be sampled on previous nodes
    @Override
    public Trace continueTraceObject(final TraceId traceId) {
        // TODO need to modify how to bind a datasender
        // always set true because the decision of sampling has been  made on previous nodes
        // TODO need to consider as a target to sample in case Trace object has a sampling flag (true) marked on previous node.
        final Storage storage = storageFactory.createStorage();
        final long localTransactionId = this.idGenerator.nextContinuedTransactionId();

        final Trace trace = new DefaultTrace(callStackFactory, storage, traceId, localTransactionId, asyncIdGenerator, true, spanFactory, recorderFactory);
        return trace;
    }


    @Override
    public Trace continueTraceObject(Trace trace) {
        return trace;
    }


    @Override
    public Trace newTraceObject() {
        // TODO need to modify how to inject a datasender
        final boolean sampling = sampler.isSampling();
        if (sampling) {
            final Storage storage = storageFactory.createStorage();

            final TraceId traceId = traceIdFactory.newTraceId();
            final long localTransactionId = traceId.getTransactionSequence();

            final Trace trace = new DefaultTrace(callStackFactory, storage, traceId, localTransactionId, asyncIdGenerator, true, spanFactory, recorderFactory);

            return trace;
        } else {
            return newDisableTrace();
        }
    }



    // internal async trace.
    @Override
    public Trace continueAsyncTraceObject(AsyncTraceId traceId, int asyncId, long startTime) {

        final TraceId parentTraceId = traceId.getParentTraceId();
        final Storage storage = storageFactory.createStorage();
        final Storage asyncStorage = new AsyncStorage(storage);
        final Trace trace = new DefaultTrace(callStackFactory, asyncStorage, parentTraceId, AtomicIdGenerator.UNTRACKED_ID, asyncIdGenerator, true,
                spanFactory, recorderFactory);

        final AsyncTrace asyncTrace = new AsyncTrace(trace, asyncId, traceId.nextAsyncSequence(), startTime);

        return asyncTrace;
    }

    // entry point async trace.
    @InterfaceAudience.LimitedPrivate("vert.x")
    @Override
    public Trace continueAsyncTraceObject(final TraceId traceId) {

        final Storage storage = storageFactory.createStorage();
        final long localTransactionId = this.idGenerator.nextContinuedTransactionId();
        final DefaultTrace trace = new DefaultTrace(callStackFactory, storage, traceId, localTransactionId, asyncIdGenerator, true,
                spanFactory, recorderFactory);

        final SpanAsyncStateListener asyncStateListener = new SpanAsyncStateListener(trace.getSpan(), storageFactory.createStorage());
        final ListenableAsyncState stateListener = new ListenableAsyncState(asyncStateListener);
        final AsyncTrace asyncTrace = new AsyncTrace(trace, stateListener);

        return asyncTrace;
    }

    // entry point async trace.
    @InterfaceAudience.LimitedPrivate("vert.x")
    @Override
    public Trace newAsyncTraceObject() {

        final boolean sampling = sampler.isSampling();
        if (sampling) {
            final Storage storage = storageFactory.createStorage();

            final TraceId traceId = traceIdFactory.newTraceId();
            final long localTransactionId = traceId.getTransactionSequence();

            final DefaultTrace trace = new DefaultTrace(callStackFactory, storage, traceId, localTransactionId, asyncIdGenerator, true,
                    spanFactory, recorderFactory);

            final SpanAsyncStateListener asyncStateListener = new SpanAsyncStateListener(trace.getSpan(), storageFactory.createStorage());
            final AsyncState closer = new ListenableAsyncState(asyncStateListener);
            final AsyncTrace asyncTrace = new AsyncTrace(trace, closer);

            return asyncTrace;
        } else {
            return newDisableTrace();
        }
    }

    private Trace newDisableTrace() {
        final long nextDisabledId = this.idGenerator.nextDisabledId();
        final Trace disableTrace = new DisableTrace(nextDisabledId);
        return disableTrace;
    }

    @Override
    public Trace disableSampling() {
        final long nextContinuedDisabledId = this.idGenerator.nextContinuedDisabledId();
        final Trace trace = new DisableTrace(nextContinuedDisabledId);
        return trace;
    }

}