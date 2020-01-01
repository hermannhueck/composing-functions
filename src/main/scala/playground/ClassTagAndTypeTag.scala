package playground

import scala.reflect.ClassTag
import scala.reflect.runtime.universe

// code of Sinisa Louc's blogpost at
// https://medium.com/@sinisalouc/overcoming-type-erasure-in-scala-8f2422070d20

object ClassTagAndTypeTag extends App {

  object extractor1 {

    object Extractor {

      // flatMap takes a function of type: A => GenTraversableOnce[B]
      def extract[T](list: List[Any]): List[T] = list.flatMap {
        case element: T => Some(element) // !!! warning:
        // abstract type pattern T is unchecked since it is eliminated by erasure
        case _ => None
      }
    }

    // Warning: a type was inferred to be `Any`
    val list                 = List(1, "string1", List(), "string2")
    val result: List[String] = Extractor.extract[String](list)
    println(result) // List(1, string1, List(), string2)
  }

  object extractor2 {

    import scala.reflect.ClassTag

    object Extractor {

      def extract[T](list: List[Any])(implicit tag: ClassTag[T]): List[T] =
        list.flatMap {
          case element: T => Some(element)
          case _          => None
        }
    }

    val list: List[Any]      = List(1, "string1", List(), "string2")
    val result: List[String] = Extractor.extract[String](list)
    println(result) // List(string1, string2)

    val list2: List[List[Any]]   = List(List(1, 2), List("a", "b"))
    val result2: List[List[Int]] = Extractor.extract[List[Int]](list2)
    println(result2) // List(List(1, 2), List(a, b))

    val list3: List[Iterable[Any]] = List(List(1, 2), Set(1, 2))
    val result3: List[List[Int]]   = Extractor.extract[List[Int]](list3)
    println(result3) // List(List(1, 2))
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
    val result: String  = Recognizer.recognize(list)
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

        def recognize[U](x: U)(implicit tag: WeakTypeTag[U]): String =
          tag.tpe match {
            case TypeRef(utype, usymbol, args) =>
              List(utype, usymbol, args).mkString("\n")
          }
      }

      val list: List[T]
      val result: String = Recognizer.recognize(list)
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

    val ct: ClassTag[String]                 = classTag[String]
    val tt: universe.TypeTag[List[Int]]      = typeTag[List[Int]]
    val wtt: universe.WeakTypeTag[List[Int]] = weakTypeTag[List[Int]]

    val array: Array[String] = ct.newArray(3)
    array.update(2, "Third")

    println(array.mkString(","))
    println(tt.tpe)
    println(wtt.tpe)
    println(wtt.equals(tt))

    //  prints:
    //    null,null,Third
    //    List[Int]
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
