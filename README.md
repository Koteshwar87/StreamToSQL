
Start and Stop Docker containers:

docker-compose down -v

docker-compose up -d




Start a Kafka broker in Docker:

**docker run -d --name broker -p 9092:9092 apache/kafka:latest**

Open a shell in the broker container:
**docker exec --workdir /opt/kafka/bin/ -it broker sh**
A topic is a logical grouping of events in Kafka. From inside the container, create a topic called test-topic:

**./kafka-topics.sh --bootstrap-server localhost:9092 --create --topic order-items-topic**
Write two string events into the test-topic topic using the console producer that ships with Kafka:

**./kafka-console-producer.sh --bootstrap-server localhost:9092 --topic order-items-topic**
This command will wait for input at a > prompt. Enter hello, press Enter, then world, and press Enter again. Enter Ctrl+C to exit the console producer.

Now read the events in the test-topic topic from the beginning of the log:

**./kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic order-items-topic --from-beginning**
You will see the two strings that you previously produced:

hello
world
The consumer will continue to run until you exit out of it by entering Ctrl+C.

When you are finished, stop and remove the container by running the following command on your host machine:
**docker rm -f broker**



Start Postgres Database in Docker:

**docker run --name some-postgres -e POSTGRES_PASSWORD=mysecretpassword -p 5432:5432 -d postgres**





