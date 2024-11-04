import chisel3._
import chisel3.iotesters
import chisel3.iotesters.PeekPokeTester

import java.util

class CPUTopTester(dut: CPUTop) extends PeekPokeTester(dut) {
  //Do not run the CPU
  poke(dut.io.run, 0)

  System.out.print("\nLoading the program memory with instructions... ")
  //Uncomment one of the following line depending on the program you want to load to the program memory
  val program = Programs.program3
  //val program = Programs.program2
  for( address <- 0 to program.length-1 ){
    poke(dut.io.testerProgMemEnable, 1)
    poke(dut.io.testerProgMemWriteEnable, 1)
    poke(dut.io.testerProgMemAddress, address)
    poke(dut.io.testerProgMemDataWrite, program(address))

    step(1)
  }
  System.out.println("Done!")

  //Run the simulation of the CPU
  System.out.println("\nRun the simulation of the CPU")
  //Start the CPU
  poke(dut.io.run, 1)
  var running = true
  var maxInstructions = 20
  var instructionsCounter = maxInstructions

  val program3 = Array(
    "b01010000000001000000000000000011".U(32.W),
    "b01010000000010000000000000000010".U(32.W),
    "b01000000100010000110000000000000".U(32.W),
    "b01010000000100000000000000000001".U(32.W),
    "b01110010000011000000000000000000".U(32.W),
    "b11110000000000000000000000000000".U(32.W)
  )
  var i = 0

  while(running) {
    step(1)
    //peek(dut.io.instruction, dut.io.)
    poke(dut.io.instruction, program3(i))
    System.out.println("YO: " + peek(dut.io.instruction))
    step(1)
    //System.out.println("YO2: " + peek(dut.controlUnit.io.opcode))
    System.out.println("YO2: " + peek(dut.io.opOut))
    System.out.println("YO3: " + peek(dut.io.testR0) + " YO4: " + peek(dut.io.testR1))
    System.out.println("YO5: " + peek(dut.io.ALUopTest))
    System.out.println("YO6: " + peek(dut.io.aSelTest) + " YO7: " + peek(dut.io.bSelTest))
    System.out.println("YO8: " + peek(dut.io.writeEnableTest) + " YO9: " + peek(dut.io.writeSelTest) + " YO10: " + peek(dut.io.writeDataTest))

    i = i + 1
    instructionsCounter = instructionsCounter - 1
    /*
    if (peek(dut.io.testR3) != 0) {
      System.out.println("wow")
    } else {
      System.out.print("\rRunning cycle: " + (maxInstructions - instructionsCounter))
    }

     */

    running = peek(dut.io.done) == 0 && instructionsCounter > 0
  }


  poke(dut.io.testerDataMemEnable, 1)
  poke(dut.io.testerDataMemWriteEnable, 0)
  poke(dut.io.testerDataMemAddress, 1)

  poke(dut.io.run, 0)
  System.out.println(" - Done!")
  poke(dut.io.testerProgMemEnable, 0)

  /*
  //Dump the data memory content
  System.out.print("\nDump the data memory content... ")
  val inputImage = new util.ArrayList[Int]
  for( i <- 0 to 399){ //Location of the original image
    poke(dut.io.testerDataMemEnable, 1)
    poke(dut.io.testerDataMemWriteEnable, 0)
    poke(dut.io.testerDataMemAddress, i)
    val data = peek(dut.io.testerDataMemDataRead)
    inputImage.add(data.toInt)
    //System.out.println("a:" + i + " d:" + data )
    step(1)
  }
  val outputImage = new util.ArrayList[Int]
  for( i <- 400 to 799){ //Location of the processed image
    poke(dut.io.testerDataMemEnable, 1)
    poke(dut.io.testerDataMemWriteEnable, 0)
    poke(dut.io.testerDataMemAddress, i)
    val data = peek(dut.io.testerDataMemDataRead)
    outputImage.add(data.toInt)
    //System.out.println("a:" + i + " d:" + data )
    step(1)
  } */

  System.out.println("Done!")
/*
  System.out.print("\r\n")
  System.out.println("Input image from address 0 to 399:")
  //Images.printImage(inputImage)
  System.out.println("Processed image from address 400 to 799:")
  //Images.printImage(outputImage)
*/
  System.out.println("End of simulation")
}

object CPUTopTester {
  def main(args: Array[String]): Unit = {
    println("Testing the full CPU")
    iotesters.Driver.execute(
      Array("--generate-vcd-output", "on",
        "--target-dir", "generated",
        "--top-name", "CPUTop"),
      () => new CPUTop()) {
      c => new CPUTopTester(c)
    }
  }
}


