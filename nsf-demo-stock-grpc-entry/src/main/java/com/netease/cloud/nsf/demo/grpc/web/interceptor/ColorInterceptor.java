package com.netease.cloud.nsf.demo.grpc.web.interceptor;

import io.grpc.*;

/**
 * Created by twogoods on 2019/7/3.
 */
public class ColorInterceptor implements ServerInterceptor {

    public static final Context.Key<String> COLOR = Context.key("null");

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> serverCall, Metadata metadata, ServerCallHandler<ReqT, RespT> serverCallHandler) {
        String color = metadata.get(Metadata.Key.of("X-NSF-COLOR", Metadata.ASCII_STRING_MARSHALLER));
        Context context = Context.current().withValue(COLOR, color);
        return Contexts.interceptCall(context, serverCall, metadata, serverCallHandler);
    }
}
