import chisel3.iotesters
import chisel3.iotesters.PeekPokeTester

import java.util
import chisel3._
import chisel3.iotesters.PeekPokeTester
import org.scalacheck.Prop.True

class RegisterFileTester(dut: RegisterFile) extends PeekPokeTester(dut) {
  //Test if data is written to register
  poke(dut.io.writeSel, 1.U)
  poke(dut.io.writeData, 10.U)
  poke(dut.io.writeEnable, true)
  poke(dut.io.aSel, 1.U)
  step(1)
  expect(dut.io.a, 10.U)

  //Check that registers that haven't been given value is 0
  poke(dut.io.aSel,0.U)
  step(1)
  expect(dut.io.a,0.U)

  //Check that data can't be written when writeEnable is false.
  poke(dut.io.writeEnable, false)
  poke(dut.io.writeSel,1.U)
  poke(dut.io.writeData,15.U)
  poke(dut.io.aSel, 1.U)
  step(1)
  expect(dut.io.a, 10.U)

  //Check that b also outputs chosen register
  poke(dut.io.bSel, 1.U)
  step(1)
  expect(dut.io.b, 10.U)

}

object RegisterFileTester {
  def main(args: Array[String]): Unit = {
    println("Testing RegisterFile")
    iotesters.Driver.execute(
      Array("--generate-vcd-output", "on",
        "--target-dir", "generated",
        "--top-name", "RegisterFile"),
      () => new RegisterFile())(rf => new RegisterFileTester(rf))
  }
}
