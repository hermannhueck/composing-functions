package playground

// code of Sinisa Louc's blogpost at
// https://medium.com/@sinisalouc/overcoming-type-erasure-in-scala-8f2422070d20

object ClassTagAndTypeTag extends App {

  object extractor1 {

    object Extractor {
      def extract[T](list: List[Any]): List[T] = list.flatMap {
        case element: T => Some(element)
        case _ => None
      }

      private def ff[T](x: Any): Option[T] = {
        x match {
          case element: T => Some(element)
          case _ => None
        }
      }
    }

    val list = List(1, "string1", List(), "string2")
    val result = Extractor.extract[String](list)
    println(result) // List(1, string1, List(), string2)
  }

  object extractor2 {

    import scala.reflect.ClassTag

    object Extractor {
      def extract[T](list: List[Any])(implicit tag: ClassTag[T]) =
        list.flatMap {
          case element: T => Some(element)
          case _ => None
        }
    }

    val list: List[Any] = List(1, "string1", List(), "string2")
    val result = Extractor.extract[String](list)
    println(result) // List(string1, string2)

    val list2: List[List[Any]] = List(List(1, 2), List("a", "b"))
    val result2 = Extractor.extract[List[Int]](list2)
    println(result2) // List(List(1, 2), List(a, b))
  }

  object recognizer1 {

    import scala.reflect.runtime.universe._

    object Recognizer {
      def recognize[T](x: T)(implicit tag: TypeTag[T]): String =
        tag.tpe match {
          case TypeRef(utype, usymbol, args) =>
            List(utype, usymbol, args).mkString("\n")
        }
    }

    val list: List[Int] = List(1, 2)
    val result = Recognizer.recognize(list)
    println(result)

    // prints:
    //   scala.type
    //   type List
    //   List(Int)
  }

  object recognizer2 {

    import scala.reflect.runtime.universe._
    abstract class SomeClass[T] {

      object Recognizer {
        def recognize[T](x: T)(implicit tag: WeakTypeTag[T]): String =
          tag.tpe match {
            case TypeRef(utype, usymbol, args) =>
              List(utype, usymbol, args).mkString("\n")
          }
      }

      val list: List[T]
      val result = Recognizer.recognize(list)
      println(result)
    }

    new SomeClass[Int] { val list = List(1) }

    // prints:
    //   scala.type
    //   type List
    //   List(T)
  }

  object helpers {

    import scala.reflect.classTag
    import scala.reflect.runtime.universe._

    val ct = classTag[String]
    val tt = typeTag[List[Int]]
    val wtt = weakTypeTag[List[Int]]

    val array = ct.newArray(3)
    array.update(2, "Third")

    println(array.mkString(","))
    println(tt.tpe)
    println(wtt.equals(tt))

    //  prints:
    //    null,null,Third
    //    List[Int]
    //    true
  }

  println("\n----- extractor1:")
  extractor1
  println("----- extractor2:")
  extractor2
  println("----- recognizer1:")
  recognizer1
  println("----- recognizer2:")
  recognizer2
  println("----- helpers:")
  helpers
  println("-----\n")
}
