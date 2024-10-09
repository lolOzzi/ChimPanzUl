;buildInfoPackage: chisel3, version: 3.3.2, scalaVersion: 2.12.11, sbtVersion: 1.3.10
circuit ProgramCounter : 
  module ProgramCounter : 
    input clock : Clock
    input reset : UInt<1>
    output io : {flip stop : UInt<1>, flip jump : UInt<1>, flip run : UInt<1>, flip programCounterJump : UInt<16>, programCounter : UInt<16>}
    
    reg programCounterNext : UInt<32>, clock with : (reset => (reset, UInt<32>("h00"))) @[ProgramCounter.scala 15:35]
    node _T = eq(io.run, UInt<1>("h00")) @[ProgramCounter.scala 17:8]
    node _T_1 = or(_T, io.stop) @[ProgramCounter.scala 17:16]
    node _T_2 = geq(io.programCounter, UInt<16>("h0ffff")) @[ProgramCounter.scala 17:48]
    node _T_3 = or(_T_1, _T_2) @[ProgramCounter.scala 17:27]
    when _T_3 : @[ProgramCounter.scala 17:67]
      programCounterNext <= io.programCounter @[ProgramCounter.scala 18:24]
      skip @[ProgramCounter.scala 17:67]
    else : @[ProgramCounter.scala 20:46]
      node _T_4 = and(io.run, io.jump) @[ProgramCounter.scala 20:22]
      node _T_5 = eq(io.stop, UInt<1>("h00")) @[ProgramCounter.scala 20:36]
      node _T_6 = and(_T_4, _T_5) @[ProgramCounter.scala 20:33]
      when _T_6 : @[ProgramCounter.scala 20:46]
        programCounterNext <= io.programCounterJump @[ProgramCounter.scala 21:24]
        skip @[ProgramCounter.scala 20:46]
      else : @[ProgramCounter.scala 23:15]
        node _T_7 = add(io.programCounter, UInt<1>("h01")) @[ProgramCounter.scala 24:45]
        node _T_8 = tail(_T_7, 1) @[ProgramCounter.scala 24:45]
        programCounterNext <= _T_8 @[ProgramCounter.scala 24:24]
        skip @[ProgramCounter.scala 23:15]
    io.programCounter <= programCounterNext @[ProgramCounter.scala 27:21]
    
