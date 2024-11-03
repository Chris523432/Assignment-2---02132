import chisel3.iotesters
import chisel3.iotesters.PeekPokeTester
import java.util
import chisel3._
import chisel3.iotesters.PeekPokeTester

class AluTester(dut: Alu) extends PeekPokeTester(dut) {
  //ADD
  poke(dut.io.a, 3.U)
  poke(dut.io.b, 5.U)
  poke(dut.io.sel, 1.U)
  step(1)
  expect(dut.io.result, 8.U)

  //MULT
  poke(dut.io.a, 3.U)
  poke(dut.io.b, 5.U)
  poke(dut.io.sel, 2.U)
  step(1)
  expect(dut.io.result, 15.U)

  //ADDI
  poke(dut.io.a, 3.U)
  poke(dut.io.b, 5.U)
  poke(dut.io.sel, 3.U)
  step(1)
  expect(dut.io.result, 8.U)

  //SUBI
  poke(dut.io.a, 3.U)
  poke(dut.io.b, 1.U)
  poke(dut.io.sel, 4.U)
  step(1)
  expect(dut.io.result, 2.U)

  //LI, LD, SD are the same so they should all act the same
  poke(dut.io.a, 5.U)
  poke(dut.io.sel, 5.U)
  step(1)
  expect(dut.io.result, 5.U)

  //JEQ
  poke(dut.io.a, 5.U)
  poke(dut.io.b, 5.U)
  poke(dut.io.sel, 8.U)
  step(1)
  expect(dut.io.comparisonResult, true)
  poke(dut.io.a, 4.U)
  poke(dut.io.b, 5.U)
  poke(dut.io.sel, 8.U)
  step(1)
  expect(dut.io.comparisonResult, false)

  //JGT
  poke(dut.io.a, 6.U)
  poke(dut.io.b, 5.U)
  poke(dut.io.sel, 9.U)
  step(1)
  expect(dut.io.comparisonResult, true)

  //JLT
  poke(dut.io.a, 6.U)
  poke(dut.io.b, 5.U)
  poke(dut.io.sel, 10.U)
  step(1)
  expect(dut.io.comparisonResult, false)
}

object AluTester {
  def main(args: Array[String]): Unit = {
    println("Testing ALU")
    iotesters.Driver.execute(
      Array("--generate-vcd-output", "on",
        "--target-dir", "generated",
        "--top-name", "Alu"),
      () => new Alu())(alu => new AluTester(alu))
  }
}
