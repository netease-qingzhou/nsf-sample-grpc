package com.netease.cloud.nsf.demo.grpc.web.service;

import io.grpc.Status;
import io.grpc.examples.helloworld.*;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

/**
 * 实现 定义一个实现服务接口的类
 */
@GrpcService
public class GatewayGrpcService extends GreeterGrpc.GreeterImplBase {
    @Override
    public void sayHello(HelloRequest req, StreamObserver<HelloReply> responseObserver) {
        System.out.println("接收到的serviceName:" + req.getName() + " ===== " + req.getSub().getAge());
        HelloReply reply = HelloReply.newBuilder().setMessage(("Hello: " + req.getName())).setAge(1111).setSex("male").build();
        /**
         * wdc
         * 返回应答消息
         */
        responseObserver.onNext(reply);
        System.out.println("开始写回:" + req.getName());

        responseObserver.onCompleted();
        System.out.println("写回完毕.");
    }

    @Override
    // 单项rpc
    public void getUsernameById(Id request, StreamObserver<Username> responseObserver) {
        try {
            System.out.println("from client getUsernameById:" + request.getId());
            //发送的数据
            responseObserver.onNext(Username.newBuilder().setUsername("allen").build());
            // Exception需要在onComplete调用之前
//            throw new Exception("exception");
            //onComplete表示发送完成
            responseObserver.onCompleted();
        } catch (Exception ex) {
            // onError给客户端发送error消息，需要在catche里面，即使没有出错
            responseObserver.onError(Status.INTERNAL.withCause(ex).withDescription("a exception happends!").asRuntimeException());
        }
    }

    @java.lang.Override
    public void getIdByUserName(io.grpc.examples.helloworld.Username request,
                                io.grpc.stub.StreamObserver<Id> responseObserver) {
        Id id = null;
        if ("A".equals(request.getUsername())) {
            id = Id.newBuilder().setId(1).build();
        } else {
            id = Id.newBuilder().setId(2).build();
        }
        responseObserver.onNext(id);
        responseObserver.onCompleted();
    }

    @java.lang.Override
    public void deleteUsernameById(io.grpc.examples.helloworld.Id request,
                                   io.grpc.stub.StreamObserver<io.grpc.examples.helloworld.Username> responseObserver) {
        Username username = null;
        if (1 == request.getId()) {
            username = Username.newBuilder().setUsername("A").build();
        } else {
            username = Username.newBuilder().setUsername("B").build();
        }

        responseObserver.onNext(username);
        responseObserver.onCompleted();
    }

    @java.lang.Override
    public void updateUsernameById(io.grpc.examples.helloworld.Id request,
                                   io.grpc.stub.StreamObserver<io.grpc.examples.helloworld.Username> responseObserver) {
        Username username = null;
        if (1 == request.getId()) {
            username = Username.newBuilder().setUsername("B").build();
        } else {
            username = Username.newBuilder().setUsername("A").build();
        }
        responseObserver.onNext(username);
        responseObserver.onCompleted();
    }
}

