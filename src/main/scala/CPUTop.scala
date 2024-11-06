import chisel3._
import chisel3.util._

class CPUTop extends Module {
  val io = IO(new Bundle {
    val done = Output(Bool ())
    val run = Input(Bool ())
    //This signals are used by the tester for loading and dumping the memory content, do not touch
    val testerDataMemEnable = Input(Bool ())
    val testerDataMemAddress = Input(UInt (16.W))
    val testerDataMemDataRead = Output(UInt (32.W))
    val testerDataMemWriteEnable = Input(Bool ())
    val testerDataMemDataWrite = Input(UInt (32.W))
    //This signals are used by the tester for loading and dumping the memory content, do not touch
    val testerProgMemEnable = Input(Bool ())
    val testerProgMemAddress = Input(UInt (16.W))
    val testerProgMemDataRead = Output(UInt (32.W))
    val testerProgMemWriteEnable = Input(Bool ())
    val testerProgMemDataWrite = Input(UInt (32.W))
  })

  //Creating components
  val programCounter = Module(new ProgramCounter())
  val dataMemory = Module(new DataMemory())
  val programMemory = Module(new ProgramMemory())
  val registerFile = Module(new RegisterFile())
  val controlUnit = Module(new ControlUnit())
  val alu = Module(new Alu())

  //Connecting the modules
  io.done := false.B
  programCounter.io.run := io.run
  programMemory.io.address := programCounter.io.programCounter

  ////////////////////////////////////////////
  //Continue here with your connections
  registerFile.io.writeEnable := controlUnit.io.writeEnable
  val immediate = programMemory.io.instructionRead(15, 0)
  programCounter.io.programCounterJump :=  immediate

  val opcode = programMemory.io.instructionRead(31, 25)
  controlUnit.io.opcode := opcode
  if (opcode != 9.U) {
    registerFile.io.writeSel := programMemory.io.instructionRead(24, 20)
    registerFile.io.aSel := programMemory.io.instructionRead(19, 15)
    if (opcode == 0.U) {
      registerFile.io.bSel := programMemory.io.instructionRead(14, 10)
      controlUnit.io.func := programMemory.io.instructionRead(5, 0)
    }
  }

  when (controlUnit.io.aluSrc) {
    alu.io.b := registerFile.io.b
  } .otherwise {
    alu.io.b := immediate >> 16
  }

  alu.io.sel := controlUnit.io.aluSel

  ////////////////////////////////////////////

  //This signals are used by the tester for loading the program to the program memory, do not touch
  programMemory.io.testerAddress := io.testerProgMemAddress
  io.testerProgMemDataRead := programMemory.io.testerDataRead
  programMemory.io.testerDataWrite := io.testerProgMemDataWrite
  programMemory.io.testerEnable := io.testerProgMemEnable
  programMemory.io.testerWriteEnable := io.testerProgMemWriteEnable
  //This signals are used by the tester for loading and dumping the data memory content, do not touch
  dataMemory.io.testerAddress := io.testerDataMemAddress
  io.testerDataMemDataRead := dataMemory.io.testerDataRead
  dataMemory.io.testerDataWrite := io.testerDataMemDataWrite
  dataMemory.io.testerEnable := io.testerDataMemEnable
  dataMemory.io.testerWriteEnable := io.testerDataMemWriteEnable
}