import Math.Combinatorics.Exact.Binomial (choose)

fact n = product [1..n]

perm n r = product [1..n] `div` product [1..(n-r)]
