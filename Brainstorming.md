# DB
</details>

## Users
<details>

- **PK** : `uuid`
- Username : `string`
- Password : `string`
- Following : `set<User>`
- GivenTrustEvaluations : `set<TrustEvaluation>`

**(optional)**

- Followers : `set<User>` (profile page)
- Votes : `set<Vote>` (profile page)
- Posts : `set<Post>` (profile page)
- Answers : `set<Answer>` (profile page)
</details>

## Votes
<details>

- **PK** : `uuid`
- Voter : `User`
- IsUpvote : `bool`

**(optional)**

- Answer : `Answer` (to display user votes on his profile page)
</details>

## Posts
<details>

- **PK** : `uuid`
- Owner : `User`
- Title : `string`
- Content : `string`
- Answers : `set<Answer>`
- CreationDate : `date`
- Tags : `set<Tag>`

**(optional)**

- LastEditDate : `date`
</details>

## Answers
<details>

- **PK** : `uuid`
- Owner : `User`
- Content : `string`
- CreationDate : `date`
- Votes : `set<Vote>`
</details>

## Tags
<details>

- **PK** : `uuid`
- Name : `string`
**(optional)**

- Color : `string`
</details>


## TrustEvaluations
<details>

- **PK** : `uuid`
- Evaluated : `User`
- Note : `int` (\[-50, 50\])
</details>
