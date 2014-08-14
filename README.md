NTCIR QALab CMU Baseline 
=========
## (English Center Eexam task)


This repository contains an [UIMA] based modular question answering (QA)
pipeline that automatically answers multiple-choice questions for the entrance
exams in English about world history, which provides an end-to-end baseline system for
[NTCIR QALab-1] challenge (stage 1). 


Contents of this repository
--------------

*   Pipeline source code and UIMA descriptors of each component: 
  * XML collection readers
  - question analysis annotator (also called hypothesis generator)
  - document retrieval based evidence collector
  - rule based answer selection and evaluation CAS consumers

*   A [specification document](https://github.com/oaqa/ntcir-qalab-cmu-baseline/blob/master/doc/baseline_specification.pdf?raw=true) describes the overall system architecture, UIMA type systems, and each pipeline phases.

*   Example intermedia data (in UIMA XMI format) from every steps of the baseline pipeline.

*   UIMA-AS client descriptors that calls baseline annotator UIMA-AS services hosted in CMU servers.

###Folder Structures

The overall folder structure:
````
ntcir-qalab-cmu-baseline/
├── data/
│   └── baseline_xmi/   /* intermediate xmi files of baseline steps */
├── doc/                /* brief document describing pipeline phases */
├── goldstandard/       /* gold standard xml files for each year and combined */
│   ├── 1997/
│   ├── ...
│   └── 97-01-05-09/
├── input/              /* input xml files for each year and combined */
│   ├── 1997/
│   ├── ...
│   └── 97-01-05-09/
├── solr/               /* (optional) solr configuration files to create wikipedia index */
└── src/                /* source code and UIMA descriptors */
````

The input XML documents are in the sub-directories of the directory input. 
The name of the current input directory is specified in the configuration file of the collection reader. 
The current input folder is input/97-01-05-09/, which contains questions for four exam years. 
The file with the gold standard data is one of the sub-directories of the directory gold standards. 
The name of this directory is also specified in the configuration file of the collection reader.


Installation
--------------

The CMU baseline system requires JDK, git, and maven. Please refer to the [prerequisites](https://github.com/oaqa/ntcir-qalab-cmu-baseline/blob/master/README_Prerequisites.md) document regarding installing and setting up them.
It has been tested on Linux and Mac OS.
Other platforms should work, but have not been significantly tested.


To download and build the baseline:
```sh
git clone git@github.com:oaqa/ntcir-qalab-cmu-baseline.git 
cd ntcir-qalab-cmu-baseline
mvn install
```

To run the baseline pipeline from command line:
```sh
mvn compile exec:java -Dexec.mainClass=edu.cmu.lti.ntcir.qalab.runner.SimpleRunCPE
```

Version
----

1.0

License
----

Apache License, Version 2.0

[UIMA]:http://uima.apache.org
[NTCIR QALab-1]:ntcir.nii.ac.jp/QALab/
