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
        ./data/config.txt
    ./logs/
    ./excel2excel.jar
    
```

The definition of the `mappings.txt` file can be found below in the section _Define a Cell Mapping File_.
Moreover, the definition of the `scale_values.txt` file can be found below in the section _Scale Values Format_.
A config file `./data/config.txt` can optionally be specified. Further information can be found in section _Define the Config File_.

## Run the Jar

To execute the Jar, run `java -jar target/excel2excel.jar` within the project's root directory.

## Limitations

**Problem**: Writing `.xls` files is problematic, since at most 256 columns can be written. when trying to access a column index larger than
`255`, the following exception is thrown: 

```java
java.lang.IllegalArgumentException: Invalid column index (256).  Allowable column range for BIFF8 is (0..255) or ('A'..'IV')
```

**Workaround**: Use a `.xlsx` file instead. Any `.xls` file can easily be converted to this file format by using excel. 


## Runtime Arguments

The program can take up to four arguments:

+ The 1st argument is the path to the FROM excel file and and is required.
+ The 2nd argument is the path to the TO excel file and is required.
+ The 3rd argument is the path to a custom cellMapping file and is optional.
+ The 4th argument is the path to a custom scale_values file and is optional.

## Define the Config File

The config file is supposed to be named `config.txt` and should be located at `./data/`.
An example config file can be fund at `./data/config.example.txt`.

A config entry has the following format: `<CONFIG_NAME>: <VALUE>`.
The `<CONFIG_NAME>` is a string which is used to identify the config entry, the `<VALUE>` is also a string. 
Texts are supposed to be enclosed by quotes `"`. Further information about the format can be read below.

### Legend

The following legend describes a list of all expected `<CONFIG_NAME>` entries and their values.

+ `debug_mod`: Is either `0` (run debug mode) or `1` (run normal mode). Running the debug mode will basically print the content of the input excel file. Running the normal mode will start the GUI application as expected.
+ `use logger`: In case this option is set to `1`, then logger statements are streamed to the terminal. Otherwise it is muted.
+ `base_from_lookup_path`: Starting directory displayed when running the gui and searching for a FROM excel file. Paths are enclosed by quotes (to handle white spaces in paths)
+ `base_to_lookup_path`: Starting directory displayed when running the gui and searching for a TO excel file. Paths are enclosed by quotes (to handle white spaces in paths)

### Example

In the following an eample that:

+ Does not run the program in the debug mode
+ Does not print logging statements in the terminal
+ Uses the following base paths:
 + FROM base path: `foobar/`
 + TO base path: `barbaz/`

```

debug_mode: 0
use_logger: 0
use_base_paths: 1
base_from_lookup_path: "foobar/"
base_to_lookup_path: "barbaz/"

```

## Define a Cell Mapping File

The cellMapping is defined in a text file called `mappings.txt` which is supposed to be located
at `./data/`. The file consists of a series of lines, where each line is series of n whitespace separated strings.
Currently, there are four different formats supported.

```
m ToExcelIndex FromSheetIndex ToSheetIndex
c configSwitch
ax ay bx by
ax ay bx by rep
ax ay bx by rep mappingId
ax ay bx by rep "dateFormat"
bx by rep default
```

Please notice that the first line defines the FROM and TO sheet indices.
The leading string `m` is required and thus must not be omitted.
Every line starting by `m` starts a new mapping to the specified TO excel file. 

### Legend

+ `ToExcelIndex`: Which TO excel file that should be used. The first file has the index 0.
+ `FromSheetIndex`: The sheet number in the FROM excel file that should be used to lookup cells. The first sheet has the index 0.
+ `ToSheetIndex`: The sheet number in the TO excel file that should be used to lookup cells. The first sheet has the index 0.
+ `configSwitch`: Config switch to be turned on for current mapping block.
+ `ax`: The row cell index of a FROM excel file. Starts counting at zero. The excel index 1 or A respectively gets mapped to the index 0.
+ `ay`: The column cell index of a FROM excel file. The excel index 1 or A respectively gets mapped to the index 0.
+ `bx`: The row cell index of a TO excel file. The excel index 1 or A respectively gets mapped to the index 0.
+ `by`: The column cell index of a TO excel file. The excel index 1 or A respectively gets mapped to the index 0.
+ `rep`: Indicates whether should we append the values to the next free column in the TO file at the given TO row index.
+ `mappingId`: The looked up value in the FROM cell gets translated to a numeric value according to a certain scale. The scale is identified by this id. This value is between zero and the number of rows in `scale_values.txt` minus 1, i.e. the specified index value directly maps to the row in this file.
+ `dateFormat`: This is a Java date format string used to convert the source cell contents to the specified format.
+ `default`: Instead of using a value from a FROM excel file, we use a default / constant and replicate it in the TO excel file. Such defaults represent a certain String. Strings in a cellMapping file are enclosed by quotes (.e. "some_fancy_string"). 

### Config Switches

Each mapping block starting with `m` may contain any number of lines starting with `c` indicating a config switch that is turned on for that specific block.

#### List of Config Switches
+ `insertAsColumn`: Will insert all the mappings in that block that are set to search for the first free cell in their destination row (i.e. `rep=1`) into the first column that is free for all of them instead.
+ `requireNonEmpty`: Assures that all of the mappings in that block are only executed if all of their source cells aren't empty.


### Example

+ Use the 1st sheet in the FROM excel file and the 3rd sheet in the 1st TO excel file.
+ This block of mappings will only be executed if all of the source cells aren't empty.
+ Cascade the string foobar in the first row in the 1st TO excel file
+ take the cell in the 2nd row and 2nd column in the FROM file (which is supposed to be a symbolic scale value) and translate it to a numeric value according to the first scale (row 0 in `scale_values.txt`). The translated numeric value is written to cell at the 4th row and 2nd column in the 1st TO excel file.
+ a new mapping block is opened with the 2nd sheet on the 2nd TO excel
+ this block of mappings will fill all of the mappings that search for a free cell into the same column and makes sure that column is free for all of them
+ Copy the cell at (3,1) on sheet 2 in the FROM file to the first free cell on the 2nd row on sheet 3 in the 2nd TO excel. 
+ Take the numeric value of the cell at (3,2) on sheet 2 in the FROM file convert it to a date in the format dd.MM.yyyy and write the result of that conversion into the first free cell on row 3 on sheet 3 in the 2nd TO file.

```
m 0 0 2
c requireNonEmptySource
0 1 1 "foobar"
1 1 3 1 1 0
m 1 1 2
c insertAsColumn
3 1 1 0 1
3 2 2 0 1 "dd.MM.yyyy"

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

In case a scale can be A, B or A and B values, the combination A and B is written as `A/B` in the scale file.
Please notice that this `A/B` must not exhibit any whitespace separation in the scale file. So, this means that
`A/B` is valid whereas `A / B`, `A/ B`, ` A  /B  ` and so forth are wrong. That does not affect the excel file.

### Example

In the following we define three different scales, _sexes_, _foobar_, and _even/odd_ :

```
m f o 1 2 3
foo bar -1 1
even odd 10 20
A B A/B 1 2 3

```

## Contributing

1. Fork this repository
2. Create your feature branch `git checkout -b my-new-feature`
3. Commit your changes `git commit -am "Add some feature"`
4. Push to the branch `git push origin my-new-feature`
5. Create new Pull Request (in your forked repository)
