import chisel3._
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
    val testR3 = Output(UInt (32.W));
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
  val inst = programMemory.io.instructionRead
  controlUnit.io.opcode := inst(31, 28)
  registerFile.io.aSel := inst(27, 23)
  registerFile.io.bSel := inst(22, 18)
  registerFile.io.writeSel := Mux(controlUnit.io.RegDst,inst(22, 18), inst(18, 13))
  programCounter.io.jump := controlUnit.io.Jump & alu.io.comp
  programCounter.io.programCounterJump := inst(17, 0)
  alu.io.x := registerFile.io.a
  alu.io.y := Mux(controlUnit.io.ALUsrc,registerFile.io.b, inst(17, 0))
  alu.io.sel := controlUnit.io.ALUop;
  dataMemory.io.address := alu.io.res
  dataMemory.io.dataWrite := Mux(controlUnit.io.StoreImd, inst(17,0), registerFile.io.b)
  dataMemory.io.writeEnable := controlUnit.io.MemWrite
  registerFile.io.writeData := Mux(controlUnit.io.MemtoReg, dataMemory.io.dataRead, alu.io.res)
  registerFile.io.writeEnable := controlUnit.io.writeEnable
  io.testR3 := registerFile.io.r3


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