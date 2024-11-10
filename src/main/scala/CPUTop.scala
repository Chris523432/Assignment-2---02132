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
  val opcode = programMemory.io.instructionRead(31, 26)
  controlUnit.io.opcode := opcode
  // Everything based on signals from control unit
  when (controlUnit.io.writeEnable && !controlUnit.io.memRead) { //ADD, MULT, ADDI, SUBI, LI
    registerFile.io.writeSel := programMemory.io.instructionRead(25, 21)
    registerFile.io.aSel := programMemory.io.instructionRead(20, 16)
    alu.io.a := registerFile.io.a
    when (controlUnit.io.aluSrc) { //ADDI SUBI, LI
      alu.io.b := programMemory.io.instructionRead(15, 0).pad(32)
    } .otherwise { //ADD, MULT
      controlUnit.io.func := programMemory.io.instructionRead(5, 0)
      registerFile.io.bSel := programMemory.io.instructionRead(15, 11)
      alu.io.b := registerFile.io.b
    }
    registerFile.io.writeData := alu.io.result //Write data from ALU into register
  } .elsewhen(controlUnit.io.memtoReg) { //LD
    registerFile.io.writeSel := programMemory.io.instructionRead(25, 21)
    registerFile.io.aSel := programMemory.io.instructionRead(20, 16)
    alu.io.a := registerFile.io.a
    dataMemory.io.address := alu.io.result(31,16)
    registerFile.io.writeData := dataMemory.io.dataRead
  } .elsewhen(controlUnit.io.memWrite) { //SD
    registerFile.io.aSel := programMemory.io.instructionRead(20, 16)
    alu.io.a := registerFile.io.a
    dataMemory.io.address := alu.io.result(31,16)
    registerFile.io.bSel := programMemory.io.instructionRead(25, 21)
    dataMemory.io.dataWrite := registerFile.io.b
  } .elsewhen(controlUnit.io.branch && alu.io.comparisonResult) { //JEQ, JLT, JGT
    programCounter.io.programCounterJump := programMemory.io.instructionRead(15,0)
    programCounter.io.jump := alu.io.comparisonResult
  }
  // End?

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