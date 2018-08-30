# From Function1#compose to Kleisli

## Different Ways of Function Composition

Fine-grained composability of functions is one of the core advantages of FP.

Treating "Functions as Data" means that we can ...
- store a function in a val
- pass it as args to other (higher order) functions (HOFs)
- return a function from other functions
- process/manipulate a function like data
- organize functions in data structures like List, Option etc.
- wrap a function in a case class

In this talk I demonstrate different ways of function composition
(and manipulation - what is often the same).

I only deal with *scala.Function1*, because due to currying we can regard
any function (except *Function0*) as a *Function1*. Curried functions are easier to compose.

I start with the methods on Function1: *compose* and *andThen*.
Then I show how to fold a List of functions.

Then I turn to function composition with Monoids.

Functions are Functors (if *Functor* for *Function1* is defined), i.e they can be mapped over.

Functions are Monads (if *Monad* for *Function1* is defined), i.e they can be flatMapped over.

With *map* and *flatMap* we can write for-comprehensions over functions.
As Monads, we can use them in any monadic context.

Next I implement my own *mycats.Kleisli* similar to *cats.data.Kleisli*
and show its usage with *flatMap*, *flatMapF*, *andThen* and *compose*.

Finally I show the *Reader* Monad, a *Kleisli* simplified with *Id*.

--

All this can be found in package *demo*.
Most of the demo programs are provided in two versions,
one using the Cats library, the other one using
my own implementations of categories provided in package *mycats*.

--

Another App (in package *kleisliapp*) is in *kleisliapp.WCApp[1-7]*.scala*.
This App is a Kleisli-demo. It implements a simple word count (WC) for a text resource.
This App is implmented in 7 subsequent steps, one improving the other.

- Step 1: First draft: synchronous impl with try-catch-finally
- Step 2: Synchronous impl with *Try* and auto-closing of the resource
- Step 3: Uses Cats' Either syntax to easily convert a *Try* to an *Either*
- Step 4: Goes functional. It turns methods (defs) into functions (vals).
- Step 5: Uses *Kleisli* wrapping an *Either*
- Step 6: Uses *Kleisli* wrapping an *EitherT* which wraps a *Future* wrapping an *Either*
- Step 7: abstracts over Future and turns Future to a generic effect: *F[_]: Monad*.
F is reified with three concrete types: *cats.Id*, *scala.concurrent.Future* and *monix.eval.Task*.
