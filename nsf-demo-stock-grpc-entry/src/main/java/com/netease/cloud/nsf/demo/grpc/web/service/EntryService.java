package com.netease.cloud.nsf.demo.grpc.web.service;

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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

/**
 * Created by twogoods on 2019/7/4.
 */
@GrpcService
public class EntryService extends EntryGrpc.EntryImplBase {

    @Value("${spring.application.name}")
    private String serverName;

    @Value("${grpc.server.port}")
    private String port;

    private String server;


    ForkJoinPool forkJoinPool = new ForkJoinPool();

    @PostConstruct
    private void init() {
        try {
            server = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @GrpcClient(value = "nsf-demo-stock-consumer-grpc")
    private ConsumerGrpc.ConsumerBlockingStub consumerBlockingStub;

    @GrpcClient(value = "nsf-demo-stock-consumer-grpc")
    private ConsumerGrpc.ConsumerStub consumerStub;

    @Override
    public void echo(EchoNum request, StreamObserver<Reply> responseObserver) {
        int num = request.getTime();
        StringBuilder sBuilder = new StringBuilder();
        try {
            for (int i = 0; i < num; i++) {
                sBuilder.append(consumerBlockingStub.echo(ConsumerRequest.newBuilder().setName(i + "").build()).getMessage()).append("\r\n");
            }
            responseObserver.onNext(Reply.newBuilder().setMessage(sBuilder.toString()).build());
            responseObserver.onCompleted();
        } catch (StatusRuntimeException e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void echoAsync(EchoNum request, StreamObserver<Reply> responseObserver) {
        int num = request.getTime();
        StringBuffer sb = new StringBuffer();
        CountDownLatch countDownLatch = new CountDownLatch(num);
        final List<Throwable> exs = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            forkJoinPool.submit(new RecursiveAction() {
                @Override
                protected void compute() {
                    try {
                        Class clazz = EntryService.class.getClassLoader().loadClass("com.netease.cloud.nsf.agent.core.flowcolor.GlobalContext");
                        Method method = clazz.getDeclaredMethod("getFlowColor");
                        System.out.println("entry echo async color is " + method.invoke(null));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    consumerStub.echoAsync(ConsumerRequest.newBuilder().setName("async").build(), new StreamObserver<ConsumerReply>() {
                        @Override
                        public void onNext(ConsumerReply value) {
                            sb.append(value.getMessage()).append("\r\n");
                        }

                        @Override
                        public void onError(Throwable t) {
                            t.printStackTrace();
                            exs.add(t);
                            countDownLatch.countDown();
                        }

                        @Override
                        public void onCompleted() {
                            countDownLatch.countDown();
                        }
                    });
                }
            });
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (exs.isEmpty()) {
            responseObserver.onNext(Reply.newBuilder().setMessage(sb.toString()).build());
            responseObserver.onCompleted();
        } else {
            responseObserver.onError(exs.get(0));
        }
    }

    private void fallback(EchoNum request, StreamObserver<Reply> responseObserver){
        responseObserver.onNext(Reply.newBuilder().setMessage("fallback方法执行").build());
        responseObserver.onCompleted();
    }
}
