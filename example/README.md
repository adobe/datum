# Examples

There are four examples provided here. Each demonstrates a different
method of bootstraping (server and client) along with a different way of
deserializing.

## Executing the examples

To start a application you must use the run `gradle` task as follows.

First, from a terminal window, start a server (in this case we are using
`protobuf` example)

``` bash
$ ./gradlew run -PappType=protobuf-server
```

Once the server starts, open a new terminal window. Then start client:

``` bash
$ ./gradlew run -PappType=protobuf-client
```

## `protobuf`

This example demonstrates how you can use protocol buffers to send data
between server and client.

A serializer or deserializer is not required for sending or receiving
`datum` messages in case of protocol buffers.

## Serializable

This is supposedly the fastest method for deserializing/serializing java
classes. It suffers from other drawbacks since the user has to write
more code than needed for other serialization types. Also, a user must
take care of handling empty/null values.

Just like `protobuf`, this method too does not need to have a custom
serializer or deserializer.

## JSON

Using `jackson`, this example shows how a third-party serialization
technique can be used with `datum`.

Since, `datum` does not support JSON out of the box, one needs to
provide a custom implementation for serialization and deserialization.

## Kryo

Kryo is one of the fast serialization library out there. It has the
advantage that it does not require much setup before you can start using
it. It does however forces tight coupling between server and client 
data-model. In most cases this is acceptable.

`kryo` is not supported out of the box, but, support can be easily added.
Please refer to the example to see how.
