Internal
============

1. Resolves source file (build-target or artifact)
2. creates target/configurator dir
3. extracts file to target/configurator/work and extracts sub-dependencies
4. parse property files
5. perform injection of values (read file.template.xml, replace values, store to file.xml)
6. reassemble outputfile into target/configurator to sourcefilename.packagingtype




