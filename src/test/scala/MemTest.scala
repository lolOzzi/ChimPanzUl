import chisel3._
import chisel3.iotesters.PeekPokeTester

class MemTest(dut: CPUTop) extends PeekPokeTester(dut) {

  poke(dut.io.run, 0)
  val program = Array(
    "b01010000000001000000000000000011".U(32.W), // Loads value 3 into R1
    "b11000000000001000000000000000000".U(32.W), // changes RegisterFile.io.bSel to 1 using NOP
    "b01010000000010000000000000000010".U(32.W), // Loads value 2 into R2
    "b11000000000010000000000000000000".U(32.W), // changes RegisterFile.io.bSel to 2 using NOP
    "b01110000100010000000000000000000".U(32.W), // Places R2 into memory(R1)
    "b01100000100011000000000000000000".U(32.W), // Loads memory(R1) into R3
    "b11000000000011000000000000000000".U(32.W), // changes RegisterFile.io.bSel to 3 using NOP
    "b11110000000000000000000000000000".U(32.W)  // END
  )

  for( address <- 0 to program.length-1){
    poke(dut.io.testerProgMemEnable, 1)
    poke(dut.io.testerProgMemWriteEnable, 1)
    poke(dut.io.testerProgMemAddress, address)
    poke(dut.io.testerProgMemDataWrite, program(address))
    step(1)
  }
  poke(dut.io.testerProgMemEnable, 0)
  System.out.println("Done Loading!")

  poke(dut.io.run, 1)
  step(1)
  expect(dut.io.instructionTest, program(0))
  expect(dut.io.bSelTest, 1)
  step(1)
  expect(dut.io.instructionTest, program(1))
  expect(dut.io.testRb, 3)
  step(1)
  expect(dut.io.instructionTest, program(2))
  expect(dut.io.bSelTest, 2)
  step(1)
  expect(dut.io.instructionTest, program(3))
  expect(dut.io.testRb, 2)
  step(1)
  expect(dut.io.instructionTest, program(4))
  expect(dut.io.aSelTest, 1)
  expect(dut.io.bSelTest, 2)
  step(1)
  expect(dut.io.instructionTest, program(5))
  expect(dut.io.aSelTest, 1)
  expect(dut.io.bSelTest, 3)
  step(1)
  expect(dut.io.instructionTest, program(6))
  expect(dut.io.bSelTest, 3)
  expect(dut.io.testRb, 2)
  poke(dut.io.run, 0)
}

object MemTest {
  def main(args: Array[String]): Unit = {
    println("Testing the actual full CPU")
    iotesters.Driver.execute(
      Array("--generate-vcd-output", "on",
        "--target-dir", "generated",
        "--top-name", "MemTest"),
      () => new CPUTop()) {
      c => new MemTest(c)
    }
  }
}


