package com.m2u.eyelink.collector.cluster.route;

import org.apache.thrift.TBase;

import com.m2u.eyelink.collector.cluster.ClusterPointLocator;
import com.m2u.eyelink.collector.cluster.TargetClusterPoint;
import com.m2u.eyelink.context.thrift.TCommandTransferResponse;
import com.m2u.eyelink.context.thrift.TRouteResult;
import com.m2u.eyelink.rpc.Future;
import com.m2u.eyelink.rpc.ResponseMessage;

public class DefaultRouteHandler extends AbstractRouteHandler<RequestEvent> {

    private final RouteFilterChain<RequestEvent> requestFilterChain;
    private final RouteFilterChain<ResponseEvent> responseFilterChain;

    public DefaultRouteHandler(ClusterPointLocator<TargetClusterPoint> targetClusterPointLocator,
            RouteFilterChain<RequestEvent> requestFilterChain,
            RouteFilterChain<ResponseEvent> responseFilterChain) {
        super(targetClusterPointLocator);

        this.requestFilterChain = requestFilterChain;
        this.responseFilterChain = responseFilterChain;
    }

    @Override
    public void addRequestFilter(RouteFilter<RequestEvent> filter) {
        this.requestFilterChain.addLast(filter);
    }

    @Override
    public void addResponseFilter(RouteFilter<ResponseEvent> filter) {
        this.responseFilterChain.addLast(filter);
    }

    @Override
    public TCommandTransferResponse onRoute(RequestEvent event) {
        requestFilterChain.doEvent(event);

        TCommandTransferResponse routeResult = onRoute0(event);

        responseFilterChain.doEvent(new ResponseEvent(event, event.getRequestId(), routeResult));

        return routeResult;
    }

    private TCommandTransferResponse onRoute0(RequestEvent event) {
        TBase<?,?> requestObject = event.getRequestObject();
        if (requestObject == null) {
            return createResponse(TRouteResult.EMPTY_REQUEST);
        }

        TargetClusterPoint clusterPoint = findClusterPoint(event.getDeliveryCommand());
        if (clusterPoint == null) {
            return createResponse(TRouteResult.NOT_FOUND);
        }

        if (!clusterPoint.isSupportCommand(requestObject)) {
            return createResponse(TRouteResult.NOT_SUPPORTED_REQUEST);
        }

        Future<ResponseMessage> future = clusterPoint.request(event.getDeliveryCommand().getPayload());
        boolean isCompleted = future.await();
        if (!isCompleted) {
            return createResponse(TRouteResult.TIMEOUT);
        }

        ResponseMessage responseMessage = future.getResult();
        if (responseMessage == null) {
            return createResponse(TRouteResult.EMPTY_RESPONSE);
        }

        byte[] responsePayload = responseMessage.getMessage();
        if (responsePayload == null || responsePayload.length == 0) {
            return createResponse(TRouteResult.EMPTY_RESPONSE, new byte[0]);
        }

        return createResponse(TRouteResult.OK, responsePayload);
    }

    private TCommandTransferResponse createResponse(TRouteResult result) {
        return createResponse(result, new byte[0]);
    }

    private TCommandTransferResponse createResponse(TRouteResult result, byte[] payload) {
        TCommandTransferResponse response = new TCommandTransferResponse();
        response.setRouteResult(result);
        response.setPayload(payload);
        return response;
    }

}
