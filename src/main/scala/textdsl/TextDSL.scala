package textdsl

object TextDSL extends App {
  type Document = Vector[String]

  type SimpleTransformation = String ⇒ String

  type Transformation = Document ⇒ Document

  type Search[T] = String ⇒ Option[T]

  type Column = Vector[String]

  type Columns = Vector[Column]

  type ColumnTransform = Columns ⇒ Columns


  // helpful stuff

  implicit def lift(e: SimpleTransformation): Transformation = (document: Document) ⇒ document.map(e)

  implicit def liftOneToMany(f: String ⇒ Document): Transformation = (_: Document).flatMap(f)

  implicit def toVector(f: String): Document = Vector(f)

  def write(d: Document) = println(d.mkString("\n"))

  // the api

  def replaceAll(regex: String, replacement: String): SimpleTransformation = (_: String).replaceAll(regex, replacement)

  def deleteAll(s: String): SimpleTransformation = replaceAll(s, "")

  def append(s: String): SimpleTransformation = (_: String) + s

  def splitAt(regex: String): Transformation = (_: String).split(regex).to[Vector]

  def lines: Transformation = (_: String).lines.to[Vector]

  // more challenging

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

  // ensures columns have the same lengths - padding with empty strings if necessary
  def normaliseColumnSize: ColumnTransform = { (columns: Columns) ⇒
    val numCols = columns.maxBy(_.size).size
    columns.map((v: Column) => if (v.size < numCols) v.padTo(numCols, "") else v)
  }

  def transposeColumns: ColumnTransform =
    _.transpose

  def padColumns: ColumnTransform = { (columns: Columns) ⇒
    columns.map((col: Column) => {
      val max: Int = col.map(_.length).max

      def pad(l: String) = l + " " * (max - l.length)

      col.map(pad)
    })
  }

  def alignColumns(s: String): Transformation =
    columnise(s) andThen
      normaliseColumnSize andThen
      transposeColumns andThen
      padColumns andThen
      transposeColumns andThen
      joinColumns

  val sample = Vector(
    "---|--",
    "---|--|----|--",
    "-|--",
    "-|||||")

  write(alignColumns("|") apply sample)

  val found: Option[Int] = index("----") apply "hi----bye"
}
