-- description: create things table
-- authoredAt: 1574519906696
-- up:

CREATE TABLE stocks (
  country text,
  location_id int,
  article_id int,
  category text, 
  product_name text,
  stock int,
  subcategory text, 
  PRIMARY KEY (article_id)
);

-- down:

DROP TABLE stocks;