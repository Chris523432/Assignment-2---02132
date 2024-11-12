import chisel3.iotesters
import chisel3.iotesters.PeekPokeTester
import java.util
import chisel3._
import chisel3.iotesters.PeekPokeTester

class ControlUnitTester(dut: ControlUnit) extends PeekPokeTester(dut) {

  //All values are initialized as false. So if they're not written in the tests
  //They are assumed to be false

  //ADD
  poke(dut.io.opcode, 0.U)
  poke(dut.io.func, 1.U)
  step(1)
  expect(dut.io.aluSel, 1.U)
  expect(dut.io.writeEnable, true.B)

  //MULT
  poke(dut.io.opcode, 0.U)
  poke(dut.io.func, 2.U)
  step(1)
  expect(dut.io.aluSel, 2.U)
  expect(dut.io.writeEnable, true.B)

  //ADDI
  poke(dut.io.opcode, 1.U)
  poke(dut.io.func, 0.U)
  step(1)
  expect(dut.io.aluSel, 3.U)
  expect(dut.io.aluSrc, true.B)
  expect(dut.io.writeEnable, true.B)

  //SUBI
  poke(dut.io.opcode, 2.U)
  poke(dut.io.func, 0.U)
  step(1)
  expect(dut.io.aluSel, 4.U)
  expect(dut.io.aluSrc, true.B)
  expect(dut.io.writeEnable, true.B)

  //LI
  poke(dut.io.opcode, 3.U)
  poke(dut.io.func, 0.U)
  step(1)
  expect(dut.io.aluSel, 5.U)
  expect(dut.io.aluSrc, true.B)
  expect(dut.io.writeEnable, true.B)

  //LD
  poke(dut.io.opcode, 4.U)
  poke(dut.io.func, 0.U)
  step(1)
  expect(dut.io.aluSel, 6.U)
  expect(dut.io.writeEnable, true.B)
  expect(dut.io.memRead, true.B)
  expect(dut.io.memtoReg, true.B)

  //SD
  poke(dut.io.opcode, 5.U)
  poke(dut.io.func, 0.U)
  step(1)
  expect(dut.io.aluSel, 7.U)
  expect(dut.io.memWrite, true.B)

  //JEQ
  poke(dut.io.opcode, 6.U)
  poke(dut.io.func, 0.U)
  step(1)
  expect(dut.io.aluSel, 8.U)
  expect(dut.io.branch, true.B)

  //JLT
  poke(dut.io.opcode, 7.U)
  poke(dut.io.func, 0.U)
  step(1)
  expect(dut.io.aluSel, 9.U)
  expect(dut.io.branch, true.B)

  //JGT
  poke(dut.io.opcode, 8.U)
  poke(dut.io.func, 0.U)
  step(1)
  expect(dut.io.aluSel, 10.U)
  expect(dut.io.branch, true.B)

  //JR
  poke(dut.io.opcode, 9.U)
  poke(dut.io.func, 0.U)
  step(1)
  expect(dut.io.aluSel, 11.U)
  expect(dut.io.branch, true.B)

  //END
  poke(dut.io.opcode, 63.U)
  poke(dut.io.func, 0.U)
  step(1)
  expect(dut.io.stop, true.B)
}

object ControlUnitTester {
  def main(args: Array[String]): Unit = {
    println("Testing ControlUnit")
    iotesters.Driver.execute(
      Array("--generate-vcd-output", "on",
        "--target-dir", "generated",
        "--top-name", "ControlUnit"),
      () => new ControlUnit())(controlUnit => new ControlUnitTester(controlUnit))
  }
}
