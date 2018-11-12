# encoding: utf-8

load 'board.rb'

class XUnit

  attr_accessor :failures
  attr_accessor :index

  def initialize
    @failures = Array.new
    @index = 1
  end

  def assert(expected, actual)
    @index += 1
    if expected.to_s == actual.to_s
      # do nothing
    else
      @failures.push "[" + @index.to_s + "] '" + expected.to_s + "' != '" + actual.to_s + "'"
    end
  end

  def print()
    puts "------------------------------------"
    if @failures.any?
      @failures.each { |fail| puts "FAIL " + fail }
    else
      puts "SUCCESS!!!"      
    end
    puts "------------------------------------"
  end

end

test = XUnit.new

str = "{\"currentFigureType\":\"T\",\"futureFigures\":[\"I\",\"O\",\"L\",\"Z\"],\"layers\":[\"" +
"......." +
"......I" +
"..LL..I" +
"...LI.I" +
".SSLI.I" +
"SSOOIOO" +
"..OOIOO" +
"\"],\"currentFigurePoint\":{\"x\":1,\"y\":2}}"

board = Board.new
board.process(str)

pt1 = Point.new(0, 0)
test.assert(ELEMENTS[:NONE], board.get_at(pt1))
test.assert(false, board.is_at?(pt1, ELEMENTS[:O_YELLOW]))
test.assert(true, board.is_at?(pt1, ELEMENTS[:NONE]))
test.assert(true, board.is_at?(pt1, [ELEMENTS[:L_ORANGE], ELEMENTS[:NONE]]))
test.assert(true, board.is_free?(pt1))

pt2 = Point.new(2, 0)
test.assert(ELEMENTS[:O_YELLOW], board.get_at(pt2))
test.assert(true, board.is_at?(pt2, ELEMENTS[:O_YELLOW]))
test.assert(false, board.is_at?(pt2, ELEMENTS[:NONE]))
test.assert(false, board.is_at?(pt2, [ELEMENTS[:L_ORANGE], ELEMENTS[:NONE]]))
test.assert(false, board.is_free?(pt2))

pt3 = Point.new(2, 2)
test.assert(ELEMENTS[:S_GREEN], board.get_at(pt3))
test.assert(false, board.is_at?(pt3, ELEMENTS[:O_YELLOW]))
test.assert(false, board.is_at?(pt3, ELEMENTS[:NONE]))
test.assert(false, board.is_at?(pt3, [ELEMENTS[:L_ORANGE], ELEMENTS[:NONE]]))
test.assert(false, board.is_free?(pt3))

pt4 = Point.new(3, 4)
test.assert(ELEMENTS[:L_ORANGE], board.get_at(pt4))
test.assert(false, board.is_at?(pt4, ELEMENTS[:O_YELLOW]))
test.assert(false, board.is_at?(pt4, ELEMENTS[:NONE]))
test.assert(true, board.is_at?(pt4, [ELEMENTS[:L_ORANGE], ELEMENTS[:NONE]]))
test.assert(false, board.is_free?(pt4))

test.assert("[\"[1,2]\", \"[2,2]\", \"[0,1]\", \"[1,1]\"]",
            board.get(ELEMENTS[:S_GREEN]).map {|it| it.to_s })

test.assert("[\"[2,4]\", \"[3,4]\", \"[3,3]\", \"[3,2]\"]",
            board.get(ELEMENTS[:L_ORANGE]).map {|it| it.to_s })

test.assert("[\"[1,2]\", \"[2,2]\", \"[0,1]\", \"[1,1]\"]",
            board.get(ELEMENTS[:S_GREEN]).map {|it| it.to_s })

test.assert("[\"[2,4]\", \"[3,4]\", \"[3,3]\", \"[1,2]\", \"[2,2]\", \"[3,2]\", \"[0,1]\", \"[1,1]\"]",
            board.get([ELEMENTS[:L_ORANGE], ELEMENTS[:S_GREEN]]).map {|it| it.to_s })

test.print