module Main
where

s2i :: String -> Int
s2i = read

plus2 :: Int -> Int
plus2 = (+2)

div10By :: Int -> Double
div10By x = (10.0 / fromIntegral x)

d2s :: Double -> String
d2s d = (show d) ++ " !!!"

fComposed :: String -> String
fComposed = d2s . div10By . plus2 . s2i

result = fComposed "3"

main = do
  print "===== Composing Functions"
  print result
  print "-----"