package textdsl

import org.scalatest.{FlatSpec, MustMatchers}

class TextDSLSpec extends FlatSpec with MustMatchers with TextDSL {

  "aligncolumns" must "align columns" in {

    val doc: Document =
      """|---x--
         |---x--x----x--
         |-x--
         |-xxxxx""".stripMargin.document

    val expected: Document =
      """|---x--
         |---x--x----x--
         |-  x--
         |-  x  x    x  xx""".stripMargin.document

    toText(alignColumns("x")(doc)) mustEqual toText(expected)
  }

  "document" must "convert a string to a Document" in {
    val text =
      """hi
        |bye""".stripMargin

    document(text) mustEqual Vector("hi", "bye")
  }

  it must "support different line feed characters" in {
    document("HiXBye")(new LineFeed("X")) mustEqual Vector("Hi", "Bye")
  }
}
