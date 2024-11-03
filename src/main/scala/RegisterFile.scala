import chisel3._
import chisel3.util._

class RegisterFile extends Module {
  val io = IO(new Bundle {
    //Define the module interface here (inputs/outputs)
    val aSel = Input(UInt(5.W))
    val bSel = Input(UInt(5.W))
    val writeData = Input(UInt(32.W))
    val writeSel = Input(UInt(4.W))
    val writeEnable = Input(UInt(1.W))
    val a = Output(UInt(32.W))
    val b = Output(UInt(32.W))
  })
  //Implement this module here
  private val registerFile = Reg(Vec(32, UInt(32.W)))

  when(io.writeEnable === 1.U(1.W)){
    registerFile(io.writeSel) := io.writeData
  }

  io.a := registerFile(io.aSel)
  io.b := registerFile(io.bSel)
}