module ProgramCounter(
  input         clock,
  input         reset,
  input         io_stop,
  input         io_jump,
  input         io_run,
  input  [15:0] io_programCounterJump,
  output [15:0] io_programCounter
);
`ifdef RANDOMIZE_REG_INIT
  reg [31:0] _RAND_0;
`endif // RANDOMIZE_REG_INIT
  reg [31:0] programCounterNext; // @[ProgramCounter.scala 15:35]
  wire  _T = ~io_run; // @[ProgramCounter.scala 17:8]
  wire  _T_1 = _T | io_stop; // @[ProgramCounter.scala 17:16]
  wire  _T_2 = io_programCounter >= 16'hffff; // @[ProgramCounter.scala 17:48]
  wire  _T_3 = _T_1 | _T_2; // @[ProgramCounter.scala 17:27]
  wire  _T_4 = io_run & io_jump; // @[ProgramCounter.scala 20:22]
  wire  _T_5 = ~io_stop; // @[ProgramCounter.scala 20:36]
  wire  _T_6 = _T_4 & _T_5; // @[ProgramCounter.scala 20:33]
  wire [15:0] _T_8 = io_programCounter + 16'h1; // @[ProgramCounter.scala 24:45]
  wire [15:0] _GEN_0 = _T_6 ? io_programCounterJump : _T_8; // @[ProgramCounter.scala 20:46]
  wire [15:0] _GEN_1 = _T_3 ? io_programCounter : _GEN_0; // @[ProgramCounter.scala 17:67]
  assign io_programCounter = programCounterNext[15:0]; // @[ProgramCounter.scala 27:21]
`ifdef RANDOMIZE_GARBAGE_ASSIGN
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_INVALID_ASSIGN
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_REG_INIT
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_MEM_INIT
`define RANDOMIZE
`endif
`ifndef RANDOM
`define RANDOM $random
`endif
`ifdef RANDOMIZE_MEM_INIT
  integer initvar;
`endif
`ifndef SYNTHESIS
`ifdef FIRRTL_BEFORE_INITIAL
`FIRRTL_BEFORE_INITIAL
`endif
initial begin
  `ifdef RANDOMIZE
    `ifdef INIT_RANDOM
      `INIT_RANDOM
    `endif
    `ifndef VERILATOR
      `ifdef RANDOMIZE_DELAY
        #`RANDOMIZE_DELAY begin end
      `else
        #0.002 begin end
      `endif
    `endif
`ifdef RANDOMIZE_REG_INIT
  _RAND_0 = {1{`RANDOM}};
  programCounterNext = _RAND_0[31:0];
`endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`ifdef FIRRTL_AFTER_INITIAL
`FIRRTL_AFTER_INITIAL
`endif
`endif // SYNTHESIS
  always @(posedge clock) begin
    if (reset) begin
      programCounterNext <= 32'h0;
    end else begin
      programCounterNext <= {{16'd0}, _GEN_1};
    end
  end
endmodule
