**Approved for Public Release; Distribution Unlimited. Case Number 15-0338**

The author's affiliation with The MITRE Corporation is provided for identification 
purposes only, and is not intended to convey or imply MITRE's concurrence with, or support 
for, the positions, opinions or viewpoints expressed by the author.

Copyright 2014 The MITRE Corporation

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

This project contains content developed by The MITRE Corporation. If this 
code is used in a deployment or embedded within another project, it is 
requested that you send an email to opensource@mitre.org in order to let 
us know where this software is being used.

---

# Shamir's Secret Sharing Scheme

Java implementation of Shamir's Secret Sharing Scheme.  See the [wikipedia article](http://en.wikipedia.org/wiki/Shamir's_Secret_Sharing) for details on the algorithm.  This implementation uses a BigInteger mod-prime polynomial.

## Maven
```xml
<dependency>
    <groupId>org.mitre.secretsharing</groupId>
    <artifactId>secret-sharing-scheme</artifactId>
    <version>1.2.0</version>
</dependency>
```

Snapshot builds are available on Sonatype's OSS snapshot repository:
```xml
<dependency>
    <groupId>org.mitre.secretsharing</groupId>
    <artifactId>secret-sharing-scheme</artifactId>
    <version>1.3.0-SNAPSHOT</version>
</dependency>
```

## Usage

Splitting a secret `byte[]` into parts:
```java
byte[] secret = ...;
Part[] parts = Secrets.split(secret);
```

Reconstructing a secret `byte[]` from parts:
```java
Parts[] parts = ...;
byte[] secret = Secrets.join(parts);
```

Converting a `Part` to a formatted `String`:
```java
Part part = ...;
String formatted = PartFormats.currentStringFormat().format(part);
```

Parsing a `String` to a `Part`:
```java
String formatted = ...;
Part part = PartFormats.parse(formatted);
```

## Security

Shamir's Secret Sharing Scheme is an [information-theoretically secure](https://en.wikipedia.org/wiki/Information-theoretic_security) way to divide a secret into shareable parts.  For comparison, key-based cryptographic techniques are [computationally secure](https://en.wikipedia.org/wiki/Computational_hardness_assumption).

This Java implementation of the scheme has been reviewed by a MITRE cryptographer and found to be sound.  However, if you have any doubts, inspect the source code and compile it yourself.

This implementation of Shamir's Secret Sharing Scheme is not subject to export control laws in the United States of America.  Please check local laws concerning cryptography before downloading or using this library.

## Online Web Implementation

This implementation of the scheme [can be used in a web browser](http://secretsharing.org).  Use it at your own risk!  If your secret is truly secret then you shouldn't trust somebody else's webapp, and should instead download, inspect, and compile the code yourself.
