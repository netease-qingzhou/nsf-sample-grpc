package com.netease.cloud.nsf.demo.grpc.web.service;

import com.netease.cloud.nsf.demo.grpc.web.interceptor.ColorInterceptor;
import com.netease.cloud.nsf.demo.stock.service.EchoGrpc;
import com.netease.cloud.nsf.demo.stock.service.EchoReply;
import com.netease.cloud.nsf.demo.stock.service.EchoRequest;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

/**
 * Created by twogoods on 2019/6/27.
 */
@GrpcService
public class EchoService extends EchoGrpc.EchoImplBase {

    int count = 0;
    @Value("${spring.application.name}")
    private String serverName;

    @Value("${grpc.server.port}")
    private String port;

    private String server;

    @PostConstruct
    private void init() {
        try {
            server = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void echo(EchoRequest req, StreamObserver<EchoReply> responseObserver) {
        String version = System.getProperty("nsf.application.version");
        String res = "[echo] from " + serverName + " version:" + version + " " + server + ":" + port + " color: " + ColorInterceptor.COLOR.get() + "; ";
        EchoReply reply = EchoReply.newBuilder().setMessage(res).build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }

    private void fallback(EchoRequest req, StreamObserver<EchoReply> responseObserver){
        responseObserver.onNext(EchoReply.newBuilder().setMessage("fallback方法执行").build());
        responseObserver.onCompleted();
    }


    @Override
    public void echoStr(EchoRequest req, StreamObserver<EchoReply> responseObserver) {
        try {
            TimeUnit.MILLISECONDS.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (req.getName().equals("error")) {
            responseObserver.onError(new StatusRuntimeException(Status.UNAVAILABLE.withDescription("get error")));
            throw new RuntimeException("get error");
        }
        String version = System.getProperty("nsf.application.version");
        EchoReply reply = EchoReply.newBuilder().setMessage("echo from " + req.getName() + " version: " + version + " " + req.getName()).build();
        String injectError = System.getProperty("nsf.error.enable");
        if ("true".equals(injectError)) {
            count++;
            if (count % 2 != 0) {
                responseObserver.onError(new RuntimeException());
            } else {
                responseObserver.onNext(reply);
            }
            responseObserver.onCompleted();
            return;
        }
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }
}
