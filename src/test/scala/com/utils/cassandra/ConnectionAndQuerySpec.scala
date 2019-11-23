package common.utils.cassandra

import com.datastax.driver.core.querybuilder.QueryBuilder
import com.datastax.driver.core.querybuilder.QueryBuilder._
import org.scalatest.{Matchers, FunSpec}

class ConnectionAndQuerySpec extends FunSpec with Matchers {
  
  describe("Connecting and querying a Cassandra database") {
  
    it("should just work") {
      val uri = CassandraConnectionUri("cassandra://localhost:9042/dev")
      val session = Helper.createSessionAndInitKeyspace(uri)
      
      session.execute("INSERT INTO stocks (country, location_id, article_id, category, product_name, stock, subcategory) VALUES ('RO', 1, 1, 'drinks', 'Cola', 200, 'soda');")  

      val selectStmt = select().column("")
        .from("stocks")
        .where(QueryBuilder.eq("article_id", 1))
        .limit(1)
      
      val resultSet = session.execute(selectStmt)
      val row = resultSet.one()

      row.getString("product_name") should be("Cola")
      session.execute("TRUNCATE things;")
    }
  
  }

}