# CS130YAS
YAAAAAS

## Database Setup
To set up mysql, run it on port 3306 (this is the default port, so simply installing and starting it should be sufficient).


To set up the database user and user permissions, run the following in mysql as root
``` 
CREATE USER 'cs130'@'localhost' IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON * . * TO 'cs130'@'localhost';
```

Then, log in as the cs130 user, entering the password "password" in the prompt.
```
mysql -u cs130 -p

mysql> CREATE DATABASE yas;
mysql> exit;
```

To initialize the database, run the following in project root.
```
mysql -u cs130 -p yas < src/main/resources/static/db/db_structure.sql
```
