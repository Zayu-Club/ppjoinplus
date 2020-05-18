# ppjoinplus 

A implementation of PPJoin+ algorithm by scala.
> C. Xiao, W. Wang, X. Lin, and J. X. Yu. Efficient similarity joins for near duplicate detection. In WWW, pages 131–140, 2008.

---

## Example:
```scala
val agent = new PPJoinPlus(0.6, 2)
// PPJoinPlus(threshold: Double = 0.8, depth: Int = 1)
agent.debug = true
agent.addRecord(
  "C D F",
  "A B E F G",
  "A B C D E",
  "B C D E F")
agent.init()
agent.ppjoin()
agent.ppjoinplus()
println("-" * 50)
agent.checkAll()
```

## Initialization Record
```text
> Initialization <
00000 ｜ [C D F                         ] <= [C D F                         ]
00001 ｜ [G A B E F                     ] <= [A B E F G                     ]
00002 ｜ [A B C D E                     ] <= [A B C D E                     ]
00003 ｜ [B C D E F                     ] <= [B C D E F                     ]
```

## Threshold: 0.600000 Depth: 2
```text
> PPJoin Threshold: 0.600000 <
Candidates: 0 -> 
Candidates: 1 -> 
Candidates: 2 -> 0 1
Candidates: 3 -> 0 1 2
> Inverted indices <
D -> (0,1) (3,2)
G -> (1,0)
A -> (1,1) (2,0)
C -> (0,0) (2,2) (3,1)
B -> (1,2) (2,1) (3,0)
> Verify Result <
3 <-> 0 => 0.600
3 <-> 2 => 0.667
```
```text
> PPJoin+ Threshold: 0.600000 <
Candidates: 0 -> 
Candidates: 1 -> 
Candidates: 2 -> 0
Candidates: 3 -> 0 1 2
> Inverted indices <
D -> (0,1) (3,2)
G -> (1,0)
A -> (1,1) (2,0)
C -> (0,0) (2,2) (3,1)
B -> (1,2) (2,1) (3,0)
> Verify Result <
3 <-> 0 => 0.600
3 <-> 2 => 0.667
```


## Threshold: 0.800000 Depth: 2
```text
> PPJoin Threshold: 0.800000 <
Candidates: 0 -> 
Candidates: 1 -> 
Candidates: 2 -> 1
Candidates: 3 -> 2
> Inverted indices <
G -> (1,0)
A -> (1,1) (2,0)
C -> (0,0) (3,1)
B -> (2,1) (3,0)
> Verify Result <
```
```text
> PPJoin+ Threshold: 0.800000 <
Candidates: 0 -> 
Candidates: 1 -> 
Candidates: 2 -> 
Candidates: 3 -> 
> Inverted indices <
G -> (1,0)
A -> (1,1) (2,0)
C -> (0,0) (3,1)
B -> (2,1) (3,0)
> Verify Result <
```

### Check All
```text
> Check  All <
0 <-> 1 => 0.143
0 <-> 2 => 0.333
0 <-> 3 => 0.600
1 <-> 2 => 0.429
1 <-> 3 => 0.429
2 <-> 3 => 0.667
```