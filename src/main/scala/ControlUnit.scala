import chisel3._
import chisel3.util._

class ControlUnit extends Module {
  val io = IO(new Bundle {
    val opcode = Input(UInt(6.W))
    val func = Input(UInt(6.W))

    val aluSel = Output(UInt(4.W))
    val regWrite = Output(Bool())
    val memRead = Output(Bool())
    val memWrite = Output(Bool())
    val aluSrc = Output(Bool())
    val branch = Output(Bool())
  })

  io.aluSel := 0.U
  io.regWrite := false.B
  io.memRead := false.B
  io.memWrite := false.B
  io.aluSrc := false.B
  io.branch:= false.B

  switch(io.opcode) {
    is(0.U) {
      switch(io.func) {
        is(1.U) { //ADD
          io.aluSel := 1.U
          io.regWrite := true.B
        }
        is(2.U) { //MULT
          io.aluSel := 2.U
          io.regWrite := true.B
        }
      }
    }
    io.aluSel := io.opcode - 2.U
    is(1.U | 2.U | 3.U) { //ADDI SUBI LI
      io.aluSrc := true.B
      io.regWrite := true.B
    }
    is(4.U) { //LD
      io.regWrite := true.B
      io.memRead := true.B
    }
    is(5.U) { //SD
      io.memWrite := true.B
    }
    is(6.U | 7.U | 8.U | 9.U) { //JEQ JGT JLT
      io.aluSrc := true.B
      io.branch := true.B
    }
  }
}