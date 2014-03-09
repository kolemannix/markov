(ns unit.knix.markov
  (:require [midje.sweet :refer :all]
            [knix.markov :refer :all]))

(def corpus (slurp "resources/corpus/wordlist.txt"))

(def test-corpus (slurp "resources/corpus/test_corpus.txt"))

(fact "Database is generated properly for small corpus"
  (build-database test-corpus 2) => {["Hack" "@"] {"U.Va!" 1},
                                     ["fast." "The"] {"sky" 1},
                                     ["is" "cloudy."] {"The" 1},
                                     ["here." "kolemannix"] {"wants" 1},
                                     ["kimkardashian" "has"] {"no" 1},
                                     ["The" "car"] {"is" 1},
                                     ["kolemannix" "wants"] {"to" 1},
                                     ["foggy." "bombgardener"] {"is" 1},
                                     ["bombgardener" "is"] {"here." 1},
                                     ["cloudy." "The"] {"sky" 1},
                                     ["has" "no"] {"fun." 1},
                                     ["kid" "jumped"] {"off." 1},
                                     ["is" "fast."] {"The" 1},
                                     ["sky" "is"] {"foggy." 1, "sunny." 1, "cloudy." 1, "blue." 1},
                                     ["is" "blue."] {"The" 1},
                                     ["is" "sunny."] {"The" 1},
                                     ["to" "party."] {"kimkardashian" 1},
                                     ["no" "fun."] {"Hack" 1},
                                     ["party." "kimkardashian"] {"has" 1},
                                     ["The" "sky"] {"is" 4},
                                     ["jumped" "off."] {"The" 1},
                                     ["fun." "Hack"] {"@" 1},
                                     ["off." "The"] {"car" 1},
                                     ["is" "here."] {"kolemannix" 1},
                                     ["is" "foggy."] {"bombgardener" 1},
                                     ["The" "kid"] {"jumped" 1},
                                     ["car" "is"] {"fast." 1},
                                     ["wants" "to"] {"party." 1},
                                     ["sunny." "The"] {"sky" 1},
                                     ["blue." "The"] {"kid" 1}}

  (build-database test-corpus 1) => {["sky"] {"is" 4},
                                     ["no"] {"fun." 1},
                                     ["Hack"] {"@" 1},
                                     ["wants"] {"to" 1},
                                     ["bombgardener"] {"is" 1},
                                     ["kid"] {"jumped" 1},
                                     ["party."] {"kimkardashian" 1},
                                     ["is"]
                                     {"here." 1,
                                      "foggy." 1,
                                      "sunny." 1,
                                      "cloudy." 1,
                                      "fast." 1,
                                      "blue." 1},
                                     ["cloudy."] {"The" 1},
                                     ["jumped"] {"off." 1},
                                     ["fun."] {"Hack" 1},
                                     ["The"] {"car" 1, "kid" 1, "sky" 4},
                                     ["fast."] {"The" 1},
                                     ["blue."] {"The" 1},
                                     ["car"] {"is" 1},
                                     ["kimkardashian"] {"has" 1},
                                     ["sunny."] {"The" 1},
                                     ["has"] {"no" 1},
                                     ["to"] {"party." 1},
                                     ["foggy."] {"bombgardener" 1},
                                     ["here."] {"kolemannix" 1},
                                     ["off."] {"The" 1},
                                     ["@"] {"U.Va!" 1},
                                     ["kolemannix"] {"wants" 1}})

;; The above data generated using clojure.pprint/pprint

(fact "lookup function behaves properly"
  (lookup ["The"] (build-database test-corpus 1)) => {"car" 1, "kid" 1, "sky" 4})

(fact "select randomly function behaves properly"
  (select-randomly {"foggy" 1, "sunny" 5, "cloudy" 1, "blue" 1}) => (some-checker
                                                                     "foggy" "sunny" "cloudy" "blue"))

(fact "Capital seeds are chosen when available"
  (choose-seed (build-database test-corpus 2)) => (some-checker ["The" "kid"]
                                                                      ["The" "car"]
                                                                      ["The" "sky"]
                                                                      ["Hack" "@"]))
