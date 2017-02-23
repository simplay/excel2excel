# Excel to Excel

Copy the content of an excel documents to another excel document.
In order to decide which cell in the FROM file is mapped to 
which other cell in the TO file, one has to define a cell mapping.

This project is licensed under the [MIT License](https://github.com/simplay/excel2excel/blob/master/LICENSE).

## Requirements

+ Java 7 (for compiling)
+ Maven (for building the project / packing the Jar)

## Build an executable Jar

Run `mvn test clean compile assembly:single` in your terminal. This will generate a executable jar file `excel2excel.jar` located at `./target/`.

You manually have to setup the expected project structure in order to later execute the jar successfully (see below).

## Expected File Hierarchy

In the same directory, where the `excel2excel.jar` file is located, there have to be two additional
directories, `./data/` and `./logs/`. The `data` directory contains the mapping and scale files,
the `logs` directory is used to store log files, which are generated whenever the jar is executed.

```
./
    ./data/
        ./data/mappings.txt
        ./data/scale_values.txt
    ./logs/
    ./excel2excel.jar
    
```

The definition of the `mappings.txt` file can be found below in the section _Define a Cell Mapping File_.
Moreover, the definition of the `scale_values.txt` file can be found below in the section _Scale Values Format_.

## Run the Jar

To execute the Jar, run `java -jar target/excel2excel.jar` within the project's root directory.

## Runtime Arguments

The program can take up to four arguments:

+ The 1st argument is the path to the FROM excel file and and is required.
+ The 2nd argument is the path to the TO excel file and is required.
+ The 3rd argument is the path to a custom cellMapping file and is optional.
+ The 4th argument is the path to a custom scale_values file and is optional.

## Define a Cell Mapping File

The cellMapping is defined in a text file called `mappings.txt` which is supposed to be located
at `./data/`. The file consists of a series of lines, where each line is series of n whitespace separated strings.
Currently, there are four different formats supported.

```
m FromSheetIndex ToSheetIndex
ax ay bx by
ax ay bx by rep
ax ay bx by rep mappingId
bx by rep default
```

### Legend

+ `FromSheetIndex`: The sheet number in the FROM excel file that should be used to lookup cells. The first sheet has the index 0.
+ `ToSheetIndex`: The sheet number in the TO excel file that should be used to lookup cells. The first sheet has the index 0.
+ `ax`: The row cell index of a FROM excel file. Starts counting at zero. The excel index 1 or A respectively gets mapped to the index 0.
+ `ay`: The column cell index of a FROM excel file. The excel index 1 or A respectively gets mapped to the index 0.
+ `bx`: The row cell index of a TO excel file. The excel index 1 or A respectively gets mapped to the index 0.
+ `by`: The column cell index of a TO excel file. The excel index 1 or A respectively gets mapped to the index 0.
+ `rep`: Indicates whether should we append the values to the next free column in the TO file at the given TO row index.
+ `mappingId`: The looked up value in the FROM cell gets translated to a numeric value according to a certain scale. The scale is identified by this id. This value is between zero and the number of rows in `scale_values.txt` minus 1, i.e. the specified index value directly maps to the row in this file.
+ `default`: Instead of using a value from a FROM excel file, we use a default / constant and replicate it in the TO excel file. Such defaults represent a certain String. Strings in a cellMapping file are enclosed by quotes (.e. "some_fancy_string"). 


### Example

+ Use the 1st sheet in the FROM excel file and the 3rd sheet in the TO excel file.
+ Cascade the string foobar in the first row in the TO excel file
+ take the cell in the 2nd row and 2nd column in the FROM file (which is supposed to be a symbolic scale value) and translate it to a numeric value according to the first scale (row 0 in `scale_values.txt`). The translated numeric value is written to cell at the 4th row and 2nd column in the TO excel file. 

```
m 0 2
0 1 1 "foobar"
3 1 1 1 1 0

```

## Scale Values Format

This program allows you to translate symbolic cell values to numerical values. 

For example, we use the commonly known labels used to represent sexes, `m` (male), `f` (female) and `o` (other) and want to map them
to numerical values. A particular mapping could be the following: `m` corresponds to `1`, `f` to `2` and `o` maps to `3`.

To do so, one has has to specify which cells should be translated in the `mappings.txt` file (see previous section)
and define in the `scale_values.txt` file, which symbolic values are translated to which numerical value.

The format of the scale_values has the following structure:

```
S11 S12 ... S1n N11 N12 ... N1n
S21 S22 ... S2n N21 N22 ... N2n
...
Sm1 Sm2 ... Smn Nm1 Nm2 ... Nmn

```

`Sij` defines a symbolic value and `Nij` its associated numerical value. Therefore, each line consists
of `2*k` number of elements (k is the number of symbols).
 
Items (both, symbolic and numerical values) in the file are white space separated.
Each line defines a specific translation, called scale. 

### Example

In the following we define three different scales, _sexes_, _foobar_, and _even/odd_ :

```
m f o 1 2 3
foo bar -1 1
even odd 10 20

```

## Contributing

1. Fork this repository
2. Create your feature branch `git checkout -b my-new-feature`
3. Commit your changes `git commit -am "Add some feature"`
4. Push to the branch `git push origin my-new-feature`
5. Create new Pull Request (in your forked repository)