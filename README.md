# From Function1#apply to Kleisli

## Different Ways of Function Composition in Scala

This repo contains code an slides for my talk on this subject.

--

Fine-grained composability of functions is one of the core advantages of FP.

Treating "Functions as Data" means that we can
store, manipulate, pass functions around and compose them
in much the same way we do with data.

This talk demonstrates different ways of function composition in Scala.

The focus lies on *scala.Function1*, because due to tupling and
currying we can regard any FunctionN (except *Function0*) as a *Function1*.
Curried functions are easier to compose.

Starting with the composition methods of *scala.Function1*: *apply*,
*compose* and *andThen*, we will investigate folding a *Seq* of functions.

We can also define a pipe operator |> as in F# in order to 'pipe'
values through a pipeline of functions.

Defining a Monoid for *Function1* allows us to combine two or more
functions into a new one.

A function can also be seen as a Functor and a Monad. That means:
Functions can be mapped and flatMapped over. And we can write
for-comprehensions in a *Function1* context just as we do
with *List*, *Option*, *Future*, *Either* etc.

Being Monads, we can use functions in any monadic context.
We will see that *Function1* **is** the Reader Monad.

The most powerful way of function composition is *Kleisli*
(also known as *ReaderT*). We will see that *Kleisli*
(defined with the *Id* context) **is** the Reader Monad again.

--

All this is demonstrated in small sample programs in package *demo*.
Most of the demos are provided in two versions,
one using the Cats library, the other one using
my own implementations of categories provided in package *mycats*.

--

Another App (in package *kleisliapp*) is in *kleisliapp.WCApp[1-7]*.scala*.
This App is a more advanced Kleisli-demo. It implements a simple word count
(WC) for a text resource. The App is implemented in 7 subsequent steps,
one improving the other.

- Step 1: First draft: synchronous impl with try-catch-finally
- Step 2: Synchronous impl with *Try* and auto-closing of the resource
- Step 3: Uses Cats' Either syntax to easily convert a *Try* to an *Either*
- Step 4: Goes functional. It turns methods (defs) into functions (vals).
- Step 5: Uses *Kleisli* wrapping an *Either*
- Step 6: Uses *Kleisli* wrapping an *EitherT* which wraps a *Future* wrapping an *Either*
- Step 7: abstracts over Future and turns Future to a generic effect: *F[_]: Monad*.
F is reified with three concrete types: *cats.Id*, *scala.concurrent.Future* and *monix.eval.Task*.
