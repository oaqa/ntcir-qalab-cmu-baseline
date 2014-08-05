A baseline for NTCIR history exam task.
========================

To run do:

mvn compile exec:java -Dexec.mainClass=edu.cmu.lti.ntcir.qalab.runner.SimpleRunCPE

The input XML documents are in the sub-directories of the directory input. 
The name of the current input directory is specified in the configuration file of the collection reader. 
The current input folder is input/97-01-05-09/, which contains questions for four exam years. 
The file with the gold standard data is one of the sub-directories of the directory goldstandards. 
The name of this directory is also specified in the configuration file of the collection reader.
