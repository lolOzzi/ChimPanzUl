import chisel3._
import chisel3.iotesters.PeekPokeTester

class ActualCPUTopTester(dut: CPUTop) extends PeekPokeTester(dut) {

  poke(dut.io.run, 0)
  val program = Array(
    "b01010000000001000000000000000011".U(32.W), // Loads value 3 into R1
    "b11000000000001000000000000000000".U(32.W), // changes RegisterFile.io.bSel to 1 using NOP
    "b01010000000010000000000000000010".U(32.W), // Loads value 2 into R2
    "b11000000000010000000000000000000".U(32.W), // changes RegisterFile.io.bSel to 2 using NOP
    "b00000000100010000110000000000000".U(32.W), // Add R1 and R2 and places result in R3
    "b11000001100000000000000000000000".U(32.W), // changes RegisterFile.io.aSel to 3 using NOP
    "b00010001100100000000000000000111".U(32.W), // Adds 7 to R3 and places into R4
    "b11000000000100000000000000000000".U(32.W), // changes RegisterFile.io.bSel to 4 using NOP
    "b00100010000010001010000000000000".U(32.W), // Subtracts R4 from R2 and places into R5
    "b11000010100000000000000000000000".U(32.W), // changes RegisterFile.io.aSel to 5 using NOP
    "b00110001100110000000000000000011".U(32.W), // Subs 3 to R3 and places into R6
    "b01000010100011001110000000000000".U(32.W), // Multiplies R5 with R3 and places into R7
    "b11000011100000000000000000000000".U(32.W), // changes RegisterFile.io.aSel to 7 using NOP
    "b01110000100111000000000000000000".U(32.W), // Places R7 into memory(R1)
    "b01100000101000000000000000000000".U(32.W), // Loads memory(R1) into R8
    "b10000000100000000000000000000100".U(32.W), // Places 4 into memory(R1)
    "b01100000101000000000000000000000".U(32.W), // Loads memory(R1) into R8
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
  expect(dut.io.writeSelTest, 3)

  step(1)
  expect(dut.io.instructionTest, program(5))
  expect(dut.io.aSelTest, 3)
  expect(dut.io.testRa, 5)

  step(1)
  expect(dut.io.instructionTest, program(6))
  expect(dut.io.aSelTest, 3) // R3 = 5
  expect(dut.io.bSelTest, 4)
  expect(dut.io.testRb, 12)

  step(1)
  expect(dut.io.instructionTest, program(7))
  expect(dut.io.aSelTest, 4) // R4 = 12
  expect(dut.io.bSelTest, 2) // R2 = 2
  expect(dut.io.writeSelTest, 5)
  step(1)
  expect(dut.io.instructionTest, program(8))
  expect(dut.io.aSelTest, 5)
  expect(dut.io.testRa, 10)

  step(1)
  expect(dut.io.instructionTest, program(9))
  expect(dut.io.aSelTest, 3) // R3 = 5
  expect(dut.io.bSelTest, 6)
  expect(dut.io.testRb, 2)

  step(1)
  expect(dut.io.instructionTest, program(10))
  expect(dut.io.aSelTest, 5) // R5 = 10
  expect(dut.io.bSelTest, 3) // R3 = 5
  expect(dut.io.writeSelTest, 7)

  step(1)
  expect(dut.io.instructionTest, program(11))
  expect(dut.io.aSelTest, 7)
  expect(dut.io.testRa, 50)

  step(1)
  expect(dut.io.instructionTest, program(12))
  expect(dut.io.aSelTest, 1) // R1 = 3
  expect(dut.io.bSelTest, 7) // R7 = 50
  expect(dut.io.addressTest, 3)

  step(1)
  expect(dut.io.instructionTest, program(13))
  expect(dut.io.aSelTest, 1) // R1 = 3
  expect(dut.io.bSelTest, 8)
  expect(dut.io.addressTest, 3)
  expect(dut.io.testRb, 50) // memory(R1) = 50

  step(1)
  expect(dut.io.instructionTest, program(14))
  expect(dut.io.aSelTest, 1) // R1 = 3
  expect(dut.io.addressTest, 3)

  step(1)
  expect(dut.io.instructionTest, program(15))
  expect(dut.io.aSelTest, 1) // R1 = 3
  expect(dut.io.bSelTest, 8)
  expect(dut.io.addressTest, 3)
  expect(dut.io.testRb, 4) // memory(R1) = 4

  poke(dut.io.run, 0)
  poke(dut.io.forceInstTest, 0)
}

object ActualCPUTopTester {
  def main(args: Array[String]): Unit = {
    println("Testing the actual full CPU")
    iotesters.Driver.execute(
      Array("--generate-vcd-output", "on",
        "--target-dir", "generated",
        "--top-name", "CPUTop"),
      () => new CPUTop()) {
      c => new ActualCPUTopTester(c)
    }
  }
}


