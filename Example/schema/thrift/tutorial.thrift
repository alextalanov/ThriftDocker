namespace java com.gmail.wristylotus.thrift.model
namespace py com.gmail.wristylotus.thrift.model

typedef i32 int

service MultiplicationService {
   int multiply(1:int x, 2:int y),
}