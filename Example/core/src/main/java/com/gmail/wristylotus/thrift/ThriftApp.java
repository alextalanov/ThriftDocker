package com.gmail.wristylotus.thrift;

import com.gmail.wristylotus.thrift.model.MultiplicationService;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.*;

public class ThriftApp {

    public static class MultiplicationHandler implements MultiplicationService.Iface {
        @Override
        public int multiply(int x, int y) {
            System.out.println(String.format("x=%s, y=%s", x, y));
            return x * y;
        }
    }

    public static void main(String[] args) throws TException {

        //Server
        final MultiplicationHandler handler = new MultiplicationHandler();
        final MultiplicationService.Processor processor = new MultiplicationService.Processor(handler);
        TServerTransport transport = new TServerSocket(9090);

        new Thread(() -> {
            TServer server = new TSimpleServer(new TServer.Args(transport).processor(processor));
            while (true){
                server.serve();
            }
        }).start();

        //Client
        TTransport socket = new TSocket("127.0.0.1", 9090);
        socket.open();
        final TBinaryProtocol binaryProtocol = new TBinaryProtocol(socket);
        final MultiplicationService.Client client = new MultiplicationService.Client(binaryProtocol);
        int res = client.multiply(3, 4);
        System.out.println(String.format("Result: %s", res));
        socket.close();
    }

}
