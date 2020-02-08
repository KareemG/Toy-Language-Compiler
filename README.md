# Toy Language Compiler

Contributors: @KareemG, @kyokeunpark, @Jas03x, and @Playjasb2

As a side project, we have decided to build a compiler for a simple toy language, that would compile the code into bytecode that the VM can execute.

### Getting Started

---

### Prerequisites:

You must have at least Java 8 installed in your system. And you also must have Apache Ant installed as well. You can install it via homebrew by running `brew install ant`, or if you're on Ubuntu, run:

```
sudo apt-get update
sudo apt-get install ant
```

### Installation:

To build our compiler, make sure you change your directory so you're in the compiler directory before you execute any of these commands. We must first run:

```
ant gettools
```

to install a local copy of JavaCUP and JFlex in lib. Then we run:

```
ant compiler488
```

to build our compiler. Afterwards, to create the jar file to run our compiler anywhere, we run:

```
ant dist
```

### Running the test cases

---

To run our test cases, execute:

```
./RUNTESTS.sh
```

This will go through of all test cases provided in our testing directory, and see if the compiler passes all of them.

### Usage

---

To use the compiler, execute the following command:

```
./RUNCOMPILER.sh -D x <location of the .488 source file>
```

This will run the compiler on the given `.488` source file, and produce an output file containing instructions that the VM can run, to produce the equivalent result of the given source code.
