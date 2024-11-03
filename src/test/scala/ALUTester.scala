import chisel3.iotesters
import chisel3.iotesters.PeekPokeTester
import chisel3._
import chisel3.util._

class ALUTester (dut: ALU ) extends PeekPokeTester (dut) {

  for (i <- 0 to 4) {
    poke(dut.io.x, 3.U)
    poke(dut.io.y, 2.U)
    poke(dut.io.sel, i.U)
    step (1)
    i match {
      case 0 => expect(dut.io.res, 3+2)
      case 1 => expect(dut.io.res, 3-2)
      case 2 => expect(dut.io.res, 3*2)
      case 3 => expect(dut.io.comp, 3==2)
      case 4 => expect(dut.io.comp, 3==0)
    }
  }

  //Extra comparison checks
  poke(dut.io.x, 2.U)
  poke(dut.io.y, 2.U)
  poke(dut.io.sel, 3.U)
  expect(dut.io.comp, 2==2)

  poke(dut.io.x, 0.U)
  poke(dut.io.sel, 4.U)
  expect(dut.io.comp, 0==0)

}

object ALUTester {
  def main(args: Array[String]): Unit = {
    println("Running the ALU tester")
    iotesters.Driver.execute(
      Array("--generate-vcd-output", "on",
        "--target-dir", "generated",
        "--top-name", "ALU"),
      () => new ALU()) {
      c => new ALUTester(c)
    }
  }
}