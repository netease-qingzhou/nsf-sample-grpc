package com.netease.cloud.nsf.demo.grpc.web.service;

import com.netease.cloud.nsf.demo.grpc.web.interceptor.ColorInterceptor;
import com.netease.cloud.nsf.demo.stock.service.*;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.client.inject.GrpcClient;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by twogoods on 2019/7/4.
 */
@GrpcService
public class ConsumerService extends ConsumerGrpc.ConsumerImplBase {

    @Value("${spring.application.name}")
    private String serverName;

    @Value("${grpc.server.port}")
    private String port;

    private String server;

    ExecutorService executorService = Executors.newFixedThreadPool(2);

    @PostConstruct
    private void init() {
        try {
            server = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @GrpcClient(value = "nsf-demo-stock-provider-grpc")
    private EchoGrpc.EchoBlockingStub echoBlockingStub;

    @GrpcClient(value = "nsf-demo-stock-provider-grpc")
    private EchoGrpc.EchoStub echoStub;

    @Override
    public void echo(ConsumerRequest request, StreamObserver<ConsumerReply> responseObserver) {
        try {
            EchoReply reply = echoBlockingStub.echo(EchoRequest.newBuilder().setName(request.getName()).build());
            String res = "[echo] from " + serverName + " " + server + ":" + port + " color: " + ColorInterceptor.COLOR.get() + " ; " + reply.getMessage();
            responseObserver.onNext(ConsumerReply.newBuilder().setMessage(res).build());
            responseObserver.onCompleted();
        } catch (StatusRuntimeException e) {
            responseObserver.onError(e);
        }
    }



    private void fallback(ConsumerRequest request, StreamObserver<ConsumerReply> responseObserver){
        responseObserver.onNext(ConsumerReply.newBuilder().setMessage("fallback").build());
        responseObserver.onCompleted();
    }

    @Override
    public void echoAsync(ConsumerRequest request, StreamObserver<ConsumerReply> responseObserver) {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                System.out.println("consumer echoAsync in thread :" + Thread.currentThread().getName());
                echoStub.echo(EchoRequest.newBuilder().setName(request.getName()).build(), new StreamObserver<EchoReply>() {
                    @Override
                    public void onNext(EchoReply reply) {
                        Object color = null;
                        try {
                            Class clazz = EntryService.class.getClassLoader().loadClass("com.netease.cloud.nsf.agent.core.flowcolor.GlobalContext");
                            Method method = clazz.getDeclaredMethod("getFlowColor");
                            color = method.invoke(null);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        String res = "[echo] from " + serverName + " " + server + ":" + port + " color: " + color + " ; " + reply.getMessage();
                        responseObserver.onNext(ConsumerReply.newBuilder().setMessage(res).build());
                        responseObserver.onCompleted();
                    }

                    @Override
                    public void onError(Throwable t) {
                        responseObserver.onError(t);
                    }

                    @Override
                    public void onCompleted() {

                    }
                });
            }
        });
    }
}
