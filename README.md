# scala-rest

The project is a REST API built in Scala with sbt.

The API logs stocks and it responds to the routes:

GET /stocks/3                               -> gets a stock
GET /stocks?country="US"&location_id=5      -> returns a list of stocks that have the properties
POST /stocks                                -> creates a stock from the json payload
DELETE /stocks/3                            -> deletes stock

the list of stocks is saved in memory into sets, but the created stocks get added to a Cassandra database called stocks
the application works with the 'dev' keyspace on cassandra, which needs to be created manually

there are some unit tests regarding the API and the cassandra integration