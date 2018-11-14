package main

import (
	b "./board"
	"fmt"
	"log"
	"strings"
)

type XUnit struct {
	Failures  []string
	Index     int
}

func NewTest() *XUnit {
	t := new(XUnit)
	t.Failures = []string{}
	t.Index = 0
	return t
}

func (t *XUnit) assert(expected interface{}, actual interface{}) {
	t.Index += 1
	if expected == actual {
	} else {
		message := fmt.Sprintf("[%v] \"%v\" != \"%v\"", t.Index, expected, actual)
		t.Failures = append(t.Failures, message)
	}
}

func (t *XUnit) print() {
	log.Println("------------------------------------")
	if len(t.Failures) > 0 {
		for _, it := range t.Failures {
			log.Println("FAIL " + it)
		}
	} else {
		log.Println("SUCCESS!!!")
	}
	log.Println("------------------------------------")
}

func main() {

	test := NewTest()

	question := b.Question{}
	question.FutureFigures = []string{"I", "O", "L", "Z"}
	question.CurrentFigurePoint = b.Point{1, 2}
	question.CurrentFigureType = "T"
	question.Layers = []string{
		"......." +
		"......I" +
		"..LL..I" +
		"...LI.I" +
		".SSLI.I" +
		"SSOOIOO" +
		"..OOIOO"}

	board := b.NewBoard(&question)
	board.ToString()

	test.assert(b.T_PURPLE, board.CurrentFigureType)
	test.assert("[1,2]", board.CurrentFigurePoint.String())
	test.assert("I,O,L,Z", strings.Join(board.FutureFigures, ","))

	test.assert(b.NONE, board.GetAt(0, 0))
	test.assert(false, board.IsAt(0, 0, []string { b.O_YELLOW }))
	test.assert(true, board.IsAt(0, 0, []string { b.NONE }))
	test.assert(true, board.IsAt(0, 0, []string { b.L_ORANGE, b.NONE }))

	test.assert(b.O_YELLOW, board.GetAt(2, 0))
	test.assert(true, board.IsAt(2, 0, []string { b.O_YELLOW }))
	test.assert(false, board.IsAt(2, 0, []string { b.NONE }))
	test.assert(false, board.IsAt(2, 0, []string { b.L_ORANGE, b.NONE }))

	test.assert(b.S_GREEN, board.GetAt(2, 2))
	test.assert(false, board.IsAt(2, 2, []string { b.O_YELLOW }))
	test.assert(false, board.IsAt(2, 2, []string { b.NONE }))
	test.assert(false, board.IsAt(2, 2, []string { b.L_ORANGE, b.NONE }))
	
	test.assert(b.L_ORANGE, board.GetAt(3, 4))
	test.assert(false, board.IsAt(3, 4, []string { b.O_YELLOW }))
	test.assert(false, board.IsAt(3, 4, []string { b.NONE }))
	test.assert(true, board.IsAt(3, 4, []string { b.L_ORANGE, b.NONE }))

	// TODO реализоватьis_free?
	// TODO реализоватьget
	// TODO реализоватьget_near
	// TODO реализоватьis_near?
	// TODO реализоватьcount_near
	// TODO реализоватьget_figures
	// TODO реализоватьget_free_space

	test.print()
}