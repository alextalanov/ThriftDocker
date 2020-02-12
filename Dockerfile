FROM dockeralexandrtalan/sbt

ARG HOME=/root/project
RUN mkdir $HOME

RUN apt-get update && apt-get install -y libboost-dev libboost-test-dev libboost-program-options-dev libevent-dev automake libtool flex bison pkg-config g++ libssl-dev

RUN wget http://apache.cp.if.ua/thrift/0.13.0/thrift-0.13.0.tar.gz
RUN tar -xvzf thrift-0.13.0.tar.gz
RUN rm -f thrift-0.13.0.tar.gz

WORKDIR thrift-0.13.0

RUN ./configure
RUN make 
RUN make install

RUN thrift -version

WORKDIR $HOME

ENTRYPOINT sbt
