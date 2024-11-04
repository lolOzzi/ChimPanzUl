import chisel3._
import chisel3.iotesters.PeekPokeTester

class RegisterFileTester(dut: RegisterFile) extends PeekPokeTester(dut) {
  /*
  poke(dut.io.aSel, 0)
  poke(dut.io.bSel, 31)
  poke(dut.io.writeData, "hffffffff".U(32.W))
  poke(dut.io.writeSel, 3)
  poke(dut.io.writeEnable, 0)
  step(1)

  expect(dut.io.a, 0)
  expect(dut.io.b, 0)

  poke(dut.io.aSel, 0)
  poke(dut.io.bSel, 3)
  poke(dut.io.writeData, 2)
  poke(dut.io.writeSel, 0)
  poke(dut.io.writeEnable, 1)
  step(1)

  expect(dut.io.a, 2)
  expect(dut.io.b, 0)
   */
  val something = "b01010000000001000000000000000011".U(32.W)
  var some2 = 0.U(4.W)
  some2 = something(31,28)
  //System.out.println("\n yo " + something(28, 31))
  System.out.println("\n yo " + something.getWidth)
  System.out.println("\n yo2 " + something.litValue())
  System.out.println("\n yo3 " + some2)
  var idek = Mux(true.B, 1.U(1.W), 0.U(1.W))
  System.out.println("HAHA" + idek.litValue())

}

object RegisterFileTester {
  def main(args: Array[String]): Unit = {
    println("Testing Register File")
    iotesters.Driver.execute(
      Array("--generate-vcd-output", "on",
        "--target-dir", "generated",
        "--top-name", "RegisterFile"),
      () => new RegisterFile()) {
      c => new RegisterFileTester(c)
    }
  }
}