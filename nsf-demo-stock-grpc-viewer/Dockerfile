FROM java:8-jre

ENV TZ=Asia/Shanghai LANG=C.UTF-8 LANGUAGE=C.UTF-8 LC_ALL=C.UTF-8

RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

#RUN apt-get update && apt-get install -y unzip wget nano vim expat net-tools less netcat

ADD target/nsf-demo-stock-*.jar /nsf/nsf-demo.jar

ADD start.sh /nsf/script/start.sh

CMD ["sh","/nsf/script/start.sh"]

