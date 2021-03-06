package textdsl

class LineFeed(val lf: String) extends AnyVal

trait TextDSL {

  implicit val LF: LineFeed = new LineFeed("\n")

  type Document = Vector[String]

  type StringTransformation = String ⇒ String

  type Transformation = Document ⇒ Document

  type Search[T] = String ⇒ Option[T]

  type Column = Vector[String]

  type Columns = Vector[Column]

  type ColumnTransform = Columns ⇒ Columns

  // helpful stuff

  implicit class StringSyntax(s: String) {
    def document: Document = TextDSL.document(s)
  }

  implicit class FunctionSyntax[A, B](f: A ⇒ B) {
    def o[C](g: B ⇒ C): A ⇒ C = f andThen g

    def ∘[C](g: B ⇒ C): A ⇒ C = f andThen g
  }

  def document(text: String)(implicit lineFeed: LineFeed): Document =
    text.split(lineFeed.lf).toVector

  def toText(d: Document)(implicit lineFeed: LineFeed): String =
    d.mkString(lineFeed.lf)

  // the api

  def replaceAll(regex: String, replacement: String): StringTransformation =
    (_: String).replaceAll(regex, replacement)

  def deleteAll(s: String): StringTransformation =
    replaceAll(s, "")

  def append(s: String): StringTransformation =
    (_: String) + s

  def sort: Transformation =
    (_: Document).sorted

  def sortCaseInsensitive: Transformation =
    (_: Document).sortWith(_.toLowerCase < _.toLowerCase)

  def index(s: String): Search[Int] =
    (l: String) => {
      val index = l.indexOf(s)
      if (index < 0) None else Some(index)
    }

  def splitLine(delimiter: String): String ⇒ Column =
    (l: String) => {
      def help(a: String): Column = {
        val index = a.indexOf(delimiter, 1)
        if (index < 0) Vector(a) else Vector(a.substring(0, index)) ++ help(a.substring(index))
      }

      help(l)
    }

  def columnise(delimiter: String): Document ⇒ Columns =
    (_: Document).map(splitLine(delimiter))

  def joinColumns: Columns ⇒ Document =
    (_: Columns).map(a => a.mkString)

  // ensures columns have the same width - padding smaller columns with empty strings if necessary
  def normaliseColumnWidth: ColumnTransform = { (columns: Columns) ⇒
    val numCols = columns.maxBy(_.size).size
    columns.map((v: Column) => if (v.size < numCols) v.padTo(numCols, "") else v)
  }

  def transposeColumns: ColumnTransform =
    _.transpose

  def padColumns: ColumnTransform =
    (columns: Columns) ⇒
      columns.map((col: Column) => {
        val max: Int = col.map(_.length).max

        def pad(l: String) = l + " " * (max - l.length)

        col.map(pad)
      })

  def trimLines: Transformation =
    (document: Document) ⇒
      document.map(_.trim)

  def alignColumns(s: String): Transformation =
    columnise(s) ∘
      normaliseColumnWidth ∘
      transposeColumns ∘
      padColumns ∘
      transposeColumns ∘
      joinColumns ∘
      trimLines
}

object TextDSL extends TextDSL