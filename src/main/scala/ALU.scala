import chisel3._
import chisel3.util._

class ALU extends Module {
  val io = IO(new Bundle {
    val x = Input(UInt(32.W))
    val y = Input(UInt(32.W))
    val sel = Input(UInt(3.W))
    val res = Output(UInt(32.W))
    val comp = Output(UInt(1.W))
  })

  io.res := 0.U
  io.comp := 0.U

  switch(io.sel) {
    is(0.U) { io.res := io.x + io.y }
    is(1.U) { io.res := io.x - io.y }
    is(2.U) { io.res := io.x * io.y }
    is(3.U) { io.comp := io.x === io.y }
    is(4.U) { io.comp := io.x === 0.U  }
  }

}