About
=====

This Java project is an implementation of linear-chain **Conditional Random Fields (CRF)** in pure Java,
with no third party dependency (except for log4j).

The CRF is exemplified by being used for the task of Part-Of-Speech tagging.

This is a free open-source project, which can be used also for commercial purposes. See LICENSE file.


Compile and run
===============

The project can be compiled with J2SE version 8.
The only thrid party required is for logging, Log4j. See [http://logging.apache.org/log4j/1.2/](http://logging.apache.org/log4j/1.2/)

The algorithms, including function optimization using LBFGS algorithm, forward-backward algorithm and Viterbi algorithm
are fully implemented in the code.


Entry points
============

For those who are interested only in the CRF but not the POS-tagger, an example entry point is
`org.crf.crf.run.ExampleMain`.

Note that this entry point is only a skeleton, and the user should copy it, and implement the feature generator
and other stuff required to run the CRF for the user's specific problem.


Those who are interested in the POS-tagging example can use the following two entry points:
`org.crf.demo.TrainAndEvaluate` and `org.crf.demo.UsePosTagger`.

The first entry point is for training the POS-tagger and evaluating it, and the second is for running it on
test examples.

Note that training requires the Penn Tree-Bank corpus, and it should be provided as a directory with
no subdirectories, which contains ".mrg" files, where each files contains parse-trees.


A note about the POS-tagging example
====================================

Please note that this is not a state-of-the-art POS tagger, since it does not employ state-of-the-art features.
Rather, this POS-tagger uses very simplistic features, and is intended to exemplify CRF.


**Enjoy!**
