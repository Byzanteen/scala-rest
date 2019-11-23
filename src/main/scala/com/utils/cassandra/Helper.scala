package com.example

import com.datastax.driver.core._

object Helper {

  def createSessionAndInitKeyspace(uri: CassandraConnectionUri,
                                   defaultConsistencyLevel: ConsistencyLevel = QueryOptions.DEFAULT_CONSISTENCY_LEVEL) = {
    val cluster = new Cluster.Builder().
      addContactPoints(uri.hosts.toArray: _*).
      withPort(uri.port).
      withQueryOptions(new QueryOptions().setConsistencyLevel(defaultConsistencyLevel)).build

    val session = cluster.connect
    session.execute(s"USE ${uri.keyspace}")
    session
  }

  def createStock(stock: Stock, session: com.datastax.driver.core.Session) = {
    val stockFormat = s"'${stock.country}', ${stock.location_id}, ${stock.article_id}, '${stock.category}', '${stock.product_name}', ${stock.stock}, '${stock.subcategory}'"
    
    val query = s"INSERT INTO stocks (country, location_id, article_id, category, product_name, stock, subcategory) VALUES (${stockFormat});"  
    session.execute(query);
    session
  }

}