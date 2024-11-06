import chisel3._
import chisel3.util._

class RegisterFile extends Module {
  val io = IO(new Bundle {
    //Define the module interface here (inputs/outputs)

    // Inputs
    val aSel = Input(UInt(5.W))
    val bSel = Input(UInt(5.W))
    val writeData = Input(UInt(32.W))
    val writeSel = Input(UInt(5.W))
    val writeEnable = Input(Bool())

    //Outputs
    val a = Output(UInt(32.W))
    val b = Output(UInt(32.W))
  })

  //Implement this module here
  val registerFile = Reg(Vec(10, UInt(32.W)))
  io.a := registerFile(io.aSel)
  io.b := registerFile(io.bSel)
  when(io.writeEnable) {
    registerFile(io.writeSel) := io.writeData
  }
}