# Distributor

# Running

The following properties are available:

```
net.jetblack.feedbus.distributor.HOST=0.0.0.0
net.jetblack.feedbus.distributor.PORT=30011
net.jetblack.feedbus.distributor.EVENT_QUEUE_CAPACITY=8096
net.jetblack.feedbus.distributor.WRITE_QUEUE_CAPACITY=8096
net.jetblack.feedbus.distributor.HEARTBEAT_INTERVAL=1000
```

The following arguments are useful.

```
-Djava.util.logging.SimpleFormatter.format="%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS %4$s %2$s %5$s%6$s%n"
-Dnet.jetblack.feedbus.distributor.HEARTBEAT_INTERVAL=0
```
