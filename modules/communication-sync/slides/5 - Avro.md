# **Apache Avro**

## **What is Avro?**

Apache Avro is a **data serialization system** designed for:

* **Compact** binary serialization
* **Fast** encoding/decoding
* **Schema-based** data exchange
* **Interoperability** across languages

Used heavily in **Kafka**, **event-driven architectures**, and **big-data pipelines** (Hadoop, Spark).

---

## **Key Features**

* **Schema-driven**: every Avro message is validated against a schema.
* **Compact binary format** → efficient for high-throughput systems.
* **Dynamic typing at runtime** (schema stored in data file or managed externally).
* **Language-agnostic** with official bindings (Java, Python, C++, Go, etc.).
* **Backward, forward, and full schema compatibility** support.
* **RPC support** (less used today thanks to gRPC, REST, GraphQL).

---

## **Schema Structure**

Avro schemas are written in **JSON** and define:

* `type` (record, enum, array, map, fixed, union, primitive)
* `fields` (name + type)
* optional defaults

```json
{
  "type": "record",
  "name": "User",
  "fields": [
    { "name": "id", "type": "string" },
    { "name": "age", "type": "int" },
    { "name": "isActive", "type": "boolean", "default": true }
  ]
}
```

---

## **Primitive Types**

* `null`
* `boolean`
* `int`, `long`, `float`, `double`
* `bytes`, `string`

## **Complex Types**

* `record`
* `enum`
* `array`
* `map`
* `fixed`
* `union` (often used for nullable fields: `["null", "string"]`)

---

## **How Avro Serializes**

1. **Writer schema** is used to encode the data.
2. **Reader schema** is used to decode the data.
3. If schemas differ, Avro applies **schema resolution rules**.

* Uses **variable-length encoding** for integers (zig-zag + varint).
* No field names included in the binary (unlike JSON).
* Only the raw values go into the payload → extremely compact.

---

## **Compatibility Types**

* **Backward compatible** → new readers can read old data.
* **Forward compatible** → old readers can read new data.
* **Full compatibility** → both directions.

## **Common Rules**

* Adding a new field → must have a **default**
* Removing a field → safe if it had a default
* Changing a type → safe only with compatible unions
* Renaming fields → NOT safe (Avro matches by name)

---

## **Why Avro is popular in Kafka**

* Messages are tiny → higher throughput
* Strong schema guarantees for producers/consumers
* Integrates with **Schema Registry** (Confluent, Apicurio, Redpanda)

**Typical Pattern**

* Producer writes message using **writer schema**
* Schema is registered in **Schema Registry**
* Consumer fetches schema ID + binary payload
* Registry provides the **reader schema**

---

## **Schema Registry**

* Store and version Avro schemas
* Enforce compatibility rules (backward/forward/full)
* Provide schema IDs for Kafka messages
* Serve schemas via REST API

## **Payload Layout in Kafka**

```
[ magic byte | schema id | avro binary ]
```

Magic byte = 0 → identifies Schema Registry payload format.

---

## **Avro in Java (Spring Boot)**

```java
Schema schema = new Schema.Parser().parse(Resources.getResource("user.avsc"));
GenericRecord user = new GenericData.Record(schema);
user.put("id", "abc123");
user.put("age", 30);

ByteArrayOutputStream out = new ByteArrayOutputStream();
BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(out, null);
DatumWriter<GenericRecord> writer = new GenericDatumWriter<>(schema);
writer.write(user, encoder);
encoder.flush();
```

```java
DatumReader<GenericRecord> reader = new GenericDatumReader<>(schema);
BinaryDecoder decoder = DecoderFactory.get().binaryDecoder(bytes, null);
GenericRecord user = reader.read(null, decoder);
```

---

## **When to Use Avro**

* High-throughput messaging (Kafka, Pulsar)
* Event-driven microservices
* Streaming analytics pipelines
* Schema-controlled data interchange
* Storage formats (Parquet uses Avro internally)

**Not ideal for**

* Human-readable APIs → JSON/REST is better
* Very complex typed RPC → gRPC or GraphQL preferred
* Small systems without schema evolution needs

