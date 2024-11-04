import chisel3._
import chisel3.experimental._
import chisel3.util._

class CPUTop extends Module {
  val io = IO(new Bundle {
    val done = Output(Bool ())
    val run = Input(Bool ())
    //This signals are used by the tester for loading and dumping the memory content, do not touch
    val testerDataMemEnable = Input(Bool ())
    val testerDataMemAddress = Input(UInt (16.W))
    val testerDataMemDataRead = Output(UInt (32.W))
    val testerDataMemWriteEnable = Input(Bool ())
    val testerDataMemDataWrite = Input(UInt (32.W))
    //This signals are used by the tester for loading and dumping the memory content, do not touch
    val testerProgMemEnable = Input(Bool ())
    val testerProgMemAddress = Input(UInt (16.W))
    val testerProgMemDataRead = Output(UInt (32.W))
    val testerProgMemWriteEnable = Input(Bool ())
    val testerProgMemDataWrite = Input(UInt (32.W))

    val testR0 = Output(UInt (32.W))
    val testR1 = Output(UInt (32.W))
    val ALUopTest = Output(UInt(3.W))

    val aSelTest = Output(UInt(5.W))
    val bSelTest = Output(UInt(5.W))

    val writeEnableTest = Output(Bool())
    val writeSelTest = Output(UInt(4.W))
    val writeDataTest = Output(UInt(32.W))

    val instruction = Input(UInt (32.W))
    val opOut = Output(UInt (32.W))
    //val inst = Input(32.W)
  })

  //Creating components
  val programCounter = Module(new ProgramCounter())
  val dataMemory = Module(new DataMemory())
  val programMemory = Module(new ProgramMemory())
  val registerFile = Module(new RegisterFile())
  val controlUnit = Module(new ControlUnit())
  val alu = Module(new ALU())


  //Connecting the modules
  programCounter.io.run := io.run
  programCounter.io.stop := controlUnit.io.stop
  io.done := controlUnit.io.stop
  programMemory.io.address := programCounter.io.programCounter
  //val inst = programMemory.io.instructionRead
  controlUnit.io.opcode := io.instruction(31, 28)
  io.opOut := controlUnit.io.opcode
  registerFile.io.aSel := io.instruction(27, 23)
  registerFile.io.bSel := io.instruction(22, 18)
  registerFile.io.writeSel := Mux(controlUnit.io.RegDst, io.instruction(22, 18), io.instruction(18, 13))
  programCounter.io.jump := controlUnit.io.Jump & alu.io.comp
  programCounter.io.programCounterJump := io.instruction(17, 0)
  alu.io.x := registerFile.io.a
  alu.io.y := Mux(controlUnit.io.ALUsrc,registerFile.io.b, io.instruction(17, 0))
  alu.io.sel := controlUnit.io.ALUop
  dataMemory.io.writeEnable := controlUnit.io.MemWrite
  dataMemory.io.address := alu.io.res
  dataMemory.io.dataWrite := Mux(controlUnit.io.StoreImd, io.instruction(17,0).pad(32), registerFile.io.b)

  registerFile.io.writeData := Mux(controlUnit.io.MemtoReg, dataMemory.io.dataRead, alu.io.res)
  registerFile.io.writeEnable := controlUnit.io.writeEnable

  io.testR0 := registerFile.io.r0
  io.testR1 := registerFile.io.r1
  io.ALUopTest := controlUnit.io.ALUop

  io.aSelTest := registerFile.io.aSel
  io.bSelTest := registerFile.io.bSel

  io.writeEnableTest := registerFile.io.writeEnable
  io.writeSelTest := registerFile.io.writeSel
  io.writeDataTest := registerFile.io.writeData


  //This signals are used by the tester for loading the program to the program memory, do not touch
  programMemory.io.testerAddress := io.testerProgMemAddress
  io.testerProgMemDataRead := programMemory.io.testerDataRead
  programMemory.io.testerDataWrite := io.testerProgMemDataWrite
  programMemory.io.testerEnable := io.testerProgMemEnable
  programMemory.io.testerWriteEnable := io.testerProgMemWriteEnable
  //This signals are used by the tester for loading and dumping the data memory content, do not touch
  dataMemory.io.testerAddress := io.testerDataMemAddress
  io.testerDataMemDataRead := dataMemory.io.testerDataRead
  dataMemory.io.testerDataWrite := io.testerDataMemDataWrite
  dataMemory.io.testerEnable := io.testerDataMemEnable
  dataMemory.io.testerWriteEnable := io.testerDataMemWriteEnable
}