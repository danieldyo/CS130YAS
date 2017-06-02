# CS130YAS
YAAAAAS

## Database Setup
To set up mysql, simply run it on port 3306 (this is the default port, so simply installing and starting it should be sufficient).


To set up the database user and user permissions, run the following in mysql as root
``` 
CREATE USER 'cs130'@'localhost' IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON * . * TO 'newuser'@'localhost';
```

To initialize the database, run the following in project root
```
mysql -u cs130 -p < src/main/resources/static/db/db_structure.sql
```
