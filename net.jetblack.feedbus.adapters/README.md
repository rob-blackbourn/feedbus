# Adapters

## Running

The following properties are available:

```
net.jetblack.feedbus.adapters.HOST=localhost
net.jetblack.feedbus.adapters.PORT=30011
net.jetblack.feedbus.adapters.WRITE_QUEUE_CAPACITY=8096
net.jetblack.feedbus.adapters.HEARTBEAT_INTERVAL=1000
net.jetblack.feedbus.adapters.SERIALIZER=net.jetblack.util.io.StringSerializer

```

The following arguments are useful.

```
-Djava.util.logging.SimpleFormatter.format="%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS %4$s %2$s %5$s%6$s%n"
```

## Things to do

### Handle heartbeats

We could use Socket.setSoTimeout and catch SocketTimeoutException in the read loop.