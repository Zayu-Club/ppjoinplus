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
Threshold: 0.600000.
A[0 - C D|F]: 
A[1 - G A B|E F]: 
A[2 - A B C|D E]: (1,2) (0,1)
A[3 - B C D|E F]: (2,2) (1,1) (0,2)
```
```text
########################## Result ##########################
Inverted indices:
D -> (0,1) (3,2)
G -> (1,0)
A -> (1,1) (2,0)
C -> (0,0) (2,2) (3,1)
B -> (1,2) (2,1) (3,0)
Return set & Verify:
3 - 2 => 0.667
3 - 0 => 0.600
######################## Check  All ########################
0<->1:0.143×  0<->2:0.333×  0<->3:0.600√  
1<->2:0.429×  1<->3:0.429×  
2<->3:0.667√  
############################################################
```

> C. Xiao, W. Wang, X. Lin, and J. X. Yu. Efficient similarity joins for near duplicate detection. In WWW, pages 131–140, 2008.

## Similarity
 Similarity | Define
:-: | :- 
Jaccard | $J(x,y)= \vert\frac{x \cap y}{x \cup y}\vert$
Cosine | $C(x,y) = \frac{\vec{x} ⋅ \vec{y}}{\vert\vert \vec{x} \vert\vert ⋅ \vert\vert \vec{y} \vert\vert} = \frac{\sum_i x_i y_i}{\sqrt{\vert x \vert} ⋅ \sqrt{\vert y \vert}}$
Overlap | $O(x,y)= \vert{x \cap y}\vert$

## 例1 规格化Record
$D_x = "yes\ as\ soon\ as\ possible"$
$D_y = "as\ soon\ as\ possible\ please"$

统计：
Word | yes | as | soon | as1 | possible | please
:-: | :-: | :-: | :-: | :-: | :-: | :-:
Token | A | B | C | D | E | F
Doc. Freq. | 1 | 2 | 2 | 2 | 2 | 1

排序：
$x = [A,B,C,D,E]$
$y = [F,B,C,D,E]$

相似度：
$J(x,y) = \frac 4 6 = 0.67$
$C(x,y) = \frac{4}{\sqrt5 ⋅ \sqrt5} = 0.80$

## Lemma 1 - Prefix Filtering Principle
> 对于一个已规格化的Record集。若$O(x,y) \geq \alpha$，则$x$的$(|x|-\alpha+1)$前缀和$y$的$(|y|-\alpha+1)$前缀至少有一个相同token。
> $ \alpha = \lceil \frac{t}{1+t} (\vert{x}\vert + \vert{y}\vert)\rceil $

## 例2 
Jaccard similarity threshold $t=0.8$
> $w=[\underline{C},D,F]$
> $z=[\underline{G,A},B,E,F]$
> $y=[\underline{A,B},C,D,E]$
> $x=[\underline{B,C},D,E,F]$

$ prefix_x = \vert{x}\vert - \lceil t ⋅ \vert{x}\vert \rceil + 1 $
