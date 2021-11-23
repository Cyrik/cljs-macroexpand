# cljs-macroexpand

clojurescript macroexpand-all macro with meta support.

```clojure
(cljs-macroexpand-all '(when (let [a 5] a) "false"))
;; (if (let* [a 5] a) (do "false"))
(macroexpand '(when (let [a 5] a) "false"))
;; (if (let [a 5] a) (do "false"))
```

Basically clojure.walk.macroexpand-all for clojurescript.

Its mostly lifted from [flow-storm](https://github.com/jpmonettas/flow-storm/blob/master/src/flow_storm/instrument.clj) who came up with a way to macroexpand inside clojurescript. The expansion itself is mostly from [orchard](https://github.com/clojure-emacs/orchard/blob/master/src/orchard/meta.clj#L313).


## Usage

FIXME: write usage documentation!

Run the project's tests (they'll fail until you edit them):

    $ clojure -X:test

Run the project's tests (they'll fail until you edit them):

    $ clojure -T:build test

Run the project's CI pipeline and build a JAR (this will fail until you edit the tests to pass):

    $ clojure -T:build ci

This will produce an updated `pom.xml` file with synchronized dependencies inside the `META-INF`
directory inside `target/classes` and the JAR in `target`. You can update the version (and SCM tag)
information in generated `pom.xml` by updating `build.clj`.

Install it locally (requires the `ci` task be run first):

    $ clojure -T:build install

Deploy it to Clojars -- needs `CLOJARS_USERNAME` and `CLOJARS_PASSWORD` environment
variables (requires the `ci` task be run first):

    $ clojure -T:build deploy

## License

Distributed under the Eclipse Public License version 1.0.
