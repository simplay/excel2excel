# Excel to Excel

Copy the content of an excel documents to another excel document.
In order to decide which cell in the FROM file is mapped to 
which other cell in the TO file, one has to define a cell mapping.

This project is licensed under the [MIT License](https://github.com/simplay/excel2excel/blob/master/LICENSE).

## Runtime Arguments

The program can take up to four arguments.
The 1st argument is the path to the FROM excel file and and is required.
The 2nd argument is the path to the TO excel file and is required.
The 3rd argument is the path to a custom mapping file and is optional.
The 4th argument is the path to a custom scala_values file and is optional.

## Define a Cell Mapping File

The mapping is defined in a text file called `mappings.txt` which is supposed to be located
at `./data/`. The file consists of a series of lines, where each line is series of n whitespace separated strings.
Currently, there are four different formats supported.

```
ax ay bx by
ax ay bx by rep
ax ay bx by rep mappingId
bx by rep default
```

### Legend

+ `ax`: The row cell index of a FROM excel file. Starts counting at zero. The excel index 1 or A respectively gets mapped to the index 0.
+ `ay`: The column cell index of a FROM excel file. The excel index 1 or A respectively gets mapped to the index 0.
+ `bx`: The row cell index of a TO excel file. The excel index 1 or A respectively gets mapped to the index 0.
+ `by`: The column cell index of a TO excel file. The excel index 1 or A respectively gets mapped to the index 0.
+ `rep`: Indicates whether should we append the values to the next free column in the TO file at the given TO row index.
+ `mappingId`: The looked up value in the FROM cell gets translated to a numeric value according to a certain scala. The scala is identified by this id. This value is between zero and the number of rows in `scala_values.txt` minus 1, i.e. the specified index value directly maps to the row in this file.
+ `default`: Instead of using a value from a FROM excel file, we use a default / constant and replicate it in the TO excel file. Such defaults represent a certain String. Strings in a mapping file are enclosed by quotes (.e. "some_fancy_string"). 


### Example

+ Cascade the string foobar in the first row in the TO excel file
+ take the cell in the 2nd row and 2nd column in the FROM file (which is supposed to be a symbolic scala value) and translate it to a numeric value according to the first scala (row 0 in `scala_values.txt`). The translated numeric value is written to cell at the 4th row and 2nd column in the TO excel file. 

```
0 1 1 "foobar"
3 1 1 1 1 0
```

## Contributing

1. Fork this repository
2. Create your feature branch `git checkout -b my-new-feature`
3. Commit your changes `git commit -am "Add some feature"`
4. Push to the branch `git push origin my-new-feature`
5. Create new Pull Request (in your forked repository)
