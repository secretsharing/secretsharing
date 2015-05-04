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

## Usage

Splitting a secret `byte[]` into parts:

    byte[] secret = ...;
    Part[] parts = Secrets.split(secret);

Reconstructing a secret `byte[]` from parts:

    Parts[] parts = ...;
    byte[] secret = Secrets.join(parts);

Converting a `Part` to a formatted `String`:

    Part part = ...;
    String formatted = PartFormats.currentStringFormat().format(part);

Parsing a `String` to a `Part`:

    String formatted = ...;
    Part part = PartFormats.parse(formatted);

## Security

Shamir's Secret Sharing Scheme is a fully secure way to divide a secret into shareable parts.  This Java implementation of the scheme has been reviewed by a MITRE cryptographer and found to be sound.  However, if you have any doubts, inspect the source code and compile it yourself.

This implementation of Shamir's Secret Sharing Scheme is not subject to export control laws in the United States of America.  Please check local laws concerning cryptography before downloading or using this library.