# CONSTRUCTION WORKER FORUM BACKEND

##### using docker-compose:

_**docker-compose.yml**_ provides a simple way to start the database and the main application simultaneously.<br>
To run both containers for the first time, use this command:

```
docker-compose up --build -d
```

Running containers (construction_worker_forum_mysql_db and construction_worker_forum_app) can be stopped by executing
this command:

```
docker-compose stop
```

To restart stopped containers, use the following command:

```
docker-compose start
```

To rebuild the image of container, use again this command:

```
docker-compose up --build -d
```

Then use the following command to run the containers again:

```
docker-compose up -d
```
