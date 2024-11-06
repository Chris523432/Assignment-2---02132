import chisel3._
import chisel3.util._

class ControlUnit extends Module {
  val io = IO(new Bundle {
    val opcode = Input(UInt(6.W))
    val func = Input(UInt(6.W))

    val aluSel = Output(UInt(4.W))
    val writeEnable = Output(Bool())
    val memRead = Output(Bool())
    val memWrite = Output(Bool())
    val aluSrc = Output(Bool())
    val branch = Output(Bool())
  })

  io.aluSel := 0.U
  io.writeEnable := false.B
  io.memRead := false.B
  io.memWrite := false.B
  io.aluSrc := false.B
  io.branch:= false.B

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
      io.memRead := true.B
    }
    is(5.U) { //SD
      io.aluSel := 7.U
      io.memWrite := true.B
    }
    is(6.U) { //JEQ
      io.aluSel := 8.U
      io.aluSrc := true.B
      io.branch := true.B
    }
    is(7.U) { //JLT
      io.aluSel := 9.U
      io.aluSrc := true.B
      io.branch := true.B
    }
    is(8.U) { //JGT
      io.aluSel := 10.U
      io.aluSrc := true.B
      io.branch := true.B
    }
    is{9.U} { //JR
      io.aluSel := 11.U
      io.aluSrc := true.B
      io.branch := true.B
    }
  }
}