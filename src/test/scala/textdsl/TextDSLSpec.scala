package textdsl

import TextDSL._
import org.scalatest.{FlatSpec, MustMatchers}

class TextDSLSpec extends FlatSpec with MustMatchers {

  "aligncolumns" must "align columns" in {

    val doc: Document =
      """|---x--
         |---x--x----x--
         |-x--
         |-xxxxx""".stripMargin

    val expected: Document =
      """|---x--
         |---x--x----x--
         |-  x--
         |-  x  x    x  xx""".stripMargin

    toText(alignColumns("x")(doc)) mustEqual toText(expected)
  }
}
