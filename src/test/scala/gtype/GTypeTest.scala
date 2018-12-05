package gtype

import org.scalatest.{Matchers, WordSpec}
import GType.API._
import org.scalacheck.Prop.{forAll, BooleanOperators}
import GType._

class GTypeTest extends WordSpec with MyTest {
  "GType pretty print test cases" in {
    (List("a", "b") -: (List('c) -: any)).prettyPrint shouldBe "(a,b)->(c)->*"
    val abcd = List('a) -: List('b) -: List('c) -: 'd
    abcd.prettyPrint shouldBe "(a)->(b)->(c)->d"
    obj('f -> abcd).prettyPrint shouldBe "{f: (a)->(b)->(c)->d}"
  }

  "Print random GTypes" in {
    forAll(GType.simpleGTypeGen) { t =>
      println(s"<${t.astSize}> $t")
      t == t
    }.check()
  }


  "the consistent-subtyping relation" should {
    val (typeGen, contextGen) = (GType.simpleGTypeGen, GType.simpleContextGen)
    "be reflexive" in {
      checkProp(
        forAll(typeGen, contextGen) { (t, context) =>
          checkSubType(t, t, context).nonEmpty
        })
    }

    "pass simple examples" in {
      import SamplePrograms._
      val context = typeContext

      assert(context.isSubType(point2D, point))
      assert(context.isSubType(point, 'PointAlias))
      assert(!context.isSubType(point, point2D))
      assert(!context.isSubType(obj('x -> "int", 'y -> "int"), point2D))
      assert(context.isSubType(point2D, obj('x -> "int", 'y -> "int")))
    }
  }
}