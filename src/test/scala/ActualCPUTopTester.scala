import chisel3._
import chisel3.iotesters.PeekPokeTester

class ActualCPUTopTester(dut: CPUTop) extends PeekPokeTester(dut) {

  poke(dut.io.run, 1)

  var loadTest = "b01010000000001000000000000000011".U(32.W) // Loads value 3 into R1
  poke(dut.io.instruction, loadTest)
  step(1)
  expect(dut.io.bSelTest, 1)
  expect(dut.io.testRb, 3)

  loadTest = "b01010000000010000000000000000010".U(32.W) // Loads value 2 into R2
  poke(dut.io.instruction, loadTest)
  step(1)
  expect(dut.io.bSelTest, 2)
  expect(dut.io.testRb, 2)

  val addTest = "b00000000100010000110000000000000".U(32.W) // Add R1 and R2 and places result in R3
  poke(dut.io.instruction, addTest)
  step(1)
  expect(dut.io.aSelTest, 1)
  expect(dut.io.bSelTest, 2)
  expect(dut.io.writeSelTest, 3)
  var readRegister = "b11000001100000000000000000000000".U(32.W) // changes RegisterFile.io.aSel to 3 using NOP
  poke(dut.io.instruction, readRegister)
  step(1)
  expect(dut.io.aSelTest, 3)
  expect(dut.io.testRa, 5)

  val adiTest = "b00010001100100000000000000000111".U(32.W) // Adds 7 to R3 and places into R4
  poke(dut.io.instruction, adiTest)
  step(1)
  expect(dut.io.aSelTest, 3) // R3 = 5
  expect(dut.io.bSelTest, 4)
  expect(dut.io.testRb, 12)

  val subTest = "b00100010000010001010000000000000".U(32.W) // Subtracts R4 from R2 and places into R5
  poke(dut.io.instruction, subTest)
  step(1)
  expect(dut.io.aSelTest, 4) // R4 = 12
  expect(dut.io.bSelTest, 2) // R2 = 2
  expect(dut.io.writeSelTest, 5)
  readRegister = "b11000010100000000000000000000000".U(32.W) // changes RegisterFile.io.aSel to 5 using NOP
  poke(dut.io.instruction, readRegister)
  step(1)
  expect(dut.io.aSelTest, 5)
  expect(dut.io.testRa, 10)

  val sbiTest = "b00110001100110000000000000000011".U(32.W) // Subs 3 to R3 and places into R6
  poke(dut.io.instruction, sbiTest)
  step(1)
  expect(dut.io.aSelTest, 3) // R3 = 5
  expect(dut.io.bSelTest, 6)
  expect(dut.io.testRb, 2)

  val mulTest = "b01000010100011001110000000000000".U(32.W) // Multiplies R5 with R3 and places into R7
  poke(dut.io.instruction, mulTest)
  step(1)
  expect(dut.io.aSelTest, 5) // R5 = 10
  expect(dut.io.bSelTest, 3) // R3 = 5
  expect(dut.io.writeSelTest, 7)
  readRegister = "b11000011100000000000000000000000".U(32.W) // changes RegisterFile.io.aSel to 7 using NOP
  poke(dut.io.instruction, readRegister)
  step(1)
  expect(dut.io.aSelTest, 7)
  expect(dut.io.testRa, 50)

  val sdTest = "b01110000100111000000000000000000".U(32.W) // Places R7 into memory(R1)
  poke(dut.io.instruction, sdTest)
  step(1)
  expect(dut.io.aSelTest, 1) // R1 = 3
  expect(dut.io.bSelTest, 7) // R7 = 50
  expect(dut.io.addressTest, 3)

  val ldTest = "b01100000101000000000000000000000".U(32.W) // Loads memory(R1) into R8
  poke(dut.io.instruction, ldTest)
  step(1)
  expect(dut.io.aSelTest, 1) // R1 = 3
  expect(dut.io.bSelTest, 8)
  expect(dut.io.addressTest, 3)
  expect(dut.io.testRb, 50) // memory(R1) = 50

  val siTest = "b10000000100000000000000000000100".U(32.W) // Places 4 into memory(R1)
  poke(dut.io.instruction, siTest)
  step(1)
  expect(dut.io.aSelTest, 1) // R1 = 3
  expect(dut.io.addressTest, 3)

  poke(dut.io.instruction, ldTest) // Loads memory(R1) into R8
  step(1)
  expect(dut.io.aSelTest, 1) // R1 = 3
  expect(dut.io.bSelTest, 8)
  expect(dut.io.addressTest, 3)
  expect(dut.io.testRb, 4) // memory(R1) = 4



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


