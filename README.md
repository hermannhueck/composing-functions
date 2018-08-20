# Composing Functions in different ways

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
