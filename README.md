# Habitector

Habitector is a Java package containing routines for generating input/output data for/from [NERsuite](http://nersuite.nlplab.org), a C/C++ implementation of conditional random fields (CRFs) specifically for named entity recognition (NER).

## Getting Started

Users should have a working NERsuite [installation](http://nersuite.nlplab.org/installation_guide.html) and are advised to familiarise themselves with both its [basic](http://nersuite.nlplab.org/basic_usage.html) and [advanced usage](http://nersuite.nlplab.org/advanced_usage.html).

It is worth noting, however, that Habitector performs its own corpus pre-processing based on the following tools: LingPipe for sentence splitting, the Stanford Tokeniser for tokenisation and the GENIA Tagger for lemmatisation, part-of-speech (POS) and chunk tagging. 

NERsuite natively supports the Begin-Inside-Outside (BIO) data format. Habitector assumes though that the user wishes to make use of corpora in the [BioNLP Shared Task (BioNLP ST) format](http://2011.bionlp-st.org/home/file-formats) which is also the representation taken by the [brat annotation tool](http://brat.nlplab.org/).

### Prerequisites

* [Java Development Kit](http://www.oracle.com/technetwork/java/javase/overview/index.html) - Development environment
* [Maven](https://maven.apache.org/) - Framework for managing dependencies
* [GENIA Tagger models](http://sourceforge.net/projects/jeniatagger/files/models.zip/download) - Models used by [GENIA Tagger](http://www.nactem.ac.uk/GENIA/tagger)
* [jeniatagger](https://github.com/juanmirocks/jeniatagger) - Java wrapper for GENIA Tagger

### Installing

Download jeniatagger

```
git clone https://github.com/juanmirocks/jeniatagger.git
```

Install jeniatagger as a third-party library in your local Maven repository

```
mvn install:install-file -Dfile=<path/to/jeniatagger-0.4.0.jar>
```

Build the Maven project. While inside the Habitector directory, run:

```
mvn install 
```

Alternatively, import Habitector as a Maven project in Eclipse and click on Run as --> Maven install.

The above step should generate a JAR file with dependencies inside a folder named *target*.

## Usage in conjunction with NERsuite

If you wish to make use of dictionary features, prepare the required compiled dictionary files following the instructions provided by NERsuite.

### Data preparation for model training

Generate training data for NERsuite given a BioNLP ST-formatted corpus

```
java -jar Habitector.jar generateTrainingData --corpusPath=bionlpst_corpus_folder --outputPath=tokenised_output --geniaModelsPath=genia_models_directory
```
where:
    *bionlpst_corpus_folder* is the path to a directory containing the corpus in BioNLP ST format, i.e., \*.txt and \*.a1 files;
    *tokenised_output* is a path to which the corpus will be saved in tokenised format;
    *genia_models_directory* is the path to the directory containing pre-downloaded GENIA Tagger models

Tag the training data with dictionary matches using NERsuite

```
cd dictionary_tagger
./nersuite_dic_tagger -n cns compiled_dictionary < tokenised_output > dictionary_tagged_output
```
where:
	*compiled_dictionary* is the path to the compiled dictionary generated by NERsuite;
	*tokenised_output* is the path to the tokenised output from the previous step;
    *dictionary_tagged_corpus* is a path to which a dictionary-tagged version of tokenised_output will be saved

### Model training

Combine the dictionary-tagged tokenised corpus with the gold standard labels. One option for doing this is using the paste command:

```
paste labels_file dictionary_tagged_corpus > labelled_dictionary_tagged_corpus
```
where:
    *labels_file* is one of the output files previously generated by Habitector that contains only gold standard labels;
    *dictionary_tagged_corpus* is the output of the previous step

Note however that this will introduce unwanted tab characters for the blank lines in dictionary_tagged_text. To eliminate these, run the following command:

```
perl -pi -e 's/^\t//g;' labelled_dictionary_tagged_corpus
```
where *labelled_dictionary_tagged_corpus* is the output of combining the dictionary-tagged tokenised corpus with the gold standard labels using paste, that contains extraneous tab characters.

Train a new model using NERsuite

```
cd nersuite
./nersuite learn -f labelled_dictionary_tagged_corpus -m model_file
```
where:
    *labelled_dictionary_tagged_corpus* is the output of the previous step;
    *model_file* is a path to a file where the trained model will be saved
    
### Data preparation for applying models on new data

Generate test data for NERsuite

```
java -jar Habitector.jar generateTestData --corpusPath=txt_corpus_folder --outputPath=tokenised_output --writeSeparateFiles=toggle_for_creating_separate_files --geniaModelsPath=genia_models_directory
```
where:
    *txt_corpus_folder* is the path to a directory containing the corpus in plain text (\*.txt) format;
    *tokenised_output* is a path to which the corpus will be saved in tokenised format;
    *toggle_for_creating_separate_files* is a boolean (true or false) allowing the user to specify whether he/she prefers to generate separate files for testing;
    *genia_models_directory* is the path to the directory containing pre-downloaded GENIA Tagger models

Tag the test data with dictionary matches using NERsuite

```
cd dictionary_tagger
./nersuite_dic_tagger -n cns compiled_dictionary < tokenised_output > dictionary_tagged_output
```
where:
	*compiled_dictionary* is the path to the compiled dictionary generated by NERsuite;
	*tokenised_output* is the path to the tokenised output from the previous step;
    *dictionary_tagged_corpus* is a path to which a dictionary-tagged version of tokenised_output will be saved
    
If *toggle_for_creating_separate_files* was set to true in the previous step, this should be done as part of a loop (where *test_data_folder* contains the separate/multiple test files)

```
for i in test_data_folder/*.*; do ./nersuite_dic_tagger -n cns compiled_dictionary < $i > dictionary_tagged_test_data_folder/$i; done;
```

### Applying models on test data

Use pre-trained model to recognise named entities

```
./nersuite tag -m model_file < dictionary_tagged_test_data > ner_result_file
```
where:
	*model_file* is the pre-trained model;
    *dictionary_tagged_test_data* is the path to the dictionary-tagged tokenised test data;
    *ner_result_file* is a path to which predictions of the model on the test data will be written 

If *toggle_for_creating_separate_files* was set to true in the previous steps, the above command should be run as part of a loop (where *test_data_folder* contains the separate/multiple test files):

```
for i in dictionary_tagged_test_data_folder/*.*; do ./nersuite tag -m model_file < $i > ner_result_folder/$i; done;
```

### Saving NER results in BioNLP Shared Task format

Encode NERsuite's BIO output in BioNLP ST format

```
java -jar Habitector.jar interpretResults --resultsPath=ner_result_file --outputPath=bionlpst_corpus_folder
```
where:
    *ner_result_file* is the path to NERsuite's output (a folder if separate files were written);
    *bionlpst_corpus_folder* is a path to the folder in which the automatically generated annotations will be saved in BioNLP Shared Task format

## Built With

* [LingPipe](http://alias-i.com/lingpipe/index.html)
* [Stanford Tokeniser](https://nlp.stanford.edu/software/tokenizer.shtml)
* [jeniatagger](https://github.com/juanmirocks/jeniatagger)
