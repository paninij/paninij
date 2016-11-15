---
title: On Ordering Problems in Message Passing Software
abstract: >
  The need for concurrency in modern software is increasingly fulfilled by
  utilizing the message passing paradigm because of its modularity and
  scalability. In the message passing paradigm, concurrently running processes
  communicate by sending and receiving messages. Asynchronous messaging
  introduces the possibility of message ordering problems: two messages with a
  specific order in the program text could take effect in the opposite order in
  the program execution and lead to bugs that are hard to find and debug. We
  believe that the engineering of message passing software could be easier if
  more is known about the characteristics of message ordering problems in
  practice. In this work, we present an analysis to study and quantify the
  relation between ordering problems and semantics variations of their
  underlying message passing paradigm in over 30 applications. Some of our
  findings are as follows: (1) semantic variations of the message passing
  paradigm can cause ordering problems exhibited by applications in different
  programming patterns to vary greatly; (2) some semantic features such as
  in-order messaging are critical for reducing ordering problems; (3) modular
  enforcement of aliasing in terms of data isolation allows small test
  configurations to trigger the majority of ordering problems.
bib: |
  @inproceedings{Long-Bagherzadeh-Lin-Upadhyaya-Rajan-16,
    author = {Yuheng Long and Mehdi Bagherzadeh and Eric Lin and Ganesha Upadhyaya and Hridesh Rajan},
    title = {On Ordering Problems in Message Passing Software},
    booktitle = {Modularity'16: 15th International Conference on Modularity},
    series = {Modularity'16},
    location = {Malaga, Spain},
    month = {March},
    year = {2016},
  }
---
