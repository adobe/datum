# datum

Provides an API that allows users to easily build data streaming
applications that are fault tolerant, easy on resources and rely on
non-blocking I/O.

## Deprecated

This project is no longer maintained and is in an archived state. If you wish to revive the project, please consider forking it.

<!-- markdown-toc start - Don't edit this section. Run M-x markdown-toc/generate-toc again -->
**Table of Contents**

- [datum](#datum)
    - [Background](#background)
    - [Quickstart](#quickstart)
    - [Description](#description)
    - [Dependencies](#dependencies)
        - [`netty`](#netty)
        - [`protobuf`](#protobuf)
    - [Usage](#usage)
        - [Setup `ConnectionSettings`](#setup-connectionsettings)
        - [Bootstrapping `datum-server`](#bootstrapping-datum-server)
        - [Bootstrapping `datum-client`](#bootstrapping-datum-client)
    - [Flow Control](#flow-control)
    - [Multi-threading](#multi-threading)
    - [Client side support](#client-side-support)
    - [Examples](#examples)
    - [Build](#build)
        - [Testing](#testing)
        - [Benchmarking](#benchmarking)
        - [Release & Snapshot](#release--snapshot)
    - [Contributing](#contributing)

<!-- markdown-toc end -->

## Background

Many web services that exist today use multple data stores like `hbase`,
`hdfs`, `cassandra` and `mysql`. There could be few external services
that rely on these data stores. Usually, there would be a rest API that
would convert a java object back and forth between either `json` or some
other representation. Another example would be a scenario where one
service periodically scans a remote hbase to do some analytics.

Due to this dependency between services on each other's data stores, we
end up with a tight coupling between these services. If you owned the
host service, consider how hard it will be for you to move data from
`hbase` to `cassandra` without breaking any of the dependent services.

Furthermore, when setting up these connections in a secure fashion it is
not straightforward to connect to multiple datastore from an external
service because of the difference in client side protocol (eg. hbase and
mysql). This problem only worsens if you bring in development
environment based off of docker containers.

Lastly, since, the size of data files in standard data store is anywhere
between a few KBs to several GBs. To not overload memory buffer this
data transfer to external services needs to happen in a streaming
fashion.

For the reasons mentioned above and to have some control over access to
data, we realized that this design pattern of exposing our data stores
to external services needed some rethinking. In comes `datum` to solve
these problems.

## Quickstart

Include the build dependency:

``` groovy
repositories {
  mavenCentral()
  jcenter()
}

dependencies {
  compile 'com.adobe.datum:datum-server:0.0.1-SNAPSHOT'
  // similarly, you can add dependency on datum-client in case you are writing a client
}
```

Then bootstrap and start `datum-server`:

``` java
public class MyServerDriver {
  public static void main(String[] args) throws InterruptedException {
    new DatumServer().addDownloadHandler(new FooDownloadHandler())
                     .addUploadHandler(new BarUploadHandler())
                     .start(ConnectionSettings.getDefaultSettings(), true);
  }
}
```

Now, write `datum-client` to connect to the server instance above:

``` java
public final class MyDatumClient {

  private final DatumClient client;

  ExampleClient(String host) {
    ConnectionSettings connectionSettings = ConnectionSettings.getDefaultSettings();
    connectionSettings.setHost(host);
    client = new DatumClient(connectionSettings);
  }

  public void downloadFoo(/* params */, DatumConsumer<Foo> consumer)
      throws DatumClientException {
    FooDownloadRequest request = new FooDownloadRequest(/* params */);
    client.sendDownloadRequest(request, Foo.class, consumer);
  }

  public void uploadBar(/* params */, DatumSupplier<Optional<Bar>> supplier)
      throws DatumClientException {
    BarRequest request = new BarRequest(/* params */);
    client.sendUploadRequest(request, Bar.class, supplier);
  }
}
```

This `datum-client` allows downloading objects of type `Foo` and
uploading objects of type `Bar`.  It is ready to be used in your driver
class.

## Description

In short `datum` provides a API to easily build a data streaming
application which supports non-blocking asynchronous I/O by the use of
`netty`. Users of `datum` can use this API to bootstrap a `server` and a
`client`.

For communication, it uses
[`protobuf`](https://developers.google.com/protocol-buffers/?hl=en).
`protobuf` is a mechanism to serialize structured data. This makes datum
language and platform neutral. As explained in later sections, we can
write a `datum-client` in languages other than `java`.

The biggest advantage of `datum` is its non-blocking nature for
performing I/O. This enables it to handle multiple data streams at the
same time in parallel.

The users of `datum` provide a custom `consumer` (for downloading) and
`supplier` (for uploading). By doing this, datum takes control of data
flow which allows it to handle complex problems like `Congestion
Control`. This way `datum` follows the "Hollywood Principle" - "dont
call us, we will call you".

## Dependencies

As indicated above, `datum` uses `netty` for data transfer and
`protobuf` for data serialization/deserialization.

### `netty`

When we first started to build `datum` we ended up using `spring` for
data communication over `HTTP`. As it turns out it was not very
straightforward to make `spring` do true data streaming, specially over
`HTTP`. Furthermore, this concocted data-streaming was not done in a
non-blocking manner.

So, we started to look into `netty` as an alternative. After some
research and reading a couple of books it was apparent that:

1. Non-Blocking I/O is not easy to achieve as it requires expert
   knowledge of concurrent systems and async socket I/O.
2. `netty` provided exactly what we wanted with minimal effort resulting
   in highly readable code.

### `protobuf`

This was also an informed decision. Among the race initially, we had:

- `Apache avro`: Does not work for streaming out-of-the-box since the
  schema is sent with every data packet.

- `Apache Thrift`: It was made to do scalable cross-language RPC not
  data streaming.

- `Protocol Buffers`: The choice was clear after looking over the
  options in detail. We ended up with version `2.5` of the library.

## Usage

There are two parts to `datum` a `server`-side and a `client`-side. Both
need to be bootstrapped separately.

### Setup `ConnectionSettings`

This is needed by both (server and client). So, lets set it up first.

In most cases, the defaults values for settings should work. A instance
with all default values can be obtained as:

``` java
ConnectionSettings.getDefaultSettings()
```

You can also selectively override any parameters you want:

``` java
ConnectionSettings connectionSettings = ConnectionSettings.getDefaultSettings();
connectionSettings.setHost(System.getProperty("datum.host", "192.168.1.10"));
```

A `ConnectionSettings` object has several properties that can be
controlled:

|name|description|default value|
|:-:|:-:|:-:|
|host|The host where server is running (has no effect on the server)|`127.0.0.1`|
|port|The port where server is running (controls which port server binds itself to)|`8643`|
|connectTimeoutMillis|Connection timeout|`5` seconds
|writeBufferHighWaterMark|Stop writing if write buffer has reached this size|`64` KB
|writeBufferLowWaterMark|Start writing again if write buffer reaches below this size|`16` KB

### Bootstrapping `datum-server`

Since, we can only send 0s and 1s digitally (which translates to bytes),
we need to serialize data to a series of bytes and deserialize back to
meaningful information on the other end. For this, any serialization
library can be used. For this example, we will stick to `protobuf`.

Before starting up a server instance you will need to establish
`protobuf` schema. For the sake of this example lets assume following
schema:

``` protobuf
syntax = "proto2";

package com.example.api

option java_outer_classname = "ExampleProto";

message ItemRequestProto {
  required int64 id = 1;
}

message ResultRequestProto {
  required int64 timestamp = 1;
  required string category = 2;
}

message ItemProto {
  required string code = 1;
  required int64 value = 2;
}

message ResultProto {
  required int64 startTime = 1;
  required int64 endTime = 2;
}
```

With this `protobuf` schema we can start to build a communication
protocol using datum. In this example, the requirements are to download
many `ItemProto` and upload many `ResultProto`.

First, you will need to implement request handlers: one for handling
download requests for `ItemProto` and the other for handling upload
requests for `ResultProto`. Lets call these: `ItemDownloadHandler` and
`ResultUploadHandler` respectively.

Then you can start your server:

``` java
new DatumServer(ConnectionSettings.getDefaultSettings())
    .addDownloadHandler(new ItemDownloadHandler())
    .addUploadHandler(new ResultUploadHandler())
    .start(true);
```

The `DatumServer.addDownloadHandler` and `DatumServer.addUploadHandler`
methods allow an optional `Callback` parameter which can be used to get
notified when a request completes or errors out.

### Bootstrapping `datum-client`

> In this section, we will present the `java` based client only. In future
> iterations, we will add other types too.

First, lets create an instance of `DatumClient` by letting it connect to
the server we started above:

``` java
ConnectionSettings connectionSettings = ConnectionSettings.getDefaultSettings();
connectionSettings.setHost(System.getProperty("datum.host", "0.0.0.0"));
DatumClient datumClient = new DatumClient(connectionSettings);
```

After obtaining an instance of `DatumClient` you can send a download
request like this:

``` java
ItemRequestProto request = buildDownloadRequest();
datumClient.sendDownloadRequest(request, ItemProto.class, itemConsumer);
```

This will send a non-blocking call to the server to ask for `ItemProto`
as per the `ItemRequestProto` instance. The `datum-server` will then
start sending `ItemProto` asynchronously. For each `ItemProto`
received, the receiver will call `itemConsumer`.

In a similar fashion, you can send an upload call like this:

``` java
ResultRequestProto request = buildUploadRequest();
datumCient.sendUploadRequest(request, resultSupplier);
```

The client will make a connection to the `datum-server` in a similar way
as in the download request above. After the connection gets established,
the client will stream `ResultProto` to the server. The will continue
until the `resultSupplier` returns a `Optional.empty()`.

## Flow Control

`datum` will make sure not to overwhelm a slow receiver if the sender is
sending messages faster than the receiver can consume them. There are
various reasons when this may happen:

- Receiver window is too small
- Receiver is not consuming the messages in a non-blocking fashion

Care must be taken to make sure that a message consumer does not do any
time-consuming operations on the calling thread. Doing so will cause the
receiver to get blocked which will fill up the receive window on the
consumer's end. Eventually, an erroneous message consumer will disrupt
operations of other receivers (and even senders).

Message suppliers should observe the same care to not block the calling
thread. A blocking supplier will consume resources from the `netty`
event loop which may hamper communication for other requests.

## Multi-threading

There are five types of objects you need to use `datum`:

1. UploadHandler
2. DownloadHandler
3. Payload consumer
4. Payload supplier
5. Serializer/deserializer for every type of payload (if the payload is
   not a `protobuf` message or extends from `java.io.Serializable`)

Out of these, 1 and 2 (upload and download handlers) are the only things
that get shared between multiple requests. This means that in your
implementations you should make sure that there are no potential bugs
due to multiple threads calling them. As a side note: it is okay to make
these blocking (as long as it is within the timeout specified in
`ConnectionSettings`) since, these handlers will only handle the request
once. Once the request is handled they return a consumer or a supplier.

Every communication thread gets its own consumer (or supplier). So, you
won't have to worry about synchronization.

## Client side support

Since, all communication in `datum` is done using `protobuf` messages we
get advantage of polygot programming without much effort. All `datum` is
responsible for is deciding on a transmission protocol that is
compatible with the client stack.

A client based on java is shipped alongside `datum-server`. Please refer
to the documentation above to see how it can be used.

We may be adding clients for: `javascript`, `ruby` and `python` at some
point in the future.

## Examples

Several examples have been provided to demonstrate different usages
`datum-server` and `datum-client`. Current examples show four types of
serialization mechanisms:

- `protobuf`
- Java's manual serialization using `java.io.Serializable` interface
- `json` serialization using [`jackson`](https://github.com/FasterXML/jackson-core)
- Efficient serialization using [`kryo`](https://github.com/EsotericSoftware/kryo)

## Build

With every commit a `jenkins` based CI environment runs all checks to
make sure the quality of code stays up to the mark. To run these checks
locally:

``` bash
$ ./gradlew check
```

### Testing

All testing is performed using the
[`spock`](http://spockframework.github.io/spock/docs/1.0/index.html)
framework. To execute:

``` bash
$ ./gradlew test
```

All tests are also run when the `check` task is executed.

### Benchmarking

`datum` harnesses
[`jmh`](http://openjdk.java.net/projects/code-tools/jmh/) to run
nano/micro/milli/macro benchmarks. To execute these:

``` bash
$ ./gradlew jmh
```

### Release & Snapshot

There are two gradle tasks configured to publish artifacts: `release`
and `snapshot`. Refer to
[`gradle-release-plugin`](https://github.com/anshulverma/gradle-release-plugin)
for details on how these tasks are configured.

The `release` task will tag the repository based on the release
type. So, it must be used once all the checks have passed. It can be
executed by:

``` bash
$ ./gradlew release
```

## Contributing

Contributions are always welcome. Check out our [contribution guidelines](CONTRIBUTING.md) for more information.

