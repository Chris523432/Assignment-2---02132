import chisel3._
import chisel3.util._

class Alu extends Module {
  val io = IO(new Bundle {
    val sel = Input(UInt(4.W))
    val a = Input(UInt(32.W)) //operand 1
    val b = Input(UInt(32.W)) //operand 2
    val result = Output(UInt(32.W))
    val comparisonResult = Output(Bool())
  })

  io.result := 0.U
  io.comparisonResult := false.B

  switch(io.sel) {
    is(1.U) {io.result := io.a + io.b} //Add
    is(2.U) {io.result := io.a * io.b}
    is(3.U) {io.result := io.a + io.b} //Add immediate
    is(4.U) {io.result := io.a - io.b}
    is(5.U) {io.result := io.a} //LI
    is(6.U) {io.result := io.a} //LD
    is(7.U) {io.result := io.a} //SD
    is(8.U) {io.comparisonResult := io.a === io.b}
    is(9.U) {io.comparisonResult := io.a > io.b}
    is(10.U) {io.comparisonResult := io.a < io.b}
    is(11.U) {io.comparisonResult := true.B}
  }
}