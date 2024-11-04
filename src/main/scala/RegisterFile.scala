import chisel3._
import chisel3.util._

class RegisterFile extends Module {
  val io = IO(new Bundle {
    //Define the module interface here (inputs/outputs)
    val aSel = Input(UInt(5.W))
    val bSel = Input(UInt(5.W))
    val writeData = Input(UInt(32.W))
    val writeSel = Input(UInt(4.W))
    val writeEnable = Input(Bool())
    val a = Output(UInt(32.W))
    val b = Output(UInt(32.W))

    val r0 = Output(UInt(32.W))
    val r1 = Output(UInt(32.W))
  })
  //Implement this module here
  val registerFile = Reg(Vec(32, UInt(32.W)))
  io.r0 := registerFile(0)
  io.r1 := registerFile(1)

  when(io.writeEnable){
    registerFile(io.writeSel) := io.writeData
  }

  io.a := registerFile(io.aSel)
  io.b := registerFile(io.bSel)
}