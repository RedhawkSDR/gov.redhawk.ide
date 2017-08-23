# Plugin gov.redhawk.ide.sdr.ui

Provides a variety of functionality including:
* UI for the IDL library
* UI for the SDRROOT / SDR file system
* UI for domain/device manager launch
* Exporting to SDRROOT
* Property providers for browsing properties

## Property providers

The plugin provides for properties from 3 locations:
* Things installed in the target SDR
* Projects in the workspace
* Well-known properties

Well-known properties are drawn from an [extension point](schema/wellKnownProperties.exsd) that allows bundles to contribute a PRF file with the well-known properties.
