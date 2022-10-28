# CONSTRUCTION WORKER FORUM BACKEND

##### using docker-compose:

To run only MySQL database in container, use this command:

```
docker-compose up -d db
```

And to stop:

```
docker-compose down
```

To run MySQL along with MongoDb in container, use this command:

```
docker-compose -f docker-compose.yaml up -d
```

And to stop:

```
docker-compose down
```

