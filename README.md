# feedbus

Work in progress.

This is a broker based message bus written in Java with clients in multiple languages.

It supports broadcast and select feed architectures.


## Architecture

This is a publish subscribe broker based message bus.

At the heart of the system is the message broker server, or *distributor*. Clients
connect to the distributor, then send and receive messages.

The following messages are supported:

- Subscribe
- Publish
- Notify
- Send

### Messages

#### Subscribe

A client may add or remove a subscription. When a client has subscribed
it will receive updates whenever the target of it's subscription changes.

When a client subscribes to data it sends a message to the distributor with the following content.

- Feed
- Topic
- IsAdd

The *feed* is a string which can be used as a namespace. For example financial data from the London
Stock Exchange may have a feed "LSE".

The topic is a string which identifies the item of data within the feed. For example Barclays PLC might
be represented as the topic "BARC".

The *IsAdd* is a boolean indicating if the subscription is being added or removed.

#### Publish

When a client publishes data, the data will be forwarded to all subscribers.

The publication has the following content:

- Feed
- Topic
- IsImage
- Data

The *feed* and *topic* have the same meanings as given above.

The *IsImage* is a boolean which indicates whether the data sent was complete, or a subset.

The *Data* is an array of bytes.

#### Notify

A client may request notifications of subscriptions to data.

The notification request has the following content:

- Feed
- IsAdd

The *feed* has the same meaning given above.

The *IsAdd* is a boolean indicating whether the notification is being added or removed.

When another client adds or removes a subscription to a topic on this feed the
subscription is forwarded to this client with the following content.

- ClientId
- Feed
- Topic
- IsAdd

The *feed*, *topic*, and *isAdd* have the same meanings as given above.

The *ClientId* is a string which identifies the client made the subscription request.

#### Send

A client can send data to a specific client.

The send message has the following content.

- ClientId
- Feed
- Topic
- IsImage
- Data

The fields have the same meanins as described above.

### Feed types

A message bus is typically described as *broadcast* or *select feed*. Both types are supported.

#### Broadcast feed

In a broadcast feed a data provided publishes all data, regardless of whether any clients are listening.
An example of this is a feed from an exchange.

This is a simple architecture. The data providers send *publish* messages and the clients request *subscriptions*.

#### Select feed

A select feed only publishes data when clients have subscribed.

This is a more complicated arcitecture. The clients still request *subscriptions*, but the data
provider is more structured.

It first requests notifications for it's feed with a *notify* request.

When it is notified of a subscription it first sends a complete set of data using the *clientId* it received
with the forwarded subscription.

Subsequently any data received on the topic is published.

When notified of a subscription removal the data provider checks if any subscriptions remain, and
stops publishing if no one is listening.


