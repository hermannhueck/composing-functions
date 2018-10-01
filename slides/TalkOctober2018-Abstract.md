# From Function1#compose to Kleisli

## Different Ways of Function Composition

Fine-grained composability of functions is one of the core advantages of FP.

Treating "Functions as Data" means that we can
store, manipulate, pass functions around and compose them
in much the same way we do with data.

This talk demonstrates different ways of function composition in Scala.

The focus lies on *scala.Function1*, because due to tupling and
currying we can regard any FunctionN (except *Function0*) as a *Function1*.
Curried functions are easier to compose.

We start with the composition methods of *scala.Function1*:
*compose* and *andThen*.

A function can also be seen as a Functor and a Monad. That means:
Functions can be mapped and flatMapped over. And we can write
for-comprehensions in a *Function1* context just as we do
with *List*, *Option*, *Future*, *Either* etc.

Being Monads, we can use functions in any monadic context.
We will see that *Function1* **is** the Reader Monad.

The most powerful way of function composition is *Kleisli*
(also known as *ReaderT*). We will see that *Kleisli*
(defined with the *Id* context) **is** the Reader Monad again.
