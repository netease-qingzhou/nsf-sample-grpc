package com.netease.cloud.nsf.demo.grpc.web.service;

import com.netease.cloud.nsf.demo.stock.service.*;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

@GrpcService
public class GrpcHelloService extends SimpleGrpc.SimpleImplBase {

    int count = 0;

    @Override
    public void sayHello(HelloRequest req, StreamObserver<HelloReply> responseObserver) {
        count++;
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
        InetAddress a = null;
        String address;
        try {
            a = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        if (a == null) {
            address = "127.0.0.1";
        } else {
            address = a.getHostAddress();
        }
        HelloReply reply = HelloReply.newBuilder().setMessage("reply" + version + " ==> " + req.getName() + "," + count + "," + address).build();
        String injectError = System.getProperty("nsf.error.enable");
        if ("true".equals(injectError)) {
            if (count % 5 != 0) {
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

    private void fallback(HelloRequest req, StreamObserver<HelloReply> responseObserver){
        responseObserver.onNext(HelloReply.newBuilder().setMessage("fallback方法执行").build());
        responseObserver.onCompleted();
    }

    @Override
    public void add(AddRequest request, StreamObserver<AddReply> responseObserver) {
        int res = request.getA() + request.getB();
        AddReply reply = AddReply.newBuilder().setResult(res).build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }
}
