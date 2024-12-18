import chisel3._
import chisel3.util._

class ControlUnit extends Module {
  val io = IO(new Bundle {
    val opcode = Input(UInt(6.W))
    val func = Input(UInt(6.W))

    val aluSel = Output(UInt(4.W)) // Choose which operation to be used.
    val writeEnable = Output(Bool()) //RegWrite
    val memWrite = Output(Bool()) // Write to memory
    val aluSrc = Output(Bool()) // Mux for immediate or registervalue
    val memtoReg = Output(Bool()) // Mux for result from ALU or read Memory
    val branch = Output(Bool()) // For Jumping
    val stop = Output(Bool())
  })

  io.aluSel := 0.U
  io.writeEnable := false.B
  io.memWrite := false.B
  io.aluSrc := false.B
  io.branch:= false.B
  io.memtoReg := false.B
  io.stop := false.B

  switch(io.opcode) {
    is(0.U) {
      switch(io.func) {
        is(1.U) { //ADD
          io.aluSel := 1.U
          io.writeEnable := true.B
        }
        is(2.U) { //MULT
          io.aluSel := 2.U
          io.writeEnable := true.B
        }
      }
    }
    is(1.U) { //ADDI
      io.aluSel := 3.U
      io.aluSrc := true.B
      io.writeEnable := true.B
    }
    is(2.U) { //SUBI
      io.aluSel := 4.U
      io.aluSrc := true.B
      io.writeEnable := true.B
    }
    is(3.U) { //LI
      io.aluSel := 5.U
      io.aluSrc := true.B
      io.writeEnable := true.B
    }
    is(4.U) { //LD
      io.aluSel := 6.U
      io.writeEnable := true.B
      io.memtoReg := true.B
    }
    is(5.U) { //SD
      io.aluSel := 7.U
      io.memWrite := true.B
    }
    is(6.U) { //JEQ
      io.aluSel := 8.U
      io.branch := true.B
    }
    is(7.U) { //JLT
      io.aluSel := 9.U
      io.branch := true.B
    }
    is(8.U) { //JGT
      io.aluSel := 10.U
      io.branch := true.B
    }
    is(9.U) { //JR
      io.aluSel := 11.U
      io.branch := true.B
    }
    is(63.U) {
      io.stop := true.B
    }
  }
}