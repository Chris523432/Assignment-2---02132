import chisel3.iotesters
import chisel3.iotesters.PeekPokeTester

import java.util
import chisel3._
import chisel3.iotesters.PeekPokeTester
import org.scalacheck.Prop.True

class RegisterFileTester(dut: RegisterFile) extends PeekPokeTester(dut) {
  poke(dut.io.writeSel, 1.U)
  poke(dut.io.writeData, 10.U)
  poke(dut.io.writeEnable, true)

  poke(dut.io.aSel, 1.U)
  step(1)
  expect(dut.io.a, 10.U)
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
