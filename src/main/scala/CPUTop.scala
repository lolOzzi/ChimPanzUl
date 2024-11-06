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

    // For testing
    val testRa = Output(UInt (32.W))
    val testRb = Output(UInt (32.W))
    val ALUopTest = Output(UInt(3.W))
    val aSelTest = Output(UInt(5.W))
    val bSelTest = Output(UInt(5.W))
    val writeEnableTest = Output(Bool())
    val writeSelTest = Output(UInt(4.W))
    val writeDataTest = Output(UInt(32.W))
    val instructionTest = Output(UInt (32.W))
    val forceInstTest = Input(Bool ())
    val forceInstruction = Input(UInt (32.W))
    val opOut = Output(UInt (32.W))
    val addressTest = Output(UInt (16.W))
  })

  //Creating components
  val programCounter = Module(new ProgramCounter())
  val dataMemory = Module(new DataMemory())
  val programMemory = Module(new ProgramMemory())
  val registerFile = Module(new RegisterFile())
  val controlUnit = Module(new ControlUnit())
  val alu = Module(new ALU())

  var instruction = RegInit(0.U(32.W))

  when(!io.run){
    instruction := "b11000000000000000000000000000000".U(32.W) // Nop
  }.elsewhen (programMemory.io.testerEnable){
    instruction := programMemory.io.testerDataRead
  }.otherwise{
    instruction := programMemory.io.instructionRead
  }
  io.instructionTest := instruction


  //Connecting the modules
  programCounter.io.run := io.run
  programCounter.io.stop := controlUnit.io.stop
  io.done := controlUnit.io.stop
  programMemory.io.address := programCounter.io.programCounter
  //val inst = programMemory.io.instructionRead
  controlUnit.io.opcode := instruction(31, 28)
  io.opOut := controlUnit.io.opcode
  registerFile.io.aSel := instruction(27, 23)
  registerFile.io.bSel := instruction(22, 18)
  registerFile.io.writeSel := Mux(controlUnit.io.RegDst, instruction(22, 18), instruction(17, 13))
  programCounter.io.jump := controlUnit.io.Jump & alu.io.comp
  programCounter.io.programCounterJump := instruction(17, 0)
  alu.io.x := registerFile.io.a
  alu.io.y := Mux(controlUnit.io.ALUsrc,registerFile.io.b, instruction(17, 0))
  alu.io.sel := controlUnit.io.ALUop
  dataMemory.io.writeEnable := controlUnit.io.MemWrite
  dataMemory.io.address := alu.io.res
  dataMemory.io.dataWrite := Mux(controlUnit.io.StoreImd, instruction(17,0), registerFile.io.b)

  registerFile.io.writeData := Mux(controlUnit.io.MemtoReg, dataMemory.io.dataRead, alu.io.res)
  registerFile.io.writeEnable := controlUnit.io.writeEnable

  // For testing
  io.testRa := registerFile.io.a
  io.testRb := registerFile.io.b
  io.ALUopTest := controlUnit.io.ALUop
  io.aSelTest := registerFile.io.aSel
  io.bSelTest := registerFile.io.bSel
  io.writeEnableTest := registerFile.io.writeEnable
  io.writeSelTest := registerFile.io.writeSel
  io.writeDataTest := registerFile.io.writeData
  io.addressTest := dataMemory.io.address


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