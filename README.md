# CONSTRUCTION WORKER FORUM BACKEND

##### using docker-compose:

To run only database in container, use this command:

```
docker-compose up -d db
```

And to stop:

```
docker-compose down
```

_**docker-compose.yml**_ also provides a simple way to start the database and the main application simultaneously.<br>
To run both containers for the first time, use this command:

```
docker-compose up --build -d
```

:heavy_exclamation_mark: Before build, please change configuration in `application.properties`<br>

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
