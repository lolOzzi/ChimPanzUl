import chisel3._
import chisel3.util._

class ProgramCounter extends Module {
  val io = IO(new Bundle {
    val stop = Input(Bool())
    val jump = Input(Bool())
    val run = Input(Bool())
    val programCounterJump = Input(UInt(16.W))
    val programCounter = Output(UInt(16.W))
  })

  //Implement this module here (respect the provided interface, since it used by the tester)

  val programCounterNext = RegInit(0.U(32.W))

  when(!io.run || io.stop || io.programCounter >= "hffff".U(16.W)){
    programCounterNext := io.programCounter

  } .elsewhen(io.run && io.jump && !io.stop) {
    programCounterNext := io.programCounterJump

  } .otherwise{
    programCounterNext := io.programCounter + 1.U(1.W)

  }
  io.programCounter := programCounterNext
}