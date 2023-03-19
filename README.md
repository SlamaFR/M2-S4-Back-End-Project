# UGE-Overflow

## Members

- Lorris CREANTOR (Lead dev)
- Jimmy TEILLARD
- Irwin MADET

## Build

With gradle wrapper:

```bash
$ ./gradlew bootJar
```

The jar is in `build/libs/` folder.
You can run it with:

```bash
$ java -jar build/libs/uge-overflow-0.1.0.jar
```

## Environment variables

- `DB_URL` : Database URL. (default: `jdbc:h2:file:./data/ugeoverflowdb`)
- `DB_DRIVER` : Database driver. (default: `org.h2.Driver`)
- `DB_USER` : Database username. (default: `admin`)
- `DB_PASSWORD` : Database password. (default: `admin`)
- `DB_DIALECT` : Database vendor dialect. (default: `org.hibernate.dialect.H2Dialect`)
- `DB_CONSOLE_ENABLED` : Enable H2 console. (default: `false`)
- `SESSION_PURGE_RATE` : Session purge rate in milliseconds. (default: `300000`)
- `ACCESS_TOKEN_LIFETIME` : Access token lifetime in milliseconds. (default: `3600000`)
- `REFRESH_TOKEN_LIFETIME` : Refresh token lifetime in milliseconds. (default: `2592000000`)
- `CIRCLE_MAX_DEPTH` : Maximum depth of a circle of followed users. This is used to compute answers order. (default: `3`)

## Question sorting algorithm

See [question_sorting_formula.md](question_sorting_formula.md)

## API

A Postman collection is available [here](UGEOverflow.postman_collection.json).
