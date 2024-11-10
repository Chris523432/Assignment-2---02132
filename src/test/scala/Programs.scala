import chisel3._
import chisel3.util._
import chisel3.iotesters
import chisel3.iotesters.PeekPokeTester
import java.util

object Programs{
  val program1 = Array(
    "b00001100001000000000000000000000".U(32.W),
    "b00001100010000000000000000000000".U(32.W),
    "b11111100000000000000000000000000".U(32.W),
  )

  val program2 = Array(
  )


}