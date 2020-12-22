提供了四个服务 ：
nsf-demo-stock-grpc-viewer-grpc
nsf-demo-stock-grpc-entry
nsf-demo-stock-grpc-consumer
nsf-demo-stock-grpc-provider

调用关系为 viewer -> entry -> consumer -> provider



启动四个项目，viewer工程提供了页面，访问http://localhost:9005/grpc 即可

注：agent配置文件里的服务名需配置为上述四个服务名才能正常调用，若想自定义服务名需修改

GrpcController.java 

```
    //改为自己需要的服务名
    @GrpcClient(value = "nsf-demo-stock-grpc-entry")
    private EntryGrpc.EntryBlockingStub entryBlockingStub;

```

application.yml

```
grpc:
  client:
    nsf-demo-stock-grpc-entry: //修改服务名
      enableKeepAlive: false
      keepAliveWithoutCalls: false
      negotiationType: plaintext

```

viewer、entry、consumer都是消费者，几个模块都要同步修改