# PhageUniqueSeq

Mycobacteriophage Unique Sequence Finder

A java program to aid my research in finding unique primers for mycobacteriophages
This program is currently under development for my research on mycobacteriophages.
It is setup to be pulled into IntelliJ Idea using VCS.
The program's eventual design will be to create unique sequences of a certain size for
all mycobacteriophage clusters for the purpose of using these as biological
identifiers of cluster status.

The program has a simple gui and requires an internet connection. It can be compiled
into a .jar file using the included manifest. Phage and common analysis take a few
minutes to complete. Unique analysis however takes more time and is very CPU intensive.
I would recommend having a good CPU with good ventilation before attempting. I performed
these computations using a Dell Inspiron 15R-SE with an Intel i7 (8 cores after hyperthreading),
8 gbs of RAM, and a SSD. There may be bugs associated with the download of the fasta files
due to errors in the phagelist from phagesdb.org. My current solution to this problem is to
hardcode the fixes. I will do this with every release, however, until a new release the
bug will persist.

This project was created at Western Kentucky University with the help of Dr. Claire Rinehart,
the WKU Bioinformatics and Information Science Center, and the WKU High Performance Computing
Center.

If you have any questions about the program or the project please email me at
charles.gregory940@topper.wku.edu.
