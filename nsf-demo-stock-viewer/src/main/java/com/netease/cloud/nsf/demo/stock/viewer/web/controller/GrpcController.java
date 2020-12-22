package com.netease.cloud.nsf.demo.stock.viewer.web.controller;

import com.netease.cloud.nsf.demo.stock.service.*;
import com.netease.cloud.nsf.demo.stock.viewer.web.entity.HttpResponse;
import com.netease.cloud.nsf.demo.stock.viewer.web.manager.LogManager;
import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.UUID;

@Controller
@RequestMapping("/grpc")
public class GrpcController implements EnvironmentAware {
    public static Environment environment;

    @GrpcClient(value = "nsf-demo-stock-provider-grpc")
    private SimpleGrpc.SimpleBlockingStub simpleStub;

    @GrpcClient(value = "nsf-demo-stock-provider-grpc")
    private EchoGrpc.EchoBlockingStub echoStub;

    @GrpcClient(value = "nsf-demo-stock-entry-grpc")
    private EntryGrpc.EntryBlockingStub entryBlockingStub;


    @GetMapping(value = {"", "/index"})
    public String indexPage() {
        return "grpc";
    }

    @Value("${spring.application.name}")
    private String serviceName;

    @Value("${test:test}")
    private String user;

    @GetMapping("/hello")
    public String sayHello(String str) {
        return String.format("Hello, %s,%s", serviceName, user);
    }

    @GetMapping("/exception")
    @ResponseBody
    public String exception(String msg) {
        if (!StringUtils.isEmpty(msg)) {
            throw new RuntimeException(msg);
        }
        return "no exception";
    }

    @GetMapping(value = "/test", produces = "application/json")
    @ResponseBody
    public String getStockList(@RequestParam(name = "delay", required = false, defaultValue = "0") int delay) {

        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        HelloRequest request = HelloRequest.newBuilder().setName("stock").build();
        HelloReply reply = this.simpleStub.sayHello(request);
        return reply.getMessage();
    }

    @GetMapping(value = "/say", produces = "application/json")
    @ResponseBody
    public String getStockList(@RequestParam(name = "name") String name) {
        HelloRequest request = HelloRequest.newBuilder().setName(name).build();
        try {
            HelloReply reply = this.simpleStub.sayHello(request);
            return reply.getMessage();
        } catch (StatusRuntimeException e) {
            e.printStackTrace();
            return "FAILED with " + e.getStatus().getCode() + " message: " + e.getStatus().getDescription();
        }
    }

    @GetMapping(value = "/add", produces = "application/json")
    @ResponseBody
    public String add(@RequestParam int a, @RequestParam int b) {
        AddRequest request = AddRequest.newBuilder().setA(a).setB(b).build();
        try {
            AddReply reply = this.simpleStub.add(request);
            return String.valueOf(reply.getResult());
        } catch (StatusRuntimeException e) {
            e.printStackTrace();
            return "FAILED with " + e.getStatus().getCode() + " message: " + e.getStatus().getDescription();
        }
    }

    @GetMapping(value = "/echo", produces = "application/json")
    @ResponseBody
    public String echo(@RequestParam(name = "name") String name) {
        EchoRequest request = EchoRequest.newBuilder().setName(name).build();
        try {
            EchoReply reply = this.echoStub.echo(request);
            return reply.getMessage();
        } catch (StatusRuntimeException e) {
            e.printStackTrace();
            return "FAILED with " + e.getStatus().getCode() + " message: " + e.getStatus().getDescription();
        }
    }

    @GetMapping(value = "/echoStr", produces = "application/json")
    @ResponseBody
    public String echoStr(@RequestParam(name = "name") String name) {
        EchoRequest request = EchoRequest.newBuilder().setName(name).build();
        try {
            EchoReply reply = this.echoStub.echoStr(request);
            return reply.getMessage();
        } catch (StatusRuntimeException e) {
            e.printStackTrace();
            return "FAILED with " + e.getStatus().getCode() + " message: " + e.getStatus().getDescription();
        }
    }


    @GetMapping(value = "/echobyecho", produces = "application/json")
    @ResponseBody
    public HttpResponse echobyecho(@RequestParam(name = "time", defaultValue = "10", required = false) int time) {
        try {
            Reply reply = entryBlockingStub.echo(EchoNum.newBuilder().setTime(time).build());
            LogManager.put(UUID.randomUUID().toString(), reply.getMessage());
            return new HttpResponse(reply.getMessage());
        } catch (StatusRuntimeException e) {
            e.printStackTrace();
            return new HttpResponse("FAILED with " + e.getStatus().getCode() + " message: " + e.getStatus().getDescription());
        }
    }

    @GetMapping(value = "/echobyechoAsync", produces = "application/json")
    @ResponseBody
    public HttpResponse echobyechoAsync(@RequestParam(name = "time", defaultValue = "10", required = false) int time) {
        try {
            Reply reply = entryBlockingStub.echoAsync(EchoNum.newBuilder().setTime(time).build());
            LogManager.put(UUID.randomUUID().toString(), reply.getMessage());
            return new HttpResponse(reply.getMessage());
        } catch (StatusRuntimeException e) {
            e.printStackTrace();
            return new HttpResponse("FAILED with " + e.getStatus().getCode() + " message: " + e.getStatus().getDescription());
        }
    }

    @GetMapping(value = "/getConfigs")
    @ResponseBody
    public String getConfig(@RequestParam String key) {
        return environment.getProperty(key);
    }

    @Override
    public void setEnvironment(Environment env) {
        environment = env;
    }
}
