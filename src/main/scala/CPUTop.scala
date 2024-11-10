import chisel3._
import chisel3.util._

class CPUTop extends Module {
  val io = IO(new Bundle {
    val run = Input(Bool())
    val done = Output(Bool())
    //This signals are used by the tester for loading and dumping the memory content, do not touch
    val testerDataMemEnable = Input(Bool())
    val testerDataMemAddress = Input(UInt(16.W))
    val testerDataMemDataRead = Output(UInt(32.W))
    val testerDataMemWriteEnable = Input(Bool())
    val testerDataMemDataWrite = Input(UInt(32.W))
    //This signals are used by the tester for loading and dumping the memory content, do not touch
    val testerProgMemEnable = Input(Bool())
    val testerProgMemAddress = Input(UInt(16.W))
    val testerProgMemDataRead = Output(UInt(32.W))
    val testerProgMemWriteEnable = Input(Bool())
    val testerProgMemDataWrite = Input(UInt(32.W))
  })

  //Creating components
  val programCounter = Module(new ProgramCounter())
  val dataMemory = Module(new DataMemory())
  val programMemory = Module(new ProgramMemory())
  val registerFile = Module(new RegisterFile())
  val controlUnit = Module(new ControlUnit())
  val alu = Module(new ALU())

  //Connecting the modules
  programCounter.io.run := io.run
  io.done := controlUnit.io.stop
  programCounter.io.stop := controlUnit.io.stop
  programMemory.io.address := programCounter.io.programCounter

  ////////////////////////////////////////////
  //Defining "Wires"
  controlUnit.io.func := programMemory.io.instructionRead(5, 0)
  controlUnit.io.opcode := programMemory.io.instructionRead(31, 26)
  registerFile.io.aSel := programMemory.io.instructionRead(20, 16)
  registerFile.io.bSel := Mux(controlUnit.io.memWrite, programMemory.io.instructionRead(25, 21), programMemory.io.instructionRead(15, 11))
  registerFile.io.writeSel := programMemory.io.instructionRead(25, 21)
  registerFile.io.writeEnable := controlUnit.io.writeEnable
  dataMemory.io.writeEnable := controlUnit.io.memWrite
  alu.io.a := registerFile.io.a
  alu.io.b := Mux(controlUnit.io.aluSrc, programMemory.io.instructionRead(15, 0).pad(32), registerFile.io.b)
  alu.io.sel := controlUnit.io.aluSel
  dataMemory.io.address := alu.io.result(15, 0) //eeh what the spruce
  dataMemory.io.dataWrite := registerFile.io.b
  registerFile.io.writeData := Mux(controlUnit.io.memtoReg, dataMemory.io.dataRead, alu.io.result)
  programCounter.io.programCounterJump := programMemory.io.instructionRead(15, 0)
  programCounter.io.jump := Mux(controlUnit.io.branch && alu.io.comparisonResult, true.B, false.B)

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