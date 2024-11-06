import chisel3._
import chisel3.util._

class ControlUnit extends Module {
  val io = IO(new Bundle {
    val opcode = Input(UInt(4.W))
    val RegDst = Output(Bool())
    val writeEnable = Output(Bool())
    val ALUsrc = Output(Bool())
    val StoreImd = Output(Bool())
    val ALUop = Output(UInt(3.W))
    val Jump = Output(Bool())
    val MemWrite = Output(Bool())
    val MemtoReg = Output(Bool())
    val stop = Output(Bool())
  })

  io.RegDst := 0.U
  io.writeEnable := 0.U
  io.ALUsrc := 0.U
  io.StoreImd := 0.U
  io.Jump := 0.U
  io.MemWrite := 0.U
  io.MemtoReg := 0.U
  io.stop := 0.U
  io.ALUop := 0.U

  switch(io.opcode) {
    is("b0000".U) { //ADD
      io.ALUop := 0.U
      io.writeEnable := 1.U
      io.ALUsrc := 1.U
    }
    is("b0001".U) { //ADI
      io.RegDst := 1.U
      io.ALUop := 0.U
      io.writeEnable := 1.U
      io.ALUsrc := 0.U
    }
    is("b0010".U) { //SUB
      io.ALUop := 1.U
      io.writeEnable := 1.U
      io.ALUsrc := 1.U
    }
    is("b0011".U) { //SBI
      io.RegDst := 1.U
      io.ALUop := 1.U
      io.writeEnable := 1.U
      io.ALUsrc := 0.U
    }
    is("b0100".U) { //MUL
      io.ALUop := 2.U
      io.writeEnable := 1.U
      io.ALUsrc := 1.U
    }
    is("b0101".U) { //LI
      io.ALUop := 5.U
      io.writeEnable := 1.U
      io.ALUsrc := 1.U
      io.StoreImd := 1.U
      io.RegDst := 1.U
      io.MemWrite :=  1.U
      io.MemtoReg := 1.U
    }
    is("b0110".U) { //LD
      io.ALUop := 5.U
      io.writeEnable := 1.U
      io.MemtoReg := 1.U
      io.RegDst := 1.U
    }
    is("b0111".U) { //SD
      io.ALUop := 5.U
      io.MemWrite := 1.U
    }
    is("b1000".U) { //SI
      io.ALUop := 5.U
      io.MemWrite := 1.U
      io.StoreImd := 1.U
    }
    is("b1001".U) { //JMP
      io.ALUop := 6.U
      io.Jump := 1.U
    }
    is("b1010".U) { //JEQ
      io.ALUop := 3.U
      io.ALUsrc := 1.U
      io.Jump := 1.U
    }
    is("b1011".U) { //JZ
      io.ALUop := 4.U
      io.Jump := 1.U
    }
    is("b1100".U) { //NOP
      io.ALUop := 5.U
    }
    is("b1111".U) { //END
      io.ALUop := 5.U
      io.stop := 1.U
    }
  }

}