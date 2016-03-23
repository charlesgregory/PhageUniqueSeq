# PhageUniqueSeq

Mycobacteriophage Unique Sequence Finder

A java program to aid my research in finding unique primers for mycobacteriophages
This program is currently under development for my research on mycobacteriophages.
It is setup to be pulled into IntelliJ Idea using VCS.
The program's eventual design will be to create unique sequences of a certain size for
all mycobacteriophage clusters for the purpose of using these as biological
identifiers of cluster status.

The program has changed vastly from the last version. It now runs from command prompt
and saves all data to a file HSQL database. This is extremely RAM intensive. I am using
WKU's HPCC to run the program. 32 GB of RAM should be suitable but a lot is necessary
as the entire database is stored in memory when it is running. There may be bugs associated
with the download of the fasta files due to errors in the phagelist from phagesdb.org.
My current solution to this problem is to hardcode the fixes. I will do this with every
release, however, until a new release the bug will persist.

This project was created at Western Kentucky University with the help of Dr. Claire Rinehart,
the WKU Bioinformatics and Information Science Center, and the WKU High Performance Computing
Center. I also use source code, compiled libraries, and converted code from the primer programs
Primer3 and OligoCalc.

Primer3:
    Untergasser A, Cutcutache I, Koressaar T, Ye J, Faircloth BC, Remm M, Rozen SG (2012)
    Primer3 - new capabilities and interfaces. Nucleic Acids Research 40(15):e115 Koressaar T,
    Remm M (2007) Enhancements and modifications of primer design program
    Primer3 Bioinformatics 23(10):1289-91

OligoCalc:
    Kibbe WA. 'OligoCalc: an online oligonucleotide properties calculator'.
    (2007) Nucleic Acids Res. 35(webserver issue): May 25

If you have any questions about the program or the project please email me at
charles.gregory940@topper.wku.edu.
