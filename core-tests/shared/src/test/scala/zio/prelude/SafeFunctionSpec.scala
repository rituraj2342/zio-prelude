package zio.prelude

import zio.prelude.laws._
import zio.test._

object SafeFunctionSpec extends ZIOSpecDefault {

  val string: String = "The quick brown fox"

  def words(string: String): List[String] =
    string.split(" ").toList

  def count(words: List[String]): Int =
    words.length

  def increment(n: Int): Int =
    n + 1

  def spec: Spec[Environment, Any] =
    suite("SafeFunctionSpec")(
      test("andThen") {
        val wordCount = SafeFunction(words) andThen SafeFunction(count)
        assert(wordCount(string))(equalTo(4))
      },
      test("compose") {
        val wordCount = SafeFunction(count) compose SafeFunction(words)
        assert(wordCount(string))(equalTo(4))
      },
      test("stack safety") {
        val fs = List.fill(100000)(SafeFunction(increment))
        val f  = fs.reduce(_ andThen _)
        assert(f(0))(equalTo(100000))
      }
    )
}
