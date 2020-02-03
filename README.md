# ppjoinplus

A implementation of PPJoin+ algorithm by scala.

Example:
```text
> Initialization <
00000 ｜ [C D F                         ] <= [C D F                         ]
00001 ｜ [G A B E F                     ] <= [A B E F G                     ]
00002 ｜ [A B C D E                     ] <= [A B C D E                     ]
00003 ｜ [B C D E F                     ] <= [B C D E F                     ]
> PPJoin <
A[0 - C D|F]: 
A[1 - G A B|E F]: 
A[2 - A B C|D E]: (1,2) (0,1)
A[3 - B C D|E F]: (2,2) (1,1) (0,2)

###### Result ######
Inverted indices:
D -> (0,1) (3,2)
G -> (1,0)
A -> (1,1) (2,0)
C -> (0,0) (2,2) (3,1)
B -> (1,2) (2,1) (3,0)
Threshold: 0.600000. Verify:
3 - 2 => 0.667
3 - 0 => 0.600
#### All Result ####
×0->1:0.143  ×0->2:0.333  √0->3:0.600  
×1->2:0.429  ×1->3:0.429  
√2->3:0.667  
####################

```

> C. Xiao, W. Wang, X. Lin, and J. X. Yu. Efficient similarity joins for near duplicate detection. In WWW, pages 131–140, 2008.