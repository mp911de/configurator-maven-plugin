Internal
============

1. Resolves source file (build-target or artifact)
2. creates target/configurator dir
3. copies source file to target/configurator
4. extracts file to target/configurator/extracted and extracts sub-dependencies (according to hierarchy
level, Jar = 1, War = 2, Ear = 3)
5. parse property files
6. perform injection of values (read file.template.xml, replace values, store to file.xml)
7. store processed output in target/configurator/work
8. reassemble outputfile into target/configurator
9. copy outputfile to sourcefilename-configured.packagingtype




