## INPUT PARAMETERS

- `max_depth`: the maximum depth of circles in the graph
- `max_trust`: the maximum trust that a user can have

## FORMULA VARIABLES

- `trust` in [1, `max_trust`] : the trust of a user

- `vote` in {-1, 1} : the vote of the user on an answer

- `circle_depth` in [0, `max_depth` - 1] : the depth of the current circle in the graph originating from user requesting
  the
  question page

- `weighting` in [0, 1] : the weighting of the trust of the current user using the trust of its ancestors in the graph

<details>
<summary>Weighting formula</summary>

- `u` the current User,
- `n` the depth of the current user in the graph
- `avgPev(x)` the function applying x to all the users following u and returning the average of the results. The series
  of the weightings is defined as follows:

```
wt(x, 0) = 1
wt(x, n) = avgPrev(W), where W = wt(n - 1) * (trust(n - 1) / max_trust)
```

We compute the average of the weightings of all the users following x multiplied by the ratio of their trust and the
maximum trust.

#### Note

The formula is applied on using the lowest depth for the given user. For example with the graph:

A -> D\
B -> D\
C -> E -> D

We will consider the depth of D as 1, and not 2 and therefore only use A and B to compute the average of the weightings.

### Motivations

This parameter aims at reducing the impact of the trust of the current user on the final score of the answer, by using
the trust of the previous users in the graph.

Example:

- `A`: the user who requests the question page
- `B`: a user followed by `A` with a trust of **2**
- `C`: a user followed by `B` with a trust of **20**

Without weighting, the trust of `C` would have a relatively big impact on the final score of the answer due to the trust
of **20** given by `B`. However, B has a relatively low trust of **2** (**2**/**20**), so the impact of the trust of `C`
should be reduced relative to the trust of `B`. This is what the weighing parameter is for.
</details>

## SCORE FORMULA

The value of the score given by a user `X` with a depth `n` in the graph is computed as follows:

```
vote * max(1, C)
```

With `C`:

```
            max_depth - n
trust(X) * --------------- * weighting(n)
              max_depth
```
