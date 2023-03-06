# DB

## Users

- **PK** : `uuid`
- Username : `string`
- Password : `string`
- Following : `set<User>`
- GivenTrustEvaluations : `set<TrustEvaluation>`

**(optional)**

- Followers : `set<User>` (profile page)
- Votes : `set<Vote>` (profile page)
- Posts : `set<Post>` (profile page)
- Comments : `set<Comment>` (profile page)

## Votes

- **PK** : `uuid`
- Voter : `User`
- IsUpvote : `bool`

**(optional)**

- Comment : `Comment` (to display user votes on his profile page)

## Posts

- **PK** : `uuid`
- Owner : `User`
- Title : `string`
- Content : `string`
- Comments : `set<Comment>`
- CreationDate : `date`
- Tags : `set<Tag>`

**(optional)**

- LastEditDate : `date`

## Comments

- **PK** : `uuid`
- Owner : `User`
- Content : `string`
- CreationDate : `date`
- Votes : `set<Vote>`

## Tags

- **PK** : `uuid`
- Name : `string`

**(optional)**

- Color : `string`

## TrustEvaluations

- **PK** : `uuid`
- Evaluated : `User`
- Note : `int` (\[-50, 50\])
