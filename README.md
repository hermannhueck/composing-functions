# From Function Composition to Kleisli

Fine-grained composability of functions is one of the core advantages of FP.

In this little project I demonstrate different ways of composing functions.

I only deal with *scala.Function1*, because due to currying we can regard
any function as a *Function1*.

Starting with *Function1#compose* and *Function1.andThen*
I implement *map* and *flatMap* for Function1 what allows me
to use functions in for-comprehensions. I also make it
a Monad in order to treat functions as Monads, i.e. to use them
in any monadic context.

Next I implement my own *mycats.Kleisli* similar to *cats.data.Kleisli*
and show its usage with *flatMap*, *flatMapF*, *andThen* and *compose*.
I then show the *Reader* Monad, a *Kleisli* simplified with *Id*.

Finally I turn to and demonstrate function composition with Monoids.

All this is in package demo.

--

Another App (in package app) is in app.WCApp[1-6]*.scala.
This App implements a simple word count (WC) for a text resource on github.
This App is implmented in 6 subsequent steps, one improving the other.

- Step 1: synchronous impl with try-catch-finally
- Step 2: synchronous impl with Try and auto-closing of the resource
- Step 3: goes functional. It turns methods (defs) into functions (vals).
- Step 4: Uses Kleisli wrapping an Either
- Step 5: Uses Kleisli wrapping an EitherT which wraps a Future wrapping an Either
- Step 6: abstracts over Future and turns Future to F[_]: Monad.
F is reified with three concrete types: cats.Id, scala.concurrent.Future and monix.eval.Task.
